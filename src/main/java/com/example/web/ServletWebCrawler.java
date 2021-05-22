package com.example.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

public class ServletWebCrawler extends HttpServlet implements Runnable {
    static long imageCount = 0;  // image found count , start from 0
    Thread searcher;                      // background search thread
    public static String url;
    boolean isThreadRan;
    private HashSet<String> links;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);                  // always!
    }

    public ServletWebCrawler() {
        this.links = new HashSet<String>();
    }

    public void run() {
        this.getPageLinks(this.url);
        isThreadRan = true;
    }

    public void getPageLinks(String url) {
        //4. Check if you have already crawled the URLs
        //(we are intentionally not checking for duplicate content in this example)
        if (!links.contains(url)) {
            try {
                //4. (i) If not add it to the index
                if (links.add(url)) {
                    System.out.println(url);
                }
                //2. Fetch the HTML code
                Document document = Jsoup.connect(url).get();
                // get image count
                Elements img = document.select("img");
                if (null != img && img.size() != 0) {
                    imageCount += img.size();
                }
                //3. Parse the HTML to extract links to other URLs
                Elements linksOnPage = document.select("a[href]");
                //5. For each extracted URL... go back to Step 4.
                for (Element page : linksOnPage) {
                    getPageLinks(page.attr("abs:href"));
                }
            } catch (IOException e) {
                System.err.println("For '" + url + "': " + e.getMessage());
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/result.jsp");
        request.setAttribute("count", imageCount);
        request.setAttribute("url", url);
        rd.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.url = request.getParameter("url");
        if (null != this.url) {
            if (isExistUrl(this.url)) {
                // start the Thread
                searcher = new Thread(this);
                searcher.setPriority(Thread.MIN_PRIORITY);  // be a good citizen
                searcher.start();
                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/result.jsp");
                request.setAttribute("count", imageCount);
                request.setAttribute("url", url);
                rd.forward(request, response);
            } else {
                RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/notfound.jsp");
                request.setAttribute("msg", "404, (" + this.url + ") not found");
                rd.forward(request, response);
            }
        } else {
            RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/notfound.jsp");
            request.setAttribute("msg", "Please enter the url");
            rd.forward(request, response);
        }
    }

    public boolean isExistUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            int responseCode = huc.getResponseCode();
            if (200 == responseCode) {
                return true;
            } else {
                return false;
            }
        } catch (MalformedURLException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}
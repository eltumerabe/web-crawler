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

/**
 * This Servlet application used to crawl for links
 * from the Internet and find the count of the images
 * in each crawled url.
 *
 * @author Hala Abumadi
 * @since 1.0
 */
public class ServletWebCrawler extends HttpServlet implements Runnable {
    /**
     * this variable stores image count that increase dynamically
     */
    static long imageCount = 0;  // image found count , start from 0
    /**
     * thread runs in the background
     */
    Thread searcher;                      // background search thread
    /**
     * url to be crawled
     */
    public static String url;
    /**
     * Set to store the crawled links in order to avoid the crawl next time
     */
    private HashSet<String> links;

    /**
     * Servlet init method
     *
     * @param config this onject created by Servlet container
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);                  // always!
    }

    /**
     * zero parameter constructor to initialize the HashSet
     */
    public ServletWebCrawler() {
        this.links = new HashSet<String>();
    }

    /**
     * run method to run the Thread that is going to fetch the image counts from each crawled url
     */
    public void run() {
        this.getPageLinks(this.url);
    }

    /**
     * This method contains the logic to run the web crawling program
     * parse the html and fetch the url and then find the images available
     * in that specific url and update the count parameter
     * @param url an URL to be crawled
     */
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
    /**
     * Process the all get request coming to the Servlet and forward the response to
     * the giving jsp to display the result
     *
     * @param  request  an absolute URL giving the base location of the image
     * @param  response the location of the image, relative to the url argument
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/jsp/result.jsp");
        request.setAttribute("count", imageCount);
        request.setAttribute("url", url);
        rd.forward(request, response);
    }
    /**
     * Process the all post request coming to the Servlet and forward the response to
     * the giving jsp to display the result
     *
     * @param  request  an absolute URL giving the base location of the image
     * @param  response the location of the image, relative to the url argument
     */
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
    /**
     * Process the all get request coming to the Servlet and forward the response to
     * the giving jsp to display the result
     *
     * @param  urlString  an  URL to be checked for existence
     * @return      true is the url is valid, or false if the url is not valid
     * @see         boolean
     */
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
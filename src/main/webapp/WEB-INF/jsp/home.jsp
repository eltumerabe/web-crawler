<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.6/umd/popper.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container" style="margin-top: 30px;">
    <div class="row">
        <div class="col">
            <h3>Image Counter Crawler</h3>
        </div>
    </div>
    <div class="row">
        <dev class="col-6">
            <form action="crawler" method="post">
                <div class="mb-3">
                    <label for="url" class="form-label">Enter Valid URL</label>
                    <input type="text" class="form-control" id="url" name="url">
                </div>
                <c:if test="${not empty msg}">
                <dev>
                    <h6 style="color: #bb2d3b">${msg}</h6>
                </dev>
                </c:if>
                <button type="submit" class="btn btn-primary">Crawl</button>
            </form>
        </dev>
        <dev class="col-6">
        </dev>
    </div>
</div>
</body>
</html>
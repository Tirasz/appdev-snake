<%@ page import="rb.snake.controller.LeaderboardController" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8" %>
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
      xmlns:f="http://xmlns.jcp.org/jsf/core">

<head>
    <link rel="icon" href="../css/icon.png" type="image/icon type">
    <title>Snake leaderboard</title>
    <meta charset="UTF-8">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js" integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" crossorigin="anonymous"></script>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-BmbxuPwQa2lc/FVzBcNJ7UAyJxM6wuqIj61tLrc4wSX0szH/Ev+nYRRuWlolflfl" crossorigin="anonymous">
    <!--script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script-->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="../css/style.css">
    <script src="../js/main.js"></script>
</head>

<body>
<jsp:include page="/Record_Controller"/>
<div class="container">
    <div class="row justify-content-center">
        <form action="../Record_Controller" method="post" class="col-lg-4 col-md-6 col-sm-12 justify-content-center" id="editForm">
            <label for="p1">Player 1</label>
            <input required name="p1" id="p1" type="text" placeholder="Enter name" value="${requestScope.record.p1}"/>
            <label for="p1Score"> Score </label>
            <input required name="p1Score" id="p1Score" type="number" placeholder="Enter score" value="${requestScope.record.p1Score}"/>

            <c:if test="${requestScope.players == 2}">
                <label for="p2">Player 2</label>
                <input required name="p2" id="p2" type="text" placeholder="Enter name" value="${requestScope.record.p2}"/>
                <label for="p2Score"> Score </label>
                <input required name="p2Score" id="p2Score" type="number" placeholder="Enter score" value="${requestScope.record.p2Score}"/>
                <input type="hidden" id="ogP2" name="ogP2" value="${requestScope.record.p2}"/>
            </c:if>
            <input type="hidden" id="ogP1" name="ogP1" value="${requestScope.record.p1}"/>
            <button type="submit" form="editForm" value="Submit">Submit</button>
        </form>
    </div>

</div>

</body>

</html>

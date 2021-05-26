<%@ page import="rb.snake.controller.LeaderboardController" %>
<%@ page import="jakarta.servlet.http.Cookie" %>
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
<jsp:include page="/Leaderboard_Controller"/>

<div class="container">
    <c:if test="${requestScope.error > 0}"> <!--ERRORS-->
    <div class="row justify-content-center" style="background-color: black; color:red; border-radius:15px 15px 15px 15px; margin-top: 15px;">
        <c:choose>
            <c:when test="${requestScope.error == 1}">
                Character name cannot contain special characters or spaces!
            </c:when>
            <c:when test="${requestScope.error == 2}">
                Character name must be between 3 and 10 characters long!
            </c:when>
            <c:when test="${requestScope.error == 3}">
                Scores can only be numbers!
            </c:when>
            <c:otherwise>
                OOPSIE WOOPSIE!! Uwu We made a fucky wucky!! A wittle fucko boingo! The code monkeys at our headquarters are working VEWY HAWD to fix this!!
            </c:otherwise>
        </c:choose>
    </div>
    </c:if>
    <div class="row justify-content-center" id="header-container"><!--BUTTONS-->
        <button class="col-lg-2 col-md-4 col-sm-12" onclick="showSP()">Singleplayer</button> <button class="col-lg-2 col-md-4 col-sm-12" onclick="showMP()">Multiplayer</button>
    </div>
    <div class="row justify-content-center" id="sp-container"><!--SP-->
        <table class="col-lg-8 col-md-10 col-sm-12 table">
            <thead>
            <tr> <th >Player 1</th> <th>Score</th>  <th style="width:200px;">Action</th></tr>
            </thead>
            <tbody>
            <c:forEach var="record" items="${requestScope.SPList}">
                <tr><td>${record.p1}</td> <td>${record.p1Score}</td>  <td> <a class="actionButton deleteButton fa fa-trash" href="edit_record.jsp?type=SP&action=DEL&p1=${record.p1}"> </a> <a class="actionButton editButton fa fa-edit" href="edit_record.jsp?type=SP&action=EDIT&p1=${record.p1}"> </a> </td></tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <div class="row justify-content-center" id="mp-container"><!--MP-->
        <table class="col-lg-8 col-md-10 col-sm-12 table">
            <thead>
            <tr> <th >Player 1</th> <th>Score</th> <th >Player 1</th> <th>Score</th> <th style="width:200px;">Action</th></tr>
            </thead>
            <tbody>
            <c:forEach var="record" items="${requestScope.MPList}">
                <tr><td>${record.p1}</td> <td>${record.p1Score}</td>  <td>${record.p2}</td> <td>${record.p2Score}</td><td><a class="actionButton deleteButton fa fa-trash" href="edit_record.jsp?type=MP&action=DEL&p1=${record.p1}&p2=${record.p2}"> </a> <a class="actionButton editButton fa fa-edit" href="edit_record.jsp?type=MP&action=EDIT&p1=${record.p1}&p2=${record.p2}"> </a> </td></tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

</div>

</body>
<script>
    init();
</script>
</html>

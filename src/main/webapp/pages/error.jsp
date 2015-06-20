<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" 
    contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" 
    isErrorPage="true"
%>
<%
    // Check if there is any message to show in page
    String message = (String) request.getAttribute("message");
%>
<jsp:include page="member/templates/header.jsp" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
       <title>Error</title>
    </head>
    <body>
        <p> Something went wrong :( </p>
        <br/>
        <% 
            if(message != null) {
                out.println("<div>"+message+"</div><br/>");
            }
        %>
        <p> Please retry or contact system administrator.</p>
    </body>
</html>
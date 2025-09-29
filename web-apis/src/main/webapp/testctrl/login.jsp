<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="ISO-8859-1">
        <!-- Text on the browser tab: "IR Host" -->  
        <title>Tests Manager</title>
        <link rel="stylesheet" href="css/login.css?ver=2.0">
        <script defer src="js/login.js?ver=2.0"></script>
    </head>
    <body>
        <!-- Title of the page: "Tests Management Login -->    
        <table class="title-table">
            <td><span>Tests Manager </span><span class="title-highlight">Login</span></td>
        </table>
        <p>
        <!-- Controls for [Host name][Password] and [Login button] -->
        <table class="login-table">
            <tr><td>Name:</td><td><input id="edtName" type="text"></td></tr>
            <tr><td>Password:</td><td><input id="edtPwd" type="password"></td></tr>
            <tr><td></td><td><input id="btnLogin" type="submit" value="Login" ></td></tr>
        </table>
        <p>
        <!--  Output area -->
        <div id="txtOutput" class="err-div"></div>
    </body>
</html>
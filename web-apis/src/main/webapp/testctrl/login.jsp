<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="ISO-8859-1">
        <!-- Text on the browser tab: "IR Host" -->  
        <title>Tests Manager</title>
        <link rel="stylesheet" href="css/login.css?ver=2.1">
        <script defer src="js/login.js?ver=2.2"></script>
    </head>
    <body>
        <!-- Title of the page: "Tests Management Login -->    
        <table class="title-table">
            <td><span>Tests Manager </span><span class="title-highlight">Login</span></td>
        </table>
        <p>
        <!-- Controls for [Github username] and [Login button] -->
        <table class="login-table">
            <tr>
                <td class="login-user-label">
                    <img src="../res/github_logo.png" alt="GitHub" class="login-user-logo">
                    <span>Username:</span>
                </td>
                <td><input id="edtName" type="text"></td>
            </tr>
            <tr><td></td><td><input id="btnLogin" type="submit" value="GitHub Login" ></td></tr>
        </table>
        <p>
        <!--  Output area -->
        <div id="txtOutput" class="err-div"></div>
    </body>
</html>
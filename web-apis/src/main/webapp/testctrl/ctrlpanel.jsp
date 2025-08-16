<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="ISO-8859-1">
        <!-- Text on the browser tab: "IR Host" -->
        <title>Tests Manager</title>
        <link rel="stylesheet" href="css/ctrlpanel.css?ver=1.0">
        <script defer src="js/ctrlpanel.js?ver=1.0"></script>
    </head>
    <body>
        <!-- Title of the page: "Instant Reaction Host username" .... [logout] -->
        <table class="title-table">
            <tr>
                <td><span>Tests Manager </span><span class="title-highlight">Control Panel</span></td>
                <td><span class="title-sid" id="titleSid"></span> <span class="title-name" id="titleName"></span></td>
                <td><input id="btnLogout" type="submit" value="logout" ></td>
            </tr>
        </table>
        <div id="divLog" class="log-div">
            <table id="tblLog">
                <!-- Log entries will be added here -->
            </table>
        </div>
    </body>
</html>
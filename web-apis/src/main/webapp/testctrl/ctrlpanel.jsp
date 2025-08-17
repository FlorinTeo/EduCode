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
        <div class="main-style">
            <table class="title-style">
                <tr>
                    <td><span>Tests Manager </span><span class="title-highlight">Control Panel</span></td>
                    <td><span class="title-sid" id="titleSid"></span> <span class="title-name" id="titleName"></span></td>
                    <td><input id="btnLogout" type="submit" value="logout" ></td>
                </tr>
            </table>
            <div id="divActions" class="actions-style">
                Action buttons and descriptions go here
            </div>
            <div id="divLog" class="log-style">
                Backend logs:
                <div id="divLogContent" class="log-content-style">
                    <table id="tblLog">
                        <!-- Log entries will be added here -->
                    </table>
                </div>
            </div>
        </div>
    </body>
</html>
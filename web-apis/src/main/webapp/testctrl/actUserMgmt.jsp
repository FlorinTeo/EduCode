<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="ISO-8859-1">
        <link rel="stylesheet" href="css/actUserMgmt.css?ver=1.0">
        <script defer src="js/actUserMgmt.js?ver=1.0"></script>
    </head>
    <div id="actUserMgmt_div" class="actUserMgmt-style">
        <!-- Controls for setting [Name][Password] -->
        <table class="actUserMgmt-table">
            <tr><td>Name:</td><td><input id="actUserMgmt_edtName" type="text"></td></tr>
            <tr><td>Password:</td><td><input id="actUserMgmt_edtPwd" type="password"></td></tr>
        </table>
        <p>
        <!--  Output area -->
        <div id="actUserMgmt_txtOutput" class="actUserMgmt-err-div"></div>
    </div>
</html>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="ISO-8859-1">
        <link rel="stylesheet" href="css/actSetPwd.css?ver=1.0">
        <script defer src="js/actSetPwd.js?ver=1.0"></script>
    </head>
    <div class="setPwd-style">
        <!-- Controls for setting [Name][Password] -->
        <table class="setPwd-table">
            <tr><td>Name:</td><td><input id="setPwd_edtName" type="text"></td></tr>
            <tr><td>Crt Password:</td><td><input id="setPwd_edtCrtPwd" type="password"></td></tr>
            <tr><td>New Password:</td><td><input id="setPwd_edtNewPwd" type="password"></td></tr>
        </table>
        <p>
        <!--  Output area -->
        <div id="setPwd_txtOutput" class="err-div"></div>
    </div>
</html>
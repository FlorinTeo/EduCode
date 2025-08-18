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
            <!----==== Title bar ====---->
            <table class="title-style">
                <tr>
                    <td><span>Tests Manager </span><span class="title-highlight">Control Panel</span></td>
                    <td><span class="title-sid" id="titleSid"></span> <span class="title-name" id="titleName"></span></td>
                    <td><input id="btnLogout" type="submit" value="logout" ></td>
                </tr>
            </table>

            <!----==== Actions list ====---->
            <div id="divActions" class="actions-style">
                <table id="tblActions">
                    <!-- Sample/Template pattern to be used when adding custom actions -->
                    <tr><td><input id="btnSample" value="Sample Action" type="submit"></td>
                        <td>Demonstrates a sample action entry.<br>
                            This is a template to be used as more actions are added to the control panel.
                        </td>
                    </tr>
                    <!-- Action entries to be added bellow -->
                    <tr><td><input id="btnShowSessions" value="Show Sessions" type="submit"></td>
                        <td>Shows the list of the active sessions currently logged in on the server.</td>
                    </tr>
                    <tr><td><input id="btnSetPwd" value="Set Password" type="submit"></td>
                        <td>Sets a new password for any registered user.</td>
                    </tr>
                </table>
            </div>
            <!----==== Logs list ====---->
            <div id="divLog" class="log-style">
                Server logs:
                <div id="divLogContent" class="log-content-style">
                    <table id="tblLog">
                        <!-- Log entries will be added here -->
                    </table>
                </div>
            </div>
        </div>

        <!----==== Action dialog ====---->
        <dialog id="dlgAction" class="action-dialog-style">
            <div id="dlgTitleBar" class="action-dialog-textBar-style">
                <span id="dlgTitle">Action Dialog</span>
                <img id="dlgApply" src="../res/window-apply.png" alt="Apply" style="width:24px;height:24px;">
                <img id="dlgClose" src="../res/window-close.png" alt="Close" style="width:24px;height:24px;">
            </div>
            <div id="dlgSource" class="action-dialog-src-style">
                <!-- <textarea id="dlgSource">This is where the action source html goes.</textarea> -->
            </div>
        </dialog>

    </body>
</html>
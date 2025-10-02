<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="ISO-8859-1">
        <!-- Text on the browser tab: "IR Host" -->
        <title>Tests Manager</title>
        <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
        <link rel="stylesheet" href="css/ctrlCheckedList.css?ver=2.0">
        <link rel="stylesheet" href="css/adminPanel.css?ver=2.0">
        <script defer type="module" src="js/adminPanel.js?ver=2.0"></script>
    </head>
    <body>
        <div class="main-style">
            <!----==== Title bar ====---->
            <table class="title-style">
                <tr>
                    <td><span>Tests Manager </span><span class="title-highlight">Admin Panel</span></td>
                    <td><span class="title-sid" id="titleSid"></span> <span class="title-name" id="titleName"></span></td>
                    <td><input id="btnLogout" type="submit" value="logout" ></td>
                </tr>
            </table>

            <!----==== Actions list ====---->
            <div id="divActions" class="actions-style">
                <table id="tblActions">
                    <!-- Sample/Template pattern to be used when adding custom actions -->
                    <tr><td><input id="btnTestEdt" value="Test Editor" type="submit"></td>
                        <td>Test editing operations such as:
                            <ul style="margin-top: 0; padding-left: 16px;">
                            <li>Create tests by selecting the questions it should contain,</li>
                            <li>Edit or delete existing tests.</li> 
                            </ul>
                        </td>
                    </tr>
                    <!-- Action entries to be added bellow -->
                    <tr><td><input id="btnSessionMgmt" value="Test Publisher" type="submit"></td>
                        <td>Test publishing operations such as:
                            <ul style="margin-top: 0; padding-left: 16px;">
                            <li>Display extended test information, such as question counts by type, links to print-ready tests, etc,</li>
                            <li>Assign test audience by selecting users to receive this test, once it is published,</li> 
                            <li>Publishing or unpublishing specific tests to their selected audience.</li>
                            </ul>
                        </td>
                    </tr>
                    <tr><td><input id="btnUserMgmt" value="User Management" type="submit"></td>
                        <td>User management operations, such as:
                            <ul style="margin-top: 0; padding-left: 16px;">
                            <li>Update user password.</li>
                            </ul>
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
            <table id="dlgActionTitleBar" class="action-dialog-titleBar-style">
                <tr>
                    <td><span id="dlgActionTitle">Action Dialog</span></td>
                    <td><img id="dlgActionApply" src="../res/window-apply.png" alt="Apply" style="width:24px; height:24px;"></td>
                    <td><img id="dlgActionClose" src="../res/window-close.png" alt="Close" style="width:24px; height:24px;"></td>
                </tr>
            </table>
            <!-- <select id="adminPanel_cb" style="width: 185px;"></select> -->
            <div id="dlgActionPane" class="action-dialog-pane-style">
                <!-- <textarea id="dlgSource">This is where the action source html goes.</textarea> -->
            </div>
        </dialog>

    </body>
</html>
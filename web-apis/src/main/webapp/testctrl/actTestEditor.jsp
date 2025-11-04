<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="ISO-8859-1">
        <link rel="stylesheet" href="css/actTestEditor.css?ver=2.13">
        <script defer type="module" src="js/actTestEditor.js?ver=2.1"></script>
    </head>
    <div id="actTestEditor_div" class="actTestEditor-style">
        <!-- Top (level 0) table: 2 rows, 1 column -->
        <table class="actTestEdt-lvl0-table">
            <tr><td>
                <table class="actTestEdt-flex-row">
                    <tr>
                        <!-- Edit field for test name -->
                        <td>
                            Test name:
                            <select id="actTestEdt_cbTestName" style="width: 240px;"></select>
                        </td>
                        <td>
                            <span id="actTestEdt_txtWarnings"><!--This is where the warnings are going to show!--></span>
                        </td>
                        <td>Solution</td>
                        <td>
                            <!-- Toggle field for displaying the answer -->
                            <label class="actTestEdt_lblSolution">
                                <input type="checkbox" id="actTestEdt_tglSolution">
                                <span class="actTestEdt_spnSolution"></span>
                            </label>
                        </td>
                    </tr>
                </table>
            </td></tr>
            <tr><td>
                <!-- Inner (level 1) table: 1 row, 2 columns -->
                <table class="actTestEdt-lvl1-table">
                    <tr><td>
                        <!-- Inner (level 2) table: 2 rows, 1 column -->
                        <table class="actTestEdt-lvl2-table">
                            <tr><td>
                                <!-- Edit field for question lists filter -->
                                <input id="actTestEdt_ckb_allMCQ" type="checkbox">
                                <input id="actTestEdt_edtFilter" autocomplete="off">
                            </td></tr>
                            <tr><td>
                                <ul id="actTestsEdt_lstMCQ">
                                    <!-- MCQ entries filled in dynamically -->
                                </ul>
                            </td></tr>
                        </table>
                    </td><td>
                        <!-- Placeholder div for a question content-->
                        <div id="actTestEdt_divQContent">
                            
                        </div>
                    </td></tr>
                </table>
            </td></tr>
        </table>
    </div>
    </html>

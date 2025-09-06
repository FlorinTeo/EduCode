<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="ISO-8859-1">
        <link rel="stylesheet" href="css/actTestMgmt.css?ver=2.0">
        <script defer type="module" src="js/actTestMgmt.js?ver=1.5"></script>
    </head>
    <div id="actTestMgmt_div" class="actTestMgmt-style">
        <!-- Top (level 0) table: 2 rows, 1 column -->
        <table class="actTestMgmt-lvl0-table">
            <tr><td>
                Test name:
                <!-- Edit field for test name -->
                <input id="actTestMgmt_edtTestName" list="actTestMgmt_dlstTests" style="width: 185px;">
            </td></tr>
            <tr><td>
                <!-- Inner (level 1) table: 1 row, 2 columns -->
                <table class="actTestMgmt-lvl1-table">
                    <tr><td>
                        <!-- Inner (level 2) table: 4 rows, 1 column -->
                        <table class="actTestMgmt-lvl2-table">
                            <tr><td>
                                <!-- Edit field for question lists filter -->
                                <input id="actTestMgmt_edtFilter" list="actTestMgmt_dlstFilters" style="width: 100%;">
                            </td></tr>
                            <tr><td>
                                <input id="actTestMgmt_ckb_allMCQ" type="checkbox"><b>Multiple Choice (MCQ):</b>
                                <ul id="actTestsMgmt_lstMCQ">
                                    <!-- MCQ entries filled in dynamically -->
                                </ul>
                            </td></tr>
                            <tr><td>
                                <input id="actTestMgmt_ckb_allFRQ" type="checkbox"><b>Free Response (FRQ):</b>
                                <ul id="actTestsMgmt_lstFRQ">
                                    <!-- FRQ entries filled in dynamically -->
                                </ul>
                            </td></tr>
                            <tr><td>
                                <input id="actTestMgmt_ckb_allAPX" type="checkbox"><b>Appendix (APX):</b>
                                <ul id="actTestsMgmt_lstAPX">
                                    <!-- APX entries filled in dynamically -->
                                </select>
                            </td></tr>
                        </table>
                    </td><td>
                        <!-- Placeholder div for a question content-->
                        <div id="actTestMgmt_divQContent">
                            
                        </div>
                    </td></tr>
                </table>
            </td></tr>
        </table>
    </div>
    <datalist id="actTestMgmt_dlstTests">
    </datalist>
    <datalist id="actTestMgmt_dlstFilters">
        <option value="ap">
        <option value="ds">
    </datalist>
    </html>

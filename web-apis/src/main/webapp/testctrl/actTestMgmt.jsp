<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="ISO-8859-1">
        <link rel="stylesheet" href="css/actTestMgmt.css?ver=1.1">
        <script defer src="js/actTestMgmt.js"></script>
    </head>
    <div id="actTestMgmt_div" class="actTestMgmt-style">
        <!-- Top (level 0) table: 2 rows, 1 column -->
        <table class="actTestMgmt-lvl0-table">
            <tr><td>
                Test name:
                <!-- Edit field for test name -->
                <input id="actTestMgmt_edtTests" list="actTestMgmt_dlTests" name="testName" style="width: 185px;">
                <datalist id="actTestMgmt_dlTests">
                    <option value="Test 1">
                    <option value="Test 2">
                    <option value="Test 3">
                </datalist>
            </td></tr>
            <tr><td>
                <!-- Inner (level 1) table: 1 row, 2 columns -->
                <table class="actTestMgmt-lvl1-table">
                    <tr><td>
                        <!-- Inner (level 2) table: 4 rows, 1 column -->
                        <table class="actTestMgmt-lvl2-table">
                            <tr><td>
                                <!-- Edit field for question lists filter -->
                                <input list="actTestMgmt_edtFilter" name="actTestMgmt_dlFilters" style="width: 250px;">
                                <datalist id="actTestMgmt_dlFilters">
                                    <option value="ap">
                                    <option value="ds">
                                </datalist>
                            </td></tr>
                            <tr><td>
                                <b>Multiple Choice Questions (MCQ):</b>
                                <ul id="actTestsMgmt_lstMCQ">
                                    <li><input type="checkbox"><label>ap2.Q3</label></li>
                                    <li><input type="checkbox"><label>ap7-8.Q2</label></li>
                                    <li><input type="checkbox"><label>ds3.Q3-4</label></li>
                                    <li><input type="checkbox"><label>ap2.Q3</label></li>
                                    <li><input type="checkbox"><label>ap7-8.Q2</label></li>
                                    <li><input type="checkbox"><label>ds3.Q3-4</label></li>
                                    <li><input type="checkbox"><label>ap2.Q3</label></li>
                                    <li><input type="checkbox"><label>ap7-8.Q2</label></li>
                                    <li><input type="checkbox"><label>ds3.Q3-4</label></li>
                                    <li><input type="checkbox"><label>ap2.Q3</label></li>
                                    <li><input type="checkbox"><label>ap7-8.Q2</label></li>
                                    <li><input type="checkbox"><label>ds3.Q3-4</label></li>
                                    <li><input type="checkbox"><label>ap2.Q3</label></li>
                                    <li><input type="checkbox"><label>ap7-8.Q2</label></li>
                                    <li><input type="checkbox"><label>ds3.Q3-4</label></li>
                                    <li><input type="checkbox"><label>ap2.Q3</label></li>
                                    <li><input type="checkbox"><label>ap7-8.Q2</label></li>
                                    <li><input type="checkbox"><label>ds3.Q3-4</label></li>
                                    <li><input type="checkbox"><label>ap2.Q3</label></li>
                                    <li><input type="checkbox"><label>ap7-8.Q2</label></li>
                                    <li><input type="checkbox"><label>ds3.Q3-4</label></li>
                                    <li><input type="checkbox"><label>ap2.Q3</label></li>
                                    <li><input type="checkbox"><label>ap7-8.Q2</label></li>
                                    <li><input type="checkbox"><label>ds3.Q3-4</label></li>
                                </ul>
                            </td></tr>
                            <tr><td>
                                <b>Free Response Questions (FRQ):</b>
                                <ul id="actTestsMgmt_lstFRQ">
                                    <li><input type="checkbox"><label>ap5.P1</label></li>
                                    <li><input type="checkbox"><label>ap7-8.P1</label></li>
                                </ul>
                            </td></tr>
                            <tr><td>
                                <b>Appendix Pages (APX):</b>
                                <ul id="actTestsMgmt_lstAPX">
                                    <li><input type="checkbox"><label>ds1.A1</label></li>
                                    <li><input type="checkbox"><label>ds5.A0</label></li>
                                    <li><input type="checkbox"><label>ds5.A0</label></li>
                                </select>
                            </td></tr>
                        </table>
                    </td><td>
                        <div id="actTestMgmt_divQContent">
                            
                        </div>
                    </td></tr>
                </table>
            </td></tr>
        </table>
    </div>
    </html>

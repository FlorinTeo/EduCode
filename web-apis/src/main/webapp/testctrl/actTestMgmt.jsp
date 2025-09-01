<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
    <!DOCTYPE html>
    <html>

    <head>
        <meta charset="ISO-8859-1">
        <link rel="stylesheet" href="css/actTestMgmt.css?ver=1.1">
        <script defer src="js/actTestMgmt.js"></script>
    </head>
    <div id="actTestMgmt_div" class="actTestMgmt-style">
        <table class="actTestMgmt-lvl0-table">
            <tr>
                <td>
                    Test name:
                    <input list="testNames" name="testName">
                    <datalist id="testNames">
                        <option value="Test 1">
                        <option value="Test 2">
                        <option value="Test 3">
                    </datalist>
                </td>
            </tr>
            <tr>
                <td>
                    <table class="actTestMgmt-lvl1-table">
                        <tr>
                            <td>
                                <table class="actTestMgmt-lvl2-table">
                                    <tr><td>
                                        filter:
                                    </td></tr>
                                    <tr><td>
                                        MCQ list:
                                    </td></tr>
                                    <tr><td>
                                        FRQ list:
                                    </td></tr>
                                    <tr><td>
                                        APX list:
                                    </td></tr>
                                </table>
                            </td>
                            <td>
                                question content.
                            </td>
                        </tr>
                        <!-- <td>
                        <tr>
                            <td>Filter:
                                <input id="qFilter_input" name="qFilter">
                                <datalist id="qFilter_dlist">
                                    <option value="ap">
                                    <option value="ds">
                                </datalist>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Multiple Choice Questions (MCQ):
                                <select id="mcq_select" size="10" style="width: 100%;">
                                    <option value="MCQ 1">MCQ 1</option>
                                    <option value="MCQ 2">MCQ 2</option>
                                    <option value="MCQ 3">MCQ 3</option>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Free Response Questions (FRQ):
                                <select id="frq_select" size="10" style="width: 100%;">
                                    <option value="FRQ 1">FRQ 1</option>
                                    <option value="FRQ 2">FRQ 2</option>
                                    <option value="FRQ 3">FRQ 3</option>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Appendix Pages (APX):
                                <select id="apx_select" size="10" style="width: 100%;">
                                    <option value="APX 1">APX 1</option>
                                    <option value="APX 2">APX 2</option>
                                    <option value="APX 3">APX 3</option>
                                </select>
                            </td>
                        </tr> -->
                    </table>
                </td>
            </tr>
        </table>
    </div>
    </html>
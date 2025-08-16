/**
 * Get page parameters from the URL.
 */
const sid = (new URLSearchParams(window.location.search)).get("sid");
const username = (new URLSearchParams(window.location.search)).get("name");

/**
 * CtrlPanel page controls
 */
const txtTitleSid = document.getElementById("titleSid");
const txtTitleName = document.getElementById("titleName");
const btnLogout = document.getElementById("btnLogout");
const divLog = document.getElementById("divLog");
const divLogContent = document.getElementById("divLogContent");
const tblLog = document.getElementById("tblLog");

/**
 * Static URLs for the CtrlPanel flow
 */
const urlAPI = window.location.origin + "/web-apis/testctrl";
const urlLoginJSP = window.location.origin + "/web-apis/testctrl/login.jsp";

/**
 * Hook code listeners to actions and events in the CtrlPanel main flow
 */
window.addEventListener("resize", onPageResize);
document.addEventListener("DOMContentLoaded", onPageLoad);
btnLogout.addEventListener("click", onClickLogout);

/**
 * Callback when page is resized
 */
function onPageResize() {
    const width = window.innerWidth;
    const height = window.innerHeight;
    console.log(`Window resized: ${width}x${height}`);
    txtTitleSid.style.display = (width < 800) ? 'none' : 'inline';
    divLog.style.display = (height < 300) ? 'none' : 'block';
}

/**
 * Callback when page is loaded
 */
function onPageLoad() {
    txtTitleSid.innerText = sid;
    txtTitleName.innerText = username;
    setInterval(onStatusRequest, 4000);
    onStatusRequest();
}

/**
 * Timer callback sending a [GET ../web-api/testctrl?cmd=status] request to the server.
 */
var logId = 0
function onStatusRequest() {
    const row = tblLog.insertRow(-1);
    row.insertCell(0).textContent = logId++;
    row.insertCell(1).textContent = 'heartbeat';
    // retain only the mosts recent 100 logs (to limit memory usage)
    if (tblLog.rows.length > 100) {
        tblLog.deleteRow(0);
    }
    divLogContent.scrollTop = divLogContent.scrollHeight;
    //var request = new  XMLHttpRequest();
    // request.open("GET", `${urlAPI}?cmd=status&name=${username}`, true);
    // request.timeout = 2000;
    // request.onload = onStatusResponse;
    // request.withCredentials = true;
    // request.send();
}

/**
 * Callback for receiving the response from the [GET ../web-api/testctrl?cmd=status] request.
 */
function onStatusResponse() {
}

/**
 * Callback for clicking on the Host Logout button
 */
function onClickLogout(e) {
    e.preventDefault();
    var request = new  XMLHttpRequest();
    request.open("GET", `${urlAPI}?cmd=logout`, true);
    request.timeout = 2000;
    request.onload = onLogoutResponse;
    request.send();
}

/**
 * Callback for receiving the response from the REST API Host Logout call
 */
function onLogoutResponse() {
    var jsonResponse = JSON.parse(this.response);
    if (this.status != 200) {
        // alert on error (unexpected)
        // alert(`[${this.status}] ${jsonResponse._error}`);
    }
    // in all cases redirect to the Login page
    window.location.href = `${urlLoginJSP}?name=${username}`;
}

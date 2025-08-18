// #region: page parameters
const sid = (new URLSearchParams(window.location.search)).get("sid");
const username = (new URLSearchParams(window.location.search)).get("name");
// #endregion: page parameters

const txtTitleSid = document.getElementById("titleSid");
const txtTitleName = document.getElementById("titleName");
const btnLogout = document.getElementById("btnLogout");
const divLog = document.getElementById("divLog");
const divLogContent = document.getElementById("divLogContent");
const tblLog = document.getElementById("tblLog");

const btnSample = document.getElementById("btnSample");
const btnShowSessions = document.getElementById("btnShowSessions");
const btnSetPwd = document.getElementById("btnSetPwd");

const dlgAction = document.getElementById("dlgAction");
const dlgTitle = document.getElementById("dlgTitle");
const dlgActionClose = document.getElementById("dlgClose");
const dlgActionSource = document.getElementById("dlgSource");

const urlAPI = window.location.origin + "/web-apis/testctrl";
const urlLoginJSP = window.location.origin + "/web-apis/testctrl/login.jsp";

// #region: event listeners
window.addEventListener("resize", onPageResize);
document.addEventListener("DOMContentLoaded", onPageLoad);
btnLogout.addEventListener("click", onClickLogout);

btnSample.addEventListener("click", onActionSampleDlgOpen);
btnShowSessions.addEventListener("click", onActionSessionsDlgOpen);
btnSetPwd.addEventListener("click", onActionSetPwdDlgOpen);
dlgActionClose.addEventListener("click", onActionDlgClose);
// #endregion: event listeners

// #region: window resize callback
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
// #endregion: window resize callback

// #region: timer callback
/**
 * Timer callback sending a [GET ../web-api/testctrl?cmd=status] request to the server.
 */
function onStatusRequest() {
    var request = new  XMLHttpRequest();
    request.open("GET", `${urlAPI}?cmd=status&type=log`, true);
    request.timeout = 2000;
    request.onload = onStatusResponse;
    request.withCredentials = true;
    request.send();
}

/**
 * Callback for receiving the response from the [GET ../web-api/testctrl?cmd=status&type=log] request.
 */
function onStatusResponse() {
    var jsonResponse = JSON.parse(this.response);
    var logs = (this.status == 200) ? jsonResponse._logs : [jsonResponse._error];
    for (const log of logs) {
        const row = tblLog.insertRow(-1);
        row.insertCell(0).textContent = log._logTime;
        row.insertCell(1).textContent = log._logText;
        // retain only the mosts recent 100 logs (to limit memory usage)
        if (tblLog.rows.length > 100) {
            tblLog.deleteRow(0);
        }
    }
    divLogContent.scrollTop = divLogContent.scrollHeight;
}
// #endregion: timer callback

// #region: logout event handlers
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
    if (this.status != 200) {
        // alert(`[${this.status}] ${jsonResponse._error}`);
    }
    // error or not, redirect to the Login page
    window.location.href = `${urlLoginJSP}?name=${username}`;
}
// #endregion: Logout event handlers

// #region: action dialog event handlers
function onActionSampleDlgOpen(e) {
    e.preventDefault();
    dlgTitle.innerHTML = 'Sample Action';
    dlgAction.style.width = '100%';
    dlgAction.style.height = '100%';
    dlgAction.style.resize = 'both';
    dlgAction.showModal();
}

function onActionSessionsDlgOpen(e) {
    e.preventDefault();
    dlgTitle.innerHTML = 'Show Sessions';
    dlgAction.style.width = '80%';
    dlgAction.style.height = '60%';
    dlgAction.style.resize = 'none';
    dlgAction.showModal();
}

function onActionSetPwdDlgOpen(e) {
    e.preventDefault();
    dlgTitle.innerHTML = 'Set Password';
    dlgAction.style.width = '40%';
    dlgAction.style.height = '30%';
    dlgAction.style.resize = 'none';
    dlgAction.showModal();
}

function onActionDlgClose(e) {
    e.preventDefault();
    dlgAction.close();
}
// #endregion: action dialog event handlers
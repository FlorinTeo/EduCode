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
const dlgActionApply = document.getElementById("dlgApply");
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
dlgActionApply.addEventListener("click", onActionDlgApply);
dlgActionClose.addEventListener("click", onActionDlgClose);
// #endregion: event listeners

// #region: window resize callback
function onPageLoad() {
    txtTitleSid.innerText = sid;
    txtTitleName.innerText = username;
    setInterval(onStatusRequest, 4000);
    onStatusRequest();
}

function onPageResize() {
    const width = window.innerWidth;
    const height = window.innerHeight;
    console.log(`Window resized: ${width}x${height}`);
    txtTitleSid.style.display = (width < 800) ? 'none' : 'inline';
    divLog.style.display = (height < 300) ? 'none' : 'block';
}
// #endregion: window resize callback

// #region: timer callback
function onStatusRequest() {
    var request = new  XMLHttpRequest();
    request.open("GET", `${urlAPI}?cmd=status&type=log`, true);
    request.timeout = 2000;
    request.onload = onStatusResponse;
    request.withCredentials = true;
    request.send();
}

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
function onClickLogout(e) {
    e.preventDefault();
    var request = new  XMLHttpRequest();
    request.open("GET", `${urlAPI}?cmd=logout`, true);
    request.timeout = 2000;
    request.onload = onLogoutResponse;
    request.send();
}

function onLogoutResponse() {
    if (this.status != 200) {
        // alert(`[${this.status}] ${jsonResponse._error}`);
    }
    // error or not, redirect to the Login page
    window.location.href = `${urlLoginJSP}?name=${username}`;
}
// #endregion: Logout event handlers

// #region: action dialog event handlers
// #region: helper methods
function getFormattedTime() {
    const now = new Date();
    const pad = (n, z = 2) => ('00' + n).slice(-z);
    const MM = pad(now.getMonth() + 1);
    const dd = pad(now.getDate());
    const HH = pad(now.getHours());
    const mm = pad(now.getMinutes());
    const ss = pad(now.getSeconds());
    const SS = pad(Math.floor(now.getMilliseconds() / 10)); // hundredths
    return `${MM}/${dd} ${HH}:${mm}:${ss}.${SS}`;
}
// #endregion: helper methodss

function onActionDlgApply(e) {
    e.preventDefault();
    const logTime = getFormattedTime();
    var logText = "Unknown action applied";
    switch (dlgAction.trigger) {
        case btnSample:
            logText = `Sample action applied`;
            break;
        case btnSetPwd:
            logText = `Set password action applied`;
            break;
    }

    const row = tblLog.insertRow(-1);
    row.insertCell(0).textContent = logTime;
    row.insertCell(1).textContent = logText;
    if (tblLog.rows.length > 100) {
        tblLog.deleteRow(0);
    }
    divLogContent.scrollTop = divLogContent.scrollHeight;
    dlgAction.close();
}

function onActionDlgClose(e) {
    e.preventDefault();
    dlgAction.close();
}

// #region: actSample handlers
function onActionSampleDlgOpen(e) {
    e.preventDefault();
    dlgAction.trigger = btnSample;
    dlgTitle.innerHTML = 'Sample Action';
    dlgAction.style.width = '100%';
    dlgAction.style.height = '100%';
    dlgAction.style.resize = 'both';

    dlgActionApply.style.display = 'block';
    dlgAction.showModal();
}
// #endregion: actSample handlers

// #region: actSessions handlers
function onActionSessionsDlgOpen(e) {
    e.preventDefault();
    dlgAction.trigger = btnShowSessions;
    dlgTitle.innerHTML = 'Show Sessions';
    dlgAction.style.width = '80%';
    dlgAction.style.height = '60%';
    dlgAction.style.resize = 'none';

    dlgActionApply.style.display = 'none';
    dlgAction.showModal();
}
// #endregion: actSession handlers

// #region: actSetPwd handlers
function onActionSetPwdDlgOpen(e) {
    e.preventDefault();
    dlgAction.trigger = btnSetPwd;
    dlgTitle.innerHTML = 'Set Password';
    dlgAction.style.width = '36%';
    dlgAction.style.height = '24%';
    dlgAction.style.resize = 'none';

    if (typeof setPwd_loaded == "undefined") {
        fetch('actSetPwd.jsp')
        .then(res => res.text())
        .then(html => {
            dlgActionSource.innerHTML = html;
            // Dynamically load JS
            const script = document.createElement('script');
            script.src = 'js/actSetPwd.js';
            document.body.appendChild(script);
            script.onload = () => {
                setPwd_reset(username);
            };
        });
    } else {
        setPwd_reset(username);
    }

    dlgActionApply.style.display = 'block';
    dlgAction.showModal();
}
// #endregion: actSetPwd handlers
// #endregion: action dialog event handlers
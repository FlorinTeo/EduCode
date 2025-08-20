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
const btnSessionMgmt = document.getElementById("btnSessionMgmt");
const btnUserMgmt = document.getElementById("btnUserMgmt");

const dlgAction = document.getElementById("dlgAction");
const dlgTitle = document.getElementById("dlgTitle");
const dlgActionApply = document.getElementById("dlgApply");
const dlgActionClose = document.getElementById("dlgClose");
const dlgActionSource = document.getElementById("dlgSource");

const actMap = {
    actSample: { div: null },
    actSessionMgmt: { div: null },
    actUserMgmt: { div: null },
};

const urlAPI = window.location.origin + "/web-apis/testctrl";
const urlLoginJSP = window.location.origin + "/web-apis/testctrl/login.jsp";

// #region: event listeners
window.addEventListener("resize", onPageResize);
document.addEventListener("DOMContentLoaded", onPageLoad);
btnLogout.addEventListener("click", onClickLogout);

btnSample.addEventListener("click", onActSampleDlgOpen);
btnSessionMgmt.addEventListener("click", onActSessionMgmt);
btnUserMgmt.addEventListener("click", onActUserMgmt);

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
function addLog(logText) {
    const now = new Date();
    const pad = (n, z = 2) => ('00' + n).slice(-z);
    const MM = pad(now.getMonth() + 1);
    const dd = pad(now.getDate());
    const HH = pad(now.getHours());
    const mm = pad(now.getMinutes());
    const ss = pad(now.getSeconds());
    const SS = pad(Math.floor(now.getMilliseconds() / 10)); // hundredths
    const logTime = `${MM}/${dd} ${HH}:${mm}:${ss}.${SS}`;
    
    const row = tblLog.insertRow(-1);
    row.insertCell(0).textContent = logTime;
    row.insertCell(1).textContent = logText;
    if (tblLog.rows.length > 100) {
        tblLog.deleteRow(0);
    }
    divLogContent.scrollTop = divLogContent.scrollHeight;
}
// #endregion: helper methodss
function selectAction(actName) {
    Array.from(dlgActionSource.children).forEach(actDiv => actDiv.style.display = 'none');
    if (actMap[actName].div == null) {
        fetch(`${actName}.jsp`)
            .then(res => res.text())
            .then(html => { dlgActionSource.insertAdjacentHTML('beforeend', html); })
            .then(() => {
                actMap[actName].div = document.getElementById(`${actName}_div`);
                const script = document.createElement(`script`);
                script.src = `js/${actName}.js`;
                script.onload = () => {
                    actMap[actName].onOpen = window[`${actName}_onOpen`];
                    actMap[actName].onApply = window[`${actName}_onApply`];
                    actMap[actName].onCancel = window[`${actName}_onCancel`];
                    if (actMap[actName].onOpen) {
                        actMap[actName].onOpen();
                    }
                };
                document.body.appendChild(script);
            });
    } else {
        actMap[actName].div.style.display = 'block';
        if (actMap[actName].onOpen) {
            actMap[actName].onOpen();
        }
    }
}

function onActionDlgApply(e) {
    e.preventDefault();
    const actName = Object.keys(actMap).find(key => actMap[key].div && actMap[key].div.style.display !== "none");
    if (actMap[actName].onApply) {
        actMap[actName].onApply();
    }
    dlgAction.close();
}

function onActionDlgClose(e) {
    e.preventDefault();
    const actName = Object.keys(actMap).find(key => actMap[key].div && actMap[key].div.style.display !== "none");
    if (actMap[actName].onCancel) {
        actMap[actName].onCancel();
    }
    dlgAction.close();
}

// #region: actSample handlers
function onActSampleDlgOpen(e) {
    e.preventDefault();
    selectAction("actSample");
    dlgTitle.innerHTML = 'Sample Action';
    dlgAction.style.width = '100%';
    dlgAction.style.height = '100%';
    dlgAction.style.resize = 'both';
    dlgActionApply.style.display = 'block';
    dlgAction.showModal();
}
// #endregion: actSample handlers

// #region: actSessions handlers
function onActSessionMgmt(e) {
    e.preventDefault();
    selectAction("actSessionMgmt");
    dlgTitle.innerHTML = 'Session Management';
    dlgAction.style.width = '80%';
    dlgAction.style.height = '60%';
    dlgAction.style.resize = 'none';
    dlgActionApply.style.display = 'none';
    dlgAction.showModal();
}
// #endregion: actSession handlers

// #region: actSetPwd handlers
let divSetPwd = document.getElementById("setPwd_div");
function onActUserMgmt(e) {
    e.preventDefault();
    selectAction("actUserMgmt");
    dlgTitle.innerHTML = 'User Management';
    dlgAction.style.width = '36%';
    dlgAction.style.height = '24%';
    dlgAction.style.resize = 'none';
    dlgActionApply.style.display = 'block';
    dlgAction.showModal();
}
// #endregion: actSetPwd handlers
// #endregion: action dialog event handlers
// #region: page parameters
const sid = (new URLSearchParams(window.location.search)).get("sid");
const username = (new URLSearchParams(window.location.search)).get("name");
const urlAPI = window.location.origin + "/web-apis/testctrl";
const urlLoginJSP = window.location.origin + "/web-apis/testctrl/login.jsp";
const actVer = "2.0";
// #endregion: page parameters

const txtTitleSid = document.getElementById("titleSid");
const txtTitleName = document.getElementById("titleName");
const btnLogout = document.getElementById("btnLogout");
const divLog = document.getElementById("divLog");
const divLogContent = document.getElementById("divLogContent");
const tblLog = document.getElementById("tblLog");

const btnTestMgmt = document.getElementById("btnTestMgmt");
const btnSessionMgmt = document.getElementById("btnSessionMgmt");
const btnUserMgmt = document.getElementById("btnUserMgmt");

const dlgAction = document.getElementById("dlgAction");
const dlgActionTitle = document.getElementById("dlgActionTitle");
const dlgActionApply = document.getElementById("dlgActionApply");
const dlgActionClose = document.getElementById("dlgActionClose");
const dlgActionPane = document.getElementById("dlgActionPane");

const actMap = {
    actTestEditor: { div: null },
    actSessionMgmt: { div: null },
    actUserMgmt: { div: null },
};

// #region: event listeners
document.addEventListener("DOMContentLoaded", onPageLoad);
window.addEventListener("resize", onPageResize);
btnLogout.addEventListener("click", onClickLogout);

btnTestMgmt.addEventListener("click", onActTestEditor);
btnSessionMgmt.addEventListener("click", onActSessionMgmt);
btnUserMgmt.addEventListener("click", onActUserMgmt);

dlgActionApply.addEventListener("click", onActionDlgApply);
dlgActionClose.addEventListener("click", onActionDlgClose);
// #endregion: event listeners

// #region: window resize callback
async function onPageLoad() {
    txtTitleSid.innerText = sid;
    txtTitleName.innerText = username;
    setInterval(onStatusRequest, 4000);
    onStatusRequest();
}

async function onPageResize() {
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
    request.open("GET", `${urlAPI}?cmd=status&op=log`, true);
    request.timeout = 2000;
    request.onload = onStatusResponse;
    request.withCredentials = true;
    request.send();
}

function onStatusResponse() {
    // deserialize Answer.Msg response
    var jsonResponse = JSON.parse(this.response);
    var logs = (this.status == 200) ? jsonResponse._logs : [{_logTime:logTime(), _logText:`[*] ${jsonResponse._error}` }];
    for (const log of logs) {
        const row = tblLog.insertRow(-1);
        row.insertCell(0).textContent = log._logTime;
        row.insertCell(1).innerHTML = log._logText;
        // retain only the mosts recent 100 logs (to limit memory usage)
        if (tblLog.rows.length > 100) {
            tblLog.deleteRow(0);
        }
    }
    divLogContent.scrollTop = divLogContent.scrollHeight;
}
// #endregion: timer callback

// #region: logout event handlers
async function onClickLogout(e) {
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
function logTime() {
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

export function addLog(logText) {
    const row = tblLog.insertRow(-1);
    row.insertCell(0).textContent = logTime();
    row.insertCell(1).textContent = `[*] ${logText}`;
    if (tblLog.rows.length > 100) {
        tblLog.deleteRow(0);
    }
    divLogContent.scrollTop = divLogContent.scrollHeight;
}
// #endregion: helper methodss

async function selectAction(actName) {
    Array.from(dlgActionPane.children).forEach(actDiv => actDiv.style.display = 'none');

    // If the div for this action is not yet loaded, fetch its .jsp and load its .js module.
    // Use the .jsp to extract the div and insert it into the document
    // Use the .js to get the action handlers
    if (actMap[actName].div == null) {
        const res = await fetch(`${actName}.jsp?ver=${actVer}`);
        const html = await res.text();
        // the dialog action pane needs to be inserted ahead of loading the module!
        dlgActionPane.insertAdjacentHTML('beforeend', html);

        // module is trying to load elements from the dlgActionPane, which needs to be in the document!
        const module = await import(`./${actName}.js?ver=${actVer}`);

        actMap[actName].div = document.getElementById(`${actName}_div`);
        actMap[actName].onCreate = module.onCreate;
        actMap[actName].onOpen = module.onOpen;
        actMap[actName].onApply = module.onApply;
        actMap[actName].onCancel = module.onCancel;
        if (actMap[actName].onCreate) {
            await actMap[actName].onCreate(sid, username, urlAPI, addLog);
        }
    } else {
        // the action was previously loaded so its div and handlers are available
        // just need to display the div
        actMap[actName].div.style.display = 'block';
    }
}

async function onActionDlgApply(e) {
    e.preventDefault();
    const actName = Object.keys(actMap).find(key => actMap[key].div && actMap[key].div.style.display !== "none");
    if (actName && actMap[actName].onApply) {
        let result = await actMap[actName].onApply();
        if (result) {
            dlgAction.close();
        }
    }
}

async function onActionDlgClose(e) {
    e.preventDefault();
    const actName = Object.keys(actMap).find(key => actMap[key].div && actMap[key].div.style.display !== "none");
    if (actName && actMap[actName].onCancel) {
        actMap[actName].onCancel();
    }
    dlgAction.close();
}

// #region: actSample handlers
async function onActTestEditor(e) {
    e.preventDefault();
    await selectAction("actTestEditor");
    dlgAction.style.width = '100%';
    dlgAction.style.height = '100%';
    dlgAction.style.minWidth = "640px";
    dlgAction.style.minHeight = "480px";
    dlgAction.style.resize = 'both';
    dlgActionTitle.innerHTML = 'Test Editor Action';
    dlgActionApply.style.display = 'block';
    dlgAction.style.minWidth = "640px";
    dlgAction.style.minHeight = "480px";
    dlgAction.showModal();
    // Once the dialog is rendered, call the action's onOpen() handler, if defined
    requestAnimationFrame(() => {
        if (actMap.actTestEditor.onOpen) {
            actMap.actTestEditor.onOpen();
        }
    });
}
// #endregion: actSample handlers

// #region: actSessions handlers
async function onActSessionMgmt(e) {
    e.preventDefault();
    await selectAction("actSessionMgmt");
    dlgAction.style.width = '60%';
    dlgAction.style.height = '40%';
    dlgAction.style.resize = 'none';
    dlgActionTitle.innerHTML = 'Session Management';
    dlgActionApply.style.display = 'none';
    dlgAction.showModal();
    // Once the dialog is rendered, call the action's onOpen() handler, if defined
    requestAnimationFrame(() => {
        if (actMap.actSessionMgmt.onOpen) {
            actMap.actSessionMgmt.onOpen();
        }
    });
}
// #endregion: actSession handlers

// #region: actUserMgmt handlers
async function onActUserMgmt(e) {
    e.preventDefault();
    await selectAction("actUserMgmt");
    dlgAction.style.width = '';
    dlgAction.style.height = '';
    dlgAction.style.minWidth = '';
    dlgAction.style.minHeight = '';
    dlgAction.style.resize = 'none';
    dlgActionTitle.innerHTML = 'User Management';
    dlgActionApply.style.display = 'block';
    dlgAction.showModal();
    // Once the dialog is rendered, call the action's onOpen() handler, if defined
    requestAnimationFrame(() => {
        if (actMap.actUserMgmt.onOpen) {
            actMap.actUserMgmt.onOpen();
        }
    });
}
// #endregion: actUserMgmt handlers
// #endregion: action dialog event handlers
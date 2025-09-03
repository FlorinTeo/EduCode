// #region: page referenced parameters
let refSid;
let refUsername;
let refUrlAPI;
let refAddLog;
// #endregion page referenced parameters

const actUserMgmt_edtName = document.getElementById("actUserMgmt_edtName");
const actUserMgmt_edtPwdNew = document.getElementById("actUserMgmt_edtPwd");
const actUserMgmt_edtPwdConfirm = document.getElementById("actUserMgmt_edtPwdConfirm");
const actUserMgmt_txtOutput = document.getElementById("actUserMgmt_txtOutput");

// #region: exported fields and methods
export async function onCreate(sid, username, urlAPI, addLog) {
    refSid = sid;
    refUsername = username;
    refUrlAPI = urlAPI;
    refAddLog = addLog;
}

export async function onOpen() {
    actUserMgmt_edtName.value = refUsername;
    actUserMgmt_edtPwdNew.value = "";
    actUserMgmt_edtPwdConfirm.value = "";
    actUserMgmt_txtOutput.innerHTML = "";
    actUserMgmt_txtOutput.classList.add('actUserMgmt-err-div');
}

export async function onApply() {
    if (actUserMgmt_edtName.value.trim() === "") {
        actUserMgmt_txtOutput.innerHTML = "Name is required!";
        return false;
    }
    if (actUserMgmt_edtPwdNew.value.trim() === "") {
        actUserMgmt_txtOutput.innerHTML = "New password is required!";
        return false;
    }
    if (actUserMgmt_edtPwdNew.value !== actUserMgmt_edtPwdConfirm.value) {
        actUserMgmt_txtOutput.innerHTML = "Passwords do not match!";
        return false;
    }
    var request = new  XMLHttpRequest();
    request.open("GET", `${refUrlAPI}?cmd=set&op=setusr&name=${actUserMgmt_edtName.value}&pwd=${actUserMgmt_edtPwdNew.value}`, true);
    request.timeout = 2000;
    request.onload = onUsrMgmtResponse;
    request.withCredentials = true;
    request.send();
    return true;
}

export async function onCancel() {
    refAddLog(`User '${actUserMgmt_edtName.value}' update was canceled.`);
}
// #endregion: exported methods

function onUsrMgmtResponse() {
    var jsonResponse = JSON.parse(this.response);
    if (this.status == 200) {
        refAddLog(`User '${actUserMgmt_edtName.value}' was updated!`);
    } else {
        refAddLog(jsonResponse._error);
    }
}

const actUserMgmt_edtName = document.getElementById("actUserMgmt_edtName");
const actUserMgmt_edtPwd = document.getElementById("actUserMgmt_edtPwd");
const actUserMgmt_txtOutput = document.getElementById("actUserMgmt_txtOutput");

function actUserMgmt_onOpen() {
    actUserMgmt_edtName.value = username;
    actUserMgmt_edtPwd.value = "";
    actUserMgmt_txtOutput.innerHTML = "";
}

function actUserMgmt_onApply() {
    addLog(`User '${actUserMgmt_edtName.value}' was updated!`);
}

function actUserMgmt_onCancel() {
    addLog(`User '${actUserMgmt_edtName.value}' update was canceled.`);
}

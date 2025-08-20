const actUserMgmt_div = document.getElementById("actUserMgmt_div");
const actUserMgmt_edtName = document.getElementById("actUserMgmt_edtName");
const actUserMgmt_edtPwd = document.getElementById("actUserMgmt_edtPwd");
const actUserMgmt_txtOutput = document.getElementById("actUserMgmt_txtOutput");

function actUserMgmt_onOpen() {
    actUserMgmt_edtName.value = username;
    actUserMgmt_edtPwd.value = "";
    actUserMgmt_txtOutput.innerHTML = "";
    actUserMgmt_txtOutput.classList.add('actUserMgmt-err-div');
}

function actUserMgmt_onApply() {
    actUserMgmt_txtOutput.innerHTML = actUserMgmt_txtOutput.innerHTML + "Action not implemented yet!<br>";
    //addLog(`User '${actUserMgmt_edtName.value}' was updated!`);
    return false;
}

function actUserMgmt_onCancel() {
    addLog(`User '${actUserMgmt_edtName.value}' update was canceled.`);
}

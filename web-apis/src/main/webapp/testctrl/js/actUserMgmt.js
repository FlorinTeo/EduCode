const actUserMgmt_div = document.getElementById("actUserMgmt_div");
const actUserMgmt_edtName = document.getElementById("actUserMgmt_edtName");
const actUserMgmt_edtPwdNew = document.getElementById("actUserMgmt_edtPwd");
const actUserMgmt_edtPwdConfirm = document.getElementById("actUserMgmt_edtPwdConfirm");
const actUserMgmt_txtOutput = document.getElementById("actUserMgmt_txtOutput");

function actUserMgmt_onOpen() {
    actUserMgmt_edtName.value = username;
    actUserMgmt_edtPwdNew.value = "";
    actUserMgmt_edtPwdConfirm.value = "";
    actUserMgmt_txtOutput.innerHTML = "";
    actUserMgmt_txtOutput.classList.add('actUserMgmt-err-div');
}

function actUserMgmt_onApply() {
    if (actUserMgmt_edtName.value.trim() === "") {
        actUserMgmt_txtOutput.innerHTML = "Name is required!";
        return false;
    }
    if (actUserMgmt_edtPwdNew.value !== actUserMgmt_edtPwdConfirm.value) {
        actUserMgmt_txtOutput.innerHTML = "Passwords do not match!";
        return false;
    }
    var request = new  XMLHttpRequest();
    request.open("GET", `${urlAPI}?cmd=set&op=setusr&name=${actUserMgmt_edtName.value}&pwd=${actUserMgmt_edtPwdNew.value}`, true);
    request.timeout = 2000;
    request.onload = onUsrMgmtResponse;
    request.withCredentials = true;
    request.send();
    //actUserMgmt_txtOutput.innerHTML = "Action not implemented yet!<br>";
    //addLog(`User '${actUserMgmt_edtName.value}' was updated!`);
    return true;
}

function onUsrMgmtResponse() {
    var jsonResponse = JSON.parse(this.response);
    if (this.status == 200) {
        addLog(`User '${actUserMgmt_edtName.value}' was updated!`);
    } else {
        addLog(jsonResponse._error);
    }
}

function actUserMgmt_onCancel() {
    addLog(`User '${actUserMgmt_edtName.value}' update was canceled.`);
}

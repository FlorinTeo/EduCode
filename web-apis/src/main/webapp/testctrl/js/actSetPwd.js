const setPwd_loaded = true;

const setPwd_edtName = document.getElementById("setPwd_edtName");
const setPwd_edtCrtPwd = document.getElementById("setPwd_edtCrtPwd");
const setPwd_edtNewPwd = document.getElementById("setPwd_edtNewPwd");
const setPwd_txtOutput = document.getElementById("setPwd_txtOutput");

function setPwd_reset(username) {
    setPwd_edtName.value = username ?? "";
    setPwd_edtCrtPwd.value = "";
    setPwd_edtNewPwd.value = "";
}

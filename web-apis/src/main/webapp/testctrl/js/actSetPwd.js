// #region: page parameters
const setPwd_username = (new URLSearchParams(window.location.search)).get("name");
// #endregion: page parameters

const setPwd_edtName = document.getElementById("setPwd_edtName");
const setPwd_edtCrtPwd = document.getElementById("setPwd_edtCrtPwd");
const setPwd_edtNewPwd = document.getElementById("setPwd_edtNewPwd");
const setPwd_txtOutput = document.getElementById("setPwd_txtOutput");

document.addEventListener("DOMContentLoaded", onPageLoad);

function onPageLoad() {
    setPwd_edtName.value = setPwd_username ?? "";
}

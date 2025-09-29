// #region: page parameters
const username = (new URLSearchParams(window.location.search)).get("name");
// #endregion: page parameters

const edtName = document.getElementById("edtName");
const edtPwd = document.getElementById("edtPwd");
const btnLogin = document.getElementById("btnLogin");
const txtOutput = document.getElementById("txtOutput");

const urlAPI = window.location.origin + "/web-apis/testctrl";
const urlAdminPanelJSP = window.location.origin + "/web-apis/testctrl/adminPanel.jsp";
const urlAdminPanelVer = "2.0";

// #region: event listeners
document.addEventListener("DOMContentLoaded", onPageLoad);
btnLogin.addEventListener("click", onClickLogin);
// #endregion: event listeners

// #region: page load handler
function onPageLoad() {
    edtName.value = username ?? "";
}
// #endregion: page load handler

// #region: login event handlers
function onClickLogin(e) {
    e.preventDefault();
    var name = edtName.value;
    var pwd = edtPwd.value;
    if(name == null || name == "") {
        alert("Error: Need a name!");
    } else {
        var request = new  XMLHttpRequest();
        request.open("GET", `${urlAPI}?cmd=login&name=${name}&pwd=${pwd}`, true);
        request.timeout = 2000;
        request.onload = onLoginResponse;
        request.withCredentials = true;
        request.send();
    }
}

function onLoginResponse() {
    var jsonResponse = JSON.parse(this.response);
    if (this.status == 200) {
        // when successful or user already logged in, redirect to the AdminPanel page
        window.location.href = `${urlAdminPanelJSP}?sid=${jsonResponse._sid}&name=${edtName.value}&ver=${urlAdminPanelVer}`;
    } else {
        // otherwise display the response on the login page.
        txtOutput.innerHTML = `[${this.status}] ${jsonResponse._error}`;
        txtOutput.classList.add('err-div');
    }
}
// #endregion: login event handlers
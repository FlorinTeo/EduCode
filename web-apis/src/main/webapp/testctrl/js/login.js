/**
 * Login page controls
 */
const edtName = document.getElementById("edtName");
const edtPwd = document.getElementById("edtPwd");
const btnLogin = document.getElementById("btnLogin");
const txtOutput = document.getElementById("txtOutput");

/**
 * Hook code listeners to actions and events in the login flow
 */
btnLogin.addEventListener("click", onClickLogin);

/**
 * Static URLs for the login flow
 */
const urlAPI = window.location.origin + "/web-apis/testctrl";
const urlCtrlPanelJSP = window.location.origin + "/web-apis/testctrl/ctrlpanel.jsp";

/**
 * Callback for clicking on the "Login" button
 */
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

/**
 * Callback for receiving the response from '../testctrl?cmd=login&name=<name>&pwd=<password>'
 */
function onLoginResponse() {
    var jsonResponse = JSON.parse(this.response);
    if (this.status == 200) {
        // when successful or user already logged in, redirect to the CtrlPanel page
        window.location.href = `${urlCtrlPanelJSP}?sid=${jsonResponse._sid}&name=${edtName.value}`;
    } else {
        // otherwise display the response on the login page.
        txtOutput.innerHTML = `[${this.status}] ${jsonResponse._error}`;
    }
}
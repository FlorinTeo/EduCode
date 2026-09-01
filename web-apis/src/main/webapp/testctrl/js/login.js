// #region: page parameters
const username = (new URLSearchParams(window.location.search)).get("name");
const initErr = decodeURIComponent((new URLSearchParams(window.location.search)).get("err") ?? "");
// #endregion: page parameters

const edtName = document.getElementById("edtName");
const btnLogin = document.getElementById("btnLogin");
const txtOutput = document.getElementById("txtOutput");

const urlAPI = window.location.origin + "/web-apis/testctrl";

// #region: event listeners
document.addEventListener("DOMContentLoaded", onPageLoad);
btnLogin.addEventListener("click", onClickLogin);
// #endregion: event listeners

// #region: page load handler
function onPageLoad() {
    edtName.value = username ?? "";
    if (initErr != null && initErr != "") {
        txtOutput.innerHTML = initErr;
    }
}
// #endregion: page load handler

// #region: login event handlers
function onClickLogin(e) {
    e.preventDefault();
    const name = edtName.value;
    if(name == null || name == "") {
        alert("Error: Need a name!");
    } else {
        const request = new XMLHttpRequest();
        request.open("GET", `${urlAPI}?cmd=login&name=${encodeURIComponent(name)}`, true);
        request.timeout = 2000;
        request.onload = onLoginResponse;
        request.withCredentials = true;
        request.send();
    }
}

function onLoginResponse() {
    const jsonResponse = JSON.parse(this.response);
    if (this.status == 200 && jsonResponse._redirect) {
        // when successful the backend gives the uri for the portal
        window.location.href = jsonResponse._redirect;
    } else {
        // otherwise display the response on the login page.
        txtOutput.innerHTML = `[${this.status}] ${jsonResponse._error}`;
        txtOutput.classList.add("err-div");
    }
}
// #endregion: login event handlers

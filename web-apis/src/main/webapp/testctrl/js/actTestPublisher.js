import { CtrlComboBox } from "./ctrlComboBox.js?ver=1.0";

// #region: page referenced parameters
let refUrlAPI;
let refAddLog;
// #endregion page referenced parameters

// #region: action globals
const actTestPbl_cbTestName = new CtrlComboBox("actTestPublisher_cb");
const actTestPbl_txtOutput = document.getElementById("actTestPublisher_txtOutput");
// #endregion: action globals

// #region: Exported methods
export async function onCreate(sid, username, urlAPI, addLog) {
    refUrlAPI = urlAPI;
    refAddLog = addLog;
}

export async function onOpen() {
    actTestPbl_requestQueryTSet();
    actTestPbl_cbTestName.setEventListener("change", actTestPbl_onCbChanged);
    actTestPbl_txtOutput.innerHTML = "";
}

export async function onCancel() {
    refAddLog("actTestPbl_onCancel called");
}
// #endregion: Exported methods

// #region: HTML event handlers
/**
 * Handler called when the selection in the combo box changes
 */
async function actTestPbl_onCbChanged(e) {
    if (e.target) {
        actTestPbl_requestQueryTest(e.target.text);
    } else {
        actTestPbl_txtOutput.innerHTML = "";
    }
}
// #endregion: HTML event handlers

// #region: Backend API calls
// #region: ..?cmd=query&op=tset
function actTestPbl_requestQueryTSet() {
    var request = new  XMLHttpRequest();
    request.open("GET", `${refUrlAPI}?cmd=query&op=tset`, true);
    request.timeout = 2000;
    request.onload = actTestPbl_onResponseQueryTSet;
    request.withCredentials = true;
    request.send();
}

function actTestPbl_onResponseQueryTSet() {
    // deserialize Answer.TList response
    const jsonResponse = JSON.parse(this.response);
    if (this.status == 200) {
        const tList = jsonResponse._tList.map((item, index) => ({
            id: `cb${index + 1}`,
            text: item._tName
        })).sort((a, b) => a.text.localeCompare(b.text));

        actTestPbl_cbTestName.setOptions(tList);
    } else {
        refAddLog(`[${this.status}] ${jsonResponse._error}`);
    }
}
// #endregion: ..?cmd=query&op=tset

// #region: ..?cmd=query&op=test&tid=<testId>
function actTestPbl_requestQueryTest(testId) {
    var request = new  XMLHttpRequest();
    request.open("GET", `${refUrlAPI}?cmd=query&op=test&tid=${testId}`, true);
    request.timeout = 2000;
    request.onload = actTestPbl_onResponseQueryTest;
    request.withCredentials = true;
    request.send();
}

function actTestPbl_onResponseQueryTest() {
    // deserialize Answer.TData response
    const jsonResponse = JSON.parse(this.response);
    if (this.status != 200) {
        actTestPbl_txtOutput.innerHTML = `[${this.status}] ${jsonResponse._error}`;
        return;
    }
    let ref = jsonResponse._tHeader._tName;
    let refTest = jsonResponse._tHeader._links.test;
    let refAnswers = jsonResponse._tHeader._links.answers;
    let nMCQ = 0;
    let nFRQ = 0;
    let nAPX = 0;
    jsonResponse._qHeaders.forEach(qHeader => {
        nMCQ += (qHeader._qType == "mcq" || qHeader._qType == "mcb") ? qHeader._qCount : 0;
        nFRQ += (qHeader._qType == "frq") ? qHeader._qCount : 0;
        nAPX += (qHeader._qType == "apx") ? qHeader._qCount : 0;
    });
    let htmlText = `<b>${ref}</b><br><div style="padding-left: 10px; padding-top: 4px;">`;
    htmlText += `Questions: MCQ=<b>${nMCQ}</b>; FRQ=<b>${nFRQ}</b>; APX=<b>${nAPX}</b>;<br>`;
    htmlText += `Reference: <a href="${refTest}" target="_blank">test</a> | <a href="${refAnswers}" target="_blank">answers</a><br>`;
    htmlText += `</div><div style="padding-left: 60px;">`;
    jsonResponse._variants.forEach(variant => {
        let ver = variant._tVersion;
        let verTest = variant._links.test;
        let verAnswers = variant._links.answers;
        htmlText += `${ver}: <a href="${verTest}" target="_blank">test</a> | <a href="${verAnswers}" target="_blank">answers</a><br>`;
    });
    htmlText += `</div>`;
    actTestPbl_txtOutput.innerHTML = htmlText;
}
// #endregion: ..?cmd=query&op=test&tid=<testId>
// #endregion: Backend API calls
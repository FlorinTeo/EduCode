import { CtrlComboBox } from "./ctrlComboBox.js?ver=1.0";

// #region: page referenced parameters
let refUrlAPI;
let refAddLog;
// #endregion page referenced parameters

// #region: action globals
const actTestPbl_cbTestName = new CtrlComboBox("actTestPublisher_cb");
// #endregion: action globals

// #region: Exported methods
export async function onCreate(sid, username, urlAPI, addLog) {
    refUrlAPI = urlAPI;
    refAddLog = addLog;
}

export async function onOpen() {
    actTestPbl_requestQueryTSet();
    actTestPbl_cbTestName.setEventListener("changed", actTestPbl_onCbChanged);
}

export async function onCancel() {
    refAddLog("actTestPbl_onCancel called");
    refAddLog(actTestPbl_cbTestName.getValue());
}
// #endregion: Exported methods

// #region: HTML event handlers
/**
 * Handler called when the selection in the combo box changes
 */
async function actTestPbl_onCbChanged(e) {
    if (e.target) {
        refAddLog(`ComboBox selected: ${e.target.text}`);
    } else {
        refAddLog(`ComboBox cleared.`);
    }
}
// #endregion: HTML event handlers

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
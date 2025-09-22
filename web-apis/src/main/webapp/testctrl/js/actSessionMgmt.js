import { CtrlComboBox } from "./ctrlComboBox.js?ver=1.0";

// #region: page referenced parameters
let refAddLog;
// #endregion page referenced parameters

// #region: action globals
let actSessionMgmt_cb;
// #endregion: action globals

// #endregion: exported fields and methods
export async function onCreate(sid, username, urlAPI, addLog) {
    refAddLog = addLog;
}

export async function onOpen() {
    if (!actSessionMgmt_cb) {
        actSessionMgmt_cb = new CtrlComboBox("actSessionMgmt_cb");
    }
    actSessionMgmt_cb.setOptions([
        { id: 'cb1', text: 'Unit 1: AP CS-A' },
        { id: 'cb2', text: 'Unit 1: Data Structures' }
    ]);
    actSessionMgmt_cb.setEventListener("changed", onCbChanged);
}

async function onCbChanged(e) {
    if (e.target) {
        refAddLog(`ComboBox selected: ${e.target.text}`);
    } else {
        refAddLog(`ComboBox cleared.`);
    }
}

export async function onCancel() {
    refAddLog("actSessionMgmt_onCancel called");
    refAddLog(actSessionMgmt_cb.getValue());
}
// #endregion: exported fields and methods

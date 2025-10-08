import { CtrlComboBox } from "./ctrlComboBox.js?ver=1.0";

// #region: page referenced parameters
let refAddLog;
// #endregion page referenced parameters

// #region: action globals
const actTestPbl_cb = new CtrlComboBox("actTestPublisher_cb");
// #endregion: action globals

// #endregion: exported fields and methods
export async function onCreate(sid, username, urlAPI, addLog) {
    refAddLog = addLog;
}

export async function onOpen() {
    actTestPbl_cb.setOptions([
        { id: 'cb1', text: 'Unit 1: AP CS-A' },
        { id: 'cb2', text: 'Unit 1: Data Structures' }
    ]);
    actTestPbl_cb.setEventListener("changed", onCbChanged);
}

async function onCbChanged(e) {
    if (e.target) {
        refAddLog(`ComboBox selected: ${e.target.text}`);
    } else {
        refAddLog(`ComboBox cleared.`);
    }
}

export async function onCancel() {
    refAddLog("actTestPbl_onCancel called");
    refAddLog(actTestPbl_cb.getValue());
}
// #endregion: exported fields and methods

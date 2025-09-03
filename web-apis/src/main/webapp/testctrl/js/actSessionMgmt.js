// #region: page referenced parameters
let refAddLog;
// #endregion page referenced parameters

// #endregion: exported fields and methods
export async function onCreate(sid, username, urlAPI, addLog) {
    refAddLog = addLog;
}

export async function onOpen() {
}

export async function onCancel() {
    refAddLog("actSessionMgmt_onCancel called");
}
// #endregion: exported fields and methods

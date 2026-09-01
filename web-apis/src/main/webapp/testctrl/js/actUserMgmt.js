// #region: page referenced parameters
let refUrlAPI;
let refAddLog;
// #endregion page referenced parameters

// #region: exported fields and methods
export async function onCreate(sid, username, urlAPI, addLog) {
    refUrlAPI = urlAPI;
    refAddLog = addLog;
}

export async function onOpen() {
    refAddLog(`User Management action panel opened.`);
}

export async function onApply() {
    refAddLog(`User Management action panel applied.`);
    return true;
}

export async function onCancel() {
    refAddLog(`User Management action panel cancelled.`);
}
// #endregion: exported methods

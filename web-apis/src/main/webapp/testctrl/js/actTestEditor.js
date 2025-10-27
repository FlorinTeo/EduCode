import { CtrlComboBox } from "./ctrlComboBox.js?ver=2.0";
import { CheckedList } from "./ctrlCheckedList.js?ver=2.3";

// #region: External references
let refUrlAPI;
let refAddLog;
// #endregion: External references

// #region: Action constants
const actTestEditor_div = document.getElementById("actTestEditor_div");
const actTestEdt_cbTestName = new CtrlComboBox("actTestMgmt_cbTestName");
const actTestEdt_edtFilter = document.getElementById("actTestEdt_edtFilter");
const actTestEdt_ckbMCQ = document.getElementById("actTestEdt_ckb_allMCQ");
const actTestEdt_lstMCQ = new CheckedList("actTestsMgmt_lstMCQ");
const actTestEdt_ckbFRQ = document.getElementById("actTestEdt_ckb_allFRQ");
const actTestEdt_lstFRQ = new CheckedList("actTestsMgmt_lstFRQ");
const actTestEdt_ckbAPX = document.getElementById("actTestEdt_ckb_allAPX");
const actTestEdt_lstAPX = new CheckedList("actTestsMgmt_lstAPX");
const actTestEdt_divQContent = document.getElementById("actTestEdt_divQContent");
const actTestEdt_tglSolution = document.getElementById("actTestEdt_tglSolution");

const actTestEdt_questions = {
   _mcqRecs: [],
   _frqRecs: [],
   _apxRecs: []
};
let actTestEdt_qSelected = undefined;
// #endregion: Action constants

// #region: HTML event registration
actTestEdt_cbTestName.setEventListener("change", actTestEdt_onChangeTest);
actTestEditor_div.addEventListener("keydown", actTestEdt_onKeyDown);
actTestEdt_tglSolution.addEventListener("change", actTestEdt_onToggleSolution); 
actTestEdt_edtFilter.addEventListener("input", actTestEdt_onFilterChange);
actTestEdt_ckbMCQ.addEventListener("change", actTestEdt_onCheckAll);
actTestEdt_lstMCQ.setEventListener("check", actTestEdt_onCheckQuestion);
actTestEdt_lstMCQ.setEventListener("select", actTestEdt_onSelectQuestion);
actTestEdt_ckbFRQ.addEventListener("change", actTestEdt_onCheckAll);
actTestEdt_lstFRQ.setEventListener("check", actTestEdt_onCheckQuestion);
actTestEdt_lstFRQ.setEventListener("select", actTestEdt_onSelectQuestion);
actTestEdt_ckbAPX.addEventListener("change", actTestEdt_onCheckAll);
actTestEdt_lstAPX.setEventListener("check", actTestEdt_onCheckQuestion);
actTestEdt_lstAPX.setEventListener("select", actTestEdt_onSelectQuestion);
// #endregion: HTML event registration

// #region: Exported methods
/**
 * Called from adminPanel, upon creating the action div element (once).
 */
export async function onCreate(sid, username, urlAPI, addLog) {
   refUrlAPI = urlAPI;
   refAddLog = addLog;
}

/**
 * Called from adminPanel each time the action div becomes visible.
 */
export async function onOpen() {
   actTestEdt_edtFilter.value = "";
   actTestEdt_ckbMCQ.checked = false;
   actTestEdt_lstMCQ.clear();
   actTestEdt_ckbFRQ.checked = false;
   actTestEdt_lstFRQ.clear();
   actTestEdt_ckbAPX.checked = false;
   actTestEdt_lstAPX.clear();
   actTestEdt_divQContent.innerHTML = "";
   // get the questions & test set
   requestQueryQSet();
   requestQueryTSet();
}

/**
 * Called from the adminPanel when the "ack" (green) button is clicked on the dialog's title bar.
 */
export async function onApply() {
   const testName = actTestEdt_cbTestName.getValue();
   return requestSetVerTest(testName);
}

/**
 * Called from the adminPanel when the "cancel" (red) button is clicked on the dialog's title bar.
 */
export async function onCancel() {
   refAddLog("actTestEdt_onCancel called");
}
// #endregion: Exported methods

// #region: Helper methods
/**
 * Extracts the list of query records (mapped to the jsonResponse) of a given type
 * from the backend json response.
 */
function loadQSet(qTypes, jsonResponse) {
   const lstTypes = qTypes.split("|");
   let lstQRec = [];
   for (const question of jsonResponse._qList) {
      if (!lstTypes.includes(question._qType)) {
         continue;
      }
      lstQRec.push(question);
   }
   return lstQRec;
}

/**
 * Fills the lists in the action's div with the data from the question records.
 */
function initializeLists() {
   actTestEdt_lstMCQ.clear();
   actTestEdt_questions._mcqRecs.forEach(qRec => { actTestEdt_lstMCQ.addItem(qRec._qName, qRec, "custom-li-1"); })
   actTestEdt_lstFRQ.clear();
   actTestEdt_questions._frqRecs.forEach(qRec => { actTestEdt_lstFRQ.addItem(qRec._qName, qRec, "custom-li-2"); })
   actTestEdt_lstAPX.clear();
   actTestEdt_questions._apxRecs.forEach(qRec => { actTestEdt_lstAPX.addItem(qRec._qName, qRec, "custom-li-3"); })
}
// #endregion: Helper methods

// #region: HTML event handlers
/**
 * Handler called each time the user types a character in the filter input element.
 */
function actTestEdt_onFilterChange(event) {
   actTestEdt_lstFRQ.filter(actTestEdt_edtFilter.value);
   actTestEdt_lstMCQ.filter(actTestEdt_edtFilter.value);
   actTestEdt_lstAPX.filter(actTestEdt_edtFilter.value);
}

/**
 * Handler called when the user checks/unchecks any of the checkbox for the _mcq, _frq and _apx lists.
 */
function actTestEdt_onCheckAll(event) {
   if (event.target === actTestEdt_ckbMCQ) {
      actTestEdt_lstMCQ.check(event.target.checked);
   } else if (event.target === actTestEdt_ckbFRQ) {
      actTestEdt_lstFRQ.check(event.target.checked);
   } else if (event.target === actTestEdt_ckbAPX) {
      actTestEdt_lstAPX.check(event.target.checked);
   }
}

/**
 * Handler called each time a question checkbox is checked/unchecked.
 */
async function actTestEdt_onCheckQuestion(event) {
   let question = event.metadata;
   question.checked = event.checked;
}

/**
 * Handler called each time a question is selected/unselected
 */
async function actTestEdt_onSelectQuestion(event) {
   if (event.target && event.selected) {
      if (event.host === actTestEdt_lstMCQ) {
         actTestEdt_lstFRQ.select(false);
         actTestEdt_lstAPX.select(false);
      } else if (event.host === actTestEdt_lstFRQ) {
         actTestEdt_lstMCQ.select(false);
         actTestEdt_lstAPX.select(false);
      } else if (event.host === actTestEdt_lstAPX) {
         actTestEdt_lstMCQ.select(false);
         actTestEdt_lstFRQ.select(false);
      }
      actTestEdt_qSelected = event.metadata;
      requestQueryDiv(actTestEdt_qSelected._qName, actTestEdt_tglSolution.checked)
   } else {
      actTestEdt_qSelected = undefined;
      actTestEdt_divQContent.innerHTML = "";
   }
}

/**
 * Handler called each time the "Solution" toggle is toggled.
 */
async function actTestEdt_onToggleSolution(event) {
   if (actTestEdt_qSelected) {
      requestQueryDiv(actTestEdt_qSelected._qName, actTestEdt_tglSolution.checked);
   }
}

/**
 * Handler called when the selection in the combo box changes
 */
async function actTestEdt_onChangeTest(event) {
   if (event.target) {
      requestQueryTest(event.target.text);
   }
}

/**
 * Handler called when key left/right is pressed
 */
async function actTestEdt_onKeyDown(event) {
   switch(event.key) {
      case "ArrowLeft":
            event.preventDefault();
            actTestEdt_tglSolution.checked = !actTestEdt_tglSolution.checked;
            actTestEdt_tglSolution.dispatchEvent(new Event('change'));
            break;
      case "ArrowRight":
            event.preventDefault();
            actTestEdt_tglSolution.checked = !actTestEdt_tglSolution.checked;
            actTestEdt_tglSolution.dispatchEvent(new Event('change'));
            break;
   }
}
// #endregion HTML event handlers

// #region: Backend API calls
// #region: ..?cmd=query&op=answer|question&qid=qName
function requestQueryDiv(qName, isAnswer) {
   const urlAPI_query = `${refUrlAPI}?cmd=query&op=${isAnswer ? "answer" : "question"}&qid=${qName}`;
   var request = new  XMLHttpRequest();
   request.open("GET",  urlAPI_query, true);
   request.timeout = 2000;
   request.onload = onResponseQueryDiv;
   request.withCredentials = true;
   request.send();
}

function onResponseQueryDiv() {
   // deserialize Answer.QData response
   var jsonResponse = JSON.parse(this.response);
   const html = (this.status == 200) ? jsonResponse._qDiv : jsonResponse._error;
   actTestEdt_divQContent.innerHTML = html;
}
// #endregion: ..?cmd=query&op=answer|question&qid=qName

// #region: ..?cmd=query&op=qset
function requestQueryQSet() {
   var request = new  XMLHttpRequest();
   request.open("GET", `${refUrlAPI}?cmd=query&op=qset`, true);
   request.timeout = 2000;
   request.onload = onResponseQueryQSet;
   request.withCredentials = true;
   request.send();
}

function onResponseQueryQSet() {
   // deserialize Answer.QList response
   const jsonResponse = JSON.parse(this.response);
   if (this.status == 200) {
      // when successful or user already logged in, redirect to the AdminPanel page
      actTestEdt_questions._mcqRecs = loadQSet('mcq|mcb', jsonResponse);
      actTestEdt_questions._frqRecs = loadQSet('frq', jsonResponse);
      actTestEdt_questions._apxRecs = loadQSet('apx', jsonResponse);
      initializeLists();
   } else {
      // otherwise display the response on the login page.
      refAddLog(`[${this.status}] ${jsonResponse._error}`);
   }
}
// #endregion: ..?cmd=query&op=qset

// #region: ..?cmd=query&op=tset
function requestQueryTSet() {
   var request = new  XMLHttpRequest();
   request.open("GET", `${refUrlAPI}?cmd=query&op=tset`, true);
   request.timeout = 2000;
   request.onload = onResponseQueryTSet;
   request.withCredentials = true;
   request.send();
}

function onResponseQueryTSet() {
   // deserialize Answer.TList response
   const jsonResponse = JSON.parse(this.response);
   if (this.status == 200) {
      const tList = jsonResponse._tList.map((item, index) => ({
         id: `cb${index + 1}`,
         text: item._tName
      })).sort((a, b) => a.text.localeCompare(b.text));

      actTestEdt_cbTestName.setOptions(tList);
   } else {
      refAddLog(`[${this.status}] ${jsonResponse._error}`);

   }
}
// #endregion: ..?cmd=query&op=tset

// #region: ..?cmd=set&op=vtest&name=vtestName&qlist=qName1,qName2,...
function requestSetVerTest(vtestName) {
   let qMCQ_Names = actTestEdt_questions._mcqRecs.filter(qRec => qRec.checked).map(qRec => qRec._qName);
   let qFRQ_Names = actTestEdt_questions._frqRecs.filter(qRec => qRec.checked).map(qRec => qRec._qName);
   let qAPX_Names = actTestEdt_questions._apxRecs.filter(qRec => qRec.checked).map(qRec => qRec._qName);
   let qAll_Names = [...qMCQ_Names, ...qFRQ_Names, ...qAPX_Names].join(",");

   var request = new  XMLHttpRequest();
   request.open("GET", `${refUrlAPI}?cmd=set&op=vtest&name=${vtestName}&qlist=${qAll_Names}`, true);
   request.timeout = 2000;
   request.onload = onResponseSetVerTest;
   request.withCredentials = true;
   request.send();
   return true;
}

function onResponseSetVerTest() {
   // deserialize Answer.Msg response
   const jsonResponse = JSON.parse(this.response);
   if (this.status != 200) {
      refAddLog(`[${this.status}] ${jsonResponse._error}`);
   } else {
      refAddLog(jsonResponse._message);
   }
}
// #endregion: ..?cmd=set&op=vtest&name=vtestName&qlist=qName1,qName2,...

// #region: ..?cmd=query&op=test&tid=tName
function requestQueryTest(tName) {
   const urlAPI_query = `${refUrlAPI}?cmd=query&op=test&tid=${tName}`;
   var request = new  XMLHttpRequest();
   request.open("GET",  urlAPI_query, true);
   request.timeout = 2000;
   request.onload = onResponseQueryTest;
   request.withCredentials = true;
   request.send();
}

function onResponseQueryTest() {
   // deserialize Answer.TData response
   var jsonResponse = JSON.parse(this.response);
   if (this.status == 200) {
      const mcqRecs = jsonResponse._qHeaders.filter(qHeader => (qHeader._qType === "mcq" || qHeader._qType === "mcb")).map(qHeader => qHeader._qName);
      const frqRecs = jsonResponse._qHeaders.filter(qHeader => qHeader._qType === "frq").map(qHeader => qHeader._qName);
      const apxRecs = jsonResponse._qHeaders.filter(qHeader => qHeader._qType === "apx").map(qHeader => qHeader._qName);
      actTestEdt_lstFRQ.checkSet(frqRecs);
      actTestEdt_lstMCQ.checkSet(mcqRecs);
      actTestEdt_lstAPX.checkSet(apxRecs);
      actTestEdt_edtFilter.value="#";
      actTestEdt_lstFRQ.filter(actTestEdt_edtFilter.value);
      actTestEdt_lstMCQ.filter(actTestEdt_edtFilter.value);
      actTestEdt_lstAPX.filter(actTestEdt_edtFilter.value);
   }
}
// #endregion: ..?cmd=query&op=test&tid=tName
// #endregion: Backend API calls
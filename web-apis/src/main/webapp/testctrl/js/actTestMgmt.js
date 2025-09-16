import { CheckedList } from "./ctrlCheckedList.js?ver=1.5";

// #region: External references
let refUrlAPI;
let refAddLog;
// #endregion: External references

// #region: Action constants
const actTestMgmt_edtTestName = document.getElementById("actTestMgmt_edtTestName");
const actTestMgmt_edtFilter = document.getElementById("actTestMgmt_edtFilter");
const actTestMgmt_ckbMCQ = document.getElementById("actTestMgmt_ckb_allMCQ");
const actTestMgmt_lstMCQ = new CheckedList("actTestsMgmt_lstMCQ");
const actTestMgmt_ckbFRQ = document.getElementById("actTestMgmt_ckb_allFRQ");
const actTestMgmt_lstFRQ = new CheckedList("actTestsMgmt_lstFRQ");
const actTestMgmt_ckbAPX = document.getElementById("actTestMgmt_ckb_allAPX");
const actTestMgmt_lstAPX = new CheckedList("actTestsMgmt_lstAPX");
const actTestMgmt_divQContent = document.getElementById("actTestMgmt_divQContent");
const actTestMgmt_tglSolution = document.getElementById("actTestMgmt_tglSolution");

const actTestMgmt_questions = {
   _mcqRecs: [],
   _frqRecs: [],
   _apxRecs: []
};
let actTestMgmt_qSelected = undefined;
// #endregion: Action constants

// #region: HTML event registration
actTestMgmt_tglSolution.addEventListener("change", actTestMgmt_onToggleSolution); 
actTestMgmt_edtFilter.addEventListener("input", actTestMgmt_onFilterChange);
actTestMgmt_ckbMCQ.addEventListener("change", actTestMgmt_onCheckAll);
actTestMgmt_lstMCQ.setEventListener("check", actTestMgmt_onCheckQuestion);
actTestMgmt_lstMCQ.setEventListener("select", actTestMgmt_onSelectQuestion);
actTestMgmt_ckbFRQ.addEventListener("change", actTestMgmt_onCheckAll);
actTestMgmt_lstFRQ.setEventListener("check", actTestMgmt_onCheckQuestion);
actTestMgmt_lstFRQ.setEventListener("select", actTestMgmt_onSelectQuestion);
actTestMgmt_ckbAPX.addEventListener("change", actTestMgmt_onCheckAll);
actTestMgmt_lstAPX.setEventListener("check", actTestMgmt_onCheckQuestion);
actTestMgmt_lstAPX.setEventListener("select", actTestMgmt_onSelectQuestion);
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
   actTestMgmt_edtFilter.value = "";
   actTestMgmt_ckbMCQ.checked = false;
   actTestMgmt_lstMCQ.clear();
   actTestMgmt_ckbFRQ.checked = false;
   actTestMgmt_lstFRQ.clear();
   actTestMgmt_ckbAPX.checked = false;
   actTestMgmt_lstAPX.clear();
   actTestMgmt_divQContent.innerHTML = "";
   // get the questions set
   requestQueryQSet();

}

/**
 * Called from the adminPanel when the "ack" (green) button is clicked on the dialog's title bar.
 */
export async function onApply() {
   if (actTestMgmt_edtTestName.value === "") {
      alert("Please provide test name!");
      return false;
   }
   return requestSetVerTest(actTestMgmt_edtTestName.value);
}

/**
 * Called from the adminPanel when the "cancel" (red) button is clicked on the dialog's title bar.
 */
export async function onCancel() {
   refAddLog("actTestMgmt_onCancel called");
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
   actTestMgmt_lstMCQ.clear();
   actTestMgmt_questions._mcqRecs.forEach(qRec => { actTestMgmt_lstMCQ.addItem(qRec._qName, qRec); })
   actTestMgmt_lstFRQ.clear();
   actTestMgmt_questions._frqRecs.forEach(qRec => { actTestMgmt_lstFRQ.addItem(qRec._qName, qRec); })
   actTestMgmt_lstAPX.clear();
   actTestMgmt_questions._apxRecs.forEach(qRec => { actTestMgmt_lstAPX.addItem(qRec._qName, qRec); })
}
// #endregion: Helper methods

// #region: HTML event handlers
/**
 * Handler called each time the user types a character in the filter input element.
 */
function actTestMgmt_onFilterChange(event) {
   actTestMgmt_lstFRQ.filter(actTestMgmt_edtFilter.value);
   actTestMgmt_lstMCQ.filter(actTestMgmt_edtFilter.value);
   actTestMgmt_lstAPX.filter(actTestMgmt_edtFilter.value);
}

/**
 * Handler called when the user checks/unchecks any of the checkbox for the _mcq, _frq and _apx lists.
 */
function actTestMgmt_onCheckAll(event) {
   if (event.target === actTestMgmt_ckbMCQ) {
      actTestMgmt_lstMCQ.check(event.target.checked);
   } else if (event.target === actTestMgmt_ckbFRQ) {
      actTestMgmt_lstFRQ.check(event.target.checked);
   } else if (event.target === actTestMgmt_ckbAPX) {
      actTestMgmt_lstAPX.check(event.target.checked);
   }
}

/**
 * Handler called each time a question checkbox is checked/unchecked.
 */
async function actTestMgmt_onCheckQuestion(event) {
   let question = event.metadata;
   question.checked = event.checked;
}

/**
 * Handler called each time a question is selected/unselected
 */
async function actTestMgmt_onSelectQuestion(event) {
   if (event.target && event.selected) {
      if (event.host === actTestMgmt_lstMCQ) {
         actTestMgmt_lstFRQ.select(false);
         actTestMgmt_lstAPX.select(false);
      } else if (event.host === actTestMgmt_lstFRQ) {
         actTestMgmt_lstMCQ.select(false);
         actTestMgmt_lstAPX.select(false);
      } else if (event.host === actTestMgmt_lstAPX) {
         actTestMgmt_lstMCQ.select(false);
         actTestMgmt_lstFRQ.select(false);
      }
      actTestMgmt_qSelected = event.metadata;
      requestQueryDiv(actTestMgmt_qSelected._qName, actTestMgmt_tglSolution.checked)
   } else {
      actTestMgmt_qSelected = undefined;
      actTestMgmt_divQContent.innerHTML = "";
   }
}

/**
 * Handler called each time the "Solution" toggle is toggled.
 */
async function actTestMgmt_onToggleSolution(event) {
   if (actTestMgmt_qSelected) {
      requestQueryDiv(actTestMgmt_qSelected._qName, actTestMgmt_tglSolution.checked);
   }
}
// #endregion HTML event handlers

// #region: Backend API calls
// #region: ..?cmd=query&type=qanswer|qtest&qid=qName
function requestQueryDiv(qName, isAnswer) {
   const urlAPI_query = `${refUrlAPI}?cmd=query&type=${isAnswer ? "qanswer" : "qtest"}&qid=${qName}`;
   var request = new  XMLHttpRequest();
   request.open("GET",  urlAPI_query, true);
   request.timeout = 2000;
   request.onload = onResponseQueryDiv;
   request.withCredentials = true;
   request.send();
}

function onResponseQueryDiv() {
   // deserialize Answer.QDiv response
   var jsonResponse = JSON.parse(this.response);
   const html = (this.status == 200) ? jsonResponse._qDiv : jsonResponse._error;
   actTestMgmt_divQContent.innerHTML = html;
}
// #endregion: ..?cmd=query&type=qanswer|qtest&qid=qName

// #region: ..?cmd=query&type=qset
function requestQueryQSet() {
   var request = new  XMLHttpRequest();
   request.open("GET", `${refUrlAPI}?cmd=query&type=qset`, true);
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
      actTestMgmt_questions._mcqRecs = loadQSet('mcq|mcb', jsonResponse);
      actTestMgmt_questions._frqRecs = loadQSet('frq', jsonResponse);
      actTestMgmt_questions._apxRecs = loadQSet('apx', jsonResponse);
      initializeLists();
   } else {
      // otherwise display the response on the login page.
      refAddLog(`[${this.status}] ${jsonResponse._error}`);
   }
}
// #endregion: ..?cmd=query&type=qset

// #region: ..?cmd=set&op=vtest&name=vtestName&args=qName1,qName2,...
function requestSetVerTest(vtestName) {
   let qMCQ_Names = actTestMgmt_questions._mcqRecs.filter(qRec => qRec.checked).map(qRec => qRec._qName);
   let qFRQ_Names = actTestMgmt_questions._frqRecs.filter(qRec => qRec.checked).map(qRec => qRec._qName);
   let qAPX_Names = actTestMgmt_questions._apxRecs.filter(qRec => qRec.checked).map(qRec => qRec._qName);
   let qAll_Names = [...qMCQ_Names, ...qFRQ_Names, ...qAPX_Names].join(",");

   var request = new  XMLHttpRequest();
   request.open("GET", `${refUrlAPI}?cmd=set&op=vtest&name=${vtestName}&args=${qAll_Names}`, true);
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
// #endregion: ..?cmd=set&op=vtest&name=vtestName&args=qName1,qName2,...

// #endregion: Backend API calls
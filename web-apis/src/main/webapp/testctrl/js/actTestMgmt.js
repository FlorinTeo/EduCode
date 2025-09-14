import { CheckedList } from "./ctrlCheckedList.js?ver=1.5";

// #region: page referenced parameters
let refUrlAPI;
let refAddLog;
// #endregion page referenced parameters

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

// #region: exported methods
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
   var request = new  XMLHttpRequest();
   request.open("GET", `${refUrlAPI}?cmd=query&type=qset`, true);
   request.timeout = 2000;
   request.onload = onQueryQSetResponse;
   request.withCredentials = true;
   request.send();
}

/**
 * Called from the adminPanel when the "ack" (green) button is clicked on the dialog's title bar.
 */
export async function onApply() {
   if (actTestMgmt_edtTestName.value === "") {
      alert("Please provide test name!");
      return false;
   }
   let qMCQs = actTestMgmt_questions._mcqRecs.filter(qRec => qRec.checked)
   let qFRQs = actTestMgmt_questions._frqRecs.filter(qRec => qRec.checked)
   let qAPXs = actTestMgmt_questions._apxRecs.filter(qRec => qRec.checked)
   refAddLog(`actTestMgmt_onApply: mcq:${qMCQs.length}, frq:${qFRQs.length}, apx:${qAPXs.length}`);
   return true;
}

/**
 * Called from the adminPanel when the "cancel" (red) button is clicked on the dialog's title bar.
 */
export async function onCancel() {
   refAddLog("actTestMgmt_onCancel called");
}
// #endregion: exported methods


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
 * Backend response handler for `${refUrlAPI}?cmd=query&type=qset`, loading the 
 * list of all available questions (mcq, mcb, frq, apx) into the global container 
 * of the three lists: _mcqRecs, _frqRecs, _apxRecs. Called from onOpen().
 */
function onQueryQSetResponse() {
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
      actTestMgmt_divQuerySend(actTestMgmt_qSelected._qName, actTestMgmt_tglSolution.checked)
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
      actTestMgmt_divQuerySend(actTestMgmt_qSelected._qName, actTestMgmt_tglSolution.checked);
   }
}

/**
 * Backend API request for fetching the content div for a specific query.
 */
function actTestMgmt_divQuerySend(qName, isAnswer) {
   const urlAPI_query = `${refUrlAPI}?cmd=query&type=${isAnswer ? "qanswer" : "qtest"}&qid=${qName}`;
   var request = new  XMLHttpRequest();
   request.open("GET",  urlAPI_query, true);
   request.timeout = 2000;
   request.onload = actTestMgmt_onDivQueryResponse;
   request.withCredentials = true;
   request.send();
}

/**
 * Backend response handler for `${refUrlAPI}?cmd=query&type=qanswer|qtest`. This is returning
 * the <div> element containing the query content - either the test or the answer versions.
 */
function actTestMgmt_onDivQueryResponse() {
   var jsonResponse = JSON.parse(this.response);
   const html = (this.status == 200) ? jsonResponse._qDiv : jsonResponse._error;
   actTestMgmt_divQContent.innerHTML = html;
}

import { CtrlComboBox } from "./ctrlComboBox.js?ver=2.0";
import { CheckedList } from "./ctrlCheckedList.js?ver=2.4";

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
const actTestEdt_divQContent = document.getElementById("actTestEdt_divQContent");
const actTestEdt_tglSolution = document.getElementById("actTestEdt_tglSolution");

const actTestEdt_questions = [];
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
      
      // attach a "filterMatch" lambda function to the question, to be used by the ctrlCheckedList filter() method
      question.filterMatch = function(pattern) {
         if (pattern.startsWith("#")) {
            return question._qType.startsWith(pattern.slice(1));
         } else {
            return (question._qName.startsWith(pattern));
         }
      };

      lstQRec.push(question);
   }
   return lstQRec;
}

/**
 * Fills the lists in the action's div with the data from the question records.
 */
function initializeLists() {
   actTestEdt_lstMCQ.clear();
   actTestEdt_questions.forEach(qRec => {
      const liStyle = (qRec._qType === "apx") ? "custom-li-3" : (qRec._qType === "frq") ? "custom-li-2" : "custom-li-1";
      actTestEdt_lstMCQ.addItem(qRec._qName, qRec, liStyle);
   });
}
// #endregion: Helper methods

// #region: HTML event handlers
/**
 * Handler called each time the user types a character in the filter input element.
 */
function actTestEdt_onFilterChange(event) {
   actTestEdt_lstMCQ.filter(actTestEdt_edtFilter.value);
}

/**
 * Handler called when the user checks/unchecks any of the checkbox for the _mcq, _frq and _apx lists.
 */
function actTestEdt_onCheckAll(event) {
   if (event.target === actTestEdt_ckbMCQ) {
      actTestEdt_lstMCQ.check(event.target.checked);
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
      actTestEdt_qSelected = event.metadata;
      requestQueryDiv(actTestEdt_qSelected._qName, actTestEdt_tglSolution.checked);
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
      const mcq = loadQSet('mcq|mcb', jsonResponse);
      const frq = loadQSet('frq', jsonResponse);
      const apx = loadQSet('apx', jsonResponse);

      actTestEdt_questions.length = 0; // clear in-place (safe for const arrays)
      actTestEdt_questions.push(...mcq, ...frq, ...apx);
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
   let q_Names = actTestEdt_questions.filter(qRec => qRec.checked).map(qRec => qRec._qName);
   let qAll_Names = [...q_Names].join(",");

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
      const qRecs = jsonResponse._qHeaders.map(qHeader => qHeader._qName);
      actTestEdt_lstMCQ.checkSet(qRecs);
      actTestEdt_edtFilter.value="#";
      actTestEdt_lstMCQ.filter(actTestEdt_edtFilter.value);
   }
}
// #endregion: ..?cmd=query&op=test&tid=tName
// #endregion: Backend API calls
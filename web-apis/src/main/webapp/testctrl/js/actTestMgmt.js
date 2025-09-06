import { CheckedList } from "./ctrlCheckedList.js?ver=1.1";

// #region: page referenced parameters
let refUrlAPI;
let refAddLog;
// #endregion page referenced parameters

//const actTestMgmt_edtTestName = document.getElementById("actTestMgmt_edtTestName");
const actTestMgmt_edtFilter = document.getElementById("actTestMgmt_edtFilter");
// const actTestMgmt_dlstTests = document.getElementById("actTestMgmt_dlstTests");
// const actTestMgmt_dlstFilters = document.getElementById("actTestMgmt_dlstFilters");
const actTestMgmt_ckbMCQ = document.getElementById("actTestMgmt_ckb_allMCQ");
const actTestMgmt_lstMCQ = new CheckedList("actTestsMgmt_lstMCQ");
const actTestMgmt_ckbFRQ = document.getElementById("actTestMgmt_ckb_allFRQ");
const actTestMgmt_lstFRQ = new CheckedList("actTestsMgmt_lstFRQ");
const actTestMgmt_ckbAPX = document.getElementById("actTestMgmt_ckb_allAPX");
const actTestMgmt_lstAPX = new CheckedList("actTestsMgmt_lstAPX");
const actTestMgmt_divQContent = document.getElementById("actTestMgmt_divQContent");

var actTestMgmt_questions = {
   _mcqRecs: [],
   _frqRecs: [],
   _apxRecs: []
};

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
export async function onCreate(sid, username, urlAPI, addLog) {
   refUrlAPI = urlAPI;
   refAddLog = addLog;
}

export async function onOpen() {
   actTestMgmt_edtFilter.value = "";
   actTestMgmt_ckbMCQ.checked = false;
   actTestMgmt_lstMCQ.clear();
   actTestMgmt_ckbFRQ.checked = false;
   actTestMgmt_lstFRQ.clear();
   actTestMgmt_ckbAPX.checked = false;
   actTestMgmt_lstAPX.clear();
   // get the questions set
   var request = new  XMLHttpRequest();
   request.open("GET", `${refUrlAPI}?cmd=query&type=qset`, true);
   request.timeout = 2000;
   request.onload = onQueryQSetResponse;
   request.withCredentials = true;
   request.send();
}

export async function onApply() {
   let qMCQs = actTestMgmt_questions._mcqRecs.filter(qRec => qRec.checked)
   let qFRQs = actTestMgmt_questions._frqRecs.filter(qRec => qRec.checked)
   let qAPXs = actTestMgmt_questions._apxRecs.filter(qRec => qRec.checked)
   refAddLog(`actTestMgmt_onApply: mcq:${qMCQs.length}, frq:${qFRQs.length}, apx:${qAPXs.length}`);
   return true;
}

export async function onCancel() {
   refAddLog("actTestMgmt_onCancel called");
}
// #endregion: exported methods

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

function initializeLists() {
   actTestMgmt_lstMCQ.clear();
   actTestMgmt_questions._mcqRecs.forEach(qRec => { actTestMgmt_lstMCQ.addItem(qRec._qName, qRec); })
   actTestMgmt_lstFRQ.clear();
   actTestMgmt_questions._frqRecs.forEach(qRec => { actTestMgmt_lstFRQ.addItem(qRec._qName, qRec); })
   actTestMgmt_lstAPX.clear();
   actTestMgmt_questions._apxRecs.forEach(qRec => { actTestMgmt_lstAPX.addItem(qRec._qName, qRec); })
}

function actTestMgmt_onFilterChange(event) {
   actTestMgmt_lstFRQ.filter(actTestMgmt_edtFilter.value);
   actTestMgmt_lstMCQ.filter(actTestMgmt_edtFilter.value);
   actTestMgmt_lstAPX.filter(actTestMgmt_edtFilter.value);
}

function actTestMgmt_onCheckAll(event) {
   if (event.target === actTestMgmt_ckbMCQ) {
      actTestMgmt_lstMCQ.check(event.target.checked);
   } else if (event.target === actTestMgmt_ckbFRQ) {
      actTestMgmt_lstFRQ.check(event.target.checked);
   } else if (event.target === actTestMgmt_ckbAPX) {
      actTestMgmt_lstAPX.check(event.target.checked);
   }
}

async function actTestMgmt_onCheckQuestion(event) {
   let question = event.metadata;
   question.checked = event.checked;
}

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

      // TODO: display question content in actTestMgmt_divQContent
      const res = await fetch(`div-mcqTest.jsp`);
      const html = await res.text();
      actTestMgmt_divQContent.innerHTML = html;
   }
}

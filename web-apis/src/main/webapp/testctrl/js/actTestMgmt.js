const actTestMgmt_edtTestName = document.getElementById("actTestMgmt_edtTestName");
const actTestMgmt_edtFilter = document.getElementById("actTestMgmt_edtFilter");
const actTestMgmt_dlstTests = document.getElementById("actTestMgmt_dlstTests");
const actTestMgmt_dlstFilters = document.getElementById("actTestMgmt_dlstFilters");
const actTestMgmt_lstMCQ = document.getElementById("actTestsMgmt_lstMCQ");
const actTestMgmt_lstFRQ = document.getElementById("actTestsMgmt_lstFRQ");
const actTestMgmt_lstAPX = document.getElementById("actTestsMgmt_lstAPX");
const actTestMgmt_ckb_allMCQ = document.getElementById("actTestMgmt_ckb_allMCQ");
const actTestMgmt_ckb_allFRQ = document.getElementById("actTestMgmt_ckb_allFRQ");
const actTestMgmt_ckb_allAPX = document.getElementById("actTestMgmt_ckb_allAPX");

var actTestMgmt_questions = {
   _mcqRecs: [],
   _frqRecs: [],
   _apxRecs: []
};

actTestMgmt_edtFilter.addEventListener("input", actTestMgmt_onFilterChange);
actTestMgmt_ckb_allMCQ.addEventListener("change", function(event) { actTestMgmt_onCheckAll(event, actTestMgmt_questions._mcqRecs, actTestMgmt_lstMCQ); });
actTestMgmt_ckb_allFRQ.addEventListener("change", function(event) { actTestMgmt_onCheckAll(event, actTestMgmt_questions._frqRecs, actTestMgmt_lstFRQ); });
actTestMgmt_ckb_allAPX.addEventListener("change", function(event) { actTestMgmt_onCheckAll(event, actTestMgmt_questions._apxRecs, actTestMgmt_lstAPX); });

function actTestMgmt_onOpen() {
   actTestMgmt_lstMCQ.innerHTML = "";
   actTestMgmt_lstFRQ.innerHTML = "";
   actTestMgmt_lstAPX.innerHTML = "";
   actTestMgmt_edtFilter.value = "";
   // get the questions set
   var request = new  XMLHttpRequest();
   request.open("GET", `${urlAPI}?cmd=query&type=qset`, true);
   request.timeout = 2000;
   request.onload = onQueryQSetResponse;
   request.withCredentials = true;
   request.send();
}

function onQueryQSetResponse() {
   jsonResponse = JSON.parse(this.response);
   if (this.status == 200) {
      // when successful or user already logged in, redirect to the CtrlPanel page
      actTestMgmt_questions._mcqRecs = loadQSet('mcq|mcb', jsonResponse);
      actTestMgmt_questions._frqRecs = loadQSet('frq', jsonResponse);
      actTestMgmt_questions._apxRecs = loadQSet('apx', jsonResponse);
      initializeLists();
   } else {
      // otherwise display the response on the login page.
      txtOutput.innerHTML = `[${this.status}] ${jsonResponse._error}`;
      txtOutput.classList.add('err-div');
   }
}

function loadQSet(qTypes, jsonResponse) {
   lstTypes = qTypes.split("|");
   let lstQRec = [];
   for (const question of jsonResponse._qRecs) {
      if (!lstTypes.includes(question._qType)) {
         continue;
      }
      question.filtered = false;
      question.checked = false;
      lstQRec.push(question);
   }
   return lstQRec;
}

function initializeList(lstQRec, listElem) {
   listElem.innerHTML = "";
   for (const question of lstQRec) {
      question.filtered = !question._qName.startsWith(actTestMgmt_edtFilter.value);
      if (question.filtered) {
         continue;
      }
      const li = document.createElement("li");
      li.innerHTML = `<input type="checkbox" ${question.checked ? "checked" : ""}><label>${question._qName}</label>`;
      const checkbox = li.querySelector("input[type='checkbox']");
      checkbox.addEventListener("change", function(event) {
         actTestMgmt_onCheckQuestion(event, question);
      });
      listElem.appendChild(li);
   }
}

function initializeLists() {
   initializeList(actTestMgmt_questions._mcqRecs, actTestMgmt_lstMCQ);
   initializeList(actTestMgmt_questions._frqRecs, actTestMgmt_lstFRQ);
   initializeList(actTestMgmt_questions._apxRecs, actTestMgmt_lstAPX);
}

function actTestMgmt_onFilterChange(event) {
   initializeLists();
}

function actTestMgmt_onCheckAll(event, lstQRec, listElem) {
   lstQRec.filter(q => !q.filtered).forEach(q => { q.checked = event.target.checked; });
   initializeLists(lstQRec, listElem);
}

function actTestMgmt_onCheckQuestion(event, question) {
   question.checked = event.target.checked;
}

function actTestMgmt_onApply() {
   addLog("actTestMgmt_onApply called");
   return true;
}

function actTestMgmt_onCancel() {
   addLog("actTestMgmt_onCancel called");
}

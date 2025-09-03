export class CheckedList {
    #liList
    #ulElem
    #fnHandlers

    // #region: [Private] helper methods
    #reset() {
        this.#ulElem.innerHTML = "";
        this.#liList
            .forEach(li => {
                if (li.filtered) {
                    li.classList.remove("selected-li");
                } else {
                    li.innerHTML =  `<input type="checkbox" ${li.checked ? "checked" : ""}><label>${li.label.textContent}</label>`;
                }
            });
    }

    #callHandler(eventName, event) {
        if (this.#fnHandlers.has(eventName)) {
            this.#fnHandlers[eventName](event)
        }
    }
    // #endregion: [Private] helper methods

    constructor(htmlId) {
        this.#ulElem = document.getElementById(htmlId);
        this.#ulElem.innerHTML = "";
        this.#liList = [];
        this.#fnHandlers = {};
    }

    setEventListener(eventName, fnHandler) {
        this.#fnHandlers[eventName] = fnHandler;
    }

    filter(filterText) {
        this.#liList.forEach(li => {
            li.filtered = !li.checkbox.textContent.startsWith(actTestMgmt_edtFilter.value);
        });
        this.#reset();
    }

    addItem(liText) {
        const li = document.createElement("li");
        li.innerHTML = `<input type="checkbox"><label>${liText}</label>`;
        li.checkbox = li.querySelector("input[type='checkbox']");
        li.label = li.querySelector("label");
        li.hidden = false;
        li.selected = false;
        li.checked = false;
        checkbox.addEventListener("change", function(event) {
            event.target = li;
            event.target.checked = event.target.checkbox.checked;
            this.#callHandler("change", event);
        });
        label.addEventListener("click", function(event) {
            event.target = li;
            event.target.selected = !event.target.selected;
            event.target.classList.toggle("selected-li");
            this.#callHandler("click", event);
        });
        this.#liList.push(li);
        this.#ulElem.appendChild(li);
    }
}

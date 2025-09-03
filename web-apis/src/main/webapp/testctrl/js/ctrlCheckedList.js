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
                    this.#ulElem.appendChild(li);
                }
            });
    }

    #callHandler(eventName, event) {
        if (eventName in this.#fnHandlers) {
            this.#fnHandlers[eventName](event);
        }
    }
    // #endregion: [Private] helper methods

    constructor(htmlId) {
        this.#ulElem = document.getElementById(htmlId);
        this.#fnHandlers = {};
        this.clear();
    }

    clear() {
        this.#ulElem.innerHTML = "";
        this.#liList = [];
    }

    setEventListener(eventName, fnHandler) {
        this.#fnHandlers[eventName] = fnHandler;
    }

    filter(pattern) {
        this.#liList.forEach(li => {
            li.filtered = !li.label.textContent.startsWith(pattern);
        });
        this.#reset();
    }

    check(state) {
        this.#liList.filter(li => !li.filtered).forEach(li => {
            li.checkbox.checked = state;
            this.#callHandler("check", {host: this, innerTarget: li});
        });
    }

    addItem(liText, metadata) {
        const li = document.createElement("li");
        li.innerHTML = `<input type="checkbox"><label>${liText}</label>`;
        li.checkbox = li.querySelector("input[type='checkbox']");
        li.label = li.querySelector("label");
        li.filtered = false;
        li.selected = false;
        li.checked = false;
        li.metadata = metadata;

        const checkedListInstance = this;
        li.checkbox.addEventListener("change", function(event) {
            event.host = this;
            event.innerTarget = li;
            checkedListInstance.#callHandler("check", event);
        });
        li.label.addEventListener("click", function(event) {
            event.host = this;
            event.innerTarget = li;
            li.selected = !li.selected;
            li.classList.toggle("selected-li");
            checkedListInstance.#callHandler("select", event);
        });
        this.#liList.push(li);
        this.#ulElem.appendChild(li);
    }
}

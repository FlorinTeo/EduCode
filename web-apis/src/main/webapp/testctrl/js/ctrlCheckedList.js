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
            this.#callHandler("check", {host: this, target: undefined, metadata: li.metadata, checked: state});
        });
    }

    select(state) {
        this.#liList.filter(li => li.selected != state).forEach(li => {
            li.selected = state;
            if (li.selected) {
                li.classList.add("selected-li");
            } else {
                li.classList.remove("selected-li");
            }
            this.#callHandler("select", {host: this, target: undefined, metadata: li.metadata, selected: state});
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
            event.host = checkedListInstance;
            event.metadata = li.metadata;
            event.checked = li.checkbox.checked;
            checkedListInstance.#callHandler("check", event);
        });
        li.label.addEventListener("click", function(event) {
            checkedListInstance.#liList.filter(pli => pli.selected && pli !== li).forEach(pli => {
                pli.selected = false;
                pli.classList.remove("selected-li");
                checkedListInstance.#callHandler("select", {host: checkedListInstance, target: pli.label, metadata: pli.metadata, selected: false});
            });
            li.selected = !li.selected;
            if (li.selected) {
                li.classList.add("selected-li");
            } else {
                li.classList.remove("selected-li");
            }
            event.host = checkedListInstance;
            event.metadata = li.metadata;
            event.selected = li.selected;
            checkedListInstance.#callHandler("select", event);
        });
        this.#liList.push(li);
        this.#ulElem.appendChild(li);
    }
}

export class CheckedList {
    #liList
    #ulElem
    #fnHandlers

    // #region: [Private] helper methods
    #reset() {
        this.#ulElem.innerHTML = "";
        this.#liList
            .forEach(li => {
                if (!li.filtered) {
                    if (li.selected) {
                        li.selected = false;
                        li.classList.remove("selected-li");
                        this.#callHandler("select", {host: this, target: undefined, metadata: li.metadata, selected: li.selected})
                    }
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
        this.#ulElem.tabIndex = 0;
        this.#ulElem.addEventListener("keydown", this.onKeyDownEvent);
        this.#ulElem.host = this;
        this.#fnHandlers = {};
        this.clear();
    }

    async onKeyDownEvent(event) {
        event.preventDefault();
        let dir = undefined;
        switch(event.key) {
            case "ArrowUp":
                dir = -1;
                break;
            case "ArrowDown":
                dir = 1;
                break;
        }
        if (dir) {
            this.host.reSelect(dir);
        }
    }

    clear() {
        this.#ulElem.innerHTML = "";
        this.#liList = [];
    }

    setEventListener(eventName, fnHandler) {
        this.#fnHandlers[eventName] = fnHandler;
    }

    /**
     * Filters out the list items if they do not match the given pattern.
     * The pattern gives either a matching prefix or a special value indicating
     * custom filtering (i.e. filterout elements which were not checked)
     */
    filter(pattern) {
        this.#liList.forEach(li => {
            if (pattern === "#") {
                li.filtered = li.checkbox.checked;
            } else {
                li.filtered = li.label.textContent.startsWith(pattern);
            }
        });
        this.#reset();
    }

    check(state) {
        this.#liList.filter(li => li.filtered).forEach(li => {
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

    reSelect(dir) {
        const iCrtSel = this.#liList.findIndex(li => li.selected);
        if (iCrtSel < 0) {
            // no current selection => no move
            return false;
        }

        var iNewSel = iCrtSel + dir;
        while(iNewSel >= 0 && iNewSel < this.#liList.length) {
            if (this.#liList[iNewSel].filtered) {
                break;
            }
            iNewSel += dir;
        } 

        if (iNewSel < 0 || iNewSel == this.#liList.length) {
            // selection at the boundary of the visible items => no move
            return false;
        }

        this.#liList[iCrtSel].selected = false;
        this.#liList[iCrtSel].classList.remove("selected-li");
        this.#callHandler("select", {host: this, target: this.#liList[iCrtSel].label, metadata: this.#liList[iCrtSel].metadata, selected: false});
        this.#liList[iNewSel].selected = true;
        this.#liList[iNewSel].classList.add("selected-li");
        this.#liList[iNewSel].scrollIntoView({ block: "nearest" });
        this.#callHandler("select", {host: this, target: this.#liList[iNewSel].label, metadata: this.#liList[iNewSel].metadata, selected: true});
    }

    addItem(liText, metadata) {
        const li = document.createElement("li");
        li.innerHTML = `<input type="checkbox"><label>${liText}</label>`;
        li.checkbox = li.querySelector("input[type='checkbox']");
        li.label = li.querySelector("label");
        li.filtered = true;
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
        li.checkbox.addEventListener("click", function(event) {
            event.stopPropagation();
        });
        li.addEventListener("click", function(event) {
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

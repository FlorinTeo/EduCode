export class CtrlComboBox {
    #cbElemId
    #cbElem
    #fnHandlers

    constructor(cbElemId) {
        this.#cbElemId = cbElemId;
        this.#fnHandlers = {}; // Initialize the handlers object
    }

    setOptions(options) {
        if (!this.#cbElem) {
            this.#cbElem = $("#"+this.#cbElemId);
            this.#cbElem.on('select2:select select2:unselect select2:clear', (e) => this.#onChange(e));
        } else {
            this.#cbElem.select2('destroy'); // Destroy previous instance
        }
        // Add an empty option as the first item to ensure proper clearing behavior
        const optionsWithEmpty = [{ id: '', text: '', disabled: true }, ...options];
        this.#cbElem.select2({
            tags: true,
            data: optionsWithEmpty,
            allowClear: true,
            placeholder: "Select or type...",
            dropdownParent: $('#dlgAction')
        });
        // Clear the selection to show placeholder instead of first option
        this.#cbElem.val(null).trigger('change');
    }

    setEventListener(eventName, fnHandler) {
        this.#fnHandlers[eventName] = fnHandler;
    }

    getValue() {
        return this.#cbElem ? this.#cbElem.select2('data').map(item => item.text) : undefined;
    }

    setValue(value) {
        if (this.#cbElem) {
            this.#cbElem.val(value).trigger('change');
        }
    }

    clear() {
        if (this.#cbElem) {
            this.#cbElem.val(null).trigger('change');
        }
    }

    #callHandler(eventName, event) {
        if (eventName in this.#fnHandlers) {
            this.#fnHandlers[eventName](event);
        }
    }

    #onChange(event) {
        // Get the item that was selected or unselected
        const triggeredItem = event.params.data;
        switch(event.type) {
            case "select2:select":
                this.#callHandler("changed", { host: this, target: triggeredItem });
                break;
            case "select2:unselect":
                this.#callHandler("changed", { host: this, target: undefined });
                break;
            case "select2:clear":
                setTimeout(() => { this.#cbElem.val(null).trigger('change'); }, 0);
                break;
        }
    }
}

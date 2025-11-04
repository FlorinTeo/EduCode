export class CtrlComboBox {
    #cbElemId
    #cbElem
    #fnHandlers
    #allOptions

    constructor(cbElemId) {
        this.#cbElemId = cbElemId;
        this.#fnHandlers = {}; // Initialize the handlers object
        this.#allOptions = [];
    }

    
    getOptions() {
        // Return the last options passed to setOptions (excluding the injected empty placeholder)
        return this.#allOptions.slice();
    }

    setOptions(options) {
        if (!this.#cbElem) {
            this.#cbElem = $("#"+this.#cbElemId);
            this.#cbElem.on('select2:select select2:unselect select2:clear', (e) => this.#onChange(e));
        } else {
            this.#cbElem.select2('destroy'); // Destroy previous instance
        }
        
        // Clear any existing options from the original select element
        this.#cbElem.empty();
        
        // Add an empty option as the first item to ensure proper clearing behavior
        const optionsWithEmpty = [{ id: '', text: '', disabled: true }, ...options];
        // Keep a copy (without the injected empty placeholder) for external queries
        this.#allOptions = options.map(o => ({ id: o.id, text: o.text, disabled: !!o.disabled }));
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

    hasOption(value, caseInsensitive = false) {
        if (value === undefined || value === null) return false;
        const v = String(value).trim();
        if (!v) return false;
        if (caseInsensitive) {
            const lv = v.toLowerCase();
            return this.#allOptions.some(o => String(o.text).toLowerCase() === lv);
        }
        return this.#allOptions.some(o => o.text === v);
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
                this.#callHandler("change", { host: this, target: triggeredItem });
                break;
            case "select2:unselect":
                this.#callHandler("change", { host: this, target: undefined });
                break;
            case "select2:clear":
                setTimeout(() => { this.#cbElem.val(null).trigger('change'); }, 0);
                break;
        }
    }
}

export class CtrlComboBox {
    #select
    #fnHandlers

    constructor(selector) {
        this.#fnHandlers = {}; // Initialize the handlers object
        this.#select = $("#"+selector);
        this.#select.select2({ 
            tags: true, 
            data: [],
            allowClear: true,
            placeholder: "Select or type...",
            dropdownParent: $('#dlgAction')            
        });
        this.#select.on('select2:select select2:unselect select2:clear', (e) => this.#onChange(e));
    }

    setOptions(options) {
        this.#select.select2('destroy'); // Destroy previous instance
        // Add an empty option as the first item to ensure proper clearing behavior
        const optionsWithEmpty = [{ id: '', text: '', disabled: true }, ...options];
        this.#select.select2({
            tags: true,
            data: optionsWithEmpty,
            allowClear: true,
            placeholder: "Select or type...",
            dropdownParent: $('#dlgAction')
        });
        // Clear the selection to show placeholder instead of first option
        this.#select.val(null).trigger('change');
    }

    setEventListener(eventName, fnHandler) {
        this.#fnHandlers[eventName] = fnHandler;
    }

    getValue() {
        return this.#select.select2('data').map(item => item.text);
    }

    setValue(value) {
        this.#select.val(value).trigger('change');
    }

    clear() {
        this.#select.val(null).trigger('change');
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
                setTimeout(() => { this.#select.val(null).trigger('change'); }, 0);
                break;
        }
    }
}

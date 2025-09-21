
// ctrlComboBox.js
export class CtrlComboBox {
    constructor(selector, options = []) {
        this.$select = $(selector);
        this.$select.select2({
            tags: true,
            dropdownParent: $('body')
        });
    }

    setOptions(options) {
        this.$select.select2('destroy'); // Destroy previous instance
        this.$select.empty();
        options.forEach(opt => {
            this.$select.append($('<option>', { value: opt.value, text: opt.text }));
        });
        this.$select.select2({
            tags: true,
            dropdownParent: $('body')
        });
        this.$select.trigger('change.select2'); // Notify Select2 of changes
    }

    getValue() {
        return this.$select.val();
    }
    setValue(val) {
        this.$select.val(val).trigger('change');
    }
}

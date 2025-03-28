export class ContextMenu {

   #hCtxMenu;    // html element for this context menu <div>
   #menuEntries; // Map of {html id, {hP:paragraph, hLabel:label, hInput:input, fOnClick:lambda}}
   #fOnClose;    // Lambda to be called when menu is closed

   isShown;  // true is menu is shown, false otherwise

    // #region - setup
    constructor(ctxMenuId) {
        this.#hCtxMenu = document.getElementById(ctxMenuId);
        this.#menuEntries = new Map();
        this.loadMenuEntries();
        this.setupListeners();
        this.isShown = false;
    }

    loadMenuEntries() {
        const hAllElements = this.#hCtxMenu.getElementsByTagName('*');
        for (const hElement of hAllElements) {
            if (hElement.id && hElement.tagName === 'P') {
                this.#menuEntries.set(
                    hElement.id,
                    {
                        hP: hElement,
                        hLabel: null,
                        hInput: null,
                        hImg: null,
                        fOnClick: null
                    });
            } else {
                let lastMenuKVP = [...this.#menuEntries].pop();
                if (hElement.tagName === 'LABEL') {
                    lastMenuKVP[1].hLabel = hElement;
                } else if (hElement.tagName === 'INPUT') {
                    lastMenuKVP[1].hInput = hElement;
                    hElement.setAttribute('autocomplete', 'off');
                } else if (hElement.tagName === 'IMG') {
                    lastMenuKVP[1].hImg = hElement;
                }
            }
        }
    }

    setupListeners() {
        this.#hCtxMenu.addEventListener('contextmenu', (event) => { event.preventDefault(); });
        for(const menuEntry of this.#menuEntries.values()) {
            if (menuEntry.hInput != null) {
                if (menuEntry.hInput.type === 'text') {
                    menuEntry.hInput.addEventListener('mouseenter', (event) => {
                        menuEntry.hInput.select();
                    });
                    menuEntry.hInput.addEventListener('mouseleave', (event) => {
                        menuEntry.hInput.select();
                    });
                    menuEntry.hInput.addEventListener('keydown', (event) => {
                        if (event.key === 'Enter') {
                            this.onClick(event, menuEntry.hP.id);
                        } else if (event.key === 'Escape') {
                            this.onClose(event);
                        }
                    });
                } else {
                    menuEntry.hInput.addEventListener('click', (event) => {
                        this.onClick(event, menuEntry.hP.id);
                    });
                }
                menuEntry.hLabel.addEventListener('click', (event) => {
                    this.onClick(event, menuEntry.hP.id);
                });
            } else if (menuEntry.hImg != null) {
                menuEntry.hP.addEventListener('click', (event) => {
                    this.onClick(event, menuEntry.hP.id);
                });
                menuEntry.hImg.addEventListener('click', (event) => {
                    this.onClick(event, menuEntry.hP.id);
                });
            } else {
                menuEntry.hP.addEventListener('click', (event) => { this.onClick(event); });
            }
        }
    }
    // #endregion - setup

    // #region - internal event handlers
    onClose(event) {
        if (this.#fOnClose && this.#fOnClose != null) {
            this.#fOnClose(this.#hCtxMenu);
            this.#fOnClose = null;
        }
        this.hide();
    }

    onClick(event, hint) {
        // locate the menu being clicked and process the click
        let menuEntry = this.#menuEntries.get(hint ? hint : event.target.id);
        if (menuEntry.fOnClick && menuEntry.fOnClick != null) {
            menuEntry.fOnClick(menuEntry.hP, menuEntry.hInput != null ? menuEntry.hInput.value : null);
        }
        this.onClose(event);
    }
    // #endregion - internal event handlers

    addContextMenuListener(hCtxMenuEntryId, fOnClick) {
        if (this.#menuEntries.has(hCtxMenuEntryId)) {
            this.#menuEntries.get(hCtxMenuEntryId).fOnClick = fOnClick;
        }
    }

    show(x, y, fOnClose) {
        this.#hCtxMenu.style.left = x;
        this.#hCtxMenu.style.top = y;
        this.#hCtxMenu.style.display = 'block';
        this.#hCtxMenu.style.zIndex = "1";
        this.#fOnClose = fOnClose;
        this.isShown = true;
    }

    hide() {
        this.#hCtxMenu.style.display = 'none';
        this.isShown = false;
    }

    getInput(name) {
        if (this.#menuEntries.has(name)) {
            let inputElement = this.#menuEntries.get(name).hInput;
            if (inputElement.type === "text") {
                return inputElement.value;
            } else if (inputElement.type === "checkbox") {
                return inputElement.checked;
            }
        }
    }

    setInput(name, value) {
        if (this.#menuEntries.has(name)) {
            let inputElement = this.#menuEntries.get(name).hInput;
            if (inputElement.type === "text") {
                inputElement.value = value;
            } else if (inputElement.type === "checkbox") {
                inputElement.checked = value;
            }
        }
    }

    setVisible(visibilityMap) {
        for(const [hCtxMenuEntryId, isVisible] of visibilityMap) {
            let menuEntry = this.#menuEntries.get(hCtxMenuEntryId);
            if (menuEntry) {
                menuEntry.hP.style.display = isVisible ? 'block' : 'none';
            }
        }
    }
 }
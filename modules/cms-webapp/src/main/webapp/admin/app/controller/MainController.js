Ext.define('CMS.controller.MainController', {
    extend: 'Ext.app.Controller',

    views: ['main.Toolbar'],

    refs: [
        {ref: 'selectedAppLabel', selector: 'mainToolbar label[id=cms-selected-application-label]'},
        {ref: 'startMenuButton', selector: 'mainToolbar button[id=cms-start-button]'},
    ],

    init: function() {
        this.control({
            '*[id=cms-start-button] menu > menuitem': {
                click: this.loadApplication
            }
        });
    },

    loadApplication: function(item, e, options ) {
        if (item.appUrl === undefined) return; // for now.

        this.getIframe().src = item.appUrl;
        this.setApplicationLabelText(item.text);
        this.setUrlFragment(item.text);
    },

    setApplicationLabelText: function(text) {
        this.getSelectedAppLabel().setText(text);
    },

    setUrlFragment: function(fragmentId) {
        window.location.hash = fragmentId;
    },

    getIframe: function() {
        return Ext.getDom('cms-main-iframe');
    }

});

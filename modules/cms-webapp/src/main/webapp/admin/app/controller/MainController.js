Ext.define('CMS.controller.MainController', {
    extend: 'Ext.app.Controller',

    views: ['main.Toolbar'],

    refs: [
        {ref: 'selectedAppLabel', selector: 'mainToolbar label[id=cms-selected-application-label]'},
    ],

    init: function() {
        this.control({
            '*[id=cms-start-button] menu > menuitem': {
                click: this.onStartMenuItemClick
            }
        });
    },

    onStartMenuItemClick: function(item, e, options ) {
        console.log(item);
        this.getSelectedAppLabel().setText(item.text);
    }
});

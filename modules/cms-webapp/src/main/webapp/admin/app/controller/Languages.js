Ext.define('CMS.controller.Languages', {
    extend: 'Ext.app.Controller',

    stores: ['Languages'],
    models: ['Language'],
    views: [
        'language.Toolbar',
        'language.Grid',
        'language.ContextMenu'
    ],

    refs: [
        {ref: 'languageGrid', selector: 'languageGrid'},
        {ref: 'languageContextMenu', selector: 'languageContextMenu', autoCreate: true, xtype: 'languageContextMenu'}

    ],

    init: function() {
        this.control({
            '*[action=newLanguage]': {
                click: this.newLanguage
            },
            '*[action=editLanguage]': {
                click: this.editLanguage
            },
            '*[action=deleteLanguage]': {
                click: this.deleteLanguage
            },
            'languageGrid': {
                itemcontextmenu: this.popupMenu
            }
        });
    },

    newLanguage: function() {
        Ext.Msg.alert('New Language', 'Not implemented.');
    },

    editLanguage: function() {
        Ext.Msg.alert('Edit Language', 'Not implemented.');
    },

    deleteLanguage: function() {
        Ext.Msg.alert('Delete Language', 'Not implemented.');
    },

    popupMenu: function(view, rec, node, index, e) {
        e.stopEvent();
        this.getLanguageContextMenu().showAt(e.getXY());
        return false;
    }

});

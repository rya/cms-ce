Ext.define('CMS.controller.ContentHandlerController', {
    extend: 'Ext.app.Controller',

    stores: ['ContentHandlerStore'],
    models: ['ContentHandlerModel'],
    views: ['contentHandler.Toolbar', 'contentHandler.GridPanel', 'contentHandler.ContextMenu'],

    refs: [
        {ref: 'contentHandlerGrid', selector: 'contentHandlerGrid'},
        {ref: 'contentHandlerContextMenu', selector: 'contentHandlerContextMenu', autoCreate: true, xtype: 'contentHandlerContextMenu'}
    ],

    init: function() {
        this.control({
            '*[action=newContentHandler]': {
                click: this.newContentHandler
            },
            '*[action=editContentHandler]': {
                click: this.editContentHandler
            },
            '*[action=deleteContentHandler]': {
                click: this.deleteContentHandler
            },
            'contentHandlerGrid': {
                itemcontextmenu: this.popupMenu
            }
        });
    },

    newContentHandler: function() {
        Ext.Msg.alert('New Content Handler', 'Not implemented.');
    },

    editContentHandler: function() {
        Ext.Msg.alert('Edit Content Handler', 'Not implemented.');
    },

    deleteContentHandler: function() {
        Ext.Msg.alert('Delete Content Handler', 'Not implemented.');
    },

    popupMenu: function(view, rec, node, index, e) {
        e.stopEvent();
        this.getContentHandlerContextMenu().showAt(e.getXY());
        return false;
    }

});

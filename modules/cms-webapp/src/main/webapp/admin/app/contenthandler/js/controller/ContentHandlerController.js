Ext.define('App.controller.ContentHandlerController', {
    extend: 'Ext.app.Controller',

    stores: ['ContentHandlerStore'],
    models: ['ContentHandlerModel'],
    views: ['Toolbar', 'GridPanel', 'ContextMenu'],

    init: function() {

        Ext.create('widget.contentHandlerContextMenu');

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
    },

    getContentHandlerContextMenu: function() {
        return Ext.ComponentQuery.query('contentHandlerContextMenu')[0];
    },

    getContentHandlerGrid: function() {
        return Ext.ComponentQuery.query('contentHandlerGrid')[0];
    }

});

Ext.define( 'App.controller.ContentTypeController', {
    extend: 'Ext.app.Controller',

    stores: ['ContentTypeStore'],
    models: ['ContentTypeModel'],
    views: ['GridPanel', 'DetailPanel', 'ContextMenu'],

    init: function()
    {
        Ext.create('widget.contentTypeContextMenu');

        this.control( {
            '*[action=newContentType]': {
                  click: this.newContentType
            },
            '*[action=editContentType]': {
                click: this.editContentType
            },
            '*[action=deleteContentType]': {
                click: this.deleteContentType
            },
            'contentTypeGrid': {
                selectionchange: this.updateDetailsPanel,
                itemcontextmenu: this.popupMenu,
                itemdblclick: this.editContentType
            }
        } );
    },

    newContentType: function()
    {
        Ext.Msg.alert( 'New Content Type', 'Not implemented.' );
    },

    editContentType: function() {
        Ext.Msg.alert('Edit Content Type', 'Not implemented.');
    },

    deleteContentType: function() {
        Ext.Msg.alert('Delete Content Type', 'Not implemented.');
    },

    updateDetailsPanel: function(selModel, selected) {
        var contentType = selected[0];
        var contentTypeDetail = this.getContentTypeDetail();

        if (contentType) {
            contentTypeDetail.update(contentType.data);
        }

    },

    popupMenu: function(view, rec, node, index, e) {
        e.stopEvent();
        this.getContentTypeContextMenu().showAt(e.getXY());
        return false;
    },

    getContentTypeGrid: function() {
        return Ext.ComponentQuery.query('contentTypeGrid')[0];
    },

    getContentTypeDetail: function() {
        return Ext.ComponentQuery.query('contentTypeDetail')[0];
    },

    getContentTypeContextMenu: function() {
        return Ext.ComponentQuery.query('contentTypeContextMenu')[0];
    }

} );
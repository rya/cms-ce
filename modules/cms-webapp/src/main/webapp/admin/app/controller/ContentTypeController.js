Ext.define( 'CMS.controller.ContentTypeController', {
    extend: 'Ext.app.Controller',

    stores: ['ContentTypeStore'],
    models: ['ContentTypeModel'],
    views: ['contentType.GridPanel', 'contentType.DetailPanel', 'contentType.ContextMenu'],

    refs: [
        {ref: 'contentTypeGrid', selector: 'contentTypeGrid'},
        {ref: 'contentTypeDetail', selector: 'contentTypeDetail'},
        {ref: 'contentTypeContextMenu', selector: 'contentTypeContextMenu', autoCreate: true, xtype: 'contentTypeContextMenu'}
    ],

    init: function()
    {
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
                selectionchange: this.updateInfo,
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

    updateInfo: function(selModel, selected) {
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
    }

} );
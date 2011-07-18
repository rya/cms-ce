Ext.define('CMS.view.contentHandler.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.contentHandlerContextMenu',

    items: [
        {
            text: 'Edit Content Handler',
            iconCls: 'icon-edit',
            action: 'editContentHandler'
        },
        {
            text: 'Delete Content Handler',
            iconCls: 'icon-delete',
            action: 'deleteContentHandler'
        }
    ]
});


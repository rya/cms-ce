Ext.define('App.view.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.contentTypeContextMenu',

    items: [
        {
            text: 'Edit Content Type',
            iconCls: 'icon-edit',
            action: 'editContentType'
        },
        {
            text: 'Delete Content Type',
            iconCls: 'icon-delete',
            action: 'deleteContentType'
        }
    ]
});


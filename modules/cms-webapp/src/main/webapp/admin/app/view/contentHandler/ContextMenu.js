Ext.define('CMS.view.contentHandler.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.contentHandlerContextMenu',

    items: [
        {
            text: 'Edit Content Handler',
            icon: 'resources/images/pencil.png',
            action: 'editContentHandler'
        },
        {
            text: 'Delete Content Handler',
            icon: 'resources/images/delete.png',
            action: 'deleteContentHandler'
        }
    ]
});


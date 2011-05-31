Ext.define('CMS.view.contentType.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.contentTypeContextMenu',

    items: [
        {
            text: 'Edit Content Type',
            icon: 'resources/images/pencil.png',
            action: 'editContentType'
        },
        {
            text: 'Delete Content Type',
            icon: 'resources/images/delete.png',
            action: 'deleteContentType'
        }
    ]
});


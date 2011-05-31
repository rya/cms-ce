Ext.define('CMS.view.contentType.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentTypeToolbar',

    items: [
        {
            text: 'New',
            icon: 'resources/images/add.png',
            action: 'newContentType'
        }
    ],

    initComponent: function() {
        this.callParent(arguments);
    }
});


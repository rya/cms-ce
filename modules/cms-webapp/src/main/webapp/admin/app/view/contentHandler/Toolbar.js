Ext.define('CMS.view.contentHandler.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentHandlerToolbar',

    items: [
        {
            text: 'New',
            icon: 'resources/images/add.png',
            action: 'newContentHandler'
        }
    ],

    initComponent: function() {
        this.callParent(arguments);
    }
});


Ext.define('CMS.view.contentHandler.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentHandlerToolbar',

    items: [
        {
            text: 'New',
            iconCls: 'icon-new',
            action: 'newContentHandler'
        }
    ],

    initComponent: function() {
        this.callParent(arguments);
    }
});


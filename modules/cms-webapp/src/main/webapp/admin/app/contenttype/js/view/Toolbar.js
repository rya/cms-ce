Ext.define('App.view.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentTypeToolbar',

    items: [
        {
            text: 'New',
            iconCls: 'icon-new',
            action: 'newContentType'
        }
    ],

    initComponent: function() {
        this.callParent(arguments);
    }
});


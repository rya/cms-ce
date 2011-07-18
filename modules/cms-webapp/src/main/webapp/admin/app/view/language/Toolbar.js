Ext.define('CMS.view.language.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.languageToolbar',

    items: [
        {
            text: 'New',
            iconCls: 'icon-new',
            action: 'newLanguage'
        }
    ],

    initComponent: function() {
        this.callParent(arguments);
    }
});


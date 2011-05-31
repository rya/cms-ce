Ext.define('CMS.view.language.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.languageToolbar',

    items: [
        {
            text: 'New',
            icon: 'resources/images/add.png',
            action: 'newLanguage'
        }
    ],

    initComponent: function() {
        this.callParent(arguments);
    }
});


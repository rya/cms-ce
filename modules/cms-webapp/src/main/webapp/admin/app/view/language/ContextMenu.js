Ext.define('CMS.view.language.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.languageContextMenu',

    items: [
        {
            text: 'Edit Language',
            icon: 'resources/images/pencil.png',
            action: 'editLanguage'
        },
        {
            text: 'Delete Language',
            icon: 'resources/images/delete.png',
            action: 'deleteLanguage'
        }
    ]
});


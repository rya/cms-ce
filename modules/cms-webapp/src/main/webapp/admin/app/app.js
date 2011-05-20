Ext.Loader.setConfig({
    enabled: true
});

Ext.application({
    name: 'CMS',

    appFolder: 'app',

    controllers: [
        'Users'
    ]
});

Ext.Loader.setConfig({
    enabled: true
});

Ext.application({
    name: 'CMS',

    appFolder: 'app',
    autoCreateViewport: true,

    controllers: [
        'Users'
    ]
});

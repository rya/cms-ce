Ext.define('App.store.LanguageStore', {
    extend: 'Ext.data.Store',

    model: 'App.model.LanguageModel',

    pageSize: 100,
    autoLoad: true,

    sorters: [
        {
            property : 'languageCode',
            direction: 'ASC'
        }
    ],

    proxy: {
        type: 'ajax',
        url: 'app/account/js/data/Languages.json',
        reader: {
            type: 'json',
            root: 'languages',
            totalProperty : 'total'
        }
    }
});


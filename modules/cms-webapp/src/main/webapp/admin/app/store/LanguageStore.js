Ext.define('CMS.store.LanguageStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.LanguageModel',

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
        url: 'app/data/Languages.json',
        reader: {
            type: 'json',
            root: 'languages',
            totalProperty : 'total'
        }
    }
});


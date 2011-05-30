Ext.define('CMS.store.Languages', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.Language',

    pageSize: 10,
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


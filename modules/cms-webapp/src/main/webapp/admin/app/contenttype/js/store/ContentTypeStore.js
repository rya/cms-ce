Ext.define( 'App.store.ContentTypeStore', {
    extend: 'Ext.data.Store',

    model: 'App.model.ContentTypeModel',

    pageSize: 100,
    autoLoad: true,
    remoteSort: true,

    sorters: [
        {
            property : 'name',
            direction: 'ASC'
        }
    ],

    proxy: {
        type: 'ajax',
        url: 'app/contenttype/js/data/ContentTypes.json',
        reader: {
            type: 'json',
            root: 'contentTypes',
            totalProperty : 'total'
        }
    }
} );
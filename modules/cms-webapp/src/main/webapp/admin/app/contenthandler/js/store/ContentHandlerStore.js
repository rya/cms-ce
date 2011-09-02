Ext.define( 'App.store.ContentHandlerStore', {
    extend: 'Ext.data.Store',

    model: 'App.model.ContentHandlerModel',

    pageSize: 100,
    autoLoad: true,
    remoteSort: false,

    sorters: [
        {
            property : 'name',
            direction: 'ASC'
        }
    ],

    proxy: {
        type: 'ajax',
        url: 'app/contenthandler/js/data/ContentHandlers.json',
        reader: {
            type: 'json',
            root: 'contentHandlers',
            totalProperty : 'total'
        }
    }
} );
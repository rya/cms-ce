Ext.define( 'CMS.store.ContentHandlerStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.ContentHandlerModel',

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
        url: 'app/data/ContentHandlers.json',
        reader: {
            type: 'json',
            root: 'contentHandlers',
            totalProperty : 'total'
        }
    }
} );
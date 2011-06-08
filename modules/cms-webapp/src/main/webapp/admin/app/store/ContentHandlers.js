Ext.define( 'CMS.store.ContentHandlers', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.ContentHandler',

    pageSize: 10,
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
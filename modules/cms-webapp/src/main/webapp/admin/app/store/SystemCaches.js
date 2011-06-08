Ext.define( 'CMS.store.SystemCaches', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.SystemCache',

    pageSize: 10,
    autoLoad: true,
    remoteSort: true,

    proxy: {
        type: 'ajax',
        url: 'app/data/SystemCaches.json',
        reader: {
            type: 'json',
            root: 'systemCaches',
            totalProperty : 'total'
        }
    }
} );
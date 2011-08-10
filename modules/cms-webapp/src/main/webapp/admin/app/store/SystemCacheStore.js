Ext.define( 'CMS.store.SystemCacheStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.SystemCacheModel',

    pageSize: 10,
    autoLoad: true,
    remoteSort: false,
    groupField: 'node',

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
Ext.define( 'CMS.store.SystemCacheStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.SystemCacheModel',

    pageSize: 10,
    autoLoad: true,
    remoteSort: false,
    groupField: 'implementationName',

    proxy: {
        type: 'ajax',
        url: 'data/system/caches',
        reader: {
            type: 'json',
            root: 'caches',
            totalProperty : 'total'
        }
    }
} );
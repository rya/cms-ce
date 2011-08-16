Ext.define( 'CMS.store.SystemCacheStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.SystemCacheModel',

    pageSize: 10,
    autoLoad: true,
    remoteSort: false,
    groupField: 'name',

    proxy: {
        type: 'ajax',
        url: 'data/system/cache/list',
        reader: {
            type: 'json',
            root: 'caches',
            totalProperty : 'total'
        }
    }
} );
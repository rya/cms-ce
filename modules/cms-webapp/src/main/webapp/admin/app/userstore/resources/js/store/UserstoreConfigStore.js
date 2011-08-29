Ext.define('App.store.UserstoreConfigStore', {
    extend: 'Ext.data.Store',
    model: 'App.model.UserstoreConfigModel',

    pageSize: 100,
    autoLoad: true,
    remoteSort: true,

    proxy: {
        type: 'ajax',
        url: '/admin/data/userstore/list',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'userStoreConfigs'
        }
    }
});

Ext.define('CMS.store.UserstoreConfigStore', {
    extend: 'Ext.data.Store',
    model: 'CMS.model.UserstoreConfigModel',

    pageSize: 100,
    autoLoad: true,
    remoteSort: true,

    proxy: {
        type: 'ajax',
        url: 'data/userstore/list',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'userStoreConfigs'
        }
    }
});

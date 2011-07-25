Ext.define('CMS.store.UserStoreConfigStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.UserStoreConfigModel',

    pageSize: 10,
    autoLoad: false,
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

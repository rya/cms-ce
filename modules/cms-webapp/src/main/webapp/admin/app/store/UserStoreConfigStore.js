Ext.define('CMS.store.UserStoreConfigStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.UserStoreConfigModel',

    pageSize: 10,
    autoLoad: true,
    remoteSort: true,

    proxy: {
        type: 'ajax',
        url: 'app/data/UserStoreConfigs.json',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'userStoreConfigs'
        }
    }
});

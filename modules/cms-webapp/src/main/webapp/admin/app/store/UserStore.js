Ext.define('CMS.store.UserStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.UserModel',

    pageSize: 10,
    autoLoad: true,
    remoteSort: true,

    proxy: {
        type: 'rest',
        url: 'rest/users',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'users',
            totalProperty : 'total'
        }
    }
});

Ext.define('CMS.store.UserStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.UserModel',

    pageSize: 100,
    autoLoad: true,
    remoteSort: true,

    proxy: {
        type: 'ajax',
        url: '/admin/data/user/list',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'users',
            totalProperty : 'total'
        }
    }
});

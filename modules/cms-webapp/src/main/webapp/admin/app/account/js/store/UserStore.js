Ext.define('App.store.UserStore', {
    extend: 'Ext.data.Store',

    model: 'App.model.UserModel',

    //pageSize: 100,
    remoteSort: true,
    buffered: true,

    proxy: {
        type: 'ajax',
        url: 'data/user/list',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'users',
            totalProperty : 'total'
        }
    }
});

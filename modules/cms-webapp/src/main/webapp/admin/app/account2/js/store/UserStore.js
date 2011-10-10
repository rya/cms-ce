Ext.define('App.store.UserStore', {
    extend: 'Ext.data.Store',

    model: 'App.model.UserModel',

    pageSize: 50,
    remoteSort: true,
    //buffered: true,
    autoLoad: true,

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

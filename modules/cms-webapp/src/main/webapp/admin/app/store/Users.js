Ext.define('CMS.store.Users', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.User',

    pageSize: 10,
    autoLoad: true,

    proxy: {
        type: 'rest',
        url: 'rest/users',
        reader: {
            type: 'json',
            root: 'users',
            totalProperty : 'total'
        }
    }
});

Ext.define('CMS.store.GroupStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.GroupModel',

    remoteFilter: false,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'data/account/grouplist',
        reader: {
            type: 'json',
            root: 'accounts'
        }
    }
});
Ext.define('CMS.store.GroupStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.GroupModel',

    remoteFilter: true,
    //autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'data/group/list',
        reader: {
            type: 'json',
            root: 'accounts'
        }
    }
});
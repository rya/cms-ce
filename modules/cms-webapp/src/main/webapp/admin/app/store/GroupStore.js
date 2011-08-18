Ext.define('CMS.store.GroupStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.GroupModel',

    //pageSize: 100,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'app/data/Groups.json',
        reader: {
            type: 'json',
            root: 'groups',
            totalProperty : 'total'
        }
    }
});
Ext.define('App.store.UserstoreConnectorStore', {
    extend: 'Ext.data.Store',
    model: 'App.model.UserstoreConnectorModel',

    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: '../admin/data/userstore/connectors',
        simpleSortMode: true,
        reader: {
            type: 'json',
            root: 'userStoreConnectors'
        }
    }
});

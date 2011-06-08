Ext.define('CMS.store.PropertyStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.PropertyModel',

    pageSize: 10,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'app/data/Properties.json',
        reader: {
            type: 'json',
            root: 'properties',
            totalProperty : 'total'
        }
    }
});
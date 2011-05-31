Ext.define('CMS.store.Properties', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.Property',

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
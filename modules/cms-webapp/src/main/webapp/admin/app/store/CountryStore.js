Ext.define('CMS.store.CountryStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.CountryModel',

    //pageSize: 10,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'app/data/Countries.json',
        reader: {
            type: 'json',
            root: 'countries',
            totalProperty : 'total'
        }
    }
});
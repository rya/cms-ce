Ext.define('CMS.store.CountryStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.CountryModel',

    //pageSize: 100,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'data/misc/country/list',
        //url: 'app/data/Countries.json',
        reader: {
            type: 'json',
            root: 'countries',
            totalProperty : 'total'
        }
    }
});
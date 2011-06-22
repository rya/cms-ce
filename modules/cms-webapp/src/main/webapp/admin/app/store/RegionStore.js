Ext.define('CMS.store.RegionStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.RegionModel',

    //pageSize: 10,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'app/data/Regions.json',
        reader: {
            type: 'json',
            root: 'regions',
            totalProperty : 'total'
        }
    }
});

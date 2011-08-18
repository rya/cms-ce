Ext.define('CMS.store.TimezoneStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.TimezoneModel',

    //pageSize: 100,
    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'data/misc/timezone/list',
        reader: {
            type: 'json',
            root: 'timezones',
            totalProperty : 'total'
        }
    }
});
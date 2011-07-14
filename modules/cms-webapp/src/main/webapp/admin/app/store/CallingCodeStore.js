Ext.define( 'CMS.store.CallingCodeStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.CallingCodeModel',

    autoLoad: true,

    remoteFilter: false,

    proxy: {
        type: 'ajax',
        url: 'data/misc/callingcodes/list',
        reader: {
            type: 'json',
            root: 'codes'
        }
    }
} );
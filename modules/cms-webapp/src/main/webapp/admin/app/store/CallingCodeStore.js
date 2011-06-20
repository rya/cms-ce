Ext.define( 'CMS.store.CallingCodeStore', {
    extend: 'Ext.data.Store',

    model: 'CMS.model.CallingCodeModel',

    autoLoad: true,

    proxy: {
        type: 'ajax',
        url: 'app/data/CallingCodes.json',
        reader: {
            type: 'json',
            root: 'codes'
        }
    }
} );
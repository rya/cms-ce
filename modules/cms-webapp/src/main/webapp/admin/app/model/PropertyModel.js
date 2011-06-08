Ext.define('CMS.model.PropertyModel', {
    extend: 'Ext.data.Model',

    fields: [
        'name', 'value'
    ],

    idProperty: 'name'
});
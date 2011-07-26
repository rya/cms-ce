Ext.define('CMS.model.UserstoreConfigModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key',
        'name',
        {name: 'defaultStore', type: 'int'},
        'connectorName',
        'configXML',
        {name: 'deleted', type: 'boolean'}
    ],

    idProperty: 'id'
});

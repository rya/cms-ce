Ext.define('CMS.model.UserstoreConfigModel', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields: [
        'key',
        'name',
        {name: 'defaultStore', type: 'int'},
        'connectorName',
        'configXML',
        {name: 'deleted', type: 'boolean'}
    ]
});

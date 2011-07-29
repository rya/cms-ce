Ext.define('CMS.model.UserstoreConfigModel', {
    extend: 'Ext.data.Model',

    idProperty: 'id',

    fields: [
        'key',
        'name',
        {name: 'defaultStore', type: 'boolean'},
        'connectorName',
        'configXML',
        {name: 'deleted', type: 'boolean'},
        {name: 'lastModified', type: 'date', defaultValue: new Date() }
    ]
});

Ext.define( 'App.model.ContentHandlerModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key',
        { name: 'name', type: 'string' },
        { name: 'class', type: 'string' },
        { name: 'configuration', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'defaultIndexing', type: 'string' },
        { name: 'defaultContentTypeConfig', type: 'string' },
        { name: 'lastModified', type: 'date', dateFormat: 'Y-m-d H:i:s' }
    ],

    idProperty: 'key'
} );
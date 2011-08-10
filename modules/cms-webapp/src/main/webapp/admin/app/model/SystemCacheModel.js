Ext.define('CMS.model.SystemCacheModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'node', 'name',
        {name: 'count', type: 'int'},
        {name: 'size', type: 'int'},
        {name: 'hits', type: 'int'},
        {name: 'misses', type: 'int'},
        {name: 'timeToLive', type: 'int'}
    ],

    idProperty: 'key'
});
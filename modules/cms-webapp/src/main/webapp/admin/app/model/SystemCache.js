Ext.define('CMS.model.SystemCache', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'implementation', {name: 'timeToLive', type: 'int'},
        {name: 'maxMemElements', type: 'int'},
        {name: 'maxDiskElements', type: 'int'},
        {name: 'objectCount', type: 'int'},
        {name: 'cacheHits', type: 'int'},
        {name: 'cacheMisses', type: 'int'}
    ],

    idProperty: 'key'
});
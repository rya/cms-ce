Ext.define('CMS.model.User', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'name', 'email', 'qualifiedName', 'displayName', 'userStore',
        {name: 'lastModified', type: 'date', dateFormat: 'Y-m-d H:i:s'}
    ],

    idProperty: 'key'
});

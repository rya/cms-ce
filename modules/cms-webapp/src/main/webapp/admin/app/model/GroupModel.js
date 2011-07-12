Ext.define( 'CMS.model.GroupModel', {
    extend: 'Ext.data.Model',

    fields: [
        'key', 'name', 'userStore', {name: 'memberCount', type: 'int'},
        {name: 'restrictedEnrollment', type: 'boolean'}
    ],

    idProperty: 'key'
} );

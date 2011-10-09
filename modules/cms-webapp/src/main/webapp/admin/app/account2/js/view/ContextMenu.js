Ext.define('App.view.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.accountContextMenu',

    items: [
        {
            text: 'Edit User',
            iconCls: 'icon-edit-user',
            action: 'edit',
            disableOnMultipleSelection: true
        },
        {
            text: 'Delete User',
            iconCls: 'icon-delete-user',
            action: 'showDeleteWindow'
        },
        '-',
        {
            text: 'Change Password',
            iconCls: 'icon-change-password',
            action: 'changePassword',
            disableOnMultipleSelection: true
        }
    ]
});


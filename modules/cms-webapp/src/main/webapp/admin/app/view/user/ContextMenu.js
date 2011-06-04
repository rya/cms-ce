Ext.define('CMS.view.user.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.userContextMenu',

    items: [
        {
            text: 'Edit User',
            iconCls: 'edit-user',
            action: 'edit'
        },
        {
            text: 'Delete User',
            iconCls: 'delete-user',
            action: 'showDeleteWindow'
        },
        '-',
        {
            text: 'Change Password',
            iconCls: 'change-password',
            action: 'changePassword'
        }
    ]
});


Ext.define('CMS.view.user.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.userContextMenu',

    items: [
        {
            text: 'Edit User',
            iconCls: 'icon-edit-user',
            action: 'edit'
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
            action: 'changePassword'
        }
    ]
});


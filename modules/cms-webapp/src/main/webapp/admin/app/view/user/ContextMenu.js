Ext.define('CMS.view.user.ContextMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.userContextMenu',

    items: [
        {
            text: 'Edit User',
            icon: 'resources/images/user_edit.png',
            action: 'edit'
        },
        {
            text: 'Delete User',
            icon: 'resources/images/user_delete.png',
            action: 'delete'
        },
        '-',
        {
            text: 'Change Password',
            icon: 'resources/images/key.png',
            action: 'changePassword'
        }
    ]
});


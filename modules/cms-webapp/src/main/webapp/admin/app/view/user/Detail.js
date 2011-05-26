Ext.define('CMS.view.user.Detail', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userDetail',

    title: 'Details',
    split: true,
    autoScroll: true,

    initComponent: function() {

        this.tpl = new Ext.XTemplate(
                '<div class="user-info">',
                '<img src="rest/users/{key}/photo" width="100">',
                '<h3>{displayName}</h3>',
                '<dl>',
                '<dt>Local Name</dt><dd>{name}</dd>',
                '<dt>User Store</dt><dd>{userStore}</dd>',
                '<dt>Qualified Name</dt><dd>{qualifiedName}</dd>',
                '<dt>Last Modified</dt><dd>{lastModified:this.formatDate}</dd>',
                '</dl>',
                '</div>', {

            formatDate: function(value) {
                if (!value) {
                    return '';
                }
                return Ext.Date.format(value, 'j M Y, H:i:s');
            }
            
        });

        this.dockedItems = [
            {
                dock: 'top',
                xtype: 'toolbar',
                border: false,
                padding: 5,
                items: [
                    {
                        text: 'Edit User',
                        icon: 'resources/images/user_edit.png',
                        action: 'edit'
                    },
                    {
                        text: 'Delete User',
                        icon: 'resources/images/user_delete.png',
                        action: 'showDeleteWindow'
                    },
                    '-',
                    {
                        text: 'Change Password',
                        icon: 'resources/images/key.png',
                        action: 'changePassword'
                    }
                ]
            }
        ];

        this.callParent(arguments);
    }

});

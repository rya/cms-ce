Ext.define('CMS.view.user.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userDetail',

    title: 'Details',
    split: true,
    autoScroll: true,

    initComponent: function() {

        this.tpl = new Ext.XTemplate(
                '<div class="detail-info">',
                '<img src="data/photo?key={key}&thumb=false" width="100">',
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
            }
        ];

        this.callParent(arguments);
    }

});

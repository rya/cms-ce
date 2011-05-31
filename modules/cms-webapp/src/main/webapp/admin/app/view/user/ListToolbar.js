Ext.define('CMS.view.user.ListToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.userListToolbar',

    items: [
        {
            xtype: 'button',
            text: 'New',
            icon: 'resources/images/add.png',
            menu: new Ext.menu.Menu({
                items: [
                    {
                        text: 'User',
                        icon: 'resources/images/user_add.png',
                        action: 'newUser'
                    },
                    {
                        text: 'Group',
                        icon: 'resources/images/group_add.png',
                        action: 'newGroup'
                    }
                ]
            })
        }
    ],

    initComponent: function() {
        this.callParent(arguments);
    }
});

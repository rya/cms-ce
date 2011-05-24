Ext.define('CMS.view.user.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.userToolbar',

    items: [
        {
            xtype: 'splitbutton',
            text : 'New',
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


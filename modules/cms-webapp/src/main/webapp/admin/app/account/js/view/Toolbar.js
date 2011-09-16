Ext.define('App.view.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias : 'widget.accountsToolbar',

    border: false,

    initComponent: function() {

        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top'
        };

        this.items = [
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        xtype: 'splitbutton',
                        text: ' &nbsp;&nbsp;&nbsp;New&nbsp;&nbsp;&nbsp; ',
                        action: 'newUser',
                        iconCls: 'icon-add-24',
                        cls: 'x-btn-as-arrow',
                        menu: new Ext.menu.Menu({
                            items: [
                                {text: 'User', iconCls: '', action: 'newUser'},
                                {text: 'Group', iconCls: '', action: 'newGroup'}
                            ]
                        })

                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 3,
                defaults: buttonDefaults,
                items: [
                    {
                        text: '&nbsp;&nbsp;Edit&nbsp;&nbsp;',
                        action: 'edit',
                        iconCls: 'icon-edit-user-24',
                        disableOnMultipleSelection: true
                    },
                    {
                        text: 'Delete',
                        action: 'showDeleteWindow',
                        iconCls: 'icon-delete-user-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Change Password',
                        action: 'changePassword',
                        iconCls: 'icon-change-password-24',
                        disableOnMultipleSelection: true
                    }
                ]
            }
        ];

        this.callParent(arguments);
    }

});

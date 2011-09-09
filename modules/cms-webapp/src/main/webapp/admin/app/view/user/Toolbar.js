Ext.define('CMS.view.user.Toolbar', {
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
                        iconCls: 'cms-empty',
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
                        text: 'Edit',
                        action: 'edit',
                        iconCls: 'cms-empty',
                        disableOnMultipleSelection: true
                    },
                    {
                        text: 'Delete',
                        action: 'showDeleteWindow',
                        iconCls: 'cms-empty'
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
                        iconCls: 'cms-empty',
                        disableOnMultipleSelection: true
                    }
                ]
            }
        ];

        this.callParent(arguments);
    }

});

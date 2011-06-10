Ext.define('CMS.view.main.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',

    alias: 'widget.mainToolbar',
    id: 'main-toolbar',


    items: [
        {
            xtype: 'tbspacer', width: 5
        },
        {
            id: 'main-start-button',
            xtype: 'button',
            iconCls: 'icon-main-start-button',
            menu: [
                {
                    id: 0,
                    text: 'Dashboard',
                    appUrl: 'dashboard.html',
                    iconCls: 'icon-dashboard'
                },
                {
                    id: 10,
                    text: 'Accounts',
                    appUrl: 'accounts.html',
                    iconCls: 'icon-accounts'
                },
                {
                    id: 20,
                    text: 'Content',
                    appUrl: '',
                    iconCls: ''
                },
                {
                    text: 'Applications',
                    menu: [
                        {
                            id: 30,
                            text: 'Flatskjerm',
                            appUrl: '',
                            iconCls: ''
                        },
                        {
                            id: 40,
                            text: 'Sporingsapplikasjon',
                            appUrl: '',
                            iconCls: ''
                        },
                        {
                            id: 50,
                            text: 'SkiFree',
                            appUrl: '',
                            iconCls: ''
                        }
                    ]
                },
                {
                    id: 60,
                    text: 'Reports',
                    appUrl: '',
                    iconCls: 'icon-report'
                },
                {
                    id: 70,
                    text: 'System',
                    appUrl: 'properties.html',
                    iconCls: 'icon-system'
                }
            ]
        },
        {
            xtype: 'tbspacer', width: 5
        },
        {
            id: 'main-selected-application-label',
            xtype: 'label',
            text: '&nbsp;'
        },
        '->',
        {
            id: 'main-logged-in-user-button',
            xtype: 'button',
            text: 'John Doe',
            menu: [
                {
                    text: 'Profile'
                },
                {
                    text: 'Change Password',
                    iconCls: 'icon-change-password'
                },
                '-',
                {
                    text: 'Log out',
                    iconCls: 'icon-log-out',
                    handler: function() {
                        document.location.href = 'index.html';
                    }
                }
            ]
        },
        '-',
        {
            id: 'main-settings-button',
            xtype: 'button',
            iconCls: 'icon-settings',
            menu: [
                {
                    text: 'Settings'
                },
                {
                    text: 'For'
                },
                {
                    text: 'Selected App'
                }
            ]
        }
    ]

});


Ext.define('CMS.view.main.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',

    alias: 'widget.mainToolbar',
    id: 'main-toolbar',

    items: [
        {
            id: 'main-start-button',
            xtype: 'button',
            iconCls: 'main-start-button',
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
                    text: 'Log out',
                    iconCls: 'icon-log-out'
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
                    text: 'For Selected App'
                }
            ]
        }
    ]

});


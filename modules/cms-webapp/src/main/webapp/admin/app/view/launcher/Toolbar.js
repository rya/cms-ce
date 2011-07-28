Ext.define('CMS.view.launcher.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',

    alias: 'widget.launcherToolbar',
    id: 'launcher-toolbar',

    items: [
        {
            xtype: 'tbspacer', width: 10
        },
        {
            xtype: 'component',
            id: 'launcher-logo',
            autoEl: {
                tag: 'div'
            }
        },
        {
            xtype: 'tbspacer', width: 15
        },
        {
            id: 'launcher-start-button',
            xtype: 'button',
            text: 'Dashboard',
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
                    text: 'CMS',
                    appUrl: 'cms.html',
                    iconCls: 'icon-enonic'
                },
                {
                    text: 'Applications',
                    menu: [
                        {
                            id: 30,
                            text: 'Flatskjerm',
                            appUrl: ''
                        },
                        {
                            id: 40,
                            text: 'SkiFree',
                            appUrl: ''
                        },
                        {
                            id: 50,
                            text: 'Sporingsapplikasjon',
                            appUrl: ''
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
                    appUrl: 'system.html',
                    iconCls: 'icon-system'
                }
            ]
        },
        '->',
        {
            id: 'launcher-logged-in-user-button',
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
            id: 'launcher-settings-button',
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


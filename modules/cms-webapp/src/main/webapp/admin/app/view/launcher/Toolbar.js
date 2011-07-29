Ext.define('CMS.view.launcher.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',

    alias: 'widget.launcherToolbar',
    id: 'launcher-toolbar',

    items: [
        {
            xtype: 'tbspacer', width: 5
        },
        // Logo
        {
            xtype: 'component',
            id: 'launcher-logo',
            autoEl: {
                tag: 'div'
            }
        },
        {
            xtype: 'tbspacer', width: 5
        },
        '-',
        {
            xtype: 'tbspacer', width: 5
        },
        // Start button
        {
            id: 'launcher-start-button',
            xtype: 'button',
            text: 'Dashboard',
            menu: [
                {
                    id: 0,
                    text: 'Dashboard',
                    appUrl: 'dashboard.html',
                    icon: 'resources/images/house.png'
                },
                {
                    id: 10,
                    text: 'Accounts',
                    appUrl: 'accounts.html',
                    icon: 'resources/images/group.png'
                },
                {
                    id: 20,
                    text: 'CMS',
                    appUrl: 'cms.html'
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
                    icon: 'resources/images/report.png'
                },
                {
                    id: 70,
                    text: 'System',
                    appUrl: 'system.html',
                    icon: 'resources/images/cog.png'
                }
            ]
        },
        '->',
        // Logged in user
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
        // Settings
        {
            id: 'launcher-settings-button',
            xtype: 'button',
            iconCls: 'icon-settings',
            menu: [
                {
                    text: 'Setting 1'
                },
                {
                    text: 'Setting 2'
                },
                {
                    text: 'Setting 3'
                }
            ]
        }
    ]

});


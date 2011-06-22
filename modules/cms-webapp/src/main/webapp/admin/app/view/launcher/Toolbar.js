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
            xtype: 'tbspacer', width: 20
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
                    appUrl: 'cms.html',
                    icon: 'resources/images/enonic-logo16x16.png'
                },
                {
                    text: 'Applications',
                    menu: [
                        {
                            id: 30,
                            text: 'Flatskjerm',
                            appUrl: '',
                            icon: ''
                        },
                        {
                            id: 40,
                            text: 'SkiFree',
                            appUrl: '',
                            icon: ''
                        },
                        {
                            id: 50,
                            text: 'Sporingsapplikasjon',
                            appUrl: '',
                            icon: ''
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
                    appUrl: 'properties.html',
                    icon: 'resources/images/cog.png'
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
                    icon: 'icon-change-password'
                },
                '-',
                {
                    text: 'Log out',
                    icon: 'icon-log-out',
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


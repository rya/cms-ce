Ext.define('CMS.view.launcher.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',

    requires: ['CMS.view.launcher.LoggedInUserButton'],

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
            menu: {
                minWidth: 160,
                items:[
                    {
                        id: 0,
                        text: 'Dashboard',
                        appUrl: 'dashboard.html',
                        icon: 'resources/images/house.png'
                    },
                    '-',
                    {
                        id: 10,
                        text: 'Accounts',
                        appUrl: 'accounts.html',
                        icon: 'resources/images/group.png'
                    },
                    {
                        id: 20,
                        text: 'Content',
                        appUrl: 'blank.html',
                        icon: 'resources/images/folder_database.png'
                    },
                    {
                        id: 30,
                        text: 'Sites',
                        appUrl: 'blank.html',
                        icon: 'resources/images/world.png'
                    },
                    {
                        id: 40,
                        text: 'Direct Marketing',
                        appUrl: 'blank.html',
                        icon: 'resources/images/chart_curve.png'
                    },
                    {
                        id: 50,
                        text: 'Reports',
                        appUrl: 'blank.html',
                        icon: 'resources/images/report.png'
                    },
                    '-',
                    {
                        id: 60,
                        text: 'Repository',
                        appUrl: 'blank.html',
                        icon: 'resources/images/database.png'
                    },
                    {
                        id: 70,
                        text: 'System',
                        appUrl: 'system.html',
                        icon: 'resources/images/cog.png'
                    },
                    '-',
                    {
                        id: 80,
                        text: 'Live Portal Trace',
                        appUrl: 'blank.html',
                        icon: 'resources/images/utilities-system-monitor.png'
                    }
                ]
            }

        },
        '->',
        // Logged in user
        {
            //id: 'launcher-logged-in-user-button',
            xtype: 'loggedInUserButton',
            text: 'Joe Doe'
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


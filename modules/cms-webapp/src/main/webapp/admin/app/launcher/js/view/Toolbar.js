Ext.define('App.view.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',

    requires: ['App.view.LoggedInUserButton'],

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
                        icon: 'app/launcher/images/house.png'
                    },
                    '-',
                    {
                        id: 100,
                        text: 'Accounts',
                        appUrl: 'accounts.html',
                        icon: 'app/launcher/images/group.png'
                    },
                    {
                        id: 200,
                        text: 'Content',
                        appUrl: 'blank.html',
                        icon: 'app/launcher/images/folder_database.png'
                    },
                    {
                        id: 300,
                        text: 'Sites',
                        appUrl: 'blank.html',
                        icon: 'app/launcher/images/world.png'
                    },
                    {
                        id: 500,
                        text: 'Direct Marketing',
                        appUrl: 'blank.html',
                        icon: 'app/launcher/images/chart_curve.png'
                    },
                    {
                        id: 600,
                        text: 'Reports',
                        appUrl: 'blank.html',
                        icon: 'app/launcher/images/report.png'
                    },
                    '-',
                    {
                        id: 700,
                        text: 'Repository',
                        appUrl: 'blank.html',
                        icon: 'app/launcher/images/database.png'
                    },
                    {
                        id: 400,
                        text: 'Userstores',
                        appUrl: 'app-userstore.html',
                        icon: 'app/launcher/images/address-book-blue-icon.png'
                    },
                    {
                        id: 500,
                        text: "Content Types",
                        appUrl: "contentTypes.html",
                        icon: 'app/launcher/images/page_world.png'
                    },
                    {
                        id: 600,
                        text:"Cache",
                        appUrl:"systemCaches.html",
                        icon: 'app/launcher/images/drive_web.png'
                    },
                    {
                        id: 800,
                        text: 'System',
                        appUrl: 'system.html',
                        icon: 'app/launcher/images/cog.png'
                    },
                    '-',
                    {
                        id: 900,
                        text: 'Live Portal Trace',
                        appUrl: 'blank.html',
                        icon: 'app/launcher/images/utilities-system-monitor.png'
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


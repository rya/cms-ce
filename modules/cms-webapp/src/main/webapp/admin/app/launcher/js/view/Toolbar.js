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
            icon: Ext.BLANK_IMAGE_URL,

            menu: {
                minWidth: 160,
                items:[
                    {
                        id: 0,
                        text: 'Dashboard',
                        cms: {
                            appUrl: 'app-dashboard.html'
                        },
                        icon: 'app/launcher/images/house.png'
                    },
                    '-',
                    {
                        id: 100,
                        text: 'Accounts',
                        cms: {
                            appUrl: 'app-account.html'
                        },
                        icon: 'app/launcher/images/group.png'
                    },
                    {
                        id: 200,
                        text: 'Content',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/launcher/images/folder_database.png'
                    },
                    {
                        id: 300,
                        text: 'Sites',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/launcher/images/world.png'
                    },
                    {
                        id: 500,
                        text: 'Direct Marketing',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/launcher/images/chart_curve.png'
                    },
                    {
                        id: 600,
                        text: 'Reports',
                        cms: {
                            appUrl: 'blank.html'
                        },
                        icon: 'app/launcher/images/report.png'
                    },

                    '-',

                    {
                        id: 800,
                        text: 'System',
                        cms: {
                            appUrl: 'app-system.html'
                        },
                        icon: 'app/launcher/images/cog.png',
                        menu: {
                            items: [
                                {
                                    id: 810,
                                    text:"Cache",
                                    cms: {
                                        appUrl:"app-systemCache.html"
                                    },
                                    icon: 'app/launcher/images/drive_web.png'
                                },
                                {
                                    id: 820,
                                    text: "Content Types",
                                    cms: {
                                        appUrl: "app-contentType.html"
                                    },
                                    icon: 'app/launcher/images/page_world.png'
                                },
                                {
                                    id: 830,
                                    text: 'Live Portal Trace',
                                    cms: {
                                        appUrl: 'blank.html'
                                    },
                                    icon: 'app/launcher/images/utilities-system-monitor.png'
                                },
                                {
                                    id: 840,
                                    text: 'Repository',
                                    cms: {
                                        appUrl: 'blank.html'
                                    },
                                    icon: 'app/launcher/images/database.png'
                                },
                                {
                                    id: 850,
                                    text: 'Userstores',
                                    cms: {
                                        appUrl: 'app-userstore.html'
                                    },
                                    icon: 'app/launcher/images/address-book-blue-icon.png'
                                }
                            ]
                        }
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


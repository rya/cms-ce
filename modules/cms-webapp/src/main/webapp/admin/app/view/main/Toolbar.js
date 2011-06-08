Ext.define('CMS.view.main.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',

    alias: 'widget.mainToolbar',
    id: 'cms-main-toolbar',
    items: [
        {
            id: 'cms-start-button',
            xtype: 'button',
            scale: 'medium',
            iconCls: 'cms-start-button',
            menu: [
                {
                    text: 'Dashboard',
                    iconCls: 'icon-dashboard'
                },
                {
                    text: 'Accounts',
                    iconCls: 'icon-accounts'
                },
                {
                    text: 'Content'
                },
                {
                    text: 'Applications',
                    menu: [
                        {
                            text: 'Flatskjerm'
                        },
                        {
                            text: 'Sporingsapplikasjon'
                        },
                        {
                            text: 'SkiFree'
                        }
                    ]
                },
                {
                    text: 'Reports',
                    iconCls: 'icon-report'
                },
                {
                    text: 'System',
                    iconCls: 'icon-system'
                },
                '-',
                {
                    text: 'John Doe',
                    iconCls: 'x-icon-user'
                },
                {
                    text: 'Log out',
                    iconCls: 'icon-log-out'
                }
            ]
        },
        {
            id: 'cms-selected-application-label',
            xtype: 'label',
            text: 'Dashboard'
        }
    ]

});


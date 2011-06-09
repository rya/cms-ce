Ext.define('CMS.view.main.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',

    alias: 'widget.mainToolbar',
    id: 'cms-main-toolbar',

    items: [
        {
            id: 'cms-start-button',
            xtype: 'button',
            scale: 'large',
            iconCls: 'cms-start-button',
            menu: [
                {
                    text: 'Dashboard',
                    appUrl: 'dashboard.html',
                    iconCls: 'icon-dashboard'
                },
                {
                    text: 'Accounts',
                    appUrl: 'accounts.html',
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
                    text: 'Log Out',
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


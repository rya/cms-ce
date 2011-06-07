Ext.define('CMS.view.App', {
    extend: 'Ext.ux.desktop.App',

    requires: [
        'Ext.window.MessageBox',
        'Ext.ux.desktop.ShortcutModel',

        'Ext.ux.desktop.modules.Notepad',
        'Ext.ux.desktop.modules.Settings',
        
        'CMS.view.modules.PropertyModule',
        'CMS.view.modules.LanguageModule',
        'CMS.view.modules.ContentTypeModule',
        'CMS.view.modules.AccountModule'
    ],

    init: function() {
        // custom logic before getXYZ methods get called...

        this.callParent();

        // now ready...
    },

    getModules : function(){
        return [
            new Ext.ux.desktop.modules.Notepad(),
            new CMS.view.modules.AccountModule(),
            new CMS.view.modules.ContentTypeModule(),
            new CMS.view.modules.PropertyModule(),
            new CMS.view.modules.LanguageModule()
        ];
    },

    getDesktopConfig: function () {
        var me = this, ret = me.callParent();

        return Ext.apply(ret, {
            contextMenuItems: [
                { text: 'Change Settings', handler: me.onSettings, scope: me }
            ],

            shortcuts: Ext.create('Ext.data.Store', {
                model: 'Ext.ux.desktop.ShortcutModel',
                data: [
                    { name: 'Notepad', iconCls: 'notepad-shortcut', module: 'notepad' },
                    { name: 'Accounts', iconCls: 'accounts-shortcut', module: 'accounts' },
                    { name: 'Content Types', iconCls: 'contentType-shortcut', module: 'contentTypes' },
                    { name: 'Properties', iconCls: 'properties-shortcut', module: 'properties' },
                    { name: 'Languages', iconCls: 'languages-shortcut', module: 'languages' }
                ]
            }),

            wallpaper: 'desktop/wallpapers/Desk.jpg',
            wallpaperStretch: false
        });
    },

    // config for the start menu
    getStartConfig : function() {
        var me = this, ret = me.callParent();

        return Ext.apply(ret, {
            title: 'admin',
            iconCls: 'user',
            height: 300,
            toolConfig: {
                width: 100,
                items: [
                    {
                        text:'Settings',
                        iconCls:'settings',
                        handler: me.onSettings,
                        scope: me
                    },
                    '-',
                    {
                        text:'Logout',
                        iconCls:'logout',
                        handler: me.onLogout,
                        scope: me
                    }
                ]
            }
        });
    },

    getTaskbarConfig: function () {
        var ret = this.callParent();

        return Ext.apply(ret, {
            quickStart: [
                { name: 'Notepad', iconCls: 'icon-grid', module: 'notepad' }
            ],
            trayItems: [
                { xtype: 'trayclock', flex: 1 }
            ]
        });
    },

    onLogout: function () {
        Ext.Msg.confirm('Logout', 'Are you sure you want to logout?');
    },

    onSettings: function () {
        var dlg = new Ext.ux.desktop.modules.Settings({
            desktop: this.desktop
        });
        dlg.show();
    }
});

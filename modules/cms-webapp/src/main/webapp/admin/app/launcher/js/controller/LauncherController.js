Ext.define('App.controller.LauncherController', {
    extend: 'Ext.app.Controller',

    views: ['Toolbar'],

    requires: [
        'App.LauncherToolbarHelper'
    ],

    init: function() {
        this.control({
            'viewport': {
                afterrender: this.loadDefaultApp
            },
            '*[id=launcher-logo]': {
                render: this.onLogoRendered
            },
            '*[id=launcher-start-button] menu > menuitem': {
                click: this.loadApp
            }
        });
    },

    onLogoRendered: function(component, options)
    {
        component.el.on('click', this.showAboutWindow);
    },

    loadDefaultApp: function(component, options) {
        if (!window.appLoadMask) {
            window.appLoadMask = new Ext.LoadMask(Ext.getDom('launcher-center'), {msg:"Please wait..."});
        }

        var defaultApplication = this.getStartMenuButton().menu.items.items[0];
        this.loadApp(defaultApplication, null, null);
    },

    loadApp: function(item, e, options ) {
        if (item.cms.appUrl === '') {
            item.cms.appUrl = 'blank.html'
        }

        if (!item.icon || item.icon === '') {
            item.icon = Ext.BLANK_IMAGE_URL
        }

        this.showLoadMask();
        this.setDocumentTitle(item.text);
        this.setUrlFragment(item.text);
        this.getIframe().src = item.cms.appUrl;
        this.updateStartButton(item);
    },

    showLoadMask: function() {
        window.appLoadMask.show();
    },

    updateStartButton: function(item) {
        var startMenuButton = this.getStartMenuButton();
        startMenuButton.setText(item.text);
        startMenuButton.setIcon(item.icon);
    },

    setDocumentTitle: function(title) {
        window.document.title = 'Enonic CMS Admin - ' + title;
    },

    setUrlFragment: function(fragmentId) {
        window.location.hash = fragmentId;
    },

    getIframe: function() {
        return Ext.getDom('launcher-iframe');
    },

    showAboutWindow: function() {
        var aboutWindow = Ext.ComponentQuery.query('#cms-about-window')[0];
        if (aboutWindow) {
            aboutWindow.show();
            return;
        }

        Ext.create('Ext.window.Window', {
            itemId: 'cms-about-window',
            modal: true,
            resizable: false,
            title: 'About',
            width: 550,
            height: 300
        }).show();
    },

    getStartMenuButton: function() {
        return Ext.ComponentQuery.query('launcherToolbar button[id=launcher-start-button]')[0];
    }

});

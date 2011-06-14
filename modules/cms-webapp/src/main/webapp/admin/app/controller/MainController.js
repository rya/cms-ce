Ext.define('CMS.controller.MainController', {
    extend: 'Ext.app.Controller',

    views: ['main.Toolbar'],

    refs: [
        {ref: 'appLabel', selector: 'mainToolbar label[id=main-selected-application-label]'},
        {ref: 'startMenuButton', selector: 'mainToolbar button[id=main-start-button]'},
    ],

    init: function() {
        this.control({
            'viewport': {
                afterrender: this.loadDefaultApp
            },
            '*[id=main-start-button] menu > menuitem': {
                click: this.loadApp
            }
        });
    },

    loadDefaultApp: function(component, options) {
        if (!window.appLoadMask) {
            window.appLoadMask = new Ext.LoadMask(Ext.getDom('main-center'), {msg:"Please wait..."});
        }

        var defaultApplication = this.getStartMenuButton().menu.items.items[0];
        this.loadApp(defaultApplication, null, null);
    },

    loadApp: function(item, e, options ) {
        if (item.appUrl === '') {
            item.appUrl = 'blank.html'
        }

        window.appLoadMask.show();
        window.document.title = 'Enonic CMS Admin - ' + item.text;
        this.updateAppLabel(item);
        this.getIframe().src = item.appUrl;
        this.setUrlFragment(item.text);
    },

    updateAppLabel: function(item) {
        var label = this.getAppLabel();
        var existingAppIconCls = label.el.dom.className.match(/icon-[a-z-_]+/g);
        if (existingAppIconCls) {
            label.removeCls(existingAppIconCls[0]);
        }

        label.addCls(item.iconCls);
        label.setText(item.text);
    },

    setUrlFragment: function(fragmentId) {
        window.location.hash = fragmentId;
    },

    getIframe: function() {
        return Ext.getDom('main-iframe');
    }

});

// TODO: Temporary. We should not use Ext in these documents.
Ext.define('CMS.common.Common', {
     statics: {
         hideMenus: function() {
             var componentQuery = this.getMainWindow().Ext.ComponentQuery;
             var toolbarMenuButtons = componentQuery.query('mainToolbar button[menu]');
             var menu = null;
             for (var i = 0; i < toolbarMenuButtons.length; i++) {
                 menu = toolbarMenuButtons[i].menu;
                 if (menu.isVisible(true)) {
                    menu.hide();
                 }
             }
         },

         showLoader: function() {
             var appLoadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Please wait..."});
             appLoadMask.show();
         },

         getMainWindow: function() {
             return window.parent;
         }
     },

     constructor: function() { }
});

Ext.EventManager.addListener( Ext.getDoc(), 'click', function() {
    CMS.common.Common.hideMenus();
});

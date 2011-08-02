Ext.define('CMS.common.Common', {
     statics: {
         hideLauncherMenus: function() {
             var componentQuery = Ext.ComponentQuery;
             var toolbarMenuButtons = componentQuery.query('launcherToolbar button[menu]');
             var menu = null;
             for (var i = 0; i < toolbarMenuButtons.length; i++) {
                 menu = toolbarMenuButtons[i].menu;
                 if (menu.isVisible(true)) {
                    menu.hide();
                 }
             }
         }

     },

     constructor: function() { }
});
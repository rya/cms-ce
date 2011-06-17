Ext.define('CMS.common.MainToolbar', {
     statics: {
         hideMenus: function() {
             var componentQuery = Ext.ComponentQuery;
             var toolbarMenuButtons = componentQuery.query('mainToolbar button[menu]');
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
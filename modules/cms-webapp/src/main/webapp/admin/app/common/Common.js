Ext.define('CMS.common.Common', {
     statics: {
         hideMenu: function() {
             var menu = this.getMainWindow().Ext.ComponentQuery.query('mainToolbar button[id=cms-start-button]')[0].menu;
             if (menu && menu.isVisible(true)) {
                 menu.hide();
             }
         },

         getMainWindow: function() {
             return window.parent;
         }
     },

     constructor: function() { }
});

Ext.EventManager.addListener( Ext.getDoc(), 'click', function() {
    CMS.common.Common.hideMenu();
});
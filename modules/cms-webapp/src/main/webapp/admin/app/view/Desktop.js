Ext.define('CMS.view.Desktop', {
    extend: 'Ext.ux.desktop.Desktop',
    
    alias: 'widget.cmsdesktop', 
    
    requires: [
        'CMS.view.App'
    ],
    
    initComponent: function () {
      this.app = new CMS.view.App();
      this.callParent();
    }
});
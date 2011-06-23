Ext.define('CMS.controller.CmsController', {
    extend: 'Ext.app.Controller',

    refs: [
        {ref: 'tabPanel', selector: 'cmsTabPanel'}
    ],

    init: function() {
        this.control({
            'viewport': {
                afterrender: this.createDefaultTabs
            }
        });
    },

    createDefaultTabs: function( component, options )
    {
        this.getTabPanel().addTab( {
           title: 'Content',
           xtype: 'panel'
       }, false );

        this.getTabPanel().addTab( {
           title: 'Sites',
           xtype: 'panel'
       }, false );

        this.getTabPanel().addTab( {
           title: 'Portlets',
           xtype: 'panel'
       }, false );

        this.getTabPanel().addTab( {
           title: 'Page Templates',
           xtype: 'panel'
       }, false );

        this.getTabPanel().setActiveTab(0);
    }
});

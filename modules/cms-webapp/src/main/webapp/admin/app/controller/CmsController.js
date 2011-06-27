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
            closable: false,
            xtype: 'panel'
        }, false );

        this.getTabPanel().addTab( {
            title: 'Sites',
            closable: false,
            xtype: 'panel'
        }, false );

        this.getTabPanel().addTab( {
            title: 'Portlets',
            closable: false,
            xtype: 'panel'
        }, false );

        this.getTabPanel().addTab( {
            title: 'Page Templates',
            closable: false,
            xtype: 'panel'
        }, false );

        this.getTabPanel().setActiveTab(0);
    }
});

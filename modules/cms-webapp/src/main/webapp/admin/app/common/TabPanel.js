Ext.define( 'CMS.common.TabPanel', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.cmsTabPanel',
    requires: [
        'CMS.plugin.TabCloseMenu'
    ],

    defaults: {
        closable: true
    },

    plugins: ['tabCloseMenu'],

    initComponent: function()
    {
        this.callParent( arguments );
    },

    addTab: function( item )
    {
        var tab = this.add( item );
        this.setActiveTab( tab );
    },

    removeAllTabs: function()
    {
        var all = this.items.items;
        var last = all[this.getTabCount()-1];
        while ( this.getTabCount() > 1 )
        {
            this.remove( last );
            last = this.items.items[this.getTabCount() - 1];
        }
    },

    getTabCount: function()
    {
        return this.items.items.length;
    }

} );

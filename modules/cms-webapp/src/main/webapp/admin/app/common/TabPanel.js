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

    addTab: function( items, closable )
    {
        var me = this;
        items.closable = closable !== undefined ? closable : true;
        var tab = this.add( items );

        if ( closable )
        {
            tab.on( {
                beforeclose: function( tab, options )
                {
                    me.onBeforeCloseTab( tab, options )
                }
            });
        }

        this.setActiveTab( tab );
    },

    onBeforeCloseTab: function( tab, options )
    {
        var tabToActivate = null;
        var activatePreviousTab = tab.isVisible();
        if ( activatePreviousTab )
        {
            var tabIndex = this.items.findIndex( 'id', tab.id );
            tabToActivate = this.items.items[ tabIndex - 1 ];
        }
        else
        {
            tabToActivate = this.getActiveTab();
        }

        this.setActiveTab( tabToActivate );
    },

    removeAllTabs: function()
    {
        var all = this.items.items;
        var last = all[this.getTabCount() -1 ];
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

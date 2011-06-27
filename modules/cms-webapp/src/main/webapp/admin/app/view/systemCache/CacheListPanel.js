Ext.define( 'CMS.view.systemCache.CacheListPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.cacheListPanel',
    store: 'SystemCacheStore',
    items: [],
    autoScroll: true,
    //bodyPadding: 5,
    width: '100%',
    layout: {
        type: 'table',
        columns: 1,
        tableAttrs: {
            style: {
                width: '98%'
            }
        }
    },

    requires: [
        'CMS.view.systemCache.CachePanel',
        'CMS.store.SystemCacheStore'
    ],

    initComponent: function()
    {
        this.store = Ext.data.StoreManager.lookup( this.store );
        this.mon( this.store, {
            scope: this,
            load: this.onLoad
        } );
        this.callParent( arguments );
    },

    onLoad: function()
    {
        var items = []
        Ext.Array.each( this.store.getRange(), function( item )
        {
            var itemPanel = {
                xtype: 'cachePanel',
                title: item.data.key,
                implementation: item.data.implementation,
                timeToLive: item.data.timeToLive,
                maxMemElements: item.data.maxMemElements,
                maxDiskElements: item.data.maxDiskElements,
                objectCount: item.data.objectCount,
                cacheHits: item.data.cacheHits,
                cacheMisses: item.data.cacheMisses
            };
            Ext.Array.include( items, itemPanel );
        } );
        this.add( items );

    }


} );
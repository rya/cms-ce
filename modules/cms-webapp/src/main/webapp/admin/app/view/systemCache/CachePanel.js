Ext.define( 'CMS.view.systemCache.CachePanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.cachePanel',

    title: 'Details',
    split: true,
    autoScroll: true,
    width: 300,
    height: 250,
    bodyBorder: true,
    frame: true,
    margin: 5,
    padding: 5,

    layout: {
        type: 'vbox'
    },

    buttons: [
        {
            text: 'Clear cache',
            action: 'clearCache'
        },
        {
            text: 'Clear statistics',
            action: 'clearStats'
        }
    ],

    //Custom config options

    implementation: "",
    timeToLive: 0,
    maxMemElements: 0,
    maxDiskElements: 0,
    objectCount: 0,
    cacheHits: 0,
    cacheMisses: 0,

    initComponent:function()
    {
        this.items = [
            {
                xtype: 'label',
                text: 'Implementation: ' + this.implementation
            },
            {
                xtype: 'label',
                text: 'Time to live: ' + this.timeToLive
            },
            {
                xtype: 'label',
                text: 'Maximum elements in memory: ' + this.maxMemElements
            },
            {
                xtype: 'label',
                text: 'Maximum elements on disk: ' + this.maxDiskElements
            },
            {
                xtype: 'label',
                text: 'Object count: ' + this.objectCount
            },
            {
                xtype: 'label',
                text: 'Cache hits: ' + this.cacheHits
            },
            {
                xtype: 'label',
                text: 'Cache misses: ' + this.cacheMisses
            }

        ];
        this.callParent( arguments );
    }
} );
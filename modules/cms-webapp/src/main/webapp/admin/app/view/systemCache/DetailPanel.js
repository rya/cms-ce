Ext.define('CMS.view.systemCache.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.systemCacheDetail',

    requires: [
        'CMS.view.systemCache.GaugePanel'
    ],

    title: 'Cache Details',
    split: true,
    autoScroll: true,
    defaults: {
        border: false
    },
    bodyPadding: 10,

    cache: {
        data: {
            timeToLive: '-',
            count: 0,
            size: '-',
            hits: 0,
            misses: 0
        }
    },

    tbar: {
        border: false,
        padding: 5,
        items: [
            {
                text: 'Clear Cache',
                iconCls: 'icon-delete',
                action: 'clearCache'
            },
            {
                text: 'Reset Statistics',
                iconCls: 'icon-delete',
                action: 'clearStats'
            },
                '->',
            {
                text: 'Refresh',
                itemId: 'toggleRefresh',
                iconCls: 'icon-refresh',
                pressed: true,
                enableToggle: true,
                action: 'refreshCache'
            }
        ]
    },

    initComponent: function() {

        this.items = [
            {
                xtype: 'panel',
                itemId: 'cacheDetailHeader',
                height: 40,
                anchor: '100%',
                styleHtmlContent: true,
                tpl: new Ext.XTemplate(
                    '<div class="cache-info">',
                    '<h1>{name} <span><tpl if="address != null">({address})</tpl></span></h1>',
                    '</div>'
                )
            },
            {
                xtype: 'form',
                itemId: 'cacheDetailFieldset',
                anchor: '100%',
                items: [
                    {
                        xtype: 'fieldset',
                        title: 'Info',
                        items: [
                            {
                                xtype: 'displayfield',
                                name: 'timeToLive',
                                fieldLabel: 'Time To Live'
                            },
                            {
                                xtype: 'displayfield',
                                name: 'size',
                                fieldLabel: 'Size'
                            }
                        ]
                    }
                ]
            },
            {
                xtype: 'container',
                height: 300,
                itemId: 'cacheDetailGauges',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },

                items: [
                    {
                        xtype: 'systemCacheGauge',
                        itemId: 'usageGauge',
                        title: 'Total Usage',
                        flex: 1,
                        margins: '0 5 0 0'
                    },
                    {
                        xtype: 'systemCacheGauge',
                        itemId: 'hitGauge',
                        title: 'Hit Rate',
                        flex: 1,
                        margins: '0 0 0 5',
                        gaugeColor: '#70be00'
                    }
                ]
            }
        ];

        this.callParent(arguments);

        if( this.cache )
            this.updateDetail( this.cache );

    },

    setCache: function( cache ) {
        this.cache = cache;
    },

    getCache: function() {
        return this.cache;
    },

    updateDetail: function( cache ) {
        if( cache )
            this.setCache( cache );

        var data = this.cache ? this.cache.data : {} ;
        this.getComponent('cacheDetailHeader').update( data );
        this.getComponent('cacheDetailFieldset').getForm().setValues( data );

        var gauges = this.getComponent('cacheDetailGauges');
        gauges.getComponent('usageGauge').updateData( [{
            name: 'usageGauge',
            data: Math.floor( data.count * 100 / data.size ) || 0
        }] );
        gauges.getComponent('hitGauge').updateData( [{
            name: 'hitGauge',
            data: Math.floor( data.hits * 100 / (data.hits + data.misses) ) || 0
        }] );
    }

});

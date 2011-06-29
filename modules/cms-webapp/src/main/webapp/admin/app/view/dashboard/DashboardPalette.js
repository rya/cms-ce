Ext.define('CMS.view.dashboard.DashboardPalette', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.dashboardPalette',

    requires: [
        'CMS.view.dashboard.DragSource',
        'CMS.view.dashboard.Dashlet',
        'CMS.view.dashboard.ChartDashlet',
        'CMS.view.dashboard.GridDashlet'
    ],

    title: 'Dashboard Palette',
    split: true,
    collapsible: true,
    bodyPadding: 10,
    border: false,


    initComponent: function() {
        var me = this;

        var viewStore = Ext.create("Ext.data.ArrayStore", {
            fields: [{
                name: "name"
            }, {
                name: "xtype"
            }],
            data: this.getData()
        });

        this.items = Ext.create("Ext.view.View", {
            tpl: '<tpl for=".">' +
                '<div class="palette-item">' +
                    '<div class="item-name">{name}</div>' +
                '</div>' +
             '</tpl>',
            itemSelector: 'div.palette-item',
            singleSelect: true,
            store: viewStore,
            listeners: {
                render:  {
                    fn: me.initDraggable
                }
            }
        });
        this.callParent(arguments);
    },

    initDraggable: function()
    {
        this.dd = Ext.create('CMS.view.dashboard.DragSource', this )
    },

    getData: function() {
        return [
            ["Text dash n1", "CMS.view.dashboard.Dashlet"],
            ["Grid dash n1", "CMS.view.dashboard.GridDashlet"],
            ["Chart dash n1", "CMS.view.dashboard.ChartDashlet"],
            ["Text dash n2", "CMS.view.dashboard.Dashlet"]
        ];
    }

});

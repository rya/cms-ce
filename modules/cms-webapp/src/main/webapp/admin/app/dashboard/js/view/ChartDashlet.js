Ext.define('App.view.ChartDashlet', {
    extend: 'App.view.Dashlet',
    alias: 'widget.chartDashlet',

    requires: [
        'App.view.Dashlet',
        "Ext.data.JsonStore",
        "Ext.chart.theme.Base",
        "Ext.chart.series.Series",
        "Ext.chart.series.Line",
        "Ext.chart.axis.Numeric"
    ],

    height: 300,
    autoScroll: false,
    bodyPadding: 0,
    title: 'Chart Dashlet',
    html: 'Chart dashlet text',
    listeners: {
        afterrender: function() {
            this.doLayout();
        }
    },

    getData: function () {
        var b = [{
            name: "x",
            djia: 10000,
            sp500: 1100
        }],
            a;
        for (a = 1; a < 50; a++) {
            b.push({
                name: "x",
                sp500: b[a - 1].sp500 + ((Math.floor(Math.random() * 2) % 2) ? -1 : 1) * Math.floor(Math.random() * 7),
                djia: b[a - 1].djia + ((Math.floor(Math.random() * 2) % 2) ? -1 : 1) * Math.floor(Math.random() * 7)
            })
        }
        return b
    },

    initComponent: function () {
        this.items = {
            xtype: "chart",
            animate: false,
            shadow: false,
            store: Ext.create( "Ext.data.JsonStore", {
                fields: ["name", "sp500", "djia"],
                data: this.getData()
            } ),
            legend: {
                position: "bottom"
            },
            axes: [
                {
                    type: "Numeric",
                    position: "left",
                    fields: ["djia"],
                    title: "Dow Jones Average",
                    label: {
                        font: "11px Arial"
                    }
                },
                {
                    type: "Numeric",
                    position: "right",
                    grid: false,
                    fields: ["sp500"],
                    title: "S&P 500",
                    label: {
                        font: "11px Arial"
                    }
                }
            ],
            series: [
                {
                    type: "line",
                    lineWidth: 1,
                    showMarkers: false,
                    fill: true,
                    axis: "left",
                    xField: "name",
                    yField: "djia",
                    style: {
                        "stroke-width": 1
                    }
                },
                {
                    type: "line",
                    lineWidth: 1,
                    showMarkers: false,
                    axis: "right",
                    xField: "name",
                    yField: "sp500",
                    style: {
                        "stroke-width": 1
                    }
                }
            ]
        };
        this.callParent(arguments)
    }

});

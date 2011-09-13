Ext.define('App.view.ShowPanel', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.contentTypeShow',

    requires: [
        'App.view.GridPanel',
        'App.view.DetailPanel'
    ],

    layout: 'border',
    border: false,

    initComponent: function() {
        this.items = [
            {
                region: 'center',
                xtype: 'contentTypeGrid',
                flex: 2
            },
            {
                region: 'south',
                xtype: 'contentTypeDetail',
                flex: 1
            }
        ];

        this.callParent(arguments);
    }

});

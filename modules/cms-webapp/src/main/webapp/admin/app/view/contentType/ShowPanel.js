Ext.define('CMS.view.contentType.ShowPanel', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.contentTypeShow',

    requires: [
        'CMS.view.contentType.GridPanel',
        'CMS.view.contentType.DetailPanel'
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

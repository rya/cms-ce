Ext.define('CMS.view.contentType.Show', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.contentTypeShow',

    requires: [
        'CMS.view.contentType.Grid',
        'CMS.view.contentType.Detail'
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

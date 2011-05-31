Ext.define('CMS.view.contentType.Show', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.contentTypeShow',

    requires: [
        'CMS.view.contentType.Grid'
    ],

    layout: 'border',
    border: false,

    initComponent: function() {
        this.items = [
            {
                region: 'center',
                xtype: 'contentTypeGrid',
                flex: 2
            }
        ];

        this.callParent(arguments);
    }

});

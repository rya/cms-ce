Ext.define('CMS.view.systemCache.ShowPanel', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.systemCacheShow',

    requires: [
        'CMS.view.systemCache.GridPanel',
        'CMS.view.systemCache.DetailPanel'
    ],

    layout: 'border',
    border: false,

    initComponent: function() {
        this.items = [
            {
                region: 'center',
                xtype: 'systemCacheGrid',
                flex: 1
            },
            {
                region: 'south',
                xtype: 'systemCacheDetail',
                flex: 2
            }
        ];

        this.callParent(arguments);
    }

});

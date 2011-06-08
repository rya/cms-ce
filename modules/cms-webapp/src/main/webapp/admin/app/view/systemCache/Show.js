Ext.define('CMS.view.systemCache.Show', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.systemCache',

    requires: [
        'CMS.view.systemCache.CacheListPanel'
    ],

    layout: 'border',
    border: false,

    initComponent: function() {
        this.items = [
            {
                region: 'center',
                xtype: 'cacheListPanel',
                flex: 2
            }
        ];

        this.callParent(arguments);
    }

});

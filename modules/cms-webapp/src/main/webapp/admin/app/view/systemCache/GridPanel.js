Ext.define( 'CMS.view.systemCache.GridPanel', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.systemCacheGrid',

    requires: [
        'CMS.plugin.PageSizePlugin'
    ],
    title: 'System Caches',
    layout: 'fit',
    loadMask: true,
    columnLines: true,
    frame: false,
    store: 'SystemCacheStore',
    features: [{ftype:'grouping'}],

    initComponent: function()
    {
        this.columns = [
            {
                text: 'Node',
                dataIndex: 'node',
                sortable: true,
                flex:1
            },
            {
                text: 'Name',
                dataIndex: 'name',
                sortable: true
            },
            {
                text: 'Count',
                dataIndex: 'count',
                xtype: 'numbercolumn',
                format:'0,000',
                sortable: true
            },
            {
                text: 'Size',
                dataIndex: 'size',
                xtype: 'numbercolumn',
                format:'0,000',
                sortable: true
            },
            {
                text: 'Hits',
                dataIndex: 'hits',
                xtype: 'numbercolumn',
                sortable: true
            },
            {
                text: 'Misses',
                dataIndex: 'misses',
                xtype: 'numbercolumn',
                sortable: true
            },
            {
                text: 'Time To Live',
                dataIndex: 'timeToLive',
                xtype: 'numbercolumn',
                sortable: true
            }
        ];

        this.viewConfig = {
            trackOver : true,
            stripeRows: true
        };

        this.callParent( arguments );
    }
});

Ext.define( 'App.view.GridPanel', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.contentTypeGrid',

    requires: [
        'App.view.Toolbar'
    ],

    layout: 'fit',
    loadMask: true,
    columnLines: true,
    frame: false,
    store: 'ContentTypeStore',

    initComponent: function()
    {

        this.columns = [
            {
                text: 'Name',
                dataIndex: 'name',
                sortable: true,
                flex: 10
            },
            {
                text: 'Content type no',
                dataIndex: 'key',
                sortable: true,
                align: 'center'
            },
            {
                text: 'Modified',
                xtype: 'datecolumn',
                dataIndex: 'timestamp',
                format: 'Y-m-d h:m',
                sortable: true
            }
        ];

        this.tbar = {
            xtype: 'contentTypeToolbar'
        };

        this.viewConfig = {
            trackOver : true,
            stripeRows: true
        };

        this.callParent( arguments );
    }
} );
Ext.define( 'CMS.view.contentType.Grid', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.contentTypeGrid',

    requires: [
        'CMS.view.contentType.Toolbar',
        'CMS.plugin.PageSize'
    ],

    layout: 'fit',
    loadMask: true,
    columnLines: true,
    frame: false,
    store: 'ContentTypes',

    initComponent: function()
    {

        this.columns = [
            {
                text: 'No',
                dataIndex: 'key',
                sortable: true,
                flex: 1
            },
            {
                text: 'Name',
                dataIndex: 'name',
                sortable: true,
                flex: 10
            },
            {
                text: 'Modified',
                dataIndex: 'timestamp',
                sortable: true,
                flex: 2
            }
        ];

        this.bbar = {
            xtype: 'pagingtoolbar',
            store: this.store,
            displayInfo: true,
            displayMsg: 'Displaying content types {0} - {1} of {2}',
            emptyMsg: 'No content types to display',
            plugins: ['pageSize']
        };

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
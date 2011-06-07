Ext.define('CMS.view.contentHandler.Grid', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.contentHandlerGrid',

    requires: [
        'CMS.view.contentHandler.Toolbar',
        'CMS.plugin.PageSize'
    ],
    layout: 'fit',
    loadMask: true,
    columnLines: true,
    frame: false,
    store: 'ContentHandlers',

    initComponent: function() {

        this.columns = [
            {
                text: 'Name',
                dataIndex: 'name',
                sortable: true
            },
            {
                text: 'Class',
                dataIndex: 'class',
                sortable: true
            },
            {
                text: 'Handler no',
                dataIndex: 'key',
                sortable: true
            },
            {
                text: 'Modified',
                dataIndex: 'lastModified',
                sortable: true,
                flex: 3
            }
        ];

        this.bbar = {
            xtype: 'pagingtoolbar',
            store: this.store,
            displayInfo: true,
            displayMsg: 'Displaying content handlers {0} - {1} of {2}',
            emptyMsg: 'No content handlers to display',
            plugins: ['pageSize']
        };

        this.tbar = {
            xtype: 'contentHandlerToolbar'
        };

        this.viewConfig = {
            trackOver : true,
            stripeRows: true
        };

        this.callParent(arguments);
    }

});

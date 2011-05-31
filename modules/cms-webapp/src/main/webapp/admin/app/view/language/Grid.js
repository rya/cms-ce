Ext.define('CMS.view.language.Grid', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.languageGrid',

    requires: [
        'CMS.view.language.Toolbar',
        'CMS.plugin.PageSize'
    ],
    layout: 'fit',
    loadMask: true,
    columnLines: true,
    frame: false,
    store: 'Languages',

    initComponent: function() {

        this.columns = [
            {
                text: 'Language Code',
                dataIndex: 'languageCode',
                sortable: true,
                width: 100,
                align: 'right'
            },
            {
                text: 'Description',
                dataIndex: 'description',
                sortable: true,
                flex: 1
            },
            {
                text: 'Key',
                dataIndex: 'key',
                sortable: true,
                width: 300
            }
        ];

        this.bbar = {
            xtype: 'pagingtoolbar',
            store: this.store,
            displayInfo: true,
            displayMsg: 'Displaying languages {0} - {1} of {2}',
            emptyMsg: 'No languages to display',
            plugins: ['pageSize']
        };

        this.tbar = {
            xtype: 'languageToolbar'
        };

        this.viewConfig = {
            trackOver : true,
            stripeRows: true
        };

        this.callParent(arguments);
    }

});

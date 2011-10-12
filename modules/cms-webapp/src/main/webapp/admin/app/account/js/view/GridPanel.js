Ext.define( 'App.view.GridPanel', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.accountGrid',

    requires: ['Common.PersistentGridSelectionPlugin'],
    plugins: ['persistentGridSelection'],
    layout: 'fit',
    multiSelect: true,
    loadMask: true,
    columnLines: true,
    frame: false,
    store: 'UserStore',
    /*verticalScrollerType: 'paginggridscroller',
    invalidateScrollerOnRefresh: false,*/

    initComponent: function()
    {
        this.selModel = Ext.create('Ext.selection.CheckboxModel', {
            mode: 'SIMPLE'
        });

        this.columns = [
            {
                text: 'Display Name',
                dataIndex: 'displayName',
                sortable: true,
                renderer: this.nameRenderer,
                flex: 1
            },
            {
                text: 'Local Name',
                dataIndex: 'name',
                sortable: true
            },
            {
                text: 'User Store',
                dataIndex: 'userStore',
                sortable: true
            },
            {
                text: 'Last Modified',
                xtype: 'datecolumn',
                dataIndex: 'lastModified',
                format: 'Y-m-d h:m',
                sortable: true
            }
        ];

        this.dockedItems = [
            {
                xtype: 'browseToolbar',
                dock: 'top'
            },
            {
                xtype: 'pagingtoolbar',
                store: this.store,
                displayInfo: true,
                displayMsg: 'Displaying {0} - {1} of {2} users'
            }
        ];

        this.viewConfig = {
            trackOver : true,
            stripeRows: true
        };

        this.callParent( arguments );
    },

    nameRenderer: function( value, p, record )
    {
        return Ext.String.format(
                Templates.account.gridPanelNameRenderer,
                record.data.key,
                value,
                record.data.name,
                record.data.userStore
                );
    }
});

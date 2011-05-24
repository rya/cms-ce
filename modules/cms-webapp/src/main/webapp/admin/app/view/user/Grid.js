Ext.define('CMS.view.user.Grid', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.userGrid',

    requires: [
        'CMS.view.user.Toolbar'
    ],
    layout: 'fit',
    loadMask: true,
    columnLines: true,
    frame: false,
    store: 'Users',

    /*verticalScrollerType : 'paginggridscroller',
     invalidateScrollerOnRefresh : false,
     */

    initComponent: function() {

        // this.selModel = Ext.create('Ext.selection.CheckboxModel');

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
                dataIndex: 'lastModified',
                sortable: true,
                xtype: 'datecolumn',
                format: 'm-d-Y'
            }
        ];

        this.bbar = {
            xtype: 'pagingtoolbar',
            store: this.store,
            displayInfo: true,
            displayMsg: 'Displaying users {0} - {1} of {2}',
            emptyMsg: 'No users to display'
        };

        this.tbar = {
            xtype: 'userToolbar'
        };

        this.viewConfig = {
            trackOver : true,
            stripeRows: true
        };

        this.callParent(arguments);
    },

    nameRenderer: function(value, p, record) {
        return Ext.String.format(
                '<img src="photo?key={0}&thumb=1" style="float:left;margin-right:6px"><b>{1}</b><br><i>{2}</i> in user store <i>{3}</i>',
                record.data.key,
                value,
                record.data.name,
                record.data.userStore
                );
    }
});

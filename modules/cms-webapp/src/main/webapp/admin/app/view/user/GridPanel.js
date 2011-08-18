Ext.define( 'CMS.view.user.GridPanel', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.userGrid',

    requires: [
        'CMS.plugin.PageSizePlugin'
    ],
    layout: 'fit',
    multiSelect: true,
    loadMask: true,
    columnLines: true,
    frame: false,
    store: 'UserStore',

    initComponent: function()
    {
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

        this.tbar = {
            xtype: 'toolbar',
            items: [
                {
                    text: 'New User',
                    iconCls: 'icon-user-add',
                    action: 'newUser'
                },
                {
                    text: 'New Group',
                    iconCls: 'icon-group-add',
                    action: 'newGroup'
                }
            ]
        };

        this.bbar = {
            xtype: 'pagingtoolbar',
            store: this.store,
            displayInfo: true,
            displayMsg: 'Displaying users {0} - {1} of {2}',
            emptyMsg: 'No users to display',
            plugins: ['pageSize']
        };

        this.viewConfig = {
            trackOver : true,
            stripeRows: true
        };

        this.callParent( arguments );
    },

    nameRenderer: function( value, p, record )
    {
        return Ext.String.format(
                '<img src="data/user/photo?key={0}&thumb=true" class="thumbnail"><strong>{1}</strong><br><em>{2}</em> in user store <strong>{3}</strong>',
                record.data.key,
                value,
                record.data.name,
                record.data.userStore
                );
    }
});

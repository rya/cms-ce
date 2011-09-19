Ext.define( 'App.view.GridPanel', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.userGrid',

    layout: 'fit',
    multiSelect: true,
    loadMask: true,
    columnLines: true,
    frame: false,
    store: 'UserStore',

    verticalScrollerType: 'paginggridscroller',
    invalidateScrollerOnRefresh: false,

    initComponent: function()
    {
        this.selModel = Ext.create('Ext.selection.CheckboxModel');

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

        this.viewConfig = {
            trackOver : true,
            stripeRows: true
        };

        this.callParent( arguments );
    },

    nameRenderer: function( value, p, record )
    {
        return Ext.String.format(
                '<div style="float:left"><img src="data/user/photo?key={0}&thumb=true" class="cms-thumbnail"></div>' +
                        '<div style="float:left"><div class="cms-grid-title">{1}</div>' +
                        '<div class="cms-grid-description"><span class="cms-emphasis">{2}</span> in user store {3}</div>',
                record.data.key,
                value,
                record.data.name,
                record.data.userStore
                );
    }
});

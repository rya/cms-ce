Ext.define('CMS.view.property.Grid', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.propertyGrid',

    layout: 'fit',
    loadMask: true,
    columnLines: true,
    frame: false,
    store: 'Properties',

    initComponent: function() {

        this.columns = [
            {
                text: 'Name',
                dataIndex: 'name',
                sortable: true,
                flex: 1
            },
            {
                text: 'Value',
                dataIndex: 'value',
                sortable: true,
                flex: 1
            }
        ];


        this.viewConfig = {
            trackOver : true,
            stripeRows: true
        };

        this.callParent(arguments);
    }
});

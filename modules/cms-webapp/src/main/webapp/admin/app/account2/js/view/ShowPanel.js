Ext.define( 'App.view.ShowPanel', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.accountShow',

    layout: 'border',
    border: false,
    padding: 5,

    initComponent: function()
    {
        this.items = [
            {
                region: 'center',
                xtype: 'accountGrid',
                flex: 2
            },
            {
                region: 'south',
                xtype: 'accountDetail',
                flex: 1
            }
        ];

        this.callParent( arguments );
    }

} );

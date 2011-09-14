Ext.define('App.view.ShowPanel', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.userShow',


    layout: 'border',
    border: false,
    padding: 0,

    initComponent: function() {
        this.items = [
            {
                region: 'center',
                xtype: 'userGrid',
                flex: 2
            },
            {
                region: 'south',
                xtype: 'userDetail',
                flex: 1
            }
        ];

        this.callParent(arguments);
    }

});

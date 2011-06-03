Ext.define('CMS.view.user.Show', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.userShow',

    requires: [
        'CMS.view.user.Grid',
        'CMS.view.user.DetailPanel'
    ],

    layout: 'border',
    border: false,

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

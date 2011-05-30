Ext.define('CMS.view.property.Show', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.propertyShow',

    requires: [
        'CMS.view.property.Grid'
    ],

    layout: 'border',
    border: false,

    initComponent: function() {
        this.items = [
            {
                region: 'center',
                xtype: 'propertyGrid',
                flex: 2
            }
        ];

        this.callParent(arguments);
    }

});

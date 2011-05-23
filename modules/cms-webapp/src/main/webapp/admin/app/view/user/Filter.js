Ext.define('CMS.view.user.Filter', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userFilter',

    title: 'Filter',
    split: true,
    collapsible: true,

    layout: {
        type: 'hbox',
        padding: 10
    },

    items: [{
        xtype: 'textfield',
        name: 'filter',
        flex: 1
    },{
        xtype: 'button',
        icon: 'resources/images/find.png',
        action: 'search',
        margins: '0 0 0 5'
    }],

    initComponent: function() {
        this.callParent(arguments);
    }
});

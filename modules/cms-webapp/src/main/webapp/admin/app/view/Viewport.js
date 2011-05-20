Ext.define('CMS.view.Viewport', {
    extend: 'Ext.container.Viewport',

    requires: [
        'CMS.view.user.Show',
        'CMS.view.user.Filter',
        'Ext.layout.container.Border'
    ],

    layout: 'border',
    padding: 5,

    items: [
        {
            region: 'center',
            xtype: 'userShow'
        },
        {
            region: 'west',
            width: 225,
            xtype: 'userFilter'
        }
    ]
});

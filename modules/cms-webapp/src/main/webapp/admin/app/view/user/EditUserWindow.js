Ext.define( 'CMS.view.user.EditUserWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.editUserWindow',

    requires: [
        'CMS.view.user.EditUserPropertiesPanel',
        'CMS.view.user.EditUserPreferencesPanel',
        'CMS.view.user.EditUserMembershipPanel'
    ],

    defaults: {
        bodyPadding: 10
    },

    items: [
        {
            xtype: 'tabpanel',
            width: 600,
            height: 700,
            autoScroll: true,
            items: [
                {
                    xtype: 'editUserPanel',
                    autoScroll: true
                },
                {
                    xtype: 'editUserPropertiesPanel'
                },
                {
                    xtype: 'editUserMembershipPanel'
                },
                {
                    xtype: 'editUserPreferencesPanel'
                }
            ]
        }

    ],



    title: 'Edit user',
    modal: true,

    layout: {
        type: 'table',
        columns: 2,
        align: 'stretch',
        defaultMargins: {top:10, right:10, bottom:10, left:10},
        padding: 10
    },

    initComponent: function()
    {
        this.callParent( arguments );
    },

    doShow: function()
    {
        this.show();

    }

} );


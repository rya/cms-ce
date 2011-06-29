Ext.define( 'CMS.view.user.EditUserPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.editUserPanel',

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
            region: 'center',
            height: '99.9%',
            width: '100%',
            items: [
                {
                    xtype: 'editUserFormPanel',
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



    modal: true,

    layout: {
        type: 'border',
        defaultMargins: {top:10, right:10, bottom:10, left:10},
        padding: 10
    },

    initComponent: function()
    {
        this.callParent( arguments );
    }

} );


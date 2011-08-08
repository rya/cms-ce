Ext.define( 'CMS.view.user.EditUserPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.editUserPanel',

    requires: [
        'CMS.view.user.EditUserPropertiesPanel',
        'CMS.view.user.EditUserPreferencesPanel',
        'CMS.view.user.EditUserMembershipPanel',
        'CMS.view.user.UserPreferencesPanel'
    ],

    autoScroll: true,

    items: [],
    currentUser: '',



    modal: true,

    layout: {
        type: 'border'
    },

    height: 800,

    initComponent: function()
    {
        var me = this;
        var editUserFormPanel = {
            xtype: 'editUserFormPanel',
            region: 'center',
            userFields: [],
            autoScroll: true,
            currentUser: me.currentUser,
            flex: 1
        };
        var prefPanel = {
            xtype: 'userPreferencesPanel',
            region: 'east',
            flex: 0.25
        };
        var shortDescPanel = {
            xtype: 'panel',
            layout: {
                type: 'border'
            },
            region: 'north',
            measureWidth: true,
            measureHeight: true,
            flex: 0.1,
            defaults: {
                bodyPadding: 5
            },

            items: [
                {
                    xtype: 'image',
                    cls: 'thumbnail',
                    region: 'west',
                    flex: 0.065,
                    src: 'data/user/photo?key=' + me.currentUser.key
                },
                {
                    layout: 'border',
                    region: 'center',
                    flex: 1,
                    items: [
                        {
                            xtype: 'userFormField',
                            type: 'text',
                            region: 'center',
                            fieldname: 'display-name',
                            currentUser: me.currentUser,
                            required: true,
                            fieldValue: me.currentUser.displayName,
                            flex: 1
                        },
                        {
                            xtype: 'displayfield',
                            region: 'south',
                            flex: 0.25,
                            value: me.currentUser.qualifiedName
                        }
                    ]
                }
            ]};
        if ( this.userFields != null )
        {
            editUserFormPanel.userFields = this.userFields;
        }
        me.items = [ editUserFormPanel, prefPanel, shortDescPanel ];
        this.callParent( arguments );

    }

} );


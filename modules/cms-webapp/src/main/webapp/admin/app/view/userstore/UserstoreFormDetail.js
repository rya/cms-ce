Ext.define( 'CMS.view.userstore.UserstoreFormDetail', {
    extend: 'Ext.form.Panel',
    alias: 'widget.userstoreFormDetail',

    layout: 'accordion',
    defaults: {
        bodyPadding: 10
    },

    localConnectorName: 'local',

    items: [
        {
            title: 'Detail',
            layout: 'anchor',
            items: [
                {
                    xtype: 'fieldset',
                    title: 'Userstore',
                    defaults: {
                        xtype: 'displayfield',
                        anchor: '100%',
                        readOnly: true
                    },
                    items: [
                        {
                            fieldLabel: 'Users',
                            name: 'userCount'
                        },{
                            fieldLabel: 'Groups',
                            name: 'groupCount'
                        },
                        {
                            fieldLabel: 'Last modified',
                            name: 'lastModified'
                        }
                    ]
                },{
                    xtype: 'fieldset',
                    title: 'Connector',
                    defaults: {
                        xtype: 'displayfield',
                        anchor: '100%',
                        readOnly: true
                    },
                    items: [
                        {
                            fieldLabel: 'Name',
                            name: 'connectorName'
                        },{
                            fieldLabel: 'Plugin',
                            name: 'plugin'
                        },
                        {
                            fieldLabel: 'User Policy',
                            name: 'userPolicy'
                        },
                        {
                            fieldLabel: 'Group Policy',
                            name: 'groupPolicy'
                        }
                    ]
                }
            ]
        },
        {
            title: 'Synchronize',
            itemId: 'syncPanel',
            items: [
                {
                    xtype: 'fieldset',
                    title: 'Options',
                    items: [
                        {
                            xtype: 'radiogroup',
                            columns: 1,
                            name: 'syncType',
                            vertical: true,
                            items: [
                                { boxLabel: 'Users And Groups', inputValue: 'ug' },
                                { boxLabel: 'Users Only', inputValue: 'u' },
                                { boxLabel: 'Groups Only', inputValue: 'g', checked: true }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'button',
                    text: 'Synchronize',
                    action: 'syncUserstore'
                }
            ]
        }
    ],

    initComponent: function() {

        this.callParent( arguments );

        this.setUserstore( this.userstore )
    },

    setUserstore: function ( u ) {
        this.userstore = u || { data: {} };
        this.getForm().setValues( this.userstore.data );

        if ( Ext.isEmpty( this.userstore.data.key )
                || Ext.isEmpty( this.userstore.data.connectorName )
                || this.userstore.data.connectorName == this.localConnectorName ) {
            this.setSyncDisabled( true );
        }
    },

    setSyncDisabled: function( flag ) {
        this.child('#syncPanel').setDisabled( flag );
    }

});

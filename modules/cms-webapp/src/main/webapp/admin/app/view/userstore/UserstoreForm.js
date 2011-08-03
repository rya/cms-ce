Ext.define( 'CMS.view.userstore.UserstoreForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.userstoreForm',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    border: false,

    items: [
        {
            xtype: 'panel',
            cls: 'userstore-info',
            styleHtmlContent: true,
            itemId: 'headerPanel',
            tpl: new Ext.XTemplate(
                '<img src="data/user/photo?key={0}&thumb=true" class="thumbnail">',
                '<div class="userstore-info">',
                '<h2 style="margin: 0;">{name}</h2>',
                '<em>{connectorName}</em>',
                '</div>'
            ),
            border: false,
            bodyPadding: 10,
            margin: {
                bottom: 10
            },
            bbar: [
                {
                    text: 'Save',
                    iconCls: 'icon-save',
                    action: 'saveUserstore'
                },
                {
                    text: 'Cancel',
                    iconCls: 'icon-cancel',
                    action: 'cancelUserstore'
                },
                    '->',
                {
                    text: 'Delete',
                    iconCls: 'icon-delete',
                    action: 'deleteUserstore'
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: 'Userstore',
            layout: 'anchor',
            defaults: {
                anchor: '100%',
                enableKeyEvents: true
            },
            items: [
                {
                    xtype: 'textfield',
                    fieldLabel: 'Name',
                    name: 'name',
                    vtype: 'alphanum',
                    allowBlank: false
                },{
                    xtype: 'combo',
                    fieldLabel: 'Connector',
                    name: 'connectorName',
                    queryMode: 'local',
                    displayField: 'name',
                    valueField: 'id',
                    store : [ [ '1', 'Standard (local)' ] ],
                    allowBlank: false
                },{
                    xtype: 'checkbox',
                    fieldLabel: 'Set as default',
                    name: 'defaultStore'
                }
            ]
        },{
            xtype: 'fieldset',
            title: 'Form configuration',
            layout: 'fit',
            flex: 1,
            style: 'padding-bottom: 30',
            items: [
                {
                    xtype: 'textarea',
                    fieldLabel: 'XML',
                    name: 'configXML'
                }
            ]
        }
    ],


    initComponent: function() {

        this.callParent( arguments );

        if ( this.userstore ) {
            this.setUserstore( this.userstore );
            this.updateUserstoreHeader( this.userstore );
        }
    },

    setUserstore: function ( u ) {
        this.userstore = u;
        this.getForm().setValues( u.data );
    },

    updateUserstoreHeader: function ( u ) {
        this.child('#headerPanel').update( u.data );
    }

});
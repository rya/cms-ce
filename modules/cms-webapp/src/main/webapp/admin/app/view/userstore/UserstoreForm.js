Ext.define( 'CMS.view.userstore.UserstoreForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.userstoreForm',

    layout: 'anchor',
    border: false,

    items: [
        {
            xtype: 'panel',
            styleHtmlContent: true,
            itemId: 'headerPanel',
            tpl: new Ext.XTemplate(
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
                    action: 'saveUserstore'
                },
                {
                    text: 'Cancel',
                    action: 'cancelUserstore'
                },
                    '->',
                {
                    text: 'Delete',
                    action: 'deleteUserstore'
                }
            ]
        },
        {
            xtype: 'fieldset',
            title: 'Userstore',
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
                    xtype: 'textfield',
                    fieldLabel: 'Connector',
                    name: 'connectorName',
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
            defaults: {
                anchor: '100%'
            },
            items: [
                {
                    xtype: 'textarea',
                    fieldLabel: 'XML',
                    name: 'configXML',
                    height: 500
                }
            ]
        }
    ],


    initComponent: function() {

        this.callParent( arguments );

        if ( this.userstore ) {
            this.setUserstore( this.userstore );
        }
    },

    setUserstore: function ( u ) {
        this.userstore = u;
        this.child('#headerPanel').update( u.data );
        this.getForm().setValues( u.data );
    }

});
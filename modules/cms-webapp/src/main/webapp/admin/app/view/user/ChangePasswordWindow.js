Ext.define( 'CMS.view.user.ChangePasswordWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.userChangePasswordWindow',

    title: 'Change Password',
    modal: true,

    layout: 'fit',
    resizable: true,

    width: 400,
    height: 295,

    minWidth: 400,
    minHeight: 295,

    initComponent: function()
    {
        var textPanel = {
            xtype: 'container',
            layout: 'vbox',
            margins: '0 0 0 8',
            height: 100,
            flex: 1,

            items: [
                {
                    xtype: 'label',
                    flex: 1
                },
                {
                    id: 'name',
                    xtype: 'label',
                    style: 'font-weight: bold'
                },
                {
                    id: 'email',
                    xtype: 'box'
                }
            ]
        };

        var textAndPhotoPanel = {
            xtype: 'container',
            layout: 'hbox',

            width: 320,
            padding: '20 0 20 0',

            items: [
                {
                    xtype: 'image',
                    id: 'photo',
                    padding: '0 0 0 70'
                },
                textPanel
            ]
        };

        var form = {
            id: 'userChangePasswordForm',
            xtype: 'form',

            bodyStyle: 'padding: 10 30 30 10;',

            layout: {
                type: 'vbox',
                align: 'stretch'
            },

            defaults: {
                xtype: 'textfield',
                inputType: 'password',
                allowBlank: false,
                minLength: 3,
                maxLength: 64,

                labelWidth: 110,
                labelAlign: 'right'
            },

            items: [textAndPhotoPanel,
                {
                    itemId: 'password1',
                    fieldLabel: 'New password'
                }, {
                    itemId: 'password2',
                    fieldLabel: 'Confirm password',
                    validator: function( value )
                    {
                        var password1 = this.previousSibling( '#password1' );
                        return (value === password1.getValue()) ? true : 'Passwords do not match.'
                    }
                }]
        };


        Ext.apply( this, {
            items: [form],

            buttons: [
                {
                    text: 'Cancel',
                    handler: this.close,
                    scope: this
                },
                {
                    text: 'Change password',
                    handler: this.doChange
                }
            ]
        } );


        this.callParent( arguments );
    },

    doShow: function( model )
    {
        var data = model.data;

        this.down( '#photo' ).setSrc( 'data/user/photo?key=' + data.key + '&thumb=false' );
        this.down( '#name' ).setText( data.displayName + ' (' + data.qualifiedName + ')' );
        this.down( '#email' ).autoEl = {tag: 'a', href: 'mailto:' + data.email, html: data.email};

        this.show();

        this.down( '#password1' ).focus( '', 10 );
    },


    doChange: function( e )
    {
        var form = Ext.getCmp( 'userChangePasswordForm' ).getForm();
        if ( form.isValid() )
        {
            Ext.Msg.alert( 'Change Password', 'TODO' );
        }
    }

} );

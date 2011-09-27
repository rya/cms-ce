Ext.define( 'App.view.ChangePasswordWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.userChangePasswordWindow',

    title: 'Change Password',
    width: 350,
    plain: true,
    modal: true,

    initComponent: function()
    {
        var tplHtml = '<div class="cms-change-password-form">'
                                +'<div class="cms-user-info clearfix">'
                                +'<div class="cms-user-photo cms-left">'
                                +'<img alt="User" src="data/user/photo?key={key}&thumb=true"/>'
                                +'</div>'
                                +'<div class="cms-left">'
                                +'{displayName}<br/>'
                                +'({qualifiedName})<br/>'
                                +'<a href="mailto:{email}">{email}</a>'
                                +'</div>'
                                +'</div>'
                                +'</div>';

        this.items = [
            {
                id: 'userChangePasswordUserInfo',
                bodyPadding: 10,
                border: false,
                html: '',
                listeners: {
                    'render': function() {
                        new Ext.XTemplate( tplHtml ).overwrite(this.body, this.up().modelData );
                    }
                }
            },
            {
                xtype: 'form',
                id: 'userChangePasswordForm',
                method: 'POST',
                url: 'data/user/changepassword',
                bodyPadding: '0 10 10 10',
                bodyCls: 'cms-no-border',
                layout: 'anchor',
                defaults: {
                    xtype: 'textfield',
                    anchor: '100%',
                    inputType: 'password',
                    allowBlank: false
                },
                items: [{
                    fieldLabel: 'New Password',
                    name: 'cpw_password',
                    id: 'cpw_password',
                    allowBlank: false
                },{
                    fieldLabel: 'Confirm Password',
                    name: 'cpw_password2',
                    submitValue: false,
                    allowBlank: false
                }]
            }
        ];

        this.buttons = [
            {
                text: 'Cancel',
                handler: function() {
                    this.up('window').close();
                }
            },
            {
                text: 'Change Password',
                disabled: true,
                handler: function() {
                    var form = Ext.getCmp( 'userChangePasswordForm' ).getForm();
                    if ( form.isValid() )
                    {
                        form.submit();
                    }
                }
            }
        ];

        this.listeners = {
            afterrender: function() {
                Ext.getCmp('cpw_password').focus();
            }
        },

        this.callParent( arguments );
    },

    doShow: function( model )
    {
        this.modelData = model.data;
        this.show();
    },

    doChange: function( e )
    {

    }

} );

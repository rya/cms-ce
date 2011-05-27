Ext.define('CMS.view.user.ChangePassword', {
    extend: 'Ext.window.Window',
    alias: 'widget.userChangePasswordWindow',

    title: 'Change Password',
    modal: true,

    initComponent: function() {

        var form = {
            id: 'userChangePasswordForm',
            xtype: 'form',
            bodyStyle: 'padding: 10px;',

            defaults: {
                xtype: 'textfield',
                inputType: 'password',
                allowBlank: false,
                minLength: 3,
                maxLength: 64,

                enableKeyEvents: true,

                labelWidth: 150
            },

            items: [{
                itemId: 'password1',
                fieldLabel: 'New password'
            }, {
                itemId: 'password2',
                fieldLabel: 'Confirm new password',
                validator: function(value) {
                    var password1 = this.previousSibling('#password1');
                    return (value === password1.getValue()) ? true : 'Passwords do not match.'
                }
            }]
        };


		Ext.apply(this, {
			items: [form],

            buttons: [
                {
                    text: 'Cancel',
                    handler: this.close,
                    scope: this
                },
                {
                    text: 'Change',
                    handler: this.doChange
                }
            ]
		});


        this.callParent(arguments);
    },

    doShow: function(model) {
        this.title = 'Change Password >> ' + model.data.displayName;
        this.show();

        this.down('#password1').focus('', 10);

        var el = Ext.getCmp('userChangePasswordForm');
        el.on('keyup', alert, this);
    },


    doChange: function(e) {
        var form = Ext.getCmp('userChangePasswordForm').getForm();
        if (form.isValid()) {
            Ext.Msg.alert('Change Password', 'TODO');
        }
    }

});

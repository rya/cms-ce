Ext.define('CMS.view.user.ChangePassword', {
    extend: 'Ext.window.Window',
    alias: 'widget.userChangePasswordWindow',

    height: 130,
    width: 400,

    title: 'Change Password',
    modal: true,

    initComponent: function() {
        this.callParent(arguments);
    },

    doShow: function(model) {
        this.title = 'Change Password >> ' + model.data.displayName;
        this.show();
    }

});


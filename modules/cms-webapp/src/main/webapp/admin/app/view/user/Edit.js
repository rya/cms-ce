Ext.define('CMS.view.user.Edit', {
    extend: 'Ext.window.Window',
    alias: 'widget.userEditWindow',

    height: 130,
    width: 400,

    title: 'Edit User',
    modal: true,

    initComponent: function() {
        this.callParent(arguments);
    },

    doShow: function(model) {
        this.title = 'Edit User >> ' + model.data.displayName;
        this.show();
    }

});


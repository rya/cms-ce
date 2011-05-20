Ext.define('CMS.view.user.Delete', {
    extend: 'Ext.window.Window',
    alias: 'widget.userDeleteWindow',

    height: 130,
    width: 400,

    title: 'Delete User',
    modal: true,

    initComponent: function() {
        this.callParent(arguments);
    },

    doShow: function(model) {
        this.title = 'Delete User >> ' + model.data.displayName;
        this.show();
    }

});


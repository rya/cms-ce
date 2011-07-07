Ext.define( 'CMS.view.user.UserMembershipWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.userMembershipWindow',

    title: 'Groups',
    modal: true,

    width: 600,
    height: 400,

    layout: 'fit',

    initComponent: function()
    {
        this.items = [
            {
                xtype: 'membershipGridPanel'
            }];

        this.callParent( arguments );
    },

    doShow: function(){
        this.show();
    }

} );
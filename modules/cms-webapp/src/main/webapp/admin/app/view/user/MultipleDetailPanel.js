Ext.define('CMS.view.user.MultipleDetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.multipleUserDetail',

    layout: 'column',

    initComponent: function(){
        var me = this;
        var userPaneArray = [];
        Ext.Array.each(me.userArray, function(user){
            Ext.Array.include(userPaneArray, me.createUserPane(user));
        });
        me.items = userPaneArray;
        this.callParent(arguments);
    },

    createUserPane: function (userData){
        var pane = {
            xtype: 'fieldcontainer',
            layout: 'hbox',
            items: [
                {
                    xtype: 'image',
                    src: 'data/user/photo?key=' + userData.key + '&thumb=true'
                },
                {
                    xtype: 'panel',
                    layout: 'vbox',
                    items: [
                        {
                            xtype: 'displayfield',
                            value: 'User Name'
                        },
                        {
                            xtype: 'displayfield',
                            value: 'userstore/username'
                        }]
                },
                {
                    xtype: 'button',
                    iconCls: 'icon-delete-user'
                }]
        };
        return pane;
    }

})
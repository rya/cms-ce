Ext.define('App.view.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userstoreDetail',

    title: 'Details',
    split: true,
    autoScroll: true,

    initComponent: function() {

        this.tpl = new Ext.XTemplate( Templates.userstore.detailPanelInfo );

        this.dockedItems = [
            {
                dock: 'top',
                xtype: 'toolbar',
                border: false,
                padding: 5,
                items: [
                    {
                        text: 'Edit Userstore',
                        iconCls: 'icon-edit',
                        action: 'editUserstore'
                    },
                    {
                        text: 'Delete Userstore',
                        iconCls: 'icon-delete',
                        action: 'deleteUserstore'
                    },
                        '-',
                    {
                        text: 'Synchronize',
                        iconCls: 'icon-refresh',
                        action: 'syncUserstore'
                    }
                ]
            }
        ];

        this.callParent(arguments);
    },

    setCurrentUserstore: function(userstore){
        this.currentUserstore = userstore;
    },

    getCurrentUserstore: function(){
        return this.currentUserstore;
    }

});

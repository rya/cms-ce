Ext.define('App.view.UserShortDetailButton', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userShortDetailButton',

    layout: 'column',
    margin: 5,
    bodyStyle: {
        background: 'lightGrey'
    },

    initComponent: function(){
        var iconPane = {
            xtype: 'panel',
            layout: 'fit',
            border: 0,
            items: [
                {
                    xtype: 'image',
                    style: {
                        background: 'lightGrey'
                    },
                    src: 'app/account/images/user_add.png',
                    padding: 5
                }]
        };
        var displayNamePane = {
            xtype: 'panel',
            border: 0,
            layout: 'fit',
            margin: 5,
            bodyStyle: {
                background: 'lightGrey'
            },
            items: [
                {
                    xtype: 'displayfield',
                    style: {
                        background: 'lightGrey'
                    },
                    value: this.userData.get('displayName')
                }
            ]
        };
        var buttonPane = {
            xtype: 'panel',
            border: 0,
            bodyStyle: {
                background: 'lightGrey'
            },
            margin: {left: 5, right: 0, bottom: 5, top: 5},
            items: [{
                xtype: 'button',
                iconCls: 'icon-delete',
                action: 'deselectItem'
            }]
        };
        this.items = [iconPane, displayNamePane, buttonPane];
        this.callParent(arguments);
    },

    getUser: function(){
        return this.userData;
    }


})
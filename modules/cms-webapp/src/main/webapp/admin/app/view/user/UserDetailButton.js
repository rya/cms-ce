Ext.define('CMS.view.user.UserDetailButton', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userDetailButton',

    layout: 'column',
    margin: 5,
    bodyStyle: {
        background: 'lightGrey'
    },

    initComponent: function(){
        var displayNamePane = {
            xtype: 'panel',
            border: 0,
            height: 25,
            layout: 'fit',
            bodyStyle: {
                background: 'lightGrey'
            },
            items: [
                {
                    xtype: 'displayfield',
                    style: {
                        background: 'lightGrey'
                    },
                    cls: 'cms-display-name',
                    value: this.userData.get('displayName')
                }
            ]
        };
        var qNamePane = {
            xtype: 'panel',
            border: 0,
            height: 15,
            layout: 'fit',
            items: [
                {
                    xtype: 'displayfield',
                    style: {
                        background: 'lightGrey'
                    },
                    value: '(' + this.userData.get('userStore') + '/' + this.userData.get('name') + ')'
                }
            ]
        };
        var imagePane = {
            xtype: 'panel',
            layout: 'fit',
            border: 0,
            items: [
                {
                    xtype: 'image',
                    style: {
                        background: 'lightGrey'
                    },
                    src: 'data/user/photo?key=' + this.userData.get('key') + '&thumb=true',
                    padding: 5
                }]
        };
        var namePane = {
            xtype: 'panel',
            border: 0,
            padding: 5,
            bodyStyle: {
                background: 'lightGrey'
            },
            items: [displayNamePane, qNamePane]
        }
        var buttonPane = {
            xtype: 'panel',
            border: 0,
            bodyStyle: {
                background: 'lightGrey'
            },
            margin: {left: 10, right: 5, bottom: 15, top: 15},
            items: [{
                xtype: 'button',
                iconCls: 'icon-delete',
                action: 'deselectItem'
            }]
        };
        this.items = [imagePane, namePane, buttonPane];
        this.callParent(arguments);
    },

    getUser: function(){
        return this.userData;
    }


})
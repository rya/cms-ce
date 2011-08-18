Ext.define('CMS.view.user.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userDetail',

    title: 'Details',
    split: true,
    autoScroll: true,

    measureHeight: true,
    measureWidth: true,

    layout: 'fit',

    initComponent: function() {

        this.items = [
            {
                region: 'center'
            }];
        this.dockedItems = [
            {
                dock: 'top',
                xtype: 'toolbar',
                border: false,
                padding: 5,
                items: [
                    {
                        text: 'Edit User',
                        iconCls: 'icon-edit-user',
                        action: 'edit'
                    },
                    {
                        text: 'Delete User',
                        iconCls: 'icon-delete-user',
                        action: 'showDeleteWindow'
                    },
                    '-',
                    {
                        text: 'Change Password',
                        iconCls: 'icon-change-password',
                        action: 'changePassword'
                    }
                ]
            }
        ];

        this.callParent(arguments);
    },

    updateDetails: function(user){
        var userFields = ['username', 'email'];
        var nameFields = ['prefix', 'first-name', 'middle-name',
                          'last-name', 'suffix', 'initials', 'nick-name'];
        var userFieldSet = this.generateFieldSet('User', userFields, user);
        var nameFieldSet = this.generateFieldSet('Name', nameFields, user);
        var photoPanel = {
                    xtype: 'panel',
                    measureWidth: true,
                    layout: {
                        type: 'hbox',
                        align: 'stretchmax'
                    },
                    items: [
                        {
                            xtype: 'image',
                            border: 1,
                            src: 'data/user/photo?key=' + user.key + '&thumb=true'
                        },
                        {
                            layout: 'vbox',
                            border: 0,
                            flex: 1,
                            items: [
                                {
                                    xtype: 'displayfield',
                                    value: user['display-name']
                                },
                                {
                                    xtype: 'displayfield',
                                    value: user['qualifiedName']
                                }]
                        }]};
        var items = [];
        Ext.Array.include(items, photoPanel);
        if (userFieldSet != null){
            Ext.Array.include(items, userFieldSet);
        }
        if (nameFieldSet != null){
            Ext.Array.include(items, nameFieldSet);
        }
        var userInfoPane = {
            xtype: 'panel',
            region: 'center',
            autoScroll: true,
            flex: 1,
            padding: 5,
            layout: {
                type: 'table',
                columns: 1,
                tableAttrs:{
                    style: {
                        width: '97%'
                    }
                },
                tdAttrs: {
                    style:{
                        padding: '10px'
                    }
                }
            },
            items: items};
        var infoFieldSet = {
                    xtype: 'fieldset',
                    title: 'Info',
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: 'Last logged in',
                            value: user.lastLogged
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: 'Created',
                            value: user.created
                        }]
                };
        var groupFieldSet = this.generateGroupsFieldSet(user);
        var detailsPanelItems = [];
        if (groupFieldSet != null){
            detailsPanelItems = [infoFieldSet, groupFieldSet];
        }else{
            detailsPanelItems = [infoFieldSet];
        }
        var detailsPanel = {
            xtype: 'panel',
            title: 'Details',
            autoScroll: true,
            layout: {
                type: 'table',
                columns: 1,
                tdAttrs: {
                    style:{
                        padding: '10px'
                    }
                }
            },
            padding: 5,
            flex: 0.35,
            region: 'east',
            items: detailsPanelItems
        };
        var pane = {
            xtype: 'panel',
            layout: 'border',
            autoScroll: true,
            items: [userInfoPane, detailsPanel]
        };
        this.removeAll();
        this.add(pane);
    },

    generateFieldSet: function(title, fields, userData){
        var displayFields = [];
        Ext.Array.each(fields, function(field){
            var fieldValue = userData[field];
            if ((fieldValue == null) && (userData.userInfo != null) ){
                fieldValue = userData.userInfo[field];
            }
            if (fieldValue != null){
                var displayField = {
                    xtype: 'displayfield',
                    fieldLabel: field,
                    value: fieldValue
                };
                Ext.Array.include( displayFields, displayField );
            }

        });
        if (displayFields.length > 0){
            var fieldSet = {
                xtype: 'fieldset',
                title: title,
                items: displayFields
            };
            return fieldSet;
        }else{
            return null;
        }

    },

    generateGroupsFieldSet: function (userData){
        var groupFields = [];
        Ext.Array.each(userData.groups, function(group){
            var groupField = {
                    xtype: 'button',
                    text: group.name,
                    iconCls: 'icon-group',
                    cls: 'group-display',
                    margin: 5
                };
            Ext.Array.include(groupFields, groupField);
        });
        if (groupFields.length > 0){
            var groupFieldSet = {
                xtype: 'fieldset',
                title: 'Groups',
                items: groupFields
            };
            return groupFieldSet;
        }else{
            return null;
        }
    },

    generateMultipleSelection: function(userArray){
        var me = this;
        var userPaneArray = [];
        Ext.Array.each(userArray, function(user){
            Ext.Array.include(userPaneArray, me.generateUserButton(user));
        });
        var panel = {
            xtype: 'panel',
            layout: 'column',
            measureWidth: true,
            autoScroll: true,
            items: userPaneArray
        };
        me.removeAll();
        me.add(panel);
    },

    generateUserButton: function(userData){
        var displayNamePane = {
            xtype: 'panel',
            border: 0,
            height: 25,
            layout: 'fit',
            items: [
                {
                    xtype: 'displayfield',
                    style: {
                        background: 'lightGrey'
                    },
                    cls: 'display-name',
                    value: userData.get('displayName')
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
                    value: '(' + userData.get('userStore') + '/' + userData.get('name') + ')'
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
                    src: 'data/user/photo?key=' + userData.get('key') + '&thumb=true',
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
                iconCls: 'icon-delete'
            }]
        };
        var pane = {
            xtype: 'panel',
            layout: 'column',
            margin: 5,
            bodyStyle: {
                background: 'lightGrey'
            },
            items: [imagePane, namePane, buttonPane]
        };
        return pane;
    },

    setCurrentUser: function(user){
        this.currentUser = user;
    },

    getCurrentUser: function(){
        return this.currentUser;
    }

});

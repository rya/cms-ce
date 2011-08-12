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
        var detailsPanel = {
            xtype: 'panel',
            title: 'Details',
            autoScroll: true,
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
            padding: 5,
            flex: 0.3,
            region: 'east',
            items: [
                {
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
                },
                {
                    xtype: 'fieldset',
                    title: 'Groups',
                    items: [
                        {
                            xtype: 'displayfield',
                            style: {
                                background: 'lightGrey',
                                border: 1
                            },
                            value: 'Very long group name'
                        },
                        {
                            xtype: 'displayfield',
                            style: {
                                background: 'lightGrey',
                                border: 1
                            },
                            value: 'group name'
                        }]
                }]
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

    setCurrentUser: function(user){
        this.currentUser = user;
    },

    getCurrentUser: function(){
        return this.currentUser;
    }

});

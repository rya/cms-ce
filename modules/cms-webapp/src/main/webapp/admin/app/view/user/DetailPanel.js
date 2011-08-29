Ext.define('CMS.view.user.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userDetail',

    title: 'Details',
    split: true,
    autoScroll: true,
    layout: 'card',
    collapsible: true,

    initComponent: function() {

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
        var largeBoxesPanel = this.createLargeBoxSelection();
        var smallBoxesPanel = this.createSmallBoxSelection();
        var detailsPanel = this.updateDetails({});
        this.items = [detailsPanel, largeBoxesPanel, smallBoxesPanel];
        this.callParent(arguments);
    },

    showMultipleSelection: function(data, detailed){
        var activeItem;
        if (detailed){
            activeItem = this.down('#largeBoxPanel');
            this.getLayout().setActiveItem('largeBoxPanel');
        }else{
            activeItem = this.down('#smallBoxPanel');
            this.getLayout().setActiveItem('smallBoxPanel');
        }
        activeItem.update({users: data});
    },

    showSingleSelection: function(data){
        var activeItem = this.down('#detailsPanel');
        this.getLayout().setActiveItem('detailsPanel');
        activeItem.update(data);
    },

    updateDetails: function(user){
        var userFields = ['username', 'email'];
        var nameFields = ['prefix', 'first-name', 'middle-name',
                          'last-name', 'suffix', 'initials', 'nick-name'];
        var userFieldSet = this.generateFieldSet('User', userFields, user);
        var nameFieldSet = this.generateFieldSet('Name', nameFields, user);
        var photoPanel = {
            xtype: 'panel',
            border: false,
            autoScroll: false,
            autoHeight: true,
            layout: {
                type: 'table',
                columns: 2
            },
            items: [
                {
                    rowspan: 2,
                    xtype: 'image',
                    border: 1,
                    src: 'data/user/photo?key=' + user.key + '&thumb=true'
                },
                {
                    xtype: 'displayfield',
                    value: user['display-name']
                },
                {
                    xtype: 'displayfield',
                    value: user['qualifiedName']
                }
            ]
        };
        var items = [photoPanel];

        if (userFieldSet != null){
            Ext.Array.include(items, userFieldSet);
        }
        if (nameFieldSet != null){
            Ext.Array.include(items, nameFieldSet);
        }
        var userInfoPane = {
            xtype: 'panel',
            border: false,
            flex: 1,
            autoScroll: false,
            autoHeight: true,
            layout: {
                type: 'anchor',
                defaultAnchor: '100%'
            },
            items: items
        };
        var infoFieldSet = {
            xtype: 'fieldset',
            width: 300,
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
                }
            ]
        };
        var groupFieldSet = this.generateGroupsFieldSet(user);
        var detailsPanelItems = [infoFieldSet];
        if (groupFieldSet != null){
            Ext.Array.include( detailsPanelItems, groupFieldSet );
        }
        var detailsPanel = {
            xtype: 'panel',
            title: 'Details',
            autoScroll: false,
            autoHeight: true,
            margins: '0 0 0 10',
            bodyPadding: '0 10',
            layout: {
                type: 'anchor',
                defaultAnchor: '100%'
            },
            items: detailsPanelItems
        };
        var pane = {
            xtype: 'panel',
            itemId: 'detailsPanel',
            border: false,
            layout: {
                type: 'hbox',
                shrinkToFit: false,
                align: 'top',
                padding: 10
            },
            autoHeight: true,
            items: [userInfoPane, detailsPanel]
        };
        return pane;
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
                width: 300,
                title: 'Groups',
                items: groupFields
            };
            return groupFieldSet;
        }else{
            return null;
        }
    },

    createLargeBoxSelection: function(){
        var tpl = Ext.Template('<tpl for="users">' +
           '<div class="cms-selected-item-box large x-btn-default-large clearfix">' +
           '<div class="left">' +
           '<img alt="User" src="data/user/photo?key={key}&thumb=true"/></div>' +
           '<div class="center">' +
           '<h2>{displayName}</h2>' +
           '<p>{userStore}/{name}</p></div>' +
           '<div class="right">' +
           ' <a href=""></a></div></div>' +
           '</tpl>');
        var panel = {
            xtype: 'panel',
            itemId: 'largeBoxPanel',
            styleHtmlContent: true,
            tpl: tpl
        }
        return panel;
    },

    createSmallBoxSelection: function(){
        var tpl = Ext.Template('<tpl for="users">' +
            '<div class="cms-selected-item-box small x-btn-default-small clearfix">' +
            '<div class="cms-selected-item-box left">' +
            '<img alt="User" src="data/user/photo?key={key}&thumb=true"/></div>' +
            '<div class="cms-selected-item-box center">' +
            '<h2>{displayName}</h2></div>' +
            '<div class="cms-selected-item-box right">' +
            '<a href="javascript;"></a></div></div>'+
            '</tpl>');
        var panel = {
            xtype: 'panel',
            itemId: 'smallBoxPanel',
            styleHtmlContent: true,
            tpl: tpl
        }
        return panel;
    },

    generateUserButton: function(userData, shortInfo){
        if (shortInfo){
            return {
                xtype: 'userShortDetailButton',
                userData: userData
            };
        }else{
            return {
                xtype: 'userDetailButton',
                userData: userData
            };
        }
    },

    setCurrentUser: function(user){
        this.currentUser = user;
    },

    getCurrentUser: function(){
        return this.currentUser;
    }

});

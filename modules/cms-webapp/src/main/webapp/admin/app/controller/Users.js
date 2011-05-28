Ext.define('CMS.controller.Users', {
    extend: 'Ext.app.Controller',

    stores: ['Users'],
    models: ['User'],
    views: ['user.ListToolbar', 'user.Grid', 'user.Detail', 'user.Filter', 'user.Delete', 'user.ChangePassword', 'user.ContextMenu'],

    refs: [
        {ref: 'userGrid', selector: 'userGrid'},
        {ref: 'userDetail', selector: 'userDetail'},
        {ref: 'userFilter', selector: 'userFilter'},
        {ref: 'filterTextField', selector: 'userFilter textfield'},
        {ref: 'userEditWindow', selector: 'userEditWindow', autoCreate: true, xtype: 'userEditWindow'},
        {ref: 'userDeleteWindow', selector: 'userDeleteWindow', autoCreate: true, xtype: 'userDeleteWindow'},
        {ref: 'userChangePasswordWindow', selector: 'userChangePassword', autoCreate: true, xtype: 'userChangePasswordWindow'},
        {ref: 'userContextMenu', selector: 'userContextMenu', autoCreate: true, xtype: 'userContextMenu'}
    ],

    init: function() {
        this.control({
            '*[action=newUser]': {
                click: this.newUser
            },
            '*[action=newGroup]': {
                click: this.newGroup
            },
            'userGrid': {
                selectionchange: this.updateInfo,
                itemcontextmenu: this.popupMenu
            },
            'userFilter button[action=search]': {
                click: this.searchFilter
            },
            '*[action=showDeleteWindow]': {
                click: this.showDeleteUserWindow
            },
            '*[action=deleteUser]': {
                click: this.deleteUser
            },
            '*[action=edit]': {
                click: this.showEditUserForm
            },
            '*[action=changePassword]': {
                click: this.showChangePasswordWindow
            },
            'userDetail': {
                render: this.setDetailsToolbarDisabled
            }
        });
    },

    newUser: function() {
        Ext.Msg.alert('New User', 'TODO');
    },

    newGroup: function() {
        Ext.Msg.alert('New Group', 'TODO');
    },

    updateInfo: function(selModel, selected) {
        var user = selected[0];
        var userDetail = this.getUserDetail();

        if (user) {
            userDetail.update(user.data);
        }

        userDetail.setTitle(selected.length + " user selected");
        this.setDetailsToolbarDisabled();
    },

    selectUser: function(view) {
        var first = this.getUsersStore().getAt(0);
        if (first) {
            view.getSelectionModel().select(first);
        }
    },

    searchFilter: function() {
        var usersStore = this.getUsersStore();
        var textField = this.getFilterTextField();

        usersStore.clearFilter();
        usersStore.filter('displayName', textField.getValue());
        usersStore.loadPage(1);
    },

    showDeleteUserWindow: function() {
        this.getUserDeleteWindow().doShow(this.getUserGrid().getSelectionModel().selected.get(0));
    },

    deleteUser: function() {
        Ext.Msg.alert('Do Delete User', 'TODO');
    },

    showEditUserForm: function() {
        Ext.Msg.alert('Show Edit User Form', 'TODO');
    },

    showChangePasswordWindow: function() {
        this.getUserChangePasswordWindow().doShow(this.getUserGrid().getSelectionModel().selected.get(0));
    },

    popupMenu: function(view, rec, node, index, e) {
        e.stopEvent();
        this.getUserContextMenu().showAt(e.getXY());
        return false;
    },

    setDetailsToolbarDisabled: function() {
        var disable = !this.gridHasSelection();
        Ext.ComponentQuery.query('*[action=edit]')[0].setDisabled(disable);
        Ext.ComponentQuery.query('*[action=showDeleteWindow]')[0].setDisabled(disable);
        Ext.ComponentQuery.query('*[action=changePassword]')[0].setDisabled(disable);
    },

    gridHasSelection: function() {
        return this.getUserGrid().getSelectionModel().getSelection().length > 0;
    }

});

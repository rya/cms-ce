Ext.define('CMS.controller.Users', {
    extend: 'Ext.app.Controller',

    stores: ['Users'],
    models: ['User'],
    views: ['user.Grid', 'user.Detail', 'user.Filter', 'user.Edit', 'user.Delete', 'user.ChangePassword'],

    refs: [
        {ref: 'userGrid', selector: 'userGrid'},
        {ref: 'userDetail', selector: 'userDetail'},
        {ref: 'userFilter', selector: 'userFilter'},
        {ref: 'filterTextField', selector: 'userFilter textfield'},
        {ref: 'userEditWindow', selector: 'userEditWindow', autoCreate: true, xtype: 'userEditWindow'},
        {ref: 'userDeleteWindow', selector: 'userDeleteWindow', autoCreate: true, xtype: 'userDeleteWindow'},
        {ref: 'userChangePasswordWindow', selector: 'userChangePassword', autoCreate: true, xtype: 'userChangePasswordWindow'}
    ],

    init: function() {
        this.control({
            'userGrid': {
                selectionchange: this.updateInfo
            },
            'userGrid > tableview': {
                refresh: this.selectUser
            },
            'userFilter button[action=search]': {
                click: this.searchFilter
            },
            'userDetail button[action=delete]': {
                click: this.deleteUser
            },
            'userDetail button[action=edit]': {
                click: this.editUser
            },
            'userDetail button[action=changePassword]': {
                click: this.changePassword
            }
        });
    },

    updateInfo: function(selModel, selected) {
        var user = selected[0];
        var userDetail = this.getUserDetail();

        if (user) {
            userDetail.update(user.data);
        }

        userDetail.setTitle(selected.length + " user selected");
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

    deleteUser: function() {
        this.getUserDeleteWindow().doShow(this.getUserGrid().getSelectionModel().selected.get(0));
    },

    editUser: function() {
        this.getUserEditWindow().doShow(this.getUserGrid().getSelectionModel().selected.get(0));
    },

    changePassword: function() {
        this.getUserChangePasswordWindow().doShow(this.getUserGrid().getSelectionModel().selected.get(0));
    }

});

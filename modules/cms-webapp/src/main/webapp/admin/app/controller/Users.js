Ext.define('CMS.controller.Users', {
    extend: 'Ext.app.Controller',

    stores: ['Users'],
    models: ['User'],
    views: ['user.Grid', 'user.Detail', 'user.Filter', 'user.Edit', 'user.Delete', 'user.ChangePassword', 'user.ContextMenu'],

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
            'userGrid': {
                selectionchange: this.updateInfo
            },
            'userGrid > tableview': {
                refresh: this.selectUser,
                itemcontextmenu: this.popupMenu
            },
            'userFilter button[action=search]': {
                click: this.searchFilter
            },
            '*[action=delete]': {
                click: this.deleteUser
            },
            '*[action=edit]': {
                click: this.editUser
            },
            '*[action=changePassword]': {
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
    },

    popupMenu: function(view, rec, node, index, e) {
        e.stopEvent();
        this.getUserContextMenu().showAt(e.getXY());
        return false;
    }

});

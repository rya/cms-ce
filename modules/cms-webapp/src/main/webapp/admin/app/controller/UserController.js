Ext.define('CMS.controller.UserController', {
    extend: 'Ext.app.Controller',

    stores: ['UserStore'],
    models: ['UserModel'],
    views: [
        'user.GridPanel',
        'user.DetailPanel',
        'user.FilterPanel',
        'user.DeleteWindow',
        'user.ChangePasswordWindow',
        'user.ContextMenu'
    ],

    refs: [
        {ref: 'tabPanel', selector: 'cmsTabPanel'},
        {ref: 'userStore', selector: 'UserStore'},
        {ref: 'userGrid', selector: 'userGrid'},
        {ref: 'userDetail', selector: 'userDetail'},
        {ref: 'userFilter', selector: 'userFilter'},
        {ref: 'filterTextField', selector: 'userFilter textfield[name=filter]'},
        {ref: 'userEditWindow', selector: 'userEditWindow', autoCreate: true, xtype: 'userEditWindow'},
        {ref: 'userDeleteWindow', selector: 'userDeleteWindow', autoCreate: true, xtype: 'userDeleteWindow'},
        {ref: 'userChangePasswordWindow', selector: 'userChangePassword', autoCreate: true, xtype: 'userChangePasswordWindow'},
        {ref: 'userContextMenu', selector: 'userContextMenu', autoCreate: true, xtype: 'userContextMenu'}
    ],

    init: function()
    {
        this.control( {
            'viewport': {
                afterrender: this.createBrowseTab
            },
            '*[action=newUser]': {
                click: this.createNewUserTab
            },
            '*[action=newGroup]': {
                click: this.createNewGroupTab
            },
            'userGrid': {
                selectionchange: this.updateDetailsPanel,
                itemcontextmenu: this.popupMenu,
                itemdblclick: this.createEditUserTab
            },
            'userFilter': {
                enterKeyPress: this.filterHandleEnterKey,
                render: this.onFilterPanelRender
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
                click: this.createEditUserTab
            },
            '*[action=changePassword]': {
                click: this.showChangePasswordWindow
            },
            'userDetail': {
                render: this.setDetailsToolbarDisabled
            }
        } );
    },

    createBrowseTab: function( component, options )
    {
        this.getTabPanel().addTab( {
            title: 'Browse',
            xtype: 'panel',
            layout: 'border',
            items: [
                {
                    region: 'west',
                    width: 225,
                    xtype: 'userFilter'
                },
                {
                    region: 'center',
                    xtype: 'userShow'
                }
            ],
            closable: false
        } );
    },

    createNewUserTab: function()
    {
        this.getTabPanel().addTab( {
            title: 'New User',
            html: 'New User Form',
            iconCls: 'icon-user-add'
        } );
    },

    createNewGroupTab: function()
    {
        this.getTabPanel().addTab( {
            title: 'New Group',
            html: 'New Group Form',
            iconCls: 'icon-group-add'
        } );
    },

    createEditUserTab: function()
    {
        var selectedGridItem = this.getSelectedGridItem();
        var user = selectedGridItem.data;

        this.getTabPanel().addTab( {
            title: user.displayName + ' (' + user.qualifiedName + ')',
            html: 'Edit User Form: ' + user.displayName + ' (' + user.qualifiedName + ')',
            iconCls: 'icon-edit-user'
        } );
    },

    createEditGroupTab: function()
    {

    },

    showDeleteUserWindow: function()
    {
        this.getUserDeleteWindow().doShow( this.getSelectedGridItem() );
    },

    showChangePasswordWindow: function()
    {
        this.getUserChangePasswordWindow().doShow( this.getSelectedGridItem() );
    },

    updateDetailsPanel: function( selModel, selected )
    {
        var user = selected[0];
        var userDetail = this.getUserDetail();

        if ( user )
        {
            userDetail.update( user.data );
        }

        userDetail.setTitle( selected.length + " user selected" );
        this.setDetailsToolbarDisabled();
    },

    searchFilter: function()
    {
        var usersStore = this.getUserStoreStore();
        var textField = this.getFilterTextField();

        usersStore.clearFilter();
        usersStore.filter( 'displayName', textField.getValue() );
        usersStore.loadPage( 1 );
    },

    filterHandleEnterKey: function( field, event )
    {
        if ( event.getKey() == event.ENTER )
        {
            this.searchFilter();
        }
    },

    popupMenu: function( view, rec, node, index, e )
    {
        e.stopEvent();
        this.getUserContextMenu().showAt( e.getXY() );
        return false;
    },

    deleteUser: function()
    {
        Ext.Msg.alert( 'Do Delete User', 'TODO' );
    },

    setDetailsToolbarDisabled: function()
    {
        var disable = !this.gridHasSelection();
        Ext.ComponentQuery.query( '*[action=edit]' )[0].setDisabled( disable );
        Ext.ComponentQuery.query( '*[action=showDeleteWindow]' )[0].setDisabled( disable );
        Ext.ComponentQuery.query( '*[action=changePassword]' )[0].setDisabled( disable );
    },

    gridHasSelection: function()
    {
        return this.getUserGrid().getSelectionModel().getSelection().length > 0;
    },

    getSelectedGridItem: function()
    {
        return this.getUserGrid().getSelectionModel().selected.get( 0 );
    },

    onFilterPanelRender: function()
    {
        Ext.getCmp( 'filter' ).focus( false, 10 );
    }

});

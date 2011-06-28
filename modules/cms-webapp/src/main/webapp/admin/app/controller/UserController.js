Ext.define( 'CMS.controller.UserController', {
    extend: 'Ext.app.Controller',

    stores: ['UserStore', 'UserStoreConfigStore', 'CountryStore', 'CallingCodeStore', 'LanguageStore',
        'RegionStore'],
    models: ['UserModel', 'UserFieldModel', 'UserStoreConfigModel', 'CountryModel', 'CallingCodeModel',
        'LanguageModel', 'RegionModel'],
    views: [
        'user.GridPanel',
        'user.DetailPanel',
        'user.FilterPanel',
        'user.DeleteWindow',
        'user.ChangePasswordWindow',
        'user.ContextMenu',
        'user.EditUserWindow',
        'user.EditUserPanel',
        'user.EditUserMembershipPanel',
        'user.EditUserPreferencesPanel',
        'user.EditUserPropertiesPanel'
    ],

    refs: [
        {ref: 'tabPanel', selector: 'cmsTabPanel'},
        {ref: 'userStore', selector: 'UserStore'},
        {ref: 'userGrid', selector: 'userGrid'},
        {ref: 'userDetail', selector: 'userDetail'},
        {ref: 'userFilter', selector: 'userFilter'},
        {ref: 'filterTextField', selector: 'userFilter textfield[name=filter]'},
        {ref: 'editUserPanel', selector: 'editUserWindow editUserPanel', autoCreate: true, xtype: 'editUserPanel'},
        {ref: 'editUserWindow', selector: 'editUserWindow', autoCreate: true, xtype: 'editUserWindow'},
        {ref: 'userDeleteWindow', selector: 'userDeleteWindow', autoCreate: true, xtype: 'userDeleteWindow'},
        {ref: 'userChangePasswordWindow', selector: 'userChangePassword', autoCreate: true, xtype: 'userChangePasswordWindow'},
        {ref: 'userContextMenu', selector: 'userContextMenu', autoCreate: true, xtype: 'userContextMenu'},
        {ref: 'userTabMenu', selector: 'userTabMenu', autoCreate: true, xtype: 'userTabMenu'}
    ],

    init: function()
    {
        this.control( {
            'viewport': {
                afterrender: this.createBrowseTab
            },
            '*[action=newUser]': {
                click: this.showEditUserForm
            },
            '*[action=newGroup]': {
                click: this.createNewGroupTab
            },
            'userGrid': {
                selectionchange: this.updateDetailsPanel,
                itemcontextmenu: this.popupMenu,
                itemdblclick: this.showEditUserForm
            },
            'userFilter': {
                specialkey: this.filterHandleEnterKey,
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
            '*[action=addNewTab]': {
                click: this.addNewTab
            },
            '*[action=edit]': {
                click: this.showEditUserForm
            },
            '*[action=changePassword]': {
                click: this.showChangePasswordWindow
            },
            'userDetail': {
                render: this.setDetailsToolbarDisabled
            },
            'editUserPanel textfield[name=prefix]': {
                keyup: this.textFieldHandleEnterKey
            },
            'editUserPanel textfield[name=first_name]': {
                keyup: this.textFieldHandleEnterKey
            },
            'editUserPanel textfield[name=middle_name]': {
                keyup: this.textFieldHandleEnterKey
            },
            'editUserPanel textfield[name=last_name]': {
                keyup: this.textFieldHandleEnterKey
            },
            'editUserPanel textfield[name=suffix]': {
                keyup: this.textFieldHandleEnterKey
            },
            'editUserPanel textfield[name=address_label]': {
                keyup: this.updateTabTitle
            }
        } );
    },

    newUser: function()
    {
        Ext.Msg.alert( 'New User', 'TODO' );
    },

    newGroup: function()
    {
        Ext.Msg.alert( 'New Group', 'TODO' );
    },

    updateInfo: function( selModel, selected )
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

    selectUser: function( view )
    {
        var first = this.getUserStoreStore().getAt( 0 );
        if ( first )
        {
            view.getSelectionModel().select( first );
        }
    },

    onFilterPanelRender: function()
    {
        Ext.getCmp( 'filter' ).focus( false, 10 );
    },

    createBrowseTab: function( component, options )
    {
        this.getTabPanel().addTab( {
            id: 'tab-browse',
            title: 'Browse',
            closable: false,
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
            ]
        });
    },

    createNewUserTab: function()
    {
        this.getTabPanel().addTab( {
            title: 'New User',
            items: this.createDummyUserForm(),
            iconCls: 'icon-user-add'
        });
    },

    createNewGroupTab: function()
    {
        this.getTabPanel().addTab( {
            title: 'New Group',
            html: 'New Group Form',
            iconCls: 'icon-group-add'
        });
    },

    createEditUserTab: function()
    {
        var selectedGridItem = this.getSelectedGridItem();
        var user = selectedGridItem.data;

        this.getTabPanel().addTab( {
            id: 'tab-user-' + user.key,
            title: user.displayName + ' (' + user.qualifiedName + ')',
            items: this.createDummyUserForm( user ),
            iconCls: 'icon-edit-user'
        });
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

    showEditUserForm: function()
    {

        this.getEditUserWindow().doShow();
        this.getEditUserPanel().doShow();
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

    textFieldHandleEnterKey: function( field, event )
    {
        var prefix = this.getEditUserWindow().down( '#prefix' )
                ? Ext.String.trim( this.getEditUserWindow().down( '#prefix' ).getValue() ) : '';
        var firstName = this.getEditUserWindow().down( '#first_name' )
                ? Ext.String.trim( this.getEditUserWindow().down( '#first_name' ).getValue() ) : '';
        var middleName = this.getEditUserWindow().down( '#middle_name' )
                ? Ext.String.trim( this.getEditUserWindow().down( '#middle_name' ).getValue() ) : '';
        var lastName = this.getEditUserWindow().down( '#last_name' )
                ? Ext.String.trim( this.getEditUserWindow().down( '#last_name' ).getValue() ) : '';
        var suffix = this.getEditUserWindow().down( '#suffix' )
                ? Ext.String.trim( this.getEditUserWindow().down( '#suffix' ).getValue() ) : '';
        var displayName = this.getEditUserWindow().down( '#display_name' );
        if ( displayName )
        {
            displayName.setValue( prefix + ' ' + firstName + ' ' + middleName + ' ' + lastName + ' ' + suffix );
        }
    },

    getSelectedGridItem: function()
    {
        return this.getUserGrid().getSelectionModel().selected.get( 0 );
    },

    addNewTab: function()
    {
        var tabPanel = this.getEditUserWindow().down( '#addressTabPanel' );
        if ( tabPanel.items.length == 1 )
        {
            tabPanel.items.get( 0 ).closable = true;
        }
        var newTab = tabPanel.items.get( 0 ).cloneConfig( {closable: true, text: '[no title]'} );
        tabPanel.add( newTab );
    },

    updateTabTitle: function ( field, event )
    {
        var tabPanel = this.getEditUserWindow().down( '#addressTabPanel' );
        tabPanel.getActiveTab().setTitle(field.getValue());
    },

    // Dummy form
    createDummyUserForm: function( user )
    {

        return Ext.create( 'Ext.form.Panel', {
            bodyStyle:'padding:15px',
            fieldDefaults: {
                labelAlign: 'top',
                msgTarget: 'side'
            },

            defaults: {
                anchor: '100%'
            },

            items: [
                {
                    layout:'column',
                    border:false,
                    items:[
                        {
                            columnWidth:.5,
                            border:false,
                            layout: 'anchor',
                            defaultType: 'textfield',
                            items: [
                                {
                                    fieldLabel: 'First Name',
                                    name: 'first',
                                    value: user ? user.name : '',
                                    anchor:'95%'
                                },
                                {
                                    fieldLabel: 'Company',
                                    name: 'company',
                                    anchor:'95%'
                                }
                            ]
                        },
                        {
                            columnWidth:.5,
                            border:false,
                            layout: 'anchor',
                            defaultType: 'textfield',
                            items: [
                                {
                                    fieldLabel: 'Last Name',
                                    name: 'last',
                                    anchor:'95%'
                                },
                                {
                                    fieldLabel: 'Email',
                                    name: 'email',
                                    vtype:'email',
                                    anchor:'95%'
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype:'tabpanel',
                    plain:true,
                    activeTab: 0,
                    height:235,
                    defaults:{bodyStyle:'padding:10px'},
                    items:[
                        {
                            title:'Personal Details',
                            defaults: {width: 230},
                            defaultType: 'textfield',

                            items: [
                                {
                                    fieldLabel: 'First Name',
                                    name: 'first',
                                    allowBlank:false
                                },
                                {
                                    fieldLabel: 'Last Name',
                                    name: 'last'
                                },
                                {
                                    fieldLabel: 'Company',
                                    name: 'company'
                                },
                                {
                                    fieldLabel: 'Email',
                                    name: 'email',
                                    vtype:'email'
                                }
                            ]
                        },
                        {
                            title:'Phone Numbers',
                            defaults: {width: 230},
                            defaultType: 'textfield',

                            items: [
                                {
                                    fieldLabel: 'Home',
                                    name: 'home',
                                    value: '(888) 555-1212'
                                },
                                {
                                    fieldLabel: 'Business',
                                    name: 'business'
                                },
                                {
                                    fieldLabel: 'Mobile',
                                    name: 'mobile'
                                },
                                {
                                    fieldLabel: 'Fax',
                                    name: 'fax'
                                }
                            ]
                        },
                        {
                            cls: 'x-plain',
                            title: 'Biography',
                            layout: 'fit',
                            items: {
                                xtype: 'htmleditor',
                                name: 'bio2',
                                fieldLabel: 'Biography'
                            }
                        }
                    ]
                }
            ],

            buttons: [
                {
                    text: 'Save'
                },
                {
                    text: 'Cancel'
                }
            ]
        } );

    }

} );

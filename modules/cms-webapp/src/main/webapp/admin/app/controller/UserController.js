Ext.define( 'CMS.controller.UserController', {
    extend: 'Ext.app.Controller',

    stores: ['UserStore', 'UserStoreConfigStore', 'CountryStore', 'CallingCodeStore', 'LanguageStore',
        'RegionStore', 'GroupStore'],
    models: ['UserModel', 'UserFieldModel', 'UserStoreConfigModel', 'CountryModel', 'CallingCodeModel',
        'LanguageModel', 'RegionModel', 'GroupModel'],
    views: [
        'user.GridPanel',
        'user.DetailPanel',
        'user.FilterPanel',
        'user.DeleteWindow',
        'user.ChangePasswordWindow',
        'user.ContextMenu',
        'user.EditUserPanel',
        'user.EditUserFormPanel',
        'user.EditUserMembershipPanel',
        'user.EditUserPreferencesPanel',
        'user.EditUserPropertiesPanel',
        'user.UserFormField',
        'user.MembershipGridPanel',
        'user.UserMembershipWindow',
        'user.GroupItemField'
    ],

    refs: [
        {ref: 'tabPanel', selector: 'cmsTabPanel'},
        {ref: 'userStore', selector: 'UserStore'},
        {ref: 'userGrid', selector: 'userGrid'},
        {ref: 'userDetail', selector: 'userDetail'},
        {ref: 'userFilter', selector: 'userFilter'},
        {ref: 'filterTextField', selector: 'userFilter textfield[name=filter]'},
        {ref: 'editUserFormPanel', selector: 'editUserPanel editUserFormPanel', autoCreate: true, xtype: 'editUserFormPanel'},
        {ref: 'editUserPanel', selector: 'editUserPanel', xtype: 'editUserPanel'},
        {ref: 'userDeleteWindow', selector: 'userDeleteWindow', autoCreate: true, xtype: 'userDeleteWindow'},
        {ref: 'userChangePasswordWindow', selector: 'userChangePassword', autoCreate: true, xtype: 'userChangePasswordWindow'},
        {ref: 'userContextMenu', selector: 'userContextMenu', autoCreate: true, xtype: 'userContextMenu'},
        {ref: 'userTabMenu', selector: 'userTabMenu', autoCreate: true, xtype: 'userTabMenu'},
        {ref: 'userMembershipWindow', selector: 'userMembershipWindow', autoCreate: true, xtype: 'userMembershipWindow'},
        {ref: 'editUserMembershipPanel', selector: 'editUserMembershipPanel', autoCreate: true, xtype: 'editUserMembershipPanel'},
        {ref: 'membershipGridPanel', selector: 'membershipGridPanel'},
        {ref: 'cmsTabPanel', selector: 'cmsTabPanel', xtype: 'cmsTabPanel'}
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
                          '*[action=toggleDisplayNameField]': {
                              click: this.toggleDisplayNameField
                          },
                          'editUserPanel #iso-country' : {
                              select: this.countryChangeHandler
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
                          },
                          '*[action=addGroup]': {
                              click: this.showAddGroupWindow
                          },
                          '*[action=deleteGroup]': {
                              click: this.deleteGroup
                          },
                          '*[action=selectGroups]': {
                              click: this.selectGroup
                          },
                          '*[action=closeMembershipWindow]': {
                              click: this.closeMembershipWindow
                          },
                          '*[action=selectGroup]': {
                              select: this.selectGroup
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
                                   } );
    },

    createNewUserTab: function()
    {
        this.getTabPanel().addTab( {
                                       title: 'New User',
                                       items: this.createDummyUserForm(),
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
                                       id: 'tab-user-' + user.key,
                                       title: user.displayName + ' (' + user.qualifiedName + ')',
                                       items: this.createDummyUserForm( user ),
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
            userDetail.setCurrentUser( user.data );
        }

        userDetail.setTitle( selected.length + " user selected" );
        this.setDetailsToolbarDisabled();
    },

    searchFilter: function()
    {
        var usersStore = this.getUserStoreStore();
        var textField = this.getFilterTextField();

        usersStore.clearFilter();
        usersStore.load( {params:{query: textField.getValue()}} );
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
        var deleteUserWindow = this.getUserDeleteWindow();
        Ext.Ajax.request( {
              url: 'data/user/delete',
              method: 'POST',
              params: {userKey: deleteUserWindow.userKey},
              success: function( response, opts )
              {
                  deleteUserWindow.close();
                  Ext.Msg.alert( 'Info', 'User was deleted' );
              },
              failure: function( response, opts )
              {
                  Ext.Msg.alert( 'Info', 'User wasn\'t deleted' );
              }
          } );
    },

    showEditUserForm: function( el, e )
    {
        var tabId, tabTitle, iconCls, currentUser;
        if ( el.action == 'newUser' )
        {
            var tab = {
               id: 'new-user',
               title: 'New User',
               iconCls: 'icon-user-add',
               closable: true,
               autoScroll: true,
               items: [
                   {
                       xtype: 'editUserPanel',
                       currentUser: currentUser
                   }
               ]
            }
            this.getTabPanel().addTab(tab);
        }
        else
        {
            var userDetail = this.getUserDetail();
            var tabPane = this.getTabPanel();
            currentUser = userDetail.getCurrentUser();
            Ext.Ajax.request( {
                url: 'data/user/userinfo',
                params: {key: currentUser.key},
                success: function( response ){
                    var jsonObj = Ext.JSON.decode( response.responseText );
                    var tab = {
                        id: currentUser.userStore + '-' + currentUser.name,
                        title: currentUser.displayName + ' (' + currentUser.qualifiedName + ')',
                        iconCls: 'icon-edit-user',
                        closable: true,
                        autoScroll: true,
                        items: [
                            {
                                xtype: 'editUserPanel',
                                userFields: jsonObj,
                                currentUser: currentUser
                            }
                        ]
                    };
                    tabPane.addTab(tab);
                  }
            });

        }
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

    countryChangeHandler: function( field, newValue, oldValue, options )
    {
        var region = field.up( 'fieldset' ).down( '#iso-region' );
        if ( region )
        {
            region.clearValue();
            Ext.apply( region.store.proxy.extraParams, {
                'countryCode': field.getValue()
            } );
            region.store.load( {
                                   callback: function( records, operation, success )
                                   {
                                       region.setDisabled( !records || records.length == 0 );
                                   }
                               } );
        }
        return true;
    },

    textFieldHandleEnterKey: function( field, event )
    {
        var prefix = this.getEditUserPanel().down( '#prefix' )
                ? Ext.String.trim( this.getEditUserPanel().down( '#prefix' ).getValue() ) : '';
        var firstName = this.getEditUserPanel().down( '#first_name' )
                ? Ext.String.trim( this.getEditUserPanel().down( '#first_name' ).getValue() ) : '';
        var middleName = this.getEditUserPanel().down( '#middle_name' )
                ? Ext.String.trim( this.getEditUserPanel().down( '#middle_name' ).getValue() ) : '';
        var lastName = this.getEditUserPanel().down( '#last_name' )
                ? Ext.String.trim( this.getEditUserPanel().down( '#last_name' ).getValue() ) : '';
        var suffix = this.getEditUserPanel().down( '#suffix' )
                ? Ext.String.trim( this.getEditUserPanel().down( '#suffix' ).getValue() ) : '';
        var displayName = this.getEditUserPanel().down( '#display_name' );
        if ( displayName )
        {
            displayName.setValue( prefix + ' ' + firstName + ' ' + middleName + ' ' + lastName + ' ' + suffix );
        }
    },

    getSelectedGridItem: function()
    {
        return this.getUserGrid().getSelectionModel().selected.get( 0 );
    },

    addNewTab: function(button, event)
    {
        var tabId = button.currentUser != null ? button.currentUser.userStore + '-' + button.currentUser.name : 'new-user';
        var tabPanel = this.getCmsTabPanel().down( '#' + tabId).down( '#addressTabPanel' );
        //var tabPanel = this.getEditUserPanel().down( '#addressTabPanel' );
        var newTab = this.getEditUserFormPanel().generateAddressFieldSet( tabPanel.sourceField, true );
        newTab = tabPanel.add( newTab );
        tabPanel.setActiveTab( newTab );
    },

    updateTabTitle: function ( field, event )
    {
        var tabPanel = this.getEditUserPanel().down( '#addressTabPanel' );
        tabPanel.getActiveTab().setTitle( field.getValue() );
    },

    toggleDisplayNameField: function ( button, event )
    {
        var tabId = button.currentUser != null ? button.currentUser.userStore + '-' + button.currentUser.name : 'new-user';
        var locked = 'icon-locked';
        var open = 'icon-unlocked';
        var displayNameField = this.getCmsTabPanel().down( '#' + tabId).down( '#display-name' );
        if ( button.iconCls == locked )
        {
            button.setIconCls( open );
            displayNameField.setDisabled( false );
        }
        else
        {
            button.setIconCls( locked );
            displayNameField.setDisabled( true );
        }

    },

    showAddGroupWindow: function()
    {
        this.getUserMembershipWindow().doShow();
    },

    selectGroup: function()
    {
        var membershipGridPanel = this.getMembershipGridPanel();
        var selection = membershipGridPanel.getSelectionModel().getSelection();
        var editUserMembershipPanel = this.getEditUserMembershipPanel();
        Ext.each( selection, function( item, index )
        {
            editUserMembershipPanel.addGroup( item.get( 'key' ), item.get( 'name' ) );
        } );
        this.getUserMembershipWindow().hide();
        membershipGridPanel.getSelectionModel().deselectAll();
    },

    deleteGroup: function( element, event )
    {
        var group = element.findParentByType( 'groupItemField' );
        this.getEditUserMembershipPanel().remove( group );
    },

    closeMembershipWindow: function()
    {
        var membershipGridPanel = this.getMembershipGridPanel();
        var selectionModel = membershipGridPanel.getSelectionModel()
        selectionModel.deselectAll();
        this.getUserMembershipWindow().hide();
    },

    selectGroup: function( field, value )
    {
        var editUserMembershipPanel = this.getEditUserMembershipPanel();
        editUserMembershipPanel.addGroup( value.get( 'key' ), value.get( 'name' ) );
        var groupSelector = editUserMembershipPanel.down( '#groupSelector' );
        field.deselectAll();
        groupSelector.clearValue();
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

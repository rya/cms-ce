Ext.define( 'CMS.controller.UserController', {
    extend: 'Ext.app.Controller',

    stores: ['UserStore', 'UserstoreConfigStore', 'CountryStore', 'CallingCodeStore', 'LanguageStore',
        'RegionStore', 'GroupStore'],
    models: ['UserModel', 'UserFieldModel', 'UserstoreConfigModel', 'CountryModel', 'CallingCodeModel',
        'LanguageModel', 'RegionModel', 'GroupModel'],
    views: [
        'user.GridPanel',
        'user.DetailPanel',
        'user.FilterPanel',
        'user.DeleteWindow',
        'user.ChangePasswordWindow',
        'user.ContextMenu',
        'user.EditUserPanel',
        'user.NewUserPanel',
        'user.EditUserFormPanel',
        'user.EditUserMembershipPanel',
        'user.EditUserPreferencesPanel',
        'user.EditUserPropertiesPanel',
        'user.UserFormField',
        'user.MembershipGridPanel',
        'user.UserMembershipWindow',
        'user.GroupItemField',
        'user.UserPreferencesPanel',
        'user.MultipleDetailPanel'
    ],

    refs: [
        {ref: 'tabPanel', selector: 'cmsTabPanel'},
        {ref: 'userStore', selector: 'UserStore'},
        {ref: 'userGrid', selector: 'userGrid'},
        {ref: 'userDetail', selector: 'userDetail', autoCreate: false, xtype: 'userDetail'},
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
                          '*[action=saveUser]': {
                              click: this.saveUser
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
                          'editUserPanel textfield[name=first-name]': {
                              keyup: this.textFieldHandleEnterKey
                          },
                          'editUserPanel textfield[name=middle-name]': {
                              keyup: this.textFieldHandleEnterKey
                          },
                          'editUserPanel textfield[name=last-name]': {
                              keyup: this.textFieldHandleEnterKey
                          },
                          'editUserPanel textfield[name=suffix]': {
                              keyup: this.textFieldHandleEnterKey
                          },
                          'editUserPanel textfield[name=label]': {
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
                          },
                          '*[action=closeUserForm]': {
                              click: this.closeUserForm
                          },
                          '*[action=initValue]': {
                              added: this.initValue
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

    createNewGroupTab: function()
    {
        this.getTabPanel().addTab( {
                                       title: 'New Group',
                                       html: 'New Group Form',
                                       iconCls: 'icon-group-add'
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
        var userDetail = this.getUserDetail();
        if (selected.length == 1){
            var user = selected[0];


            if ( user )
            {
                Ext.Ajax.request( {
                    url: 'data/user/userinfo',
                    method: 'GET',
                    params: {key: user.get('key')},
                    success: function( response ){
                        var jsonObj = Ext.JSON.decode( response.responseText );
                        userDetail.updateDetails( jsonObj );
                        userDetail.setCurrentUser( user.data );
                      }
                });

            }

            userDetail.setTitle( selected.length + " user selected" );
            this.setDetailsToolbarDisabled();
        }else{
            userDetail.generateMultipleSelection(selected);
            this.setDetailsToolbarDisabled();
            userDetail.setTitle( selected.length + " user selected" );
        }
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
                       xtype: 'newUserPanel'
                   }
               ]
            }
            this.getTabPanel().addTab(tab);
        }
        else
        {
            var userDetail = this.getUserDetail();
            var tabPane = this.getTabPanel();
            var currentUser = userDetail.getCurrentUser();
            Ext.Ajax.request( {
                url: 'data/user/userinfo',
                method: 'GET',
                params: {key: currentUser.key},
                success: function( response ){
                    var jsonObj = Ext.JSON.decode( response.responseText );
                    var tab = {
                        id: currentUser.userStore + '-' + currentUser.name,
                        layout: 'border',
                        title: currentUser.displayName + ' (' + currentUser.qualifiedName + ')',
                        iconCls: 'icon-edit-user',
                        closable: true,
                        autoScroll: true,
                        items: [
                            {
                                xtype: 'editUserPanel',
                                region: 'center',
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
        return this.getUserGrid().getSelectionModel().getSelection().length == 1;
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
        var formPanel = field.up('editUserPanel');
        var prefix = formPanel.down( '#prefix' )
                ? Ext.String.trim( formPanel.down( '#prefix' ).getValue() ) : '';
        var firstName = formPanel.down( '#first-name' )
                ? Ext.String.trim( formPanel.down( '#first-name' ).getValue() ) : '';
        var middleName = formPanel.down( '#middle-name' )
                ? Ext.String.trim( formPanel.down( '#middle-name' ).getValue() ) : '';
        var lastName = formPanel.down( '#last-name' )
                ? Ext.String.trim( formPanel.down( '#last-name' ).getValue() ) : '';
        var suffix = formPanel.down( '#suffix' )
                ? Ext.String.trim( formPanel.down( '#suffix' ).getValue() ) : '';
        var displayName = formPanel.down( '#display-name' );
        if ( displayName )
        {
            var displayNameValue = prefix + ' ' + firstName + ' ' + middleName + ' ' + lastName + ' ' + suffix;
            displayName.setValue( Ext.String.trim(displayNameValue) );
        }
    },

    getSelectedGridItem: function()
    {
        return this.getUserGrid().getSelectionModel().selected.get( 0 );
    },

    addNewTab: function(button, event)
    {
        var tabId = button.currentUser != '' ? button.currentUser.userStore + '-' + button.currentUser.name : 'new-user';
        var tabPanel = this.getCmsTabPanel().down( '#' + tabId).down( '#addressTabPanel' );
        var newTab = this.getEditUserFormPanel().generateAddressFieldSet( tabPanel.sourceField, true );
        newTab = tabPanel.add( newTab );
        tabPanel.setActiveTab( newTab );
    },

    updateTabTitle: function ( field, event )
    {
        var formPanel = field.up('editUserFormPanel');
        var tabPanel = formPanel.down( '#addressTabPanel' );
        tabPanel.getActiveTab().setTitle( field.getValue() );
    },

    toggleDisplayNameField: function ( button, event )
    {
        var tabId = button.currentUser != '' ? button.currentUser.userStore + '-' + button.currentUser.name : 'new-user';
        var locked = 'icon-locked';
        var open = 'icon-unlocked';
        var displayNameField = this.getCmsTabPanel().down( '#' + tabId).down( '#display-name' );
        if ( button.iconCls == locked )
        {
            button.setIconCls( open );
            displayNameField.setReadOnly( false );
        }
        else
        {
            button.setIconCls( locked );
            displayNameField.setReadOnly( true );
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

    saveUser: function(button){
        var editUserForm = button.up('editUserPanel');
        if (editUserForm.getForm().isValid()){
            var formValues = editUserForm.getValues();
            var userData = {
                username: formValues['username'],
                'display-name': formValues['display-name'],
                email: formValues['email'],
                key: editUserForm.userFields.key,
                userStore: editUserForm.userFields.userStore ?
                        editUserForm.userFields.userStore : editUserForm.defaultUserStoreName,
                userInfo: formValues
            }
            var tabPanel = editUserForm.down('#addressTabPanel');
            var tabs = tabPanel.query('form');
            var addresses = [];
            for (index in tabs){
                var address = tabs[index].getValues();
                Ext.Array.include(addresses, address);
            }
            userData.userInfo.addresses = addresses;

            Ext.Ajax.request({
                  url: 'data/user/update',
                  method: 'POST',
                  jsonData: userData,
                  success: function( response, opts )
                  {
                      var serverResponse = Ext.JSON.decode(response.responseText);
                      if (!serverResponse.success){
                          Ext.Msg.alert('Error', serverResponse.error);
                      }else{
                          Ext.Msg.alert('Info', 'User was updated');
                      }
                  },
                  failure: function( response, opts )
                  {
                      Ext.Msg.alert('Error', 'Internal server error was occured');
                  }
            });
        }else{
            Ext.Msg.alert('Error', 'Some required fields are missing');
        }
    },

    closeUserForm: function(button){
        var tabPane = this.getTabPanel();
        tabPane.getActiveTab().close();
    },

    initValue: function(field){
        var formField = field.up('userFormField');
        field.valueNotFoundText = formField.fieldValue;
        field.setValue(formField.fieldValue);
    }

} );

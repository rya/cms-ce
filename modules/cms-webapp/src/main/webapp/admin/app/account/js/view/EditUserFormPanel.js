Ext.define( 'App.view.EditUserFormPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.editUserFormPanel',

    defaults: {
        bodyPadding: 10
    },

    autoScroll: true,

    measureWidth: true,

    border: 0,

    style: {
        overflow: 'hidden'
    },

    layout: {
        type: 'table',
        columns: 1,
        defaultMargins: {top:10, right:10, bottom:10, left:10},
        padding: 10,
        tdAttrs: {
            style:{
                padding: '10px'
            }
        },
        tableAttrs: {
            style:{
                width: '98%'
            }
        }
    },

    currentUser: undefined,
    defaultUserStoreName: 'default',
    enableToolbar: true,

    listeners: {
        afterrender: function( me )
        {
            me.el.mask( "Loading..." );
            me.renderUserForm(me.currentUser);
        }
    },

    initComponent: function()
    {
        var me = this;
        if (this.enableToolbar){
            this.dockedItems = [
            {
                dock: 'top',
                xtype: 'toolbar',
                border: false,
                padding: 5,
                items: [
                    {
                        text: 'Save',
                        iconCls: 'icon-save',
                        action: 'saveUser'
                    },
                    {
                        text: 'Cancel',
                        action: 'closeUserForm'
                    },
                    '->',
                    {
                        text: 'Delete',
                        iconCls: 'icon-delete-user',
                        action: 'deleteUser'
                    },
                    {
                        text: 'Change Password',
                        iconCls: 'icon-change-password',
                        action: 'changePassword'
                    }
                ]
            }];
        }
        this.userFieldSet = {
            'username': this.createTextField,
            'email': this.createTextField
        };
        this.nameFieldSet = {
            'prefix': this.createTextField,
            'first-name': this.createTextField,
            'middle-name': this.createTextField,
            'last-name': this.createTextField,
            'suffix': this.createTextField,
            'initials': this.createTextField,
            'nick-name': this.createTextField
        };
        this.photoFieldSet = {
            'photo': this.createPhotoField
        };
        this.detailsFieldSet = {
            'personal-id': this.createTextField,
            'member-id': this.createTextField,
            'organization': this.createTextField,
            'birthday': this.createDateField,
            'gender': this.createComboBoxField,
            'title': this.createTextField,
            'description': this.createTextField,
            'html-email': this.createCheckBoxField,
            'homepage': this.createTextField
        };
        this.locationFieldSet = {
            'timezone': this.createComboBoxField,
            'locale': this.createComboBoxField,
            'country': this.createComboBoxField,
            'global-position': this.createTextField
        };
        this.communicationFieldSet = {
            'phone': this.createAutoCompleteField,
            'mobile': this.createAutoCompleteField,
            'fax': this.createAutoCompleteField
        };
        this.addressFieldSet = {
            'address': function(field)
            {
                if (me.userFields && me.userFields.userInfo && me.userFields.userInfo.addresses){
                    var addresses = me.userFields.userInfo.addresses;
                    var tabs = [];
                    for ( var index in addresses){
                        Ext.Array.include(tabs, me.generateAddressPanel(field, true, addresses[index]));
                    }
                    return {
                        sourceField: field,
                        xtype: 'addressContainer',
                        itemId: 'addressContainer',
                        items: tabs
                    };
                }else{
                    var tabItem = me.generateAddressPanel(field);
                    return {
                        sourceField: field,
                        xtype: 'addressContainer',
                        itemId: 'addressContainer',
                        items: [tabItem]
                    };
                }
            }
        };
        this.callParent( arguments );
        this.removeAll();
        this.show();
    },

    renderUserForm: function( user ){
        var me = this;
        Ext.Ajax.request( {
                url: 'data/userstore/detail',
                method: 'GET',
                params: {
                    name: user ? user.userStore : me.defaultUserStoreName
                },
                success: function( response )
                {
                    var obj = Ext.decode( response.responseText, true );
                    if ( obj )
                    {
                        me.removeAll();
                        me.generateForm( obj );
                    }
                    me.el.unmask();
                }
            } );
    },

    createAutoCompleteField: function ( field )
    {
        var callingCodeStore = Ext.data.StoreManager.lookup( 'CallingCodeStore' );
        var f = {
            xtype: 'userFormField',
            type: 'autocomplete',
            fieldLabel: field.fieldlabel,
            fieldStore: callingCodeStore,
            valueField: 'callingCode',
            displayField: 'callingCode',
            displayConfig:{
                getInnerTpl: function()
                {
                    return '{callingCode} ({englishName})';
                }
            }
        };
        return f;
    },

    createComboBoxField: function ( field )
    {
        var fieldStore;
        var valueField;
        var displayField;
        if ( field.type == 'timezone' )
        {
            fieldStore = Ext.data.StoreManager.lookup( 'TimezoneStore' );
            valueField = 'id';
            displayField = 'name';
        } else if ( field.type == 'country' )
        {
            fieldStore = Ext.data.StoreManager.lookup( 'CountryStore' );
            valueField = 'code';
            displayField = 'englishName';
        } else if ( field.type == 'region' )
        {
            fieldStore = new App.store.RegionStore();
            valueField = 'code';
            displayField = 'englishName';
        } else if ( field.type == 'locale' )
        {
            fieldStore = Ext.data.StoreManager.lookup( 'LanguageStore' );
            valueField = 'languageCode';
            displayField = 'description';
        } else if ( field.type == 'gender' )
        {
            fieldStore = new Ext.data.Store({
                fields: ['label', 'value'],
                data: [
                    {label: 'Male', value: 'MALE'},
                    {label: 'Female', value: 'FEMALE'}
                ]});
            valueField = 'value';
            displayField = 'label';
        }

        return {
            xtype: 'userFormField',
            type: 'combo',
            queryMode: 'local',
            minChars: 1,
            emptyText: 'Please select',
            fieldStore: fieldStore,
            valueField: valueField,
            displayField: displayField
        };
    },

    createTextField: function( field )
    {
        return {
            xtype: 'userFormField',
            type: 'text'
        };
    },

    createCheckBoxField: function ( field )
    {
        return {
            xtype: 'userFormField',
            type: 'boolean'
        }
    },

    createPhotoField: function( field )
    {
        return {
            xtype: 'userFormField',
            type: 'file'
        };
    },

    createDateField: function( field )
    {
        return {
            xtype: 'userFormField',
            type: 'date'
        };
    },

    generateForm: function(storeConfig)
    {
        var fields = [
            {
                label: 'Username',
                type: 'username',
                required: true,
                remote: false,
                readonly: false
            },
            {
                label: 'Password',
                type: 'password',
                required: true,
                remote: false,
                readonly: false
            },
            {
                label: 'Repeat password',
                type: 'repeat-password',
                required: true,
                remote: false,
                readonly: false
            },
            {
                label: 'E-mail',
                type: 'email',
                required: true,
                remote: false,
                readonly: false
            },
            {
                label: 'Display name',
                type: 'display-name',
                required: true,
                remote: false,
                readonly: false
            }
        ];
        if ( storeConfig && storeConfig.userFields )
        {

            fields = Ext.Array.merge( fields, Ext.Array.toArray(storeConfig.userFields) );
            this.add( this.generateFieldSet( 'User', this.userFieldSet, fields ) );
            this.add( this.generateFieldSet( 'Name', this.nameFieldSet, fields ) );
            this.add( this.generateFieldSet( 'Photo', this.photoFieldSet, fields ) );
            this.add( this.generateFieldSet( 'Personal Information', this.detailsFieldSet, fields ) );
            this.add( this.generateFieldSet( 'Location', this.locationFieldSet, fields ) );
            this.add( this.generateFieldSet( 'Communication', this.communicationFieldSet, fields ) );
            this.add( this.generateFieldSet( 'Address', this.addressFieldSet, fields ) );
        }


    },

    generateFieldSet: function( title, fieldSet, storeConfig )
    {
        var me = this;
        var fieldSetItem = {
            defaults: {
                bodyPadding: 10
            },
            xtype: 'fieldset',
            measureWidth: true,
            title: title
        };
        var fieldItems = [];
        Ext.Array.each( storeConfig, function ( item )
        {
            if ( fieldSet[item.type] )
            {
                var fieldValue;
                if (me.userFields){
                    fieldValue = me.userFields[item.type];
                    if ((fieldValue == null) && (me.userFields.userInfo != null)){
                        fieldValue = me.userFields.userInfo[item.type];
                    }
                }
                var baseConfig = {
                    fieldLabel: item.label || item.type,
                    fieldname: item.type,
                    required: item.required || false,
                    remote: item.remote || false,
                    readonly: item.readOnly || false,
                    fieldValue: fieldValue,
                    currentUser: me.currentUser
                };
                var createFunc = fieldSet[item.type];
                var newField = createFunc( item );
                newField = Ext.apply( newField, baseConfig );
                Ext.Array.include( fieldItems, newField );
            }
        }, this );
        if ( title == 'Address' )
        {
            return fieldItems;
        } else if ( fieldItems.length > 0 )
        {
            fieldSetItem.items = fieldItems;
            return fieldSetItem;
        }
        else
        {
            return [];
        }
    },

    generateAddressPanel: function ( field , closable, values)
    {
        var addressPanel = {
            xtype: 'addressPanel',
            values: values,
            closable: closable || false,
            readonly: field.readonly,
            iso: field.iso

        };
        return addressPanel;
    },

    setItemValue: function(itemId, value){
        var field = this.down('#' + itemId);
        if (field){
            field.setValue(value);
        }
    }

} );


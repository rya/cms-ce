Ext.define( 'CMS.view.user.EditUserFormPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.editUserFormPanel',
    store: 'UserStoreConfigStore',

    defaults: {
        bodyPadding: 10
    },

    autoScroll: true,

    title: 'User',
    modal: true,

    layout: {
        type: 'table',
        columns: 2,
        defaultMargins: {top:10, right:10, bottom:10, left:10},
        padding: 10,
        tdAttrs: {
            style:{
                padding: '10px'
            }
        }
    },

    buttons: [
        {
            text: 'Cancel',
            scope: this,
            handler: this.close
        },
        {
            text: 'Save',
            action: 'deleteUser'
        }
    ],

    initComponent: function()
    {
        this.store = Ext.data.StoreManager.lookup( this.store );
        this.userFieldSet = {
            'username': this.createTextField,
            'password': this.createPasswordField,
            'repeat_password': this.createPasswordField,
            'email': this.createTextField
        };
        this.nameFieldSet = {
            'display_name': this.createTextField,
            'prefix': this.createTextField,
            'first_name': this.createTextField,
            'middle_name': this.createTextField,
            'last_name': this.createTextField,
            'suffix': this.createTextField,
            'initials': this.createTextField,
            'nick_name': this.createTextField
        };
        this.photoFieldSet = {
            'photo': this.createPhotoField
        };
        this.detailsFieldSet = {
            'personal_id': this.createTextField,
            'member_id': this.createTextField,
            'organization': this.createTextField,
            'birthday': this.createDateField,
            'gender': this.createTextField,
            'title': this.createTextField,
            'description': this.createTextField,
            'html_e_mail': this.createTextField,
            'homepage': this.createTextField
        };
        this.locationFieldSet = {
            'timzone': this.createTextField,
            'locale': this.createComboBoxField,
            'country': this.createComboBoxField,
            'global_position': this.createTextField
        };
        this.communicationFieldSet = {
            'phone': this.createAutoCompleteField,
            'mobile': this.createAutoCompleteField,
            'fax': this.createAutoCompleteField
        };
        this.addressFieldSet = {
            'address': this.generateAddressFieldSet
        };
        this.callParent( arguments );
        this.removeAll();
        this.generateForm();
        this.show();
    },

    createAutoCompleteField: function ( field )
    {
        var callingCodeStore = Ext.data.StoreManager.lookup( 'CallingCodeStore' );
        var autoCompleteField = {
            xtype: 'combobox',
            store: callingCodeStore,
            triggeredAction: 'all',
            typeAhead: true,
            mode: 'remote',
            minChars: 1,
            forceSelection: true,
            hideTrigger: true,
            valueField: 'calling_code',
            displayField: 'calling_code',
            fieldLabel: field.fieldlabel,
            name: field.fieldname,
            itemId: field.fieldname,
            disabled: field.readonly,
            listConfig:{
                getInnerTpl: function()
                {
                    return '{calling_code} ({english_name})';
                }
            }
        };
        return autoCompleteField;
    },

    createComboBoxField: function ( field )
    {
        var fieldStore;
        var valueField;
        var displayField;
        if ( field.fieldname == 'country' )
        {
            fieldStore = Ext.data.StoreManager.lookup( 'CountryStore' );
            valueField = 'name';
            displayField = 'name';
        } else if ( field.fieldname == 'locale' )
        {
            fieldStore = Ext.data.StoreManager.lookup( 'LanguageStore' );
            valueField = 'languageCode';
            displayField = 'description';
        }

        var fieldConfig = {
            fieldLabel: field.fieldlabel,
            name: field.fieldname,
            itemId: field.fieldname,
            disabled: field.readonly
        };
        if ( fieldStore.getTotalCount() > 0 )
        {
            fieldConfig.xtype = 'combobox';
            fieldConfig.store = fieldStore;
            fieldConfig.valueField = valueField;
            fieldConfig.displayField = displayField;
        }
        else
        {
            fieldConfig.xtype = 'textfield';
        }
        return fieldConfig;
    },

    createTextField: function( field )
    {
        var textField = {xtype: 'textfield',
            enableKeyEvents: true,
            bubbleEvents: ['keyup'],
            fieldLabel: field.fieldlabel,
            disabled: field.readonly,
            name: field.fieldname,
            itemId: field.fieldname};
        if ( field.fieldname == 'display_name' )
        {
            textField.disabled = true;
        }
        return textField;
    },

    createPasswordField: function( field )
    {
        return {xtype: 'textfield',
            fieldLabel: field.fieldlabel,
            inputType: 'password',
            disabled: field.readonly,
            name: field.fieldname};
    },

    createPhotoField: function( field )
    {
        return {xtype: 'filefield',
            fieldLabel: field.fieldlabel,
            disabled: field.readonly,
            name: field.fieldname};
    },

    createDateField: function( field )
    {
        return {xtype: 'datefield',
            disabled: field.readonly,
            fieldLabel: field.fieldlabel,
            name: field.fieldname};
    },

    generateForm: function()
    {
        var storeConfig = this.store.first();
        this.add( this.generateFieldSet( 'User', 'this.userFieldSet', storeConfig ) );
        this.add( this.generateFieldSet( 'Name', 'this.nameFieldSet', storeConfig ) );
        this.add( this.generateFieldSet( 'Photo', 'this.photoFieldSet', storeConfig ) );
        this.add( this.generateFieldSet( 'Personal Information', 'this.detailsFieldSet', storeConfig ) );
        this.add( this.generateFieldSet( 'Location', 'this.locationFieldSet', storeConfig ) );
        this.add( this.generateFieldSet( 'Communication', 'this.communicationFieldSet', storeConfig ) );
        this.add( this.generateFieldSet( 'Address', 'this.addressFieldSet', storeConfig ) );
    },

    generateFieldSet: function( title, fieldSet, storeConfig )
    {
        var fieldSetItem = {
            measureWidth: true,
            measureHeight: true,
            defaults: {
                bodyPadding: 10
            },
            xtype: 'fieldset',
            title: title
        };
        var fieldItems = [];
        Ext.Array.each( storeConfig.raw.userfields, function ( item )
        {
            if ( eval( fieldSet + '.' + item.fieldname ) )
            {
                var createFunc = eval( fieldSet + '.' + item.fieldname );
                var newField = createFunc( item );
                Ext.Array.include( fieldItems, newField )
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

    generateAddressFieldSet: function ( field )
    {
        var countryField, regionField;
        if ( field.iso )
        {
            var countryStore = Ext.data.StoreManager.lookup( 'CountryStore' );
            var regionStore = Ext.data.StoreManager.lookup( 'RegionStore' );
            var countryField = {
                xtype: 'combobox',
                store: countryStore,
                fieldLabel: 'Country',
                valueField: 'name',
                displayField: 'name',
                name: 'address_country',
                itemId: 'address_country',
                disabled: field.readonly
            };
            var regionField = {
                xtype: 'combobox',
                store: regionStore,
                valueField: 'name',
                displayField: 'name',
                fieldLabel: 'Region',
                name: 'address_region',
                itemId: 'address_region',
                disabled: field.readonly
            };
        }
        else
        {
            var countryField = {
                xtype: 'textfield',
                fieldLabel: 'Country',
                name: 'address_country',
                itemId: 'address_country',
                disabled: field.readonly
            };
            var regionField = {
                xtype: 'textfield',
                fieldLabel: 'Region',
                name: 'address_region',
                itemId: 'address_region',
                disabled: field.readonly
            };
        }
        var fieldSetItem = {
            measureWidth: true,
            measureHeight: true,
            defaults: {
                bodyPadding: 10
            },
            xtype: 'fieldset',
            title: 'Address',
            items: [
                {
                    xtype: 'textfield',
                    fieldLabel: 'Label',
                    name: 'address_label',
                    itemId: 'address_label',
                    enableKeyEvents: true,
                    bubbleEvents: ['keyup'],
                    disabled: field.readonly
                },
                {
                    xtype: 'textfield',
                    fieldLabel: 'Street',
                    name: 'address_street',
                    itemId: 'address_street',
                    disabled: field.readonly
                },
                {
                    xtype: 'textfield',
                    fieldLabel: 'Postal Code',
                    name: 'address_postal_code',
                    itemId: 'address_postal_code',
                    disabled: field.readonly
                },
                {
                    xtype: 'textfield',
                    fieldLabel: 'Postal Address',
                    name: 'address_postal_address',
                    itemId: 'address_postal_address',
                    disabled: field.readonly
                },
                countryField,
                regionField
            ]
        };
        var tabItem = {
            title: '[no title]',
            items: [fieldSetItem]
        };
        var tabbedPanel = {
            xtype: 'tabpanel',
            itemId: 'addressTabPanel',
            height: 280,
            width: 300,
            items: [tabItem],
            buttons: [
                {
                    text: 'Add New Address',
                    action: 'addNewTab'
                }
            ]
        };
        return tabbedPanel;
    }

} );


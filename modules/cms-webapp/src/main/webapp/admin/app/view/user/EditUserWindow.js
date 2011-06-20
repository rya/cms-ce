Ext.define( 'CMS.view.user.EditUserWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.editUserWindow',
    store: 'UserStoreConfigStore',

    measureWidth: true,
    measureHeight: true,

    title: 'Edit user',
    modal: true,

    layout: {
        type: 'table',
        columns: 2,
        align: 'stretch',
        defaultMargins: {top:10, right:10, bottom:10, left:10},
        padding: 10
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
            'label': this.createTextField,
            'street': this.createTextField,
            'postal_code': this.createTextField,
            'postal_address': this.createTextField,
            'address_country': this.createTextField,
            'region': this.createTextField
        };
        this.callParent( arguments );
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
            itemId: field.fieldname
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
            name: field.fieldname};
    },

    createPhotoField: function( field )
    {
        return {xtype: 'textfield',
            fieldLabel: field.fieldlabel,
            inputType: 'file',
            name: field.fieldname};
    },

    createDateField: function( field )
    {
        return {xtype: 'datefield',
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
        if ( fieldItems.length > 0 )
        {
            fieldSetItem.items = fieldItems;
            return fieldSetItem;
        }
        else
        {
            return [];
        }
    },

    doShow: function()
    {
        this.removeAll();
        this.generateForm();
        this.show();

    }

} );


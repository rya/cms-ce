Ext.define( 'CMS.view.user.UserFormField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.userFormField',

    layout: 'hbox',

    initComponent: function()
    {
        this.fieldConfigBuilders = {
            'date': this.createDateConfig,
            'file': this.createFileConfig,
            'combo': this.createComboConfig,
            'autocomplete': this.createAutoCompleteConfig,
            'password': this.createPasswordConfig,
            'text': this.createTextConfig
        };
        this.items = [];
        var fieldConfig = {
            disabled: this.readonly,
            name: this.fieldname,
            itemId: this.fieldname,
            action: this.actionName
        };
        var builderFunction;
        if ( this.type )
        {
            builderFunction = this.fieldConfigBuilders[this.type];
        }
        else
        {
            builderFunction = this.fieldConfigBuilders.text;
        }
        fieldConfig = builderFunction( fieldConfig, this );

        if ( this.fieldname == 'display_name' )
        {
            fieldConfig.disabled = true;
            var lockButton = {
                xtype: 'button',
                iconCls: 'icon-locked',
                action: 'toggleDisplayNameField'
            };
            Ext.Array.include( this.items, [fieldConfig, lockButton] );
        }
        else
        {
            Ext.Array.include( this.items, fieldConfig );
        }
        if ( this.required )
        {
            this.fieldLabel =
                    this.fieldLabel + "<span style=\"color:red;\" ext:qtip=\"This field is required\">*</span>";
        }
        if ( this.remote )
        {
            var remoteIcon = {xtype: 'image', src: 'resources/images/icon_remote_10px.gif'};
            Ext.Array.include( this.items, remoteIcon );
        }

        this.callParent( arguments );
    },

    createDateConfig: function( fieldConfig )
    {
        var dateConfig = {xtype: 'datefield'};
        return Ext.apply(fieldConfig, dateConfig);
    },

    createComboConfig: function( fieldConfig, me )
    {
        var comboConfig;
        if ( me.fieldStore.getTotalCount() > 0 )
        {
            comboConfig = {
                xtype: 'combobox',
                store: me.fieldStore,
                valueField: me.valueField,
                displayField: me.displayField
            };
        }
        else
        {
            comboConfig = {xtype: 'textfield'};
        }
        return Ext.apply(fieldConfig, comboConfig);
    },

    createAutoCompleteConfig: function( fieldConfig, me )
    {
        var autoCompleteConfig = {
            xtype: 'combobox',
            store: me.fieldStore,
            triggeredAction: 'all',
            typeAhead: true,
            queryMode: 'local',
            minChars: 1,
            forceSelection: true,
            hideTrigger: true,
            valueField: me.valueField,
            displayField: me.displayField,
            listConfig: me.displayConfig
        };
        return Ext.apply(fieldConfig, autoCompleteConfig);
    },

    createPasswordConfig: function( fieldConfig )
    {
        var passwordConfig = {
            xtype: 'textfield',
            inputType: 'password'
        };
        return Ext.apply(fieldConfig, passwordConfig);
    },

    createFileConfig: function( fieldConfig )
    {
        var fileConfig = {xtype: 'filefield'};
        return Ext.apply(fieldConfig, fileConfig);
    },

    createTextConfig: function( fieldConfig )
    {
        var textConfig = {
            xtype: 'textfield',
            enableKeyEvents: true,
            bubbleEvents: ['keyup']
        };
        return Ext.apply(fieldConfig, textConfig);
    }
} );
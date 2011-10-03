Ext.define( 'App.view.EditUserPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.editUserPanel',


    autoScroll: true,

    items: [],
    currentUser: '',

    bodyStyle: {
        background: 'white'
    },

    layout: {
        type: 'border'
    },

    border: 0,

    initComponent: function()
    {
        var me = this;
        var editUserFormPanel = {
            xtype: 'editUserFormPanel',
            region: 'center',
            userFields: [],
            margin: 5,
            autoScroll: true,
            currentUser: me.currentUser,
            flex: 1
        };
        var prefPanel = {
            xtype: 'userPreferencesPanel',
            region: 'east',
            userFields: me.userFields,
            margin: 5,
            flex: 0.2
        };
        var headerPanelTpl = Ext.Template('<div class="cms-edit-form-header clearfix">' +
            '<div class="left">' +
            '<img alt="User" src="data/user/photo?key={key}"/></div>' +
            '<div class="right">' +
            '<h1><input id="display-name" type="text" value="{displayName}" readonly="true" class="cms-display-name"/></h1><a href="javascript:;" class="edit-button"></a>' +
            '<p>{qualifiedName}</p></div></div>');
        var headerPanel = {
            xtype: 'panel',
            tpl: headerPanelTpl,
            border: 0,
            region: 'north',
            styleHtmlContent: true,
            listeners: {
                click: {
                    element: 'body',
                    fn: this.toggleDisplayNameField,
                    data: me.currentUser
                }
            },
            data: me.currentUser
        };
        if ( this.userFields != null )
        {
            editUserFormPanel.userFields = this.userFields;
        }
        me.items = [ editUserFormPanel, prefPanel, headerPanel ];
        this.callParent( arguments );

    },

    toggleDisplayNameField: function(e, t){
        var className = t.attributes.getNamedItem('class').nodeValue;
        if (className == 'edit-button'){
            var displayNameField = Ext.get('display-name');
            var readonly = displayNameField.getAttribute('readonly');
            if (readonly){
                displayNameField.dom.removeAttribute('readonly');
            }else{
                displayNameField.set({readonly: true});
            }
            displayNameField.toggleCls('cms-display-name-editable');
        }
    }

} );

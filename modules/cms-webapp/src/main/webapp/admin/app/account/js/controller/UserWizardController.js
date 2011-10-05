Ext.define( 'App.controller.UserWizardController', {
    extend: 'Ext.app.Controller',

    stores: ['UserstoreConfigStore'],
    models: ['UserstoreConfigModel'],
    views: [],

    init: function()
    {
        this.control( {
                          '*[action=wizardPrev]': {
                              click: this.wizardPrev
                          },
                          '*[action=wizardNext]': {
                              click: this.wizardNext
                          },
                          'wizardPanel': {
                              beforestepchanged: this.validateStep,
                              stepchanged: this.stepChanged,
                              finished: this.wizardFinished
                          },
                          'userStoreListPanel': {
                              itemclick: this.userStoreSelected,
                              afterrender: this.userStoreAfterRender
                          }
                      } );
    },

    validateStep: function( wizard, step )
    {
        var data = undefined;
        if (step.getData){
            data = step.getData();
        }
        if (data){
            wizard.addData( data );
        }
        return true;
    },

    stepChanged: function( wizard, oldStep, newStep )
    {
        if ((newStep.getXType() == 'editUserFormPanel') &&
                (oldStep.getXType() == 'userStoreListPanel')){
            var prefix = newStep.down('#prefix');
            var firstName = newStep.down('#first-name');
            var middleName = newStep.down( '#middle-name' );
            var lastName = newStep.down( '#last-name' );
            var suffix = newStep.down( '#suffix' );
            if (prefix) prefix.on('keyup', this.textFieldHandleEnterKey);
            if (firstName) firstName.on('keyup', this.textFieldHandleEnterKey);
            if (middleName) middleName.on('keyup', this.textFieldHandleEnterKey);
            if (lastName) lastName.on('keyup', this.textFieldHandleEnterKey);
            if (suffix) suffix.on('keyup', this.textFieldHandleEnterKey);
        }
        if (oldStep.getXType() == 'userStoreListPanel'){
            var userStore = wizard.getData().userStore;
            Ext.get('q-userstore').dom.innerHTML = userStore + '\\';
            Ext.get('q-username').dom.innerHTML = 'unnamed';
        }
    },

    wizardFinished: function( wizard, data )
    {

    },

    wizardPrev: function( btn, evt )
    {
        var w = this.getWizardPanel();
        w.prev( btn );
    },

    wizardNext: function( btn, evt )
    {
        var w = this.getWizardPanel();
        w.next( btn );
    },

    userStoreSelected: function(view, record, item)
    {
        view.setData(record);
        var itemElement = new Ext.Element( item );
        itemElement.highlight( '9B30FF' );
        var radioButton = itemElement.down( 'input' );
        radioButton.dom.checked = true;
        var nextButton = this.getUserWizardPanel().down('#next');
        var userForm = this.getUserWizardPanel().down('#userForm');
        userForm.renderUserForm({userStore: record.get('name')});
        this.wizardNext(nextButton);
    },

    userStoreAfterRender: function(view)
    {
        if (view.getNodes().length == 1){
            var node = Ext.fly(view.getNodes()[0]);
            var radioButton = node.down('input');
            radioButton.dom.checked = true;
            var nextButton = this.getUserWizardPanel().down('#next');
            var userForm = this.getUserWizardPanel().down('#userForm');
            userForm.renderUserForm({userStore: record.get('name')});
            this.wizardNext(nextButton);
        }

    },

    textFieldHandleEnterKey: function( field, event )
    {
        var formPanel = field.up( 'editUserFormPanel' );
        var prefix = formPanel.down( '#prefix' ) ? Ext.String.trim( formPanel.down( '#prefix' ).getValue() ) : '';
        var firstName = formPanel.down( '#first-name' ) ? Ext.String.trim( formPanel.down( '#first-name' ).getValue() )
                : '';
        var middleName = formPanel.down( '#middle-name' )
                ? Ext.String.trim( formPanel.down( '#middle-name' ).getValue() ) : '';
        var lastName = formPanel.down( '#last-name' ) ? Ext.String.trim( formPanel.down( '#last-name' ).getValue() )
                : '';
        var suffix = formPanel.down( '#suffix' ) ? Ext.String.trim( formPanel.down( '#suffix' ).getValue() ) : '';
        var displayName = Ext.get('display-name');
        if ( displayName )
        {
            var displayNameValue = prefix + ' ' + firstName + ' ' + middleName + ' ' + lastName + ' ' + suffix;
            displayName.dom.value = Ext.String.trim( displayNameValue );
            displayName.addCls('cms-edited-field');
        }
    },

    userImageClicked: function()
    {
        alert('clicked');
    },


    getUserWizardPanel: function()
    {
        return Ext.ComponentQuery.query( 'userWizardPanel' )[0];
    },

    getWizardPanel: function()
    {
        return Ext.ComponentQuery.query( 'wizardPanel' )[0];
    }

} );

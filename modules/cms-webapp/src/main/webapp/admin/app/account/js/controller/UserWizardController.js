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
            return true;
        }
        return false;
    },

    stepChanged: function( wizard, oldStep, newStep )
    {
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

    userStoreSelected: function(view, record, item){
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

    userStoreAfterRender: function(view){
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

    getUserWizardPanel: function()
    {
        return Ext.ComponentQuery.query( 'userWizardPanel' )[0];
    },


    getWizardPanel: function()
    {
        return Ext.ComponentQuery.query( 'wizardPanel' )[0];
    }

} );

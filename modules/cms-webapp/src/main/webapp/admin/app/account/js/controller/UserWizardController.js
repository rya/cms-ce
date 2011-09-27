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
                          }
                      } );
    },

    validateStep: function( wizard, step )
    {
        console.log( 'beforestepchanged\n' + wizard + '\n' + step );
        var data = {};
        data[ step.title ] = true;
        wizard.addData( data );
    },

    stepChanged: function( wizard, oldStep, newStep )
    {
        console.log( 'stepchanged\n' + wizard + '\n' + oldStep + '\n' + newStep );
    },

    wizardFinished: function( wizard, data )
    {
        console.log( 'finished\n' + data );
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

    getWizardPanel: function()
    {
        return Ext.ComponentQuery.query( 'wizardPanel' )[0];
    }

} );

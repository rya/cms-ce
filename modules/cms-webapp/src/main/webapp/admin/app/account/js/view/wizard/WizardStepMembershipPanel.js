Ext.define( 'App.view.wizard.WizardStepMembershipPanel', {
    extend: 'Ext.form.Panel',
    alias : 'widget.wizardStepMembershipPanel',

    requires: [ 'Common.BoxSelect' ],
    border: false,

    initComponent: function()
    {
        this.items = [
            {
                xtype: 'fieldset',
                title: 'Member of',
                padding: '10px 15px',
                items: [
                    {
                        allowBlank:false,
                        forceSelection : true,
                        xtype:'boxselect',
                        resizable: false,
                        name: 'memberships',
                        anchor:'100%',
                        store: 'GroupStore',
                        mode: 'local',
                        displayField: 'name',
                        displayFieldTpl: '{name} ({userStore})',
                        valueField: 'key'
                    }
                ]
            }
        ];

        this.callParent( arguments );
    }

} );

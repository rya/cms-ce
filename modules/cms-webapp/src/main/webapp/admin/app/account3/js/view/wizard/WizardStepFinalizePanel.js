Ext.define( 'App.view.wizard.WizardStepFinalizePanel', {
    extend: 'Ext.form.Panel',
    alias : 'widget.wizardStepFinalizePanel',

    border: false,

    items: [
        {
            xtype: 'fieldset',
            title: 'Notify User',
            padding: '10px 15px',
            defaults: {
                xtype: 'textfield',
                anchor: '100%'
            },
            items: [
                {
                    xtype: 'checkbox',
                    checked: true,
                    name: 'sendEmail',
                    fieldLabel: 'Send E-mail'
                },
                {
                    name: 'to',
                    fieldLabel: 'To'
                },
                {
                    name: 'subject',
                    fieldLabel: 'Subject'
                },
                {
                    xtype: 'textarea',
                    rows: 5,
                    name: 'message',
                    fieldLabel: 'Message'
                }
            ]
        }
    ],

    initComponent: function()
    {
        this.callParent( arguments );
    }

} );
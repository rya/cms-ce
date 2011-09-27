Ext.define( 'App.view.UserWizardPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userWizardPanel',
    requires: ['Common.WizardPanel'],

    layout: {
        type: 'hbox',
        align: 'stretch',
        padding: 10
    },

    defaults: {
        border: false
    },

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            defaults: {
                scale: 'medium',
                iconAlign: 'top'
            },
            items: [
                {
                    text: 'save',
                    action: 'wizardSave'
                },
                '->',
                {
                    text: 'Prev',
                    itemId: 'prev',
                    disabled: true,
                    action: 'wizardPrev'
                },
                {
                    text: 'Next',
                    itemId: 'next',
                    action: 'wizardNext'
                }
            ]
        }
    ],

    items: [
        {
            html: 'user ico',
            width: 100
        },
        {
            flex: 1,
            layout: {
                type: 'vbox',
                align: 'stretch',
                padding: '0 10'
            },
            defaults: {
                border: false
            },
            items: [
                {
                    styleHtmlContent: true,
                    html: '<h1>New User</h1><h4>User Wizard</h4>'
                },
                {
                    flex: 1,
                    xtype: 'wizardPanel',
                    showControls: false,
                    items: [
                        {
                            stepTitle: "Step 1",
                            html: 'Panel 1<br/> gf dg 34 geg dfg dfg dfg<br/> sew4r wr wer wer'
                        },
                        {
                            title: "Step 2",
                            html: 'Panel 2<br/> dfs dfwe fhgh wer wvd fggf '
                        },
                        {
                            stepTitle: "Step 3",
                            html: 'Panel 3'
                        },
                        {
                            stepTitle: "Step 4",
                            html: 'Panel 4<br/> dfg we ewgr er gwe ergerg ergewr gsd'
                        },
                        {
                            stepTitle: "Step 5",
                            html: 'Panel 5<br/> rtewr wr wer wer wertg dfg df'
                        }
                    ]
                }
            ]
        }
    ],

    initComponent: function()
    {
        this.callParent( arguments );
    }

} );

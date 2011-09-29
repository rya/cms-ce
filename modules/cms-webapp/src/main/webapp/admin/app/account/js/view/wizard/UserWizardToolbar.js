Ext.define('App.view.wizard.UserWizardToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias : 'widget.userWizardToolbar',

    border: false,

    initComponent: function() {

        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        this.items = [
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Save',
                        iconCls: 'icon-btn-save-24'
                    }
                ]
            },
            '->',
            {
                xtype: 'buttongroup',
                columns: 3,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Previous',
                        iconCls: 'icon-btn-arrow-left-24',
                        itemId: 'prev',
                        disabled: true,
                        action: 'wizardPrev'
                    },
                    {
                        text: 'Next',
                        iconCls: 'icon-btn-arrow-right-24',
                        itemId: 'next',
                        action: 'wizardNext'
                    },
                    {
                        text: 'Finish',
                        iconCls: 'icon-btn-tick-24',
                        disabled: true,
                        itemId: 'finish',
                        action: 'wizardNext'
                    }
                ]
            }
        ];
        this.callParent(arguments);
    }

});

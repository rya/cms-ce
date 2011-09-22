Ext.define('App.view.UserWizardToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias : 'widget.userWizardToolbar',

    border: false,

    initComponent: function() {

        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top'
        };

        this.items = [
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Save'
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
                        text: 'Previous'
                    },
                    {
                        text: 'Next'
                    },
                    {
                        text: 'Finish'
                    }
                ]
            }
        ];

        this.callParent(arguments);
    }

});

Ext.define('App.view.UserWizard', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.userWizard',


    layout: 'border',
    border: false,
    padding: 0,

    initComponent: function() {
        this.items = [
            {
                region: 'center',
                xtype: 'panel',
                flex: 2
            }
        ];
        this.dockedItems = [
               {
                   xtype: 'userWizardToolbar',
                   dock: 'top'
               }],

        this.callParent(arguments);
    }

});

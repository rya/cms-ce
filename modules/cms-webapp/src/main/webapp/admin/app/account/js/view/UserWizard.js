Ext.define('App.view.UserWizard', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.userWizard',


    layout: 'card',
    border: false,
    padding: 0,

    initComponent: function() {
        this.items = [
            {
                xtype: 'userStoreListPanel'
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

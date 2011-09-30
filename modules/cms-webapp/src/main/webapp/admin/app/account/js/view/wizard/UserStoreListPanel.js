Ext.define('App.view.wizard.UserStoreListPanel', {
    extend: 'Ext.view.View',
    alias : 'widget.userStoreListPanel',
    border: false,
    padding: 0,
    store: 'UserstoreConfigStore',

    initComponent: function() {
        var tpl = Templates.account.userstoreRadioButton;
        this.tpl = tpl;
        this.itemSelector = 'div.userstore';
        this.listeners = {
            itemclick: function(view, record, item){
                Ext.fly( item ).highlight();
                var radioButton = new Ext.Element(item).down('input');
                radioButton.dom.checked = true;
            }
        };
        this.callParent(arguments);
    }

});

Ext.define('App.view.wizard.UserStoreListPanel', {
    extend: 'Ext.view.View',
    alias : 'widget.userStoreListPanel',
    border: false,
    padding: 0,
    store: 'UserstoreConfigStore',

    initComponent: function() {
        var tpl = '<tpl for=".">' +
                    '<div class="userstore">' +
                    '<input type="radio" name="userstore" value="{key}">' +
                    '<div class="userstore-block">' +
                        '<div class="left"><img width="50" height="50" src="app/account/images/app-icon-userstores.png"/></div>' +
                        '<div class="center">' +
                            '<h2>{name}</h2>' +
                            '<p>(usersstores\\\\{name})</p>' +
                        '</div>' +
                    '</div>' +
                    '</div><br>' +
                '</tpl>';
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

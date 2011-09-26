Ext.define('App.view.UserStoreListPanel', {
    extend: 'Ext.view.View',
    alias : 'widget.userStoreListPanel',


    border: false,
    padding: 0,
    store: 'UserstoreConfigStore',

    initComponent: function() {
        var tpl = '<tpl for=".">' +
                '<div class="userstore">' +
                '<input type="radio" name="userstore" value="{key}">' +
                '<p>{name}</p>' +
                '</div>' +
                '</tpl>';
        this.tpl = tpl;
        this.itemSelector = 'div.userstore'
        this.callParent(arguments);
    }

});

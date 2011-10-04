Ext.define('App.view.wizard.UserStoreListPanel', {
    extend: 'Ext.view.View',
    alias : 'widget.userStoreListPanel',
    border: false,
    padding: 0,
    store: 'UserstoreConfigStore',

    initComponent: function() {/*
        var tpl = '<tpl for=".">' +
                    '<div class="cms-userstore">' +
                    '<input type="radio" name="userstore" value="{key}">' +
                    '<div class="cms-userstore-block">' +
                        '<div class="left"><img width="50" height="50" src="app/account/images/app-icon-userstores.png"/></div>' +
                        '<div class="center">' +
                            '<h2>{name}</h2>' +
                            '<p>(usersstores\\\\{name})</p>' +
                        '</div>' +
                    '</div>' +
                    '</div><br>' +
                '</tpl>'; */
        var tpl = Templates.account.userstoreRadioButton;
        this.tpl = tpl;
        this.itemSelector = 'div.cms-userstore';
        this.callParent(arguments);
    },

    getData: function(){
        if (this.selectedUserStore){
            return {
                userStore: this.selectedUserStore.get('name')
            };
        }else{
            return undefined;
        }
    },

    setData: function(record){
        this.selectedUserStore = record;
    }

});

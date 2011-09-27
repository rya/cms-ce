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
                Ext.create('Ext.fx.Animator', {
                    target: item,
                    duration: 1000,
                    easing: 'easeIn',
                    keyframes: {
                        '0%': {
                            opacity: 1,
                            backgroundColor: '9B30FF'
                        },
                        '20%': {
                            opacity: 0.5
                        },
                        '40%': {
                            opacity: 0.3
                        },
                        '60%': {
                            opacity: 0.5
                        },
                        '80%': {
                            opacity: 1
                        },
                        '100%': {
                            backgroundColor: 'transparent'
                        }
                    }
                });
                var radioButton = new Ext.Element(item).down('input');
                radioButton.dom.checked = true;
            }
        };
        this.callParent(arguments);
    }

});

Ext.define('CMS.view.user.Filter', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userFilter',

    title: 'Filter',
    split: true,
    collapsible: true,

    userstore: {
        style : 'text-decoration: underline; cursor: pointer; color: blue',
        listeners : {
           render: function() {
              Ext.fly(this.el).on('click', function(e, t) {
                  Ext.select('.usertore-list').applyStyles( { fontWeight : 'normal'} );
                  var fly =  Ext.fly( t );
                  var checked = fly.getStyle('fontWeight') === 'bold';
                  if (!checked) fly.applyStyles( { fontWeight : 'bold' } );
              });
           },
           scope: this.el
        }
    },

    initComponent: function() {
        var search = {
            xtype: 'fieldcontainer',
            layout: 'hbox',

            items: [{
                xtype: 'textfield',
                name: 'filter',
                flex: 1
            },{
                xtype: 'button',
                icon: 'resources/images/find.png',
                action: 'search',
                margins: '0 0 0 5'
            }]
        };


        var filter = {
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            border: false,
            bodyPadding: 10,

            defaults: {
                margins: '0 0 0 0'
            },

            items: [search, {
                xtype: 'label',
                style: 'font-weight:bold;',
                text: 'Type',
                margins: '0 0 2 0'
            }, {
                xtype: 'checkbox',
                boxLabel: 'Users',
                margins: '0 0 0 8'
            }, {
                xtype: 'checkbox',
                boxLabel: 'Groups',
                margins: '0 0 0 8'
            }, {
                xtype: 'label',
                style: 'font-weight: bold',
                text: 'Userstore',
                margins: '4 0 2 0'
            }, {
                xtype: 'label',
                text: 'default',
                cls: 'usertore-list',
                margins: '0 0 0 8',
                style: this.userstore.style + '; font-weight : bold',
                listeners: this.userstore.listeners
            }, {
                xtype: 'label',
                text: 'global',
                cls: 'usertore-list',
                margins: '0 0 0 8',
                style: this.userstore.style,
                listeners: this.userstore.listeners
            }]
        };

 		Ext.apply(this, filter);


        this.callParent(arguments);
    }
});

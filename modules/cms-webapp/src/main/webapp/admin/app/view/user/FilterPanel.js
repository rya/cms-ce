Ext.define('CMS.view.user.FilterPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userFilter',
    cls: 'facet-navigation',

    title: 'Filter',
    split: true,
    collapsible: true,

    facetType: {
        listeners : {
           change: function(field, newVal, oldVal) {
               var boxLabelEl = field.boxLabelEl;
               if (field.checked) {
                   boxLabelEl.addCls('facet-selected');
               } else {
                   boxLabelEl.removeCls('facet-selected');
               }
           },
           scope: this.el
        }
    },

    facetUserstore: {
        listeners : {
           render: function() {
              Ext.fly(this.el).on('click', function(e, t) {
                  Ext.select('.facet-single-select-item').each(function(el) {
                      if (el.dom == t) {
                          el.addCls('facet-selected');
                      } else {
                          el.removeCls('facet-selected');
                      }
                  }, this);
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
                enableKeyEvents: true,
                bubbleEvents: ['enterKeyPress'],
                id: 'filter',
                name: 'filter',
                flex: 1
            },{
                xtype: 'button',
                iconCls: 'find',
                action: 'search',
                margins: '0 0 0 5'
            }]
        };


        var filter = {
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            border: true,
            bodyPadding: 10,

            defaults: {
                margins: '0 0 0 0'
            },

            items: [search, {
                xtype: 'label',
                text: 'Type',
                cls: 'facet-header'
            }, {
                xtype: 'checkbox',
                boxLabel: 'Users',
                cls: 'facet-multi-select-item',
                listeners: this.facetType.listeners
            }, {
                xtype: 'checkbox',
                boxLabel: 'Groups',
                cls: 'facet-multi-select-item',
                listeners: this.facetType.listeners
            }, {
                xtype: 'label',
                text: 'Userstore',
                cls: 'facet-header'
            }, {
                xtype: 'label',
                text: 'default',
                cls: 'facet-single-select-item',
                listeners: this.facetUserstore.listeners
            }, {
                xtype: 'label',
                text: 'global',
                cls: 'facet-single-select-item',
                listeners: this.facetUserstore.listeners
            }]
        };

 		Ext.apply(this, filter);

        this.callParent(arguments);
    }
});

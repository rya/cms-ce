Ext.define('App.view.FilterPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userFilter',
    cls: 'facet-navigation',

    title: 'Filter',
    split: true,
    collapsible: true,

    initComponent: function() {
        var search = {
            xtype: 'fieldcontainer',
            layout: 'hbox',

            items: [
                {
                    xtype: 'textfield',
                    enableKeyEvents: true,
                    bubbleEvents: ['specialkey'],
                    id: 'filter',
                    name: 'filter',
                    flex: 1
                },
                {
                    xtype: 'button',
                    iconCls: 'icon-find',
                    action: 'search',
                    margins: '0 0 0 5'
                }
            ]
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

            items: [
                search,
                {
                    xtype: 'label',
                    text: 'Type',
                    cls: 'facet-header'
                },
                {
                    xtype: 'checkbox',
                    boxLabel: 'Users (917)',
                    cls: 'facet-multi-select-item',
                    checkedCls: 'x-form-cb-checked facet-selected'
                },
                {
                    xtype: 'checkbox',
                    boxLabel: 'Groups (5)',
                    cls: 'facet-multi-select-item',
                    checkedCls: 'x-form-cb-checked facet-selected'
                },
                {
                    xtype: 'label',
                    text: 'Userstore',
                    cls: 'facet-header'
                },
                {
                    xtype: 'radiogroup',
                    columns: 1,
                    vertical: true,

                    defaults: {
                        name: 'userstore',
                        cls: 'facet-single-select-item',
                        checkedCls: 'x-form-cb-checked facet-selected',
                        width: 170
                    },

                    items: [
                        { boxLabel: 'default (922)', inputValue: '1', checked: true },
                        { boxLabel: 'global (124)', inputValue: '2'},
                        { boxLabel: 'department 5 (748)', inputValue: '3'}
                    ]
                }
            ]
        };

 		Ext.apply(this, filter);

        this.callParent(arguments);
    }
});

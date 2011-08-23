Ext.define( 'CMS.view.user.UserPreferencesPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userPreferencesPanel',

    xtype: 'panel',
    layout: 'accordion',
    layoutConfig:
    {
        autoWidth: true
    },

    initComponent: function()
    {
        var groupPanel = this.generateGroupPanel(this.userFields);
        var groupSearch = {
                xtype: 'combobox',
                store: 'GroupStore',
                triggeredAction: 'all',
                typeAhead: true,
                queryMode: 'local',
                minChars: 1,
                forceSelection: true,
                hideTrigger: true,
                valueField: 'key',
                displayField: 'name',
                action: 'selectGroup',
                listConfig: {
                    getInnerTpl: function()
                    {
                        return '<div style="white-space: nowrap;">{name} ({userStore})</div>';
                    }
                }
            };
        var groupsPanel = {
            xtype: 'panel',
            layout: {
                type: 'vbox',
                align: 'stretchmax'
            },
            title: 'Groups',
            items: [groupSearch, groupPanel]
        };

        this.items = [groupsPanel];
        this.callParent( arguments );
    },

    generateGroupPanel: function (userData){
        var groupFields = [];
        Ext.Array.each(userData.groups, function(group){
            var groupField = {
                xtype: 'groupDetailButton',
                value: group.name
            };
            Ext.Array.include(groupFields, groupField);
        });
        var groupPanel = {
            xtype: 'panel',
            itemId: 'groupPanel',
            layout: 'column',
            flex: 1
        };
        if (groupFields.length > 0){
            groupPanel.items = groupFields;
            return groupPanel;
        }else{
            return groupPanel;
        }
    }
} )

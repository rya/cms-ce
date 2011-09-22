Ext.define( 'App.view.UserPreferencesPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userPreferencesPanel',

    layout: 'accordion',

    initComponent: function()
    {
        var groupPanel = this.generateGroupPanel( this.userFields );
        var groupSearch = {
            xtype: 'combobox',
            store: 'GroupStore',
            triggeredAction: 'all',
            typeAhead: true,
            queryMode: 'remote',
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

    generateGroupPanel: function ( userData )
    {
        var groupFields = [];
        var groupKeys = [];
        Ext.Array.each( userData.groups, function( group )
        {
            var groupField = {
                xtype: 'groupDetailButton',
                value: group.name,
                key: group.key
            };
            Ext.Array.include( groupFields, groupField );
            Ext.Array.include( groupKeys, group.key );
        } );
        var groupPanel = {
            xtype: 'panel',
            itemId: 'groupPanel',
            groupKeys: groupKeys,
            layout: 'column',
            border: 0,
            flex: 1,
            removeItem: function( item )
            {
                this.remove( item );
                Ext.Array.remove( this.groupKeys, item.key );
            },
            addItem: function( item )
            {
                this.add( item );
                Ext.Array.include( this.groupKeys, item.key );
            }
        };
        if ( groupFields.length > 0 )
        {
            groupPanel.items = groupFields;
            return groupPanel;
        }
        else
        {
            return groupPanel;
        }
    }
} );

Ext.define( 'CMS.view.user.UserPreferencesPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userPreferencesPanel',

    xtype: 'panel',
    width: 230,
    layout: 'accordion',
    activeItem: 1,
    height: 600,
    layoutConfig:
    {
        autoWidth: false
    },

    initComponent: function()
    {
        var groupsPanel = {
            xtype: 'panel',
            title: 'Groups'
        };
        this.callParent( arguments );
    },

    generateGroupsFieldSet: function (userData){
        var groupFields = [];
        Ext.Array.each(userData.groups, function(group){
            var groupField = {
                    xtype: 'button',
                    text: group.name,
                    iconCls: 'icon-group',
                    cls: 'group-display',
                    margin: 5
                };
            Ext.Array.include(groupFields, groupField);
        });
        if (groupFields.length > 0){
            var groupFieldSet = {
                xtype: 'panel',
                layout: 'column',
                items: groupFields
            };
            return groupFieldSet;
        }else{
            return null;
        }
    }
} )

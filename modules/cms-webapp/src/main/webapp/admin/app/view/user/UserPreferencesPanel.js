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
    }
    ,
    items: [
        {
            xtype: 'form',
            title: 'Groups',
            items: [
                {
                    xtype: 'panel',
                    border: false,
                    items: [
                        {
                            xtype: 'textfield',
                            style: 'margin-bottom: 10px'
                        }
                    ]
                },
                {
                    xtype: 'container',
                    height: 100,
                    style: 'overflow:hidden;',
                    items: [
                        {
                            xtype: 'button',
                            text: 'Laaaaaaaaaaaaaang gruppe  x',
                            style: 'float:left;margin-right:5px;margin-bottom:5px;'
                        },
                        {
                            xtype: 'button',
                            text: 'Group Name  x',
                            style: 'float:left;margin-right:5px;margin-bottom:5px;'
                        },
                        {
                            xtype: 'button',
                            text: 'Enonic  x',
                            style: 'float:left;margin-right:5px;margin-bottom:5px;'
                        },
                        {
                            xtype: 'button',
                            text: 'David Gruppen x',
                            style: 'float:left;margin-right:5px;margin-bottom:5px;'
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'panel',
            title: 'Preferences',
            border: false
        },
        {
            xtype: 'panel',
            title: 'Log',
            border: false
        }
    ]
} )

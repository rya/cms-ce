Ext.define('CMS.view.system.NavigationPanel', {
    extend: 'Ext.tree.Panel',
    alias: 'widget.systemNavigation',

    title: 'Navigation',
    split: true,
    collapsible: true,

    rootVisible: false,


    initComponent: function() {
        this.store = Ext.create('Ext.data.TreeStore', {
            fields: ["text", "leaf", "appUrl"],
            root: {
                expanded: true,
                children: this.getChildren()
            }
        });
        this.callParent(arguments);
    },

    getChildren: function() {
        return [
            {text:"About", leaf: true, appUrl:"about.html"},
            {text:"Properties", leaf: true, appUrl:"properties.html"},
            {text:"Cache", leaf: true, appUrl:"systemCaches.html"},
            {text:"Userstores", leaf: true, appUrl:"userstores.html"},
            {text:"Content Types", leaf: true, appUrl:"contentTypes.html"},
            {text:"Languages", leaf: true, appUrl:"languages.html"},
            {text:"Countries", leaf: true, appUrl:"blank.html"},
            {text:"Time Zones", leaf: true, appUrl:"blank.html"},
            {text:"Sites", leaf: true, appUrl:"blank.html"},
            {text:"Cluster", leaf: true, appUrl:"blank.html"},
            {text:"Scheduler", leaf: true, appUrl:"blank.html"},
            {text:"Audit Log", leaf: true, appUrl:"blank.html"}
        ];
    }

});

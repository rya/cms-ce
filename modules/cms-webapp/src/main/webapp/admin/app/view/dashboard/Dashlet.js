Ext.define('CMS.view.dashboard.Dashlet', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.dashlet',

    cls: 'dashlet',
    bodyCls: 'dashlet-body',
    title: 'Dashlet Panel',

    collapsible: true,
    draggable: true,
    closable: true,
    border: false,
    frame: true,
    minHeight: 200,
    autoHeight: true,
    bodyPadding: 10,
    layout: "fit",

    html: 'Dashlet text',

    initComponent: function() {
        this.tools = this.getTools();
        this.callParent(arguments);
    },

    getTools: function() {
        return [{
            xtype: 'tool',
            type: 'gear',
            handler: function(e, target, panelHeader, tool){
                var dashlet = panelHeader.ownerCt;
                dashlet.setLoading('Working...');
                Ext.defer(function() {
                    dashlet.setLoading(false);
                }, 2000);
            }
        }];
    }
});
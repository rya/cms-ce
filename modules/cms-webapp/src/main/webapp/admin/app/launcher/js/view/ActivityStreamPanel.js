Ext.define('App.view.ActivityStreamPanel', {
    extend: 'Ext.panel.Panel',
    alias : 'widget.activityStream',
    title: 'Activity Stream',
    collapsible: true,
    width: 200,
    minWidth: 200,
    maxWidth: 200,
    bodyCls: 'cms-activity-stream-background',

    initComponent: function() {
        this.callParent(arguments);
    }

});

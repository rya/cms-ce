Ext.define("CMS.view.dashboard.DashletColumn", {
    extend: "Ext.container.Container",
    alias: "widget.dashletColumn",
    layout: {
        type: "anchor"
    },
    defaultType: "dashlet",
    cls: "dashlet-column",
    autoHeight: true
});
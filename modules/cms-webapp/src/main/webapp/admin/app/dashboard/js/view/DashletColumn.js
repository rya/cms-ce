Ext.define("App.view.DashletColumn", {
    extend: "Ext.container.Container",
    alias: "widget.dashletColumn",
    layout: {
        type: "anchor"
    },
    defaultType: "dashlet",
    cls: "dashlet-column",
    autoHeight: false
});
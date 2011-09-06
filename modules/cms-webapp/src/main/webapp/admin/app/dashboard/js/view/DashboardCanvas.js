Ext.define( "App.view.DashboardCanvas", {
    extend: "Ext.panel.Panel",
    alias: 'widget.dashboardCanvas',

    layout: 'column',
    defaults: {
        columnWidth: 1/3,
        border: false
    },

    requires: [
        "Ext.layout.component.Body",
        "App.view.DropTarget",
        "App.view.DashletColumn"
    ],
    cls: "dashboard",
    bodyCls: "dashboard-body",
    defaultType: "dashletColumn",
    componentLayout: "body",
    autoScroll: true,

    initComponent: function () {
        var a = this;
        this.callParent();
        this.addEvents({
            validatedrop: true,
            beforedragover: true,
            dragover: true,
            beforedrop: true,
            drop: true
        });
        this.on("drop", this.doLayout, this)
    },

    beforeLayout: function () {
        var b = this.layout.getLayoutItems(),
            a = b.length,
            c = 0,
            d;
        for (; c < a; c++) {
            d = b[c];
            d.columnWidth = 1 / a;
            d.removeCls(["dashboard-column-first", "dashboard-column-last"])
        }
        b[0].addCls("dashboard-column-first");
        b[a - 1].addCls("dashboard-column-last");
        return this.callParent(arguments)
    },

    initEvents: function () {
        this.callParent();
        this.dd = Ext.create("App.view.DropTarget", this, this.dropConfig)
    },

    beforeDestroy: function () {
        if (this.dd) {
            this.dd.unreg()
        }
        this.callParent();
    }

});

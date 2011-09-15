Ext.define("App.view.AddressDropTarget", {
    extend: "Ext.dd.DropTarget",

     requires: [
        'Ext.panel.Proxy'
    ],

    ddScrollConfig: {
        vthresh: 50,
        hthresh: -1,
        animate: true,
        increment: 200
    },

    constructor: function (a, b) {
        this.portal = a;
        Ext.dd.ScrollManager.register(a.body);
        App.view.AddressDropTarget.superclass.constructor.call(this, a.body, b);
        a.body.ddScrollConfig = this.ddScrollConfig
    },
//    createEvent: function (a, f, d, b, h, g) {
//        return {
//            portal: this.portal,
//            panel: d.panel,
//            columnIndex: b,
//            column: h,
//            position: g,
//            data: d,
//            source: a,
//            rawEvent: f,
//            status: this.dropAllowed
//        }
//    },

    notifyDrop: function(source, event, data){
        this.getEl().appendChild(source.getEl());
    },

    notifyOver: function (source, event, data) {
        var x = u;
    }//,
//    notifyOut: function () {
//        delete this.grid
//    },
//    notifyDrop: function (l, h, g) {
//        delete this.grid;
//        if (!this.lastPos) {
//            return
//        }
//        var j = this.lastPos.c,
//            f = this.lastPos.col,
//            k = this.lastPos.p,
//            a = l.panel,
//            b = this.createEvent(l, h, g, f, j, k !== false ? k : j.items.getCount());
//        if (this.portal.fireEvent("validatedrop", b) !== false && this.portal.fireEvent("beforedrop", b) !== false) {
//            if (!g.draggedRecord) {
//                a.el.dom.style.display = "";
//                if (k !== false) {
//                    j.insert(k, a)
//                } else {
//                    j.add(a)
//                }
//                l.proxy.hide();
//            } else {
//                var dashlet = Ext.create(g.draggedRecord.data.xtype, {
//                    title: g.draggedRecord.data.title,
//                    html: g.draggedRecord.data.body
//                });
//                Ext.isNumber(k) ? j.insert(k, dashlet) : j.add(dashlet);
//            }
//            this.portal.fireEvent("drop", b);
//            var m = this.scrollPos.top;
//            if (m) {
//                var i = this.portal.body.dom;
//                setTimeout(function () {
//                    i.scrollTop = m
//                }, 10)
//            }
//        }
//        delete this.lastPos;
//        return true
//    },
//    getGrid: function () {
//        var a = this.portal.body.getBox();
//        a.columnX = [];
//        this.portal.items.each(function (b) {
//            a.columnX.push({
//                x: b.el.getX(),
//                w: b.el.getWidth()
//            })
//        });
//        return a
//    },
//    unreg: function () {
//        Ext.dd.ScrollManager.unregister(this.portal.body);
//        App.view.DropTarget.superclass.unreg.call(this)
//    }

});
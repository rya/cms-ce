Ext.define( 'App.view.AddressDragSource', {
    extend: 'Ext.dd.DragSource',

    constructor : function( panel, cfg )
    {
        this.contentPanel = panel;
        this.panelParent = panel.up('addressContainer');
        this.dragData = {panel: panel};

        this.callParent( [panel.el, cfg] );

        if (this.containerScroll) {
            Ext.dd.ScrollManager.register(this.el);
        }
    },

    onInitDrag : function(x, y){
        this.proxy.update(Ext.clone(this.contentPanel).el.dom);
        this.onStartDrag(x, y);
        return true;
    },

    getDragData: function( e )
    {
        var sourceEl = e.getTarget( this.contentPanel.itemSelector, 10 );
        if ( sourceEl )
        {
            var d = sourceEl.cloneNode( true );
            d.id = Ext.id();
            return {
                ddel: d,
                sourceEl: sourceEl,
                repairXY: Ext.fly( sourceEl ).getXY(),
                draggedRecord: this.contentPanel.getRecord( sourceEl )
            }
        }
    },

    getRepairXY: function()
    {
        return this.dragData.repairXY;
    },

    afterRepair : function(){
        var me = this;
        if (Ext.enableFx) {
            Ext.fly(me.dragData.ddel).highlight(me.repairHighlightColor);
        }
        me.dragging = false;
    },

    destroy : function(){
        this.callParent();
        if (this.containerScroll) {
            Ext.dd.ScrollManager.unregister(this.el);
        }
    }

});
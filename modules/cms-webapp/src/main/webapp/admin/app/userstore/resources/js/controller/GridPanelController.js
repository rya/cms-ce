Ext.define( 'CMS.controller.GridPanelController', {
    extend: 'Ext.app.Controller',

    stores: [
        'UserstoreConfigStore',
        'UserstoreConnectorStore'
    ],
    models: [
        'UserstoreConfigModel',
        'UserstoreConnectorModel'
    ],
    views: [
        'ContextMenu',
        'GridPanel'
    ],

    refs: [
        {ref: 'userstoreGrid', selector: 'userstoreGrid'},
        {ref: 'userstoreContextMenu', selector: 'userstoreContextMenu', autoCreate: true, xtype: 'userstoreContextMenu'}
    ],

    init: function()
    {
        this.control({
            'userstoreGrid': {
                selectionchange: function(view, selections, eOpts ) {
                    this.application.fireEvent('updateDetailsPanel', selections )
                },
                itemdblclick: function(view, record, item, index, e, eOpts) {

                },
                itemcontextmenu: this.showContextMenu
            },
            '*[action=editUserstore]': {
                click: function() {
                    this.application.fireEvent( 'createUserstoreTab', false);
                }
            }
        });
    },

    showContextMenu: function( view, rec, node, index, e )
    {
        e.stopEvent();
        this.getUserstoreContextMenu().showAt( e.getXY() );
        return false;
    }

} );

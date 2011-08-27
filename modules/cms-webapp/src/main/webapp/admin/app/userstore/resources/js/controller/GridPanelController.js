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
        {ref: 'grid', selector: 'userstoreGrid'},
        {ref: 'contextMenu', selector: 'userstoreContextMenu', autoCreate: true, xtype: 'userstoreContextMenu'}
    ],

    init: function()
    {
        // Add listeners to GUI controls.
        this.control({
            'userstoreGrid': {
                selectionchange: function(view, selections, eOpts) {
                    this.application.fireEvent('updateDetailsPanel', selections )
                },

                itemdblclick: function(view, record, item, index, e, eOpts) {
                    this.application.fireEvent( 'createUserstoreTab', record.data, false);
                },

                itemcontextmenu: this.showContextMenu
            },

            'userstoreGrid button[action=newUserstore]': {
                click: function(button, e, eOpts) {
                    this.application.fireEvent( 'createUserstoreTab', null, true);
                }
            },

            'userstoreContextMenu menuitem[action=editUserstore]': {
                click: function(item, e, eOpts) {
                    var userstore = this.getGrid().getSelectionModel().getSelection()[0].data;
                    this.application.fireEvent( 'editUserstore', userstore, false);
                }
            }
        });
    },

    showContextMenu: function( view, rec, node, index, e )
    {
        e.stopEvent();
        this.getContextMenu().showAt( e.getXY() );
        return false;
    }

} );

Ext.define( 'CMS.controller.UserstoreController', {
    extend: 'Ext.app.Controller',

    stores: [
        'UserstoreConfigStore'
    ],
    models: [
        'UserstoreConfigModel'
    ],
    views: [
        'userstore.ContextMenu',
        'userstore.GridPanel',
        'userstore.DetailPanel'
    ],

    refs: [
        {ref: 'tabPanel', selector: 'cmsTabPanel'},
        {ref: 'userstoreDetail', selector: 'userstoreDetail'},
        {ref: 'userstoreGrid', selector: 'userstoreGrid'},
        {ref: 'userstoreContextMenu', selector: 'userstoreContextMenu', autoCreate: true, xtype: 'userstoreContextMenu'}
    ],

    init: function()
    {
        this.control( {
                          '*[action=newUserstore]': {
                              click: this.notImplementedAction
                          },
                          '*[action=editUserstore]': {
                              click: this.notImplementedAction
                          },
                          '*[action=deleteUserstore]': {
                              click: this.notImplementedAction
                          },
                          '*[action=syncUserstore]': {
                              click: this.notImplementedAction
                          },
                          'userstoreGrid': {
                              selectionchange: this.updateDetailsPanel,
                              itemcontextmenu: this.popupMenu
                          }
                      } );
    },

    notImplementedAction: function ( btn, evt, opts ) {
        Ext.Msg.alert("Not implemented", btn.action + " is not implemented yet.");
    },

    updateDetailsPanel: function( selModel, selected )
    {
        var userstore = selected[0];
        var userstoreDetail = this.getUserstoreDetail();

        if ( userstore )
        {
            userstoreDetail.update( userstore.data );
            userstoreDetail.setCurrentUserstore( userstore.data );
        }

        userstoreDetail.setTitle( selected.length + " userstore selected" );
    },

    popupMenu: function( view, rec, node, index, e )
    {
        e.stopEvent();
        this.getUserstoreContextMenu().showAt( e.getXY() );
        return false;
    }

} );

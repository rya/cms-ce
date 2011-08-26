Ext.define( 'CMS.controller.DetailPanelController', {
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
        'DetailPanel'
    ],

    refs: [
        {ref: 'userstoreDetail', selector: 'userstoreDetail'}
    ],

    init: function()
    {
        this.application.on({
            updateDetailsPanel: this.updateDetailsPanel,
            scope: this
        });

        this.control( {
        });
    },

    updateDetailsPanel: function( selected )
    {
        var userstore = selected[0];
        var userstoreDetail = this.getUserstoreDetail();

        if ( userstore )
        {
            userstoreDetail.update( userstore.data );
            userstoreDetail.setCurrentUserstore( userstore.data );
        }

        userstoreDetail.setTitle( selected.length + " userstore selected" );
    }

} );

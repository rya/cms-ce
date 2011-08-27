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
        {ref: 'detailPanel', selector: 'userstoreDetail'}
    ],

    init: function()
    {
        this.application.on({
            updateDetailsPanel: this.updatePanel,
            scope: this
        });

        this.control({

            'userstoreDetail': {
                render: function () {
                    this.setButtonsDisabled(true);
                }
            },

            'userstoreDetail button[action=editUserstore]': {
                click: function(item, e, eOpts) {
                    var userstore = this.getDetailPanel().getCurrentUserstore();
                    this.application.fireEvent( 'editUserstore', userstore, false);
                }
            }

        });
    },

    updatePanel: function( selected )
    {
        var userstore = selected[0];
        var detailPanel = this.getDetailPanel();

        if ( userstore )
        {
            detailPanel.update( userstore.data );
            detailPanel.setCurrentUserstore( userstore.data );
        }

        detailPanel.setTitle( selected.length + " userstore selected" );

        this.setButtonsDisabled(false);

    },

    setButtonsDisabled: function(disable) {
        Ext.ComponentQuery.query( 'userstoreDetail button[action=editUserstore]' )[0].setDisabled( disable );
        Ext.ComponentQuery.query( 'userstoreDetail button[action=deleteUserstore]' )[0].setDisabled( disable );
    }

} );

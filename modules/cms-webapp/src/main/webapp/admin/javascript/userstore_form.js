var UserStoreForm = {

    onChangeType : function( selectElement )
    {
        var numberOfavailableConnectors = g_number_of_available_connectors;
        var isSelectedRemote = selectElement.value === 'remote';
        var formConnectorRow = document.getElementById( 'form-connector-row' );

        formConnectorRow.style.display = isSelectedRemote ? '' : 'none';

        this.enableDisableSaveButton( isSelectedRemote, numberOfavailableConnectors );
    }

    // -------------------------------------------------------------------------------------------------------------------------------------

    ,onChangeConnector : function( selectElement )
    {
        var selected = selectElement.options[ selectElement.selectedIndex ];
        var selectedConnectorExist = selected.className !== '-connector-not-found';

        var errorMessageElement = document.getElementById( 'userstore-error-message' );
        errorMessageElement.style.display = !selectedConnectorExist ? 'block' : 'none'
    }

    // -------------------------------------------------------------------------------------------------------------------------------------

    ,enableDisableSaveButton : function( isRemote, numberOfAvailableConnectors )
    {
        var saveButtonElements = document.getElementsByName( 'save' );
        var isRemoteAndThereIsNoAvailableConnectors = isRemote && numberOfAvailableConnectors === 0;

        var i;
        for ( i = 0; i < saveButtonElements.length; i++ )
        {
            saveButtonElements[i].disabled = ( isRemoteAndThereIsNoAvailableConnectors ) ? true : false;
        }
    }
};


/*admin.js*/ addEvent( window, 'load', function()
{
    UserStoreForm.enableDisableSaveButton( g_is_remote, g_number_of_available_connectors );
}, false );
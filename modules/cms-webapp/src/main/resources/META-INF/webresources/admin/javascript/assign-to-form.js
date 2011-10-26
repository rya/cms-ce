AssignToForm = {

    addUser : function( args )
    {
        var key = args[0];
        var type = args[1];
        var name = args[2];
        var userstoreName = args[3];
        var qualifiedName = args[4];
        var photoExists = args[5];

        $( '#assignee-status-arrow' ).show();
        $( '#assignee-status-new-assignee' ).show();

        var photoSquareSize = $( '#new-assignee-photo' ).attr( 'width' );
        var photoSource = photoExists ? '_image/user/' + key + '?_filter=scalesquare(' + photoSquareSize + ');rounded(2)' : 'images/dummy-user-small.png';

        $( '#new-assignee-photo' ).attr( 'src', photoSource );
        $( '#new-assignee-name' ).html( name + qualifiedName );

        AssignToForm.enableDisableSendButtons();
    }

    // -------------------------------------------------------------------------------------------------------------------------------------

    ,removeUser : function()
    {
        $( '#assignee-status-arrow' ).hide();
        $( '#assignee-status-new-assignee' ).hide();

        AssignToForm.enableDisableSendButtons();
    }

    // -------------------------------------------------------------------------------------------------------------------------------------

    ,enableDisableSendButtons : function()
    {
        var disable = $( 'input#_assignee' ).val().length === 0;

        $( '#send-button-1' ).attr( 'disabled', disable ).attr( 'style', disable ? 'cursor: default' : 'cursor: pointer' );
        $( '#send-button-2' ).attr( 'disabled', disable ).attr( 'style', disable ? 'cursor: default' : 'cursor: pointer' );
    }

    // -------------------------------------------------------------------------------------------------------------------------------------

    ,validateAndSubmit : function()
    {
        var form = $( 'form#formAdmin' );
        var assigneeField = $( 'input#_assignee' );
        var assigneeNameField = $( 'input#-ui-_assignee-autocomplete' );
        var assigneeCompareNameField = $( 'input#-ui-_assignee-compare-name' );

        var autoCompleteFieldValueEqualsNameFieldValue = assigneeNameField.val().length > 0 &&
                assigneeNameField.val() == assigneeCompareNameField.val();

        assigneeNameField.val( jQuery.trim( assigneeNameField.val() ) );
        assigneeCompareNameField.val( jQuery.trim( assigneeCompareNameField.val() ) );

        if ( assigneeField.val().length === 0 || assigneeNameField.val() === '%txtSearchUsers%' )
        {
            alert( '%errRequired%: %fldUser%' );
            assigneeNameField.focus();
            return;
        }

        if ( !autoCompleteFieldValueEqualsNameFieldValue )
        {
            alert( '%errInvalidUser%' );
            assigneeNameField.focus();
            return;
        }

        form.submit();
    }

};


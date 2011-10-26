function setPublishFromEnabled( tfEnabled )
{
    var row = document.getElementById( 'publishfrom-row' );
    if ( tfEnabled )
    {
        row.style.display = '';
    }
    else
    {
        row.style.display = 'none';
    }
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function setButtonStatus()
{
    var importFile = document.getElementById( 'importfile' );
    var importButton = document.getElementById( 'importbtn' );

    if ( importFile.value != "" )
    {
        importButton.disabled = false;
    }
    else
    {
        importButton.disabled = true;
    }
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function setPublishToEnabled( tfEnabled )
{
    var row = document.getElementById( 'publishto-row' );
    if ( tfEnabled )
    {
        row.style.display = '';
    }
    else
    {
        row.style.display = 'none';
    }
}

function statusChanged( select )
{
    var selectedIndex = select.selectedIndex;

    var tfFromDate = document.getElementsByName( "date_pubdata_publishfrom" )[0];
    var tfFromTime = document.getElementsByName( "time_pubdata_publishfrom" )[0];
    var tfToDate = document.getElementsByName( "date_pubdata_publishto" )[0];
    var tfToTime = document.getElementsByName( "time_pubdata_publishto" )[0];
    var autoApproved = g_autoApprovedImports[selectedIndex];
    var assigned = g_assignedImports[selectedIndex];

    if ( assigned )
    {
        showAssignmentFieldset();
    }
    else
    {
        hideAssignmentFieldset();
        clearAssignmentFields();
    }
    
    // disable/enable publish from and to fields
    setPublishFromEnabled( autoApproved );
    setPublishToEnabled( autoApproved );
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function validateAll()
{
    var date_pubdata_publishfrom = document.getElementsByName( 'date_pubdata_publishfrom' )[0];
    var time_pubdata_publishfrom = document.getElementsByName( 'time_pubdata_publishfrom' )[0];
    var date_pubdata_publishto = document.getElementsByName( 'date_pubdata_publishto' )[0];
    var time_pubdata_publishto = document.getElementsByName( 'time_pubdata_publishto' )[0];

    if ( !validateDate( date_pubdata_publishfrom ) )
    {
        return false;
    }
    if ( !validateTime( time_pubdata_publishfrom ) )
    {
        return false;
    }
    if ( !validateDate( date_pubdata_publishto ) )
    {
        return false;
    }
    if ( !validateTime( time_pubdata_publishto ) )
    {
        return false;
    }

    if ( date_pubdata_publishfrom.value == '' && time_pubdata_publishfrom.value != '' )
    {
        alert( '%msgTimeCanNotBeSetWithoutDate%' );
        date_pubdata_publishfrom.focus();
        return false;
    }

    if ( date_pubdata_publishto.value == '' && time_pubdata_publishto.value != '' )
    {
        alert( '%msgTimeCanNotBeSetWithoutDate%' );
        date_pubdata_publishto.focus();
        return false;
    }

    if ( dateTimeRangeValidator.isStartDateTimeLaterThanOrSameAsEndDateTime() )
    {
        alert( '%errOnlineFromIsLaterThanOnlineTo%' );
        return false;
    }

    document.getElementById( 'wait' ).style.display = 'block';
    document.getElementById( 'form' ).style.display = 'none';

    return true;
}

// Globals
var pollID;
var pollIntervalMS = 2000;
var getSynchUserStoreStatusTimeoutMS = 300000;
var batchSize = 20;
// -----------------------------------------------------------------------------------------------------------------------------------------

function synchronize( userStoreKey, mode )
{
    if ( mode === 'users' )
    {
        if ( confirm('%alertSynchronizeUsers%') )
        {
            AjaxService.startSyncUserStore(userStoreKey, true, false, batchSize);
            displayStatusBoxAndStartPolling(userStoreKey);
        }
    }
    else if ( mode === 'groups' )
    {
        if ( confirm('%alertSynchronizeGroups%') )
        {
            AjaxService.startSyncUserStore(userStoreKey, false, true, batchSize);
            displayStatusBoxAndStartPolling(userStoreKey);
        }
    }
    else if ( mode === 'usersAndGroups' )
    {
        if ( confirm('%alertSynchronizeUserAndGroup%') )
        {
            AjaxService.startSyncUserStore(userStoreKey, true, true, batchSize);
            displayStatusBoxAndStartPolling(userStoreKey);
        }
    }
    else
    {
        /**/
    }
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function checkIfUserstoreIsSynchronizing( userStoreKey )
{
    var isSynchronizing;
    
    AjaxService.getSynchUserStoreStatus( userStoreKey, {callback: function( status )
    {
        isSynchronizing = !status.completed;

        if ( isSynchronizing )
        {
            displayStatusBoxAndStartPolling(userStoreKey);
        }
    }});
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function displayStatusBoxAndStartPolling( userStoreKey )
{
    createStatusBox();
    poll(userStoreKey);
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function poll( userStoreKey )
{
    AjaxService.getSynchUserStoreStatus( userStoreKey, {callback: function( status )
    {
        updateStatusBox(status);

        if ( status.completed )
        {
            cancelStatusPolling();

            updateStartAndFinishDate(userStoreKey);
        }
        else
        {
            pollID = setTimeout( function() { poll(userStoreKey); }, pollIntervalMS);
        }
    },
        errorHandler: getSynchUserStoreStatusErrorAndWarningHandler,
        warningHandler: getSynchUserStoreStatusErrorAndWarningHandler,
        timeout: getSynchUserStoreStatusTimeoutMS
    });
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function updateStatusBox( status )
{
    $('#synchronize-status-box').html(status.message);
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function getSynchUserStoreStatusErrorAndWarningHandler( message )
{
    var errorMessageHTML = '<p>%synchError%: ' + message + '<br/>%synchCheckLogsMsg%</p>';
    $('#synchronize-status-box').html(errorMessageHTML);

    cancelStatusPolling();
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function cancelStatusPolling()
{
    clearTimeout(pollID);

    var buttonHTML = '';
    buttonHTML += '<p id="synchronize-close-button">';
    buttonHTML += '<button class="button_text" onclick="closeStatusBox()">%synchClose%</button>';
    buttonHTML += '</p>';

    $('#synchronize-status-box').append(buttonHTML);
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function updateStartAndFinishDate( userStoreKey )
{
    AjaxService.getSynchUserStoreStatus( userStoreKey, {callback: function( status )
    {
        var startedDate = status.startedDate;
        var finishedDate = status.finishedDate;

        if( startedDate != null && finishedDate != null )
        {
            $('#synchronizationFieldset').show();
        }

        if ( startedDate )
            $('#synchronize-started-date').html( getFormatedDate(startedDate) );

        if ( finishedDate )
            $('#synchronize-finished-date').html( getFormatedDate(finishedDate) );

    }});
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function createStatusBox()
{
    $('body').append('<div id="synchronize-overlay"><!-- --></div>');
    $('body').append('<div id="synchronize-status-box"><!-- --></div>');

    $('#synchronize-overlay').css({
        'height': $(document).height() + 'px'
    });

    updateStatusBoxPosition();
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function closeStatusBox()
{
    $('#synchronize-overlay').remove();
    $('#synchronize-status-box').remove();
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function updateStatusBoxPosition()
{
    var statusBoxWidth = $('#synchronize-status-box').outerWidth();
    var documentWidth = $(document).width();
    var newLeftPositionForStatusBox = 50 - Math.round( statusBoxWidth / documentWidth * 100 / 2 );

    $('#synchronize-status-box').css({
        'left': newLeftPositionForStatusBox + '%'
    });
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function getFormatedDate( date )
{
    var year    = date.getFullYear();
    var month   = date.getMonth() + 1;
    var day     = date.getDate();
    var hours   = date.getHours();
    var minutes = date.getMinutes();
    var seconds = date.getSeconds();

    day         = padDateComponent(day);
    month       = padDateComponent(month);
    hours       = padDateComponent(hours);
    minutes     = padDateComponent(minutes);
    seconds     = padDateComponent(seconds);

    return day + '.' + month + '.' + year + ' ' + hours + ':' + minutes + ':' + seconds;
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function padDateComponent( component )
{
    return component = (component < 10) ? '0' + component : component;
}
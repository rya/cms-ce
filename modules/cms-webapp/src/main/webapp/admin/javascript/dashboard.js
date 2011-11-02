function showFullAssignmentDescription( contentKey, showFull )
{
    var descriptionLong = document.getElementById( 'assignment-description-long-' + contentKey );
    var descriptionShort = document.getElementById( 'assignment-description-short-' + contentKey );

    if ( showFull )
    {
        descriptionLong.style.display = 'block';
        descriptionShort.style.display = 'none';
    }
    else
    {
        descriptionLong.style.display = 'none';
        descriptionShort.style.display = 'block';
    }
}

// ---------------------------------------------------------------------------------------------------------------------

function removeContent( contentKey, page, cat )
{
    var toBeRemoved = [];
    toBeRemoved.push( contentKey );

    AjaxService.isContentInUse( toBeRemoved, {
        callback:function( bInUse )
        {
            doRemoveContent( bInUse, contentKey, page, cat );
        }
    } );

    return false;
}

// ---------------------------------------------------------------------------------------------------------------------

function doRemoveContent( bInUse, contentKey, page, cat )
{
    var alertMsg;

    if ( bInUse )
    {
        alertMsg = '%alertDeleteContentWithParents%';
    }
    else
    {
        alertMsg = '%msgConfirmRemoveSelected%';
    }

    if ( confirm( alertMsg ) )
    {
        document.location.href = 'adminpage?page=' + page + '&op=remove&key=' + contentKey + '&cat=' + cat + '&selectedunitkey=1';
    }
}
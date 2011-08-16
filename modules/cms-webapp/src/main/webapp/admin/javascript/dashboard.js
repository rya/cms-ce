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

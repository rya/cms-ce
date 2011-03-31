if ( !cms ) var cms = {};
if ( !cms.utils ) cms.utils = {};
if ( !cms.utils.Event ) cms.utils.Event = {};

cms.utils.Event = {
    // Adds a listener to the object/element.
    addListener: function( obj, type, fn, useCapture, uniqueId )
    {
        if (!uniqueId)
            var uniqueId = '';

        if ( !useCapture ) useCapture = false;
        
        if (obj.addEventListener)
            obj.addEventListener( type, fn, useCapture );
        else if (obj.attachEvent)
        {
            obj["e" + type + fn + uniqueId] = fn;
            obj[type + fn + uniqueId] = function() { obj["e" + type + fn + uniqueId]( window.event ); }
            obj.attachEvent( "on" + type, obj[type + fn + uniqueId] );
        }
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    // Removes a listener to the object/element.
    removeListener: function( obj, type, fn, uniqueId )
    {
        if (!uniqueId)
            var uniqueId = '';

        if ( obj.removeEventListener )
            obj.removeEventListener(type, fn, false);
        else if ( obj.detachEvent )
        {
            obj.detachEvent("on" + type, obj[type + fn + uniqueId]);
            obj[type + fn + uniqueId] = null;
            obj["e" + type + fn + uniqueId] = null;
        }
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    // Gets the target element where the event was triggerd
    getTarget: function ( event )
    {
        // W3C || MSIE
        return event.target || event.srcElement;
    }
    // -------------------------------------------------------------------------------------------------------------------------------------
};
if ( !cms ) var cms = {};
if ( !cms.window ) cms.window = {};

cms.window = {
    close : function()
    {
        if ( parent ) // The doument is in a frameset.
            parent.close();
        else
            window.close();
    },

    // -------------------------------------------------------------------------------------------------------------------------------------

    attatchKeyEvent : function( action )
    {
        document.onkeypress = function( e )
        {
            var e = e || event;
            if ( action == 'close' )
            {
                if ( e.keyCode == 27 )
                    cms.window.close();
            }
        };
    }
};
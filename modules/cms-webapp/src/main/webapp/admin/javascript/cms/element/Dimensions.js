if ( !cms ) var cms = {};
if ( !cms.element ) cms.element = {};
if ( !cms.element.Dimensions ) cms.element.Dimensions = {};

cms.element.Dimensions = {

    // Finds and returns the computed x(left) position of element.
    getX: function( element )
    {
        var currX = 0;
        if ( element.offsetParent )
        {
            do
            {
                currX += element.offsetLeft;
            }
            while ( element = element.offsetParent );
            return currX;
        }
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    // Finds and returns the computed y(top) position of element.
    getY: function( element )
    {
        var currY = 0;
        if ( element.offsetParent )
        {
            do
            {
                currY += element.offsetTop;
            }
            while ( element = element.offsetParent );
            return currY;
        }
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    // Finds and returns the computed width of element.
    getWidth: function( element )
    {
        return element.offsetWidth;
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    // Finds and returns the computed height of element
    getHeight: function( element )
    {
        return element.offsetHeight;
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    // Finds and returns the computed width, height, x(left) and y(top) properties of the element.
    getDimensions: function( element )
    {
        var x, y, w, h;

        x = this.getX( element );
        y = this.getY( element );
        w = this.getWidth( element );
        h = this.getHeight( element );

        return[x, y, w, h];
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    // Finds and returns the width and height of the window.
    getWindowSize: function()
    {
        var myWidth = 0, myHeight = 0;
        if ( typeof( window.innerWidth ) == 'number' )
        {
            //Non-IE
            myWidth = window.innerWidth;
            myHeight = window.innerHeight;
        }
        else if ( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) )
        {
            //IE 6+ in 'standards compliant mode'
            myWidth = document.documentElement.clientWidth;
            myHeight = document.documentElement.clientHeight;
        }
        else if ( document.body && ( document.body.clientWidth || document.body.clientHeight ) )
        {
            //IE 4 compatible
            myWidth = document.body.clientWidth;
            myHeight = document.body.clientHeight;
        }

        return [myWidth, myHeight];
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    // Finds and returns how much the user has scrolled a document.
    getScrollXY: function ()
    {
        var scrOfX = 0, scrOfY = 0;
        if ( typeof( window.pageYOffset ) == 'number' )
        {
            //Netscape compliant
            scrOfY = window.pageYOffset;
            scrOfX = window.pageXOffset;
        }
        else if ( document.body && ( document.body.scrollLeft || document.body.scrollTop ) )
        {
            //DOM compliant
            scrOfY = document.body.scrollTop;
            scrOfX = document.body.scrollLeft;
        }
        else if ( document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) )
        {
            //IE6 standards compliant mode
            scrOfY = document.documentElement.scrollTop;
            scrOfX = document.documentElement.scrollLeft;
        }
        return [ scrOfX, scrOfY ];
    }
};

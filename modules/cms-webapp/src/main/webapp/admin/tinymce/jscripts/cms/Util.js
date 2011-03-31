/*
    Class: CmsUtils

    This class contains methods needed to get the HTML editor to work with EVS.
*/

function CMSUtil()
{
    /*
     Function: getLanguageCode

     Gets the login language code, cookie data.

     Returns:

     A string containing a standard language code.
     */
    this.getLanguageCode = function()
    {
        try
        {
            var sLang = this.getCookie('languageCode');
        }
        catch( err )
        {
            alert('Function getCookie does not exist!');
            return;
        }

        switch ( sLang )
                {
            case 'no':
                sLang = 'nb';  // Norwegian Bokmål.
                break;
            case 'se':
                sLang = 'sv';  // Swedish.
                break;
            case 'da':
                sLang = 'da';  // Danish.
                break;
            default:
                sLang = 'en';  // Fallback, English.
        }
        return sLang;
    };
    //--------------------------------------------------------------------------------------------------------------------

    /*
        Function: getCookie

        Returns a cookie based on the name parameter.
        Based on code from Quirksmode.com.
    */
    this.getCookie = function( sName )
    {
        var sNameEQ = sName + "=";
        var aCookies = document.cookie.split(';');
        for ( var i = 0; i < aCookies.length; i++ )
        {
            var sCookie = aCookies[i];
            while ( sCookie.charAt(0) == ' ' ) sCookie = sCookie.substring(1, sCookie.length);
            if ( sCookie.indexOf(sNameEQ) == 0 ) return sCookie.substring(sNameEQ.length, sCookie.length);
        }
        return null;
    };
    //--------------------------------------------------------------------------------------------------------------------

    /*
        Function: getBody

        Returns a pointer to the body element.
    */
    this.getBody = function()
    {
        return document.getElementsByTagName('body')[0];
    };
    //--------------------------------------------------------------------------------------------------------------------

    /*
        Function: getX

        Returns the x position of an element.
        Original code: http://www.quirksmode.org/js/findpos.html
    */
    this.getX = function( oElement )
    {
        var iCurLeft = 0;
        if ( oElement.offsetParent )
        {
            iCurLeft = oElement.offsetLeft;
            while ( oElement = oElement.offsetParent )
            {
                iCurLeft += oElement.offsetLeft;
            }
        }
        return iCurLeft;
    };
    //--------------------------------------------------------------------------------------------------------------------

    /*
        Function: getY

        Returns the y position of an element.
        Original code: http://www.quirksmode.org/js/findpos.html
    */
    this.getY = function( oElement )
    {
        var iCurTop = 0;
        if ( oElement.offsetParent )
        {
            iCurTop = oElement.offsetTop;
            while ( oElement = oElement.offsetParent )
            {
                iCurTop += oElement.offsetTop;
            }
        }
        return iCurTop;
    };
    //--------------------------------------------------------------------------------------------------------------------

    /*
        Function: setOpacity

        Sets the opacity for an element.

        Parameters:

        oElement - a HTML element.
        iVal - opacity (0 - 1)
    */
    this.setOpacity = function( oElement, iVal )
    {
        if ( document.all )
        { // IE way.
            oElement.style.filter = 'alpha(opacity=' + (iVal * 100) + ')';
        }
        else
        {
            oElement.style.opacity = iVal;
        }
        
        return true;
    };
    //--------------------------------------------------------------------------------------------------------------------
}
/**
 * Utils
 */
cms.ice.Utils = function() 
{
	return {
		
		getPortletRect : function( jQObject )
		{
			var markerElem = jQObject; // The jQuery object contains the marker element.

			var firstChild = markerElem.children();
			
			// If the first child element of the marker is aboslute positioned or is floating we should try to use that element.
			// CSS wise they are the marker does not tell the correct position.
			// This may not always be correct.
			if ( $ice(firstChild) )
			{
				var firstChildHasAbsoluteFixedOrFloat = $ice(firstChild).css('position') === 'absolute' || $ice(firstChild).css('position') === 'fixed' || $ice(firstChild).css('float') === 'left' || $ice(firstChild).css('float') === 'right';

				if ( firstChildHasAbsoluteFixedOrFloat )
				{
					markerElem = $ice(firstChild);
				}
			}
			
			var position, w, h, x, y;
			position = $ice(markerElem).offset();
			w = $ice(markerElem).width();
			h = $ice(markerElem).height();
			x = position.left;
			y = position.top;

			return {'w': w, 'h': h, 'x': x, 'y': y};
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		isElementOutsideOfViewport : function( event, elem )
        {
            var isOutsideOfViewport = false;

            var element = $ice(elem);
            var documentScrollTop = $ice(document).scrollTop();
            var windowWidth = $ice(window).width();
            var windowHeight = $ice(window).height();
            var width = element.width();
            var height = element.height();
            var position = element.offset();
            var xPosition = event.offsetX;
            var yPosition = event.offsetY - documentScrollTop;

            if ( ( xPosition + width ) >  windowWidth || xPosition < 0 )
            {
                isOutsideOfViewport = true;
            }

            if ( ( yPosition + height ) >  windowHeight || yPosition < 0 )
            {
                isOutsideOfViewport = true;
            }

            return isOutsideOfViewport;
        },
        // ---------------------------------------------------------------------------------------------------------------------------------------------

		openWindow : function( url, width, height )
		{
		    var l = (screen.width - width) / 2;
		    var t = (screen.height - height) / 2;

		    var newWindow = window.open(url, '_blank', 'toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=' + width + ',height=' + height + ',top=' + t + ',left=' + l + '');
		    newWindow.focus();
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		getURLParameter : function( name )
		{
		    var url = document.location.toString();

		    if ( url.indexOf('?') > -1 )
		    {
		        var params = url.split('?');
		        var param = params[1].split('&');
		        for ( var i = 0; i < param.length; i++ )
		        {
		            var pair = param[i].split('=');
		            if ( pair[0] == name )
		            {
		                return pair[1];
		            }
		        }
		    }
		    return null;
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		createCookie : function( name, value, days )
		{
		    var expires;
		    if ( days )
		    {
		        var date = new Date();
		        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
		        expires = "; expires=" + date.toGMTString();
		    }
		    else
		    {
		        expires = "";
		    }
		    document.cookie = name + "=" + value + expires + "; path=/";
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		readCookie : function( name )
		{
		    var nameEQ = name + "=";
		    var ca = document.cookie.split(';');
		    for ( var i = 0; i < ca.length; i++ )
		    {
		        var c = ca[i];
		        while ( c.charAt(0) == ' ' ) c = c.substring(1, c.length);
		        if ( c.indexOf(nameEQ) == 0 ) return c.substring(nameEQ.length, c.length);
		    }
		    return null;
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		eraseCookie : function( name )
		{
			this.createCookie(name, "", -1);
		},
        // ---------------------------------------------------------------------------------------------------------------------------------------------

        createICEInfoCookie : function()
        {
            var panel = cms.ice.Panel;
            var portletOverlay = cms.ice.PortletOverlay;
            var pagePanelOffset = $ice('#ice-panel').offset();

            var cookieStr = 'iceOn:' + panel.isIceOn;
            cookieStr += ',activePortletKey:' + portletOverlay.activePortletKey;
            cookieStr += ',icePagePanelX:' + pagePanelOffset.left;
            cookieStr += ',icePagePanelY:' + pagePanelOffset.top;

            this.createCookie('iceInfo', cookieStr);
        },

        // ---------------------------------------------------------------------------------------------------------------------------------------------

        getICEInfoCookieByName : function( name )
        {
            // Format: "name:value,name:value,name:value"

            var returnValue = null;
            var iceInfoCookie = this.readCookie('iceInfo') || '';
            var fields = iceInfoCookie.split(',');
            var nameValue, n, v;
            
            for ( var i in fields )
            {
                nameValue = fields[i].split(':');
                n = nameValue[0];
                v = nameValue[1];

                if (n === name)
                {
                    returnValue = v;
                    break;
                }
            }

            return returnValue;
        },

        // ---------------------------------------------------------------------------------------------------------------------------------------------

        reloadPage : function()
        {
            var url = window.location.href;
            window.location.href = url.replace(/#/g, '');
        },
        // ---------------------------------------------------------------------------------------------------------------------------------------------
        
        injectLanguageStr : function(str, elements)
        {

            elements.each( function( i )
			{
                $ice(this).html(str);
            });
        },
        // ---------------------------------------------------------------------------------------------------------------------------------------------

        fixFlashElementsWithoutWmode : function()
        {
            // In order to get the absolute positioned portlet overlays to work with flash
            // the embeded flashlets needs to have the window mode(wmode) parameter set to opaque/transparent.
            // Flash Player is currently the only plugin that supports this.
            var hasOpaque;

            $ice('object param').each( function(i) {
                hasOpaque = this.name == 'wmode' && this.value == 'opaque' || this.value == 'transparent';

                if ( !hasOpaque )
                {
                   $ice(this).closest('object').append('<param name="wmode" value="opaque"/>');
                }
            });

            // Non IE browseres
            if ( !document.all )
            {
                $ice('embed').each( function(i) {
                    hasOpaque = this.name == 'wmode' && this.value == 'opaque' || this.value == 'transparent';

                    if ( !hasOpaque )
                    {
                        $ice(this).attr('wmode', 'opaque');

                        // This will repaint the plug-in and set the new parameter.
                        $ice(this).css('display', 'none');
                        $ice(this).css('display', 'block');
                    }
                });
            }
        }
	};
}();


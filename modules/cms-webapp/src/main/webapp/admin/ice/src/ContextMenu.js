/**
 * ContextMenu
 */
cms.ice.ContextMenu = function() 
{
	return {
		
		currentTarget : null,
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		create : function()
		{
            var utils = cms.ice.Utils;
			var portletOverlay = cms.ice.PortletOverlay;

			$ice('body').append('<div id="ice-context-menu"><!-- --></div>');

			// *********************************************************************************************************
			// Experimental dragging
			// *********************************************************************************************************
			$ice('#ice-context-menu').bind('dragstart',function( event )
            {
				if ( !$ice(event.target).is('#ice-context-menu h3') )
                {
                    return false;
                }
			}).bind('drag',function( event )
            {

                if ( !utils.isElementOutsideOfViewport(event, this) )
                {
                    cms.ice.Setup.isDragging = true;
                    
                    if (!document.all )
                    {
                        cms.ice.PortletOverlay.drawLine( $ice('#' + portletOverlay.activePortletKey)[0] );
                    }

                    $ice( this ).css( {
                        top: event.offsetY,
                        left: event.offsetX
                     });
                }                

			}).bind('dragend',function( event )
            {
                cms.ice.Setup.isDragging = false;

                if (!document.all )
				{
					cms.ice.PortletOverlay.drawLine( $ice('#' + portletOverlay.activePortletKey)[0] );
				}

			});
			// *********************************************************************************************************

		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		show : function( event, htmlText )
		{
			$ice('#ice-context-menu').css('height',  '' );

			$ice('#ice-context-menu').html( htmlText );

			$ice('#ice-context-menu').show();

			if ( $ice('#ice-context-menu .ice-menu-item-content-container').height() > 115 )
			{
			    $ice('#ice-context-menu .ice-menu-item-content-container').height('115px');
			}

			var windowWidth = $ice(window).width(), windowHeight = $ice(window).height();
			var scrollTop = $ice(document).scrollTop();	
				
			var contextMenuWidth = $ice('#ice-context-menu').width(), contextMenuHeight = $ice('#ice-context-menu').height();

			var x = event.pageX + contextMenuWidth < windowWidth ? event.pageX : event.pageX - contextMenuWidth;
			var y = event.pageY + contextMenuHeight - scrollTop < windowHeight ? event.pageY  : event.pageY - contextMenuHeight;
			
			$ice('#ice-context-menu').css({ 'top': y + 'px', 'left': x + 'px' });

            this.postRender();
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		hide : function()
		{
			$ice('#ice-context-menu').hide();
			this.viewState = 0;
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		remove : function()
		{
			$ice('#ice-context-menu').remove();
		},
        // ---------------------------------------------------------------------------------------------------------------------------------------------

        windowResize : function()
        {
            var contextMenuElem = $ice('#ice-context-menu'); 
            var position = contextMenuElem.offset();
            var contextMenuXPos = position.left;
            var contextMenuYPos = position.top;
            var contextMenuWidth = contextMenuElem.width();
            var contextMenuHeight = contextMenuElem.height();
            var windowWidth = $ice(window).width(), windowHeight = $ice(window).height();

            if ( contextMenuXPos > windowWidth )
            {
                var newXPosition = windowWidth - contextMenuWidth;
                contextMenuElem.css('left', newXPosition + 'px');
            }

            if ( contextMenuYPos > windowHeight )
            {
                var newYPosition = windowHeight - contextMenuHeight;
                contextMenuElem.css('top', newYPosition + 'px');
            }
        },
        // ---------------------------------------------------------------------------------------------------------------------------------------------
        
        postRender : function()
        {
            var utils = cms.ice.Utils;
            var setup = cms.ice.Setup;
            var tooltip = cms.ice.Tooltip;
            var portletOverlay = cms.ice.PortletOverlay;
            var contextMenuElement = $ice('#ice-context-menu');
            var contentMenuItems = $ice('#ice-context-menu .ice-menu-item-content-container a');

            utils.injectLanguageStr('%cmdIceCreateContent%', $ice('#ice-context-menu .ice-lang-placeholder-create-content'));
            utils.injectLanguageStr('%cmdIceViewPortletTrace%', $ice('#ice-context-menu .ice-lang-placeholder-portlet-trace'));
            utils.injectLanguageStr('%cmdIceViewPortletXML%', $ice('#ice-context-menu .ice-lang-placeholder-portlet-xml'));
            utils.injectLanguageStr('%cmdIceEdit% %txtIcePortlet%', $ice('#ice-context-menu .ice-lang-placeholder-edit-portlet'));

            contextMenuElement.bind('mouseover', function(e)
            {
                var isMenuItemWithContent = e.target.parentNode && e.target.parentNode.className === 'ice-menu-item-content-container';

                if ( !isMenuItemWithContent )
                {
                    tooltip.hide();
                }

                if ( portletOverlay.previousMousedHoverOverlay )
                {
                    if ( $ice(portletOverlay.previousMousedHoverOverlay).attr('ice-overlay-is-active') !== 'true' )
                    {
                        $ice(portletOverlay.previousMousedHoverOverlay).css('opacity', setup.portletOverlayOpacity);
                    }
                }
            });

            contentMenuItems.bind('mouseover', function(e)
            {
                tooltip.show('%msgIceClickToOpen%');
            });

            contentMenuItems.bind('mousemove', function(e)
            {
                tooltip.move(e);
            });

            // Set explicit width to avoid collapsing of the context menu when dragging it
            // to one of the sides of the document. 
            contextMenuElement.css('width', contextMenuElement.width() + 'px');
        }
	};
}();


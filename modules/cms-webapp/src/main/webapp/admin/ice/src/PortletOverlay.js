/**
 * PortletOverlay
 */
cms.ice.PortletOverlay = function() 
{

	return {

        activePortletKey : '',
        previousMousedHoverOverlay : null,

		create : function()
	    {
			var t = this, key, portletMarkerElem, dimensions, x, y, w, h, counter = 0;
			var utils = cms.ice.Utils;
			var portletInfo = cms.ice.Setup.portletInfo;

            var isCached;

			for ( var i in portletInfo )
			{
				key = portletInfo[i][0];
				portletMarkerElem = $ice('#marker-' + key)[0];
				isCached = portletInfo[i][3];
			
				if ( !portletMarkerElem ) continue;
			
				dimensions = utils.getPortletRect($ice(portletMarkerElem));
				x = dimensions.x;
				y = dimensions.y;
				w = dimensions.w;
				h = dimensions.h;
			
				t.overlay(key, dimensions, counter);
				t.icon(key, dimensions, counter);
				
				if ( !isCached )
				{
					t.overlayNotCached(key, dimensions, counter);
				}
					
				counter++;
			}
	    },
		// ---------------------------------------------------------------------------------------------------------------------------------------------

	    overlayNotCached : function( portletKey, dimensions, counter )
	    {
			var id = 'ice-portlet-not-cached-overlay-' + portletKey;
			var zIndex = 1001000 + counter; 
			
	        $ice('body').append('<div id="' + id + '" class="ice-portlet-not-cached-overlay"><!-- --></div>');

			$ice('#'+id).css({
				'width': (dimensions.w - 1) + 'px',
				'height': (dimensions.h - 1) + 'px',
				'top': (dimensions.y - 2) + 'px',
				'left': (dimensions.x -2) + 'px',
				'zIndex': zIndex
			});
		},

	    overlay : function( markerKey, dimensions, counter )
	    {
			var t = this;
			var utils = cms.ice.Utils;
            var tooltip = cms.ice.Tooltip;

			var id = 'ice-portlet-overlay-' + markerKey;
			var currentPortletKey = markerKey.substring(markerKey.lastIndexOf('-') +1, markerKey.length);
			var zIndex = 2001000 + counter;
			var opacity = cms.ice.Setup.portletOverlayOpacity;
			var active = false;

            var activeMarkerKey = utils.getICEInfoCookieByName('activePortletKey') || '';
            var activePortletKey = activeMarkerKey.substring(activeMarkerKey.lastIndexOf('-') +1, activeMarkerKey.length);

	        $ice('body').append('<div id="' + id + '" class="ice-portlet-overlay"><!-- --></div>');

			// Repainting in case of window resize or the page is reloaded.
			if (currentPortletKey === activePortletKey)
			{
				active = true;
				opacity = 0;
                if ( $ice('#' + activeMarkerKey)[0] )
                {
                    setTimeout( function() {
                        t.drawLine($ice('#' + activeMarkerKey)[0]);
                    }, 10);
                }
			}

			$ice('#'+id).attr('ice-overlay-is-active', active);
			$ice('#'+id).css({
				'width': dimensions.w + 'px',
				'height': dimensions.h + 'px',
				'top': dimensions.y + 'px',
				'left': dimensions.x + 'px',
				'opacity': opacity,
				'zIndex': zIndex
			});

			$ice('#'+id).bind('click', function(e) 
			{
				t.click(e, this, counter);
			});

			$ice('#'+id).bind('mouseover', function(e) 
			{
				t.mouseover(e, this, counter);
			});

			$ice('#'+id).bind('mousemove', function(e) 
			{
				tooltip.move(e);
			});
	    },
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		click : function( event, overlayElem, counter )
		{
			event.stopPropagation();
			
			var t = this;
			var utils = cms.ice.Utils;
			var contextMenu = cms.ice.ContextMenu;
			var setup = cms.ice.Setup;

			var menuHtml = setup.portletInfo[counter][2];

			// The portlet is active, store the @id for possible window resizing events.
			t.activePortletKey = $ice(overlayElem).attr('id');

            $ice('#ice-utility-canvas').show();

			$ice('.ice-portlet-overlay').each( function( i ) 
			{
				if (this !== overlayElem)
				{
					$ice(this).css('opacity', setup.portletOverlayOpacity);
				}
				$ice(this).attr('ice-overlay-is-active', 'false');
			});
		
			$ice(overlayElem).attr('ice-overlay-is-active', 'true');

			contextMenu.show(event, menuHtml);

            utils.createICEInfoCookie();
            
			// ****************************************************************************************************************
			// Experimental
			// ****************************************************************************************************************
			if ( !document.all )
			{
				t.drawLine(overlayElem);
			}
			// ****************************************************************************************************************

		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		mouseover : function( e, overlay, counter, additionalToolTipText)
		{
            if ( cms.ice.Setup.isDragging )
            {
                return;
            }

			e.stopPropagation();

			var t = this;
            var tooltip = cms.ice.Tooltip;
            var setup = cms.ice.Setup;

            var tooltipText = '%txtIcePortlet%: ' + cms.ice.Setup.portletInfo[counter][1];

            if ( additionalToolTipText != null )
            {
                tooltipText += additionalToolTipText;         
            }

            if ( t.previousMousedHoverOverlay )
            {
                if ( $ice(t.previousMousedHoverOverlay).attr('ice-overlay-is-active') !== 'true' )
                {
                    $ice(t.previousMousedHoverOverlay).css('opacity', setup.portletOverlayOpacity);
                }
            }

            t.previousMousedHoverOverlay = overlay;

			//$ice(overlay).css('opacity', 0);
			$ice(overlay).fadeTo(170, 0);
			tooltip.show(tooltipText);
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		mouseout : function( e, overlay )
		{
			e.stopPropagation();
			if ( $ice(overlay).attr('ice-overlay-is-active') !== 'true' )
			{
				$ice(overlay).fadeTo(170, cms.ice.Setup.portletOverlayOpacity);
			}
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------
   
		drawLine : function(overlay)
		{
			var key = overlay.id.split('ice-portlet-overlay-')[1];

			var utilContext = cms.ice.PageOverlay.utilContext;

			var iconRect            = $ice('#ice-portlet-overlay-icon-' + key).offset();
			var iconHeight          = $ice('#ice-portlet-overlay-icon-' + key).height();
			var iconWidth           = $ice('#ice-portlet-overlay-icon-' + key).height();
			var contextMenuRect     = $ice('#ice-context-menu').offset();
			var contextMenuWidth    = $ice('#ice-context-menu').width();
			
			var startX  = contextMenuRect.left + contextMenuWidth / 2;
			var startY  = contextMenuRect.top + 4;
			var endX    = iconRect.left + iconWidth / 2;
			var endY    = iconRect.top + iconHeight / 2;

            utilContext.clearRect(0, 0, 5000, 5000);
            utilContext.strokeStyle = "rgba(0, 0, 0, 1)";
			utilContext.beginPath();
			utilContext.moveTo(startX, startY);
			utilContext.lineTo(endX, endY);
			utilContext.closePath();
			utilContext.stroke();
		},

		resetAll : function()
		{
			var t = this;

			t.activePortletKey = '';
		
			$ice('.ice-portlet-overlay').each( function( i ) 
			{
				$ice(this).css('opacity', cms.ice.Setup.portletOverlayOpacity);
				$ice(this).attr('ice-overlay-is-active', 'false');
			});
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		remove : function()
		{
			$ice('.ice-portlet-overlay').each( function( i ) 
			{
				$ice(this).remove();
			});
		
			$ice('.ice-portlet-overlay-icon').each( function( i ) 
			{
				$ice(this).remove();
			});

			$ice('.ice-portlet-not-cached-overlay').each( function( i ) 
			{
				$ice(this).remove();
			});
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		icon : function( portletKey, dimensions, counter )
	    {
            var t = this;
            var setup = cms.ice.Setup;
            var tooltip = cms.ice.Tooltip;
            var portletOverlay = $ice('#ice-portlet-overlay-' + portletKey)[0];
			var id = 'ice-portlet-overlay-icon-' + portletKey;
			var zIndex = 3000000 + counter;
	        var xPos = dimensions.x + dimensions.w - 20;
            var yPos = dimensions.y;
		    var portletIconImage = setup.baseUrl + 'ice/images/portlet.gif';

			$ice('body').append('<div id="' + id + '" class="ice-portlet-overlay-icon"><!-- --></div>');

	 		var portletIcon = $ice('#ice-portlet-overlay-icon-' + portletKey);

			portletIcon.css({
				'background-image': 'url(' + portletIconImage + ')',
				'left': xPos + 'px',
				'top': yPos + 'px',
				'zIndex': zIndex
			});

			portletIcon.bind('mouseover', function( event )
			{
                var extraToolTipText =  dimensions.h > 0 ? '' : '<br/>%txtIceHeightWarning1%.<br/>' +
                                                                '%txtIceHeightWarning2%:<br/>' +
                                                                '* %txtIceHeightWarning3%.<br/>' +
                                                                '* %txtIceHeightWarning4%.';

                t.mouseover(event, portletOverlay, counter, extraToolTipText);
			});

			portletIcon.bind('mouseout', function( event )
			{
				t.mouseout(event, portletOverlay);
			});

			portletIcon.bind('click', function( event )
			{
				t.click(event, portletOverlay, counter);
			});
		
			$ice('#'+id).bind('mousemove', function( event )
			{
				tooltip.move(event);
			});
	    }
	};
}();


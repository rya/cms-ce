/**
 * PageOverlay
 */
cms.ice.PageOverlay = function()
{
	var pageOverlayElem = null;
	
	return {
		
		context : null,
		utilContext : null,

	    create : function()
	    {
			var t = this;
			var docWidth = $ice(document).width(), docHeight = $ice(document).height();
			var tooltip = cms.ice.Tooltip;

			if ( !document.all ) // Browsers with standard support.
	        {
		        $ice('body').append('<canvas id="ice-page-overlay"></canvas>');
				pageOverlayElem = $ice('#ice-page-overlay')[0];
				$ice(pageOverlayElem).attr('width', docWidth);
				$ice(pageOverlayElem).attr('height', docHeight);
				$ice(pageOverlayElem).css('opacity', cms.ice.Setup.pageOverlayOpacity);

				t.context = pageOverlayElem.getContext('2d');
				t.context.fillStyle = 'rgba(0, 0, 0, 1)';
				t.context.fillRect (0, 0, docWidth, docHeight);

				// ****************************************************************************************************************
				// Experimental
				// ****************************************************************************************************************
		        $ice('body').append('<canvas id="ice-utility-canvas"></canvas>');
				$ice('#ice-utility-canvas').attr('width', docWidth);
				$ice('#ice-utility-canvas').attr('height', docHeight);
				t.utilContext = $ice('#ice-utility-canvas')[0].getContext('2d');
				// ****************************************************************************************************************
				
				t.clearRectangles();
	        }
			else
			{
		        $ice('body').append('<div id="ice-page-overlay" style="position:absolute;top:0;left:0;width:1500px;height:1500px; filter: progid:DXImageTransform.Microsoft.BasicImage(mask=1) alpha(opacity=' + (cms.ice.Setup.pageOverlayOpacity * 100) + ')"><!-- --></div>');
				pageOverlayElem = $ice('#ice-page-overlay')[0];
				$ice(pageOverlayElem).css('width', docWidth + 'px');
				$ice(pageOverlayElem).css('height', docHeight + 'px');

		        $ice('#ice-page-overlay').append('<img id="ice-ie-page-overlay-shim" src="' + cms.ice.Setup.baseUrl + 'ice/images/shim.gif"/>');
				$ice('#ice-ie-page-overlay-shim').css('height', docHeight + 'px');
				$ice('#ice-ie-page-overlay-shim').css('height', docHeight + 'px');
				t.clearRectangles(null);
			}
			
			$ice(pageOverlayElem).bind('click', function( event )
			{
				t.click(event);
			});
		
			$ice(pageOverlayElem).bind('mouseover', function( event )
			{
				t.mouseover(event);
			});

			$ice(pageOverlayElem).bind('mouseout', function( event )
			{
				tooltip.hide();
			});

			$ice(pageOverlayElem).bind('mousemove', function( event )
			{
				tooltip.move(event);
			});
	    },
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		click: function( event )
		{
			event.stopPropagation();
			
            var utils = cms.ice.Utils;
			var contextMenu = cms.ice.ContextMenu;
            var portletOverlay = cms.ice.PortletOverlay;

            var scrollTop = $ice(document).scrollTop();

            $ice('#ice-utility-canvas').hide();
            
            portletOverlay.resetAll();
			contextMenu.hide();
            utils.createICEInfoCookie();

            $ice('#ice-panel').css({
               top: (event.pageY - scrollTop) + 'px',
               left: event.pageX + 'px'
            });
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		mouseover: function( event )
		{
			// e.stopPropagation();

			var setup = cms.ice.Setup;
			var tooltip = cms.ice.Tooltip;
			var tooltipText = '%txtIcePage%: ' + setup.pageInfo[1];

			tooltip.show(tooltipText);
		
			$ice('.ice-portlet-overlay').each( function( i ) 
			{
				if ( $ice(this).attr('ice-overlay-is-active') !== 'true' )
				{
					$ice(this).css('opacity', setup.portletOverlayOpacity);
				}
			});
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		clearRectangles : function()
		{
			var t = this;
			var key, markerElem, dimensions, x, y, w, h;
			var utils = cms.ice.Utils;
			var portletInfo = cms.ice.Setup.portletInfo;
		
			for ( i in portletInfo )
			{
				key = portletInfo[i][0];
				markerElem = $ice('#marker-' + key)[0];

				if ( !markerElem ) continue;

				dimensions = utils.getPortletRect($ice(markerElem));
				x = dimensions.x;
				y = dimensions.y;
				w = dimensions.w;
				h = dimensions.h;
				
				if ( !document.all ) // Standard compliant user agents
				{
					t.context.clearRect(x, y, w, h);
				}
				else
				{
					t.clearRectIE(parseInt(i), x, y, w, h);
				}
			}
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		clearRectIE: function(i, x, y, w, h)
		{
			var id = 'ice-ie-portlet-mask' + i;
			
			$ice('#ice-page-overlay').append('<img id="' + id + '" class="ice-ie-clear-img" src="'+ cms.ice.Setup.baseUrl + 'ice/images/pix.gif"/>');
			
			$ice('#' + id).attr('width', w);
			$ice('#' + id).attr('height', h);
			
			$ice('#' + id).css({
				'position': 'absolute',
				'left': x + 'px',
				'top': y + 'px',
				'zIndex': i
			});
			
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		show : function( event )
		{
            $ice('#ice-page-overlay').css('opacity', cms.ice.Setup.pageOverlayOpacity);
			$ice('#ice-ie-page-overlay-shim').show();
			$ice('#ice-utility-canvas').show();
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		hide : function( event )
		{
			cms.ice.PortletOverlay.resetAll();
			$ice('#ice-page-overlay').css('opacity', 0);
			$ice('#ice-ie-page-overlay-shim').hide();
			$ice('#ice-utility-canvas').hide();
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------
	
		remove : function()
		{
			if ( document.all ) // IE
			{
				$ice('.ice-ie-clear-img').each( function( i ) 
				{
					$ice(this).remove();
				});

				$ice('#ice-ie-page-overlay-shim').remove();
			}

			$ice('#ice-page-overlay').remove();

			// ****************************************************************************************************************
			// Experimental
			// ****************************************************************************************************************
			$ice('#ice-utility-canvas').remove();
		}
	};
}();


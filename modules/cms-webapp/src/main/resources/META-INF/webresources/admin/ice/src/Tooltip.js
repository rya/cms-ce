/**
 * Tooltip
 */
cms.ice.Tooltip = function() 
{
	return {
		
		timeout : 0,
	    create : function()
	    {
			$ice('body').append('<div id="ice-tooltip"><!-- --></div>');
	    },
		// ---------------------------------------------------------------------------------------------------------------------------------------------

	    show : function( htmlText )
	    {
            var isDragging = cms.ice.Setup.isDragging;

            if ( !isDragging )
            {
                $ice('#ice-tooltip').html(htmlText);
                $ice('#ice-tooltip').show();
            }
	    },
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		move: function( event )
		{
            var tooltipElem = $ice('#ice-tooltip');
            var offsetX = 10, offsetY = 16;
			var tooltipWidth = $ice('#ice-tooltip').width(), tooltipHeight = $ice('#ice-tooltip').height();
			var windowWidth = $ice(window).width(), windowHeight = $ice(window).height();
			var documentScrollTop = $ice(document).scrollTop();	

			var xPos = event.pageX + offsetX + tooltipWidth < (windowWidth - 10) ? event.pageX + offsetX : event.pageX - tooltipWidth ;
			var yPos = event.pageY + offsetY + tooltipHeight - documentScrollTop < (windowHeight - 10) ? event.pageY + offsetY : event.pageY - tooltipHeight - offsetY + 6;

			$ice(tooltipElem).css({
                'top': yPos + 'px',
                'left': xPos + 'px'
            });
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------

	    hide : function()
	    {
			$ice('#ice-tooltip').hide();
	    },
		// ---------------------------------------------------------------------------------------------------------------------------------------------

	    remove : function()
	    {
			$ice('#ice-tooltip').remove();
	    }
	};
}();


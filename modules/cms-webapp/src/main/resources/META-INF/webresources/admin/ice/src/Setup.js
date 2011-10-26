/**
 * Setup
 */
cms.ice.Setup = function() 
{
	return {
		
		portletInfo : [],
		pageInfo : [],
		pageOverlayOpacity : .1,
		portletOverlayOpacity : .2,
		baseUrl : '',
        isDragging : false,

        lang : {
            headContent : '%headIceContent%',
            cmdIceEdit : '%cmdIceEdit%',
            txtIceObject : '%txtIcePortlet%',
            lblOn : '%txtIceOn%',
            lblOff : '%txtIceOff%',
            cmdIceCreateNewContent : '%cmdIceCreateNewContent%'
        },
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		setPageInfo : function ( key, title, menuHtml )
		{
			var t = this;
			t.pageInfo = [key, title, menuHtml];
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------
		
		addPortletInfo : function ( key, title, menuHtml, isCached )
		{
			var t = this;
		
		  	if ( document.getElementById('marker-' + key) ) 
			{
		    	t.portletInfo[t.portletInfo.length] = [key, title, menuHtml, isCached];
		  	}
		},
		// ---------------------------------------------------------------------------------------------------------------------------------------------

		setBaseUrl : function( url )
		{
			this.baseUrl = url;
		}
	};
}();


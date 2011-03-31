/**
 * Init
 */
cms.ice.Init = function()
{
    var utils = cms.ice.Utils;

    $ice(window).bind('resize', function( event )
    {
        if ( $ice('#ice-toggle-on-off-button').hasClass('ice-tb-on') )
        {
            cms.ice.PageOverlay.remove();
            cms.ice.PortletOverlay.remove();
            cms.ice.PageOverlay.create();
            cms.ice.PortletOverlay.create();
            cms.ice.ContextMenu.windowResize();
        }

        cms.ice.Panel.windowResize();
    });

    $ice(window).bind('unload', function( event )
    {
        cms.ice.Utils.createICEInfoCookie();
        cms.ice.PageOverlay.remove();
        cms.ice.PortletOverlay.remove();
        cms.ice.ContextMenu.remove();
        cms.ice.Tooltip.remove();
        cms.ice.Panel.remove();
    });

    cms.ice.ContextMenu.create();
    cms.ice.Tooltip.create();
    cms.ice.Panel.create();

    if ( utils.readCookie('iceInfo') )
    {
        if ( utils.getICEInfoCookieByName('iceOn') === 'true' )
        {
            $ice("#ice-toggle-on-off-button").removeClass('ice-tb-off');
            $ice("#ice-toggle-on-off-button").addClass('ice-tb-on');

            cms.ice.Panel.isIceOn = true;
            cms.ice.PageOverlay.create();
            cms.ice.PortletOverlay.create();
        }
    }

    cms.ice.Panel.windowResize();
    utils.fixFlashElementsWithoutWmode();
};

$ice(document).ready(function() 
{
    cms.ice.Init();
});
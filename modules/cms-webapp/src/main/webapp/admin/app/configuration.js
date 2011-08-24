(function() {

    Ext.Loader.setConfig({
        enabled: true,
        disableCaching: true
    });

    function cms_hideLoadMaskOnLoad()
    {
        if (window.addEventListener)
        {
            window.addEventListener('load', function() {
                cms_getParentWindow().appLoadMask.hide();
            }, false);
        }
        else if (window.attachEvent) // IE
        {
            window.attachEvent('onload', function() {
                cms_getParentWindow().appLoadMask.hide();
            });
        }
    }

    function cms_hideMainMenusOnClick()
    {
        if (window.addEventListener)
        {
            window.addEventListener('click', function() {
                cms_getParentWindow().CMS.common.Common.hideLauncherMenus();
            }, false);
        }
        else if (window.attachEvent) // IE
        {
            window.attachEvent('onclick', function() {
                cms_getParentWindow().CMS.common.Common.hideLauncherMenus();
            });

        }
    }

    function cms_launcherExist()
    {
        return cms_getParentWindow().frames.length > 0;
    }

    function cms_getParentWindow() {
        return window.parent.parent || window.parent;
    }

    if ( cms_launcherExist() ) {
        cms_hideLoadMaskOnLoad();
        cms_hideMainMenusOnClick();
    }
})();

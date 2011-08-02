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
                window.parent.appLoadMask.hide();
            }, false);
        }
        else if (window.attachEvent) // IE
        {
            window.attachEvent('onload', function() {
                window.parent.appLoadMask.hide();
            });
        }
    }

    function cms_hideMainMenusOnClick()
    {
        if (window.addEventListener)
        {
            window.addEventListener('click', function() {
                window.parent.CMS.common.Common.hideLauncherMenus();
            }, false);
        }
        else if (window.attachEvent) // IE
        {
            window.attachEvent('onclick', function() {
                window.parent.CMS.common.Common.hideLauncherMenus();
            });

        }
    }

    function cms_launcherExist()
    {
        return window.parent.frames.length > 0;
    }

    if ( cms_launcherExist() ) {
        cms_hideLoadMaskOnLoad();
        cms_hideMainMenusOnClick();
    }
})();

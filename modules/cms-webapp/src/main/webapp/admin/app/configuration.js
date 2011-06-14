(function() {

    Ext.Loader.setConfig({
        enabled: true,
        disableCaching: true
    });

    function hideLoadMaskOnLoad()
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

    function hideMainMenusOnClick()
    {
        if (window.addEventListener)
        {
            window.addEventListener('click', function() {
                window.parent.CMS.common.Common.hideMenus();
            }, false);

        }
        else if (window.attachEvent) // IE
        {
            window.attachEvent('onclick', function() {
                window.parent.CMS.common.Common.hideMenus();
            });

        }
    }

    hideLoadMaskOnLoad();
    hideMainMenusOnClick();

})();

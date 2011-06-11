(function() {

    Ext.Loader.setConfig({
        enabled: true,
        disableCaching: true
    });

    // TODO: Cross browser support (IE < 9).
    window.addEventListener('load', function() {
        window.parent.appLoadMask.hide();
    }, false);

    window.addEventListener('click', function() {
        window.parent.CMS.common.Common.hideMenus();
    }, false);
})();

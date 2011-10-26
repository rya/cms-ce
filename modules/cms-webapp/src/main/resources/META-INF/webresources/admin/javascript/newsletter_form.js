function OpenMenuItemsAcrossSitesSelectorWindowPage( width, height )
{
    var pageURL = "adminpage?page=850";
    pageURL += "&op=menuitem_selector_across_sites";
    pageURL += "&callback=changeNewsletterMenuItem";
    pageURL += "&menuItemTypeRestriction=4"; // Restrict to menu items of type CONTENT
    pageURL += "&menuItemPageTemplateTypeRestriction=4"; // Restrict to menu items with page templates of type NEWSLETTER

    var l = (screen.width - width) / 2;
    var t = (screen.height - height) / 2;

    var props = "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" +
                height + ",top=" + t + ",left=" + l;
    newWindow = window.open(pageURL, "MenuItemsAcrossSitesSelector", props);
    newWindow.focus();
}

function changeNewsletterMenuItem( menuitemKey, pathToMenuItem )
{
    var hiddenInputNewsletter_menuitemkey = document.getElementById( 'contentdata_newsletter_@menuitemkey' );
    var viewNewsletter_menuitem = document.getElementById( 'viewcontentdata_newsletter_@menuitemkey' );

    hiddenInputNewsletter_menuitemkey.value = menuitemKey;
    viewNewsletter_menuitem.value = pathToMenuItem;
}

function writeTextAreaValueToIFrameDocument( textAreaId, css )
{
    var textAreaValue = document.getElementById(textAreaId).value;
    var iFrame = document.getElementById(textAreaId + '_iframe');
    var isIE = document.all;

    var iFrameDocument = isIE ? iFrame.contentWindow.document : iFrame.contentDocument;

    iFrameDocument.open();
    iFrameDocument.write(textAreaValue);
    iFrameDocument.close();

    // We need to set the class for ifr/document/body to "mceContentBody"
    // since TinyMCEs content.css uses this class as root in their selectors in content.css
    // iFrameDocument.getElementsByTagName('body')[0].className = 'mceContentBody';

    // Embed TinyMCEs content.css and EVSs contenttype css
    // css = "content.css,screen.css"
    /*
    if ( css != '' )
    {
        var cssFiles = css.split(',');

        for ( var i = 0; i < cssFiles.length; i++ )
        {
            var link = iFrameDocument.createElement('link');
            link.href = cssFiles[i].toString();
            link.type = 'text/css';
            link.rel = 'stylesheet';
            link.media = 'screen';
            iFrameDocument.getElementsByTagName('head')[0].appendChild(link);
        }
    }

    // Create a transparent shim above the iFrame to make it apear readonly/disabled
    var shim = iFrameDocument.createElement('div');
    shim.style.position = 'absolute';
    shim.style.backgroundColor = '#fff';
    shim.style.left = 0;
    shim.style.top = 0;
    shim.style.width = iFrame.style.width;
    shim.style.height = iFrame.style.height;

    iFrameDocument.getElementsByTagName('body')[0].appendChild(shim);
    */
};
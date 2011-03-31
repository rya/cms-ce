if ( !cms ) var cms = {};
if ( !cms.ui ) cms.ui = {};

cms.ui.Menu = function( dataSoruceElementId )
{
    // *************************************************************************************************************************************
    // *** Private members.
    // *************************************************************************************************************************************
    var dimensions = cms.element.Dimensions;

    var t = this;
    var menu = null;
    var cssMenu = 'cms-menu';
    var dataSourceElem = document.getElementById(dataSoruceElementId);
    var data = dataSourceElem.getElementsByTagName('li');

    // *************************************************************************************************************************************
    // *** Private methods.
    // *************************************************************************************************************************************
    // Method: _createMenu
    function _createMenu()
    {
        menu = document.createElement('div');
        menu.id = dataSourceElem.id + 'menu';
        menu.style.display = 'none';
        menu.className = cssMenu;

        // Create the menu items based on the li elements.
        var currentLi, position, href, text, title, target, onclick, backgroundImage, disabled;
        for ( var i = 0; i < data.length; i++ )
        {
            currentLi = data[i];
            position = i;
            href = currentLi.getElementsByTagName('a')[0].href;
            text = currentLi.getElementsByTagName('a')[0].firstChild.nodeValue || '';
            title = currentLi.getElementsByTagName('a')[0].title;
            target = currentLi.getElementsByTagName('a')[0].target;
            onclick = currentLi.getElementsByTagName('a')[0].onclick;
            backgroundImage = currentLi.style.backgroundImage;
            disabled = currentLi.className.indexOf('cms-sp-mi-disabled') > -1;

            t.addMenuItem({
                position : position,
                href : href,
                text : text,
                tooltip : title,
                target : target,
                onclick : onclick,
                backgroundImage : backgroundImage,
                disabled : disabled
            });
        }

        dataSourceElem.parentNode.insertBefore(menu, dataSourceElem);
    };

    // *************************************************************************************************************************************
    // *** Public methods.
    // *************************************************************************************************************************************
    // Insert
    this.insert = function()
    {
        _createMenu();
    };

    // getMenuElement
    this.getMenuElement = function()
    {
        return t.menu;
    };

    // toggleMenu
    this.toggleMenu = function( event )
    {
        if ( !event ) var event = window.event; // IE

        event.cancelBubble = true;
        if ( event.stopPropagation ) event.stopPropagation();

        var x, y, h, menuRectBottom, windowViewPort;
        x = dimensions.getX(button);
        h = button.offsetHeight;
        menu.style.top = ( y + h ) + 'px';
        menu.style.left = x + 'px';

        if ( menu.style.display == 'none' )
            this.showMenu();
        else
            this.hideMenu();

        // Point the menu in the upward direction if the menu height is out of window bottom bound.
        windowViewPort = dimensions.getWindowSize()[1] + dimensions.getScrollXY()[1];
        menuRectBottom = parseInt(menu.style.top) + y + menu.offsetHeight;
        menu.style.top = ( menuRectBottom > windowViewPort ) ? parseInt(y - menu.offsetHeight) + 'px' : menu.style.top;

    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    // showMenu
    this.showMenu = function()
    {
        menu.style.display = 'block';
        _hideSelectElements(true);
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    // hideMenu
    this.hideMenu = function()
    {
        menu.style.display = 'none';
        button.className = cssButton;
        _hideSelectElements(false);
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    // addMenuItem
    this.addMenuItem = function( config )
    {
        var menuItem = document.createElement('a');

        if ( config.href == '#' || config.href == 'javascript:void(0);' || config.href == 'void(0);' || config.onclick )
            config.href = 'javascript:;';

        if ( !config.disabled )
            menuItem.href = config.href.toString();
        else
        {
            menuItem.href = '#';
            menuItem._href = config.href.toString();
            menuItem.className = 'cms-sp-mi-disabled';
        }

        if ( config.tooltip ) menuItem.title = config.tooltip;
        if ( config.target ) menuItem.target = config.target;
        if ( config.onclick ) menuItem.onclick = config.onclick;

        if ( config.backgroundImage )
        {
            menuItem.style.backgroundImage = config.backgroundImage;
            menuItem.style.backgroundRepeat = 'no-repeat';
        }

        if ( !css.hasClass(dataSourceElem, 'no-default-action') && config.position == 0 )
            menuItem.style.display = 'none';

        var text = document.createTextNode(config.text);
        menuItem.appendChild(text);

        menu.appendChild(menuItem);
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    // getMenuItemText
    this.getMenuItemText = function( pos )
    {
        var text = '';
        var liElement = data[pos];
        var aElement = liElement.getElementsByTagName('a')[0];
        if ( aElement )
            text = aElement.innerHTML;
        else
            text = liElement.innerHTML;

        return text || '';
    };
};
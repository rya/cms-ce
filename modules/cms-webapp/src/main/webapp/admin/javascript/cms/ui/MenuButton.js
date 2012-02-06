/**
 * This class creates a split button using a unordered list (UL) as it datasoruce.
 */

if ( !cms ) var cms = {};
if ( !cms.ui ) cms.ui = {};

cms.ui.MenuButton = function( dataSoruceElementId )
{
    // *************************************************************************************************************************************
    // *** Private members.
    // *************************************************************************************************************************************
    var t = this;
    var dimensions = cms.element.Dimensions;
    var css = cms.element.Css;
    var evt = cms.utils.Event;

    var dataSource = document.getElementById(dataSoruceElementId);
    var data = dataSource.getElementsByTagName('li');
    var button = null;
    var buttonIsPressed = false;
    var menu = null;

    var cssButton = 'cms-menu-button';
    var cssButtonPressed = 'cms-menu-button-pressed';
    var cssButtonDisabled = 'cms-menu-button-disabled';

    // *************************************************************************************************************************************
    // *** Public members.
    // *************************************************************************************************************************************
    this.text = dataSource.title;

    // *************************************************************************************************************************************
    // *** Private methods.
    // *************************************************************************************************************************************
    // Method: _createSplitButtons
    function _createMenuButton()
    {
        button = _createButtonElement(dataSource.title, dataSource.id + 'button', cssButton);

        dataSource.parentNode.insertBefore(button, dataSource);

        button.title = dataSource.title;

        evt.addListener(button, 'mousedown', function()
        {
            this.className = cssButtonPressed;
        }, false);

        evt.addListener(button, 'mouseup', function()
        {
            this.className = cssButtonPressed;
            // this.blur();
        });

        evt.addListener(button, 'click', function( event )
        {
            buttonIsPressed = ( buttonIsPressed === false );
            t.toggleMenu(event);
        }, true);

        if ( !document.all )
            button.style.width = button.offsetWidth + 'px';
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    // _createButtonElement
    function _createButtonElement( value, id, className )
    {
        var button = document.createElement('input');
        button.type = 'button';
        button.id = id;
        button.className = className;
        button.value = value;
        return button;
    }
    // -------------------------------------------------------------------------------------------------------------------------------------

    // Method: _createMenu
    function _createMenu()
    {
        menu = document.createElement('div');
        menu.id = dataSource.id + 'menu';
        menu.style.display = 'none';
        menu.className = 'cms-menu';

        // Create the menu items based on the li elements.
        var currentLi, position, href, text, title, target, onclick, className, disabled;
        for ( var i = 0; i < data.length; i++ )
        {
            currentLi = data[i];
            position = i;
            href = currentLi.getElementsByTagName('a')[0].href;
            text = currentLi.getElementsByTagName('a')[0].firstChild.nodeValue || '';
            title = currentLi.getElementsByTagName('a')[0].title;
            target = currentLi.getElementsByTagName('a')[0].target;
            onclick = currentLi.getElementsByTagName('a')[0].onclick;
            className = currentLi.className.replace(/cms-sp-mi-disabled/gi, '');
            disabled = currentLi.className.indexOf('cms-sp-mi-disabled') > -1;

            t.addMenuItem({
                position : position,
                href : href,
                text : text,
                tooltip : title,
                target : target,
                onclick : onclick,
                className : className,
                disabled : disabled
            });
        }

        dataSource.parentNode.insertBefore(menu, dataSource);
    }

    // -------------------------------------------------------------------------------------------------------------------------------------

    // _hideSelectElements
    function _hideSelectElements( hide )
    {
        if ( !cms.browser.isIE6 ) return;

        var selectElements = document.getElementsByTagName('select');
        var selectElementsLn = selectElements.length;
        var val = hide ? 'hidden' : 'visible';
        for ( var i = 0; i < selectElementsLn; i++ )
        {
            selectElements[i].style.visibility = val;
        }
    }

    // *************************************************************************************************************************************
    // *** Public methods.
    // *************************************************************************************************************************************
    // insert
    this.insert = function()
    {
        if ( dataSource.nodeName.toLowerCase() != 'ul' )
        {
            alert('Datasource for splitbutton must be an UL element!');
            return;
        }

        dataSource.style.display = 'none';

        _createMenuButton();
        _createMenu();

        var isDisabled = css.hasClass(dataSource, 'disabled');
        this.setDisabled(isDisabled);
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    // setDisabled
    this.setDisabled = function( disabled )
    {
        if ( typeof disabled != 'boolean' ) return;
        button.disabled = disabled;
        button.className = disabled ? cssButtonDisabled : cssButton;
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    // setText
    this.setText = function( text, tooltipText )
    {
        button.value = text;
        if ( tooltipText && tooltipText != '' )
            button.title = tooltipText;
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    // show
    this.show = function()
    {
        button.style.visibility = 'visible';
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    // hide
    this.hide = function()
    {
        button.style.visibility = 'hidden';
        this.hideMenu();
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    // toggleMenu
    this.toggleMenu = function( event )
    {
        if ( !event ) var event = window.event; // IE

        event.cancelBubble = true;
        if ( event.stopPropagation ) event.stopPropagation();

        var x, y, h, menuRectBottom, windowViewPort;
        x = dimensions.getX(button);
        y = dimensions.getY(button);
        h = button.offsetHeight;

        menu.style.top = ( y + h ) + 'px';

        menu.style.left = x + 'px';

        if ( buttonIsPressed )
            this.showMenu();
        else
            this.hideMenu();

        // Point the menu in the upward direction if the menu height is out of window view port.
        windowViewPort = dimensions.getWindowSize()[1] + dimensions.getScrollXY()[1];
        menuRectBottom = parseInt(menu.style.top) + y + menu.offsetHeight;
        menu.style.top = ( menuRectBottom > windowViewPort ) ? parseInt(y - menu.offsetHeight) + 'px' : menu.style.top;
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    // showMenu
    this.showMenu = function()
    {
        menu.style.display = 'block';
        button.className = cssButtonPressed;
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
    this.addMenuItem = function( attributes )
    {
        var menuItem = document.createElement('a');

        if ( attributes.href == '#' || attributes.href == 'javascript:void(0);' || attributes.href == 'void(0);' || attributes.onclick )
            attributes.href = 'javascript:;';

        if ( !attributes.disabled )
            menuItem.href = attributes.href.toString();
        else
        {
            menuItem.href = '#';
            menuItem._href = attributes.href.toString();
            menuItem.className = 'cms-sp-mi-disabled';
        }

        if ( attributes.tooltip ) menuItem.title = attributes.tooltip;
        if ( attributes.target ) menuItem.target = attributes.target;
        if ( attributes.onclick ) menuItem.onclick = attributes.onclick;

        if ( attributes.className )
        {
            menuItem.className = attributes.className;
        }

        /*
        if ( !css.hasClass(dataSource, 'no-default-action') && attributes.position == 0 )
            menuItem.style.display = 'none';
        */

        var text = document.createTextNode(attributes.text);
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
    // -------------------------------------------------------------------------------------------------------------------------------------

    // Attatch a click event to the document. This will hide the menu when the user clicks anywhere outside the button.
    evt.addListener(document, 'click', function( event )
    {
        if ( !event ) var event = window.event; // IE

        var target = evt.getTarget(event);
        if (target !== button) buttonIsPressed = false;

        t.hideMenu(event);

    }, true, dataSoruceElementId);
};
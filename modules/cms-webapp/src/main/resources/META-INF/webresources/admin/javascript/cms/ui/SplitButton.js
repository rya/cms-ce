/**
 * This class creates a split button using a unordered list (UL) as it datasoruce.
 */

if ( !cms ) var cms = {};
if ( !cms.ui ) cms.ui = {};

cms.ui.SplitButton = function( dataSoruceElementId )
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
    var leftButton = null;
    var rightButton = null;
    var menu = null;

    var cssLeftButton = 'cms-split-button-left';
    var cssRightButton = 'cms-split-button-right';
    var cssLeftButtonPressed = 'cms-split-button-left-pressed';
    var cssLeftButtonDisabled = 'cms-split-button-left-disabled';
    var cssRightButtonPressed = 'cms-split-button-right-pressed';
    var cssRightButtonDisabled = 'cms-split-button-right-disabled';
    var cssRightButtonHover = 'cms-split-button-right-hover';

    // *************************************************************************************************************************************
    // *** Public members.
    // *************************************************************************************************************************************
    this.text = dataSource.title;

    // *************************************************************************************************************************************
    // *** Private methods.
    // *************************************************************************************************************************************
    // Method: _createSplitButtons
    function _createSplitButton()
    {
        leftButton = _createButtonElement(dataSource.title, dataSource.id + 'leftButton', cssLeftButton);
        rightButton = _createButtonElement(' ', dataSource.id + 'rightButton', cssRightButton);

        dataSource.parentNode.insertBefore(rightButton, dataSource);
        dataSource.parentNode.insertBefore(leftButton, rightButton);

        if( css.hasClass( dataSource, "no-default-action" ) )
            leftButton.title = dataSource.title;
        else
            leftButton.title = dataSource.title + ': ' + t.getMenuItemText(0);

        evt.addListener(leftButton, 'mousedown', function()
        {
            this.className = cssLeftButtonPressed;
        }, false);

        evt.addListener(leftButton, 'mouseup', function()
        {
            this.className = cssLeftButton;
        });

        evt.addListener(leftButton, 'click', function( event )
        {
            if ( css.hasClass(dataSource, 'no-default-action') )
            {
                t.toggleMenu(event);
            }
            else
            {
                var aElem = menu.getElementsByTagName('a')[0];
                // Using the click method will ensure that the referer is reported in MSIE.
                if ( document.all )
                    aElem.click();
                else
                    document.location.href = aElem.href;
            }

        }, true);

        // Right button down state
        evt.addListener(rightButton, 'mousedown', function( event )
        {
            t.toggleMenu(event);
            this.className = ( menu.style.display == 'block' ) ? cssRightButtonPressed : cssRightButton;

        }, true);

        evt.addListener(rightButton, 'mouseup', function()
        {
            // this.blur();
        }, true);

        evt.addListener(rightButton, 'mouseover', function()
        {
            this.className = ( menu.style.display == 'block' ) ? cssRightButtonPressed : cssRightButtonHover;
        }, false);

        evt.addListener(rightButton, 'mouseout', function()
        {
            this.className = ( menu.style.display == 'block' ) ? cssRightButtonPressed : cssRightButton;
        }, true);

    }
    ;
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

        dataSource.parentNode.insertBefore(menu, dataSource);
    }
    ;
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

        _createSplitButton();
        _createMenu();

        var isDisabled = css.hasClass(dataSource, 'disabled');
        this.setDisabled(isDisabled);
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    // setDisabled
    this.setDisabled = function( disabled )
    {
        if ( typeof disabled != 'boolean' ) return;
        leftButton.disabled = disabled;
        rightButton.disabled = disabled;
        leftButton.className = disabled ? cssLeftButtonDisabled : cssLeftButton;
        rightButton.className = disabled ? cssRightButtonDisabled : cssRightButton;
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    // setText
    this.setText = function( text, tooltipText )
    {
        leftButton.value = text;
        if ( tooltipText && tooltipText != '' )
            leftButton.title = tooltipText;
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    // show
    this.show = function()
    {
        leftButton.style.visibility = 'visible';
        rightButton.style.visibility = 'visible';
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    // hide
    this.hide = function()
    {
        leftButton.style.visibility = 'hidden';
        rightButton.style.visibility = 'hidden';
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
        x = dimensions.getX(leftButton);
        y = dimensions.getY(leftButton);
        h = leftButton.offsetHeight;
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
        rightButton.className = cssRightButtonPressed;
        _hideSelectElements(true);
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    // hideMenu
    this.hideMenu = function()
    {
        menu.style.display = 'none';
        rightButton.className = cssRightButton;
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

        if ( !css.hasClass(dataSource, 'no-default-action') && config.position == 0 )
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
    // -------------------------------------------------------------------------------------------------------------------------------------

    // Attatch a click event to the document. This will hide the menu when the user clicks outside the right button.
    evt.addListener(document, 'click', function( event )
    {
        if ( !event ) var event = window.event; // IE

        var target = evt.getTarget(event);

        if ( (css.hasClass(dataSource, 'no-default-action') && target) == leftButton || target == rightButton ) return;

        var buttonRightClassName = cssRightButton;
        t.hideMenu(event);

        if ( menu.style.display == 'block' )
            buttonRightClassName = cssRightButtonPressed;
        else
            buttonRightClassName = ( !rightButton.disabled ) ? cssRightButton : cssRightButtonDisabled;

        rightButton.className = buttonRightClassName;

    }, true, dataSoruceElementId);

};
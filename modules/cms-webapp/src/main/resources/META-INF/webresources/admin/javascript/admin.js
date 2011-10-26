/**
 * Cross browser createElement method
 * Creates an element(el) with a name attribute and appends it to docObj
 * This function solves the MSIE problem where @name is readonly.
 */
function createNamedElement(docObj, el, name)
{
	var element = null;

	// IE way; Fails on standards-compliant browsers

	try {
		element = docObj.createElement('<'+el+' name="'+name+'">');
  } catch (e)	{
		// error if not IE
	}

	// Other browsers

	if (!element) {
		element = docObj.createElement(el);
		element.name = name;
	}

  return element;
}

/**
 * Cross browser event handler
 *
 * @param {Object} obj          - Object to attach event.
 * @param {Event} evType		    - Event type. click etc.
 * @param {Function} fn			    - Function.
 * @param {Boolean} useCapture
 */
function addEvent(obj, evType, fn, useCapture)
{
	// Standard way
	if (obj.addEventListener) {
		obj.addEventListener(evType, fn, useCapture);
		return true;
	} else if (obj.attachEvent) {
		var r = obj.attachEvent("on"+evType, fn);
		return r;
	} else {
		alert("Handler could not be attached");
    return true;
  }
}

/**
 * Cross browser remove event handler
 *
 * @param {Object} obj          - Object to attach event.
 * @param {Event} evType		- Event type. click etc.
 * @param {Function} fn			- Function.
 */
function removeEvent( obj, evType, fn )
{
    if ( obj.detachEvent )
    {
        obj.detachEvent( 'on'+evType, obj[evType+fn] );
        obj[evType+fn] = null;
    } else
        obj.removeEventListener( evType, fn, false );
}

/**
 * Go to location
 * (Note! IE does not leave any referer)
 *
 * @param (String) href
 */
function gotoLocation(href)
{
	if (href)
	{
		document.location.href = href;
	}
	else
	{
		alert("URI not specified");
	}
}

function addTableRow( bodyName, copyIndex, clear)
{
    var body = document.getElementById(bodyName);
	var sourceRow = body.rows[copyIndex];
    var clone = sourceRow.cloneNode(true);

    body.appendChild(clone);

    var row = body.rows[body.rows.length - 1];
	if (clear) {
	  var inputElements = row.getElementsByTagName('input');
        for(var i = 0; i < inputElements.length; ++i) {
	        if (inputElements[i].type == 'radio' || inputElements[i].type == 'checkbox') {
	            inputElements[i].checked = false;
            } else {
	            inputElements[i].value = '';
	        }
        }
	}
}

function showHideHelp(callee, name) {
  var objects = document.getElementsByName(callee.id);
  var itemcount = 1;

  if (objects.length != null)
		itemcount = objects.length;

  // Find current index
	var index = 0;
	if (itemcount > 1) {
    for(var i=0; i < itemcount; i++) {
			if(objects[i] == callee) {
				index = i;
				break;
      }
    }
  }
  var elem = document.getElementsByName(name+'_help')[index];

  if (elem.style.display == "none") {
		elem.style.display = "block";
	} else {
		elem.style.display = "none";
	}
}

function admin_lockUnlockTextInput( lockIconElem, textInputelemId, attributeToSet, callback )
{
    var textInputElem = document.getElementById(textInputelemId);
    var attr = ( attributeToSet === 'disabled' ) ? 'disabled' : 'readOnly';

    textInputElem[attr] = ( textInputElem[attr] !== true );

    if ( textInputElem[attr] === true )
    {
        lockIconElem.src = 'images/icon_lock_closed.png';
    }
    else
    {
        lockIconElem.src = 'images/icon_lock_open.png';
        textInputElem.focus();
    }

    if ( callback )
    {
        callback();
    }
}

function admin_css_toggleDisplay(elem, defaultDisplay)
{
    var _defaultDisplay = ( defaultDisplay ) ? defaultDisplay : 'block';
    elem.style.display = ( elem.style.display === 'none' ) ? _defaultDisplay : 'none';
}

function setParameter(url, param, value) {
	var paramStart = url.indexOf("?");

	// If parameter is not present
	if (paramStart == -1)
		url += ("?"+param+"="+value);
	else {
		var address = url.substring(0, paramStart);
		var params = url.substring(paramStart);

		var paramPos = params.indexOf(param+"=");

		if (paramPos > -1 && (params.charAt(paramPos-1) == '?' || params.charAt(paramPos-1) == '&')) {
			// Parameter is in the url
			paramStart = Math.max(params.indexOf("?"+param+"="), params.indexOf("&"+param+"="));
			var paramEnd = params.substring(paramStart+1).indexOf("&");

			if (paramEnd == -1) {
				// This was the last parameter
				url = address + params.substring(0, paramStart+1) + param + "=" +value;
			}
			else {
				url = address + params.substring(0, paramStart+1) + param + "=" + value + params.substring(paramStart+1+paramEnd);
			}
		}
		else {
			// Parameter is not already in the url
			url = url + "&" + param + "="+ value;
		}
	}
	return url;
}

function OpenObjectBrowsePopup(menuKey, fieldName, fieldRow) {
  var width = 600;
  var height = 500;
  var l = (screen.width - width) / 2;
  var t = (screen.height - height) / 2;
  var url = "adminpage?page=900&menukey="+menuKey+"&op=browse&subop=browsepopup&fieldname="+fieldName+"&fieldrow="+fieldRow;
  var newWindow = window.open(url, "Preview", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height + ",top=" + t + ",left=" + l);
  newWindow.focus();
}

function OpenResourcePopup(fieldName, mimeType, extension) {
	var url = "adminpage?page=800&op=popup&fieldname="+fieldName+"&mimetype="+mimeType+"&extension="+extension;

	var width = 900;
	var height = 600;
	var l = (screen.width - width) / 2;
	var t = (screen.height - height) / 2;
  	var newWindow = window.open(url, "_blank", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width="+width+",height="+height+",top="+t+",left="+l+"");
	newWindow.focus();
}

function OpenResourceInUseByPopup(resourceKey) {
	var url = "adminpage?page=800&op=inuseby&resourcekey="+resourceKey;
	var width = 300;
	var height = 600;
	var l = (screen.width - width) / 2;
	var t = (screen.height - height) / 2;
  	var newWindow = window.open(url, "_blank", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width="+width+",height="+height+",top="+t+",left="+l+"");
	newWindow.focus();
}

function OpenContentPopup(unitKey, categoryKey, mode, fieldName, fieldRow, contentTypes, minOccurrence, maxOccurrence)
{
    minOccurrence = ( minOccurrence === undefined ) ? -1 : minOccurrence;
    maxOccurrence = ( maxOccurrence === undefined ) ? -1 : maxOccurrence;

    var url = "adminpage?page=600&op=popup&subop=" + mode + "&selectedunitkey=" + unitKey + "&fieldname=" + fieldName + "&fieldrow=" + fieldRow;
    if (contentTypes != null)
    {
        if (contentTypes.length == undefined)
        {
            // One contenttype is specified
            if (isInteger(contentTypes))
                url = url + "&contenttypekey="+contentTypes;
            else
                url = url + "&contenttypename="+contentTypes;
        }
        else
        {
            // More than one contenttypes are specified
            for(key in contentTypes)
            {
                if (isInteger(contentTypes[key]))
                    url = url + "&contenttypekey="+contentTypes[key];
                else
                    url = url + "&contenttypename="+contentTypes[key];
            }
        }
    }

    url = url + "&minoccurrence=" + minOccurrence + "&maxoccurrence=" + maxOccurrence;

    var width = 980;
    var height = 600;
    var l = (screen.width - width) / 2;
    var t = (screen.height - height) / 2;
    var newWindow = window.open(url, "_blank", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width="+width+",height="+height+",top="+t+",left="+l+"");
    newWindow.focus();
    return newWindow;
}

function OpenObjectPopup(menuKey, objectKey, fieldName, fieldRow) {
	var url = "adminpage?page=900&op=form&subop=popup&key=" + objectKey + "&menukey=" +menuKey + "&fieldname=" + fieldName + "&fieldrow=" + fieldRow + "&callback=callback_editObject";
	var width = 900;
	var height = 600;
	var l = (screen.width - width) / 2;
	var t = (screen.height - height) / 2;

	var newWindow = window.open(url, "_blank", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width="+width+",height="+height+",top="+t+",left="+l+"");
	newWindow.focus();
	return newWindow;
}

function OpenContentPopupByHandler(unitKey, categoryKey, mode, fieldName, fieldRow, handler)
{
  var url = "adminpage?page=600&op=popup&subop=" + mode + "&selectedunitkey=" + unitKey + "&fieldname=" + fieldName + "&fieldrow=" + fieldRow + "&handler=" + handler;
	var width = 990;
  var height = 620;
	var l = (screen.width - width) / 2;
	var t = (screen.height - height) / 2;
	var newWindow = window.open(url, "_blank", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width="+width+",height="+height+",top="+t+",left="+l+"");
	newWindow.focus();
	return newWindow;
}

function OpenEditContentPopup(contentKey, versionKey, fieldName, fieldRow, callback)
{
	var url = "adminpage?page=993&op=form&key=" + contentKey + "&versionkey=" + versionKey + "&subop=popup" + "&fieldname=" + fieldName + "&fieldrow=" + fieldRow+ "&callback=" + callback;
	var width = 990;
	var height = 620;
	var l = (screen.width - width) / 2;
	var t = (screen.height - height) / 2;
	var newWindow = window.open(url, "_blank", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width="+width+",height="+height+",top="+t+",left="+l+"");
	newWindow.focus();
	return newWindow;
}

function OpenNewCategorySelector(unitKey, contentTypeString, unitFilterContentType, requireCategoryAdmin, excludeCategoryKey, excludeCategoryKeyWithChildren)
{
	var url = "adminpage?page=200&op=menu&selectedunitkey=" + unitKey + "&subop=callback" + "&contenttypestring=" + contentTypeString;
	if (unitFilterContentType != undefined)
		url = url + "&unitfiltercontenttype="+unitFilterContentType;
	if (requireCategoryAdmin != undefined)
		url = url + "&requirecategoryadmin="+requireCategoryAdmin;
	if (excludeCategoryKey != undefined)
		url = url + "&excludecategorykey="+excludeCategoryKey;
	if (excludeCategoryKeyWithChildren != undefined)
		url = url + "&excludecategorykey_withchildren="+excludeCategoryKeyWithChildren;

	var width = 260;
	var height = 360;
	var l = (screen.width - width) / 2;
	var t = (screen.height - height) / 2;

	var newWindow = window.open(url, "categorypopup", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width="+width+",height="+height+",top="+t+",left="+l+"");
	newWindow.focus();
	return newWindow;
}

function moveTableRowUp(tBodyId, index)
{
	var tBodyElem = document.getElementById(tBodyId);
	var row1 = tBodyElem.rows[index];
	var row2 = tBodyElem.rows[(index-1)];

  if (index-1 == 0) {
    row1 = tBodyElem.rows[index];
    row2 = tBodyElem.rows[tBodyElem.rows.length];
  }

	tBodyElem.insertBefore(row1, row2);
}

function moveTableRowDown(tBodyId, index)
{
  var tBodyElem = document.getElementById(tBodyId);
  var row1 = tBodyElem.rows[(index+1)];
  var row2 = tBodyElem.rows[index];

  if ((index+1) == tBodyElem.rows.length) {
    row1 = tBodyElem.rows[index];
    row2 = tBodyElem.rows[1];
  }

  tBodyElem.insertBefore(row1, row2);
}

// Removes a row from a table, using 'callee' to locate the
// row number that is to be deleted. If only one row is found,
// clearFunction is called instead of removing the row (if defined).
function removeTableRow(callee, tableName, clearFunction, addIdx)
{
	count = itemcount(document.getElementsByName(callee.name));
	if( count == 1 )
	{
		if (clearFunction && clearFunction != null)
		{
			eval(clearFunction);
		}
		else
		{
			document.getElementById(tableName).deleteRow(0 + addIdx);
		}
    	return;
	}
	var index = getObjectIndex(callee);
	document.getElementById(tableName).deleteRow(index + addIdx);
}


function getObjectIndex( obj )
{
	var lNumRows = itemcount(document.getElementsByName(obj.name));
	if( lNumRows > 1 )
	{
		for( var i=0; i < lNumRows; i++ )
		{
			if( document.getElementsByName(obj.name)[i] == obj )
			{
				return i;
			}
		}
	}
	else
	{
		return 0;
	}
}

function itemcount(elName)
{
	var lItems;
	if (elName.length!=null)
	{
		lItems = elName.length;
	}
	else
	{
		lItems = 1;
	}
	return lItems;
}

function stripSpaces(str)
{
  var x;
  x = str;
  return (x.replace(/^\W+/, '')).replace(/\W+$/, '');
}

function getParameterInTextDefaultValue( text, paramName, defaultValue )
{
    var value = getParameterInText( text, paramName );
    if( value == null )
	{
		return defaultValue;
	}
}

function getParameterInTextDefault( text, paramName )
{
    return getParameterInText( text, paramName, "[", "]", ";" );
}

function getParameterInText( text, paramName, paramsStartBlock, paramsEndBlock, paramsSeperator )
{
    var posParamsStartBlock = text.indexOf( paramsStartBlock );
    if( posParamsStartBlock == -1 )
        return null;


    var posParamName = text.indexOf( paramName, posParamsStartBlock );
    if( posParamName == -1 )
        return null;

    var posParamValueStart = text.indexOf( "=", posParamName );
    if( posParamValueStart == -1 )
        return null;

    // Find end position of the value..
    // Trying paramsSeperator first
    var posParamValueEnd = text.indexOf( paramsSeperator, posParamValueStart );
    // .. if not successful, trying paramsEndBlock instead
    if( posParamValueEnd == -1 )
        posParamValueEnd = text.indexOf( paramsEndBlock, posParamValueStart );

    if( posParamValueStart == -1 || posParamValueEnd == -1 )
        return null;

    // Set the position past the = character
    posParamValueStart++;

    return text.substring( posParamValueStart, posParamValueEnd );
}

function createIconButton( type, name, image, onclickCode, disabled )
{
    return createButton( type, name, null, image, onclickCode, disabled );
}

function createButton( type, name, caption, image, onclickCode, disabled )
{
    var className = "button_image_small";

    if ( caption != null )
    {
        className = "button_text";
    }


    // var buttonElement = document.createElement('button');
    var buttonElement = createNamedElement(document, 'button', name)
    buttonElement.setAttribute('type', type);
    buttonElement.setAttribute('class', className); //IE
    buttonElement.setAttribute('className', className); //Other browsers
    // buttonElement.setAttribute('name', name);
    buttonElement.setAttribute('id', name);
    if ( onclickCode != null )
    {
        //buttonElement.setAttribute('onclick',onclickCode);
        buttonElement.onclick = function()
        {
            eval(onclickCode)
        }
    }

    if ( image != null )
    {
        var btnImage = document.createElement('img');
        btnImage.setAttribute('src', image);
        btnImage.setAttribute('style', 'border:none');
        buttonElement.appendChild(btnImage);
    }
    return buttonElement;
}

function iconButton_setDisabled(buttonElement)
{
    buttonElement.disabled = true;
    var iconElement = buttonElement.getElementsByTagName('img')[0];
    if (iconElement)
        iconElement.className = 'disabled-element';
}

/*
** getParentNodeOfType
**
** Description:
**  Returns the first parent node with specified tag name.
*/
function getParentNodeOfType( child, type )
{
    var current = child.parentNode;
    var parentOfType = null;

    while( true ) {
        if( current == null )
            break;

        if( current.nodeName.toLowerCase() == type.toLowerCase() )
        {
            parentOfType = current;
            break;
        }
        current = current.parentNode;
    }

    return parentOfType;
}

/*
** getParentNodeOfClass
**
** Description:
**  Returns the first parent node with specified className.
*/
function getParentNodeOfClass( child, className )
{
    var current = child.parentNode;
    var parentOfClass = null;

    while( true ) {
        if( current == null )
            break;

        if( current.className.toLowerCase() == className.toLowerCase() )
        {
            parentOfClass = current;
            break;
        }
        current = current.parentNode;
    }

    return parentOfClass;
}


/*
** getChildPosition
**
** Description:
**  Returns the position of the child among it's siblings
*/
function getChildPosition( child )
{
    var current = child;
    var counter = 1;
    while( true )
	{
        if( current.previousSibling == null )
		{
            break;
		}
        current = current.previousSibling;

		// Skip text nodes because Firefox counts them.

		if (current.nodeType !=3)
		{
        	counter++;
		}
    }
    return counter;
}

/*
** showPopupWindow
**
** Description:
**  ...
*/
function showPopupWindow( url, name, width, height)
{
	var l = (screen.width - width) / 2;
	var t = (screen.height - height) / 2;
  var features = "toolbar=0,location=0,directories=0,status=1,menubar=0,scrollbars=1,resizable=1,copyhistory=02";
  features += ",width="+width+",height="+height+',top='+t+',left='+l;
  return window.open( url, name, features );
}

/*
** selectAllRowsInSelect
**
** Description:
**  ...
*/
function selectAllRowsInSelect( src )
{
	var s = document.getElementById( src );
	for( var i = 0; i < s.options.length; i++ ) {
		s.options[i].selected = true;
	}
}

/*
** setImageButtonEnabled
**
** Description:
**  ...
*/
function setImageButtonEnabled( button, enabled ) {
    // Fx 3 sometimes throws an unknown and unharmful exception that seems related to the browser chorme(XUL/Javascript).
    // Could be Firebug 1.2b13.
    try
    {
        if( button && button.nodeName != '' && ( button.nodeName.toLowerCase() == "a" || button.nodeName.toLowerCase() == "button" )) {
            if(enabled) {
                button.style.cursor="pointer";
            } else {
                button.style.cursor="default";
            }

            if (button.nodeName.toLowerCase() == "a") {
                button = button.children[0];
            }
        }

        button.disabled = ( enabled ? false : true );

        // Check if the button have a image to "disable"
        if( button.firstChild ) {
            if( enabled ) {
                if (document.all) {
                    button.firstChild.style.filter="alpha(opacity=100)";
                } else  {
                    button.firstChild.style.opacity = '1';
                }
            } else {
                if (document.all) {
                    button.firstChild.style.filter="alpha(opacity=30)";
                } else {
                    button.firstChild.style.opacity = '0.3';
                }
            }
        }
    }
    catch(err) { /**/ }
}

function disableTextButtons(buttonIds)
{
	if (buttonIds != null)
	{
		if (buttonIds.length == undefined)
		{
			// One button is specified
			var buttons = document.getElementsByName(buttonIds);
			if (buttons != null) {
				for (var i=0; i<buttons.length; i++) {
					setTextButtonEnabled(buttons[i], false);
				}
			}
		}
		else
		{
			// More than one button is specified
			for(id in buttonIds)
			{
				var buttons = document.getElementsByName(buttonIds[id]);
				if (buttons != null) {
					for (var i=0; i<buttons.length; i++) {
						setTextButtonEnabled(buttons[i], false);
					}
				}
 	    }
   	}
  }
}

function enableTextButtons(buttonIds)
{
  if (buttonIds != null)
	{
		if (buttonIds.length == undefined)
		{
			// One button is specified
			var buttons = document.getElementsByName(buttonIds);
			if (buttons != null) {
				for (var i=0; i<buttons.length; i++) {
					setTextButtonEnabled(buttons[i], true);
				}
			}
		}
		else
		{
			// More than one button is specified
			for(id in buttonIds)
			{
				var buttons = document.getElementsByName(buttonIds[id]);
				if (buttons != null) {
					for (var i=0; i<buttons.length; i++) {
						setTextButtonEnabled(buttons[i], true);
					}
				}
      }
    }
  }
}

/*
** setTextButtonEnabled
**
** Description:
**  ...
*/
function setTextButtonEnabled( button, enabled )
{
    // check if it is a link button
    if( button.nodeName == "A" || button.nodeName == "a" ) {
        // Change the <a> tag first
        if( enabled )
            button.style.cursor="hand";
        else
            button.style.cursor="default";

        // move to the <button> element
        button = button.children[0];
    }

    if( enabled )
        button.style.cursor="hand";
    else
        button.style.cursor="default";

    button.disabled = ( enabled ? false : true );
}

function setFieldFocus(fieldId) {
	var field = document.getElementById(fieldId);
	if (field != undefined) {
		try {
			field.focus();
		}
		catch(e){
		}
	}
}

function setFocus()
{
	var forms = document.getElementsByTagName("form")[0];
	if (forms != undefined && forms.length > 0) {
		form_setFocus(forms);
	}
}

// Sets focus to the first field in the form
function form_setFocus( aForm )
{
	if( aForm.elements.length > 0)
	{
		var i;
		var max = aForm.length;
		for( i = 0; i < max; i++ ) {
			if( aForm.elements[ i ].type != "hidden" &&
				aForm.elements[ i ].type != undefined &&
				!aForm.elements[ i ].disabled &&
				!aForm.elements[ i ].readOnly ) {
				try {
					aForm.elements[ i ].focus();
				}
				catch(e){
				}
				break;
			}
		}
	}
}

// Moves options from one select box to another
function moveOptions( srcId, destId, fallbackFunction )
{
    if ( !fallbackFunction )
        fallbackFunction = null;

    var s = document.getElementById(srcId);
    var d = document.getElementById(destId);

    // add options to dest and delete them from src
    var optionElement;
    for ( i = 0; i < s.options.length; i++ )
	{
    	if ( s.options[i].selected )
		{
            optionElement = new Option(s.options[i].text, s.options[i].value);
    	    d.options[d.options.length] = optionElement;
            addEvent(optionElement, 'dblclick', function() { moveOptions( destId, srcId, fallbackFunction ); } , false);
        	s.options[i--] = null;
    	}
	}

    if ( fallbackFunction && typeof fallbackFunction === 'function' )
        fallbackFunction();

    s.blur();
}

function checkRadioButtonsOrCheckBoxes(elementsArray, setChecked)
{
    for(var i = 0; i < elementsArray.length; i++)
	{
		var element = elementsArray[i];
        element.checked = setChecked;
    }
}

function setCheckboxValues(checkboxName, value)
{
	var checkboxes = document.getElementsByName(checkboxName);
    for (var i=0; i < checkboxes.length; i++)
	{
		checkboxes[i].checked = value;
	}
}

function anyChecked(checkboxName)
{
	var checkboxes = document.getElementsByName(checkboxName);
	for (var i=0; i < checkboxes.length; i++)
		if (checkboxes[i].checked)
			return true;

	return false;
}

function getCheckedValues(checkboxName)
{
	var checkboxes = document.getElementsByName(checkboxName);
	var values = new Array();
	var counter = 0;
	for (var i=0; i < checkboxes.length; i++)
		if (checkboxes[i].checked)
			values[counter++] = checkboxes[i].value;

	return values;
}

function getElementsByTagNameAndNameWithRexExp(tagName, nameRegexp)
{
    var allElements = document.getElementsByTagName(tagName);
    var matchedElements = new Array();
    if (allElements == null || allElements.length == 0)
	{
        return matchedElements;
    }

    var matchedElementsCount = 0
    var i;
    for (i = 0; i < allElements.length; i++ )
	{
        var element = allElements[i];

        if (nameRegexp.test(element.name))
		{
            matchedElements[matchedElementsCount++] = element;
        }
    }

    return matchedElements;
}

function getCheckedIndexes(checkboxName)
{
	var checkboxes = document.getElementsByName(checkboxName);

	var values = new Array();
	var counter = 0;
	for (var i=0; i < checkboxes.length; i++)
		if (checkboxes[i].checked)
			values[counter++] = i;

	return values;
}

function setValueChecked(checkboxName, value)
{
	var checkboxes = document.getElementsByName(checkboxName);

	for (var i=0; i < checkboxes.length; i++) {
		if (checkboxes[i].value == value) {
			checkboxes[i].checked = value;
			continue;
		}
	}
}

function setValuesUnchecked(checkboxName)
{
	var checkboxes = document.getElementsByName(checkboxName);

	for (var i=0; i < checkboxes.length; i++)
	{
		checkboxes[i].checked = false;
	}
}

function setCheckedValues(checkboxName, valueArray)
{
	var checkboxes = document.getElementsByName(checkboxName);

	for (var i=0; i < checkboxes.length; i++)
	{
		var value = false;
		for (var j=0; j < valueArray.length; j++)
		{
			if (valueArray[j] == checkboxes[i].value)
			{
				value = true;
				continue;
			}
		}
		checkboxes[i].checked = value;
	}
}

function addResource(fieldname, value) {
    var field = document.getElementById(fieldname);
	field.value = value;
	field.className = "textfield";
	if (field.onchange)
		field.onchange();

    var saveButton = document.getElementById('lagre');
    if (saveButton) {
        saveButton.disabled = false;
        saveButton.style.cursor = 'pointer';
    }
}

function addUserParamResource( fieldname, value )
{
    // paramIndex is global and defined in the html document.
    var tParamIndex = paramIndex - 1;
    var field = document.getElementsByName(fieldname)[tParamIndex];
                                                
    var viewField = document.getElementsByName('viewparameter_value')[tParamIndex];

    field.value = value;

    if ( viewField ) viewField.value = value;
}

function removeResource(fieldname) {
	var field = document.getElementById(fieldname);
	field.value = '';

    var saveButton = document.getElementById('lagre');
    if (saveButton) {
        saveButton.disabled = false;
        saveButton.style.cursor = 'pointer';
    }
}

function removeUserResourceParam(fieldname, position) {
    var pos = position - 1;
    var field = document.getElementsByName(fieldname)[pos];
    var viewField = document.getElementsByName('viewparameter_value')[pos] || document.getElementsByName('viewxslparam_value')[pos];

    field.value = '';
    viewField.value = '';
}

function menuItem_hasDefinedParams()
{
    var hasDefinedParams = false;
    var paramName = document.getElementsByName('paramname');
    var paramNameLn = paramName.length;

    for ( var i = 0; i < paramNameLn; i++ )
    {
        if ( paramName[i].value != '' )
        {
            hasDefinedParams = true;
            break;
        }
    }

    return hasDefinedParams;
}

function menuItem_getParamNamedKeyPosition()
{
    var paramPosition = -1;
    var paramName = document.getElementsByName('paramname');
    var paramNameLn = paramName.length;

    for ( var i = 0; i < paramNameLn; i++ )
    {
        if ( paramName[i].value == 'key' )
        {
            paramPosition = i;
            break;
        }
    }

    return paramPosition;
}

function menuItem_countParamFields()
{
    var paramName = document.getElementsByName('paramname');
    return paramName.length;
}

function menuItem_findParamNamedKeyPosition()
{
    var paramRowPosition = -1;

    var hasDefinedParams = menuItem_hasDefinedParams();

    if ( hasDefinedParams )
    {
        var paramNamedKeyPosition = menuItem_getParamNamedKeyPosition();
        if ( paramNamedKeyPosition > -1 )
        {
            paramRowPosition = paramNamedKeyPosition;
        }
        else
        {
            addTableRow('parambody', 0, true);
            paramRowPosition = document.getElementsByName('paramname').length - 1;;
        }
    }
    else
    {
        paramRowPosition = 0;
    }

    return paramRowPosition;
}

function menuItem_removeContent()
{
    var viewSelectedContent =  document.getElementById('view_selected_content');
    var selectedContent =  document.getElementById('_selected_content');
    var contentKeyParamName = document.getElementById('content_key_paramname');
    var contentKeyParamVal = document.getElementById('content_key_paramval');
    var contentKey = document.getElementById('contentkey');
    var contentKeyParamTable = document.getElementById('content_key_paramtable');

    if ( selectedContent )
    {
        viewSelectedContent.value = '';
        selectedContent.value = '';
    }

    if ( contentKeyParamName )
    {
        contentKeyParamName.value = '';
        contentKeyParamVal.value = '';
    }

    if ( contentKey != null)
    {
        contentKey.value = contentKeyParamVal ? contentKeyParamVal.value : '';
    }

    if ( contentKeyParamTable )
    {
        contentKeyParamTable.style.display = 'none';
    }

    if ( typeof contentKeys != 'undefined' )
    {
        contentKeys[0] = -1;
    }
}

function callback_contentfield(fieldname, fieldrow, key, title, current)
{
    document.getElementById(fieldname).value = key;
	document.getElementById('view'+fieldname).value = title;

	setImageButtonEnabled(document.getElementById('edit'+fieldname), true);
	setImageButtonEnabled(document.getElementById('remove'+fieldname), true);

    document.getElementById('content_key_paramtable').style.display = '';

    var paramNameInput = document.getElementById('content_key_paramname');
    paramNameInput.value = 'key';

    var paramValueInput = document.getElementById('content_key_paramval');
    paramValueInput.value = key;

	window.close();
}

function menuItem_addParamRow()
{
    addTableRow('parambody', 0, true);
}

function callback_inuseby(fieldname, fieldrow, key, title, current)
{
	/*
	var titleField = document.getElementsByName("inuseby_title")[fieldrow];
	var currentField = document.getElementsByName("inuseby_current")[fieldrow];

	titleField.innerHTML = title;

	if (current) {
		currentField.innerHTML = '<img src="images/icon_check.gif" border="0"/>';
	}
	else {
		currentField.innerHTML = "&nbsp;";
	}
	*/
}
function isInteger(value)
{
	var regExpInt = /^[0-9]+$/;
	return regExpInt.test(value);
}

function displayFeedback(feedback)
{
	var feedbackTable = document.getElementById("feedbackTable");
	var feedbackDiv = document.getElementById("feedbackDiv");

	if (feedbackTable != undefined)
	{

		if (feedback == undefined)
		{
			feedbackTable.style.display = "none";
			return;
		}

		feedbackTable.style.display = ""; // Use empty value since MSIE does not support "table" as value.
		feedbackDiv.innerHTML = feedback;
	}
}

function isInt(iString)
{
    // no leading 0s allowed
    return (("" + parseInt(iString)) == iString);
}

function displayBytes(bytes)
{
    if( !isInt(bytes))
	{
        return bytes;
    }

    var gig = 1073741824;
    var meg = 1048576;
    var kilo = 1024;

    if(bytes >= gig)
	{
        var size = (bytes / gig);
        size = round(size, 3);
        return size + " GB";
    }
    else if(bytes >= meg)
	{
        var size = (bytes / meg);
        size = round(size, 1);
        return size + " MB";
    }
    else if(bytes >= kilo)
	{
        var size = (bytes / kilo);
        size = round(size, 1);
        return size + " KB";
    }
    else
	{
        return bytes + " bytes";
    }
}

function round(x, decimals)
{
    var f = Math.pow(10, decimals);
    var result = Math.round(x*f)/f;
    result = limitDecimalPlaces(result, decimals);
    return result;
}

function limitDecimalPlaces(number, placesToLimitTo)
{
    number = number.toString();
    var decimalIndex = number.search(/\./);

    //if there was a decimal point, then go from the start of the string
    //to one more after the decimal index (because we want the decimal) plus
    //the placesToLimitTo that was passed in
    var result;
    if (decimalIndex >= -1) {
        result = number.substring(0, decimalIndex + 1+ placesToLimitTo);
    }
    else{
        result = number;
    }

    return result;
}

function setFormFieldFocus(id) {
  var searchField = document.getElementById(id) || null;
  if (searchField) {
    searchField.focus();
  }
}

function getPreviousSibling (elem) {
  elem = elem.previousSibling;
  while (elem.tagName == 'undefined') {
    elem = elem.previousSibling;
  };
  return elem;
}


function getNextSibling (elem) {
  elem = elem.nextSibling;
  while (elem.tagName == 'undefined') {
    elem = elem.nextSibling;
  };
  return elem;
}

function findPosX(obj) {
  var curleft = 0;
  if (obj.offsetParent) {
    while (1) {
      curleft+=obj.offsetLeft;
      if (!obj.offsetParent) {
        break;
      }
      obj=obj.offsetParent;
    }
  } else if (obj.x) {
    curleft+=obj.x;
  }
  return curleft;
}

function findPosY(obj) {
  var curtop = 0;
  if (obj.offsetParent) {
    while (1) {
      curtop+=obj.offsetTop;
        if (!obj.offsetParent) {
          break;
        }
        obj=obj.offsetParent;
    }
  } else if (obj.y) {
    curtop+=obj.y;
  }
  return curtop;
}

function changeStatusIconHandler( imgId, state )
{

    var imgElement = document.getElementById(imgId);

    if ( !imgElement )
        return;

    switch ( parseInt(state) )
            {
        case 1:
            imgElement.src = './images/icon_state_approve.gif';
            break;

        case 2:
            imgElement.src = './images/icon_state_approved.gif';
            break;

        case 3:
            imgElement.src = './images/icon_state_archived.gif';
            break;

        case 4:
            imgElement.src = './images/icon_state_pending.gif';
            break;

        case 5:
            imgElement.src = './images/icon_state_published.gif';
            break;

        case 6:
            imgElement.src = './images/icon_state_expired.gif';
            break;

        default:
            imgElement.src = './images/icon_state_draft.gif';
    }
}



function get_html_translation_table(table, quote_style) {
    // http://kevin.vanzonneveld.net
    // +   original by: Philip Peterson
    // +    revised by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +   bugfixed by: noname
    // +   bugfixed by: Alex
    // +   bugfixed by: Marco
    // +   bugfixed by: madipta
    // +   improved by: KELAN
    // +   improved by: Brett Zamir (http://brettz9.blogspot.com)
    // %          note: It has been decided that we're not going to add global
    // %          note: dependencies to php.js. Meaning the constants are not
    // %          note: real constants, but strings instead. integers are also supported if someone
    // %          note: chooses to create the constants themselves.
    // *     example 1: get_html_translation_table('HTML_SPECIALCHARS');
    // *     returns 1: {'"': '&quot;', '&': '&amp;', '<': '&lt;', '>': '&gt;'}

    var entities = {}, histogram = {}, decimal = 0, symbol = '';
    var constMappingTable = {}, constMappingQuoteStyle = {};
    var useTable = {}, useQuoteStyle = {};

    // Translate arguments
    constMappingTable[0]      = 'HTML_SPECIALCHARS';
    constMappingTable[1]      = 'HTML_ENTITIES';
    constMappingQuoteStyle[0] = 'ENT_NOQUOTES';
    constMappingQuoteStyle[2] = 'ENT_COMPAT';
    constMappingQuoteStyle[3] = 'ENT_QUOTES';

    useTable     = !isNaN(table) ? constMappingTable[table] : table ? table.toUpperCase() : 'HTML_SPECIALCHARS';
    useQuoteStyle = !isNaN(quote_style) ? constMappingQuoteStyle[quote_style] : quote_style ? quote_style.toUpperCase() : 'ENT_COMPAT';

    if (useTable !== 'HTML_SPECIALCHARS' && useTable !== 'HTML_ENTITIES') {
        throw Error("Table: "+useTable+' not supported');
        // return false;
    }

    // ascii decimals for better compatibility
    entities['38'] = '&amp;';
    if (useQuoteStyle !== 'ENT_NOQUOTES') {
        entities['34'] = '&quot;';
    }
    if (useQuoteStyle === 'ENT_QUOTES') {
        entities['39'] = '&#039;';
    }
    entities['60'] = '&lt;';
    entities['62'] = '&gt;';

    if (useTable === 'HTML_ENTITIES') {
      entities['160'] = '&nbsp;';
      entities['161'] = '&iexcl;';
      entities['162'] = '&cent;';
      entities['163'] = '&pound;';
      entities['164'] = '&curren;';
      entities['165'] = '&yen;';
      entities['166'] = '&brvbar;';
      entities['167'] = '&sect;';
      entities['168'] = '&uml;';
      entities['169'] = '&copy;';
      entities['170'] = '&ordf;';
      entities['171'] = '&laquo;';
      entities['172'] = '&not;';
      entities['173'] = '&shy;';
      entities['174'] = '&reg;';
      entities['175'] = '&macr;';
      entities['176'] = '&deg;';
      entities['177'] = '&plusmn;';
      entities['178'] = '&sup2;';
      entities['179'] = '&sup3;';
      entities['180'] = '&acute;';
      entities['181'] = '&micro;';
      entities['182'] = '&para;';
      entities['183'] = '&middot;';
      entities['184'] = '&cedil;';
      entities['185'] = '&sup1;';
      entities['186'] = '&ordm;';
      entities['187'] = '&raquo;';
      entities['188'] = '&frac14;';
      entities['189'] = '&frac12;';
      entities['190'] = '&frac34;';
      entities['191'] = '&iquest;';
      entities['192'] = '&Agrave;';
      entities['193'] = '&Aacute;';
      entities['194'] = '&Acirc;';
      entities['195'] = '&Atilde;';
      entities['196'] = '&Auml;';
      entities['197'] = '&Aring;';
      entities['198'] = '&AElig;';
      entities['199'] = '&Ccedil;';
      entities['200'] = '&Egrave;';
      entities['201'] = '&Eacute;';
      entities['202'] = '&Ecirc;';
      entities['203'] = '&Euml;';
      entities['204'] = '&Igrave;';
      entities['205'] = '&Iacute;';
      entities['206'] = '&Icirc;';
      entities['207'] = '&Iuml;';
      entities['208'] = '&ETH;';
      entities['209'] = '&Ntilde;';
      entities['210'] = '&Ograve;';
      entities['211'] = '&Oacute;';
      entities['212'] = '&Ocirc;';
      entities['213'] = '&Otilde;';
      entities['214'] = '&Ouml;';
      entities['215'] = '&times;';
      entities['216'] = '&Oslash;';
      entities['217'] = '&Ugrave;';
      entities['218'] = '&Uacute;';
      entities['219'] = '&Ucirc;';
      entities['220'] = '&Uuml;';
      entities['221'] = '&Yacute;';
      entities['222'] = '&THORN;';
      entities['223'] = '&szlig;';
      entities['224'] = '&agrave;';
      entities['225'] = '&aacute;';
      entities['226'] = '&acirc;';
      entities['227'] = '&atilde;';
      entities['228'] = '&auml;';
      entities['229'] = '&aring;';
      entities['230'] = '&aelig;';
      entities['231'] = '&ccedil;';
      entities['232'] = '&egrave;';
      entities['233'] = '&eacute;';
      entities['234'] = '&ecirc;';
      entities['235'] = '&euml;';
      entities['236'] = '&igrave;';
      entities['237'] = '&iacute;';
      entities['238'] = '&icirc;';
      entities['239'] = '&iuml;';
      entities['240'] = '&eth;';
      entities['241'] = '&ntilde;';
      entities['242'] = '&ograve;';
      entities['243'] = '&oacute;';
      entities['244'] = '&ocirc;';
      entities['245'] = '&otilde;';
      entities['246'] = '&ouml;';
      entities['247'] = '&divide;';
      entities['248'] = '&oslash;';
      entities['249'] = '&ugrave;';
      entities['250'] = '&uacute;';
      entities['251'] = '&ucirc;';
      entities['252'] = '&uuml;';
      entities['253'] = '&yacute;';
      entities['254'] = '&thorn;';
      entities['255'] = '&yuml;';
    }

    // ascii decimals to real symbols
    for (decimal in entities) {
        symbol = String.fromCharCode(decimal);
        histogram[symbol] = entities[decimal];
    }

    return histogram;
}

function htmlentities(string, quote_style) {
    // http://kevin.vanzonneveld.net
    // +   original by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +    revised by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +   improved by: nobbler
    // +    tweaked by: Jack
    // +   bugfixed by: Onno Marsman
    // +    revised by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // -    depends on: get_html_translation_table
    // *     example 1: htmlentities('Kevin & van Zonneveld');
    // *     returns 1: 'Kevin &amp; van Zonneveld'
    // *     example 2: htmlentities("foo'bar","ENT_QUOTES");
    // *     returns 2: 'foo&#039;bar'

    var histogram = {}, symbol = '', tmp_str = '', entity = '';
    tmp_str = string.toString();

    if (false === (histogram = this.get_html_translation_table('HTML_ENTITIES', quote_style))) {
        return false;
    }

    for (symbol in histogram) {
        entity = histogram[symbol];
        tmp_str = tmp_str.split(symbol).join(entity);
    }

    return tmp_str;
}


function updateBreadCrumbHeader( headerContainerElementId, titleInputElement, prependForwardSlash )
{
    var headerContainerElement = document.getElementById(headerContainerElementId);

    if ( prependForwardSlash === undefined )
    {
        prependForwardSlash = true;
    }

    var forwardSlash = prependForwardSlash ? '/ ' : '';

    headerContainerElement.innerHTML = titleInputElement.value != '' ? forwardSlash + htmlentities(titleInputElement.value) : '';
}

function getUrlParameter( url, name )
{
    name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    var regexS = "[\\?&]"+name+"=([^&#]*)";
    var regex = new RegExp( regexS );
    var results = regex.exec( url );

    if( results == null )
    {
        return "";
    }
    else
    {
        return results[1];
    }
}

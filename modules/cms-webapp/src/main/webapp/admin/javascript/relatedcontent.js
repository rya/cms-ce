function removeRelatedContent( targetElement, fieldName )
{
    var tBody = findTbody( targetElement, fieldName );

    if ( !tBody ) return;

    var selectedTr = getParent(targetElement, 'tr');
    var counterField = document.getElementsByName(fieldName + "_counter")[tBody[1]];

    if ( selectedTr && counterField )
    {
        tBody[0].removeChild(selectedTr);
        counterField.value = parseInt(counterField.value) - 1;
    }
}

function removeRelatedContentMultipleFalse( fieldRow, fieldName )
{
    var hiddenKeyInput = document.getElementsByName(fieldName)[fieldRow];
    var titlePlaceholder = document.getElementsByName(fieldName + '_title_placeholder')[fieldRow];

    hiddenKeyInput.value = '';
    titlePlaceholder.value = '';
}

function moveRelatedContentUp( targetElement, fieldName  )
{
    var selectedTbody = findTbody( targetElement, fieldName )[0];

    if ( !selectedTbody ) return;

    var selectedTrPos = findTrPosition( targetElement, fieldName );
    var selectedTr = selectedTbody.getElementsByTagName('tr')[selectedTrPos];

    if ( selectedTrPos == 0 )
    {
        selectedTbody.appendChild( selectedTr );
    }
    else
    {
        var siblingTr = selectedTbody.getElementsByTagName('tr')[(selectedTrPos - 1)];
        selectedTbody.insertBefore(selectedTr, siblingTr);
    }
}

function moveRelatedContentDown( targetElement, fieldName )
{

    var selectedTbody = findTbody( targetElement, fieldName )[0];

    if ( !selectedTbody ) return;

    var selectedTrPos = findTrPosition( targetElement, fieldName );
    var selectedTr = selectedTbody.getElementsByTagName('tr')[selectedTrPos];

    if ( selectedTrPos + 1 == selectedTbody.getElementsByTagName('tr').length )  
    {
        var firstTr = selectedTbody.getElementsByTagName('tr')[0];
        selectedTbody.insertBefore(selectedTr, firstTr);
    }
    else
    {
        var siblingTr = selectedTbody.getElementsByTagName('tr')[(selectedTrPos + 1)];
        selectedTbody.insertBefore(siblingTr, selectedTr);
    }
}

function findTrPosition( targetElement, fieldName )
{
    var selectedTbody = findTbody( targetElement, fieldName )[0];

    var selectedTr = getParent(targetElement, 'tr');
    var allTrs = selectedTbody.getElementsByTagName('tr');
    var allTrsLn = allTrs.length;

    var trPosition = 0;
    for ( var j = 0; j < allTrsLn; j++ )
    {
        if ( allTrs[j] == selectedTr )
        {
            trPosition = j;
            break;
        }
    }

    return trPosition;
}

function findTbody( targetElement, fieldName )
{
    var tBody = getParent(targetElement, 'tbody');

    var allTBodies = document.getElementsByName(fieldName + '_table');
    var allTBodiesLn = allTBodies.length;

    var selectedTbody = null;

    for ( var i = 0; i < allTBodiesLn; i++ )
    {
        if ( allTBodies[i] == tBody )
        {
            selectedTbody = allTBodies[i];
            break;
        }
    }

    return [selectedTbody, i];
}


function getParent( startElement, elementName )
{
    var currentElement = startElement;

    // May not need the lower casing on the right hand side. It's just there for stability.
    while ( currentElement.nodeName.toLowerCase() != elementName.toLowerCase() )
    {
        currentElement = currentElement.parentNode;
    }
    return currentElement;
}

function getRelatedContent(fieldName, fieldRow, content_key) 
{
	var objTABLE = document.getElementsByName( fieldName + "_table" )[fieldRow];
	var children = objTABLE.childNodes;
	if (children != null || children.length !=0) {
		for (var i=0; i<children.length; i++) {
			if (children[i].nodeType < 3) {
				var thisKey = children[i].id.substring(15);
				if(thisKey == content_key ) {
	                return children[i];
	            }
			}
		}
	}
	return null;
}

function getRelatedFile(fieldName, fieldRow, content_key) {
  var field = document.getElementsByName(fieldName);
  
  if (!field)
    return;

  var fieldLn = field.length;
  for (var i = 0; i < fieldLn; i++) {
    if (field[i].value == content_key) {
      return field[i];
    }
  }
  return null;
}

function addRelatedContentMultipleFalse( fieldName, fieldRow, content_key, content_title )
{
    var keyInputField = document.getElementsByName(fieldName)[fieldRow];
    var titleInputField = document.getElementsByName(fieldName + '_title_placeholder')[fieldRow];

    keyInputField.value = content_key;
    titleInputField.value = content_title;
}

function addRelatedContent(fieldName, fieldRow, content_key, content_title, minOccurrence, maxOccurrence )
{
    var contentIsAlreadyAdded = getRelatedContent(fieldName, fieldRow, content_key) != null;

    if ( contentIsAlreadyAdded )
    {
        return;
	}

    
    if ( maxOccurrence == 1 )
    {
        addRelatedContentMultipleFalse(fieldName, fieldRow, content_key, content_title);
        return;
    }

    // Create a new row in the table.

    var tBodyElement = document.getElementsByName( fieldName + "_table" )[fieldRow];

    var counterField = document.getElementsByName( fieldName + "_counter" )[fieldRow];
    counterField.value = parseInt(counterField.value) + 1;
	
	var tableRow = document.createElement("tr");
	tableRow.setAttribute('id','relatedcontent_' + content_key);

	var titleCell = document.createElement('td');
    titleCell.className = 'related-content-title';

    var contentKeyInput = createNamedElement(document, 'input', fieldName);
    contentKeyInput.type = 'hidden';
    contentKeyInput.value = content_key;

    titleCell.appendChild(contentKeyInput);
    titleCell.appendChild(document.createTextNode(content_title));

    var operationButtonsCell = document.createElement('td');

    if ( maxOccurrence != 1 )
    {
        var objMoveUpButton = createIconButton( "button", "moveRelatedContentUpmoveRelatedContentUp[key="+content_key+"]", "images/icon_move_up.gif", "javaScript: moveRelatedContentUp(this, '"+fieldName+"');" );
        operationButtonsCell.appendChild(objMoveUpButton);

        var objMoveDownButton = createIconButton( "button", "moveRelatedContentDown[key="+content_key+"]", "images/icon_move_down.gif", "javaScript: moveRelatedContentDown(this, '"+fieldName+"');" );
        operationButtonsCell.appendChild(objMoveDownButton);
    }

    var objRemoveButton = createIconButton( "button", "removeRelatedContent[key="+content_key+"]", "images/icon_remove.gif", "javaScript: removeRelatedContent( this, '"+fieldName+"');" );
    operationButtonsCell.appendChild(objRemoveButton);

	tableRow.appendChild(titleCell);
	tableRow.appendChild(operationButtonsCell);
	tBodyElement.appendChild(tableRow);
}

function relatedImagesDisplay(fieldName) {
    var tBody = document.getElementsByName(fieldName)[0];
	var tr = tBody.getElementsByTagName('tr');
	var trLn = tr.length;
	for (var i=0; i<trLn; i++)
	{
		if (i !=0)
		{
			tr[i].style.display = '';
		}	
	}
	
}
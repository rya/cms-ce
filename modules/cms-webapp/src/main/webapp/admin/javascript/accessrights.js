// Creates a hashtable for holding which rights that is available
var rightsAvailable = {}

// Create a hashtable to hold choosen groups or users
var choosen = {};

function showUserAndGroupsPopup(usersOnly, cbFunction, allowAuthenticatedGroups, opener)
{
    if ( !cbFunction )
    {
        cbFunction = "callback_selectednew";
    }
	var url = "adminpage?page=700&op=browse&callback=opener." + cbFunction + "&mode=users&userstoreselector=true&allow-all-to-be-added=true";

	url = url + "&modeselector="+!usersOnly;

	if ( allowAuthenticatedGroups )
	{
		url = url + "&allowauthenticated=true";
	}

    if ( opener !== undefined )
    {
        url = url + '&opener=' + opener
    }

    var userAndGroupsPopupWindow = showPopupWindow(url, '', 900, 600);
    userAndGroupsPopupWindow.focus();
}

function callback_selectednew(key, type, name, userstorename, qualifiedName, photoExists)
{
    if ( !isChoosen(key) )
    {
        addAccessRightRow(document.getElementById('accessRightTable'), key, type, name, userstorename);
        addChoosen(key);
    }

    /*
    if (isChoosen(key)) {
    	var typeStr = 'group';
    	if (type == '6')
    		typeStr = 'user';
        userAndGroupsPopupWindow.alert("You have already added this " + typeStr);
    }
    else {
        addAccessRightRow(document.getElementById('accessRightTable'), key, type, name, userstorename);
        addChoosen(key);
    }
    */
}

function addChoosen( key )
{
    choosen[key] = "true";
}

function removeChoosen( key )
{
    choosen[key] = null;
}

function isChoosen( key )
{
    return (choosen[key] == "true");
}

function setRightAvailable( right, available )
{
    rightsAvailable[right] = available;
}

function isRightAvailable( right )
{
    return rightsAvailable[right];
}


function addAccessRightRow( objTBODY, key, type, name, userstorename )
{
	var objTR = document.createElement('tr');
    var objTDIcon = document.createElement('td');

	if( "6" == type )
    {
    	// user
		var icon = document.createElement('img');
		icon.setAttribute('src','images/icon_usersgroups.gif');
		icon.setAttribute('style','vertical-align: middle;')
	}
	else
    {
		// group
		var icon = document.createElement('img');
		icon.setAttribute('src','images/icon_groups.gif');
		icon.setAttribute('style','vertical-align: middle;')
	}

	objTDIcon.setAttribute("nowrap", "nowrap");
	objTDIcon.appendChild(icon);

    // Adding hidden field for storing the accessright parameter value
	var hidden = document.createElement('input');
	hidden.setAttribute("type","hidden");
	hidden.setAttribute("id","accessright[key="+key+"]");
	hidden.setAttribute("name","accessright[key="+key+"]");
	hidden.setAttribute("value","[adminread=false;read=true;update=false;delete=false;create=false;publish=false;add=false;administrate=false;name="+name+";grouptype="+type+"]");
    objTDIcon.appendChild(hidden);

	objTR.appendChild(objTDIcon);

	var objTDName = document.createElement("td");
	// objTDName.setAttribute("width","260")
  	var label = document.createTextNode(" " + name);
	objTDName.appendChild(label);

	var objGROUPNAME = document.createElement("input");
	objGROUPNAME.setAttribute("type","hidden");
	objGROUPNAME.setAttribute("id","groupname_"+key+"");
	objGROUPNAME.setAttribute("name","groupname_"+key+"");
	objGROUPNAME.setAttribute("value",""+name+"");
    objTDName.appendChild( objGROUPNAME );

	var objGROUPTYPE = document.createElement("input");
	objGROUPTYPE.setAttribute("type","hidden");
	objGROUPTYPE.setAttribute("id","grouptype_"+key+"");
	objGROUPTYPE.setAttribute("name","grouptype_"+key+"");
	objGROUPTYPE.setAttribute("value",""+type+"");
    objTDName.appendChild( objGROUPTYPE );

	objTR.appendChild(objTDName);

	if( isRightAvailable( "read" ) )
	{
        addAccessRightCell( objTR, "read", key, true, g_readDisabled );
	}
    if( isRightAvailable( "adminread" ) )
	{
        addAccessRightCell( objTR, "adminread", key, false, true );
	}
    if( isRightAvailable( "add" ) ) {
        addAccessRightCell( objTR, "add", key, false, true );
	}
    if( isRightAvailable( "create" ) ) {
        addAccessRightCell( objTR, "create", key, false, true );
	}
    if( isRightAvailable( "publish" ) )
	{
        addAccessRightCell( objTR, "publish", key, false, true );
	}
    if( isRightAvailable( "update" ) ) {
        addAccessRightCell( objTR, "update", key, false, true );
	}
    if( isRightAvailable( "delete" ) )
	{
        addAccessRightCell( objTR, "delete", key, false, true );
	}
    if( isRightAvailable( "approve" ) )
	{
        addAccessRightCell( objTR, "approve", key, false, true );
	}
    if( isRightAvailable( "administrate" ) )
	{
        addAccessRightCell( objTR, "administrate", key, false, true );
	}

    // Adding remove button
    var objTDRemove = document.createElement('td');
	objTDRemove.setAttribute("width", "20");
	var objRemoveButton = createIconButton( "button", "removeAccessRight[key="+ key +"]", "images/icon_remove.gif", "handle_AccessRightRemove_onclick( this );" );
    objRemoveButton.id = "removeAccessRight[key="+key+"]";
	objTDRemove.appendChild( objRemoveButton );
	objTR.appendChild(objTDRemove);

	/*
    // Adding remove button
    var objTDRemove = objTR.insertCell();
    objTDRemove.setAttribute("width", "20");
    objTDRemove.align = "center";

    var objRemoveButton = createIconButton( "button", "removeAccessRight[key="+key+"]", "images/icon_remove.gif", "javaScript: handle_AccessRightRemove_onclick( this );" );
    objRemoveButton.id = "removeAccessRight[key="+key+"]";

    objTDRemove.appendChild( objRemoveButton );
    */

	objTBODY.appendChild(objTR);
}

function addAccessRightCell( objTR, right, key, checked, enabled  )
{
    var objTD = document.createElement("td");
    //var objTD = objTR.insertCell();
    objTD.setAttribute("align","center");


    var objCHK = document.createElement("input");
	objCHK.setAttribute("type","checkbox");
	objCHK.name = "chkAccessRight[key="+key+";right="+right+"]";
	objCHK.onclick = function() { eval("handle_AccessRightCheckbox_onclick(this)") }

	if( enabled )
	{
        objCHK.disabled = false;
	}
    else
	{
        objCHK.disabled = true;
	}

    objTD.appendChild( objCHK );
    objTR.appendChild( objTD );

	if (checked)
	{
		objCHK.setAttribute('checked','checked');
	}


}

function updateAccessRights( key )
{

    var rights = "[";

    var inputs = document.getElementsByTagName( "input" );
    for( var i = 0; i < inputs.length; i++ )
    {
        var inputType = inputs[i].type;
        if( "checkbox" == inputType.toLowerCase() )
        {
            var inputName = inputs[i].name;
            if( inputName.indexOf("chkAccessRight") == 0 )
            {
                var curKey = getParameterInTextDefault( inputName, "key" );
                var curRight = getParameterInTextDefault( inputName, "right" );
                if( key == curKey  )
                {

                    var curValue = "false";
                    if( inputs[i].checked )
                        curValue = "true";

                    rights += curRight + "=" + curValue + ";";
                }
            }
        }
    }

    var groupname = document.getElementById( "groupname_" + key ).value;
    var grouptype = document.getElementById( "grouptype_" + key ).value;
    rights += "name="+groupname+";grouptype="+grouptype;

    rights += "]";

    var objHIDDEN = document.getElementById( "accessright[key="+key+"]" );
    objHIDDEN.value = rights;

}


function checkCheckbox( key, right, checked )
{
    checkOrEnableCheckbox( key, right, checked, null );
}

function enableCheckbox( key, right, enabled )
{
    checkOrEnableCheckbox( key, right, null, enabled );
}

function checkOrEnableCheckbox( key, right, checked, enabled )
{
    var inputs = document.getElementsByTagName( "input" );
    for( var i = 0; i < inputs.length; i++ )
    {
        var inputType = inputs[i].type;
        if( "checkbox" == inputType.toLowerCase() )
        {
            var inputName = inputs[i].name;
            if( inputName.indexOf("chkAccessRight") == 0 )
            {
                var curKey = getParameterInTextDefault( inputName, "key" );
                var curRight = getParameterInTextDefault( inputName, "right" );
                if( key == curKey && right == curRight  )
                {
                    if( checked != null )
                        inputs[i].checked = checked;
                    if( enabled != null )
                        inputs[i].disabled = ( enabled == false );
                }
            }
        }
    }
}


function handle_AccessRightRemove_onclick( obj )
{
    var key = getParameterInTextDefault( obj.name, "key" );
    var parentTR = getParentNodeOfType( obj, 'TR' );
    var rowPosition = getChildPosition( parentTR );
    removeAccessRight( rowPosition, key );
}

function removeAccessRight( position, key )
{
    var objTABLE = document.getElementById( "accessRightTable");
    var objOriginalAccessRight = document.getElementById("original_accessright[key="+key+"]");
    if(objOriginalAccessRight != null  )
        objTABLE.appendChild(objOriginalAccessRight);

    objTABLE.deleteRow( position-1 );
    removeChoosen( key );

}

function handle_AccessRightCheckbox_onclick( chk )
{
    var key = getParameterInTextDefault( chk.name, "key" );
    var right = getParameterInTextDefault( chk.name, "right" );

    if( "create" == right )
    {
        if( chk.checked )
        {
        	/*
            checkCheckbox( key, "adminread", true);
            checkCheckbox( key, "update", true);
            checkCheckbox( key, "delete", true);
            */
        }
    }
    else if( "publish" == right )
    {
        if( chk.checked )
        {
        	/*
            checkCheckbox( key, "adminread", true);
            checkCheckbox( key, "update", true);
            checkCheckbox( key, "delete", true);
            checkCheckbox( key, "create", true);
            */
        }
    }

    if( "administrate" == right )
    {
        if( chk.checked )
        {
            checkCheckbox( key, "adminread", true);
            checkCheckbox( key, "update", true);
            checkCheckbox( key, "delete", true);
            checkCheckbox( key, "create", true);
            checkCheckbox( key, "publish", true);
            checkCheckbox( key, "add", true);
            checkCheckbox( key, "approve", true);
        }

        enableCheckbox( key, 'adminread', (chk.checked != true));
        enableCheckbox( key, 'update', (chk.checked != true));
        enableCheckbox( key, 'delete', (chk.checked != true));
        enableCheckbox( key, 'create', (chk.checked != true));
        enableCheckbox( key, 'publish', (chk.checked != true));
        enableCheckbox( key, 'add', (chk.checked != true));
        enableCheckbox( key, 'approve', (chk.checked != true));
    }

    updateAccessRights( key );

}

function isAccessRightsChanged() {

    var isChanged = false;

    var inputs = document.getElementsByTagName( "input" );
    for( var i = 0; i < inputs.length; i++ )
    {
        var inputType = inputs[i].type;
        if( "hidden" == inputType.toLowerCase() )
        {
            var inputName = inputs[i].name;
            if( inputName.indexOf("accessright") == 0 )
            {
                var original = document.getElementById( "original_" + inputName );

                // Ny
                if( original == null ) {
                    isChanged = true;
                    break;
                }
                // Eksisterende endret
                if( inputs[i].value != original.value ) {
                    isChanged = true;
                    break;
                }
            }
            else if( inputName.indexOf("original_accessright") == 0 )
            {
                var currentName = inputName.substr( 9, inputName.length );
                var current = document.getElementById( currentName );

                // Fjernet
                if( current == null ) {
                    isChanged = true;
                    break;
                }

            }
        }
    }

    return isChanged;
}

// Create a hashtable to hold choosen groups or users
var choosen = {};

userAndGroupsPopupWindow = null;

function showUserAndGroupsPopup( userStoreKey, mode, modeSelector, excludeKey, userStoreSelector, allowAllToBeAdded, allowAuthenticated )
{
    var url = "adminpage?page=700&op=browse&callback=opener.callback_selectednew";

    // http://localhoset:8080/admin/adminpage?op=browse&page=700&callback=opener.callback_selectednew&modeselector=true&userstoreselector=true&excludekey=A680BA122FDF148C0D1ADC6E689E9966FCAF4627&userstorekey=0&mode=users&query=

    if (userStoreKey != undefined) {
		url = url + "&userstorekey="+userStoreKey;
        url = url + "&userstorekeyincontext="+userStoreKey;
    }

	if (mode != undefined) {
		url = url + "&mode="+mode;
    }

    if (allowAllToBeAdded) {
        url = url + "&allow-all-to-be-added=true";
    }

    url = url + "&allowauthenticated=" + ( ( allowAuthenticated ) ? 'true' : 'false' );

	if (modeSelector != undefined)
		url = url + "&modeselector="+modeSelector;

	if (excludeKey != undefined)
		url = url + "&excludekey="+excludeKey;

    if (userStoreSelector != undefined)
        url = url + "&userstoreselector=" + userStoreSelector;

    userAndGroupsPopupWindow = showPopupWindow(url, '', 900, 600);
	userAndGroupsPopupWindow.focus();
}

function callback_selectednew( key, type, name, userstorename, qualifiedName, photoExists )
{
    if ( typeof(validateUserGroupAdd) == "function" )
    {
        var message = validateUserGroupAdd(key, type, name, userstorename);
        if ( message )
        {
            userAndGroupsPopupWindow.alert(message);
            return;
        }
    }

    if ( !isChoosen(key) )
    {
        addMemberRow(document.getElementById('memberstable'), key, type, name, userstorename);
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
     addMemberRow(document.getElementById('memberstable'), key, type, name, userstorename);
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


function removeGroup( position, key )
{
    var objTABLE = document.getElementById( "memberstable");
    objTABLE.deleteRow( position-1 );
    removeChoosen( key );
}

function handle_groupRemove_onclick( obj )
{
    var key = getParameterInTextDefault( obj.name, "key" );
    var parentTR = getParentNodeOfType( obj, 'TR' );
    var rowPosition = getChildPosition( parentTR );
    removeGroup( rowPosition, key );
}


function addMemberRow( objTBODY, key, type, name, userstorename )
{
    var objTR = document.createElement('tr');
    var objTDIcon = document.createElement('td');
    var icon = null;

    if( "6" == type )
    { // user
        icon = document.createElement('img');
        icon.setAttribute('src','images/icon_usersgroups.gif');
        icon.setAttribute('style','vertical-align: middle;')
    }
    else
    { // group
        icon = document.createElement('img');
        icon.setAttribute('src','images/icon_groups.gif');
        icon.setAttribute('style','vertical-align: middle;')
    }

    objTDIcon.setAttribute("nowrap", "nowrap");
    objTDIcon.appendChild(icon);

    var label = document.createTextNode(" " + name);
    objTDIcon.appendChild(label);

    // Adding hidden field for storing the accessright parameter value
    var memberKeyInput = /* admin.js */ createNamedElement(document, 'input', 'member');
    memberKeyInput.type = 'hidden';
    memberKeyInput.value = key;

    objTDIcon.appendChild(memberKeyInput);

    var objTDRemove = document.createElement('td');
    objTDRemove.setAttribute("width", "20");
    var tooltip;
    if( type == 2 )
    {
        tooltip="%msgClickToRemoveGroup%";
    }
    else if( type == 6 )
    {
        tooltip="%msgClickToRemoveUser%";
    }
    else
    {
        tooltip = "%btnRemove%";
    }
    var objRemoveButton = createIconButton( "button", "dssdaf[key="+ key +"]", "images/icon_remove.gif", "handle_groupRemove_onclick( this );" );
    objRemoveButton.setAttribute("title", tooltip);
    objTDRemove.appendChild( objRemoveButton );

    objTR.appendChild(objTDIcon);
    objTR.appendChild(objTDRemove);
    objTBODY.appendChild(objTR);
}
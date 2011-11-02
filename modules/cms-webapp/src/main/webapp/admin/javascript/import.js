/*****

Name: import.js
Author: Enonic as
Version: 1.0

Client side script for section wizard functions.

*****/

// variables use by menu.js
var cookiename = "importwizard";

// step1: 
function checkBoxClicked(thisCheckBox)
{
    var thisId = thisCheckBox.id;
    var childPrefix = thisId + "_";
    var thisChecked = thisCheckBox.checked;

    // click children where checked status is not equal
    var i = 1;
    var checkBox = document.getElementById(childPrefix + i);
    while (checkBox != null) {
        if (thisChecked != checkBox.checked) {
            checkBox.checked = thisChecked;
            checkBoxClicked(checkBox);
        }
        i++;
        checkBox = document.getElementById(childPrefix + i);
    }

    // check/uncheck parents where checked status is not equal
    var parentId = thisId.substring(0, thisId.lastIndexOf("_"));
    while (parentId != "entry") {
        checkBox = document.getElementById(parentId);
        if (thisChecked == false && thisChecked != checkBox.checked)
            checkBox.checked = thisChecked;
        else
            break;
        parentId = parentId.substring(0, parentId.lastIndexOf("_"));
    }
}

var publishFromName = '_publishfrom';
var publishToName = '_publishto';

function setPublishFromEnabled(tfEnabled) {
    var row = document.getElementById(publishFromName + "-row");
    if (tfEnabled)
        row.style.display = '';
    else
        row.style.display = 'none';
}

function setPublishToEnabled(tfEnabled) 
{
    var row = document.getElementById(publishToName + "-row");
    if (tfEnabled)
        row.style.display = '';
    else
        row.style.display = 'none';
}

function statusChanged(select)
{
    var selectedIndex = select.selectedIndex;
    var tfFromDate = document.getElementsByName("date" + publishFromName)[0];
    var tfFromTime = document.getElementsByName("time" + publishFromName)[0];
    var tfToDate = document.getElementsByName("date" + publishToName)[0];
    var tfToTime = document.getElementsByName("time" + publishToName)[0];
    var draftIsSelected = selectedIndex == 1;

    // disable/enable publish from and to fields
    setPublishFromEnabled( !draftIsSelected );
    setPublishToEnabled( !draftIsSelected );

    if ( draftIsSelected )
	{
        showAssignmentFieldset();
    }
    else 
	{

        hideAssignmentFieldset();
        clearAssignmentFields();
    }
}

function validateForm() 
{
    //alert("foo");
    //waitsplash();
    return true;
}

/*****

Name: resource.js
Author: Enonic as
Version: 1.0

Client side script for resource wizard functions.

*****/

var validatedFields = new Array(2);
validatedFields[0] = new Array("%fldName%", "stepstate_resource_name", validateRequired);
validatedFields[1] = new Array("%fldData%", "stepstate_resource_data_CDATA", validateRequired);

var alertUpdateXSL_CO = false;
var alertUpdateXSL_FW = false;
var readOnly = false;


function checkCloseWizard()
{
    if ( readOnly == false && confirm('%msgConfirmSaveResource%') ) {
			var saveButton = document.getElementById("save");

			if (document.all) { // IE
				saveButton.click();
			} else { // Others
				buttonClick(saveButton);
			}
			return false;
    }
    return true;
}

function checkSaveResource()
{
    if (checkAll('formAdmin', validatedFields)) {
        if (alertUpdateXSL_CO)
            return confirm('%alertUpdateStyleSheetPO%');
        else if (alertUpdateXSL_FW)
            return confirm('%alertUpdateStyleSheetPAT%');
        else
            return true;
    }
    return false;
}

function focusName() {
    var name = document.getElementById("stepstate_resource_name");
    name.focus();
}
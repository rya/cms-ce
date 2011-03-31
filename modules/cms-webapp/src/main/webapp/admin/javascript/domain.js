/*****

Name: section.js
Author: Enonic as
Version: 1.0

Client side script for domain wizard functions.

*****/

var validatedFields = new Array(4);
validatedFields[0] = new Array("%fldName%", "name", validateRequired);
validatedFields[1] = new Array("%fldName%", "name", validateAZ09_dot_dash_underscore);
validatedFields[2] = new Array("%fldTopDN%", "topdn", validateRequired);
validatedFields[3] = new Array("%fldLDAPServer%", "ldapserver", validateDropdown);
validatedFields[4] = new Array("%fldObjectClass%", "selectedoc", validateDropdown);

function moveOptions(src, dest)
{
    var s = document.getElementById(src);
    var d = document.getElementById(dest);
                                    
    // add options to dest and delete them from src
    for (i = 0; i < s.options.length; i++) {
        if (s.options[i].selected) {
            d.options[d.options.length] = new Option(s.options[i].text, s.options[i].value);
            s.options[i--] = null;
        }
    }
}

function confirmDefaultUserstore() {
	var checkbox = document.getElementById("defaultuserstore");
	if (checkbox != undefined && checkbox.checked) {
		if (!confirm('%alertChangeDefaultUserStore%')) {
			return false;		
		}
	}
	
	return checkAll('formAdmin', validatedFields);
}
/*****

Name: section.js
Author: Enonic as
Version: 1.0

Client side script for section wizard functions.

*****/

// variables use by menu.js
var cookiename = "sectionwizard";

// global variables
var orderedSectionsCount = 0;
var totalSectionsCount = 0;


// step0: if no sections are select, alert the user and abort next function
function validateSelectedSectionsCount() {
    if (totalSectionsCount < 1) {
        alert('%alertSelectedSectionsZero%');
        return false;
    }
    return true;
}

// step1: variable and functions used to count ordered sections and display 
//        an alert if there are more than one ordered section
function alertOrderedSections() {
    if (orderedSectionsCount > 0) {
	alert("%alertOneOrMoreOrderedSectionsApproved%");
    }
}

function sectionClicked(checkbox, ordered) {
    var checked = checkbox.getAttribute("checked");
    if (checked) {
        totalSectionsCount = totalSectionsCount + 1;
	if (ordered)
            orderedSectionsCount = orderedSectionsCount + 1;
    }
    else {
        totalSectionsCount = totalSectionsCount - 1;
	if (ordered)
	    orderedSectionsCount = orderedSectionsCount - 1;
    }
}


// step2: function for moving content up and down
//        records the row index and direction (buttonName)
function moveContent(index, buttonName) {
    var contentidx = document.getElementsByName("contentidx")[0];
    contentidx.value = index;

    submitForm(buttonName);
}
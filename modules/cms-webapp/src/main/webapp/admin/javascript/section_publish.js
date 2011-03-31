/*****

Name: section_publish.js
Author: Enonic as
Version: 1.0
Last modified: 2007-07-25

Client side script for section publish wizard functions.

*****/

// variables use by menu.js
var cookiename = "sectionPublishWizard";

// global variables
var orderedSectionsCount = 0;
var totalSectionsCount = 0;

// step0
var countMenuSelected = 0;

function chooseTaskChanged(dropdown, originalStatus, reloadForm)
{
	var selectedIndex = dropdown.selectedIndex;
	var value = dropdown.options[selectedIndex].value;

	var publishing = document.getElementById("publishing");
	var message = document.getElementById("message");
	var sites = document.getElementById("sites");

	if (value != 2) {
		setVisible(publishing, false);

		if (value == 0) {
			if (originalStatus == 1) {
			    // reject content
				if (reloadForm) {
					setNextButtonEnabledStep0(false);
					submitForm('loadOwner');
				}
				else {
					setVisible(message, true);
					setVisible(sites, false);
				}
			}
			else {
		    	// keep as draft
				setVisible(message, false);
				setVisible(sites, false);
			}
		}
		else if (value == 1) {
			if (originalStatus == 1) {
				// keep as waiting for approval
				setVisible(message, false);
				setVisible(sites, true);
			}
			else {
			    // send to approval
				if (reloadForm) {
					setNextButtonEnabledStep0(false);
					submitForm('loadRecipients');
				}
				else {
					setVisible(message, true);
					setVisible(sites, true);
				}
			}
		}
		else {
		    // archive content (value == 3)
			setVisible(message, false);
			setVisible(sites, false);
		}

		setNextButtonNameStep0();
	}
	else {
	    // approve content or keep approval
		setVisible(publishing, true);
		setVisible(message, false);
		setVisible(sites, true);

		setNextButtonNameStep0();
	}
}

function setVisible(element, visible)
{
	if (element != null)
	{
		if (visible)
		{
			//element.style.removeAttribute("display");
			element.style.display = "block";
		}
		else
		{
			element.style.display = "none";
		}
	}
}

function checkBoxSiteChanged(checkBox)
{
    if (checkBox.checked)
		countMenuSelected++;
	else
		countMenuSelected--;

	setNextButtonNameStep0();
}

function setNextButtonNameStep0()
{
    var buttons = document.getElementsByName("next");
    var i;

    for ( i = 0; i < buttons.length; i++ )
    {
        if ( countMenuSelected !== 0 )
        {
            wizard_setButtonText(buttons[i], '%cmdNext%')
        }
        else
        {
            wizard_setButtonText(buttons[i], '%cmdFinish%')
        }
    }
}

function wizard_setButtonText(buttonElement, text)
{
    if ( buttonElement )
    {
        buttonElement.innerHTML = text;
    }
}



function setNextButtonEnabledStep0(enabled)
{
	var button = document.getElementById("next");
	button.disabled = enabled == false;
}

// step1:
function dropdownChanged(dropdown, page, menuKey, contentKey)
{
	var button = document.getElementById("button_" + menuKey);
	var selectedIndex = dropdown.selectedIndex;
	var href = "adminpage?page=" + page + "&op=preview&menukey=" + menuKey + "&contentkey=" + contentKey;
	if (dropdown.options[selectedIndex].value != '')
		href = href + "&pagetemplatekey=" + dropdown.options[selectedIndex].value;
	var homeKey = getHomeKey(menuKey);
	if (homeKey >= 0)
		href = href + "&menuitemkey=" + homeKey;
	button.disabled = false;
	var anchor = button.parentNode;
	anchor.href = href;
	anchor.target = "_top_";
}

function radiobuttonChanged(radiobutton, page, menuKey, contentKey)
{
	if (radiobutton.checked) {
		var dropdown = document.getElementById("contentframework_" + menuKey);
		dropdownChanged(dropdown, page, menuKey, contentKey);
	}
}

function getHomeKey(menuKey)
{
	var radiobuttons = document.getElementsByName("menuitem_home_" + menuKey);
	for (i = 0; i < radiobuttons.length; i++) {
		if (radiobuttons[i].checked)
			return radiobuttons[i].value;
	}
	return -1;
}


function selectCheckBoxChanged(selectCheckBox, publishCheckBoxName, homeRadioButtonName, menuKey)
{

    var selectCheckBoxes = document.getElementsByName(selectCheckBox.name);
	// finner indexen til den endrede selectCheckBoxen (blant de med samme navn - ergo i samme meny)
    var index;
	for (var i = 0; i < selectCheckBoxes.length; i++) {
		if (selectCheckBox == selectCheckBoxes[i]) {
			index = i;
	    }
	}


	var manuallyOrderCheckBox = document.getElementsByName(publishCheckBoxName);
    var homeRadioButtons = document.getElementsByName(homeRadioButtonName);
    var radioButton = homeRadioButtons[index];

	if (selectCheckBox.checked)
	{
        manuallyOrderCheckBox[index].disabled = false;
		radioButton.disabled = false;
	}
	else
	{
        manuallyOrderCheckBox[index].disabled = true;
		radioButton.disabled = true;
	}

    var numberOfSecionSelections = countSectionSelectionsInMenu(menuKey);

    if (isFirstSectionSelection(selectCheckBox, numberOfSecionSelections)) {
        radioButton.checked = true;
    }
    else if (isNoSectionSelection(numberOfSecionSelections)) {
        checkRadioButtonsOrCheckBoxes(homeRadioButtons, false);
    }
}

function isFirstSectionSelection(checkBox, numberOfSecionSelections) {
    return checkBox.checked && numberOfSecionSelections == 1;
}

function isNoSectionSelection(numberOfSecionSelections) {
    return numberOfSecionSelections == 0;
}

function countSectionSelectionsInMenu(menuKey) {
    var elementsName = "menuitem_select_"+menuKey;
    var regExp = new RegExp(elementsName);
    var elements = document.getElementsByName(elementsName);

    var count = 0;
    var i;
    for (i = 0; i < elements.length; i++) {
        if(elements[i].checked) {
            count++;
        }
    }
    return count;
}



// step2: function for moving content up and down
//        records the row index and direction (buttonName)
function moveContent(index, buttonName) {
    var contentidx = document.getElementsByName("contentidx")[0];
    if (!contentidx) return;

    contentidx.value = index;
    submitForm(buttonName);
}
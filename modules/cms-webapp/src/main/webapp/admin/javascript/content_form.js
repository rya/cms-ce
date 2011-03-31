var ContentFormUtil = {
    getElementsByClassName: function( searchClass, startNode, tag )
    {
        if ( startNode == null )
            startNode = document;

        // Native function.
        if ( document.getElementsByClassName && tag == null )
            return startNode.getElementsByClassName(searchClass);

        var classElements = new Array();
        if ( tag == null )
            tag = '*';
        var els = startNode.getElementsByTagName(tag);
        var elsLen = els.length;
        var pattern = new RegExp("(^|\\s)" + searchClass + "(\\s|$)");
        for ( var i = 0,j = 0; i < elsLen; i++ )
        {
            if ( pattern.test(els[i].className) )
            {
                classElements[j] = els[i];
                j++;
            }
        }

        return classElements;
    },

    getRandom: function()
    {
        return Math.random().toString().split('.')[1];
    }
};

// -----------------------------------------------------------------------------------------------------------------------------------------

function renameRadioButtonsInNewBlock( oNewBlockRootElement )
{
    var oRadioButtonGroups  = ContentFormUtil.getElementsByClassName('radiobutton-group', oNewBlockRootElement, 'div');
    var iRadioButtonGroupLn = oRadioButtonGroups.length;
    var oRadioButtonGroup, oRadioButtons, iRadioButtonLn, oRadioButton;

    for ( var i = 0; i < iRadioButtonGroupLn; i++ )
    {
        oRadioButtonGroup = oRadioButtonGroups[i];
        oRadioButtons = getRadioButtonElements(oRadioButtonGroup);
        iRadioButtonLn = oRadioButtons.length;

        if ( iRadioButtonLn > 0 )
        {
            var sOriginalRadioButtonName = oRadioButtons[0].name.split(':')[2];

            var sNewRadioButtonName = 'rb:' + ContentFormUtil.getRandom() + ':' + sOriginalRadioButtonName;

            for ( var j = 0; j < iRadioButtonLn; j ++ )
            {
                oRadioButton = oRadioButtons[j];

                var bDefaultCheckedInContentType = oRadioButton.defaultChecked;

                // Fuck IE!!
                // IE does not let you set or change the name attribute in most input elements.
                // This hack creates a new button and removes the original.
                if ( document.all ) // IE
                {
                    var oParent = oRadioButton.parentNode;
                    var sTempValue = oRadioButton.value;

                    var sInputElement = '<input type="radio" name="' + sNewRadioButtonName + '" value="' + sTempValue + '"';
                    if ( bDefaultCheckedInContentType )
                    {
                        sInputElement += ' checked="true" />';
                    }
                    else
                    {
                        sInputElement += ' />';
                    }

                    var oNewRadioButton = document.createElement(sInputElement);

                    oParent.insertBefore(oNewRadioButton, oRadioButton);
                    oParent.removeChild(oRadioButton);
                }
                else
                {
                    oRadioButton.name = sNewRadioButtonName;
                    oRadioButton.checked = bDefaultCheckedInContentType;
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function markRadioButtonAsChecked( oBlock )
{
    var oRadioButtons = getRadioButtonElements(oBlock);
    var iRadioButtonsLn = oRadioButtons.length;
    var oRadioButton;

    for ( var i = 0; i < iRadioButtonsLn; i++ )
    {
        oRadioButton = oRadioButtons[i];

        if ( oRadioButton.checked )
            oRadioButton.className = 'checked_true';
        else
            oRadioButton.className = 'checked_false';
    }
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function restoreRadioButtonCheckState( oBlock )
{
    var oRadioButtons = getRadioButtonElements(oBlock);
    var iRadioButtonsLn = oRadioButtons.length;
    var oRadioButton;

    for ( var i = 0; i < iRadioButtonsLn; i++ )
    {
        oRadioButton = oRadioButtons[i];
        oRadioButton.checked = ( oRadioButton.className === 'checked_true' );
    }
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function checkRadioButtonGroupsForAnyChecked()
{
    var oRadioButtonGroups  = ContentFormUtil.getElementsByClassName('radiobutton-group', document.forms['formAdmin'], 'div');
    var iRadioButtonGroupLn = oRadioButtonGroups.length;
    var oRadioButtonGroup, oRadioButtons, iRadioButtonLn, oRadioButton;

    for ( var i = 0; i < iRadioButtonGroupLn; i++ )
    {
        oRadioButtonGroup = oRadioButtonGroups[i];
        oRadioButtons = getRadioButtonElements(oRadioButtonGroup);
        iRadioButtonLn = oRadioButtons.length;
        var bIsAnyRadioButtonsChecked = false;

        for ( var j = 0; j < iRadioButtonLn; j++ )
        {
            oRadioButton = oRadioButtons[j];

            if ( oRadioButton.checked )
            {
                bIsAnyRadioButtonsChecked = true;
                break;
            }
        }

        if (!bIsAnyRadioButtonsChecked)
        {
            var oReplacementForNonCheckedRadioButtons = document.createElement('input');
            oReplacementForNonCheckedRadioButtons.type = 'hidden';
            oReplacementForNonCheckedRadioButtons.name = oRadioButtons[0].name;
            oReplacementForNonCheckedRadioButtons.value = oRadioButtons[0].name;
            oRadioButtonGroup.appendChild(oReplacementForNonCheckedRadioButtons);
        }
    }
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function addIndexFieldForEachRadioButtonGroup()
{
    var oBlocks = ContentFormUtil.getElementsByClassName('block', document.forms['formAdmin'], 'table');
    var iBlocksLn = oBlocks.length;
    var oBlock;

    var oRadioButtonGroups;

    for ( var i = 0; i < iBlocksLn; i++ )
    {
        oBlock = oBlocks[i];

        var oBlockGroups = getBlockGroups(oBlock);

        if ( oBlockGroups.length > 0 )
        {
            var oBlockGroupsLn = oBlockGroups.length;

            for ( var j = 0; j < oBlockGroupsLn; j++ )
            {
                var oBlockGroup = oBlockGroups[j];
                oRadioButtonGroups  = ContentFormUtil.getElementsByClassName('radiobutton-group', oBlockGroup, 'div');
                addIndexFieldToGroup(oRadioButtonGroups, j);
            }

        }
        else
        {
            oRadioButtonGroups  = ContentFormUtil.getElementsByClassName('radiobutton-group', oBlock, 'div');
            addIndexFieldToGroup(oRadioButtonGroups, 0);
        }
    }
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function addIndexFieldToGroup( oRadioButtonGroups, idx )
{
    for ( var i = 0; i < oRadioButtonGroups.length; i ++ )
    {
        var oRadioButtons = getRadioButtonElements(oRadioButtonGroups[i]);
        var sElementIDAndName = oRadioButtons[0].name + ':index';

        if ( !document.getElementById(sElementIDAndName) )
        {
            var oInputIndexField = document.createElement('input');
            oInputIndexField.type = 'hidden';
            oInputIndexField.id = sElementIDAndName;
            oInputIndexField.name = sElementIDAndName;
            oInputIndexField.value = idx;
            oRadioButtonGroups[i].appendChild(oInputIndexField);
        }
    }
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function getRadioButtonElements( oParentElement )
{
    var aRadioButtonElements = [];
    var oInputElements = oParentElement.getElementsByTagName('input');
    var iInputElementsLn = oInputElements.length;
    var oInputElement;
    for ( var i = 0; i < iInputElementsLn; i++ )
    {
        oInputElement = oInputElements[i];

        if (oInputElement.type === 'radio')
        {
            aRadioButtonElements.push(oInputElement);
        }
    }

    return aRadioButtonElements;
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function removeRadioButtonGroupCounterFromBlock( oNewBlockRootElement )
{
    var oInputElements = oNewBlockRootElement.getElementsByTagName('input');
    var iInputElementsLn = oInputElements.length;
    var bIsIndexField;

    for ( var i = 0; i < iInputElementsLn; i++ )
    {
        bIsIndexField = oInputElements[i].type == 'hidden' && oInputElements[i].name.indexOf(':index') > -1;

        if ( bIsIndexField )
        {
            oInputElements[i].parentNode.removeChild(oInputElements[i]);
            break;
        }
    }
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function getBlockGroups( oBlock )
{
    var aBlockGroups = new Array();
    var oTbody = oBlock.getElementsByTagName('tbody');
    var iTobyLn = oTbody.length;

    for (var i = 0; i < iTobyLn; i++)
    {
        if ( oTbody[i].className.indexOf('_tbody') > -1 )
        {
            aBlockGroups.push(oTbody[i]);
        }
    }

    return aBlockGroups;
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function content_name_lockClickCallback( defaultPlaceHolderText )
{
    var nameInputElem = document.getElementById('_name');

    if ( !nameInputElem.readOnly )
    {
        if ( nameInputElem.value === '' )
        {
            nameInputElem.disabled = true;
            nameInputElem.value = defaultPlaceHolderText;
            nameInputElem.className += ' grey-text';
        }
    }
    else
    {
        nameInputElem.disabled = false;
        nameInputElem.className = nameInputElem.className.replace(/ grey-text/g, '');

        if ( nameInputElem.value === defaultPlaceHolderText )
        {
            nameInputElem.value = '';
        }
    }
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function content_form_displayNewAssignee()
{
    document.getElementById('assignee-status-arrow').style.display = '';
    document.getElementById('assignee-status-new-assignee').style.display = '';

    document.getElementById('assignee-status-arrow-2').style.display = 'none';
    document.getElementById('assignee-status-unassigned-2').style.display = 'none';
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function content_form_displayAssignedToNobody()
{
    document.getElementById('assignee-status-current-assignee').style.display = '';
    document.getElementById('assignee-status-arrow').style.display = 'none';
    document.getElementById('assignee-status-new-assignee').style.display = 'none';
    document.getElementById('assignee-status-unassigned').style.display = 'none';

    document.getElementById('assignee-status-arrow-2').style.display = '';
    document.getElementById('assignee-status-unassigned-2').style.display = '';
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function content_form_enableDisableDueDateDescription(disable)
{
    var dueDateDateInput = document.getElementById('date_assignment_duedate');
    var dueDateTimeInput = document.getElementById('time_assignment_duedate');
    var calenderPickerButton = document.getElementById('link_assignment_duedate');
    var assignmentDescriptionInput = document.getElementById('_assignment_description');

    if ( disable )
    {
        dueDateDateInput.value = '';
        dueDateTimeInput.value = '';
        assignmentDescriptionInput.value = '';
    }

    dueDateDateInput.disabled = disable;
    dueDateDateInput.readOnly = disable;

    dueDateTimeInput.disabled = disable;
    dueDateTimeInput.readOnly = disable;

    calenderPickerButton.disabled = disable;

    assignmentDescriptionInput.disabled = disable;
    assignmentDescriptionInput.readOnly = disable;
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function content_form_assignToMe( key, type, nameQualifiedName, userstorename )
{
    var assigneeInput = document.getElementById('_assignee');
    var assigneeViewInput = document.getElementById('view_assignee');
    var assignerInput = document.getElementById('_assigner');
    var assignerViewInput = document.getElementById('view_assigner');
    var unassignButton = document.getElementById('_unassign_button');
    var assignToMeButton = document.getElementById('_assign_to_me_button');

    assigneeInput.value = key;
    assigneeViewInput.innerHTML = nameQualifiedName;

    assignerInput.value = key;
    assignerViewInput.innerHTML = nameQualifiedName;

    assignToMeButton.className = 'button_text_hidden';
    unassignButton.className = 'button_text';

    content_form_enableDisableDueDateDescription( false );

    content_form_displayNewAssignee();
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function content_form_unassign()
{
    var assigneeInput = document.getElementById('_assignee');
    var assigneeViewInput = document.getElementById('view_assignee');
    var assignerInput = document.getElementById('_assigner');
    var assignerViewInput = document.getElementById('view_assigner');
    var unassignButton = document.getElementById('_unassign_button');
    var assignToMeButton = document.getElementById('_assign_to_me_button');

    assigneeInput.value = '';
    assigneeViewInput.innerHTML = '';

    assignerInput.value = '';
    assignerViewInput.innerHTML = '';

    assignToMeButton.className = 'button_text';
    assignToMeButton.disabled = false;
    assignToMeButton.style.cursor = 'pointer';

    unassignButton.className = 'button_text_hidden';

    content_form_enableDisableDueDateDescription( true );

    content_form_displayAssignedToNobody();
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function content_form_focusVersionCommentField( commentField )
{
    var commentFieldIsBlank = commentField.className.indexOf('placeholder-text') > -1;

    if ( commentFieldIsBlank )
    {
        commentField.value = '';
        commentField.className = commentField.className.replace(/placeholder-text/, '');
    }
}

// -----------------------------------------------------------------------------------------------------------------------------------------

function content_form_blurVersionCommentField( commentField )
{
    var commentFieldIsBlank = commentField.value.length === 0;

    if ( commentFieldIsBlank )
    {
        commentField.value = '%versionCommentPlaceholderText%';
        commentField.className += ' placeholder-text';
    }
}
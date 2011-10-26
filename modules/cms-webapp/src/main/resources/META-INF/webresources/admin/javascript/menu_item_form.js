function formbuilder_isReceiptInformationValid()
{
    var oReceiptSendEmail = document.getElementById('receiptSendEmail');

    if ( !oReceiptSendEmail.checked ) return true;

    var oReceiptFromName    = document.getElementById('receiptFromName');
    var oReceiptFromAddress = document.getElementById('receiptFromAddress');
    var oReceiptSubject     = document.getElementById('receiptSubject');
    var oReceiptMessage     = document.getElementById('receiptMessage');

    var regExEmail = /^[a-zA-Z][\w\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\w\.-]*[a-zA-Z0-9]\.[a-zA-Z][a-zA-Z\.]*[a-zA-Z]$/;
    
    if ( oReceiptFromName.value === '' )
    {
        error(oReceiptFromName, '%errFormReceiptFromNameRequired%', null);
        return false;
    }

    if ( !regExEmail.test(oReceiptFromAddress.value) )
    {
        error(oReceiptFromAddress, '%errFormReceiptFromEmailInvalid%', null);
        return false;
    }

    if ( oReceiptSubject.value === '' )
    {
        error(oReceiptSubject, '%errFormReceiptSubjectRequired%', null);
        return false;
    }

    if ( oReceiptMessage.value === '' )
    {
        error(oReceiptMessage, '%errFormReceiptMessageRequired%', null);
        return false;
    }

    return true;
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function formbuilder_labelValueExist( labelFieldValue, currentLabelFieldIndex )
{
    var labelValueExist = false;
    var formBuilderTableElem = document.getElementById('form_fieldtable');
    var formElements = formBuilderTableElem.getElementsByTagName('input');
    var formElementsLn = formElements.length;
    var labelFieldIndexCounter = 0;
    
    var formElement = null;

    for ( var i = 0; i < formElementsLn; i++ )
    {
        formElement = formElements[i];

        if ( formElement.name === 'field_label' )
        {
            if ( formElement.value === labelFieldValue && currentLabelFieldIndex !== labelFieldIndexCounter)
            {
                labelValueExist = true;
                break;
            }

            labelFieldIndexCounter++;
        }

    }
    
    return labelValueExist;
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function formbuilder_validateUniqueLabelValues()
{
    var hasUniqueLabelValues = true;
    var formBuilderTableElem = document.getElementById('form_fieldtable');
    var formElements = formBuilderTableElem.getElementsByTagName('input');
    var formElementsLn = formElements.length;
    var labelFieldIndexCounter = 0;

    var formElement = null;

    for ( var i = 0; i < formElementsLn; i++ )
    {
        formElement = formElements[i];

        if ( formElement && formElement.name === 'field_label' )
        {
            if ( formbuilder_labelValueExist(formElement.value, labelFieldIndexCounter) )
            {
                hasUniqueLabelValues = false;
                break;
            }

            labelFieldIndexCounter++;
        }
    }
    
    return hasUniqueLabelValues;
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function menuitem_name_lockClickCallback( defaultPlaceHolderText )
{
    var nameInputElem = document.getElementById('name');
    
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

function menuitem_checkIfMenuItemNameExistsAndSubmit( nameElem, existingMenuItemKey, parentKey, tabPane )
{
    AjaxService.menuItemNameExistsUnderParent( nameElem.value, existingMenuItemKey, parentKey, {
        callback : function(exists) {
            if ( exists )
            {
                /* validate.js */
                error(nameElem, '%alertMenuItemExists%', tabPane );
            }
            else
            {
                var form = document.forms['formAdmin'];
                var buttons = new Array("save", "cancel", "preview");
                var cancelButtonAnchorElemWrapper = document.getElementById("cancel2");

                /* admin.js */
                disableTextButtons(buttons);

                if ( cancelButtonAnchorElemWrapper != null )
                {
                    cancelButtonAnchorElemWrapper.href = '#';
                }

                form.submit();
            }
        }
    });
}


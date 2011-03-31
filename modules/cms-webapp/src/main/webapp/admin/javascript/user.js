function updateDisplayNameValue( event )
{
    var isKeyEvent = event.type === 'keyup' || event.type === 'keydown';
    var keyCode = event.charCode || event.keyCode || 0;

    if ( isKeyEvent )
    {
        // Skip some of the modifier keys.
        // http://www.cambiaresearch.com/c4/702b8cd1-e5b0-42e6-83ac-25f0306e3e25/Javascript-Char-Codes-Key-Codes.aspx
        if ( keyCode >= 9 && keyCode <= 27 && keyCode !== 224 )
        {
            return;
        }
    }

    var doc = document;
    var fldDisplayName = doc.getElementById('display_name');
    var generateDisplayName = fldDisplayName.readOnly;

    if ( !generateDisplayName )
    {
        return;
    }

    var inputFields = [];

    inputFields['prefix']       = doc.getElementById('prefix');
    inputFields['firstName']    = doc.getElementById('first_name');
    inputFields['middleName']   = doc.getElementById('middle_name');
    inputFields['lastName']     = doc.getElementById('last_name');
    inputFields['suffix']       = doc.getElementById('suffix');

    var displayName = createDisplayName( inputFields );
    if( displayName != null && displayName.length > 0 )
    {
        fldDisplayName.value = displayName;
        return displayName;
    }

    inputFields = [];
    inputFields['nick_name']       = doc.getElementById('nick_name');
    displayName = createDisplayName( inputFields );

    if( displayName != null && displayName.length > 0 )
    {
        fldDisplayName.value = displayName;
        return displayName;
    }

    inputFields = [];
    inputFields['initials']       = doc.getElementById('initials');
    displayName = createDisplayName( inputFields );

    if( displayName != null && displayName.length > 0 )
    {
        fldDisplayName.value = displayName;
        return;
    }

    inputFields = [];
    inputFields['uid'] = doc.getElementById('uid_dummy');
    displayName = createDisplayName( inputFields );
    
    fldDisplayName.value = displayName;
}

function createDisplayName( inputFields )
{
    var valueForDisplayName = [];

    for ( var key in inputFields )
    {
        if ( inputFields[key] && inputFields[key].value != '' )
        {
            valueForDisplayName.push(inputFields[key].value);
        }
    }

    return  valueForDisplayName.join(' ');
}

// -----------------------------------------------------------------------------------------------------------------------------------------

// -----------------------------------------------------------------------------------------------------------------------------------------

function updateRegion( countryElem )
{
    var country = countryElem.value;

    if ( country !== '' )
    {
        AjaxService.getCountryRegions(country, {
            callback: function( regions )
            {
                addRegions(countryElem, regions);
            },

            preHook: function()
            {
                // displayLoader(true, regionElementId);
                removeRegions(countryElem);
            },

            postHook: function()
            {
                setTimeout( function() {
                    // displayLoader(false, regionElementId);
                    // document.getElementById(regionElementId + '-initial-value').value = '';
                }, 100);
            }
        });
    }
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function addRegions(countryElem, regions )
{
    if ( regions && regions.length > 0 )
    {
        var position = getCountryElemPosition(countryElem);
        var regionElem = $('.user-region-combo:eq(' + position +')')[0];
        var selectedRegion = $('.temp-region:eq(' + position +')')[0].value;

        var regionOptionElement, regionOptionTextNode;
        var regionCode, localName, englishName, regionName;

        for ( var key in regions )
        {
            regionCode      = regions[key].code;
            localName       = regions[key].localName;
            englishName     = regions[key].englishName;
            regionName      = englishName !== localName ? englishName + '(' + localName + ')' : englishName;

            regionOptionElement = document.createElement('option');
            regionOptionElement.value = regions[key].code;

            regionOptionTextNode = document.createTextNode(regionName);
            regionOptionElement.appendChild(regionOptionTextNode);

            regionElem.appendChild(regionOptionElement);

            if ( regionCode === selectedRegion )
            {
                regionOptionElement.selected = 'true';
            }
        }
    }
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function removeRegions( countryElem )
{
    var position = getCountryElemPosition(countryElem);
    var regionElem = $('.user-region-combo:eq(' + position +')')[0];

    regionElem.length = 1;
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function getCountryElemPosition( countryElem )
{
    var position = -1;

    $('.user-country-combo').each( function(i) {
        if (this === countryElem)
        {
            position = i;
        }
    });

    return position;
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function displayLoader( show, regionElementId )
{
    document.getElementById(regionElementId + '-loader').style.display = show ? 'inline' : 'none';
}
// -----------------------------------------------------------------------------------------------------------------------------------------

$(function() {
    $("#address-tabs").tabs({
        tabTemplate: '<li dragable="true"><a href="#{href}"><span class="ui-tabs-address-primary-icon primary-icon-hidden"></span><span class="ui-tabs-address-label">#{label}</span><span class="ui-tabs-address-required ui-tabs-address-required-hidden">*</span> <span class="ui-tabs-address-remove-icon"><img src="images/icon_close.gif" alt="" title="%cmdRemoveAddress%" onclick="closeAddressTab(this);" onmouseover="iconCloseMouseOverOut(this, \'hover\')" onmouseout="iconCloseMouseOverOut(this, \'out\')"/></span></a></li>',
        panelTemplate: $('#address-tab-panel-template').html(),

        show: function( event, ui )
        {
            // $('#' + ui.panel.id).find('input:eq(0)').focus();
        },

        add: function( event, ui )
        {
            $("#address-tabs").tabs('select', '#' + ui.panel.id);
            $('#' + ui.panel.id).find('input:eq(0)').focus();
        },

        remove: function( event, ui )
        {
            var selectedIndex = $('#address-tabs ul li').index( $('li.ui-state-active') );
            $('#address-tabs div.ui-tabs-panel:eq(' + selectedIndex + ')').find('input:eq(0)').focus();

            if ( $('#address-tabs li').size() == 1 )
            {
                $('#address-tabs li span.ui-tabs-address-remove-icon img' ).remove();
            }

        }
    }).find(".ui-tabs-nav").sortable({
        axis:'x',
        forceHelperSize: true,
        tolerance: 'pointer',
        items: 'li[dragable=true]',
        update: function(event, ui) {
            updateInfoInPrimaryTab();
        }
    });

    var selectedIndex = $('#address-tabs ul li').index( $('li.ui-state-active') );
    $('#address-tabs ul li:eq(' + selectedIndex + ')').removeClass('ui-state-default');

});
// -----------------------------------------------------------------------------------------------------------------------------------------

function openNewAddresTab()
{
    if ( $("#address-tabs li").size() == 1 )
    {
        $("#address-tabs li:eq(0) span.ui-tabs-address-remove-icon").html('<img src="images/icon_close.gif" alt="" title="%cmdRemoveAddress%" onclick="closeAddressTab(this);"/>');
    }

    var id = '#address-tab-' + new Date().getTime();

    $("#address-tabs").tabs('add', id, '[%txtNoLabel%]');
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function updateAddressTabLabel( inputElement )
{
    var text = inputElement.value;

    if ( inputElement.value === '' )
    {
        text = '[%txtNoLabel%]';
    }

    var selectedIndex = $('#address-tabs ul li').index( $('li.ui-state-active') );
    $('#address-tabs ul li:eq(' + selectedIndex + ') a span.ui-tabs-address-label').text( text );
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function closeAddressTab( target )
{
    if ( confirm('%alertRemoveAddress%') )
    {
        var indexToRemove = $('#address-tabs ul li').index($(target).parents('li'));
        $("#address-tabs").tabs('remove', indexToRemove);

        updateInfoInPrimaryTab();
    }
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function updateInfoInPrimaryTab()
{
    if ( !g_addressIsRequired ) return;

    $("#address-tabs li").each( function( i )
    {
        var requiredStar = $(this).find('span.ui-tabs-address-required');

        if ( i === 0 )
        {
            $(requiredStar).removeClass('ui-tabs-address-required-hidden');
        }
        else
        {
            $(requiredStar).addClass('ui-tabs-address-required-hidden');
        }
    });

    if ( !g_isRemote ) return;

    $("#address-tabs li").each( function( i )
    {
        var primaryIcon = $(this).find('span.ui-tabs-address-primary-icon');
        var requiredStar = $(this).find('span.ui-tabs-address-required');

        if ( i === 0 )
        {
            $(primaryIcon).removeClass('primary-icon-hidden');
            $(primaryIcon).attr('title', '%txtPrimaryAddress%');

            $(requiredStar).removeClass('ui-tabs-address-required-hidden');
        }
        else
        {
            $(primaryIcon).addClass('primary-icon-hidden');
            $(primaryIcon).attr('title', '');

            $(requiredStar).addClass('ui-tabs-address-required-hidden');

        }
    });
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function orderTabPanels()
{
    // Reorder tab panels.
    $('#address-tabs li a').each( function(i)
    {
        var panelId = $(this).attr('href');
        var tempPanel = $(panelId);

        $(panelId).remove();

        $("#address-tabs").append(tempPanel);

        renameAddressFieldsForPanel( panelId, i );
    });

}
// -----------------------------------------------------------------------------------------------------------------------------------------

function renameAddressFieldsForPanel( panelId, idx )
{
    var addressFieldNamePattern = /address\[(\d+|)\]\.\w+/i;

    $(panelId + ' :input').each( function(j)
    {
        var fieldName = $(this).attr('name');

        if ( addressFieldNamePattern.test(fieldName) )
        {
            var addressPropertyName = fieldName.split('.')[1];

            $(this).attr('name', 'address[' + idx + '].' + addressPropertyName);

            // console.log('name: ' + $(this).attr('name') + ', value: ' + $(this).attr('value'));
        }

    });
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function removePhoto( remove )
{
    if ( remove )
    {
        var photoWrapperElem = document.getElementById('photo_wrapper');

        if ( photoWrapperElem )
        {
            photoWrapperElem.innerHTML = '';
            document.getElementById('remove_photo').value = 'true';
        }
    }
    else
    {
        document.getElementById('remove_photo').value = 'false';
    }
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function iconCloseMouseOverOut( imgElement, state )
{
    imgElement.src = state === 'hover' ? 'images/icon_close_hover.gif' : 'images/icon_close.gif';
}
// -----------------------------------------------------------------------------------------------------------------------------------------

function validateAddressTab()
{
    var form = document.forms['formAdmin'];
    var isValid = true;

    var requiredFields = [];
    requiredFields[0] = ['address[0].street', '%errRequired% %fldAddressStreet%'];
    requiredFields[1] = ['address[0].postal_code', '%errRequired% %fldAddressPostalCode%'];
    requiredFields[2] = ['address[0].postal_address', '%errRequired% %fldAddressPostalAddress%'];

    var requiredField, requiredFieldErrorMessage;

    for ( var key in requiredFields )
    {
        requiredField = form[requiredFields[key][0]];
        requiredFieldErrorMessage = requiredFields[key][1];

        if ( requiredField.value === '' )
        {
            alert(requiredFields[key][1]);
            requiredField.focus();
            isValid = false;
            break;
        }
    }

    return isValid;
}
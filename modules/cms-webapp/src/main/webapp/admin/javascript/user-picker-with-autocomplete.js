/**
 * @requires:
    javascript/lib/jquery/ui/autocomplete/css/cms/jquery-ui-1.8.1.custom.css
    javascript/lib/jquery/ui/autocomplete/css/cms/jquery-ui-1.8.1.overrides.css

    dwr/engine.js
    dwr/interface/AjaxService.js
    javascript/tabpane.js
    javascript/accessrights.js
    javascript/lib/jquery/jquery-1.4.2.min.js
    javascript/lib/jquery/ui/autocomplete/js/jquery-ui-1.8.1.custom.min.js

 */

function UserPickerAutoComplete( id, config )
{
    var t = this;

    t.id = id;
    t.instanceName = 'userPickerAutoComplete_' + t.id;
    t.userField = $( 'input#' + t.id );
    t.autoCompleteElement = $( 'input#-ui-' + t.id + '-autocomplete' );
    t.autoCompleteCompareElement = $( 'input#-ui-' + t.id + '-compare-name' );
    t.selectedUserQualifiedNameElement = $( '#-ui-' + t.id + '-selected-user-qname' );
    t.feedbackPopup = $( '#-ui-autocomplete-feedback-' + t.id );

    // Jquery UI Autocomplete configuration
    t.autoCompleteElement.autocomplete({

        source : eval( 'AjaxService_' + config.ajaxServiceFunctionToExecute ),
        delay: 500,
        minLength: 2,

        select : function( event, ui )
        {
            var key = ui.item.key;
            if ( config.useUserGroupKey )
            {
                key = ui.item.userGroupKey;
            }
            
            var qualifiedName = ' (' + ui.item.qualifiedName + ')';
            var name = ui.item.label;
            var photoExists = ui.item.photoExists;

            t.addUserToForm( key, '', name, '', qualifiedName, photoExists );

        }}).data( "autocomplete" )._renderItem = function( ul, item ) {

            var listItemHtml = getAutocompleteItemTemplate( item );

            return $( "<li></li>" )
                    .data( "item.autocomplete", item )
                    .append( listItemHtml )
                    .appendTo( ul );
    };

    // -------------------------------------------------------------------------------------------------------------------------------------

    t.addUserToForm = function( key, type, name, userstoreName, qualifiedName, photoExists )
    {
        t.userField.val( key );
        t.autoCompleteElement.val( name );
        t.autoCompleteElement.removeClass( 'placeholder-text' );
        t.autoCompleteCompareElement.val( name );
        t.selectedUserQualifiedNameElement.html( qualifiedName );

        config.onAddUser( arguments );
    };

    // -------------------------------------------------------------------------------------------------------------------------------------

    t.removeUser = function()
    {
        t.userField.val( '' );
        t.autoCompleteElement.val( getPlaceHolderTextForAutoCompleteField() );
        t.autoCompleteElement.attr( 'class', 'placeholder-text' );
        t.autoCompleteCompareElement.val( '' );
        t.selectedUserQualifiedNameElement.html( '' );

        config.onRemoveUser( arguments );
    };

    // -------------------------------------------------------------------------------------------------------------------------------------

    t.showUserPopup = function()
    {
        var useUserGroupKey = config.useUserGroupKey === true;

        var url = 'adminpage?page=700&op=browse&callback=opener.' + t.instanceName +
                '.addUserToForm&mode=users&userstoreselector=true&modeselector=false';
            url += '&opener=user-picker&use-user-group-key=' + useUserGroupKey;
            url += '&user-picker-key-field=' + t.id;

        var userAndGroupsPopupWindow = showPopupWindow( url, '', 900, 600 );

        userAndGroupsPopupWindow.focus();
    };

    // -------------------------------------------------------------------------------------------------------------------------------------

    t.autoCompleteElement.keyup( function()
    {
        onKeyUp();
    });

    // -------------------------------------------------------------------------------------------------------------------------------------

    t.autoCompleteElement.focus( function()
    {
        onSetFocus();
    });

    // -------------------------------------------------------------------------------------------------------------------------------------

    t.autoCompleteElement.blur( function()
    {
        onBlur();
    });

    // -------------------------------------------------------------------------------------------------------------------------------------

    t.autoCompleteElement.click( function()
    {
        onClick();
    });

    // -------------------------------------------------------------------------------------------------------------------------------------

    function AjaxService_findUsers( request, response )
    {
        var name = request.term;

        AjaxService.findUsers( name, {
            callback : function( foundUsers )
            {
                response( foundUsers );
                showFeedback( foundUsers.length )
            }
        } );
    }

    // -------------------------------------------------------------------------------------------------------------------------------------

    function AjaxService_findUsersAndAccessType( request, response )
    {
        var contentKeyField = $('input#content-key');
        if ( contentKeyField.size() === 0 )
        {
            alert('Error: Missing hidden input with id content-key');
        }

        var name = request.term;

        AjaxService.findUsersAndAccessType( name, contentKeyField.val(), {
            callback : function( foundUsers )
            {
                response( foundUsers );
                showFeedback( foundUsers.length )
            }
        } );
    }

    // ------------f-------------------------------------------------------------------------------------------------------------------------
    
    function showFeedback( noOfUsers )
    {
        if ( noOfUsers === 0 )
        {
            t.feedbackPopup.fadeIn( 'fast', function()
            {
                setTimeout( function()
                {
                    t.feedbackPopup.fadeOut();
                }, 3600 );
            } );
        }
        else
        {
            t.feedbackPopup.fadeOut();
        }
    }

    // -------------------------------------------------------------------------------------------------------------------------------------

    function getPlaceHolderTextForAutoCompleteField()
    {
        return '%txtSearchUsers%';
    }

    // -------------------------------------------------------------------------------------------------------------------------------------

    function onSetFocus()
    {
        if ( t.autoCompleteElement.hasClass( 'placeholder-text' ) )
        {
            t.autoCompleteElement.removeClass('placeholder-text');
            t.autoCompleteElement.val('');
        }
    }

    // -------------------------------------------------------------------------------------------------------------------------------------

    function onKeyUp()
    {
        t.feedbackPopup.hide();

        if ( t.autoCompleteElement.val().length === 0 )
        {
            t.userField.val('');
            t.autoCompleteElement.val('');
            t.autoCompleteCompareElement.val('');
        }
    }

    // -------------------------------------------------------------------------------------------------------------------------------------

    function onBlur()
    {
        if ( t.autoCompleteElement.val() === '' )
        {
            t.autoCompleteElement.val( getPlaceHolderTextForAutoCompleteField() );
            t.autoCompleteElement.attr('class', 'placeholder-text');

            t.removeUser();
        }
    }

    // -------------------------------------------------------------------------------------------------------------------------------------

    function onClick()
    {
        if ( t.autoCompleteElement.val().length > 2 )
        {
            t.autoCompleteElement.autocomplete( 'search', t.autoCompleteElement.val() );
        }
    }

    // -------------------------------------------------------------------------------------------------------------------------------------

    function getAutocompleteItemTemplate( item )
    {
        var currentTypedInName          = t.autoCompleteElement.val();
        var currentTypedInNamePattern   = new RegExp('(' + currentTypedInName + ')', 'gi');

        var displayNameHighlighted      = item.displayName.replace( currentTypedInNamePattern, '<span class="search-highlight">$1</span>' );
        var qualifiedNameHighlighted    = item.qualifiedName.replace( currentTypedInNamePattern, '<span class="search-highlight">$1</span>' );
        var emailHighlighted            = item.email.replace( currentTypedInNamePattern, '<span class="search-highlight">$1</span>' );
        var photoSize                   = 26;
        var photoUrl                    = ( item.photoExists === true ) ? '_image/user/' + item.key + '?_filter=scalesquare(' + photoSize + ')' : 'images/dummy-user-small.png';
        var highestAccessRight          = item.highestAccessRight;

        var highestAccessRightIcon = '';
        switch ( highestAccessRight )
        {
            case 'read':
                highestAccessRightIcon = 'images/icon_highest_access_read.gif';
                break;
            case 'update':
                highestAccessRightIcon = 'images/icon_highest_access_update.gif';
                break;
            case 'approve':
                highestAccessRightIcon = 'images/icon_highest_access_approve.gif';
                break;
            default:
                highestAccessRightIcon = 'images/icon_highest_access_none.gif'
        }

        var popupMenuItemHtml = '';

        popupMenuItemHtml += '<a>';
        popupMenuItemHtml += '   <table border="0" cellspacing="0" cellpadding="0" style="width:320px">';
        popupMenuItemHtml += '       <tr>';
        popupMenuItemHtml += '           <td class="user-picker-autocomplete-photo" style="width: ' + photoSize + 'px">';
        popupMenuItemHtml += '               <img src="' + photoUrl + '" alt="" width="' + photoSize + '" height="' + photoSize + '"/>';
        popupMenuItemHtml += '           </td>';
        popupMenuItemHtml += '           <td class="user-picker-autocomplete-name-email">';
        popupMenuItemHtml += '               <span>' + displayNameHighlighted + ' (' + qualifiedNameHighlighted + ')</span>';
        popupMenuItemHtml += '               <br/>';
        popupMenuItemHtml += '               <span class="user-picker-autocomplete-email">' + emailHighlighted + '</span>';
        popupMenuItemHtml += '           </td>';

        if ( config.ajaxServiceFunctionToExecute === 'findUsersAndAccessType' )
        {
            popupMenuItemHtml += '        <td class="user-picker-autocomplete-access-right">';
            popupMenuItemHtml += '            <img src="' + highestAccessRightIcon + '" alt=""/>';
            popupMenuItemHtml += '        </td>';
        }
        
        popupMenuItemHtml += '       </tr>';
        popupMenuItemHtml += '   </table>';
        popupMenuItemHtml += '</a>';

        return popupMenuItemHtml;
    }

}

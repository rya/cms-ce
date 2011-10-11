if ( !Templates )
{
    var Templates = {};
}

Templates.account = {
    userstoreRadioButton:
        '<tpl for=".">' +
                    '<div class="cms-userstore">' +
                    '<input type="radio" name="userstore" value="{key}">' +
                    '<div class="cms-userstore-block">' +
                        '<div class="left"><img width="50" height="50" src="app/account/images/app-icon-userstores.png"/></div>' +
                        '<div class="center">' +
                            '<h2>{name}</h2>' +
                            '<p>(usersstores\\\\{name})</p>' +
                        '</div>' +
                    '</div>' +
                    '</div><br>' +
                '</tpl>'

    ,gridPanelNameRenderer:
        '<div style="float:left"><img src="data/user/photo?key={0}&thumb=true" class="cms-thumbnail"></div>' +
        '<div style="float:left;padding-top: 5px"><div class="cms-grid-title">{1}</div>' +
        '<div class="cms-grid-description"><span class="cms-emphasis">{2}</span> in user store {3}</div>'

    ,editUserPanelHeader:
        '<div class="cms-edit-form-header clearfix">' +
            '<div class="left">' +
            '<img alt="User" src="data/user/photo?key={key}"/></div>' +
            '<div class="right">' +
            '<h1><input id="display-name" type="text" value="{displayName}" readonly="true" class="cms-display-name"/></h1><a href="javascript:;" class="edit-button"></a>' +
            '<p>{qualifiedName}</p></div></div>'

    ,noUserSelected:
        '<div>No user selected</div>'

    ,selectedUserLarge:
        '<tpl for="users">' +
            '<div id="selected-item-box-{key}" class="cms-selected-item-box large x-btn-default-large clearfix">' +
                '<div class="left">' +
                    '<img alt="User" src="data/user/photo?key={key}&thumb=true"/>' +
                '</div>' +
                '<div class="center">' +
                    '<h2>{displayName}</h2>' +
                    '<p>{userStore}/{name}</p>' +
                '</div>' +
                '<div class="right">' +
                    ' <a id="remove-from-selection-button-{key}" class="remove-selection" href="javascript:;"></a>' +
                '</div>' +
            '</div>' +
        '</tpl>'

    ,selectedUserSmall:
        '<tpl for="users">' +
            '<div id="selected-item-box-{key}" class="cms-selected-item-box small x-btn-default-small clearfix">' +
                '<div class="cms-selected-item-box left">' +
                    '<img alt="User" src="data/user/photo?key={key}&thumb=true"/>' +
                '</div>' +
                '<div class="cms-selected-item-box center">' +
                    '<h2>{displayName}</h2>' +
                '</div>' +
                '<div class="cms-selected-item-box right">' +
                    '<a id="remove-from-selection-button-{key}" class="remove-selection" href="javascript:;"></a>' +
                '</div>' +
            '</div>'+
        '</tpl>'

    ,deleteManyUsers:
        '<div class="cms-delete-user-confirmation-message">' +
            '<div class="icon-question-mark-32 cms-left" style="width:32px; height:32px; margin-right: 10px"><!-- --></div>' +
            '<div class="cms-left" style="margin-top:5px">' +
                'Are you sure you want to delete the selected {selectionLength} items?' +
            '</div>' +
        '</div>'

};

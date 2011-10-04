if ( !Templates )
{
    var Templates = {};
}

Templates.launcher = {
    loggedInUserButtonPopup:
        '<div class="cms-logged-in-user-popup-left">' +
            '<img src="resources/images/x-user.png"/>' +
        '</div>' +
        '<div class="cms-logged-in-user-popup-right">' +
            '<h1>{displayName}</h1>' +
            '<p>{qualifiedName}</p>' +
            '<p>{email}</p>' +
            '<p>&nbsp;</p>' +
            '<p>Edit Account</p>' +
            '<p>Change Password</p>' +
            '<p>&nbsp;</p>' +
            '<p class="cms-logged-in-user-popup-log-out"><a href="index.html">Log Out</a></p>' +
        '</div>'
};

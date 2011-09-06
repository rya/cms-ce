Ext.define('App.view.LoggedInUserButton', {
    extend: 'Ext.Button',
    alias: 'widget.loggedInUserButton',
    text: 'No Name',
    menu: [],
    enableToggle: true,

    constructor: function(config) {
        config = config || {};
        config.listeners = config.listeners || {};

        Ext.applyIf(config.listeners, {
            move: {scope:this, fn:function(component, x, y, options) {
                this.updatePopupPosition(component, x, y, options);
            }}
        });

        // Can we use 'this' here instead of CMS.view ..?
        App.view.LoggedInUserButton.superclass.constructor.apply(this, arguments);
    },

    onRender: function() {
        // Can we use 'this' here instead of CMS.view ..?
        App.view.LoggedInUserButton.superclass.onRender.apply(this, arguments);

        this.createPopup();
    },

    toggleHandler: function(button, state) {
        state ? this.popup.show() : this.popup.hide();
    },

    createPopup: function() {
        var user = {
            uid: 'abcdef012345',
            displayName: 'Joe Doe',
            qualifiedName: 'local\\joe',
            email: 'joe@doe.com'
        };

        this.popup = Ext.create('Ext.Component', {
            width: 380,
            floating: true,
            cls: 'x-panel-default-framed x-panel-default-framed logged-in-user-popup',
            bodyCls: 'logged-in-user-popup-body',
            renderTo: Ext.getBody()
        });
        this.popup.hide();

        this.createPopupTemplate().overwrite(this.popup.getEl(), user);
        this.updatePopupPosition();
    },

    createPopupTemplate: function() {
        return new Ext.XTemplate(
                '<div class="logged-in-user-popup-left"><img src="resources/images/x-user.png"/></div>',
                '<div class="logged-in-user-popup-right">',
                '<h1>{displayName}</h1>',
                '<p>{qualifiedName}</p>',
                '<p>{email}</p>',
                '<p>&nbsp;</p>',
                '<p>Edit Account</p>',
                '<p>Change Password</p>',
                '<p>&nbsp;</p>',
                '<p class="logged-in-user-popup-log-out"><a href="index.html">Log Out</a></p>',
                '</div>'
                );
    },

    updatePopupPosition: function(component, x, y, options) {
        var buttonArea = this.getEl().getPageBox();
        var popupX = buttonArea.right - this.popup.width;
        var popupY = buttonArea.bottom;
        this.popup.setPosition(popupX, popupY);
    }

});


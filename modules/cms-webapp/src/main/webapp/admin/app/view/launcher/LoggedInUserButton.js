Ext.define('CMS.view.launcher.LoggedInUserButton', {
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
                this.updatePanelPosition(component, x, y, options);
            }}
        });

        // Can we use 'this' here instead of CMS.view ..?
        CMS.view.launcher.LoggedInUserButton.superclass.constructor.apply(this, arguments);
    },

    onRender: function() {
        // Can we use 'this' here instead of CMS.view ..?
        CMS.view.launcher.LoggedInUserButton.superclass.onRender.apply(this, arguments);

        this.createPanel();
    },

    toggleHandler: function(button, state) {
        state ? this.panel.show() : this.panel.hide();
    },

    createPanel: function() {
        var self = this;

        self.panel = new Ext.panel.Panel({
            width: 420,
            height: 180,
            floating: true,
            //html: '<p>TODO: Create layout template</p><ul><li>Photo</li><li>Display Name</li><li>Qualified Name</li><li>E-mail</li><li><a href="">Account</a></li><li><a href="">Change Password</a></li><li><a href="index.html">Log Out</a></li></ul>',
            cls: 'logged-in-user-panel',
            bodyCls: 'logged-in-user-panel-body',
            renderTo: Ext.getBody()
        });
        self.panel.hide();

        var user = {
            uid: 'abcdef012345',
            displayName: 'Joe Doe',
            qualifiedName: 'local\\joe',
            email: 'joe@doe.com'
        };

        this.createTemplate().overwrite(this.panel.body, user);  // pass the root node of the data object

        self.updatePanelPosition();
    },

    createTemplate: function() {

        return new Ext.XTemplate(
                '<div class="logged-in-user-panel-left"><img src="resources/images/x-user.png"/></div>',
                '<div class="logged-in-user-panel-right">',
                '<h1>{displayName}</h1>',
                '<p>{qualifiedName}</p>',
                '<p>{email}</p>',
                '<p>&nbsp;</p>',
                '<p>Edit</p>',
                '<p>Change Password</p>',
                '<p>&nbsp;</p>',
                '<p><a href="index.html">Log Out</a></p>',
                '</div>'

                );
    },

    updatePanelPosition: function(component, x, y, options) {
        var buttonArea = this.getEl().getPageBox();
        var panelX = buttonArea.right - this.panel.width;
        var panelY = buttonArea.bottom;
        this.panel.setPosition(panelX, panelY);
    }

});


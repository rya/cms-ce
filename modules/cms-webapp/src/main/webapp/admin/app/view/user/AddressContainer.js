Ext.define( 'CMS.view.user.AddressContainer', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.addressContainer',

    width: '100%',
    title: 'Address',

    requires: [
        "CMS.view.user.AddressDropTarget"
    ],

    initComponent: function() {
        var button = {
            xtype: 'button',
            text: 'Add New Address',
            action: 'addNewTab',
            currentUser: this.currentUser
        };
        this.addEvents({
            validatedrop: true,
            beforedragover: true,
            dragover: true,
            beforedrop: true,
            drop: true
        });
        Ext.Array.include(this.items, button);
        this.callParent( arguments );
        this.on("drop", this.doLayout, this);
    },

    initEvents: function () {
        this.callParent();
        this.dd = Ext.create("CMS.view.user.AddressDropTarget", this, this.dropConfig)
    },

    beforeDestroy: function () {
        if (this.dd) {
            this.dd.unreg()
        }
        this.callParent();
    }

})
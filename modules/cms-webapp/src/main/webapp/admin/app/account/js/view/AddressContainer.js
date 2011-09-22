Ext.define( 'App.view.AddressContainer', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.addressContainer',

    width: '100%',
    title: 'Address',
    padding: 10,

    requires: [
        "App.view.AddressDropTarget"
    ],

    initComponent: function()
    {
        var button = {
            xtype: 'button',
            text: 'Add New Address',
            action: 'addNewTab',
            currentUser: this.currentUser
        };
        this.addEvents( {
                            validatedrop: true,
                            beforedragover: true,
                            dragover: true,
                            beforedrop: true,
                            drop: true
                        } );
        Ext.Array.include( this.items, button );
        this.callParent( arguments );
    },

    initEvents: function ()
    {
        this.callParent();
        this.dd = new App.view.AddressDropTarget( this, this.dropConfig )
    }

} );
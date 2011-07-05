Ext.define( 'CMS.view.user.DeleteWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.userDeleteWindow',

    height: 182,
    width: 400,

    title: 'Delete User',
    modal: true,

    layout: {
        type: 'vbox',
        align: 'stretch',
        defaultMargins: {top:10, right:10, bottom:10, left:10}
    },

    items: [
        {
            xtype: 'form',
            id: 'line1',
            layout: {
                type: 'hbox',
                defaultMargins: {top:0, right:10, bottom:0, left:0}
            },
            items: [
                {
                    xtype: 'image',
                    id: 'photo'
                },
                {
                    xtype: 'label',
                    id: 'dName'
                }
            ]
        }
    ],

    initComponent: function()
    {
        this.buttons = [
            {
                text: 'Cancel',
                scope: this,
                handler: this.close
            },
            {
                text: 'Delete user',
                action: 'deleteUser'
            }
        ];
        this.callParent( arguments );
    },

    doShow: function( model )
    {
        this.title = 'Delete User';
        var name = this.child( '#line1' ).child( '#dName' );
        var photo = this.child( '#line1' ).child( '#photo' );
        name.text = model.data.displayName + ' (' + model.data.qualifiedName + ')';
        photo.src = 'data/user/photo?key=' + model.data.key;
        photo.addCls('photo');
        this.show();
    }

} );


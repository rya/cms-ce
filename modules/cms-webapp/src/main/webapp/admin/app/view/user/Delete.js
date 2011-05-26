Ext.define( 'CMS.view.user.Delete', {
    extend: 'Ext.window.Window',
    alias: 'widget.userDeleteWindow',

    height: 130,
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
            xtype: 'container',
            id: 'line1',
            layout: {
                type: 'hbox',
                pack: 'left',
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
        },
        {
            xtype: 'container',
            layout: {
                type: 'hbox',
                align: 'stretch',
                pack: 'center',
                defaultMargins: {top:0, right:110, bottom:0, left:50}
            },
            height: 20,
            items: [
                {
                    xtype: 'button',
                    text: 'Cancel',
                    handler: function(b, e)
                    {
                        b.findParentByType('window').close();
                    }
                },
                {
                    xtype: 'button',
                    text: 'Delete',
                    handler: function()
                    {
                        alert( 'Not implemented' );
                    }
                }
            ]
        }
    ],

    initComponent: function()
    {
        this.callParent( arguments );

    },

    doShow: function( model )
    {
        this.title = 'Delete User >> ' + model.data.displayName;
        this.child( '#line1' ).child( '#dName' ).text = model.data.displayName + ' (' + model.data.qualifiedName + ')';
        this.child( '#line1' ).child( '#photo' ).src = 'rest/users/' + model.data.key + '/photo';
        this.show();
    }

} );


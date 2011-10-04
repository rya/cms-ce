Ext.define( 'App.view.DeleteWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.userDeleteWindow',
    title: 'Delete User',
    layout: 'fit',
    width: 350,
    bodyPadding: 10,
    plain: true,
    modal: true,
    border: false,

    initComponent: function()
    {
        this.listeners = {
                    'render': function() {
                        if (this.selectionLength === 1)
                        {
                            this.tpl = new Ext.XTemplate(Templates.common.userInfo),
                            this.update(this.modelData);
                        }
                        else
                        {
                            this.tpl = new Ext.XTemplate(Templates.account.deleteManyUsersForm),
                            this.update({selectionLength: this.selectionLength});
                        }
                    }
                }

        this.buttons = [
            {
                text: 'Cancel',
                handler: function() {
                    this.up('window').close();
                }
            },
            {
                text: 'Delete user'
            }
        ];

        this.callParent( arguments );
    },

    doShow: function( selection )
    {
        this.selectionLength = selection.length;

        if ( this.selectionLength === 1 )
        {
            this.modelData = selection[0].data;
        }

        this.show();
    }

});

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
        // TODO: Generalize (same template used in change password window)
        var tplDeleteOneHtml = '<div class="cms-delete-user-confirmation-message">'
                +'<div class="cms-user-info clearfix">'
                +'<div class="cms-user-photo cms-left">'
                +'<img alt="User" src="data/user/photo?key={key}&thumb=true"/>'
                +'</div>'
                +'<div class="cms-left">'
                +'{displayName}<br/>'
                +'({qualifiedName})<br/>'
                +'<a href="mailto:{email}:">{email}</a>'
                +'</div>'
                +'</div>'
                +'</div>';

        var tplDeleteManyHtml = '<div class="cms-delete-user-confirmation-message">'
                +'<div class="icon-question-mark-32 cms-left" style="width:32px; height:32px; margin-right: 10px">'
                +'</div>'
                +'<div class="cms-left" style="margin-top:5px">'
                +'Are you sure you want to delete the selected {selectionLength} items?'
                +'</div>'
                +'</div>';

        this.listeners = {
                    'render': function() {
                        if (this.selectionLength === 1)
                        {
                            this.tpl = new Ext.XTemplate(tplDeleteOneHtml),
                            this.update(this.modelData);
                        }
                        else
                        {
                            this.tpl = new Ext.XTemplate(tplDeleteManyHtml),
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

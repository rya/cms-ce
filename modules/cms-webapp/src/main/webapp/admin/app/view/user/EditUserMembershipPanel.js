Ext.define( 'CMS.view.user.EditUserMembershipPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.editUserMembershipPanel',

    title: 'Member of ...',

    layout: {
        type: 'table',
        columns: 1,
        defaultMargins: {top:10, right:10, bottom:10, left:10},
        padding: 10,
        tdAttrs: {
            style:{
                padding: '10px'
            }
        }
    },

    initComponent: function()
    {
        this.items = [
            {
                xtype: 'button',
                text: 'Add',
                action: 'addGroup'
            },
            {
                xtype: 'button',
                text: 'Add',
                action: 'addGroup'
            }
        ];
        this.callParent( arguments )
    },

    addGroup: function(id, title){
        var group = {
            xtype: 'groupItemField',
            groupId: id,
            title: title
        };
        var pos = this.items.getCount() - 1;
        this.insert(pos, group);
    }

} );
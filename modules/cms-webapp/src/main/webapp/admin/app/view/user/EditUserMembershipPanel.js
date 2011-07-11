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
                xtype: 'combobox',
                store: 'GroupStore',
                triggeredAction: 'all',
                typeAhead: true,
                mode: 'remote',
                minChars: 1,
                forceSelection: true,
                hideTrigger: true,
                valueField: 'key',
                displayField: 'name',
                listConfig: {
                    getInnerTpl: function()
                    {
                        return '{name} ({memberCount} members)';
                    },
                    action: 'selectGroup'
                }
            }
        ];
        this.callParent( arguments )
    },

    addGroup: function( id, title )
    {
        var group = {
            xtype: 'groupItemField',
            groupId: id,
            title: title
        };
        var pos = this.items.getCount();
        var unique = true;
        var items = this.items;
        Ext.each( items, function( el, index )
        {
            if ( items.get( index ).groupId == id )
            {
                unique = false;
            }
        } );
        if ( unique )
        {
            this.insert( pos, group );
        }
    }

} );
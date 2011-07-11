Ext.define( 'CMS.view.user.EditUserMembershipPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.editUserMembershipPanel',

    title: 'Member of ...',

    layout: {
        type: 'table',
        columns: 1,
        defaultMargins: {top:10, right:10, bottom:10, left:10},
        padding: 10,
        tableAttrs: {
            style:{
                width: '100%'
            }
        },
        tdAttrs: {
            style:{
                padding: '5px 10px 0px 10px'
            }
        }
    },

    initComponent: function()
    {
        this.items = [
            {
                xtype: 'combobox',
                store: 'GroupStore',
                itemId: 'groupSelector',
                triggeredAction: 'all',
                typeAhead: true,
                mode: 'remote',
                minChars: 1,
                forceSelection: true,
                hideTrigger: true,
                fieldLabel: 'Group Search',
                valueField: 'key',
                displayField: 'name',
                width: 400,
                listConfig: {
                    getInnerTpl: function()
                    {
                        return '<div style="white-space: nowrap;">{name} ({memberCount} members)</div>';
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
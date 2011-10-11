Ext.define('App.view.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.accountDetail',
    split: true,
    autoScroll: true,
    layout: 'card',
    collapsible: true,

    initComponent: function() {
        var largeBoxesPanel = this.createLargeBoxSelection();
        var smallBoxesPanel = this.createSmallBoxSelection();
        var noneSelectedPanel = this.createNoneSelection();

        this.items = [noneSelectedPanel, largeBoxesPanel, smallBoxesPanel];
        this.callParent(arguments);
    },

    showMultipleSelection: function(data, detailed){
        var activeItem;
        if (detailed)
        {
            activeItem = this.down('#largeBoxPanel');
            this.getLayout().setActiveItem('largeBoxPanel');
        }
        else
        {
            activeItem = this.down('#smallBoxPanel');
            this.getLayout().setActiveItem('smallBoxPanel');
        }

        activeItem.update({users: data});
    },

    showNoneSelection: function(data)
    {
        var activeItem = this.down('#noneSelectedPanel');
        this.getLayout().setActiveItem('noneSelectedPanel');
        activeItem.update(data);
    },

    createNoneSelection: function()
    {
        var tpl = new Ext.XTemplate( Templates.account.noUserSelected );

        var panel = {
            xtype: 'panel',
            itemId: 'noneSelectedPanel',
            styleHtmlContent: true,
            padding : 10,
            border: 0,
            tpl: tpl
        };

        return panel;
    },

    createLargeBoxSelection: function()
    {
        var tpl = Ext.Template( Templates.account.selectedUserLarge );

        var panel = {
            xtype: 'panel',
            itemId: 'largeBoxPanel',
            styleHtmlContent: true,
            autoScroll: true,
            listeners: {
                click: {
                    element: 'body',
                    fn: this.deselectItem,
                    scope: this
                }
            },
            padding: 10,
            border: 0,
            tpl: tpl
        };

        return panel;
    },

    createSmallBoxSelection: function()
    {
        var tpl = Ext.Template( Templates.account.selectedUserSmall );

        var panel = {
            xtype: 'panel',
            itemId: 'smallBoxPanel',
            styleHtmlContent: true,
            listeners: {
                click: {
                    element: 'body',
                    fn: this.deselectItem,
                    scope: this
                }
            },
            autoScroll: true,
            padding: 10,
            border: 0,
            tpl: tpl
        };

        return panel;
    },

    deselectItem: function(event, target)
    {
        var className = target.className;
        if (className && className === 'remove-selection')
        {
            var key = target.attributes.getNamedItem('id').nodeValue.split('remove-from-selection-button-')[1];
            var userGridSelModel = this.up('cmsTabPanel').down('accountGrid').getSelectionModel();
            var persistentGridSelection = this.persistentGridSelection;
            var selection = persistentGridSelection.getSelection();
            Ext.each(selection, function(item)
            {
                if (item.get('key') == key)
                {
                    Ext.get('selected-item-box-' + key).remove();
                    persistentGridSelection.deselect(item);
                }
            });
        }
    },

    generateUserButton: function(userData, shortInfo)
    {
        if (shortInfo)
        {
            return {
                xtype: 'userShortDetailButton',
                userData: userData
            };
        }
        else
        {
            return {
                xtype: 'userDetailButton',
                userData: userData
            };
        }
    },

    setCurrentUser: function(user)
    {
        this.currentUser = user;
    },

    getCurrentUser: function()
    {
        return this.currentUser;
    },

    updateTitle: function(persistentGridSelection)
    {
        this.persistentGridSelection = persistentGridSelection;
        var count = persistentGridSelection.getSelectionCount();
        var header = count + " user(s) selected";
        if ( count > 0 )
        {
            header += " (<a href='javascript:;' class='clearSelection'>Clear selection</a>)";
        }
        this.setTitle( header );

        var clearSel = this.header.el.down( 'a.clearSelection' );
        if ( clearSel )
        {
            clearSel.on( "click", function() {
                persistentGridSelection.clearSelections();
            }, this );
        }

    }

});

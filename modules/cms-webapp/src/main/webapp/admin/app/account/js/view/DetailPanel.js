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
        var nonSelectedPanel = this.createNonSelection();
        this.items = [nonSelectedPanel, largeBoxesPanel, smallBoxesPanel];
        this.callParent(arguments);
    },

    showMultipleSelection: function(data, detailed){
        var activeItem;
        if (detailed){
            activeItem = this.down('#largeBoxPanel');
            this.getLayout().setActiveItem('largeBoxPanel');
        }else{
            activeItem = this.down('#smallBoxPanel');
            this.getLayout().setActiveItem('smallBoxPanel');
        }
        activeItem.update({users: data});
    },

    showNonSelection: function(data){
        var activeItem = this.down('#nonSelectedPanel');
        this.getLayout().setActiveItem('nonSelectedPanel');
        activeItem.update(data);
    },

    createNonSelection: function(){
        var tpl = new Ext.XTemplate(
                '<div>No user selected</div>'
                );
        var panel = {
            xtype: 'panel',
            itemId: 'nonSelectedPanel',
            styleHtmlContent: true,
            padding : 10,
            border: 0,
            tpl: tpl
        };

        return panel;
    },

    createLargeBoxSelection: function(){
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

    createSmallBoxSelection: function(){
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

    deselectItem: function(e, t){
        var className = t.attributes.getNamedItem('class').nodeValue;
        if (className == 'remove-selection'){
            var key = t.attributes.getNamedItem('id').nodeValue.split('remove-from-selection-button-')[1];
            var userGridSelModel = this.up('cmsTabPanel').down('accountGrid').getSelectionModel();
            var persistentGridSelection = this.persistentGridSelection;
            var selection = persistentGridSelection.getSelection();
            Ext.each(selection, function(item){
                if (item.get('key') == key) {
                    Ext.get('selected-item-box-' + key).remove();
                    persistentGridSelection.deselect(item);
                }
            });
        }
    },

    generateUserButton: function(userData, shortInfo){
        if (shortInfo){
            return {
                xtype: 'userShortDetailButton',
                userData: userData
            };
        }else{
            return {
                xtype: 'userDetailButton',
                userData: userData
            };
        }
    },

    setCurrentUser: function(user){
        this.currentUser = user;
    },

    getCurrentUser: function(){
        return this.currentUser;
    },

    updateTitle: function(persistentGridSelection){
        this.persistentGridSelection = persistentGridSelection;
        var count = persistentGridSelection.getSelection().length;
        var header = count + " user(s) selected";
        if ( count > 0 ) {
            header += " (<a href='javascript:;' class='clearSelection'>Clear selection</a>)";
        }
        this.setTitle( header );

        var clearSel = this.header.el.down( 'a.clearSelection' );
        if ( clearSel ) {
            clearSel.on( "click", function() {
                persistentGridSelection.clearSelections();
            }, this );
        }

    },

});

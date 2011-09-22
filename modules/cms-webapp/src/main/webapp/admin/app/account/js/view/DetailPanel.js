Ext.define('App.view.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userDetail',
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
        var tpl = Ext.Template('<tpl for="users">' +
           '<div class="cms-selected-item-box large x-btn-default-large clearfix">' +
           '<div class="left">' +
           '<img alt="User" src="data/user/photo?key={key}&thumb=true"/></div>' +
           '<div class="center">' +
           '<h2>{displayName}</h2>' +
           '<p>{userStore}/{name}</p></div>' +
           '<div class="right">' +
           ' <a id="{key}" class="remove-selection" href="javascript:;"></a></div></div>' +
           '</tpl>');

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
        var tpl = Ext.Template('<tpl for="users">' +
            '<div class="cms-selected-item-box small x-btn-default-small clearfix">' +
            '<div class="cms-selected-item-box left">' +
            '<img alt="User" src="resources/images/user_add.png"/></div>' +
            '<div class="cms-selected-item-box center">' +
            '<h2>{displayName}</h2></div>' +
            '<div class="cms-selected-item-box right">' +
            '<a id="{key}" class="remove-selection" href="javascript:;"></a></div></div>'+
            '</tpl>');

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
            var key = t.attributes.getNamedItem('id').nodeValue;
            var userGridSelModel = this.up('cmsTabPanel').down('userGrid').getSelectionModel();
            var selection = userGridSelModel.getSelection();
            Ext.each(selection, function(item){
                if (item.get('key') == key){
                    userGridSelModel.deselect(item);
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

    updateTitle: function(selModel){
        this.selModel = selModel;
        this.setTitle(selModel.selected.length + " user(s) selected (<a href='#' class='selectAll'>select all</a>)")
        var a = this.header.el.down( 'a.selectAll' );
        a.on( "click", this.selectAll, this );
    },

    selectAll: function(){
        this.selModel.selectAll();
    }

});

Ext.define('CMS.view.modules.PropertyModule', {
    extend: 'Ext.ux.desktop.Module',

    requires: [
        'CMS.view.property.GridPanel'
    ],
    

    id: 'properties',

    init : function(){
        this.launcher = {
            text: 'Properties',
            iconCls: 'notepad',
            handler: this.createWindow,
            scope: this
        }
    },

    createWindow : function(){
        var desktop = this.app.getDesktop();
        var win = desktop.getWindow('properties');
        if(!win){
            win = desktop.createWindow({
                id: 'properties',
                title:'Properties',
                width: 800,
                height: 600,
                iconCls: 'notepad',
                animCollapse:false,
                border: false,
                //defaultFocus: 'notepad-editor', EXTJSIV-1300

                // IE has a bug where it will keep the iframe's background visible when the window
                // is set to visibility:hidden. Hiding the window via position offsets instead gets
                // around this bug.
                hideMode: 'offsets',

                layout: 'border',


                items: [
                    {
                        region: 'center',
                        xtype: 'propertyGrid'
                    }
                ]
            });
        }
        win.show();
        return win;
    }
});

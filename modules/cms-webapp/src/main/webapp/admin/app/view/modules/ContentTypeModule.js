Ext.define('CMS.view.modules.ContentTypeModule', {
    extend: 'Ext.ux.desktop.Module',

    requires: [
        'CMS.view.contentType.ShowPanel'
    ],
    

    id: 'contentTypes',

    init : function(){
        this.launcher = {
            text: 'Content Types',
            iconCls: 'notepad',
            handler: this.createWindow,
            scope: this
        }
    },

    createWindow : function(){
        var desktop = this.app.getDesktop();
        var win = desktop.getWindow('contentTypes');
        if(!win){
            win = desktop.createWindow({
                id: 'contentTypes',
                title:'Content Types',
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
                        xtype: 'contentTypeShow'
                    }
                ]
            });
        }
        win.show();
        return win;
    }
});

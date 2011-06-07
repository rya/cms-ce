Ext.define('CMS.view.modules.LanguageModule', {
    extend: 'Ext.ux.desktop.Module',

    requires: [
        'CMS.view.language.GridPanel'
    ],
    

    id: 'languages',

    init : function(){
        this.launcher = {
            text: 'Languages',
            iconCls: 'notepad',
            handler: this.createWindow,
            scope: this
        }
    },

    createWindow : function(){
        var desktop = this.app.getDesktop();
        var win = desktop.getWindow('languages');
        if(!win){
            win = desktop.createWindow({
                id: 'languages',
                title:'Languages',
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
                        xtype: 'languageGrid'
                    }
                ]
            });
        }
        win.show();
        return win;
    }
});

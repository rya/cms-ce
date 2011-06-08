/*!
 * Ext JS Library 4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */

Ext.define('CMS.view.modules.AccountModule', {
    extend: 'Ext.ux.desktop.Module',

    requires: [
        'CMS.view.user.ShowPanel',
        'CMS.view.user.FilterPanel'
    ],
    

    id: 'accounts',

    init : function(){
        this.launcher = {
            text: 'Accounts',
            iconCls: 'notepad',
            handler: this.createWindow,
            scope: this
        }
    },

    createWindow : function(){
        var desktop = this.app.getDesktop();
        var win = desktop.getWindow('accounts');
        if(!win){
            win = desktop.createWindow({
                id: 'accounts',
                title:'Accounts',
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
                        xtype: 'userShow'
                    },
                    {
                        region: 'west',
                        width: 225,
                        xtype: 'userFilter'
                    }
                ]
            });
        }
        win.show();
        return win;
    }
});

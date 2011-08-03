Ext.define( 'CMS.view.userstore.UserstoreFormPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userstoreFormPanel',

    requires: [
        'CMS.view.userstore.UserstoreForm',
        'CMS.view.userstore.UserstoreFormDetail'
    ],

    title: 'New Userstore',
    layout: 'border',
    defaults: {
        padding: 10,
        style: 'background: #FFF;'
    },


    initComponent: function() {

        this.items = [
            {
                xtype: 'userstoreForm',
                region: 'center',
                userstore: this.userstore,
                flex: 3
            },
            {
                xtype: 'userstoreFormDetail',
                region: 'east',
                userstore: this.userstore,
                flex: 1
            }
        ],

        this.callParent( arguments );

    }

});
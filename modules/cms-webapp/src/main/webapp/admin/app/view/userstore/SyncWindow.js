Ext.define('CMS.view.userstore.SyncWindow', {
    extend: 'Ext.window.Window',
    alias : 'widget.userstoreSyncWindow',

    title: 'Synchronize Userstore',
    bodyPadding: '20 40 10',
    layout: {
        type: 'anchor',
        defaultAnchor: '100%'
    },
    width: 400,
    closeAction: 'hide',
    defaults: {
        styleHtmlContent: true,
        style: 'text-align: center; margin-bottom: 20px;'
    },

    items: [
        {
            xtype: 'component',
            itemId: 'step',
            tpl: '<h3>Step {0} of {1} - {2}</h3>'
        },
        {
            xtype: 'progressbar',
            itemId: 'progress'
        },
        {
            xtype: 'component',
            itemId: 'count',
            tpl: '<h4>{0} of {1}</h4>'
        }
    ],
    buttonAlign: 'center',
    buttons: [
        {
            xtype: 'button',
            text: 'Stop',
            action: 'stopSyncUserstore'
        }
    ],


    initComponent: function() {
        this.callParent(arguments);
        this.updateData( this.userstore );
    },

    updateData: function( data ) {
        if ( data ) {
            this.child( '#step' ).update( data.step );
            this.child( '#progress' ).updateProgress( data.progress );
            this.child( '#count' ).update( data.count );
        }
        return this;
    }
});

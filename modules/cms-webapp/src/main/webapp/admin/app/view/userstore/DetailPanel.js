Ext.define('CMS.view.userstore.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userstoreDetail',

    title: 'Details',
    split: true,
    autoScroll: true,
    layout: {
        type: 'card'
    },
    bodyPadding: 10,
    tbar : {
        xtype: 'toolbar',
        border: false,
        padding: 5,
        items: [
            {
                itemId: 'editBtn',
                text: 'Edit Userstore',
                iconCls: 'icon-edit',
                action: 'editUserstore'
            },
            {
                itemId: 'delBtn',
                text: 'Delete Userstore',
                iconCls: 'icon-delete',
                action: 'deleteUserstore'
            },
            '-',
            {
                itemId: 'syncBtn',
                text: 'Synchronize',
                iconCls: 'icon-refresh',
                action: 'syncUserstore'
            },
            {
                itemId: 'stopBtn',
                text: "Stop synchronize",
                iconCls: 'icon-refresh',
                action: 'stopSyncUserstore',
                hidden: true
            }
        ]
    },
    activeItem: 0,
    items: [
        {
            itemId: 'defaultView',
            xtype: 'component',
            tpl: new Ext.XTemplate(
                    '<div class="detail-info">',
                    '<h3>{name}</h3>',
                    '<dl>',
                    '<dt>Key</dt><dd>{key}</dd>',
                    '<dt>Default Store</dt><dd>{defaultStore}</dd>',
                    '<dt>Connector Name</dt><dd>{connectorName}</dd>',
                    '</dl>',
                    '</div>')
        },
        {
            itemId: 'syncView',
            border: false,
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
            ]
        }
    ],

    initComponent: function() {
        this.callParent(arguments);
    },

    setCurrentUserstore: function(userstore) {
        this.currentUserstore = userstore;
    },

    getCurrentUserstore: function() {
        return this.currentUserstore;
    },

    updateData: function( userstore ) {
        this.child( '#defaultView' ).update( userstore.data );
        this.setCurrentUserstore( userstore );
    },

    updateSync: function( data ) {
        if ( data && this.currentUserstore
                && data.key == this.currentUserstore.data.key ) {
            this.down( '#step' ).update( data.step );
            this.down( '#progress' ).updateProgress( data.progress );
            this.down( '#count' ).update( data.count );
        }
    },

    setActiveView: function( view ) {
        var edit = this.down('#editBtn');
        var del = this.down('#delBtn');
        var sync = this.down('#syncBtn');
        var stop = this.down('#stopBtn');
        switch ( view ) {
            case 'sync':
                this.getLayout().setActiveItem( 'syncView' );
                edit.setDisabled( true );
                del.setDisabled( true );
                sync.setVisible( false );
                stop.setVisible( true );
            break;
            default:
                this.getLayout().setActiveItem( 'defaultView' );
                edit.setDisabled( false );
                del.setDisabled( false );
                sync.setVisible( true );
                stop.setVisible( false );
            break;
        }
    }

});

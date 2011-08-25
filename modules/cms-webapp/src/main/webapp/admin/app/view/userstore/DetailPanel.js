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
                text: 'Edit Userstore',
                iconCls: 'icon-edit',
                action: 'editUserstore'
            },
            {
                text: 'Delete Userstore',
                iconCls: 'icon-delete',
                action: 'deleteUserstore'
            },
            '-',
            {
                text: 'Synchronize',
                iconCls: 'icon-refresh',
                action: 'syncUserstore'
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
        if ( data ) {
            this.down( '#step' ).update( data.step );
            this.down( '#progress' ).updateProgress( data.progress );
            this.down( '#count' ).update( data.count );
        }
    }

});

Ext.define('App.view.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentTypeDetail',

    title: 'Details',
    split: true,
    autoScroll: true,

    initComponent: function() {

        this.tpl = new Ext.XTemplate( Templates.contenttype.detailPanelInfo, {
            formatDate: function( value )
            {
                if ( !value )
                {
                    return '';
                }
                return Ext.Date.format( value, 'j M Y, H:i:s' );
            }
        });

        this.dockedItems = [
            {
                dock: 'top',
                xtype: 'toolbar',
                border: false,
                padding: 5,
                items: [
                    {
                        text: 'Edit Content Type',
                        iconCls: 'icon-edit',
                        action: 'editContentType'
                    },
                    {
                        text: 'Delete Content Type',
                        iconCls: 'icon-delete',
                        action: 'deleteContentType'
                    }
                ]
            }
        ];

        this.callParent(arguments);
    }

});

Ext.define('CMS.view.contentType.Detail', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentTypeDetail',

    title: 'Details',
    split: true,
    autoScroll: true,

    initComponent: function() {

        this.tpl = new Ext.XTemplate(
                '<div class="detail-info">',
                '<h3>{name}</h3>',
                '<dl>',
                '<dt>Key</dt><dd>{key}</dd>',
                '<dt>Last Modified</dt><dd>{timestamp:this.formatDate}</dd>',
                '</dl>',
                '</div>', {

            formatDate: function(value) {
                if (!value) {
                    return '';
                }
                return Ext.Date.format(value, 'j M Y, H:i:s');
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
                        icon: 'resources/images/pencil.png',
                        action: 'editContentType'
                    },
                    {
                        text: 'Delete Content Type',
                        icon: 'resources/images/delete.png',
                        action: 'deleteContentType'
                    }
                ]
            }
        ];

        this.callParent(arguments);
    }

});

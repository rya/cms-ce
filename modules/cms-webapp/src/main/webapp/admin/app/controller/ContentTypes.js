Ext.define( 'CMS.controller.ContentTypes', {
    extend: 'Ext.app.Controller',

    stores: ['ContentTypes'],
    models: ['ContentType'],
    views: ['contentType.Grid'],

    refs: [
        {ref: 'contentTypeGrid', selector: 'contentTypeGrid'}
    ],

    init: function()
    {
        this.control( {
            '*[action=newContentType]': {
                  click: this.newContentType
            }
        } );
    },

    newContentType: function()
    {
        Ext.Msg.alert( 'New Language', 'Not implemented.' );
    }

} )
Ext.define('CMS.controller.Properties', {
    extend: 'Ext.app.Controller',

    stores: ['Properties'],
    models: ['Property'],
    views: ['property.Grid'],

    refs: [
        {ref: 'propertyGrid', selector: 'propertyGrid'}
    ]

})
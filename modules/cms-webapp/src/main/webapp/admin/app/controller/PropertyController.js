Ext.define('CMS.controller.PropertyController', {
    extend: 'Ext.app.Controller',

    stores: ['PropertyStore'],
    models: ['PropertyModel'],
    views: ['property.GridPanel'],

    refs: [
        {ref: 'propertyGrid', selector: 'propertyGrid'}
    ]

})
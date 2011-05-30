Ext.define('CMS.controller.Languages', {
    extend: 'Ext.app.Controller',

    stores: ['Languages'],
    models: ['Language'],
    views: ['language.Toolbar', 'language.Grid'],

    refs: [
        {ref: 'languageGrid', selector: 'languageGrid'}
    ],

    init: function() {
        this.control({
            '*[action=newLanguage]': {
                click: this.newLanguage
            }
        });
    },

    newLanguage: function() {
        Ext.Msg.alert('New Language', 'Not implemented.');
    }

});

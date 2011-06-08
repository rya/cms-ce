Ext.define('CMS.controller.LanguageController', {
    extend: 'Ext.app.Controller',

    stores: ['LanguageStore'],
    models: ['LanguageModel'],
    views: ['language.Toolbar', 'language.GridPanel'],

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

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

    newLanguage: function()
    {
        var editor = this.getLanguageGrid().getPlugin('cellEditor');
        editor.cancelEdit();
        var r = Ext.ModelManager.create( {
                                             key: '',
                                             languageCode: '',
                                             description: '',
                                             lastModified: new Date()
                                         }, 'CMS.model.LanguageModel' );
        this.getLanguageStoreStore().insert( 0, r );
        editor.startEditByPosition( {row: 0, column: 0} );
    }

});

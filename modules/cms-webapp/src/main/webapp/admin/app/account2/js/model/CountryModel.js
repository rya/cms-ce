Ext.define('App.model.CountryModel', {
    extend: 'Ext.data.Model',

    idField: 'code',

    fields: [
        'code',
        'englishName',
        'localName',
        'regionsEnglishName',
        'regionsLocalName',
        'callingCode'
    ]
});

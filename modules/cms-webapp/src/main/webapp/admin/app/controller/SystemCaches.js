Ext.define( 'CMS.controller.SystemCaches', {
    extend: 'Ext.app.Controller',

    stores: ['SystemCaches'],
    models: ['SystemCache'],
    views: ['systemCache.CacheListPanel', 'systemCache.CachePanel'],

    refs: [
        {ref: 'cacheListPanel', selector: 'cacheListPanel'},
        {ref: 'cachePanel', selector: 'cachePanel'}
    ],

    init: function()
    {
        this.control( {
          '*[action=clearCache]': {
              click: this.clearCache
          },
          '*[action=clearStats]': {
              click: this.clearStats
          }
        } );
    },

    clearCache: function()
    {
        alert( "Not implemented" );
    },

    clearStats: function()
    {
        alert( "Not implemented" );
    }



} );
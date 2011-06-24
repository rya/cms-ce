Ext.define( 'CMS.controller.SystemCacheController', {
    extend: 'Ext.app.Controller',

    stores: ['SystemCacheStore'],
    models: ['SystemCacheModel'],
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
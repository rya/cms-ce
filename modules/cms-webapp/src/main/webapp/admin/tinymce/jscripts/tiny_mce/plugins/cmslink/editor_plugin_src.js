/**
 * $Id: editor_plugin_src.js 539 2008-01-14 19:08:58Z tan $
 *
 * @author Enonic
 */

(function()
{
    tinymce.PluginManager.requireLangPack( 'cmslink' );

    tinymce.create( 'tinymce.plugins.CMSLinkPlugin', {
        
        init : function( ed, url )
        {
            ed.addCommand( 'cmslink', function()
            {
                var selection = ed.selection;
                var popupUpWindowWidth = 460, popupWindowHeight = 280;

                ed.windowManager.open( {
                    file : 'adminpage?page=1048&op=select',
                    width : popupUpWindowWidth + parseInt( ed.getLang( 'cmslink.delta_width', 0 ) ),
                    height : popupWindowHeight + parseInt( ed.getLang( 'cmslink.delta_height', 0 ) ),
                    inline : 1
                }, {
                    plugin_url : url
                } );
            } );

            ed.addButton( 'cmslink', {
                title : 'advlink.link_desc',
                cmd : 'cmslink'
            } );

            ed.addShortcut( 'ctrl+k', 'cmslink.cmslink_desc', 'cmslink' );

            ed.onNodeChange.add( function( ed, cm, n, co )
            {
                var activateLinkButton = (n.nodeName == 'A' && !n.name) ||
                        ( n.nodeName == 'IMG' && !n.name && n.parentNode.nodeName == 'A');
                
                cm.setActive( 'cmslink', activateLinkButton );
            } );
        },

        getInfo : function()
        {
            return {
                longname : 'CMS Link',
                author : 'Enonic AS',
                authorurl : 'http://www.enonic.com',
                infourl : 'http://www.enonic.com',
                version : '0.3'
            };
        }
    } );

    // Register plugin
    tinymce.PluginManager.add( 'cmslink', tinymce.plugins.CMSLinkPlugin );
})();
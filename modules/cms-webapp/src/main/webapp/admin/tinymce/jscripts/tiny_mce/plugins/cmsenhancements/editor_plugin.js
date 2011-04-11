/**
 * $Id: editor_plugin_src.js 000 200=-05-24 00:00:00Z tan $
 *
 * @author Tan
 * @copyright Copyright ? 2004-2008, Enonic, All rights reserved.
 *
 * This plugin fixes som quirks in the content document and various
 * browser differences in the document.
 */

(function() {
    tinymce.create('tinymce.plugins.CMSEnhancements', {
        init : function(ed, url)
        {
            var t = this;

            ed.onBeforeSetContent.add(function(ed, o)
            {
                t._padEmptyElementsBeforeContentIsSet( o )
            });


            ed.onGetContent.add(function(ed, o) {
                t._padEmptyElementsOnGetContent( o );
            });


            ed.onBeforeGetContent.add(function( ed, o )
            {
                t._cleanPreTag( ed );
            });


            /*
                Add HTML source button and Fullscreen button to the status bar
            */
            ed.onPostRender.add(function(ed, cm)
            {
                var sId = ed.id;
                var oPathRow = document.getElementById(sId + '_path_row');
                var oStatusBar = oPathRow.parentNode;

                oPathRow.style.padding = '3px 0 0 0';
                oStatusBar.style.height = '26px';

                var sButtons = '';

                if (ed.settings.accessToHtmlSource) {
                    sButtons += '<a class="mceButton mceButtonEnabled cms_code" title="' + ed.getLang('advanced.code_desc') + '" ' +
                            'onclick="javascript:tinyMCE.get(\'' + sId + '\').execCommand(\'mceCodeEditor\',false); return false;" ' +
                            'onmousedown="return false;" href="javascript:;" ' +
                            'style="float:left"><span class="mceIcon cms_code"></span></a>';
                }

                sButtons += '<a class="mceButton mceButtonEnabled mce_fullscreen" ' +
                        'title="' + ed.getLang('fullscreen.desc') +  '" onclick="javascript:tinyMCE.get(\'' + sId + '\').execCommand(\'mceFullScreen\',false); return false;" ' +
                        'href="javascript:;" style="float:left"><span class="mceIcon mce_fullscreen"></span></a><span class="mceSeparator" style="float:left"></span>';

                var oButtonWrapper = document.createElement('div');
                oButtonWrapper.id = sId + '_cms_button_wrapper';
                oButtonWrapper.style.cssFloat = 'left';

                oButtonWrapper.innerHTML = sButtons;

                oStatusBar.insertBefore(oButtonWrapper, oPathRow);
            });


            /*
                If the caret is inside a PRE element IE inserts a new PRE element each time the user hits enter.
                This will make the behaviour more Gecko like where a BR element is created.
            */
            if (tinymce.isIE)
            {
                ed.onKeyDown.add(function(ed, e)
                {
                    var selection = ed.selection;
                    var selectedNodeIsPreElementAndKeyboardKeyIsEnter = selection.getNode().nodeName == 'PRE' && e.keyCode == 13;
                    if ( selectedNodeIsPreElementAndKeyboardKeyIsEnter )
                    {

                        // IE will not display the new line if there is no content.
                        selection.setContent( '<br id="__" />&nbsp;', { format : 'raw' } );
                        var n = ed.dom.get( '__' );
                        n.removeAttribute( 'id' );

                        selection.select( n );
                        selection.collapse();

                        return tinymce.dom.Event.cancel( e );
                    }
                });
            }

            /*
                CMS Context menu
            */
            if (ed && ed.plugins.cmscontextmenu && ( ed.controlManager.get('table') || ed.controlManager.get('tablecontrols') ) )
            {
                ed.plugins.cmscontextmenu.onContextMenu.add(function(th, m, e)
                {
                    var sm, se = ed.selection, el = se.getNode() || ed.getBody();

                    if (ed.dom.getParent(e, 'td') || ed.dom.getParent(e, 'th')) {
                        m.removeAll();

                        if ( el.nodeName == 'A' && !ed.dom.getAttrib(el, 'name') && ed.controlManager.get('cmslink') ) {
                            m.add({title : 'advanced.link_desc', icon : 'link', cmd : 'cmslink', ui : true});
                            m.add({title : 'advanced.unlink_desc', icon : 'unlink', cmd : 'UnLink'});
                            m.addSeparator();
                        }

                        if (el.nodeName == 'IMG' && el.className.indexOf('mceItem') == -1) {
                            m.add({title : 'advanced.image_desc', icon : 'image', cmd : ed.plugins.advimage ? 'mceAdvImage' : 'mceImage', ui : true});
                            m.addSeparator();
                        }

                        m.add({title : 'table.desc', icon : 'table', cmd : 'mceInsertTable', ui : true, value : {action : 'insert'}});
                        m.add({title : 'table.props_desc', icon : 'table_props', cmd : 'mceInsertTable', ui : true});
                        m.add({title : 'table.del', icon : 'delete_table', cmd : 'mceTableDelete', ui : true});
                        m.addSeparator();

                        // Cell menu
                        sm = m.addMenu({title : 'table.cell'});
                        sm.add({title : 'table.cell_desc', icon : 'cell_props', cmd : 'mceTableCellProps', ui : true});
                        sm.add({title : 'table.split_cells_desc', icon : 'split_cells', cmd : 'mceTableSplitCells', ui : true});
                        sm.add({title : 'table.merge_cells_desc', icon : 'merge_cells', cmd : 'mceTableMergeCells', ui : true});

                        // Row menu
                        sm = m.addMenu({title : 'table.row'});
                        sm.add({title : 'table.row_desc', icon : 'row_props', cmd : 'mceTableRowProps', ui : true});
                        sm.add({title : 'table.row_before_desc', icon : 'row_before', cmd : 'mceTableInsertRowBefore'});
                        sm.add({title : 'table.row_after_desc', icon : 'row_after', cmd : 'mceTableInsertRowAfter'});
                        sm.add({title : 'table.delete_row_desc', icon : 'delete_row', cmd : 'mceTableDeleteRow'});
                        sm.addSeparator();
                        sm.add({title : 'table.cut_row_desc', icon : 'cut', cmd : 'mceTableCutRow'});
                        sm.add({title : 'table.copy_row_desc', icon : 'copy', cmd : 'mceTableCopyRow'});
                        sm.add({title : 'table.paste_row_before_desc', icon : 'paste', cmd : 'mceTablePasteRowBefore'});
                        sm.add({title : 'table.paste_row_after_desc', icon : 'paste', cmd : 'mceTablePasteRowAfter'});

                        // Column menu
                        sm = m.addMenu({title : 'table.col'});
                        sm.add({title : 'table.col_before_desc', icon : 'col_before', cmd : 'mceTableInsertColBefore'});
                        sm.add({title : 'table.col_after_desc', icon : 'col_after', cmd : 'mceTableInsertColAfter'});
                        sm.add({title : 'table.delete_col_desc', icon : 'delete_col', cmd : 'mceTableDeleteCol'});
                    } else
                        m.add({title : 'table.desc', icon : 'table', cmd : 'mceInsertTable', ui : true});
                });
            }

        },

        getInfo : function()
        {
            return {
                longname : 'CMS Code Formater',
                author : 'tan@enonic.com',
                authorurl : 'http://www.enonic.com',
                infourl : 'http://www.enonic.com',
                version : "1.0"
            };
        },


        _padEmptyElementsBeforeContentIsSet : function( o )
        {
            // Make sure empty TD elements are padded with "&nbsp"
            o.content = o.content.replace(/<td\/>/g, '<td>&nbsp;<\/td>');
            // Make sure empty TEXTAREA elements are padded with "&nbsp"
            o.content = o.content.replace(/<textarea\s+(.+)\/>/g, '<textarea $1><\/textarea>');
            // Make sure empty LABEL elements are padded with "&nbsp"
            o.content = o.content.replace(/<label\s+(.+)\/>/g, '<label $1><\/label>');
            // Make sure @_moz_dirty is added to IMG elements so the ui works properly for Fx.
            o.content = o.content.replace(/<img\s+(.+)\/>/g, '<img $1 _moz_dirty="" \/>');
            // Make sure empty EMBED elements are padded with "&nbsp"
            o.content = o.content.replace(/<embed(.+\n).+\/>/g, '<embed$1>.</embed>');
        },

        _padEmptyElementsOnGetContent : function( o )
        {
            // Pad IFRAME elements with "cms_content"
            o.content = o.content.replace( /<iframe(.+?)>(|\s+)<\/iframe>/g, '<iframe$1>cms_content<\/iframe>' );
            // Pad empty TD and TH elements with "&nbsp;"
            o.content = o.content.replace( /<td><\/td>/g, '<td>&nbsp;</td>' );
            o.content = o.content.replace( /<th><\/th>/g, '<th>&nbsp;</th>' );
        },

        _cleanPreTag : function( ed )
        {
            if ( tinymce.isIE )
            {
                var preElements = ed.getDoc().getElementsByTagName( 'pre' );
                var preElementsLn = preElements.length;
                for ( var x = 0; x < preElementsLn; x++ )
                {
                    var preElement = preElements[x];
                    var temp = preElement.innerHTML.split( '&nbsp;' );
                    preElement.innerHTML = temp.join( '' );
                }
            }

            var br = ed.dom.select( 'pre br' );
            for ( var i = 0; i < br.length; i++ )
            {
                var nlChar;
                if ( tinymce.isIE )
                    nlChar = '\r\n';
                else
                    nlChar = '\n';

                var nl = ed.getDoc().createTextNode( nlChar );
                ed.dom.insertAfter( nl, br[i] );
                ed.dom.remove( br[i] );
            }
        }

    });

    tinymce.PluginManager.add( 'cmsenhancements', tinymce.plugins.CMSEnhancements );
})();

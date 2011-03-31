/**
 * $Id: editor_plugin_src.js 000 200=-05-24 00:00:00Z tan $
 *
 * @author Tan
 * @copyright Copyright � 2004-2008, Enonic, All rights reserved.
 *
 * This plugin fixes som quirks in the content document and various
 * browser differences in the document.
 */

(function() {
  tinymce.create('tinymce.plugins.CMSCodeFormater', {
    init : function(ed, url) {
			var t = this;

      // ---------------------------------------------------------------------------------------------------------------
      // Events.
      // ---------------------------------------------------------------------------------------------------------------
      // Register events to the current editor instance.
      /*
      ed.onNodeChange.add(function(ed, cm, e) {
        t._insertTrailingElement(ed);
      });
      */

      ed.onGetContent.add(function(ed, o) {
        t._populateEmptyTableCells(o);
      });

      /*
      ed.onSetContent.add(function(ed, o) {
        t._insertTrailingElement(ed);
      });
      */

      /*
      ed.onMouseDown.add(function(ed, e) {
        if (ed.selection.getNode().nodeName == 'IMG') {
          t._marginHandler(ed);
        }
      });
      */

      // IE inserts a new PRE tag each time the user hits enter. This will make the behaviour more Gecko like by
      // preventing the behaviour and insert a temporary BR.
      if (tinymce.isIE) {
        ed.onKeyDown.add(function(ed, e) {
          var n, s = ed.selection;
          if (e.keyCode == 13 && s.getNode().nodeName == 'PRE') {
            // IE will not display the new line if there is no content. The nbsp will be removed on
            // onBeforeSetContent events.
            s.setContent('<br id="__" />&nbsp;', {format : 'raw'});
            n = ed.dom.get('__');
            n.removeAttribute('id');
            s.select(n);
            s.collapse();
            return tinymce.dom.Event.cancel(e);
          }
        });
      }

      // This makes sure that all BRs inside PRE is replaced with newlines when events like
      // save view HTML source etc. occurres.
      // Also cleans up trailing empty P
      // TODO: Remvoe trailing P elements.
      ed.onBeforeGetContent.add(function(ed, o) {
        t._cleanPreTag(ed);
        // t._removeTrailingElement(ed);
      });
    },

    getInfo : function() {
			return {
				longname : 'CMS Code Fromater',
				author : 'tan@enonic.com',
				authorurl : 'http://www.enonic.com',
				infourl : 'http://www.enonic.com',
				version : "1.0"
			};
		},

    // -----------------------------------------------------------------------------------------------------------------
    // Internals.
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Method: _insertTrailingElement
     *
     * This should Make sure that it is always posible to place the caret after
     * the block where the caret is.
    */
      
    /*
    _insertTrailingElement : function(ed) {
      var lc = ed.getDoc().getElementsByTagName('body')[0].lastChild;

      if (!lc)
        return;

      try {
        if (!lc.innerHTML.match(/^<br>$/i)) {
          var p, br;
          p = ed.getDoc().createElement('p');
          br = ed.getDoc().createElement('br');
          p.appendChild(br);
          ed.dom.insertAfter(p, lc);
        }
      } catch(err) {}
    },
    */


    /**
     * Method: _cleanPreTag
     *
     * Replaces all BR tags with newlines inside the PRE elements.
    */
    _cleanPreTag : function(ed) {
        // Since our handling of the pre tag inserts &nbsp;:Text we need to remove them.
        // TODO: This could be done with regex, but I have not found a way to find the nbsp;
        if (tinymce.isIE) {
            var preElems = ed.getDoc().getElementsByTagName('pre');
            var preElemsLn = preElems.length;
            for (var x = 0; x < preElemsLn; x++) {
                var preElem = preElems[x];
                var temp = preElem.innerHTML.split('&nbsp;');
                preElem.innerHTML = temp.join('');
            }
        }

        var br = ed.dom.select('pre br');
        for (var i = 0; i < br.length; i++) {
            var nlChar;
            if (tinymce.isIE)
                nlChar = '\r\n';
            else
                nlChar = '\n';

            var nl = ed.getDoc().createTextNode(nlChar);
            ed.dom.insertAfter(nl, br[i]);
            ed.dom.remove(br[i]);
        }
    },

    /**
     * Method: _removeTrailingElement
     *
     * Removes trailing P elements.
     */
     /*
    _removeTrailingElement : function(ed) {
      var root = ed.getDoc().getElementsByTagName('body')[0];
      if (!root)
        return;

      var children = root.childNodes;
      if (children.length <= 1)
        return;

      var current = root.firstChild, i = 0, last;
      while(current) {
        if (current.nodeName == 'P') {
          last = i;
        }
        current = current.nextSibling;
        i++;
      }

      if (children[last].innerHTML.match(/^<br>$/i)) {
        root.removeChild(children[last]);
        // this._removeTrailingP(ed);
      }
    },
    */

    /**
     * Method: _populateEmptyTDElements
     *
     * Finds all empty cells in a table and inject a nbsp element to them.
     */
    _populateEmptyTableCells : function(o) {
      o.content = o.content.replace(/<td><\/td>/g, '<td>&nbsp;</td>');
      o.content = o.content.replace(/<th><\/th>/g, '<th>&nbsp;</th>');
    }
  });

	tinymce.PluginManager.add('cmscodeformater', tinymce.plugins.CMSCodeFormater);
})();

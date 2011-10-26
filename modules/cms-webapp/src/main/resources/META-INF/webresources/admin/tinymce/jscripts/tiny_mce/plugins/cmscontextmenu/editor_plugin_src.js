/**
 * $Id: editor_plugin_src.js 755 2008-03-29 19:14:42Z spocke $
 *
 * @author Moxiecode
 * @copyright Copyright � 2004-2008, Moxiecode Systems AB, All rights reserved.
 *
 * @Modified by tan@enonic.com, www.enonic.com
 */

(function() {
	var Event = tinymce.dom.Event, each = tinymce.each, DOM = tinymce.DOM;

	tinymce.create('tinymce.plugins.CMSContextMenu', {
		init : function(ed) {
			var t = this;
            var ctrlKeyPressed = false;

            var isFx2 = navigator.userAgent.indexOf('Firefox/2.0');

            t.editor = ed;
			t.onContextMenu = new tinymce.util.Dispatcher(this);

            if (tinymce.isMac && isFx2 > -1) {
                ed.onKeyDown.add(function(ed, e) {
                    if(e.keyCode == 17) {
                        ctrlKeyPressed = true;
                    }
                });

                ed.onKeyUp.add(function(ed, e) {
                    if(e.keyCode == 17) {
                        ctrlKeyPressed = false;
                    }
                });

                ed.onClick.add(function(ed, e) {
                    ctrlKeyPressed = false;
                });
            }

            // TODO: Refactor!
            if (tinymce.isMac && isFx2 > -1) {
                ed.onContextMenu.add(function(ed, e) {
                    if (!ctrlKeyPressed) {
                        t._getMenu(ed).showMenu(e.clientX, e.clientY);
                        Event.add(document, 'click', hide);
                        Event.cancel(e);
                    }
                    ctrlKeyPressed = false;
                });
            } else {
                ed.onContextMenu.add(function(ed, e) {
                    if (!e.ctrlKey) {
                        t._getMenu(ed).showMenu(e.clientX, e.clientY);
                        Event.add(document, 'click', hide);
                        Event.cancel(e);
                    }
                });
            }

            function hide() {
				if (t._menu) {
					t._menu.removeAll();
					t._menu.destroy();
					Event.remove(document, 'click', hide);
				}
			};

			ed.onMouseDown.add(hide);
			ed.onKeyDown.add(hide);
		},

		getInfo : function() {
			return {
				longname : 'CMS Contextmenu',
				author : 'Enonic',
				authorurl : 'http://www.enonic.com',
				infourl : 'http://www.enonic.com',
				version : tinymce.majorVersion + "." + tinymce.minorVersion
			};
		},

		_getMenu : function(ed) {
            var t = this, m = t._menu, se = ed.selection, col = se.isCollapsed(), el = se.getNode() || ed.getBody(), am, p1, p2;

            function isMceItemFlash(elem) {
                var isMceItemFlash = true;
                if (elem.className.indexOf('mceItemFlash') < 0)
                  isMceItemFlash = false;

                return isMceItemFlash;
            }

            function isCMSBinary(a) {
              return /binary\/(\d+)\/file/.test(a);
            }

            if (m) {
				m.removeAll();
				m.destroy();
			}

			p1 = DOM.getPos(ed.getContentAreaContainer());
			p2 = DOM.getPos(ed.getContainer());

			m = ed.controlManager.createDropMenu('cmscontextmenu', {
				offset_x : p1.x + 3,
				offset_y : p1.y + 3,
/*				vp_offset_x : p2.x,
				vp_offset_y : p2.y,*/
				constrain : 1
			});

			t._menu = m;

            m.add({title : 'advanced.cut_desc', icon : 'cut', cmd : 'Cut'}).setDisabled(col);
			m.add({title : 'advanced.copy_desc', icon : 'copy', cmd : 'Copy'}).setDisabled(col);
			m.add({title : 'advanced.paste_desc', icon : 'paste', cmd : 'Paste'});

            if (ed.controlManager.get('cmslink') && !isMceItemFlash(el)) {
                if ((el.nodeName == 'A' && !ed.dom.getAttrib(el, 'name')) || col) {
                    m.addSeparator();
                    m.add({title : 'advanced.link_desc', icon : 'link', cmd : ed.plugins.cmslink ? 'cmslink' : 'cmslink', ui : true});
                }
                if ((el.nodeName == 'A' && !ed.dom.getAttrib(el, 'name')) || !col) {
                    m.add({title : 'advanced.unlink_desc', icon : 'unlink', cmd : 'UnLink'});
                }
            }

            if (ed.controlManager.get('cmsimage') && !isMceItemFlash(el)) {
                m.addSeparator();
                m.add({title : 'advanced.image_desc', icon : 'image', cmd : ed.plugins.cmsimage ? 'cmsimage' : 'cmsimage', ui : true});
            }

            if (ed.controlManager.get('justifyleft')) {
                m.addSeparator();
                am = m.addMenu({title : 'contextmenu.align'});
                am.add({title : 'contextmenu.left', icon : 'justifyleft', cmd : 'JustifyLeft'});
                am.add({title : 'contextmenu.center', icon : 'justifycenter', cmd : 'JustifyCenter'});
                am.add({title : 'contextmenu.right', icon : 'justifyright', cmd : 'JustifyRight'});
                am.add({title : 'contextmenu.full', icon : 'justifyfull', cmd : 'JustifyFull'});
            }
            t.onContextMenu.dispatch(t, m, el, col);

			return m;
		}
	});

	// Register plugin
	tinymce.PluginManager.add('cmscontextmenu', tinymce.plugins.CMSContextMenu);
})();
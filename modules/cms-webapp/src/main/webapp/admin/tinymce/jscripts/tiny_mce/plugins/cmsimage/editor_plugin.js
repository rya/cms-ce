/*
    This plug in is not tested on editors that has force_br_newlines set to true.
    This plug in is not tested on a document containing multiple editors.

    Known issues:

        - Opera does not allow draging of image. This is a feature/bug in Opera's designMode architecture.
        - The re select after an alignment task is a bit quirky in Opera, not sure if this is Operas fault.
        - After a drag event, IE selects the image, Firefox does not. This event is outside of the JavaScript scope.

    TODO:

    Alignment:
        - Handle Flash/media placeholders.

    DIV:
    - Use the dom.getNext/prev API instead of our own.

*/

(function() {
	tinymce.PluginManager.requireLangPack('cmsimage');

    var reCMSImagePattern = /_image\/(\d+)/im;

	tinymce.create('tinymce.plugins.CMSImagePlugin',
    {
        init : function(ed, url)
        {
            var t = this;

            ed.addCommand('cmsimage', function()
            {
                var pageToOpen = 'adminpage?page=600&op=popup&subop=insert' +
                                 '&selectedunitkey=-1' +
                                 '&fieldname=null' +
                                 '&fieldrow=null' +
                                 '&handler=com.enonic.vertical.adminweb.handlers.ContentEnhancedImageHandlerServlet' +
                                 '&contenthandler=image';

                var dom = ed.dom;
                var selection = ed.selection;
                var selectedNode = selection.getNode();
                var imageContentKey;

                if ( selectedNode.nodeName === 'IMG'  )
                {
                    if ( dom.getAttrib(selectedNode, 'src').match(reCMSImagePattern) )
                    {
                        imageContentKey = dom.getAttrib(selectedNode, 'src').match(reCMSImagePattern)[1];
                        pageToOpen = 'adminpage?page=992&key=' + imageContentKey + '&cat=-1&op=insert&subop=update';
                    }
                }

                ed.windowManager.open({
                    file : pageToOpen,
                    width : 990 + ed.getLang('cmsimage.delta_width', 0),
                    height : 620 + ed.getLang('cmsimage.delta_height', 0),
                    inline : 1,
                    scrollbars : 'yes'
                }, {
                    plugin_url : url
                });
            });
            //  ----------------------------------------------------------------------------------------------------------------------------

            ed.addButton('cmsimage', {
                title : 'cmsimage.desc',
                cmd : 'cmsimage',
                image : url + '/img/image.gif'
            });
            //  ----------------------------------------------------------------------------------------------------------------------------

            // If the caret is in a CMS image block (eg. <p class="editor-p-center"> ..) and the user presses enter,
            // the editor adds the same attributes to new P block element as the previous sibling.
            // This is standard functionality in the content editable browser implementation.
            // We want to remove our classes when this happen.
            ed.onKeyUp.add(function( ed, e )
            {
                if ( e.keyCode === 13 ) // Enter
                {
                    var dom = ed.dom;
                    var selection = ed.selection;
                    var currentNode = selection.getNode();
                    var currentNodeHasCMSImageClass = dom.hasClass(currentNode, 'editor-p-block') ||
                                                      dom.hasClass(currentNode, 'editor-p-center');

                    if ( currentNodeHasCMSImageClass )
                    {
                        dom.removeClass(currentNode, 'editor-p-block');
                        dom.removeClass(currentNode, 'editor-p-center');
                    }
                }
            });
            //  ----------------------------------------------------------------------------------------------------------------------------

            ed.onNodeChange.add(function(ed, cm, n)
            {
                var oDOM = ed.dom;
                var oParentNode = n.parentNode;

                var bSetCMSImageButtonActive = n.nodeName == 'IMG' && oDOM.getAttrib(n, 'src').match(reCMSImagePattern) && n.className.indexOf('mceItem') == -1;
                var bSetJustifyLeftButtonActive = n.nodeName == 'IMG' && oDOM.hasClass(n, 'editor-image-left') || oDOM.getStyle(n, 'textAlign') == 'left';
                var bSetJustifyRightButtonActive = n.nodeName == 'IMG' && oDOM.hasClass(n, 'editor-image-right') || oDOM.getStyle(n, 'textAlign') == 'right';
                var bSetJustifyCenterButtonActive = oDOM.hasClass(oParentNode, 'editor-p-center') || oDOM.getStyle(n, 'textAlign') == 'center';
                var bSetJustifyFullButtonActive = oDOM.hasClass(oParentNode, 'editor-p-block') || oDOM.getStyle(n, 'textAlign') == 'justify';

                cm.setActive('cmsimage', bSetCMSImageButtonActive);
                cm.setActive('justifyleft', bSetJustifyLeftButtonActive);
                cm.setActive('justifyright', bSetJustifyRightButtonActive);
                cm.setActive('justifycenter', bSetJustifyCenterButtonActive);
                cm.setActive('justifyfull', bSetJustifyFullButtonActive);
            });


            /*******************************************************************************************************************************
            *** START: Alignment prototype code
            *******************************************************************************************************************************/

            var oSelectedImage = null;

            // Image dragging solution for non Gecko browsers.
            // Firefox selects any wrapped P element when the image is dragged in the editor document.
            // This is desirable for our logic (centered images is wraped inside a P elem).
            // Other browseres selects only the image, leaving the P elem at its original position.
            // On non Gecko browseres, try to select the P wrapper element.

            ed.onMouseDown.add( function(ed, e)
            {
                if ( !tinymce.isGecko )
                {
                    var oDOM = ed.dom;

                    if ( e.target.nodeName === 'IMG' )
                    {
                        oSelectedImage = e.target;

                        var parent = e.target.parentNode;

                        if ( parent.nodeName === 'P' && ( oDOM.hasClass(parent, 'editor-p-block') || oDOM.hasClass(parent, 'editor-p-center')) )
                        {
                            ed.selection.select(parent);
                        }
                    }
                }
            });


            ed.onMouseUp.add( function(ed, e)
            {
                if ( !tinymce.isGecko )
                {
                    var oDOM = ed.dom;

                    if ( e.target.nodeName === 'IMG' )
                    {
                        oSelectedImage = e.target;

                        var parent = e.target.parentNode;
                        if ( parent.nodeName === 'P' && ( oDOM.hasClass(parent, 'editor-p-block') || oDOM.hasClass(parent, 'editor-p-center')) )
                        {
                            ed.selection.select(e.target);
                        }
                    }
                }
            });


            // A command in the editor instance has been requested. Trap the justify commands and prepeare the image.
            ed.onBeforeExecCommand.add(function(ed, cmd, ui, val)
            {
                if ( cmd === 'JustifyLeft' || cmd === 'JustifyRight' ||
                     cmd === 'JustifyCenter' || cmd === 'JustifyFull')
                {
                    var oDOM = ed.dom;

                    // For IE the oSelectedImage is selected on the mouse events.
                    if ( tinymce.isGecko )
                        oSelectedImage = ed.selection.getNode();

                    if ( oSelectedImage && oSelectedImage.nodeName === 'IMG' )
                    {
                        t.removeCmsCssFromImage(ed.selection.getNode(), ed);

                        // Since the node can be cloned and removed after such a command is executed.
                        // Setting a bogus attribute on the IMG makes it easier to find.
                        oDOM.setAttrib(oSelectedImage, 'cmsimage', '1');

                        // Since TinyMCE creates a P for us, mark our P element for removal after the command has executed.
                        if ( cmd === 'JustifyCenter' && oDOM.hasClass(oSelectedImage.parentNode, 'editor-p-block') )
                        {
                            oDOM.setAttrib(oSelectedImage.parentNode, 'cmsp', '1');
                        }
                    }
                }
            });

            // The command has now been executed and we want to remove the native TinyMCEs alignment solution.
            ed.onExecCommand.add(function(ed, cmd, ui, val)
            {
                if ( !/Justify(left|right|center|full)/i.test(cmd) )
                {
                    return;
                }

                var oDOM = ed.dom;
                var oImageElement = oSelectedImage;

                if ( oImageElement && oImageElement.nodeName === 'IMG')
                {
                    if ( cmd === 'JustifyLeft')
                    {
                        t.alignImage(ed, oImageElement, 'left');
                    }
                    else if ( cmd === 'JustifyRight')
                    {
                        t.alignImage(ed, oImageElement, 'right');
                    }
                    else if ( cmd === 'JustifyCenter')
                    {
                        // On centering, TinyMCE has cloned and removed the original IMG element.
                        // Select it again using the bogus attribute added before the command started.

                        oImageElement = oDOM.select('img[cmsimage=1]')[0];

                        t.centerImage(ed, oImageElement);

                        var oCMSPBlock = oDOM.select('p[cmsp=1]')[0];
                        if ( oDOM.hasClass(oCMSPBlock, 'editor-p-block') )
                        {
                            oDOM.remove(oCMSPBlock);
                        }

                    }
                    else if ( cmd === 'JustifyFull')
                    {
                        t.blockImage(ed, oImageElement);
                    }

                    // Firefox leaves the resize handlers in the old position. This command will clean it up.
                    ed.execCommand('mceRepaint');

                    oImageElement = oDOM.select('img[cmsimage=1]')[0];

                    oDOM.setAttrib(oImageElement, 'cmsimage');

                    ed.selection.select(oImageElement);

                    oDOM.setAttrib(oImageElement, '_moz_resizing', 'true');

                    // Needed for IE.
                    oSelectedImage = oImageElement;
                }
            });

            /*******************************************************************************************************************************
            *** END: Alignment prototype code!!
            *******************************************************************************************************************************/
		},
        // ---------------------------------------------------------------------------------------------------------------------------------

		createControl : function(n, cm) {
			return null;
		},
        // ---------------------------------------------------------------------------------------------------------------------------------

		getInfo : function() {
			return {
				longname : 'Image Browser for CMS',
				author : 'Enonic',
				authorurl : 'http://www.enonic.com',
				infourl : 'http://www.enonic.com',
				version : "0.2"
			};
		},

        /*******************************************************************************************************************************
        *** START: Alignment prototype routines
        *******************************************************************************************************************************/

        alignImage: function( ed, imageElement, align )
        {
            var t = this;
            var dom = ed.dom;
            var parentParagraphToImage = dom.getParent( imageElement, 'p' );
            var imageHasLink = imageElement.parentNode.nodeName === 'A';
            var isImageInsideCMSParagraph, closestParagraph, isSiblingToImageParagraph;

            // When the justify left|right command is executed, TinyMCE adds a style float to the IMG element.
            dom.setStyle( imageElement, 'float', '' );
            dom.addClass( imageElement, 'editor-image-' + align );

            // Sometimes css classes is added to the image link and we need to remove them.
            if ( imageHasLink )
            {
                dom.removeClass( imageElement.parentNode, 'editor-image-left' );
                dom.removeClass( imageElement.parentNode, 'editor-image-right' );
            }

            // If the image is inside a P element, we want to remove it because we want left|right aligned images to be inlined with the text node.
            isImageInsideCMSParagraph = parentParagraphToImage && parentParagraphToImage.nodeName === 'P' && ( dom.hasClass( parentParagraphToImage, 'editor-p-block') || dom.hasClass( parentParagraphToImage, 'editor-p-center' ) );

            if ( isImageInsideCMSParagraph )
            {
                dom.remove( parentParagraphToImage, true );
            }
            else
            {
                return;
            }

            // Now the IMG should float/align next to a P element.
            // Example:
            //
            // P
            // IMG[class="editor-image-left"]
            // P

            // Find the closest P sibling element to the image element or A element if the image has link.
            closestParagraph = ( imageHasLink ) ? t.getNextSiblingElement( imageElement.parentNode ) : t.getNextSiblingElement( imageElement );
            isSiblingToImageParagraph = closestParagraph && closestParagraph.nodeName === 'P';

            // If there is no next sibling P element, try previous sibling.
            if ( !isSiblingToImageParagraph )
            {
                closestParagraph = ( imageHasLink ) ? t.getPrevSiblingElement( imageElement.parentNode ) : t.getPrevSiblingElement( imageElement )
            }

            // If there are any P sibling elements before or after the image element,
            // prepend the IMG element as a first child to the P element.

            if ( closestParagraph && closestParagraph.nodeName === 'P' )
            {
                var elementToClone = null;

                if ( imageHasLink )
                {
                    elementToClone = imageElement.parentNode;
                    t.prependChild( closestParagraph, elementToClone.cloneNode( true ) );
                }
                else
                {
                    elementToClone = imageElement;
                    t.prependChild( closestParagraph, elementToClone.cloneNode( false ) );
                }

                dom.remove( elementToClone, false );
            }
            else
            {
                // Fallback
                var serializer = new tinymce.dom.Serializer( ed.settings, ed.dom );
                var elementAsString = ( imageHasLink ) ? serializer.serialize( imageElement.parentNode ) : serializer.serialize( imageElement );
                var elementToRemove = ( imageHasLink ) ? imageElement.parentNode : imageElement;

                ed.selection.setContent('<p>' + elementAsString + '<br/></p>');

                dom.remove(elementToRemove, false);
            }
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        blockImage: function( ed, imageElement )
        {
            var dom = ed.dom;
            var parentParagraphToImage = dom.getParent( imageElement, 'p' );
            var imageHasLink = imageElement.parentNode.nodeName === 'A';
            var isImageBlockAligned, isImageCenterAligned;
            var elementToClone;

            // Remove styles added by TinyMCE
            dom.setStyle( parentParagraphToImage, 'textAlign', '' );
            dom.setStyle( imageElement, 'display', '' );
            dom.setStyle( imageElement, 'marginLeft', '' );
            dom.setStyle( imageElement, 'marginRight', '' );

            // Remove CMS classes from image
            dom.removeClass( imageElement, 'editor-img-left' );
            dom.removeClass( imageElement, 'editor-img-right' );


            isImageBlockAligned = parentParagraphToImage && parentParagraphToImage.nodeName === 'P' && dom.hasClass(parentParagraphToImage, 'editor-p-block');

            if ( isImageBlockAligned )
            {
                return;
            }

            isImageCenterAligned = parentParagraphToImage && parentParagraphToImage.nodeName === 'P' && dom.hasClass(parentParagraphToImage, 'editor-p-center');

            if ( isImageCenterAligned )
            {
                dom.removeClass( parentParagraphToImage, 'editor-p-center' );
                dom.addClass( parentParagraphToImage, 'editor-p-block' );
                return;
            }

            // Image is left or right aligned.


            var parentElementToImage = dom.getParent(imageElement, dom.isBlock);

            var newParagraphForImage;

            if ( !parentElementToImage || parentElementToImage.childNodes.length > 1 )
            {
                newParagraphForImage = dom.create('p', {'class': 'editor-p-block'});

                if ( imageHasLink )
                {
                    elementToClone = imageElement.parentNode;
                    newParagraphForImage.appendChild( elementToClone.cloneNode(true) );
                }
                else
                {
                    elementToClone = imageElement;
                    newParagraphForImage.appendChild( elementToClone.cloneNode(false));
                }


                parentElementToImage.parentNode.insertBefore( newParagraphForImage, parentElementToImage );

                dom.remove( elementToClone );

                imageElement = newParagraphForImage.firstChild;
                parentElementToImage = newParagraphForImage;
            }
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        centerImage: function( ed, imageElement )
        {
            // We know that TinyMCE wraps P element around the IMG when justify/align equals center.
            // Do not create it!
            // TODO: Linked image

            var t = this, dom = ed.dom;

            // Assume image is always in a P element.
            var initialParagraphForImage = dom.getParent( imageElement, 'p' );

            var imageHasLink = imageElement.parentNode.nodeName === 'A';

            // Remove styles added by TinyMCE
            dom.setStyle( initialParagraphForImage, 'textAlign', '' );
            dom.setStyle( imageElement, 'display', '' );
            dom.setStyle( imageElement, 'marginLeft', '' );
            dom.setStyle( imageElement, 'marginRight', '' );

            // Remove CMS classes from image
            dom.removeClass( imageElement, 'editor-img-left' );
            dom.removeClass( imageElement, 'editor-img-right' );

            var isImageAlreadyCenterAligned = initialParagraphForImage && dom.hasClass( initialParagraphForImage, 'editor-p-center' )
            if ( isImageAlreadyCenterAligned )
            {
                return;
            }

            var isImageBlockAligned = initialParagraphForImage && dom.hasClass( initialParagraphForImage, 'editor-p-block' );
            if ( isImageBlockAligned )
            {
                dom.removeClass( initialParagraphForImage, 'editor-p-block' );
                dom.addClass( initialParagraphForImage, 'editor-p-center' );
                return;
            }

            // Image is left or right aligned.


            var newPElement = dom.create( 'p', { 'class': 'editor-p-center' } );
            var imageClone = imageElement.cloneNode( true );
            dom.add( newPElement, imageClone );

            var previousElementToParagraph = t.getPrevSiblingElement( initialParagraphForImage );
            if ( previousElementToParagraph && previousElementToParagraph.nodeName === 'P' )
            {
                dom.insertAfter( newPElement, previousElementToParagraph );
            }
            else
            {
                var nextSiblingElementToParagraph = t.getNextSiblingElement( initialParagraphForImage );
                initialParagraphForImage.parentNode.insertBefore( newPElement, initialParagraphForImage );
            }

            dom.remove( imageElement );

        },

        getPrevSiblingElement: function ( element )
        {
            var sibling = ( element ) ? element.previousSibling : null;

            if ( sibling )
            {
                while ( sibling && sibling.nodeType != 1 )
                {
                    sibling = sibling.previousSibling;
                }
            }

            return sibling;
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        getNextSiblingElement: function ( element )
        {
            var sibling = ( element ) ? element.nextSibling : null;

            if ( sibling )
            {
                while ( sibling && sibling.nodeType != 1 )
                {
                    sibling = sibling.nextSibling;
                }
            }

            return sibling;
        },

        prependChild: function ( parentElement, elementToPrepend )
        {
            parentElement.insertBefore( elementToPrepend, parentElement.firstChild );
        },

        // ---------------------------------------------------------------------------------------------------------------------------------

        removeCmsCssFromImage: function( element, ed )
        {
            var dom = ed.dom;
            dom.removeClass( element, 'editor-image-left' );
            dom.removeClass( element, 'editor-image-right' );
        }

        /*******************************************************************************************************************************
        *** END: Alignment prototype routines
        *******************************************************************************************************************************/

	});

	// Register plugin
	tinymce.PluginManager.add('cmsimage', tinymce.plugins.CMSImagePlugin);
})();
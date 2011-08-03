/*
    Attachment in img/@src 1
    External: _attachment/$contentKey
    Internal: attachment://$contentKey

    Attachment in img/@src 2
    External: _attachment/$contentKey/binary/$binaryKey
    Internal: attachment://$contentKey/binary/$binaryKey

    Image in img/@src
    External: _image/$contentKey?filter=$filter&size=$size&format=$format
    Internal: image://$contentKey?filter=$filter&size=$size&format=$format
*/

(function()
{
    var reImgSrcWithSizeParamEQCustomPattern = /^_image\/.+(_size=custom)/im;
    var reFilterParamPattern = /([\?&]_filter(?:=[^&]*)?)/im;

    var reExternalImgSrcPattern = /^_image\/(.+)/im;
    var reExternalImgSrcReplacePattern = 'image://$1';

    var reInternalImgSrcPattern = /^image:\/\/(.+)/im;
    var reInternalImgSrcReplacePattern = '_image/$1';

    var reInternalImgSrcAttachmentPattern = /^attachment:\/\/(.*?)/im;
    var reInternalImgSrcAttachmentReplacePattern = '_attachment/$1';

    var reExternalImgSrcAttachmentPattern = /^_attachment\/(.*?)/im;
    var reExternalImgSrcAttachmentReplacePattern = 'attachment://$1';

    tinymce.create('tinymce.plugins.InternalLinkPlugin', {

        init: function( ed, url )
        {
            var t = this;

            ed.onSetContent.add( function( ed, o )
            {
                t.transformURLsToExternalFormat(ed, o);
            });

            ed.onPreProcess.add(function( ed, o )
            {
                if ( o.get )
                {
                    t.transformURLsToInternalFormat(ed, o);
                }
            });
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        // Public

        /*
            Method: transformURLsToExternalFormat
        */
        transformURLsToExternalFormat: function( oEditor, oOptions )
        {
            var t = this;
            var oDOM = oEditor.dom;
            var oImgElements = oDOM.select('img', oOptions.node);

            tinymce.each( oImgElements, function( oImgElement )
            {
                var sImgSrc = oDOM.getAttrib(oImgElement, 'src');

                // TODO: Refactor to function
                if ( sImgSrc.match(reInternalImgSrcPattern) )
                {
                    if ( !t._isCustomSize(sImgSrc) )
                    {
                        if ( !sImgSrc.match(reFilterParamPattern) )
                        {
                            var sFilterParam = t.resolveFilterParam(sImgSrc, oEditor);

                            if ( sFilterParam !== '' )
                            {
                                sImgSrc = sImgSrc + '&_filter=' + sFilterParam;
                            }
                        }
                    }

                    sImgSrc = sImgSrc.replace(reInternalImgSrcPattern, reInternalImgSrcReplacePattern);
                }

                // TODO: Refactor to function
                if ( sImgSrc.match(reInternalImgSrcAttachmentPattern) )
                {
                    sImgSrc = sImgSrc.replace(reInternalImgSrcAttachmentPattern, reInternalImgSrcAttachmentReplacePattern);
                }

                oDOM.setAttrib(oImgElement, 'src', sImgSrc);
            });
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        /*
            Method: transformURLsToInternalFormat
        */
        transformURLsToInternalFormat: function( oEditor, oOptions  )
        {
            var t = this;
            var oDOM = oEditor.dom;
            var oImgElements = oDOM.select('img', oOptions.node);

            tinymce.each( oImgElements, function( oImgElement )
            {
                var sImgSrc = oDOM.getAttrib(oImgElement, 'src');

                // TODO: Refactor to function
                if ( sImgSrc.match(reExternalImgSrcPattern) )
                {
                    // Remove filter param for non custom sizes.
                    if ( !t._isCustomSize(sImgSrc) )
                    {
                        sImgSrc = sImgSrc.replace(reFilterParamPattern, '');
                    }

                    sImgSrc = sImgSrc.replace(reExternalImgSrcPattern, reExternalImgSrcReplacePattern);
                }

                // TODO: Refactor to function
                if ( sImgSrc.match(reExternalImgSrcAttachmentPattern) )
                {
                    sImgSrc = sImgSrc.replace(reExternalImgSrcAttachmentPattern, reExternalImgSrcAttachmentReplacePattern);
                }

                oDOM.setAttrib(oImgElement, 'src', sImgSrc);
            });
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        /*
            function: resolveFilterParam
        */
        resolveFilterParam: function( sImageSrc, oEditor, iCustomWidth )
        {
            var t = this;

            var sFilterString = '';

            var iEditorWidth = t._getWidthForEditorInstance(oEditor);
            var iFortyPercentOfEditorWidth = Math.round((iEditorWidth * 40 / 100));
            var iTwentyFivePercentOfEditorWidth = Math.round((iEditorWidth * 25 / 100));
            var iFifteenPercentOfEditorWidth = Math.round((iEditorWidth * 15 / 100));
            var iHeightForScaleWideFormat = Math.round((iEditorWidth * 0.42));

            if ( sImageSrc.match(/_size\=full/i ) )
            {
                sFilterString = 'scalewidth(' + iEditorWidth + ')';
            }
            else if ( sImageSrc.match(/_size\=wide/i ) )
            {
                sFilterString = 'scalewide(' + iEditorWidth + ',' + iHeightForScaleWideFormat + ')';
            }
            else if ( sImageSrc.match(/_size\=regular/i) )
            {
                sFilterString = 'scalewidth(' + iFortyPercentOfEditorWidth + ')';
            }
            else if ( sImageSrc.match(/_size\=square/i) )
            {
                sFilterString = 'scalesquare(' + iFortyPercentOfEditorWidth + ')';
            }
            else if ( sImageSrc.match(/_size\=list/i) )
            {
                sFilterString = 'scalesquare(' + iTwentyFivePercentOfEditorWidth + ')';
            }
            else if ( sImageSrc.match(/_size\=thumbnail/i) )
            {
                sFilterString = 'scalesquare(' + iFifteenPercentOfEditorWidth + ')';
            }                                                                                   // TODO: Use \d+
            else if ( sImageSrc.match(/_size\=custom/i) && (iCustomWidth && iCustomWidth.match(/\d/)) )
            {
                sFilterString = 'scalewidth(' + iCustomWidth + ')';
            }
            else
            {
                sFilterString = '';
            }

            return sFilterString;
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        /*
            Function: getInfo
        */
        getInfo: function()
        {
            return {
                longname : 'Internal Link Plugin',
                author : 'tan@enonic.com',
                authorurl : 'http://www.enonic.com',
                infourl : 'http://www.enonic.com',
                version : '0.1'
            };
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        // Private

        /*
            function: _getWidthForEditorInstance
        */
        _getWidthForEditorInstance: function( oEditor )
        {
            var t = this;

            var oBodyElemStyleMarginAndPadding = t._getBodyStyleMarginAndPaddingForEditorInstance( oEditor );

            var iBodyMarginLeft     = oBodyElemStyleMarginAndPadding.marginleft;
            var iBodyMarginRight    = oBodyElemStyleMarginAndPadding.marginright;
            var iBodyPaddingLeft    = oBodyElemStyleMarginAndPadding.paddingleft;
            var iBodyPaddingRight   = oBodyElemStyleMarginAndPadding.paddingright;

            var iContentAreaWidth   = oEditor.settings.initial_width - 2; // Initial width - gui left and right border.

            return ( iContentAreaWidth - ( iBodyMarginLeft + iBodyPaddingLeft ) - ( iBodyMarginRight + iBodyPaddingRight ) );
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        /*
            Function: _getBodyStylePaddingForEditorInstance
        */
        _getBodyStyleMarginAndPaddingForEditorInstance: function( oEditor )
        {
            var oDOM = oEditor.dom;

            var oEditorBodyElement = oEditor.getBody();

            var margintop, marginright, marginbottom ,marginleft,
                    paddingtop, paddingright, paddingbottom ,paddingleft;

            margintop       = parseInt(oDOM.getStyle(oEditorBodyElement, 'margin-top', true))       || 0;
            marginright     = parseInt(oDOM.getStyle(oEditorBodyElement, 'margin-right', true))     || 0;
            marginbottom    = parseInt(oDOM.getStyle(oEditorBodyElement, 'margin-nottom', true))    || 0;
            marginleft      = parseInt(oDOM.getStyle(oEditorBodyElement, 'margin-left', true))      || 0;

            paddingtop      = parseInt(oDOM.getStyle(oEditorBodyElement, 'padding-top', true))      || 0;
            paddingright    = parseInt(oDOM.getStyle(oEditorBodyElement, 'padding-right', true))    || 0;
            paddingbottom   = parseInt(oDOM.getStyle(oEditorBodyElement, 'padding-bottom', true))   || 0;
            paddingleft     = parseInt(oDOM.getStyle(oEditorBodyElement, 'padding-left', true))     || 0;

            return { 'margintop': margintop, 'marginright': marginright, 'marginbottom': marginbottom, 'marginleft': marginleft,
                'paddingtop': paddingtop, 'paddingright': paddingright, 'paddingbottom': paddingbottom, 'paddingleft': paddingleft };
        },
        // ---------------------------------------------------------------------------------------------------------------------------------

        /*
            Function: _isCustomSize
        */
        _isCustomSize: function( sImageSrc )
        {
            return sImageSrc.match(reImgSrcWithSizeParamEQCustomPattern);
        }
        // ---------------------------------------------------------------------------------------------------------------------------------
    });

    tinymce.PluginManager.add('internallinkplugin', tinymce.plugins.InternalLinkPlugin);
})();
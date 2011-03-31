/*
    Method and functions for the insert image dialog.
*/

var CMSImage = {

    /*
        Method: init
    */
    init: function()
    {
        var oSelectedNode = ed.selection.getNode();
        var oDOM = ed.dom;

        createCustomWidthSlider();

        if ( oSelectedNode.nodeName.toLowerCase() !== 'img')
        {
            return;
        }

        var oForm = document.forms['formAdmin'];

        var sSrc = oDOM.getAttrib(oSelectedNode, 'src');
        var sId = oDOM.getAttrib(oSelectedNode, 'id');
        var sName = oDOM.getAttrib(oSelectedNode, 'name');
        var sAlt = oDOM.getAttrib(oSelectedNode, 'alt');
        var sTitle = oDOM.getAttrib(oSelectedNode, 'title');
        var sLongdesc = oDOM.getAttrib(oSelectedNode, 'longdesc');
        var sUsemap = oDOM.getAttrib(oSelectedNode, 'usemap');
        var sIsmap = oDOM.getAttrib(oSelectedNode, 'ismap');
        var sAlign = oDOM.getAttrib(oSelectedNode, 'align');
        var sStyleFloat = oDOM.getStyle(oSelectedNode, 'float');

        var sHspace = oDOM.getAttrib(oSelectedNode, 'hspace');
        var sVspace = oDOM.getAttrib(oSelectedNode, 'vspace');
        var sBorder = oDOM.getAttrib(oSelectedNode, 'border');
        var sClass = oDOM.getAttrib(oSelectedNode, 'class');

        // Load the values to the form elements.

        CMSImage.updateCustomWidthValue(sSrc);
        CMSImage.updateSizeValue(sSrc);

        var oCheckBox = document.getElementById('checkbox1');
        var oAltTextElement = document.getElementsByName('alt')[0];
        var oTitleTextElement = document.getElementsByName('title')[0];

        oAltTextElement.value = sAlt;
        oTitleTextElement.value = sTitle;

        if ( sAlt === sTitle )
        {
            oCheckBox.checked = true;
            CMSImage.copyAltValueToTitleValue(oCheckBox, oAltTextElement, oTitleTextElement );
        }

        if ( sBorder )
            oForm.border.value = parseInt(sBorder);

        if ( sAlign )
            oForm.align.value = sAlign;

        if ( sStyleFloat )
            oForm.stylefloat.value = sStyleFloat;

        if ( sClass )
            oForm.cssclass.value = sClass;

        oForm.id.value = sId;
        oForm.name.value = sName;
        oForm.longdesc.value = sLongdesc;
        oForm.usemap.value = sUsemap;
        oForm.ismap.value = sIsmap;
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    /*
        Method: insert
    */
    insert: function()
    {
        tinyMCEPopup.restoreSelection();

        var oSelectedNode = ed.selection.getNode();
        var oDOM = ed.dom;

        var oForm = document.forms['formAdmin'];

        var sSrc = this.getImageSrcString();
        var sId = oForm.id.value;
        var sName = oForm.name.value;
        var sAltText = oForm.alt.value;
        var sTitleText = oForm.title.value;
        var sLongdesc = oForm.longdesc.value;
        var sUsemap = oForm.usemap.value;
        var sIsmap = oForm.ismap.value;

        var oSizeField = document.getElementsByName('size')[0];
        var sSize = oSizeField.options[oSizeField.selectedIndex].value;

        // var sBorder = oForm.border.value;

        if ( sAltText === '' )
        {
            alert(cmslang.sysMsgRequiredFields);
            oForm.alt.focus();
            return;
        }

        if ( sSize === '' )
        {
            alert(cmslang.sysMsgRequiredFields);
            oSizeField.focus();
            return;
        }

        var sHTML = '';

        var bIsSelectedNodeEmpty = oSelectedNode.innerHTML.test(/^(\s|<br\s*\/?>|&nbsp;)*$/);

        var bWrapInsertedImageWithPElement = oSelectedNode && !bIsSelectedNodeEmpty || ed.getContent() === '';

        if ( bWrapInsertedImageWithPElement )
            sHTML += '<p id="__cms" class="editor-p-block">';

        sHTML += '<img src="' + sSrc + '"';
        if ( sAltText != '' )
            sHTML += ' alt="' + sAltText + '"';

        if ( sTitleText != '' )
            sHTML += ' title="' + sTitleText + '"';

        // Needed for Gecko. Is cleaned up later by TinyMCE.
        sHTML += ' _moz_dirty=""';

        // Not supported by the EVS GUI, but is added in case they exist.
        if ( sId != '' )
            sHTML += ' id="' + sId + '"';
        if ( sName != '' )
            sHTML += ' name="' + sName + '"';
        if ( sLongdesc != '' )
            sHTML += ' longdesc="' + sLongdesc + '"';
        if ( sUsemap != '' )
            sHTML += ' usemap="' + sUsemap + '"';
        if ( sIsmap != '' )
            sHTML += ' usemap="' + sIsmap + '"';

        sHTML += '/>';

        var bIsEditorEmpty = ed.getContent() === '';

        if ( bWrapInsertedImageWithPElement )
        {
            sHTML += '</p>';
            if ( bIsEditorEmpty )
                sHTML += '<p>&nbsp;</p>';
        }

        var bIsSelectedNodeAnEmptyPElement = oSelectedNode && oSelectedNode.nodeName === 'P' && oSelectedNode.innerHTML.test(/^(\s|<br\s*\/?>|&nbsp;)*$/);

        if ( bIsSelectedNodeAnEmptyPElement )
        {
            oDOM.addClass(oSelectedNode, 'editor-p-block');
        }

        tinyMCEPopup.execCommand("mceInsertContent", false, sHTML);
        tinyMCEPopup.execCommand("mceCleanup");

        var oPElemWrapper = oDOM.get('__cms');
        
        oDOM.setAttrib(oPElemWrapper, 'id', null);

        var isGecko = window.parent.opener.tinymce.isGecko;

        if ( isGecko )
        {
            if ( oPElemWrapper )
            {
                var oPreviousElementToPWrapper = ed.plugins.cmsimage.getPrevSiblingElement(oPElemWrapper);
                var oNextElementToPWrapper = ed.plugins.cmsimage.getNextSiblingElement(oPElemWrapper);
                var oNextNextElementToPWrapper = ed.plugins.cmsimage.getNextSiblingElement(oNextElementToPWrapper);

                if ( oPreviousElementToPWrapper && oPreviousElementToPWrapper.nodeName === 'P' && oPreviousElementToPWrapper.innerHTML == '<br>' )
                {
                    oDOM.remove(oPreviousElementToPWrapper);
                }

                if ( oNextElementToPWrapper && oNextElementToPWrapper.nodeName === 'P' && oNextElementToPWrapper.innerHTML == '<br>' )
                {
                    oDOM.remove(oNextElementToPWrapper);
                }

                if ( oNextNextElementToPWrapper && oNextNextElementToPWrapper.nodeName === 'P' && oNextNextElementToPWrapper.innerHTML == '<br>' )
                {
                    oDOM.remove(oNextNextElementToPWrapper);
                }
            }
        }


        tinyMCEPopup.close();

        /*
        if ( bIsEditorEmpty )
        {
            tinyMCE.activeEditor.selection.select(oDOM.select('p[id=_selectme]'));
            tinyMCE.activeEditor.selection.collapse();
        }
        */
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    /*
        Method: updateSizeValue
    */
    updateSizeValue: function( sImageSrc )
    {
        if ( sImageSrc === '' ) return;

        var oSizeElem = document.getElementById('size');
        var oOptionElems = oSizeElem.getElementsByTagName('option');
        var sSizeUrlParam = getParameterInUrl('_size', sImageSrc);

        var oOptionElem;

        for ( var i = 0; i < oOptionElems.length; i++ )
        {
            oOptionElem = oOptionElems[i];
            if ( oOptionElem.value === sSizeUrlParam )
            {
                oOptionElem.selected = true;
            }
        }

        this.changeSizeAction(sSizeUrlParam);
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    /*
        Method: updateCustomWidthValue
    */
    updateCustomWidthValue: function( sImageSrc )
    {
        var sSizeParam = getParameterInUrl('_size', sImageSrc);

        if ( sSizeParam === 'custom' )
        {
            var oCustomWidthElem = document.getElementById('customwidth');
            var sFilterParam = getParameterInUrl('_filter', sImageSrc);

            oCustomWidthElem.value = sFilterParam.match(/\d+/g)[0];
        }
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    /*
        Method: updatePreviewImage
    */
    uWait : 0,

    updatePreviewImage: function()
    {
        var sImageSrc = this.getImageSrcString();

        var oPreviewImageElem = document.getElementById('preview-image');
        var oPreviewLoaderMsgElem = document.getElementById('preview-loader-message');

        if (sImageSrc !== '')
        {
            oPreviewImageElem.style.display = 'inline';
            oPreviewLoaderMsgElem.style.display = 'block';

            oPreviewImageElem.src = sImageSrc;

            oPreviewImageElem.onload = function()
            {
                oPreviewLoaderMsgElem.style.display = 'none';
            };
        }
        else
        {
            oPreviewImageElem.style.display = 'none';
            oPreviewLoaderMsgElem.style.display = 'none';
        }
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    /*
        Method: changeSizeAction
    */
    changeSizeAction: function( sLabel )
    {
        var oCustomWidthWrapperElem = document.getElementById('custom-width-properties-container');
        var oCustomWidthElem = document.getElementById('customwidth');

        this.updateTextWrapValue( sLabel );

        if ( sLabel === 'custom' )
        {
            oCustomWidthWrapperElem.style.display = '';
            g_oCustomWidthSlider.set(oCustomWidthElem.value);
        }
        else
            oCustomWidthWrapperElem.style.display = 'none';

        this.updatePreviewImage();
    },
    // --------------------------------------------------------------------------------------------------------------------

    /*
        Function: updateTextWrapValue
    */
    updateTextWrapValue: function( sLabel )
    {
        /*
        var oTextWrapElement = document.getElementById('text-wrap');

        if ( sLabel == 'full' || sLabel == 'wide' )
        {
            oTextWrapElement.disabled = true;
        }
        else
        {
            oTextWrapElement.disabled = false;
        }

        oTextWrapElement.selectedIndex = 1;
        */
    },

    /*
        Function: getImageSrcString
    */
    getImageSrcString: function()
    {
        var sImgSrc = '';

        var oInternalLinkPlugin = tinyMCEPopup.editor.plugins.internallinkplugin;

        var iContentKey = document.getElementById('selectedcontentkey').value;
        var sBinaryName = document.getElementById('selectedbinaryname').value;
        var sFileExtension = getFileExtension(sBinaryName);
        var oSizeField = document.getElementsByName('size')[0];
        var sSize = oSizeField.options[oSizeField.selectedIndex].value;
        var iCustomWidthValue = document.getElementById('customwidth').value;

        // Gif is not supported, use PNG
        if ( sFileExtension === 'gif' ) sFileExtension = 'png';

        if ( sSize !== '')
        {
            sImgSrc += '_image/' + iContentKey + '?_size=' + sSize + '&_format=' + sFileExtension;

            var sFilter = oInternalLinkPlugin.resolveFilterParam(sImgSrc, ed, iCustomWidthValue);

            if ( sFilter !== '' )
                sImgSrc += '&_filter=' + sFilter;
        }

        return sImgSrc;
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    /*
        Function: getEditorBodyRectangle
    */
    getEditorBodyRectangle: function()
    {
        return ed.dom.getRect(ed.getBody());
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    /*
        Function: getEditorBodyStylePadding
    */
    getEditorBodyStylePadding: function()
    {
        var t, r, b ,l;

        t = ed.dom.getStyle(ed.getBody(), 'padding-top', true);
        r = ed.dom.getStyle(ed.getBody(), 'padding-right', true);
        b = ed.dom.getStyle(ed.getBody(), 'padding-bottom', true);
        l = ed.dom.getStyle(ed.getBody(), 'padding-left', true);

        return { 'top': t, 'right': r, 'bottom': b, 'left': l };
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    /*
        Method: copyAltValueToTitleValue
    */
    copyAltValueToTitleValue: function( oCheckBox, oAltTextElem, oTitleTextElem )
    {
        if ( oCheckBox.checked )
        {
            oTitleTextElem.value = oAltTextElem.value;
        }
        else
            oTitleTextElem.value = '';

    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    /*
        Method: cancel
    */
    cancel: function()
    {
        history.back();
    }
    // -------------------------------------------------------------------------------------------------------------------------------------
};

function getFileExtension( sFilename )
{
    var oExtension = (/[.]/.exec(sFilename)) ? /[^.]+$/.exec(sFilename) : undefined;
    return (oExtension) ? oExtension.toString().toLowerCase() : '';
}

function addValidationOnKeyUpToMarginTextField( el )
{
    el.onkeyup = function()
    {
        if ( !this.value.match(/^(\d|-)?(\d|\.)*\.?\d*$/) )
        {
            this.style.borderColor = '#f00';
        }
        else
        {
            this.style.borderColor = '';
        }
    };
}

function restrictInt( oElement )
{
    if ( !/^\d*$/.test(oElement.value) )
    {
        oElement.value = oElement.value.replace(/[^\d]/g,"");
    }

    if ( parseInt(oElement.value) === '' )
    {
        oElement.value = 1;
    }

}

function updateUnitSelector( sUnit, sElementNameToUpdate )
{
    var oForm = document.forms['formAdmin'];

    if ( !oForm[sElementNameToUpdate] )
        return;

    if ( sUnit.indexOf('em') > -1 )
    {
        oForm[sElementNameToUpdate].selectedIndex = 1;
    }
    else if ( sUnit.indexOf('ex') > -1 )
    {
        oForm[sElementNameToUpdate].selectedIndex = 2;
    }
    else if ( sUnit.indexOf('%') > -1 )
    {
        oForm[sElementNameToUpdate].selectedIndex = 3;
    }
    else
    {
        oForm[sElementNameToUpdate].selectedIndex = 0;
    }
}

function getParameterInUrl( sParamName, sUrl )
{
    if ( sUrl.indexOf('?') > -1 )
    {
        var params = sUrl.split('?');
        var param = params[1].split('&');
        for ( var i = 0; i < param.length; i++ )
        {
            var pair = param[i].split('=');
            if ( pair[0] == sParamName )
            {
                return pair[1];
            }
        }
    }
    return null;
}

/******************************************************************************************************************************************/
// The tinyMCEPopup object is designed for standalone documents, not framesets.
// so we have to load that object in the frameset document rather than this.
// To avoid crashes in IE we call the init method from this document.

var tinyMCEPopup = window.parent.tinyMCEPopup;
window.parent.tinyMCEPopup.init();
var ed = tinyMCEPopup.editor;

/******************************************************************************************************************************************/

var g_oCustomWidthSlider;
function createCustomWidthSlider()
{
    // IE has problems with creating a slider if the slider element is inside an element that is hidden using CSS.
    // We need to display it when creating a new slider.
    $('custom-width-properties-container').setStyle('display','block');

    var oCustomWidthSliderElement = $('custom-width-slider');
    var oCustomWidthValueElement = $('customwidth');
    var oKnobElement = oCustomWidthSliderElement.getElement('.knob');

    g_oCustomWidthSlider = new Slider(oCustomWidthSliderElement, oKnobElement, {
        steps: 1000,
        range: [1],
        onChange: function( value )
        {
            oCustomWidthValueElement.value = value;
        },
        onTick: function( pos )
        {
            this.knob.setStyle('left', pos);
        },
        onComplete: function( step )
        {
            CMSImage.updatePreviewImage();
        }
    });

    $('custom-width-properties-container').setStyle('display','none');
}

document.onkeypress = function( e )
{
    var e = e || event; // IE
    if ( e.keyCode == 27 )
        tinyMCEPopup.close();
};

cmsOnLoadChain(CMSImage.init);
var CMSImage = {

    /*
        Method: init
    */
    init: function()
    {
        var selectedNode = ed.selection.getNode();
        var dom = ed.dom;

        createCustomWidthSlider();

        if ( selectedNode.nodeName.toLowerCase() !== 'img')
        {
            return;
        }

        var form = document.forms['formAdmin'];

        // Get attributes from the selected image.
        var src = dom.getAttrib(selectedNode, 'src');
        var id = dom.getAttrib(selectedNode, 'id');
        var name = dom.getAttrib(selectedNode, 'name');
        var alt = dom.getAttrib(selectedNode, 'alt');
        var title = dom.getAttrib(selectedNode, 'title');
        var longDesc = dom.getAttrib(selectedNode, 'longdesc');
        var useMap = dom.getAttrib(selectedNode, 'usemap');
        var ismap = dom.getAttrib(selectedNode, 'ismap');
        var align = dom.getAttrib(selectedNode, 'align');
        var styleFloat = dom.getStyle(selectedNode, 'float');

        var hSpace = dom.getAttrib(selectedNode, 'hspace');
        var vSpace = dom.getAttrib(selectedNode, 'vspace');
        var border = dom.getAttrib(selectedNode, 'border');
        var cssClass = dom.getAttrib(selectedNode, 'class');

        // Load the values to the form.
        CMSImage.updateCustomWidthValue(src);
        CMSImage.updateSizeValue(src);

        var copyAltValueToTitleValueCheckBox = document.getElementById('checkbox1');
        var altTextInput = document.getElementsByName('alt')[0];
        var titleTextInput = document.getElementsByName('title')[0];

        altTextInput.value = alt;
        titleTextInput.value = title;

        if ( alt === title )
        {
            copyAltValueToTitleValueCheckBox.checked = true;
            CMSImage.copyAltValueToTitleValue(copyAltValueToTitleValueCheckBox, altTextInput, titleTextInput );
        }

        if ( border )
            form.border.value = parseInt(border);

        if ( align )
            form.align.value = align;

        if ( styleFloat )
            form.stylefloat.value = styleFloat;

        if ( cssClass )
            form.cssclass.value = cssClass;

        form.id.value = id;
        form.name.value = name;
        form.longdesc.value = longDesc;
        form.usemap.value = useMap;
        form.ismap.value = ismap;
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

    /*
        Method: insert
    */
    insert: function()
    {
        tinyMCEPopup.restoreSelection();

        var isIE = window.parent.opener.tinymce.isIE;
        var isGecko = window.parent.opener.tinymce.isGecko;
        var selectedNode = ed.selection.getNode();
        var dom = ed.dom;
        var form = document.forms['formAdmin'];

        var src = this.getImageSrcString();
        var id = form.id.value;
        var name = form.name.value;
        var altText = form.alt.value;
        var titleText = form.title.value;
        var longDesc = form.longdesc.value;
        var useMap = form.usemap.value;
        var ismap = form.ismap.value;
        var sizeInput = document.getElementsByName('size')[0];
        var size = sizeInput.options[sizeInput.selectedIndex].value;

        if ( altText === '' )
        {
            alert(cmslang.sysMsgRequiredFields);
            form.alt.focus();
            return;
        }

        if ( size === '' )
        {
            alert(cmslang.sysMsgRequiredFields);
            sizeInput.focus();
            return;
        }

        var html = '';

        var selectedNodeIsEmpty = selectedNode.innerHTML.test(/^(\s|<br\s*\/?>|&nbsp;)*$/);
        var wrapImageInPElement = selectedNode && !selectedNodeIsEmpty || ed.getContent() === '';

        if ( wrapImageInPElement )
            html += '<p id="__cms" class="editor-p-block">';

        html += '<img src="' + src + '"';
        if ( altText != '' )
            html += ' alt="' + altText + '"';

        if ( titleText != '' )
            html += ' title="' + titleText + '"';

        // Needed for Gecko. Is cleaned up later by TinyMCE.
        html += ' _moz_dirty=""';

        // Not supported by the EVS GUI, but is added in case they exist.
        if ( id != '' )
            html += ' id="' + id + '"';
        if ( name != '' )
            html += ' name="' + name + '"';
        if ( longDesc != '' )
            html += ' longdesc="' + longDesc + '"';
        if ( useMap != '' )
            html += ' usemap="' + useMap + '"';
        if ( ismap != '' )
            html += ' usemap="' + ismap + '"';

        html += '/>';

        var isEditorEmpty = ed.getContent() === '';

        if ( wrapImageInPElement )
        {
            html += '</p>';
            if ( isEditorEmpty )
                html += '<p>&nbsp;</p>';
        }

        var addCmsCssClassToPElement = selectedNode && selectedNode.nodeName === 'P' && selectedNode.innerHTML.test(/^(\s|<br\s*\/?>|&nbsp;)*$/);
        if ( addCmsCssClassToPElement )
        {
            dom.addClass(selectedNode, 'editor-p-block');
        }

        // Seems like IE's caret position get's lost using mceInsertRawHTML when the image has a link.
        if ( isIE && selectedNode.parentNode.nodeName.toLowerCase() === 'a' )
        {
            ed.selection.select(selectedNode.parentNode);
        }

        tinyMCEPopup.execCommand('mceInsertRawHTML',false, html);
        tinyMCEPopup.execCommand("mceCleanup");

        var pElemWrapper = dom.get('__cms');
        dom.setAttrib(pElemWrapper, 'id', null);

        if ( isGecko )
        {
            if ( pElemWrapper )
            {
                var previousElementToPWrapper = ed.plugins.cmsimage.getPrevSiblingElement(pElemWrapper);
                var nextElementToPWrapper = ed.plugins.cmsimage.getNextSiblingElement(pElemWrapper);
                var nextNextElementToPWrapper = ed.plugins.cmsimage.getNextSiblingElement(nextElementToPWrapper);

                if ( previousElementToPWrapper && previousElementToPWrapper.nodeName === 'P' && previousElementToPWrapper.innerHTML == '<br>' )
                {
                    dom.remove(previousElementToPWrapper);
                }

                if ( nextElementToPWrapper && nextElementToPWrapper.nodeName === 'P' && nextElementToPWrapper.innerHTML == '<br>' )
                {
                    dom.remove(nextElementToPWrapper);
                }

                if ( nextNextElementToPWrapper && nextNextElementToPWrapper.nodeName === 'P' && nextNextElementToPWrapper.innerHTML == '<br>' )
                {
                    dom.remove(nextNextElementToPWrapper);
                }
            }
        }


        tinyMCEPopup.close();
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

    changeSizeAction: function( sLabel )
    {
        var oCustomWidthWrapperElem = document.getElementById('custom-width-properties-container');
        var oCustomWidthElem = document.getElementById('customwidth');

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

    getEditorBodyRectangle: function()
    {
        return ed.dom.getRect(ed.getBody());
    },
    // -------------------------------------------------------------------------------------------------------------------------------------

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
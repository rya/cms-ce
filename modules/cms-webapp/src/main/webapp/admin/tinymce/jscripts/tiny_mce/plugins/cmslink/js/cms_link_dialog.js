var templates = {
	"window.open" : "window.open('${url}','${target}','${options}')"
};

var CMSLinkDialog = {

    init : function()
    {
        tinyMCEPopup.restoreSelection();

        var ed = tinyMCEPopup.editor;

        var dom = ed.dom;

        var selection = ed.selection;

        var selectedNode = selection.getNode();

        var selectedNodeIsLink = dom.getParent( selectedNode, 'A' ) !== null;

        var selectedNodeIsImageWithLink = selectedNode.nodeName == 'IMG' && selectedNodeIsLink;

        var linkTextContainer = document.getElementById( 'link-text-container' );

        var moreThanOneBlockIsSelected = selection.getSelectedBlocks().length > 1;

        var selectionHasLink = selectedNode && selectedNodeIsLink || selectedNodeIsImageWithLink;

        var showLinkTextField = !moreThanOneBlockIsSelected && selectedNode.nodeName !== 'IMG' ||
                ( !moreThanOneBlockIsSelected && selectedNode.nodeName !== 'IMG' && selectedNodeIsLink );

        if ( selectedNodeIsImageWithLink )
        {
            selectedNode = selectedNode.parentNode;
        }

        if ( showLinkTextField )
        {
            linkTextContainer.style.display = '';

            var selectedText = selection.getContent( { 'format' : 'text' } );
            if (  selectedNodeIsLink )
            {
                selectedText = selectedNode.innerHTML;
            }

            document.getElementById('link-text').value = selectedText;
        }

        if ( selectionHasLink )
        {
            updateInsertButtonText( cmslang.cmdUpdateLink );
        }

        this.updateFormElements( selectedNode );
    },

    updateFormElements : function( selectedNode )
    {
        var ed = tinyMCEPopup.editor;
        var dom = ed.dom;
        var hrefVal = dom.getAttrib( selectedNode, 'href' );
        var titleVal =  dom.getAttrib( selectedNode, 'title' );
        var relVal = dom.getAttrib( selectedNode, 'rel' );
        var linkType = getLinkType( hrefVal );
        var linkTypeUrlField = 'input-' + linkType;
        var targetVal = dom.getAttrib( selectedNode, 'target' );

        this.onChangeLinkType( linkType );

        if ( hrefVal.indexOf('download=true') > -1 )
            targetVal = 'download';

        setFormElemValue( 'targetlist', targetVal );
        setFormElemValue( 'title', titleVal );
        setFormElemValue( 'rel', relVal );

        var selectedIsUrlOrMailLink = linkTypeUrlField === 'input-standard' || linkTypeUrlField === 'input-mail';
        var selectedIsAnchor = linkTypeUrlField === 'input-anchor';

        this.populateAnchorList( hrefVal );

        if ( selectedIsUrlOrMailLink )
        {
            setFormElemValue( linkTypeUrlField, hrefVal );
        }
        else
        {
            setPickerFieldValues( linkTypeUrlField, hrefVal, titleVal );
        }
    },

    onChangeLinkType : function( linkType )
    {
        this.handleTargetForTypeFile( linkType )

        var linkTypeSelectElem = document.getElementById( 'link-type' );
        var urlFieldContainers = document.getElementById( 'url-field-containers').getElementsByTagName( 'tr' );
        var urlFieldToDisplay = document.getElementById( linkType + '-field-container' );

        linkTypeSelectElem.value = linkType;

        var i, urlFieldContainer;
        for ( i = 0; i < urlFieldContainers.length; i++ )
        {
            urlFieldContainer = urlFieldContainers[i];

            if ( urlFieldContainer.id.indexOf( '-field-container' ) > -1 )
            {
                urlFieldContainer.style.display = ( urlFieldContainer === urlFieldToDisplay ) ? '' : 'none';
            }
        }

    },

    handleTargetForTypeFile: function( linkType )
    {
        var targetList = document.getElementById('targetlist');
        var selected = targetList.selectedIndex;

        removeAllDropdownOptions( targetList );

        addDropdownOption( targetList, cmslang.optOpenExistingWindow, '' );

        if ( linkType !== 'file' )
        {
            addDropdownOption( targetList, cmslang.optURLOpenNewWindow, '_blank' );
        }

        if ( linkType === 'file' )
        {
            addDropdownOption( targetList, cmslang.optDownloadFile, 'download' );
        }

        targetList.selectedIndex = selected;
    },

    insertLinkAction : function()
    {
        var ed = tinyMCEPopup.editor;
        var selection = ed.selection;
        var linkType = getLinkTypeValue();
        var linkTypeIsMail = linkType === 'mail';
        var hrefVal = getFormElementValue( 'input-' + linkType );
        var selectedNode = selection.getNode();
        var selectedText = selection.getContent( { 'format' : 'text' } );
        var selectedNodeIsImage = selectedNode.nodeName === 'IMG';

        // Validate url
        if ( !this.validateUrl( hrefVal ) )
        {
            alert( cmslang.errInvalidLink );
            document.getElementsByName( 'input-' + linkType )[0].focus();
            return;
        }

        var mailToSchemePattern = /^mailto:/i.test( hrefVal );
        if (  linkTypeIsMail && !mailToSchemePattern )
        {
            hrefVal = 'mailto:' + hrefVal;
        }

        // Start inserting link to the document

        tinyMCEPopup.execCommand("mceBeginUndoLevel");

        var linkTextInputElement = document.getElementById( 'link-text' );

        var selectedHyperlinkElement = ed.dom.getParent( selectedNode, 'A' );

        var selectedNodeIsNotHyperLinkAndNoTextIsSelected = selectedNodeIsImage === false && selectedHyperlinkElement == null && selectedText.length === 0;

        if ( selectedNodeIsNotHyperLinkAndNoTextIsSelected )
        {
            var linkTextToSet;
            if ( linkTextInputElement.value === '' )
            {
                linkTextToSet = hrefVal;
            }

            selection.setContent('<a href="javascript:mctmp(0);" id="_cms_temp_link">' + linkTextToSet + '</a>');

            selectedHyperlinkElement = ed.dom.select('#_cms_temp_link')[0];
        }

        if ( selectedHyperlinkElement == null )
        {
            ed.getDoc().execCommand( "unlink", false, null );
            tinyMCEPopup.execCommand( "CreateLink", false, "#mce_temp_url#", {skip_undo : 1} );

            var elementArray = tinymce.grep( ed.dom.select( "a" ), function( n )
            {
                return ed.dom.getAttrib( n, 'href' ) == '#mce_temp_url#';
            } );

            var i;
            for ( i = 0; i < elementArray.length; i++ )
            {
                setAllAttribs( selectedHyperlinkElement = elementArray[i] );

                if ( linkTextInputElement.value !== '' )
                {
                    selectedHyperlinkElement.innerHTML = linkTextInputElement.value;
                }
            }
        }
        else
        {
            setAllAttribs( selectedHyperlinkElement );

            if ( !selectedNodeIsImage )
            {
                selectedHyperlinkElement.innerHTML = linkTextInputElement.value;
            }
        }

        // Don't move caret if selection was image
        if ( selectedHyperlinkElement.childNodes.length != 1 || selectedHyperlinkElement.firstChild.nodeName != 'IMG' )
        {
            ed.focus();
            ed.selection.select( selectedHyperlinkElement );
            ed.selection.collapse( 0 );
            tinyMCEPopup.storeSelection();
        }

        tinyMCEPopup.execCommand("mceEndUndoLevel");
        tinyMCEPopup.close();
    },

    populateAnchorList : function( selectedLinkHrefValue )
    {
        document.getElementById( 'anchorlistcontainer' ).innerHTML = getAnchorListHTML( selectedLinkHrefValue );
    },

    openPickerWindow : function( url, width, height )
    {
        var w = ( width ) ? width : 990;
        var h = ( height ) ? height : 620;
        var leftPos = (screen.width - w) / 2;
        var topPos = (screen.height - h) / 2;
        var pickerWindow = window.open( url, "pickerWindow", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" +
                                                             w + ",height=" + h + ",top=" + topPos + ",left=" + leftPos );
        pickerWindow.focus();
    },

    validateUrl : function( hrefVal )
    {
        return !/^(http:\/\/||mailto:)$/.test( hrefVal );
    }
};

function setAllAttribs( elm ) {
	var formObj = document.forms[0];
    var selectedLinkType = getLinkTypeValue();
    var href = getFormElementValue('input-' + selectedLinkType);
	var target = getSelectValue(formObj, 'targetlist');

    var isDownloadFileEqTrue = target === 'download';
    if ( isDownloadFileEqTrue )
    {
        target = '';
        var hrefValueHasParameters = href.indexOf('?') > -1 || href.indexOf('&') > -1;
        href = hrefValueHasParameters ? ( href + '&download=true' ) : ( href + '?download=true' )
    }
    else
    {
       href = href.replace(/(\?|&)download=true/gim, '');
    }

	setAttrib(elm, 'href', href);
	setAttrib(elm, 'title');
	setAttrib(elm, 'target', target == '_self' ? '' : target);
	setAttrib(elm, 'id');
	setAttrib(elm, 'style');
	setAttrib(elm, 'class', getSelectValue(formObj, 'classlist'));
	setAttrib(elm, 'rel');
	setAttrib(elm, 'rev');
	setAttrib(elm, 'charset');
	setAttrib(elm, 'hreflang');
	setAttrib(elm, 'dir');
	setAttrib(elm, 'lang');
	setAttrib(elm, 'tabindex');
	setAttrib(elm, 'accesskey');
	setAttrib(elm, 'type');
	setAttrib(elm, 'onfocus');
	setAttrib(elm, 'onblur');
	setAttrib(elm, 'onclick');
	setAttrib(elm, 'ondblclick');
	setAttrib(elm, 'onmousedown');
	setAttrib(elm, 'onmouseup');
	setAttrib(elm, 'onmouseover');
	setAttrib(elm, 'onmousemove');
	setAttrib(elm, 'onmouseout');
	setAttrib(elm, 'onkeypress');
	setAttrib(elm, 'onkeydown');
	setAttrib(elm, 'onkeyup');

	// Refresh in old MSIE
	if (tinyMCE.isMSIE5)
		elm.outerHTML = elm.outerHTML;
}

function setAttrib(elm, attrib, value) {
	var formObj = document.forms[0];
	var valueElm = formObj.elements[attrib.toLowerCase()];
	var dom = tinyMCEPopup.editor.dom;

	if (typeof(value) == "undefined" || value == null) {
		value = "";

		if (valueElm)
			value = valueElm.value;
	}

	// Clean up the style
	if (attrib == 'style')
		value = dom.serializeStyle(dom.parseStyle(value));

	dom.setAttrib(elm, attrib, value);
}


function getLinkType( uri )
{
    var linkType;

    if ( /^attachment:\/\//i.test( uri ) )
    {
        linkType = 'file';
    }
    else if ( /^page:\/\//i.test( uri ) )
    {
        linkType = 'page';
    }
    else if ( /^content:\/\//i.test( uri ) )
    {
        linkType = 'content';
    }
    else if ( /^mailto:/i.test( uri ) )
    {
        linkType = 'mail';
    }
    else if ( /^#.+/i.test( uri ) )
    {
        linkType = 'anchor';
    }
    else
    {
        linkType = 'standard';
    }

    return linkType;
}

function setPickerFieldValues( inputName, cmsInternalUrl, contentTitle )
{
    var key = getKeyFromInternalUrl(cmsInternalUrl);
    var altTextFieldElem = document.getElementById('title');

    if ( key === null ) return;

    if ( altTextFieldElem.value !== '' )
    {
        altTextFieldElem.value = contentTitle;
    }

    if ( inputName === 'input-page' )
    {
        AjaxService.getPagePath( key, function( pagePath )
        {
            var path = cmslang.sites + '/' + pagePath;
            document.getElementById( 'view' + inputName ).value = path;
            document.getElementById( 'view' + inputName ).title = path;
            document.getElementById( inputName ).value = cmsInternalUrl;
        } );
    }
    else
    {
        AjaxService.getContentPath( key, function( contentPath )
        {
            document.getElementById( 'view' + inputName ).value = contentPath;
            document.getElementById( 'view' + inputName ).title = contentPath;
            document.getElementById( inputName ).value = cmsInternalUrl;
        } );
    }
}

function setFormElemValue( inputName, value )
{
    var val = value;
    var isMailtoLink = /^mailto:/i.test(val);

    if ( inputName === 'input-standard' && val === '' )
    {
        val = 'http://';
    }

    if ( isMailtoLink )
    {
        val = val.replace(/^mailto:(.+)/, '$1');

        /*validate.js*/
        val = window.opener.tinymce.trim(val);

        if ( val === '' )
        {
            val = 'mailto:';
        }
    }

    document.forms['formAdmin'].elements[inputName].value = val;
}

function getFormElementValue( inputName )
{
    return document.forms['formAdmin'][inputName].value;
}

function getKeyFromInternalUrl( url )
{
    try
    {
        return url.match( /\d+/ )[0];
    }
    catch( e )
    {
        return null;
    }
}

function getLinkTypeValue()
{
    return document.forms['formAdmin']['link-type'].value;
}

function addDropdownOption( dropdown, labelText, value )
{
    dropdown.options[dropdown.options.length] = new Option( labelText, value );
}

function removeAllDropdownOptions( dropdown )
{
    if ( dropdown === null )
        return;

    while ( dropdown.hasChildNodes() )
    {
        dropdown.removeChild( dropdown.firstChild );
    }
}

function getAnchorListHTML( selectedLinkHrefValue )
{
    var html = '';
    var ed = tinyMCEPopup.editor;
    var nodes = ed.dom.select('a.mceItemAnchor,img.mceItemAnchor');
    var name, i;

    html += '<select id="input-anchor" name="input-anchor" class="mceAnchorList" o2nfocus="tinyMCE.addSelectAccessibility(event, this, window);" onchange="document.forms[0][\'input-standard\'].value=';
    html += 'this.options[this.selectedIndex].value;">';
    html += '<option value="">---</option>';

    for ( i = 0; i < nodes.length; i++ )
    {
        if ( ( name = ed.dom.getAttrib( nodes[i], "name" ) ) !== '' )  {
            html += '<option value="#' + name + '"';
            if ( selectedLinkHrefValue && ( '#' + name === selectedLinkHrefValue ) )
            {
                html += ' selected="true"';
            }
            html += '>' + name + '</option>';
        }
    }
    html += '</select>';

    return html;
}

function updateInsertButtonText( text )
{
    document.getElementById( 'insert' ).value = text;
}

window.onload = function() {
    CMSLinkDialog.init();
};
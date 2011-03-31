/*
    Class: Editor

    See also:

    <TinyMCE>
    <CmsUtil>
*/
function Editor( id )
{

    this.id = id;           
    this.cmsutil = new CMSUtil();
    // -------------------------------------------------------------------------------------------------------------------------------------

    /*
        Method: create

        Wrapper, creates a new editor instance.
    */
    this.create = function( oConfig )
    {
        if ( oConfig )
        {
            var oEditor = new tinymce.Editor(this.id, oConfig);
            oEditor.render();
        }
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    /*
        Method: createOverlayForDisabledMode

        Create a overlay for shading when the editor is disabled.
    */
    this.createOverlayForDisabledMode = function()
    {
        var editor = tinyMCE.get(this.id);
        var editorIframeElem = document.getElementById(this.id + '_ifr');
        var editorBodyElem = editor.getBody();
        var editorRectangle = editor.dom.getRect(editor.getContentAreaContainer());
        var widthForOverlayElem = editorRectangle.w + 20;
        var heightForOverlayElem = editorRectangle.h;
        var overlayElem = editor.getDoc().createElement('div');
        
        editorBodyElem.style.overflow = 'hidden';

        editorIframeElem.scrolling = 'no';
        editorIframeElem.scrollbar = 'no';
        editorIframeElem.style.overflow = 'hidden';

        overlayElem.style.backgroundColor = '#fff';
        overlayElem.style.position = 'absolute';
        overlayElem.style.width = widthForOverlayElem + 'px';
        overlayElem.style.height = heightForOverlayElem + 'px';
        overlayElem.style.left = '0';
        overlayElem.style.top = '0';
        overlayElem.setAttribute('mce_bogus', '1');

        this.cmsutil.setOpacity(overlayElem, .5);

        editorBodyElem.appendChild(overlayElem);
    };
}
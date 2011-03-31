tinyMCEPopup.requireLangPack();
tinyMCEPopup.onInit.add(onLoadInit);

var g_codemirror;

function onLoadInit()
{
    tinyMCEPopup.resizeToInnerSize();

    document.getElementById('htmlSource').value = blockElementsWithNewlines(tinyMCEPopup.editor.getContent());

    resizeInputs();

    g_codemirror = CodeMirror.fromTextArea('htmlSource', {
        lineNumbers: true,
        textWrapping: true,
        path: "../../../../../codemirror/js/",
        tabMode: 'shift',
        indentUnit: 2,
        parserfile: ["parsexml.js", "parsecss.js", "tokenizejavascript.js", "parsejavascript.js", "parsehtmlmixed.js"],
        stylesheet: ["../../../../../codemirror/css/cms.xmlcolors.css", "../../../../../codemirror/css/cms.jscolors.css", "../../../../../codemirror/css/cms.csscolors.css"],
        parserConfig: { useHTMLKludges: true },
        reindentOnLoad: true
    });
}

function blockElementsWithNewlines( content )
{
    var contentWithNewlines = content;

    // P
    contentWithNewlines     = contentWithNewlines.replace(/(<p(?:\s+[^>]*)?>)/gim, '$1\n');
    contentWithNewlines     = contentWithNewlines.replace(/<\/p>/gim, '\n</p>');

    // H1-6
    contentWithNewlines     = contentWithNewlines.replace(/(<h[1-6].*?>)/gim, '$1\n');
    contentWithNewlines     = contentWithNewlines.replace(/(<\/h[1-6].*?>)/gim, '\n$1');

    return contentWithNewlines;
}

function saveContent()
{
    tinyMCEPopup.editor.setContent(g_codemirror.getCode());
    tinyMCEPopup.close();
}

var wHeight = 0, wWidth = 0, owHeight = 0, owWidth = 0;

function resizeInputs()
{
    var el = document.getElementsByTagName('iframe')[0] || document.getElementById('htmlSource');

    if ( !el )
        return;

    if ( !tinymce.isIE )
    {
        wHeight = self.innerHeight - 65;
        //wWidth = self.innerWidth - 16;
    }
    else
    {
        wHeight = document.body.clientHeight - 70;
        //wWidth = document.body.clientWidth - 16;
    }

    el.style.height = Math.abs(wHeight) + 'px';
    //el.style.width = Math.abs(wWidth) + 'px';
}
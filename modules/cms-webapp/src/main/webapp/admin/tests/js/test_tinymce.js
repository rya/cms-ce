module("TinyMCE");

var editor;

function getBaseUrl()
{
    var url = window.location.href;
    var positionOfTinyMCE = url.lastIndexOf( 'test_tinymce' );

    return url.substring( 0, positionOfTinyMCE );
}

function clickElement(elem)
{
    var e = document.createEvent('MouseEvents');
    e.initEvent( 'click', true, true );
    elem.dispatchEvent(e);
}

test( 'internal links plug-in', function()
{
    expect( 3 );

    editor.setContent( '<img src="_image/247?_size=regular&_format=jpg&_filter=scalewidth(234)" alt="Salta" title="Salta" />' );
    equals(editor.getContent(), '<p><img title="Salta" src="image://247?_size=regular&amp;_format=jpg" alt="Salta" /></p>');

    editor.setContent( '<img src="image://247?_size=regular&_format=jpg" alt="Fjell - Salta" />' );
    equals(editor.getContent(), '<p><img src="image://247?_size=regular&amp;_format=jpg" alt="Fjell - Salta" /></p>');

    editor.setContent( '<img src="_attachment/64177" alt="Fjell - Salta" />' );
    equals(editor.getContent(), '<p><img src="attachment://64177" alt="Fjell - Salta" /></p>');
});

test( 'test IFRAME element always has content', function()
{
    expect( 2 );

    editor.setContent( '<iframe title="YouTube video player" width="480" height="390" src="http://www.youtube.com/embed/L4_Ue2lrwTQ" frameborder="0" allowfullscreen></iframe>' );
    equals(editor.getContent(), '<p><iframe frameborder="0" height="390" src="http://www.youtube.com/embed/L4_Ue2lrwTQ" title="YouTube video player" width="480">cms_content</iframe></p>');

    editor.setContent( '<iframe title="YouTube video player" width="480" height="390" src="http://www.youtube.com/embed/L4_Ue2lrwTQ" frameborder="0" allowfullscreen>Some content</iframe>' );
    equals(editor.getContent(), '<p><iframe frameborder="0" height="390" src="http://www.youtube.com/embed/L4_Ue2lrwTQ" title="YouTube video player" width="480">Some content</iframe></p>');
});

test( 'form elements', function()
{
    expect( 2 );

    var textarea = '<textarea id="textarea_id" name="textarea_name">Content</textarea>';

    var select = '<select id="select_id" name="select="><option value="1">Ost</option><option value="2">Fisk</option><option value="3">Sjøstjerne</option></select>';

    editor.setContent( textarea );
    equals(editor.getContent(), '<p><textarea id="textarea_id" name="textarea_name">Content</textarea></p>');

    editor.setContent( select );
    equals(editor.getContent(), '<p><select id="select_id" name="select="><option value="1">Ost</option><option value="2">Fisk</option><option value="3">Sj&oslash;stjerne</option></select></p>');
});

test( 'image align basic', function()
{
    expect( 4 );

    var content = '<p class="editor-p-block"><img src="test-image.jpg" alt="" /></p>';
    editor.setContent( content );

    editor.selection.select( editor.dom.select( 'img' )[0] );

    editor.execCommand( 'JustifyLeft' );
    equals( editor.getContent(), '<p><img class="editor-image-left" src="test-image.jpg" alt="" /></p>' );

    editor.selection.select( editor.dom.select( 'img' )[0] );

    editor.execCommand( 'JustifyCenter' );
    equals( editor.getContent(), '<p class="editor-p-center"><img src="test-image.jpg" alt="" /></p>' );

    editor.selection.select( editor.dom.select( 'img' )[0] );

    editor.execCommand( 'JustifyRight' );
    equals( editor.getContent(), '<p><img class="editor-image-right" src="test-image.jpg" alt="" /></p>' );

    editor.selection.select( editor.dom.select( 'img' )[0] );

    editor.execCommand( 'JustifyFull' );
    equals( editor.getContent(), '<p class="editor-p-block"><img src="test-image.jpg" alt="" /></p>' );

} );

test( 'image align with text block below image', function()
{
    expect( 4 );

    var content = '<p class="editor-p-block"><img src="test-image.jpg" alt="" /></p>';
    content += '<p>Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus. </p>';

    editor.setContent( content );
    editor.selection.select(editor.dom.select('img')[0]);
    editor.execCommand('JustifyLeft');
    equals(editor.getContent(), '<p><img class="editor-image-left" src="test-image.jpg" alt="" />Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus.</p>');

    editor.setContent( content );
    editor.selection.select(editor.dom.select('img')[0]);
    editor.execCommand('JustifyCenter');
    equals(editor.getContent(), '<p class="editor-p-center"><img src="test-image.jpg" alt="" /></p>\n<p>Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus.</p>');

    editor.setContent( content );
    editor.selection.select(editor.dom.select('img')[0]);
    editor.execCommand('JustifyRight');
    equals(editor.getContent(), '<p><img class="editor-image-right" src="test-image.jpg" alt="" />Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus.</p>');

    editor.setContent( content );
    editor.selection.select(editor.dom.select('img')[0]);
    editor.execCommand('JustifyFull');
    equals(editor.getContent(), '<p class="editor-p-block"><img src="test-image.jpg" alt="" /></p>\n<p>Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus.</p>');
});

test( 'image align with text block above image', function()
{
    expect( 4 );

    var content = '<p>Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus. </p>';
    content += '<p class="editor-p-block"><img src="test-image.jpg" alt="" /></p>';

    editor.setContent( content );
    editor.selection.select( editor.dom.select( 'img' )[0] );
    editor.execCommand( 'JustifyLeft' );
    equals( editor.getContent(), '<p><img class="editor-image-left" src="test-image.jpg" alt="" />Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus.</p>' );

    editor.setContent( content );
    editor.selection.select( editor.dom.select( 'img' )[0] );
    editor.execCommand( 'JustifyCenter' );
    equals( editor.getContent(), '<p>Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus.</p>\n<p class="editor-p-center"><img src="test-image.jpg" alt="" /></p>' );

    editor.setContent( content );
    editor.selection.select( editor.dom.select( 'img' )[0] );
    editor.execCommand( 'JustifyRight' );
    equals( editor.getContent(), '<p><img class="editor-image-right" src="test-image.jpg" alt="" />Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus.</p>' );

    editor.setContent( content );
    editor.selection.select( editor.dom.select( 'img' )[0] );
    editor.execCommand( 'JustifyFull' );
    equals( editor.getContent(), '<p>Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus.</p>\n<p class="editor-p-block"><img src="test-image.jpg" alt="" /></p>' );
} );

test( 'image align with image between two text blocks', function()
{
    expect( 4 );

    var content = '';
    content += '<p>Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus. </p>';
    content += '<p class="editor-p-block"><img src="test-image.jpg" alt="" /></p>';
    content += '<p>Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus. </p>';

    editor.setContent( content );
    editor.selection.select( editor.dom.select( 'img' )[0] );
    editor.execCommand( 'JustifyLeft' );
    equals( editor.getContent(), '<p>Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus.</p>\n<p><img class="editor-image-left" src="test-image.jpg" alt="" />Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus.</p>' );

    editor.setContent( content );
    editor.selection.select( editor.dom.select( 'img' )[0] );
    editor.execCommand( 'JustifyRight' );
    equals( editor.getContent(), '<p>Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus.</p>\n<p><img class="editor-image-right" src="test-image.jpg" alt="" />Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus.</p>' );

    editor.setContent( content );
    editor.selection.select( editor.dom.select( 'img' )[0] );
    editor.execCommand( 'JustifyFull' );
    equals( editor.getContent(), '<p>Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus.</p>\n<p class="editor-p-block"><img src="test-image.jpg" alt="" /></p>\n<p>Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus.</p>' );

    editor.setContent( content );
    editor.selection.select( editor.dom.select( 'img' )[0] );
    editor.execCommand( 'JustifyCenter' );
    equals( editor.getContent(), '<p>Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus.</p>\n<p class="editor-p-center"><img src="test-image.jpg" alt="" /></p>\n<p>Etiam id leo nulla, a euismod mi. Nullam eleifend venenatis facilisis. Praesent dolor sem, commodo dapibus ultrices sed, vestibulum sed eros. Morbi porttitor pellentesque fermentum. Donec lacinia suscipit justo, nec facilisis tellus vehicula sed. Duis accumsan ligula non arcu fermentum ac porttitor diam viverra. Nam sit amet nibh eu dui rhoncus interdum. Suspendisse gravida magna ut lorem tempor vehicula. Nunc lorem libero, iaculis eu sollicitudin non, volutpat id nibh. Vivamus vitae lorem nec erat dapibus tincidunt vitae at tellus. Ut laoreet tellus gravida libero porttitor ac vestibulum erat facilisis. Praesent urna diam, laoreet vitae euismod at, porta at lorem. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Sed consequat pretium quam, at ullamcorper nunc euismod sed. Vestibulum vel lacus risus.</p>' );
} );

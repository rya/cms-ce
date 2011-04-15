package com.enonic.cms.api.plugin.ext;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class defines the text extractor plugin.
 */
public abstract class TextExtractor
    extends ExtensionBase
{
    /**
     * This method returns true if mime type can be indexed by this plugin.
     *
     * @param mimeType A mime-type.
     * @return <code>true</code> if the implementation can handle file with the given mime-type, <code>false</code> otherwise.
     */
    public abstract boolean canHandle( String mimeType );

    /**
     * Extracts all the text from a given binary document.
     *
     * @param stream An InputStream, connected to the binary document to extract text from.
     * @return The pure text contained in the document.
     * @throws java.io.IOException If there are problems reading the input stream.
     */
    public abstract String extractText( InputStream stream )
        throws IOException;
}

package com.enonic.cms.testtools;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class MockHTTPServer
        extends NanoHTTPD
{

    private static final Logger LOG = Logger.getLogger( "ENONIC." + MockHTTPServer.class.getName() );

    static public String TYPE_PARAM = "type";

    static public String TEXT_TYPE = "text";

    static public String XML_TYPE = "xml";

    private String responseText = "";

    public MockHTTPServer( int port )
            throws IOException
    {
        super( port );
        LOG.info( "Running embedded server on http://localhost:" + port );
    }

    public Response serve( String uri, String method, Properties header, Properties params, Properties files )
    {
        String type = params.getProperty( TYPE_PARAM );
        if ( TEXT_TYPE.equals( type ) )
        {
            return new Response( HTTP_OK, MIME_PLAINTEXT, responseText );
        }
        else
        {
            if ( XML_TYPE.equals( type ) )
            {
                return new Response( HTTP_OK, MIME_XML, responseText );
            }
            else
            {
                return new Response( HTTP_NOTIMPLEMENTED, MIME_PLAINTEXT, "Method is not emplemented" );
            }
        }
    }

    public void setResponseText( String text )
    {
        this.responseText = text;
    }

}

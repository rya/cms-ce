/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class InvalidContentTypeConfigException
    extends RuntimeException
{
    public InvalidContentTypeConfigException( DataEntryConfig input, String message )
    {
        super( buildMessage( input, message ) );
    }

    public InvalidContentTypeConfigException( String message )
    {
        super( message );
    }

    public InvalidContentTypeConfigException( String message, Element el )
    {
        super( message + "\r\nConfig XML:\r\n" + parseElement( el ) );
    }

    private static String buildMessage( DataEntryConfig input, String message )
    {
        return "Invalid input config '" + input.getName() + "': " + message;
    }

    private static String parseElement( Element el )
    {
        if ( el == null )
        {
            return "";
        }
        return JDOMUtil.prettyPrintDocument( new Document( (Element) el.detach() ) );
    }
}

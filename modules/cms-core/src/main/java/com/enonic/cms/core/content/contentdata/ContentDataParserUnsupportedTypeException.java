/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata;

/**
 * Created by rmy - Date: Jun 25, 2009
 */
public class ContentDataParserUnsupportedTypeException
    extends RuntimeException
{
    public ContentDataParserUnsupportedTypeException( String message )
    {
        super( message );
    }
}
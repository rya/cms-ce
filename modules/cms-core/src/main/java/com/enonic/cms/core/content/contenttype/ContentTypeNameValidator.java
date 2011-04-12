/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Jun 9, 2010
 */
public class ContentTypeNameValidator
{
    private static final Pattern pattern = Pattern.compile( "^[a-zA-Z0-9\\.\\-_]+$" );

    public static boolean isValid( String contentTypeName )
    {
        Matcher matcher = pattern.matcher( contentTypeName );
        return matcher.matches();
    }
}

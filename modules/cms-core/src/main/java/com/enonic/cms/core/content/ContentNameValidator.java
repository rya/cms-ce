/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import org.apache.commons.lang.StringUtils;

public class ContentNameValidator
{
    public static final int CONTENT_NAME_MAX_LENGTH = 256;

    private static final String FORBIDDEN_CHARS = "/\\#;";

    public static void validate( String contentName )
    {
        if ( StringUtils.isEmpty( contentName ) )
        {
            throw new ContentNameValidatorException( "Content name cannot be empty" );
        }

        if ( StringUtils.startsWith( contentName, " " ) || StringUtils.endsWith( contentName, " " ) )
        {
            throw new ContentNameValidatorException( "Content name cannot start or end with whitespace" );
        }

        if ( StringUtils.containsAny( contentName, FORBIDDEN_CHARS ) )
        {
            throw new ContentNameValidatorException( "Content name cannot contain any of these characters: " + FORBIDDEN_CHARS );
        }

        if ( contentName.length() > CONTENT_NAME_MAX_LENGTH )
        {
            throw new ContentNameValidatorException(
                "Content name is too long: " + contentName.length() + " . Maximum length is " + CONTENT_NAME_MAX_LENGTH + " characters." );
        }
    }
}

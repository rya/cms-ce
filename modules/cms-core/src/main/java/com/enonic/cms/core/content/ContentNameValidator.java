/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/21/10
 * Time: 9:31 AM
 */
public class ContentNameValidator
{

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


    }

}

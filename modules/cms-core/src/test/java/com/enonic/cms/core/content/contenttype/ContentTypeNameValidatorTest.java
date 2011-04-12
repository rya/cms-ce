/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Jun 9, 2010
 */
public class ContentTypeNameValidatorTest
{
    @Test
    public void isValid()
    {
        assertTrue( ContentTypeNameValidator.isValid( "abc" ) );
        assertTrue( ContentTypeNameValidator.isValid( "Abc" ) );
        assertTrue( ContentTypeNameValidator.isValid( "123Abc" ) );
        assertTrue( ContentTypeNameValidator.isValid( "Abc123" ) );

        assertFalse( ContentTypeNameValidator.isValid( "Spaced name" ) );
        assertFalse( ContentTypeNameValidator.isValid( "Who's" ) );
        assertFalse( ContentTypeNameValidator.isValid( "jvs@enonic.com" ) );
    }
}

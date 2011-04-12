/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.index;

import com.enonic.cms.core.content.index.FieldHelper;
import junit.framework.TestCase;

public class FieldHelperTest
    extends TestCase
{
    public void testTranslateFieldName()
    {
        assertEquals( "a", FieldHelper.translateFieldName("a") );
        assertEquals( "a", FieldHelper.translateFieldName( "@a" ) );
        assertEquals( "a", FieldHelper.translateFieldName( "/a" ) );
        assertEquals( "a", FieldHelper.translateFieldName( "/@a" ) );
        assertEquals( "a#b", FieldHelper.translateFieldName( "a/b" ) );
        assertEquals( "a#b", FieldHelper.translateFieldName( "/a/b/" ) );
        assertEquals( "data#a#b", FieldHelper.translateFieldName( "data/a/b" ) );
        assertEquals( "data#a#b", FieldHelper.translateFieldName( "contentdata/a/b" ) );
        assertEquals( "data#a#b", FieldHelper.translateFieldName( "/data/a/b" ) );
        assertEquals( "data#a#b", FieldHelper.translateFieldName( "/contentdata/a/b" ) );
        assertEquals( "data#a#b", FieldHelper.translateFieldName( "/data/@a/b" ) );
        assertEquals( "data#a#b", FieldHelper.translateFieldName( "/contentdata/a/@b" ) );
        assertEquals( "data#a#b", FieldHelper.translateFieldName( "contentdata.a.b" ) );
        assertEquals( "data#a#b", FieldHelper.translateFieldName( "data.a.b" ) );
    }

    public void testIsUserDefinedField()
    {
        assertFalse( FieldHelper.isUserDefinedField( "a" ) );
        assertFalse( FieldHelper.isUserDefinedField( "@a" ) );
        assertFalse( FieldHelper.isUserDefinedField( "/a" ) );
        assertFalse( FieldHelper.isUserDefinedField( "a/b" ) );
        assertFalse( FieldHelper.isUserDefinedField( "/a/b/" ) );
        assertTrue( FieldHelper.isUserDefinedField( "data/a/b" ) );
        assertTrue( FieldHelper.isUserDefinedField( "contentdata/a/b" ) );
        assertTrue( FieldHelper.isUserDefinedField( "/data/a/b" ) );
        assertTrue( FieldHelper.isUserDefinedField( "/contentdata/a/b" ) );
        assertTrue( FieldHelper.isUserDefinedField( "/data/@a/b" ) );
        assertTrue( FieldHelper.isUserDefinedField( "/contentdata/a/@b" ) );
    }
}

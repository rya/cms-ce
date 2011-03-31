/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.user.field;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.cms.domain.user.Address;

public class UserFieldTypeTest
{
    @Test
    public void testGetName()
    {
        Assert.assertEquals( "first-name", UserFieldType.FIRST_NAME.getName() );
        Assert.assertEquals( "last-name", UserFieldType.LAST_NAME.getName() );
    }

    @Test
    public void testGetTypeClass()
    {
        Assert.assertEquals( String.class, UserFieldType.FIRST_NAME.getTypeClass() );
        Assert.assertEquals( Address.class, UserFieldType.ADDRESS.getTypeClass() );
    }

    @Test
    public void testIsOfType()
    {
        Assert.assertTrue( UserFieldType.FIRST_NAME.isOfType( String.class ) );
        Assert.assertTrue( UserFieldType.BIRTHDAY.isOfType( Date.class ) );
        Assert.assertTrue( UserFieldType.BIRTHDAY.isOfType( java.sql.Date.class ) );
    }

    @Test
    public void testFromName()
    {
        Assert.assertEquals( UserFieldType.FIRST_NAME, UserFieldType.fromName( "first-name" ) );
        Assert.assertNull( UserFieldType.fromName( "FIRST-NAME" ) );
        Assert.assertNull( UserFieldType.fromName( "FIRST_NAME" ) );
        Assert.assertNull( UserFieldType.fromName( "bogus-name" ) );
        Assert.assertEquals( UserFieldType.ADDRESS, UserFieldType.fromName( "address" ) );
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.user.field;

import org.junit.Test;

import junit.framework.Assert;

public class UserFieldTest
{
    @Test
    public void testLegalType()
    {
        UserField field = new UserField( UserFieldType.FIRST_NAME );
        Assert.assertNull( field.getValue() );
        field.setValue( "Ola" );
        Assert.assertEquals( "Ola", field.getValue() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalType()
    {
        UserField field = new UserField( UserFieldType.FIRST_NAME );
        field.setValue( Boolean.TRUE );
    }
}

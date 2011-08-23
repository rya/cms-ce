/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.api.client.model.user;

import org.junit.Assert;
import org.junit.Test;

public class UserInfoTest
{
    @Test
    public void testAddresses()
    {
        Address a1 = new Address();
        Address a2 = new Address();
        Address a3 = new Address();

        UserInfo info = new UserInfo();
        Assert.assertEquals( 0, info.getAddresses().length );
        Assert.assertNull( info.getPrimaryAddress() );

        info.setAddresses( a1 );
        Assert.assertEquals( 1, info.getAddresses().length );
        Assert.assertEquals( a1, info.getPrimaryAddress() );

        info.setAddresses( a3, a2, a1 );
        Assert.assertEquals( 3, info.getAddresses().length );
        Assert.assertEquals( a3, info.getPrimaryAddress() );
    }
}

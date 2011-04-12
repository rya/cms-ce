/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.stringbased;

import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.UrlDataEntryConfig;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Mar 8, 2010
 * Time: 2:58:06 PM
 */
public class UrlDataEntryEqualsTest
    extends AbstractEqualsTest
{

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        UrlDataEntryConfig config = new UrlDataEntryConfig( "test", true, "test", "contentdata/test", 255 );

        return new UrlDataEntry( config, "hello" );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        UrlDataEntryConfig config = new UrlDataEntryConfig( "test", true, "test", "contentdata/test", 255 );
        UrlDataEntryConfig config2 = new UrlDataEntryConfig( "test", true, "test", "contentdata/test2", 255 );
        UrlDataEntryConfig config3 = new UrlDataEntryConfig( "test2", true, "test", "contentdata/test", 255 );
        TextDataEntryConfig config4 = new TextDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new Object[]{new UrlDataEntry( config, "hello2" ), new UrlDataEntry( config2, "hello" ),
            new UrlDataEntry( config3, "hello" ), new TextDataEntry( config4, "hello" )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        UrlDataEntryConfig config = new UrlDataEntryConfig( "test", true, "test", "contentdata/test", 255 );

        return new UrlDataEntry( config, "hello" );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        UrlDataEntryConfig config = new UrlDataEntryConfig( "test", true, "test", "contentdata/test", 255 );

        return new UrlDataEntry( config, "hello" );
    }
}

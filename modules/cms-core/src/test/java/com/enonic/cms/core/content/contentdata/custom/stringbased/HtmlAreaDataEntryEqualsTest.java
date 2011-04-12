/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.stringbased;

import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;
import com.enonic.cms.core.content.contenttype.dataentryconfig.HtmlAreaDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextAreaDataEntryConfig;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Mar 8, 2010
 * Time: 2:31:22 PM
 */
public class HtmlAreaDataEntryEqualsTest
    extends AbstractEqualsTest
{

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        HtmlAreaDataEntryConfig config = new HtmlAreaDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new HtmlAreaDataEntry( config, "hello" );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        HtmlAreaDataEntryConfig config = new HtmlAreaDataEntryConfig( "test", true, "test", "contentdata/test" );
        HtmlAreaDataEntryConfig config2 = new HtmlAreaDataEntryConfig( "test", true, "test", "contentdata/test2" );
        HtmlAreaDataEntryConfig config3 = new HtmlAreaDataEntryConfig( "test2", true, "test", "contentdata/test" );
        TextAreaDataEntryConfig config4 = new TextAreaDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new Object[]{new HtmlAreaDataEntry( config, "hello2" ), new HtmlAreaDataEntry( config2, "hello" ),
            new HtmlAreaDataEntry( config3, "hello" ), new TextAreaDataEntry( config4, "hello" )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        HtmlAreaDataEntryConfig config = new HtmlAreaDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new HtmlAreaDataEntry( config, "hello" );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        HtmlAreaDataEntryConfig config = new HtmlAreaDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new HtmlAreaDataEntry( config, "hello" );
    }


}

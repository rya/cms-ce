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
 * Time: 2:50:15 PM
 */
public class TextAreaDataEntryEqualsTest
    extends AbstractEqualsTest
{

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        TextAreaDataEntryConfig config = new TextAreaDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new TextAreaDataEntry( config, "hello" );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        TextAreaDataEntryConfig config = new TextAreaDataEntryConfig( "test", true, "test", "contentdata/test" );
        TextAreaDataEntryConfig config2 = new TextAreaDataEntryConfig( "test", true, "test", "contentdata/test2" );
        TextAreaDataEntryConfig config3 = new TextAreaDataEntryConfig( "test2", true, "test", "contentdata/test" );
        HtmlAreaDataEntryConfig config4 = new HtmlAreaDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new Object[]{new TextAreaDataEntry( config, "hello2" ), new TextAreaDataEntry( config2, "hello" ),
            new TextAreaDataEntry( config3, "hello" ), new HtmlAreaDataEntry( config4, "hello" )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        TextAreaDataEntryConfig config = new TextAreaDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new TextAreaDataEntry( config, "hello" );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        TextAreaDataEntryConfig config = new TextAreaDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new TextAreaDataEntry( config, "hello" );
    }


}

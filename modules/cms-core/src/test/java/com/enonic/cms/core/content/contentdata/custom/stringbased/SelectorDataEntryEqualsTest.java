/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.stringbased;

import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextAreaDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Mar 9, 2010
 * Time: 9:59:55 AM
 */
public class SelectorDataEntryEqualsTest
    extends AbstractEqualsTest
{

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        TextDataEntryConfig config = new TextDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new SelectorDataEntry( config, "hello" );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        TextDataEntryConfig config = new TextDataEntryConfig( "test", true, "test", "contentdata/test" );
        TextDataEntryConfig config2 = new TextDataEntryConfig( "test", true, "test", "contentdata/test2" );
        TextDataEntryConfig config3 = new TextDataEntryConfig( "test2", true, "test", "contentdata/test" );
        TextAreaDataEntryConfig config4 = new TextAreaDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new Object[]{new SelectorDataEntry( config, "hello2" ), new SelectorDataEntry( config2, "hello" ),
            new SelectorDataEntry( config3, "hello" ), new TextAreaDataEntry( config4, "hello" )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        TextDataEntryConfig config = new TextDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new SelectorDataEntry( config, "hello" );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        TextDataEntryConfig config = new TextDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new SelectorDataEntry( config, "hello" );
    }

}

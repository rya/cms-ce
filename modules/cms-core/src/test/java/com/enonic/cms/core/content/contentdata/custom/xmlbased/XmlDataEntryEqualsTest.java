/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.xmlbased;

import org.junit.Test;

import com.enonic.cms.domain.AbstractEqualsTest;
import com.enonic.cms.core.content.contentdata.custom.stringbased.HtmlAreaDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.HtmlAreaDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.XmlDataEntryConfig;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Mar 9, 2010
 * Time: 2:58:33 PM
 */
public class XmlDataEntryEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        XmlDataEntryConfig config = new XmlDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new XmlDataEntry( config, "<hello>test</hello>" );
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        XmlDataEntryConfig config = new XmlDataEntryConfig( "test", true, "test", "contentdata/test" );
        XmlDataEntryConfig config2 = new XmlDataEntryConfig( "test", true, "test", "contentdata/test2" );
        XmlDataEntryConfig config3 = new XmlDataEntryConfig( "test2", true, "test", "contentdata/test" );
        HtmlAreaDataEntryConfig config4 = new HtmlAreaDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new Object[]{new XmlDataEntry( config, "<hello>test2</hello>" ), new XmlDataEntry( config2, "<hello>test</hello>" ),
            new XmlDataEntry( config3, "<hello>test</hello>" ), new HtmlAreaDataEntry( config4, "<hello>test</hello>" )};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        XmlDataEntryConfig config = new XmlDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new XmlDataEntry( config, "<hello>test</hello>" );
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        XmlDataEntryConfig config = new XmlDataEntryConfig( "test", true, "test", "contentdata/test" );

        return new XmlDataEntry( config, "<hello>test</hello>" );
    }
}
/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.support;

import java.util.List;

import org.jdom.Element;

import com.enonic.cms.core.content.contentdata.custom.KeywordsDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;


public class KeywordsDataEntryParser
{
    public KeywordsDataEntry parse( final Element element, final DataEntryConfig inputConfig )
    {
        final KeywordsDataEntry entry = new KeywordsDataEntry( inputConfig );
        for ( final Element keywordEl : (List<Element>) element.getChildren( "keyword" ) )
        {
            final String keyword = keywordEl.getText();
            entry.addKeyword( keyword );
        }
        return entry;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.support;

import com.enonic.cms.core.content.ContentHandlerName;
import org.jdom.Element;

import com.enonic.cms.core.content.contentdata.ContentDataXPathCreator;
import com.enonic.cms.core.content.contentdata.custom.BinaryDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;


public class BinaryDataEntryXmlCreator
{
    private BinaryDummyKeyResolver binaryDummyKeyResolver;

    public BinaryDataEntryXmlCreator( BinaryDummyKeyResolver binaryDummyKeyResolver )
    {
        this.binaryDummyKeyResolver = binaryDummyKeyResolver;
    }

    public void createAndAddElement( Element parentEl, BinaryDataEntry binaryDataEntry, boolean inBlockGroup )
    {
        final DataEntryConfig config = binaryDataEntry.getConfig();
        Element entryEl =
            ContentDataXPathCreator.ensurePath( parentEl, stripContentdataWhenNotBlockGroup( config.getRelativeXPath(), inBlockGroup ) );

        final ContentTypeConfig contentTypeConfig = config.getSetConfig().getContentTypeConfig();
        if ( contentTypeConfig.getContentHandlerName() == ContentHandlerName.FILE )
        {
            applyKey( binaryDataEntry, entryEl );
        }
        else
        {
            Element binarydataEl = new Element( "binarydata" );
            entryEl.addContent( binarydataEl );
            applyKey( binaryDataEntry, binarydataEl );
        }

    }

    private void applyKey( BinaryDataEntry binaryDataEntry, Element binarydataEl )
    {
        if ( binaryDataEntry.hasExistingBinaryKey() )
        {
            binarydataEl.setAttribute( "key", binaryDataEntry.getExistingBinaryKeyAsString() );
        }
        else
        {
            binarydataEl.setAttribute( "key", "%" + ( binaryDummyKeyResolver.getNew() ) );
        }
    }

    public interface BinaryDummyKeyResolver
    {
        int getNew();
    }

    private String stripContentdataWhenNotBlockGroup( String xpath, boolean inBlockGroup )
    {
        if ( !inBlockGroup && xpath.startsWith( "contentdata/" ) )
        {
            return xpath.substring( "contentdata/".length() );
        }
        else if ( !inBlockGroup && xpath.startsWith( "/contentdata/" ) )
        {
            return xpath.substring( "/contentdata/".length() );
        }
        return xpath;
    }
}

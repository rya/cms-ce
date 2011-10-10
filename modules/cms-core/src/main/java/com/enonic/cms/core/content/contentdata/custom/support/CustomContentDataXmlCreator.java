/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.support;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.core.content.contentdata.ContentDataXPathCreator;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.DataEntry;
import com.enonic.cms.core.content.contentdata.custom.GroupDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.CtySet;
import com.enonic.cms.core.content.contenttype.CtySetConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class CustomContentDataXmlCreator
{
    private FileDataEntryXmlCreator fileDataEntryXmlCreator = new FileDataEntryXmlCreator();

    private int binaryCount = 0;

    private BinaryDataEntryXmlCreator binaryDataEntryXmlCreator =
        new BinaryDataEntryXmlCreator( new BinaryDataEntryXmlCreator.BinaryDummyKeyResolver()
        {
            public int getNew()
            {
                return binaryCount++;
            }
        } );


    public static Document createContentDataDocument( CustomContentData contentData )
    {
        return new Document( createContentDataElement( contentData ) );
    }

    public static Element createContentDataElement( CustomContentData contentData )
    {
        CustomContentDataXmlCreator xmlCreator = new CustomContentDataXmlCreator();
        return xmlCreator.createElement( contentData );
    }

    public Element createElement( CustomContentData contentData )
    {

        Element contentdataEl = new Element( "contentdata" );

        doAddToContentData( contentdataEl, contentData );

        return contentdataEl;
    }

    private void doAddToContentData( Element el, CustomContentData contentData )
    {
        ContentTypeConfig contentTypeConfig = contentData.getContentTypeConfig();
        List<CtySetConfig> setConfigs = contentTypeConfig.getForm().getSetConfig();

        for ( CtySetConfig setConfig : setConfigs )
        {
            // the set/block/group has its own xpath
            if ( setConfig.getGroupXPath() != null )
            {
                List<GroupDataEntry> groupDataSets = contentData.getGroupDataSets( setConfig.getName() );
                for ( GroupDataEntry groupEntrySet : groupDataSets )
                {
                    CtySet groupSetConfig = groupEntrySet.getConfig();
                    final String xpath = stripContentdata( groupSetConfig.getRelativeXPath() );
                    Element groupEl = ContentDataXPathCreator.createNewPath( el, xpath );
                    doCreateEntryElements( groupSetConfig.getInputConfigs(), groupEntrySet, groupEl );
                }
            }
            else
            {
                List<DataEntryConfig> inputConfigs = setConfig.getInputConfigs();
                doCreateEntryElements( inputConfigs, contentData, el );
            }

        }
    }

    private void doCreateEntryElements( List<DataEntryConfig> inputConfigs, GroupDataEntry groupEntrySet, Element groupEl )
    {
        DataEntryXmlCreator dataEntryXmlCreator = new DataEntryXmlCreator( fileDataEntryXmlCreator, binaryDataEntryXmlCreator, true );
        for ( DataEntryConfig dataEntryConfig : inputConfigs )
        {
            String name = dataEntryConfig.getName();
            DataEntry dataEntry = groupEntrySet.getEntry( name );
            if ( dataEntry != null && dataEntry.hasValue() )
            {
                dataEntryXmlCreator.createEntryElement( groupEl, dataEntry );
            }
            else
            {
                final Element el = ContentDataXPathCreator.ensurePath( groupEl, dataEntryConfig.getRelativeXPath() );
                el.setAttribute( "has-value", "false" );
            }
        }
    }

    private void doCreateEntryElements( List<DataEntryConfig> inputConfigs, CustomContentData contentData, Element contentdataEl )
    {
        DataEntryXmlCreator dataEntryXmlCreator = new DataEntryXmlCreator( fileDataEntryXmlCreator, binaryDataEntryXmlCreator, false );
        for ( DataEntryConfig dataEntryConfig : inputConfigs )
        {
            String name = dataEntryConfig.getName();
            DataEntry dataEntry = contentData.getEntry( name );
            if ( dataEntry != null && dataEntry.hasValue() )
            {
                dataEntryXmlCreator.createEntryElement( contentdataEl, dataEntry );
            }
            else
            {
                final Element el =
                    ContentDataXPathCreator.ensurePath( contentdataEl, stripContentdata( dataEntryConfig.getRelativeXPath() ) );
                el.setAttribute( "has-value", "false" );
            }
        }
    }

    private String stripContentdata( String xpath )
    {
        if ( xpath.startsWith( "contentdata/" ) )
        {
            return xpath.substring( "contentdata/".length() );
        }
        else if ( xpath.startsWith( "/contentdata/" ) )
        {
            return xpath.substring( "/contentdata/".length() );
        }
        return xpath;
    }
}

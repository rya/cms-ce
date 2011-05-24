package com.enonic.cms.business.client;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.api.client.model.content.image.ImageContentDataInput;

import com.enonic.cms.domain.content.contentdata.ContentData;
import com.enonic.cms.domain.content.contentdata.legacy.LegacyImageContentData;

public class ImageContentdataResolver
{
    public ContentData resolveContentdata( ImageContentDataInput imageContentDataInput )
     {
        Document xml = buildXml( imageContentDataInput );
        return new LegacyImageContentData( xml );
    }

    private Document buildXml( ImageContentDataInput input )
    {
        Element contentDataEl = new Element( "contentdata" );
        contentDataEl.addContent( new Element( "name" ).setText( input.name.getValue() ) );
        if ( input.description != null )
        {
            contentDataEl.addContent( new Element( "description" ).setText( input.description.getValue() ) );
        }
        contentDataEl.addContent( new Element( "filesize" ).setText( String.valueOf( input.binary.getBinarySize() ) ) );

        final Element binarydataEl = new Element( "binarydata" );
        if ( input.binary.hasExistingBinaryKey() )
        {
            binarydataEl.setAttribute( "key", input.binary.getExistingBinaryKey().toString() );
        }
        else
        {
            binarydataEl.setAttribute( "key", "%0" );
        }
        contentDataEl.addContent( binarydataEl );

        Element keywords = new Element( "keywords" );
        if ( input.keywords != null )
        {
            for ( String keyword : input.keywords.getKeywords() )
            {
                Element keywordEl = new Element( "keyword" ).setText( keyword );
                keywords.addContent( keywordEl );
            }
        }
        contentDataEl.addContent( keywords );

        Element sourceimageEl = new Element( "sourceimage" );
        Element binaryImageEl = new Element( "binarydata" );
        if ( input.binary.hasExistingBinaryKey() )
        {
            binaryImageEl.setAttribute( "key", input.binary.getExistingBinaryKey().toString() );
        }
        else
        {
            binaryImageEl.setAttribute( "key", "%0" );
        }
        sourceimageEl.addContent( binaryImageEl );
        contentDataEl.addContent( sourceimageEl );

        return new Document( contentDataEl );
    }
}

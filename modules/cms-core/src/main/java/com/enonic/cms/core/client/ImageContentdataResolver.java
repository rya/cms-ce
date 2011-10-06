package com.enonic.cms.core.client;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.api.client.model.content.image.ImageContentDataInput;
import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.legacy.LegacyImageContentData;
import com.enonic.cms.core.content.image.ImageUtil;

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

        Element keywords = buildKeywordsElement( input );

        contentDataEl.addContent( keywords );

        BufferedImage origImage = null;
        try
        {
            origImage = ImageUtil.readImage( input.binary.getBinary() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

        Element sourceimageEl = new Element( "sourceimage" );
        sourceimageEl.setAttribute( "width", Integer.toString( origImage.getWidth() ) );
        sourceimageEl.setAttribute( "height", Integer.toString( origImage.getHeight() ) );
        Element binarySourceimageEl = new Element( "binarydata" );
        if ( input.binary.hasExistingBinaryKey() )
        {
            binarySourceimageEl.setAttribute( "key", input.binary.getExistingBinaryKey().toString() );
        }
        else
        {
            binarySourceimageEl.setAttribute( "key", "%0" );
        }
        sourceimageEl.addContent( binarySourceimageEl );
        contentDataEl.addContent( sourceimageEl );

        Element imagesEl = new Element( "images" );
        imagesEl.setAttribute( "border", "no" );
        Element imageEl = new Element( "image" );
        imageEl.setAttribute( "rotation", "none" );
        imageEl.setAttribute( "type", "original" );
        Element imageWidthEl = new Element( "width" );
        imageWidthEl.addContent( Integer.toString( origImage.getWidth() ) );
        Element imageHeightEl = new Element( "height" );
        imageHeightEl.addContent( Integer.toString( origImage.getHeight() ) );
        Element binaryImageEl = new Element( "binarydata" );
        if ( input.binary.hasExistingBinaryKey() )
        {
            binaryImageEl.setAttribute( "key", input.binary.getExistingBinaryKey().toString() );
        }
        else
        {
            binaryImageEl.setAttribute( "key", "%0" );
        }
        imageEl.addContent( imageWidthEl ).addContent( imageHeightEl ).addContent( binaryImageEl );
        imagesEl.addContent( imageEl );
        contentDataEl.addContent( imagesEl );

        return new Document( contentDataEl );
    }

    private Element buildKeywordsElement( ImageContentDataInput input )
    {
        Element keywords = new Element( "keywords" );

        if ( input.keywords != null && !input.keywords.isEmpty() )
        {
            StringBuilder keywordsBuilder = new StringBuilder( "" );

            for ( String keyword : input.keywords.getKeywords() )
            {
                keywordsBuilder.append( keyword ).append( " " );
            }
            String keywordsText = keywordsBuilder.substring( 0, keywordsBuilder.length() - 1 );

            keywords.setText( keywordsText );
        }
        return keywords;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.client;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.api.client.model.content.file.FileContentDataInput;

import com.enonic.cms.core.content.binary.BinaryDataAndBinary;
import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.legacy.LegacyFileContentData;

public class FileContentdataResolver
{

    public ContentData resolveContentdata( FileContentDataInput fileContentDataInput )
    {
        Document xml = buildXml( fileContentDataInput );
        return new LegacyFileContentData( xml, BinaryDataAndBinary.convertFromBinaryInput( fileContentDataInput.binary ) );
    }

    private Document buildXml( FileContentDataInput input )
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

        return new Document( contentDataEl );
    }

}

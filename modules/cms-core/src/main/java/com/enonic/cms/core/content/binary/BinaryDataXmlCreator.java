/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import java.util.Collection;

import com.enonic.cms.core.content.ContentVersionEntity;
import org.jdom.Element;

public class BinaryDataXmlCreator
{
    public Element createBinariesElement( final ContentVersionEntity contentVersion )
    {
        final Element binariesElement = new Element( "binaries" );

        int addCount = 0;

        if ( contentVersion.hasContentBinaryData() )
        {
            final Collection<ContentBinaryDataEntity> contentBinaries = contentVersion.getContentBinaryData();
            for ( ContentBinaryDataEntity contentBinary : contentBinaries )
            {
                Element binaryElement = doCreateBinaryElement( contentBinary );
                binariesElement.addContent( binaryElement );
                addCount++;
            }
        }

        binariesElement.setAttribute( "count", Integer.toString( addCount ) );

        return binariesElement;
    }

    private Element doCreateBinaryElement( final ContentBinaryDataEntity contentBinaryData )
    {
        final BinaryDataEntity binaryData = contentBinaryData.getBinaryData();
        final Element binaryEl = new Element( "binary" );
        binaryEl.setAttribute( "filename", binaryData.getName() );
        binaryEl.setAttribute( "filesize", Integer.toString( binaryData.getSize() ) );
        binaryEl.setAttribute( "key", Integer.toString( binaryData.getKey() ) );
        final String label = contentBinaryData.getLabel();
        if ( label != null && label.length() > 0 )
        {
            binaryEl.setAttribute( "label", label );
        }
        return binaryEl;
    }
}

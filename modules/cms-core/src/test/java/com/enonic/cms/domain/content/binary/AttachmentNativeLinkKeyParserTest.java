/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.binary;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.binary.*;
import org.junit.Test;

import com.enonic.cms.domain.Path;

import static org.junit.Assert.*;

/**
 * Feb 15, 2010
 */
public class AttachmentNativeLinkKeyParserTest
{

    @Test
    public void parse_path_with_only_content_key()
    {
        AttachmentNativeLinkKey linkKey = AttachmentNativeLinkKeyParser.parse( new Path( "123" ) );
        assertEquals( new ContentKey( 123 ), linkKey.getContentKey() );
        assertNull( "jpg", linkKey.getExtension() );
        assertEquals( "123", linkKey.asUrlRepresentation() );
    }

    @Test
    public void parse_path_with_content_key_and_extension()
    {
        AttachmentNativeLinkKey linkKey = AttachmentNativeLinkKeyParser.parse( new Path( "123.jpg" ) );
        assertEquals( new ContentKey( 123 ), linkKey.getContentKey() );
        assertEquals( "jpg", linkKey.getExtension() );
        assertEquals( "123.jpg", linkKey.asUrlRepresentation() );
    }

    @Test
    public void parse_path_with_content_key_and_empty_extension()
    {
        AttachmentNativeLinkKey linkKey = AttachmentNativeLinkKeyParser.parse( new Path( "123." ) );
        assertEquals( new ContentKey( 123 ), linkKey.getContentKey() );
        assertEquals( "", linkKey.getExtension() );
        assertEquals( "123.", linkKey.asUrlRepresentation() );
    }

    @Test
    public void parse_path_with_content_key_and_label()
    {
        AttachmentNativeLinkKeyWithLabel linkKey =
            (AttachmentNativeLinkKeyWithLabel) AttachmentNativeLinkKeyParser.parse( new Path( "123/label/source" ) );
        assertEquals( new ContentKey( 123 ), linkKey.getContentKey() );
        assertNull( linkKey.getExtension() );
        assertEquals( "source", linkKey.getLabel() );
        assertEquals( "123/label/source", linkKey.asUrlRepresentation() );
    }

    @Test
    public void parse_path_with_content_key_and_label_and_extension()
    {
        AttachmentNativeLinkKeyWithLabel linkKey =
            (AttachmentNativeLinkKeyWithLabel) AttachmentNativeLinkKeyParser.parse( new Path( "/123/label/medium.gif" ) );
        assertEquals( new ContentKey( 123 ), linkKey.getContentKey() );
        assertEquals( "gif", linkKey.getExtension() );
        assertEquals( "medium", linkKey.getLabel() );
        assertEquals( "123/label/medium.gif", linkKey.asUrlRepresentation() );
    }

    @Test
    public void parse_path_with_content_key_and_label_with_slash_at_end()
    {
        AttachmentNativeLinkKeyWithLabel linkKey =
            (AttachmentNativeLinkKeyWithLabel) AttachmentNativeLinkKeyParser.parse( new Path( "/123/label/small/" ) );
        assertEquals( new ContentKey( 123 ), linkKey.getContentKey() );
        assertNull( linkKey.getExtension() );
        assertEquals( "small", linkKey.getLabel() );
        assertEquals( "123/label/small", linkKey.asUrlRepresentation() );
    }

    @Test
    public void parse_path_with_content_key_and_binary()
    {
        AttachmentNativeLinkKeyWithBinaryKey linkKey =
            (AttachmentNativeLinkKeyWithBinaryKey) AttachmentNativeLinkKeyParser.parse( new Path( "/123/binary/222" ) );
        assertEquals( new ContentKey( 123 ), linkKey.getContentKey() );
        assertNull( linkKey.getExtension() );
        assertEquals( new BinaryDataKey( 222 ), linkKey.getBinaryKey() );
        assertEquals( "123/binary/222", linkKey.asUrlRepresentation() );
    }

    @Test
    public void parse_path_with_content_key_and_binary_and_extension()
    {
        AttachmentNativeLinkKeyWithBinaryKey linkKey =
            (AttachmentNativeLinkKeyWithBinaryKey) AttachmentNativeLinkKeyParser.parse( new Path( "/123/binary/222.pdf" ) );
        assertEquals( new ContentKey( 123 ), linkKey.getContentKey() );
        assertEquals( new BinaryDataKey( 222 ), linkKey.getBinaryKey() );
        assertEquals( "pdf", linkKey.getExtension() );
        assertEquals( "123/binary/222.pdf", linkKey.asUrlRepresentation() );
    }

    @Test
    public void parse_path_with_contentkey_and_only_labelmarker_throws_exception()
    {
        try
        {
            AttachmentNativeLinkKeyParser.parse( new Path( "143/label" ) );
            fail( "Expected InvalidAttachmentNativeLinkKeyException" );
        }
        catch ( InvalidAttachmentNativeLinkKeyException e )
        {
            assertEquals( "Invalid Attachment Key '143/label': Missing label", e.getMessage() );
        }
        catch ( Exception e )
        {
            fail( "Expected InvalidAttachmentNativeLinkKeyException" );
        }
    }

    @Test
    public void parse_path_with_contentkey_and_only_binarymarker_throws_exception()
    {
        try
        {
            AttachmentNativeLinkKeyParser.parse(new Path("143/binary"));
            fail( "Expected InvalidAttachmentNativeLinkKeyException" );
        }
        catch ( InvalidAttachmentNativeLinkKeyException e )
        {
            assertEquals( "Invalid Attachment Key '143/binary': Missing binary key", e.getMessage() );
        }
        catch ( Exception e )
        {
            fail( "Expected InvalidAttachmentNativeLinkKeyException" );
        }
    }

    @Test
    public void parse_empty_path_throws_exception()
    {
        try
        {
            AttachmentNativeLinkKeyParser.parse( new Path( "" ) );
            fail( "Expected InvalidAttachmentNativeLinkKeyException" );
        }
        catch ( InvalidAttachmentNativeLinkKeyException e )
        {
            assertEquals( "Invalid Attachment Key '': Path is empty", e.getMessage() );
        }
        catch ( Exception e )
        {
            fail( "Expected InvalidAttachmentNativeLinkKeyException" );
        }
    }

}

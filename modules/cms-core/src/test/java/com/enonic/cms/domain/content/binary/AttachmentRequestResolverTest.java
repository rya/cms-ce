/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.binary;

import java.util.Date;

import com.enonic.cms.core.content.*;
import com.enonic.cms.core.content.binary.AttachmentRequestResolver;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.core.content.category.CategoryEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.PathAndParams;
import com.enonic.cms.domain.RequestParameters;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerKey;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Dec 17, 2009
 * Time: 11:38:02 AM
 */
public class AttachmentRequestResolverTest

{

    ContentEntity content;

    ContentVersionEntity contentVersion;

    ContentTypeEntity contentType;

    CategoryEntity category;

    ContentHandlerEntity contentHandler;


    @Before
    public void setUp()
    {
        content = new ContentEntity();
        content.setKey( new ContentKey( 123 ) );

        contentHandler = createContentHandler( "File content", ContentHandlerName.FILE.getHandlerClassShortName() );

        contentType = new ContentTypeEntity();
        contentType.setHandler( contentHandler );

        category = new CategoryEntity();
        category.setContentType( contentType );

        content.setCategory( category );

        contentVersion = new ContentVersionEntity();
        contentVersion.setKey( new ContentVersionKey( "1" ) );

        content.setMainVersion( contentVersion );

    }

    @Test
    public void testNativeLinkWithNoLabel()
    {

        AttachmentRequestResolver attachmentRequestResolver = new AttachmentRequestResolver()
        {
            @Override
            protected BinaryDataKey getBinaryData( ContentEntity content, String label )
            {
                Assert.assertEquals( label, "source" );

                return new BinaryDataKey( "1" );
            }

            @Override
            protected ContentEntity getContent( ContentKey contentKey )
            {
                return content;
            }
        };

        attachmentRequestResolver.resolveBinaryDataKey( new PathAndParams( new Path( "_attachment/123" ), new RequestParameters() ) );
    }

    protected ContentHandlerEntity createContentHandler( String name, String handlerClassName )
    {
        ContentHandlerName contentHandlerName = ContentHandlerName.parse( handlerClassName );
        ContentHandlerEntity contentHandler = new ContentHandlerEntity();
        contentHandler.setKey( new ContentHandlerKey( 1 ) );
        contentHandler.setName( name );
        contentHandler.setClassName( contentHandlerName.getHandlerClassShortName() );
        contentHandler.setTimestamp( new Date() );
        return contentHandler;
    }

}

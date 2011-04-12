/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.ContentResultSetNonLazy;
import com.enonic.cms.core.content.resultset.ContentResultSetWithOverridingContent;

import static org.junit.Assert.*;

/**
 * Sep 16, 2010
 */
public class ContentResultSetWithOverridingContentTest
{
    @Test
    public void basic()
    {
        ContentResultSet source = createContentResultSet( createContents( 0, 3 ) );
        ContentEntity overridingContent = createContent( 1, "overriding" );

        // exercise
        ContentResultSetWithOverridingContent result = new ContentResultSetWithOverridingContent( source, overridingContent );

        // verify
        assertEquals( 3, result.getLength() );
        assertEquals( 0, result.getFromIndex() );
        assertTrue( result.containsContent( contentKey( 0 ) ) );
        assertTrue( result.containsContent( contentKey( 1 ) ) );
        assertTrue( result.containsContent( contentKey( 2 ) ) );
        assertEquals( "content-0", result.getContent( 0 ).getName() );
        assertEquals( "overriding", result.getContent( 1 ).getName() );
        assertEquals( "content-2", result.getContent( 2 ).getName() );

        assertEquals( Lists.newArrayList( contentKey( 0 ), contentKey( 1 ), contentKey( 2 ) ), result.getKeys() );

        assertEquals(
            Lists.newArrayList( createContent( 0, "content-0" ), createContent( 1, "overriding" ), createContent( 2, "content-2" ) ),
            result.getContents() );

    }

    @Test
    public void one_and_only()
    {
        ContentResultSet source = createContentResultSet( createContents( 0, 1 ) );
        ContentEntity overridingContent = createContent( 0, "overriding" );

        ContentResultSetWithOverridingContent result = new ContentResultSetWithOverridingContent( source, overridingContent );

        assertEquals( 1, result.getLength() );
        assertEquals( 0, result.getFromIndex() );
        assertEquals( "overriding", result.getContent( 0 ).getName() );

        assertEquals( Lists.newArrayList( contentKey( 0 ) ), result.getKeys() );

        assertEquals( Lists.newArrayList( createContent( 0, "overriding" ) ), result.getContents() );
    }

    @Test
    public void createRandomizedResult()
    {
        ContentResultSet source = createContentResultSet( createContents( 0, 3 ) );
        ContentEntity overridingContent = createContent( 1, "overriding" );

        // exercise
        ContentResultSetWithOverridingContent result = new ContentResultSetWithOverridingContent( source, overridingContent );

        // verify createRandomizedResult
        ContentResultSet randomizedResult = result.createRandomizedResult( source.getLength() );
        Collection<ContentEntity> randomizedContents = randomizedResult.getContents();
        assertTrue( randomizedContents.contains( createContent( 1, "overriding" ) ) );
        assertTrue( randomizedContents.contains( createContent( 0, "content-0" ) ) );
        assertTrue( randomizedContents.contains( createContent( 2, "content-2" ) ) );
    }

    private ContentResultSet createContentResultSet( List<ContentEntity> contents )
    {
        return new ContentResultSetNonLazy( contents, 0, contents.size() );
    }

    private List<ContentEntity> createContents( int fromKey, int toKey )
    {
        List<ContentEntity> contents = new ArrayList<ContentEntity>();
        for ( int i = fromKey; i < toKey; i++ )
        {
            contents.add( createContent( i, "content-" + i ) );
        }
        return contents;
    }

    private ContentEntity createContent( int key, String name )
    {
        ContentEntity content = new ContentEntity();
        content.setKey( contentKey( key ) );
        content.setName( name );
        return content;
    }

    private ContentKey contentKey( int key )
    {
        return new ContentKey( key );
    }
}

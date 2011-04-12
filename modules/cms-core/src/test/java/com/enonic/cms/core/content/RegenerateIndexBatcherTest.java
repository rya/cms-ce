/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;


public class RegenerateIndexBatcherTest
{
    private IndexService indexService;

    private ContentService contentService;

    private RegenerateIndexBatcher regenerateIndexBatcher;


    @Before
    public void before()
    {

        contentService = createMock( ContentService.class );

        indexService = createMock( IndexService.class );

        regenerateIndexBatcher = new RegenerateIndexBatcher( indexService, contentService );
    }

    @Test
    public void testRegenrateIndexUnequalToBatchSize()
    {

        ContentTypeEntity cty1 = createContentTypeEntity( 1, "test" );
        List<ContentKey> contentKeysOfCty1 = createContentKeys( 11 );

        expect( contentService.findContentKeysByContentType( cty1 ) ).andReturn( contentKeysOfCty1 );
        indexService.regenerateIndex( createContentKeys( new int[]{1, 2, 3, 4} ) );
        indexService.regenerateIndex( createContentKeys( new int[]{5, 6, 7, 8} ) );
        indexService.regenerateIndex( createContentKeys( new int[]{9, 10, 11} ) );

        replay( contentService );
        replay( indexService );

        regenerateIndexBatcher.regenerateIndex( cty1, 4, null );

        verify( indexService );
    }

    @Test
    public void testRegenrateIndexEqualToBatchSize()
    {

        ContentTypeEntity cty1 = createContentTypeEntity( 1, "test" );
        List<ContentKey> contentKeysOfCty1 = createContentKeys( 8 );

        expect( contentService.findContentKeysByContentType( cty1 ) ).andReturn( contentKeysOfCty1 );
        indexService.regenerateIndex( createContentKeys( new int[]{1, 2, 3, 4} ) );
        indexService.regenerateIndex( createContentKeys( new int[]{5, 6, 7, 8} ) );

        replay( contentService );
        replay( indexService );

        regenerateIndexBatcher.regenerateIndex( cty1, 4, null );

        verify( indexService );
    }

    @Test
    public void testRegenrateIndexSameTotalAsBatchSize()
    {

        ContentTypeEntity cty1 = createContentTypeEntity( 1, "test" );
        List<ContentKey> contentKeysOfCty1 = createContentKeys( 4 );

        expect( contentService.findContentKeysByContentType( cty1 ) ).andReturn( contentKeysOfCty1 );
        indexService.regenerateIndex( createContentKeys( new int[]{1, 2, 3, 4} ) );

        replay( contentService );
        replay( indexService );

        regenerateIndexBatcher.regenerateIndex( cty1, 4, null );

        verify( indexService );
    }

    @Test
    public void testRegenrateIndexWithContentKeysSmallerThanBatchSize()
    {

        ContentTypeEntity cty1 = createContentTypeEntity( 1, "test" );
        List<ContentKey> contentKeysOfCty1 = createContentKeys( 2 );

        expect( contentService.findContentKeysByContentType( cty1 ) ).andReturn( contentKeysOfCty1 );
        indexService.regenerateIndex( createContentKeys( new int[]{1, 2} ) );

        replay( contentService );
        replay( indexService );

        regenerateIndexBatcher.regenerateIndex( cty1, 4, null );

        verify( indexService );
    }

    @Test
    public void testRegenrateIndexWithNoneContentKeys()
    {

        ContentTypeEntity cty1 = createContentTypeEntity( 1, "test" );
        List<ContentKey> contentKeysOfCty1 = createContentKeys( 0 );

        expect( contentService.findContentKeysByContentType( cty1 ) ).andReturn( contentKeysOfCty1 );
        //indexService.regenerateIndex(createContentKeys(new int[] {1, 2}));

        replay( contentService );
        replay( indexService );

        regenerateIndexBatcher.regenerateIndex( cty1, 4, null );

        verify( indexService );
    }

    private List<ContentKey> createContentKeys( int[] contentKeys )
    {

        List<ContentKey> keys = new ArrayList<ContentKey>();
        for ( int contentKey : contentKeys )
        {
            keys.add( new ContentKey( contentKey ) );
        }
        return keys;
    }

    private List<ContentKey> createContentKeys( int count )
    {

        List<ContentKey> keys = new ArrayList<ContentKey>();
        for ( int i = 0; i < count; i++ )
        {
            keys.add( new ContentKey( i + 1 ) );
        }
        return keys;
    }

    private ContentTypeEntity createContentTypeEntity( int key, String name )
    {

        ContentTypeEntity contentType = new ContentTypeEntity();
        contentType.setKey( key );
        contentType.setName( name );
        return contentType;
    }

}

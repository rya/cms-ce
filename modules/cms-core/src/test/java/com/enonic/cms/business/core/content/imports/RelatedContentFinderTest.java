package com.enonic.cms.business.core.content.imports;

import com.enonic.cms.business.core.content.index.ContentIndexService;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.contentdata.ContentData;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.custom.DataEntry;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.domain.content.contenttype.ContentTypeConfig;
import com.enonic.cms.domain.content.contenttype.CtyFormConfig;
import com.enonic.cms.domain.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.domain.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.domain.content.resultset.ContentResultSet;
import com.enonic.cms.domain.content.resultset.ContentResultSetNonLazy;
import com.enonic.cms.store.dao.ContentTypeDao;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.easymock.EasyMock.createMock;

public class RelatedContentFinderTest
{
    private RelatedContentFinder relatedContentFinder;

    private ContentIndexService contentIndexServiceMock = createMock( ContentIndexService.class );

    private ContentTypeDao contentTypeDaoMock = createMock( ContentTypeDao.class );

    @Before
    public void setUp()
    {
        relatedContentFinder = new RelatedContentFinder( contentTypeDaoMock, contentIndexServiceMock );
    }

    @Test
    public void testGetOrderedKeys()
    {
        List<ContentEntity> contents = new ArrayList<ContentEntity>();

        appendContentEntity( contents, "100", "test2" );
        appendContentEntity( contents, "200", "test3" );
        appendContentEntity( contents, "300", "test4" );

        ContentResultSet resSet = new ContentResultSetNonLazy( contents, 0, 10 );

        List<String> orderMask = new LinkedList<String>();
        orderMask.add( "test2" );
        orderMask.add( "test4" );
        orderMask.add( "test3" );

        List<ContentKey> actualResult = relatedContentFinder.getOrderedKeys( resSet, orderMask, null );

        assertListEquals( new String[]{"100", "300", "200"}, actualResult );
    }

    @Test
    public void testGetUnorderedKeysKeepEnterOrder()
    {
        List<ContentEntity> contents = new ArrayList<ContentEntity>();

        appendContentEntity( contents, "300", "test2" );
        appendContentEntity( contents, "100", "test3" );
        appendContentEntity( contents, "200", "test4" );

        ContentResultSet resSet = new ContentResultSetNonLazy( contents, 0, 10 );

        List<String> orderMask = new LinkedList<String>();
        orderMask.add( "test22" );
        orderMask.add( "test44" );
        orderMask.add( "test33" );

        List<ContentKey> actualResult = relatedContentFinder.getOrderedKeys( resSet, orderMask, null );

        assertListEquals( new String[]{"300", "100", "200"}, actualResult );
    }

    @Test
    public void testGetUnorderedKeysSomeKeyMatch()
    {
        List<ContentEntity> contents = new ArrayList<ContentEntity>();

        appendContentEntity( contents, "300", "test2" );
        appendContentEntity( contents, "100", "test3" );
        appendContentEntity( contents, "200", "test4" );
        appendContentEntity( contents, "400", "test5" );
        appendContentEntity( contents, "500", "test6" );

        ContentResultSet resSet = new ContentResultSetNonLazy( contents, 0, 10 );

        List<String> orderMask = new LinkedList<String>();
        orderMask.add( "wrong" );
        orderMask.add( "wrong" );
        orderMask.add( "test3" );
        orderMask.add( "test2" );
        orderMask.add( "test6" );

        List<ContentKey> actualResult = relatedContentFinder.getOrderedKeys( resSet, orderMask, null );

        assertListEquals( new String[]{"100", "300", "500", "200", "400"}, actualResult );
    }

    private static void appendContentEntity( List<ContentEntity> contents, String id, final String contentKey )
    {
        ContentKey key = new ContentKey( id );
        ContentEntity contentEntity = new ContentEntity();
        contentEntity.setKey( key );

        ContentVersionEntity versionEntity = new ContentVersionEntity();
        contentEntity.setMainVersion( versionEntity );

        CtyFormConfig formConfig = new CtyFormConfig( null );
        formConfig.setTitleInputName( "cty" );

        ContentTypeConfig config = new ContentTypeConfig( ContentHandlerName.CUSTOM, "name" );
        config.setForm( formConfig );

        final DataEntryConfig eConfig = new TextDataEntryConfig( null, false, null, null );

        ContentData contentData = new CustomContentData( config )
        {
            public DataEntry getEntry( String name )
            {
                return new TextDataEntry( eConfig, contentKey );
            }
        };
        versionEntity.setContentData( contentData );

        contents.add( contentEntity );
    }

    private static String arrayToString( Object[] a )
    {
        StringBuilder result = new StringBuilder( "[" );

        for ( int i = 0; i < a.length; i++ )
        {
            result.append( i ).append( ": " ).append( a[i] );
            if ( i < a.length - 1 )
            {
                result.append( ", " );
            }
        }

        result.append( "]" );

        return result.toString();
    }

    private static void assertArrayEquals( Object[] a1, Object[] a2 )
    {
        Assert.assertEquals( arrayToString( a1 ), arrayToString( a2 ) );
    }

    private static void assertListEquals( Object[] a1, List a2 )
    {
        assertArrayEquals( a1, a2.toArray() );
    }
}

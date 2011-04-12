/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.util.HashMap;
import java.util.Map;

import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.CategoryStatistics;
import junit.framework.TestCase;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;

public class CategoryStatisticsHelperTest
    extends TestCase
{

    private CategoryHandler categoryHandler;

    private CategoryStatisticsHelper categoryStatisticsHelper;

    private final int unitKey = 0;


    protected void setUp()
        throws Exception
    {
        super.setUp();

        categoryHandler = createMock( CategoryHandler.class );
        categoryStatisticsHelper = new CategoryStatisticsHelper( categoryHandler );
        Map<Integer, CategoryStatistics> catStats = getFixtureCategoryStats();
        expect( categoryHandler.getCategoryStatistics( unitKey ) ).andReturn( catStats );
        categoryHandler.collectStatisticsFromContent( unitKey, catStats );
        categoryHandler.collectStatisticsFromBinaryData( unitKey, catStats );
    }

    private Map<Integer, CategoryStatistics> getFixtureCategoryStats()
    {
        Map<Integer, CategoryStatistics> map = new HashMap<Integer, CategoryStatistics>();
        map.put( 4, createCategoryStatistics( 4, 0, 1145 ) );
        map.put( 5, createCategoryStatistics( 5, 0, 2094 ) );
        map.put( 68, createCategoryStatistics( 68, 0, 3925 ) );
        map.put( 69, createCategoryStatistics( 69, 0, 279 ) );
        map.put( 78, createCategoryStatistics( 78, 0, 3360 ) );
        map.put( 80, createCategoryStatistics( 80, 68, 538 ) );
        map.put( 81, createCategoryStatistics( 81, 80, 638 ) );
        map.put( 84, createCategoryStatistics( 84, 80, 167 ) );
        map.put( 0, createCategoryStatistics( 0, null, 7664 ) );
        return map;
    }

    private CategoryStatistics createCategoryStatistics( Integer categoryKey, Integer parentCategoryKey, int size )
    {
        CategoryStatistics cs = new CategoryStatistics( categoryKey );
        cs.setParentCategoryKey( parentCategoryKey );
        cs.setSize( size );
        return cs;
    }

    public void testGetArchiveSizeByCategory()
    {

        CategoryKey categoryKey = new CategoryKey( 68 );

        expect( categoryHandler.getUnitKey( categoryKey ) ).andReturn( unitKey );
        replay( categoryHandler );

        long size = categoryStatisticsHelper.getArchiveSizeByCategory( categoryKey );
        assertEquals( 5268, size );
    }

    public void testGetArchiveSizeByUnit()
    {

        replay( categoryHandler );

        long size = categoryStatisticsHelper.getArchiveSizeByUnit( unitKey );
        int expectedSize = 19810;
        assertEquals( expectedSize, size );
    }

    public void testGetArchiveSizeByCategoryWithNoMatchingCategory()
    {

        CategoryKey categoryKey = new CategoryKey( 9898 );

        expect( categoryHandler.getUnitKey( categoryKey ) ).andReturn( unitKey );
        replay( categoryHandler );

        long size = categoryStatisticsHelper.getArchiveSizeByCategory( categoryKey );
        assertEquals( 0, size );
    }

    public void testGetArchiveSizeByUnitWithNoMatchingUnit()
    {

        reset( categoryHandler );
        Map<Integer, CategoryStatistics> catStats = new HashMap<Integer, CategoryStatistics>();
        expect( categoryHandler.getCategoryStatistics( 999 ) ).andReturn( catStats );
        categoryHandler.collectStatisticsFromBinaryData( 999, catStats );
        categoryHandler.collectStatisticsFromContent( 999, catStats );
        replay( categoryHandler );

        long size = categoryStatisticsHelper.getArchiveSizeByUnit( 999 );
        assertEquals( 0, size );
    }
}

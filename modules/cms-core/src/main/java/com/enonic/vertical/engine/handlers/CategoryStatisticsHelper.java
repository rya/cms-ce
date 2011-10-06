/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.util.Collection;
import java.util.Map;

import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.CategoryStatistics;

public class CategoryStatisticsHelper
{

    private final CategoryHandler categoryHandler;


    public CategoryStatisticsHelper( CategoryHandler categoryHandler )
    {
        this.categoryHandler = categoryHandler;
    }

    public long getArchiveSizeByUnit( int unitKey )
    {

        Map<Integer, CategoryStatistics> catStats = categoryHandler.getCategoryStatistics( unitKey );
        connectCategories( catStats );
        categoryHandler.collectStatisticsFromBinaryData( unitKey, catStats );
        categoryHandler.collectStatisticsFromContent( unitKey, catStats );

        return summarizeAll( catStats.values() );
    }

    public long getArchiveSizeByCategory( CategoryKey categoryKey )
    {

        int unitKey = categoryHandler.getUnitKey( categoryKey );

        Map<Integer, CategoryStatistics> catStats = categoryHandler.getCategoryStatistics( unitKey );
        connectCategories( catStats );
        categoryHandler.collectStatisticsFromBinaryData( unitKey, catStats );
        categoryHandler.collectStatisticsFromContent( unitKey, catStats );

        return getAccumulatedSize( categoryKey, catStats );
    }


    private void connectCategories( Map<Integer, CategoryStatistics> categoryStatisicsMap )
    {

        for ( Map.Entry<Integer, CategoryStatistics> entry : categoryStatisicsMap.entrySet() )
        {
            CategoryStatistics curCategoryStat = entry.getValue();

            CategoryStatistics parent = categoryStatisicsMap.get( curCategoryStat.getParentCategoryKey() );
            if ( parent != null && !curCategoryStat.getCategoryKey().equals( curCategoryStat.getParentCategoryKey() ) )
            {
                parent.addChild( curCategoryStat );
                curCategoryStat.setParent( parent );
            }
            else
            {
                curCategoryStat.setParent( null );
            }
        }
    }

    private long summarizeAll( Collection<CategoryStatistics> categoryStatisics )
    {

        long sum = 0;

        for ( CategoryStatistics cs : categoryStatisics )
        {
            sum += cs.getSize();
        }

        return sum;
    }

    private long getAccumulatedSize( CategoryKey categoryKey, Map<Integer, CategoryStatistics> categoryStatsMap )
    {

        CategoryStatistics cs = categoryStatsMap.get( categoryKey.toInt() );
        if ( cs == null )
        {
            return 0;
        }
        return cs.calculateAccumulatedSize();
    }
}

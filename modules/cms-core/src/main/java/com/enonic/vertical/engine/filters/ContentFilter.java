/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.filters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import com.enonic.vertical.engine.BaseEngine;
import com.enonic.vertical.engine.handlers.CategoryHandler;

public final class ContentFilter
    implements Filter
{

    // category filter

    private int[] categoryKeys;

    private boolean categoryRecursive;

    // when categoriesProcessed is true, the category array
    // have been processed (expanded and/or sorted)

    private boolean categoriesProcessed;

    // content type filter

    private int[] contentTypeKeys;

    /**
     * @param categoryKeys
     * @param categoryRecursive
     * @param contentTypeKeys
     */
    public ContentFilter( int[] categoryKeys, boolean categoryRecursive, int[] contentTypeKeys )
    {
        this.categoryKeys = categoryKeys;
        this.categoryRecursive = categoryRecursive;
        this.contentTypeKeys = contentTypeKeys;
        if ( contentTypeKeys != null )
        {
            Arrays.sort( contentTypeKeys );
        }
    }

    private void processCategories( BaseEngine engine )
    {
        if ( !categoriesProcessed )
        {
            if ( categoryRecursive )
            {
                CategoryHandler categoryHandler = engine.getCategoryHandler();
                int[] subCategoryKeys = categoryHandler.getCategoryKeysBySuperCategories( null, categoryKeys, true );
                int[] keys = new int[categoryKeys.length + subCategoryKeys.length];
                System.arraycopy( categoryKeys, 0, keys, 0, categoryKeys.length );
                System.arraycopy( subCategoryKeys, 0, keys, categoryKeys.length, subCategoryKeys.length );
                categoryKeys = keys;
            }
            Arrays.sort( categoryKeys );
            categoriesProcessed = true;
        }
    }

    /**
     * @see com.enonic.vertical.engine.Filter#filter(com.enonic.vertical.engine.BaseHandler, java.sql.ResultSet)
     */
    public boolean filter( BaseEngine engine, ResultSet resultSet )
        throws SQLException
    {
        if ( categoryKeys != null && categoryKeys.length > 0 )
        {
            processCategories( engine );
            int categoryKey = resultSet.getInt( "cat_lKey" );
            if ( Arrays.binarySearch( categoryKeys, categoryKey ) < 0 )
            {
                return false;
            }
        }

        if ( contentTypeKeys != null && contentTypeKeys.length > 0 )
        {
            int contentTypeKey = resultSet.getInt( "cat_cty_lKey" );
            if ( Arrays.binarySearch( contentTypeKeys, contentTypeKey ) < 0 )
            {
                return false;
            }
        }

        return true;
    }

    public void appendWhereClause( BaseEngine engine, StringBuffer sql )
    {
        if ( categoryKeys != null && categoryKeys.length > 0 )
        {
            processCategories( engine );
            sql.append( "AND cat_lKey IN (" );
            for ( int i = 0; i < categoryKeys.length; i++ )
            {
                if ( i > 0 )
                {
                    sql.append( ',' );
                }
                sql.append( categoryKeys[i] );
            }
            sql.append( ')' );
        }

        if ( contentTypeKeys != null && contentTypeKeys.length > 0 )
        {
            sql.append( "AND cat_cty_lKey IN (" );
            for ( int i = 0; i < contentTypeKeys.length; i++ )
            {
                if ( i > 0 )
                {
                    sql.append( ',' );
                }
                sql.append( contentTypeKeys[i] );
            }
            sql.append( ')' );
        }
    }

}

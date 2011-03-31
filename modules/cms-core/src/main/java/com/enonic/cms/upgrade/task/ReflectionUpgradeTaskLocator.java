/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.task;

import java.util.ArrayList;
import java.util.List;

public final class ReflectionUpgradeTaskLocator
    implements UpgradeTaskLocator
{
    private final static String TASK_PREFIX = ReflectionUpgradeTaskLocator.class.getPackage().getName() + ".UpgradeModel";

    private final static int FIRST_MODEL_NUMBER = 201;

    public List<UpgradeTask> getTasks()
    {
        ArrayList<UpgradeTask> list = new ArrayList<UpgradeTask>();

        int modelNumber = FIRST_MODEL_NUMBER;
        while ( true )
        {
            UpgradeTask task = createTask( modelNumber );
            if ( task == null )
            {
                break;
            }
            else
            {
                list.add( task );
                modelNumber++;
            }
        }

        return list;
    }

    private UpgradeTask createTask( int modelNum )
    {
        Class<UpgradeTask> clz = findTaskClass( modelNum );
        if ( clz != null )
        {
            try
            {
                return clz.newInstance();
            }
            catch ( Exception e )
            {
                throw new IllegalStateException( "Failed to create instance of task [" + clz.getName() + "]", e );
            }
        }

        return null;
    }

    private Class<UpgradeTask> findTaskClass( int modelNum )
    {
        StringBuffer str = new StringBuffer( TASK_PREFIX );
        if ( modelNum < 1000 )
        {
            str.append( "0" );
        }

        if ( modelNum < 100 )
        {
            str.append( "0" );
        }

        if ( modelNum < 10 )
        {
            str.append( "0" );
        }

        str.append( modelNum );
        return findTaskClass( str.toString() );
    }

    @SuppressWarnings("unchecked")
    private Class<UpgradeTask> findTaskClass( String clzName )
    {
        try
        {
            return (Class<UpgradeTask>) Class.forName( clzName );
        }
        catch ( Exception e )
        {
            return null;
        }
    }
}

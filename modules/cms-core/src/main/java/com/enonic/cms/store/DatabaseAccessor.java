/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.enonic.esl.sql.model.Database;
import com.enonic.esl.sql.model.DatabaseXMLFactory;

/**
 * This class accesses the database meta data and stores it.
 */
public final class DatabaseAccessor
{
    private static String DATABASE_XML_FILE = "com/enonic/cms/store/database{0,number,0000}.xml";

    private static final int firstModelNumber = 201;

    // NOTE: Must reflect the latest database.xml file

    private static int latestModelNumber = 201;

    private static Map<Integer, Database> cache = new HashMap<Integer, Database>();

    public static Database getLatestDatabase()
    {
        return doGetDatabase( latestModelNumber );
    }

    public static Database getDatabase( int modelNumber )
    {
        if ( modelNumber < firstModelNumber )
        {
            modelNumber = firstModelNumber;
        }
        return doGetDatabase( modelNumber );
    }

    private static Database doGetDatabase( int modelNumber )
    {
        if ( cache.containsKey( modelNumber ) )
        {
            return cache.get( modelNumber );
        }
        String modelDefinition = MessageFormat.format( DATABASE_XML_FILE, modelNumber );
        Database database = loadDatabase( modelDefinition );
        if ( database.getVersion() != modelNumber )
        {
            throw new RuntimeException(
                "Invalid database definition (" + modelDefinition + "). Unexpected model number found: " + database.getVersion() +
                    ", expected: " + modelNumber );
        }
        cache.put( modelNumber, database );
        return database;
    }

    private static ClassLoader getClassLoader()
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if ( loader == null )
        {
            loader = DatabaseAccessor.class.getClassLoader();
        }
        return loader;
    }

    private static Database loadDatabase( String modelDefinition )
    {
        try
        {
            InputStream in = getClassLoader().getResourceAsStream( modelDefinition );
            return DatabaseXMLFactory.generateDatabase( in );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to load database " + modelDefinition, e );
        }
    }
}

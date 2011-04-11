/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jdbc;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import com.enonic.esl.sql.model.DatabaseSchemaTool;

import com.enonic.cms.framework.jdbc.dialect.Dialect;
import com.enonic.cms.framework.jdbc.dialect.DialectResolver;
import com.enonic.cms.framework.jdbc.sql.SQLFormatter;

import com.enonic.cms.api.Version;
import com.enonic.cms.store.DatabaseAccessor;

/**
 * This class generates the database schema.
 */
public final class DatabaseSchemaGenerator
{
    /**
     * SQL formatter.
     */
    private final static SQLFormatter FORMATTER = new SQLFormatter();

    /**
     * Generate database schema and writes it to the specified file.
     */
    private static void generateDatabaseSchema( File outputDir, Dialect dialect, List schema )
        throws Exception
    {
        File output = new File( outputDir, dialect.getName() + ".sql" );
        PrintWriter out = new PrintWriter( new FileWriter( output ) );
        out.println( "--" );
        out.println( "-- Version: " + Version.getTitleAndVersion() );
        out.println( "-- Dialect: " + dialect.getName() );
        out.println( "--" );
        out.println();

        for ( Iterator i = schema.iterator(); i.hasNext(); )
        {
            out.println( FORMATTER.prettyPrint( dialect.translateStatement( (String) i.next() ) ) );
            out.println( dialect.translateStatement( "@separator@" ) );
            out.println();
        }

        out.close();
    }

    /**
     * Generate database schemas for all dialects.
     */
    public static void generateDatabaseSchemas( File outputDir, List schema )
        throws Exception
    {
        for ( Dialect dialect : DialectResolver.getInstance().getDialects() )
        {
            generateDatabaseSchema( outputDir, dialect, schema );
        }
    }

    /**
     * Generate database schemas for all dialects.
     */
    public static void generateDatabaseSchemas( File outputDir )
        throws Exception
    {
        List schema = DatabaseSchemaTool.generateDatabaseSchema( DatabaseAccessor.getLatestDatabase() );
        generateDatabaseSchemas( outputDir, schema );
    }

    /**
     * Generate database schemas for all dialects.
     */
    public static void generateDatabaseSchemas( String outputDir )
        throws Exception
    {
        generateDatabaseSchemas( new File( outputDir ) );
    }

    /**
     * Generate database schema.
     */
    public static void main( String[] args )
        throws Exception
    {
        generateDatabaseSchemas( args[0] );
    }
}

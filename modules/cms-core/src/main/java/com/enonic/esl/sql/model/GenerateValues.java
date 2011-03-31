/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model;

import java.io.File;

/**
 * This class generates the datbase value objects. Replaces the ant task in vertical project.
 */
public final class GenerateValues
    extends GenerateBase
{
    /**
     * Private constructor.
     */
    private GenerateValues()
    {
    }

    /**
     * Generate the database singletons.
     */
    protected void doGenerate( File packageDir, String packageName, Database db )
        throws Exception
    {
        ValueClassUtility.generateValueClasses( packageDir, db, packageName );
    }

    /**
     * Static method for generation.
     */
    public static void generate( String destDir, String packageName, String databaseFile )
        throws Exception
    {
        GenerateValues main = new GenerateValues();
        main.setDestDir( destDir );
        main.setPackageName( packageName );
        main.setDatabaseFile( databaseFile );
        main.generate();
    }

    /**
     * Execute the database singleton generation.
     */
    public static void main( String[] args )
        throws Exception
    {
        if ( args.length >= 3 )
        {
            generate( args[0], args[1], args[2] );
        }
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model;

import java.io.File;

/**
 * This class generates the database singletons. Replaces the ant task in vertical project.
 */
public final class GenerateModel
    extends GenerateBase
{
    /**
     * Private constructor.
     */
    private GenerateModel()
    {
    }

    /**
     * Generate the database singletons.
     */
    protected void doGenerate( File packageDir, String packageName, Database db )
        throws Exception
    {
        SingletonUtility.generateSingletonClasses( packageDir, db, packageName );
    }

    /**
     * Static method for generation.
     */
    public static void generate( String destDir, String packageName, String databaseFile )
        throws Exception
    {
        GenerateModel main = new GenerateModel();
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

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Base class for both model and value generation classes.
 */
public abstract class GenerateBase
{
    /**
     * Destination directory.
     */
    private File destDir;

    /**
     * Package name for generated classes.
     */
    private String packageName;

    /**
     * Database file.
     */
    private File databaseFile;

    /**
     * Construct the utility.
     */
    public GenerateBase()
    {
    }

    /**
     * Return the destination directory.
     */
    public File getDestDir()
    {
        if ( this.destDir != null )
        {
            return this.destDir;
        }
        else
        {
            throw new IllegalArgumentException( "Destination directory not set" );
        }
    }

    /**
     * Set the destination directory.
     */
    public void setDestDir( String destDir )
    {
        setDestDir( new File( destDir ) );
    }

    /**
     * Set the destination directory.
     */
    public void setDestDir( File destDir )
    {
        this.destDir = destDir;
    }

    /**
     * Return the database file.
     */
    public File getDatabaseFile()
    {
        if ( this.databaseFile != null )
        {
            return this.databaseFile;
        }
        else
        {
            throw new IllegalArgumentException( "Database file not set" );
        }
    }

    /**
     * Set the database file.
     */
    public void setDatabaseFile( String databaseFile )
    {
        setDatabaseFile( new File( databaseFile ) );
    }

    /**
     * Set the database file.
     */
    public void setDatabaseFile( File databaseFile )
    {
        this.databaseFile = databaseFile;
    }

    /**
     * Return the package name.
     */
    public String getPackageName()
    {
        if ( this.packageName != null )
        {
            return this.packageName;
        }
        else
        {
            throw new IllegalArgumentException( "Package name not set" );
        }
    }

    /**
     * Set the package name.
     */
    public void setPackageName( String packageName )
    {
        this.packageName = packageName;
    }

    /**
     * Return the package directory.
     */
    public File getPackageDir()
    {
        return new File( getDestDir(), getPackageName().replace( '.', File.separatorChar ) );
    }

    /**
     * Generate the database singletons.
     */
    public void generate()
        throws Exception
    {
        doGenerate( new BufferedInputStream( new FileInputStream( getDatabaseFile() ) ) );
    }

    /**
     * Generate the database singletons.
     */
    private void doGenerate( InputStream in )
        throws Exception
    {
        doGenerate( DatabaseXMLFactory.generateDatabase( in ) );
    }

    /**
     * Generate the database singletons.
     */
    private void doGenerate( Database db )
        throws Exception
    {
        File packageDir = getPackageDir();
        packageDir.mkdirs();
        doGenerate( packageDir, getPackageName(), db );
    }

    /**
     * Generate the database singletons.
     */
    protected abstract void doGenerate( File packageDir, String packageName, Database db )
        throws Exception;
}

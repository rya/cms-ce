/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.io;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class DirectorySnapshot
{
    private final HashMap<String, Long> map;

    private final String[] suffixList;

    private final boolean recursive;

    public DirectorySnapshot( File rootDir, boolean recursive, String... suffixList )
    {
        this.map = new HashMap<String, Long>();
        this.suffixList = suffixList;
        this.recursive = recursive;
        scanForFiles( rootDir );
    }

    private void scanForFiles( File dir )
    {
        if ( !dir.exists() || !dir.isDirectory() )
        {
            return;
        }

        for ( File file : dir.listFiles() )
        {
            if ( this.recursive && file.isDirectory() )
            {
                scanForFiles( file );
            }
            else if ( matchesSuffix( file ) )
            {
                this.map.put( file.getAbsolutePath(), file.lastModified() );
            }
        }
    }

    private boolean matchesSuffix( File file )
    {
        for ( String suffix : this.suffixList )
        {
            if ( file.getName().endsWith( suffix ) )
            {
                return true;
            }
        }

        return false;
    }

    public Set<String> getLocations()
    {
        return this.map.keySet();
    }

    public Set<String> getUpdated( DirectorySnapshot snapshot )
    {
        HashSet<String> set = new HashSet<String>();
        for ( Map.Entry<String, Long> entry : this.map.entrySet() )
        {
            Long time = entry.getValue();
            Long snapshotTime = snapshot.map.get( entry.getKey() );

            if ( ( snapshotTime != null ) && ( snapshotTime > time ) )
            {
                set.add( entry.getKey() );
            }
        }

        return set;
    }

    public Set<String> getDeleted( DirectorySnapshot snapshot )
    {
        HashSet<String> set = new HashSet<String>();
        for ( String name : this.map.keySet() )
        {
            if ( !snapshot.map.containsKey( name ) )
            {
                set.add( name );
            }
        }

        return set;
    }

    public Set<String> getAdded( DirectorySnapshot snapshot )
    {
        HashSet<String> set = new HashSet<String>();
        for ( String name : snapshot.map.keySet() )
        {
            if ( !this.map.containsKey( name ) )
            {
                set.add( name );
            }
        }

        return set;
    }
}

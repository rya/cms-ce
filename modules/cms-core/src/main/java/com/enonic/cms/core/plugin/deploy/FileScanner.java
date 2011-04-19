package com.enonic.cms.core.plugin.deploy;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class FileScanner
{
    private final File dir;

    private final FileFilter filter;

    private Map<File, Long> files;

    private Set<File> deleted, modified, added;

    public FileScanner( final File dir, final FileFilter filter )
    {
        this.dir = dir;
        this.files = new HashMap<File, Long>();
        this.filter = filter;
    }

    private Map<File, Long> findFiles()
    {
        final HashMap<File, Long> map = new HashMap<File, Long>();
        findFiles( map, this.dir );
        return map;
    }

    private void findFiles( final Map<File, Long> map, final File dir )
    {
        if ( !dir.exists() )
        {
            return;
        }

        for ( File file : dir.listFiles( this.filter ) )
        {
            map.put( file, file.lastModified() );
        }
    }

    private void checkFiles( final Map<File, Long> oldFiles, final Map<File, Long> newFiles )
    {
        // First find all deleted
        this.deleted = new HashSet<File>();

        for ( File file : oldFiles.keySet() )
        {
            if ( !newFiles.containsKey( file ) )
            {
                this.deleted.add( file );
            }
        }

        // First find all modified
        final HashSet<File> commonFiles = new HashSet<File>( oldFiles.keySet() );
        commonFiles.retainAll( newFiles.keySet() );

        this.modified = new HashSet<File>();
        for ( File file : commonFiles )
        {
            if ( !oldFiles.get( file ).equals( newFiles.get( file ) ) )
            {
                this.modified.add( file );
            }
        }

        // First find all added
        this.added = new HashSet<File>();
        for ( File file : newFiles.keySet() )
        {
            if ( !oldFiles.containsKey( file ) )
            {
                this.added.add( file );
            }
        }
    }

    public Set<File> getModified()
    {
        return this.modified;
    }

    public Set<File> getDeleted()
    {
        return this.deleted;
    }

    public Set<File> getAdded()
    {
        return this.added;
    }

    public void scan()
    {
        final Map<File, Long> newFiles = findFiles();
        checkFiles( this.files, newFiles );
        this.files = newFiles;
    }
}
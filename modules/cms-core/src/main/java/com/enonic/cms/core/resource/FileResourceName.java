/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class FileResourceName
    implements Serializable
{
    private final String[] parts;

    private final String path;

    private static final String THIS_DIRECTORY = ".";

    private static final String PARENT_DIRECTORY = "..";

    public FileResourceName( String path )
    {
        this( split( path ) );
    }


    private String[] normalize( String[] parts )
    {

        List<String> normalizedParts = new ArrayList<String>();

        for ( int i = 0; i < parts.length; i++ )
        {
            String currentPart = parts[i];

            if ( currentPart.equals( "." ) )
            {
                continue;
            }

            if ( currentPart.equals( ".." ) )
            {
                if ( !normalizedParts.isEmpty() )
                {
                    normalizedParts.remove( normalizedParts.size() - 1 );
                }

                continue;
            }

            normalizedParts.add( currentPart );
        }

        return normalizedParts.toArray( new String[normalizedParts.size()] );
    }

    public FileResourceName( FileResourceName parent, String path )
    {
        this( parent.getPath() + "/" + path );
    }

    private FileResourceName( String[] parts )
    {
        this.parts = normalize( parts );
        this.path = join( this.parts );
    }

    public boolean isRoot()
    {
        return this.parts.length == 0;
    }

    public boolean isPublic()
    {
        return this.parts.length > 0 && this.parts[0].equals( "_public" );
    }

    public boolean isHidden()
    {
        return this.parts[this.parts.length - 1].startsWith( THIS_DIRECTORY );
    }

    public FileResourceName getParent()
    {
        if ( this.parts.length > 0 )
        {
            String[] tmp = new String[this.parts.length - 1];
            System.arraycopy( this.parts, 0, tmp, 0, tmp.length );
            return new FileResourceName( tmp );
        }
        else
        {
            return null;
        }
    }

    public String getPath()
    {
        return this.path;
    }

    public String getName()
    {
        if ( this.parts.length == 0 )
        {
            return "";
        }

        return this.parts[this.parts.length - 1];
    }

    public boolean equals( Object o )
    {
        return ( o instanceof FileResourceName ) && ( (FileResourceName) o ).path.equals( this.path );
    }

    public int hashCode()
    {
        return this.path.hashCode();
    }

    public String toString()
    {
        return this.path;
    }

    private static String[] split( String path )
    {
        if ( path == null )
        {
            return new String[0];
        }

        path = path.trim();
        if ( path.length() == 0 )
        {
            return new String[0];
        }

        ArrayList<String> list = new ArrayList<String>();
        for ( String part : path.split( "/" ) )
        {
            part = part.trim();
            if ( part.length() > 0 )
            {
                list.add( part );
            }
        }

        return list.toArray( new String[list.size()] );
    }

    private static String join( String[] parts )
    {
        if ( parts.length == 0 )
        {
            return "/";
        }

        StringBuffer str = new StringBuffer();
        for ( String part : parts )
        {
            str.append( "/" ).append( part );
        }

        return str.toString();
    }
}

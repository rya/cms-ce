/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class Path
{

    public static final Path ROOT = new Path( "/" );

    private String pathAsStringWithoutFragment;

    private String pathAsString;

    private String pathAsStringInLowerCase;

    private List<String> pathElements;

    private String fragment;

    public Path( String path )
    {
        this( path, false );
    }

    public Path( String path, boolean enforcePathStartsWithSlash )
    {

        if ( path == null )
        {
            throw new IllegalArgumentException( "Given path cannot be null" );
        }

        if ( enforcePathStartsWithSlash && ( path.length() > 0 && path.charAt( 0 ) != '/' ) )
        {
            this.pathAsString = "/" + path;
        }

        resolvePathAndFragment( path );

        initPathElements();
    }

    public Path( Collection<String> path, boolean startWithSlash )
    {
        this( path, startWithSlash, null );
    }

    public Path( Collection<String> path, boolean startWithSlash, String fragment )
    {

        StringBuffer s = new StringBuffer( 25 * path.size() );
        if ( startWithSlash )
        {
            s.append( "/" );
        }
        this.pathElements = new ArrayList<String>( path.size() );
        int i = 0;
        for ( String element : path )
        {
            this.pathElements.add( element );
            s.append( element );
            if ( i < path.size() - 1 )
            {
                s.append( "/" );
            }
            i++;
        }

        if ( fragment != null )
        {
            this.pathAsString = s.toString() + "#" + fragment;
        }
        else
        {
            this.pathAsString = s.toString();
        }
        this.pathAsStringWithoutFragment = pathAsString;
        this.fragment = fragment;
    }

    private void resolvePathAndFragment( String path )
    {
        path = path.trim();

        int fragmentStart = path.indexOf( "#" );
        if ( fragmentStart == -1 )
        {
            pathAsString = path;
            pathAsStringWithoutFragment = path;
        }
        else
        {
            pathAsStringWithoutFragment = path.substring( 0, fragmentStart );
            fragment = path.substring( fragmentStart + 1 );
            pathAsString = pathAsStringWithoutFragment + "#" + fragment;
        }
    }

    private void initPathElements()
    {

        pathElements = new ArrayList<String>();

        StringTokenizer pathTokenizer = new StringTokenizer( pathAsStringWithoutFragment, "/" );
        while ( pathTokenizer.hasMoreTokens() )
        {
            String element = pathTokenizer.nextToken();
            if ( element.length() > 0 )
            {
                pathElements.add( element );
            }
        }
    }

    public boolean isRoot()
    {
        return pathElements.size() == 0;
    }

    public List<String> getPathElements()
    {
        return Collections.unmodifiableList( pathElements );
    }

    public int getPathElementsCount()
    {
        return pathElements.size();
    }

    public String getPathElement( int index )
    {
        return pathElements.get( index );
    }

    public int numberOfElements()
    {
        return pathElements.size();
    }

    public String getLastPathElement()
    {
        return pathElements.get( pathElements.size() - 1 );
    }

    public String getFirstPathElement()
    {
        return pathElements.get( 0 );
    }

    public Path appendPathElement( final String pathElement )
    {
        if ( pathElement == null )
        {
            throw new IllegalArgumentException( "Given pathElement cannot be null" );
        }
        final String existingPathAsString = this.getPathWithoutFragmentAsString();

        StringBuffer buff = new StringBuffer();
        buff.append( existingPathAsString );
        if ( !existingPathAsString.endsWith( "/" ) )
        {
            buff.append( "/" );
        }
        buff.append( pathElement );
        if ( this.hasFragment() )
        {
            buff.append( "#" ).append( this.getFragment() );
        }

        Path newPath = new Path( buff.toString() );
        return newPath;
    }

    public Path appendPath( Path path )
    {

        StringBuffer newPath = new StringBuffer( toString() );
        if ( !endsWithSlash() && !isEmpty() && path.isRelative() )
        {
            newPath.append( "/" );
        }
        if ( path.isAbsolute() && endsWithSlash() )
        {
            newPath.append( path.toString().substring( 1 ) );
        }
        else
        {
            newPath.append( path );
        }

        return new Path( newPath.toString() );
    }

    public String getPathAsString()
    {
        return pathAsString;
    }

    public String getPathWithoutFragmentAsString()
    {
        return pathAsStringWithoutFragment;
    }

    public String getPathAsStringInLowerCase()
    {

        if ( pathAsStringInLowerCase == null )
        {
            pathAsStringInLowerCase = getPathAsString().toLowerCase();
        }

        return pathAsStringInLowerCase;
    }

    public boolean endsWithSlash()
    {
        return pathAsStringWithoutFragment.length() > 0 &&
            pathAsStringWithoutFragment.charAt( pathAsStringWithoutFragment.length() - 1 ) == '/';
    }

    public boolean endsWith( String suffix )
    {
        return pathAsStringWithoutFragment.endsWith( suffix );
    }

    public boolean endsWithIgnoreCase( String suffix )
    {
        return getPathAsStringInLowerCase().endsWith( suffix.toLowerCase() );
    }

    public boolean startsWithSlash()
    {
        return pathAsStringWithoutFragment.startsWith( "/" );
    }

    public boolean isAbsolute()
    {
        return pathAsStringWithoutFragment.startsWith( "/" );
    }

    public boolean isRelative()
    {
        return !pathAsStringWithoutFragment.startsWith( "/" );
    }

    public boolean isEmpty()
    {
        return pathAsString.length() == 0;
    }

    public boolean contains( String substring )
    {
        return this.pathAsStringWithoutFragment.contains( substring );
    }

    public Path substractLastPathElement()
    {
        final List<String> source = this.getPathElements();
        final List<String> newList = new ArrayList<String>( source.size() );
        int count = 0;
        for ( String sourceString : source )
        {
            newList.add( sourceString );
            count++;
            if ( count >= source.size() - 1 )
            {
                break;
            }
        }

        Path newPath = new Path( newList, this.startsWithSlash(), this.getFragment() );
        return newPath;
    }

    public Path subtractPath( String path )
    {
        if ( !this.toString().startsWith( path ) )
        {
            return new Path( this.toString() );
        }

        String newPath = pathAsStringWithoutFragment.substring( path.length(), pathAsStringWithoutFragment.length() );
        if ( hasFragment() )
        {
            newPath += "#" + getFragment();
        }
        return new Path( newPath );
    }

    public String subPath( int startPathElementIndex, int endPathElementIndex )
    {
        if ( startPathElementIndex < 0 )
        {
            throw new IllegalArgumentException( "Given startPathElementIndex cannot be negative" );
        }
        if ( endPathElementIndex > pathElements.size() )
        {
            throw new IllegalArgumentException(
                "Given endPathElementIndex cannot be larger than the path element count: " + pathElements.size() );
        }

        StringBuffer pathStr = new StringBuffer();

        for ( int i = startPathElementIndex; i < endPathElementIndex; i++ )
        {

            pathStr.append( getPathElement( i ) );

            if ( i < endPathElementIndex - 1 )
            {
                pathStr.append( "/" );
            }
        }

        return pathStr.toString();
    }

    public boolean startsWith( String s )
    {
        return pathAsString.startsWith( s );
    }

    public int length()
    {
        return toString().length();
    }

    public String getAsUrlEncoded( boolean includeFragment, String encoding )
    {

        StringBuffer s = new StringBuffer();
        try
        {
            if ( startsWithSlash() )
            {
                s.append( "/" );
            }
            final boolean endsWithSlash = endsWithSlash();
            for ( int i = 0; i < pathElements.size(); i++ )
            {
                s.append( URLEncoder.encode( pathElements.get( i ), encoding ) );

                if ( i == pathElements.size() - 1 && hasFragment() && includeFragment )
                {
                    String encodedFragment = URLEncoder.encode( fragment, encoding );
                    s.append( "#" );
                    s.append( encodedFragment );
                }

                if ( i < pathElements.size() - 1 || endsWithSlash )
                {
                    s.append( "/" );
                }
            }
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
        return s.toString();
    }

    public boolean hasFragment()
    {
        return fragment != null;
    }

    public String getFragment()
    {
        return fragment;
    }

    public int indexOf( String pathElement )
    {
        int index = -1;
        for ( String s : getPathElements() )
        {
            index++;
            if ( s.equals( pathElement ) )
            {
                return index;
            }
        }
        return -1;
    }


    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null || getClass() != obj.getClass() )
        {
            return false;
        }

        Path other = (Path) obj;

        if ( !pathAsString.equals( other.pathAsString ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return pathAsString.hashCode();
    }

    public String toString()
    {
        return pathAsString;
    }

    public Path removeTrailingSlash()
    {
        Path newPath = new Path( this.getPathElements(), this.startsWithSlash(), this.getFragment() );
        return newPath;
    }
}

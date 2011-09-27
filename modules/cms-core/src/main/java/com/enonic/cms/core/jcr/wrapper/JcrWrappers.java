/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.wrapper;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.Session;

public final class JcrWrappers
{
    public static JcrRepository wrap( final Repository value )
    {
        JcrRepositoryWrapper repo = new JcrRepositoryWrapper( value );
        repo.setPassword( "admin" );
        repo.setUserId( "admin" );
        return repo;
    }

    public static JcrSession wrap( final Session value )
    {
        return ( value == null ) ? null : new JcrSessionWrapper( value );
    }

    public static JcrNode wrap( final Node value )
    {
        return ( value == null ) ? null : new JcrNodeWrapper( value );
    }

    public static JcrNodeIterator wrap( NodeIterator nodeIterator )
    {
        return ( nodeIterator == null ) ? null : new JcrNodeIteratorWrapper( nodeIterator );
    }

    public static JcrBinary wrap( Binary value )
    {
        return ( value == null ) ? null : new JcrBinaryWrapper( value );
    }

    public static Session unwrap( final JcrSession value )
    {
        if ( value instanceof JcrSessionWrapper )
        {
            return ( (JcrSessionWrapper) value ).getWrappedSession();
        }
        else
        {
            throw new UnsupportedOperationException( "Unable to unwrap jcr session" );
        }
    }

    public static Node unwrap( final JcrNode value )
    {
        if ( value instanceof JcrNodeWrapper )
        {
            return ( (JcrNodeWrapper) value ).getWrappedNode();
        }
        else
        {
            throw new UnsupportedOperationException( "Unable to unwrap jcr node" );
        }
    }

}

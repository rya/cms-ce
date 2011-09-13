package com.enonic.cms.core.jcr.wrapper;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

class JcrSessionWrapper
    implements JcrSession
{
    private Session session;

    JcrSessionWrapper( Session session )
    {
        this.session = session;
    }

    @Override
    public JcrNode getRootNode()
    {
        try
        {
            return JcrWrappers.wrap( session.getRootNode() );
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public void save()
    {
        try
        {
            session.save();
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public JcrQuery createQuery( String statement )
    {
        return new JcrQueryWrapper( this, statement );
    }

    Session getSession()
    {
        return this.session;
    }
}

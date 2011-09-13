package com.enonic.cms.core.jcr.wrapper;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

class JcrRepositoryWrapper
    implements JcrRepository
{

    private Repository repository;

    private String userId;

    private String password;

    JcrRepositoryWrapper( Repository repository )
    {
        this.repository = repository;
    }


    @Override
    public JcrSession login()
    {
        try
        {
            Credentials credentials = new SimpleCredentials( userId, password.toCharArray() );
            Session session = this.repository.login( credentials );
            return JcrWrappers.wrap( session );
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public void logout( JcrSession session )
    {
        if ( session != null )
        {
            JcrWrappers.unwrap( session ).logout();
        }
    }

    public void setUserId( String userId )
    {
        this.userId = userId;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

}

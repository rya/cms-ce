package com.enonic.cms.core.jcr.wrapper;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

class JcrQueryWrapper
    implements JcrQuery
{
    private JcrSession session;

    private String statement;

    private Query query;

    private ValueFactory valueFactory;


    JcrQueryWrapper( JcrSession session, String statement )
    {
        this.session = session;
        this.statement = statement;
        this.query = createQuery();
    }

    private Query createQuery()
    {
        QueryManager queryManager;
        try
        {
            queryManager = JcrWrappers.unwrap( session ).getWorkspace().getQueryManager();
            return queryManager.createQuery( this.statement, javax.jcr.query.Query.JCR_SQL2 );
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public JcrNodeIterator execute()
    {
        try
        {
            QueryResult results = query.execute();
            NodeIterator nodes = results.getNodes();
            return JcrWrappers.wrap( nodes );
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public JcrQuery bindValue( String varName, Object value )
    {
        try
        {
            if ( this.valueFactory == null )
            {
                this.valueFactory = JcrWrappers.unwrap( session ).getValueFactory();
            }

            if ( value instanceof String )
            {
                this.query.bindValue( varName, valueFactory.createValue( (String) value ) );
            }
            else if ( value instanceof Integer )
            {
                this.query.bindValue( varName, valueFactory.createValue( (Integer) value ) );
            }
            else if ( value instanceof Long )
            {
                this.query.bindValue( varName, valueFactory.createValue( (Long) value ) );
            }
            else if ( value instanceof Boolean )
            {
                this.query.bindValue( varName, valueFactory.createValue( (Boolean) value ) );
            }
            else
            {
                throw new IllegalArgumentException(
                    "Cannot bind value of type [" + value.getClass().getName() + "] to '" + varName + "' for JCR query: " + statement );
            }

            return this;
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public JcrQuery setLimit( long limit )
    {
        query.setLimit( limit );
        return this;
    }

    @Override
    public JcrQuery setOffset( long offset )
    {
        query.setOffset( offset );
        return this;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.support;

import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.SessionFactory;
import com.enonic.cms.framework.jdbc.ConnectionDecorator;

/**
 * This class implements the connection factory.
 */
public final class ConnectionFactory
{
    private SessionFactory sessionFactory;
    private ConnectionDecorator decorator;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    public void setDecorator( ConnectionDecorator decorator )
    {
        this.decorator = decorator;
    }

    public Connection getConnection( boolean decorated )
        throws SQLException
    {
        final Connection conn = this.sessionFactory.getCurrentSession().connection();
        return decorated ? this.decorator.decorate( conn ) : conn;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.support.JdbcUtils;

import com.enonic.vertical.engine.handlers.CategoryHandler;
import com.enonic.vertical.engine.handlers.CommonHandler;
import com.enonic.vertical.engine.handlers.ContentHandler;
import com.enonic.vertical.engine.handlers.ContentObjectHandler;
import com.enonic.vertical.engine.handlers.GroupHandler;
import com.enonic.vertical.engine.handlers.LanguageHandler;
import com.enonic.vertical.engine.handlers.LogHandler;
import com.enonic.vertical.engine.handlers.MenuHandler;
import com.enonic.vertical.engine.handlers.PageHandler;
import com.enonic.vertical.engine.handlers.PageTemplateHandler;
import com.enonic.vertical.engine.handlers.SectionHandler;
import com.enonic.vertical.engine.handlers.SecurityHandler;
import com.enonic.vertical.engine.handlers.UserHandler;

import com.enonic.cms.store.support.ConnectionFactory;

public abstract class BaseEngine
{
    private ConnectionFactory connectionFactory;

    public void setConnectionFactory( ConnectionFactory connectionFactory )
    {
        this.connectionFactory = connectionFactory;
    }

    public CategoryHandler getCategoryHandler()
    {
        throw new RuntimeException();
    }

    public CommonHandler getCommonHandler()
    {
        throw new RuntimeException();
    }

    public ContentHandler getContentHandler()
    {
        throw new RuntimeException();
    }

    public ContentObjectHandler getContentObjectHandler()
    {
        throw new RuntimeException();
    }

    public GroupHandler getGroupHandler()
    {
        throw new RuntimeException();
    }

    public LanguageHandler getLanguageHandler()
    {
        throw new RuntimeException();
    }

    public LogHandler getLogHandler()
    {
        throw new RuntimeException();
    }

    public MenuHandler getMenuHandler()
    {
        throw new RuntimeException();
    }

    public SectionHandler getSectionHandler()
    {
        throw new RuntimeException();
    }

    public SecurityHandler getSecurityHandler()
    {
        throw new RuntimeException();
    }

    public PageHandler getPageHandler()
    {
        throw new RuntimeException();
    }

    public PageTemplateHandler getPageTemplateHandler()
    {
        throw new RuntimeException();
    }

    public UserHandler getUserHandler()
    {
        throw new RuntimeException();
    }

    /**
     * Tries to close a database result set.
     */
    public final void close( ResultSet resultSet )
    {
        JdbcUtils.closeResultSet( resultSet );
    }

    /**
     * Tries to close a database statement.
     */
    public final void close( Statement stmt )
    {
        JdbcUtils.closeStatement( stmt );
    }

    /**
     * Returns a connection.
     */
    public final Connection getConnection()
        throws SQLException
    {
        return this.connectionFactory.getConnection( true );
    }
}

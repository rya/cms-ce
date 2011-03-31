/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.filters;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.enonic.vertical.engine.BaseEngine;

public interface Filter
    extends Serializable
{

    /**
     * @return False if the resultset should be discarded.
     */
    boolean filter( BaseEngine engine, ResultSet resultSet )
        throws SQLException;

}

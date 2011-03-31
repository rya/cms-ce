/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.filters;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import com.enonic.vertical.engine.BaseEngine;
import com.enonic.vertical.engine.handlers.CategoryHandler;
import com.enonic.vertical.engine.handlers.GroupHandler;

import com.enonic.cms.domain.security.user.User;

public final class UnitFilter
    implements Filter
{

    private User user;

    public UnitFilter( User user )
    {
        this.user = user;
    }

    public boolean filter( BaseEngine engine, ResultSet resultSet )
        throws SQLException
    {

        // allways return false if user is enterprise admin
        if ( user.isEnterpriseAdmin() )
        {
            return false;
        }

        int unitKey = resultSet.getInt( "uni_lKey" );

        GroupHandler gHandler = engine.getGroupHandler();
        String[] groups = gHandler.getAllGroupMembershipsForUser( user );
        Arrays.sort( groups );
        String eaGroupKey = gHandler.getEnterpriseAdministratorGroupKey();
        String saGroupKey = gHandler.getAdminGroupKey();

        // allways return false if user is enterprise or site admin
        if ( Arrays.binarySearch( groups, eaGroupKey ) >= 0 || Arrays.binarySearch( groups, saGroupKey ) >= 0 )
        {
            return false;
        }

        Connection con = resultSet.getStatement().getConnection();

        CategoryHandler categoryHandler = engine.getCategoryHandler();
        boolean isCategories;
        isCategories = categoryHandler.hasCategoriesWithRights( con, user, unitKey, -1 );

        return !isCategories;
    }

}

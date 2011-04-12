/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.translator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.framework.hibernate.support.InClauseBuilder;
import com.enonic.cms.framework.hibernate.support.SelectBuilder;

import com.enonic.cms.core.content.ContentAccessEntity;

/**
 * This class implements the query translator.
 */
public abstract class AbstractQueryTranslator
{

    protected Map<String, Object> parameters = new HashMap<String, Object>();

    @SuppressWarnings({"unchecked"})
    protected void appendFilterQuery( SelectBuilder hqlQuery, String columnName, Collection keys )
    {

        if ( keys != null && keys.size() > 0 )
        {

            InClauseBuilder inClauseFilter = new InClauseBuilder<Object>( columnName, keys )
            {
                public void appendValue( StringBuffer sql, Object value )
                {
                    sql.append( value );
                }
            };
            hqlQuery.addFilter( "AND", inClauseFilter.toString() );
        }
    }

    protected void appendSecurityFilterQuery( SelectBuilder hqlQuery, String tableAlias, Collection<GroupKey> groupKeys )
    {

        if ( groupKeys == null || groupKeys.size() == 0 )
        {
            return;
        }

        String filter = tableAlias + ".contentKey IN (" + createSecurityFilterSelect( groupKeys ) + ")";
        hqlQuery.addFilter( "AND", filter );

    }

    protected String createSecurityFilterSelect( Collection<GroupKey> groupKeys )
    {
        StringBuffer str = new StringBuffer();
        str.append( "SELECT sec.content.key FROM " ).append( ContentAccessEntity.class.getName() ).append( " AS sec" );
        str.append( " WHERE sec.readAccess = 1 AND " );
        new InClauseBuilder<GroupKey>( "sec.group.key", groupKeys )
        {
            public void appendValue( StringBuffer sql, GroupKey value )
            {
                sql.append( "'" ).append( value ).append( "'" );
            }
        }.appendTo( str );

        return str.toString();
    }

}

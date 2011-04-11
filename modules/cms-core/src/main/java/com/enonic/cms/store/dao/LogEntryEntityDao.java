/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.log.*;
import org.hibernate.Query;
import org.hibernate.transform.ResultTransformer;

import com.enonic.cms.framework.hibernate.support.SelectBuilder;

import com.enonic.cms.domain.content.ContentEntity;

public class LogEntryEntityDao
    extends AbstractBaseEntityDao<LogEntryEntity>
    implements LogEntryDao
{

    @SuppressWarnings({"unchecked"})
    public List<LogEntryKey> findBySpecification( LogEntrySpecification specification, final String orderBy )
    {

        Query compiled =
            getHibernateTemplate().getSessionFactory().getCurrentSession().createQuery( getLogEntryKeysHQL( specification, orderBy ) );

        if ( specification.getDateFilter() != null )
        {
            compiled.setDate( "dateFilter", specification.getDateFilter() );
        }

        compiled.setCacheable( true );

        @SuppressWarnings({"unchecked"}) LogEntryResultTransformer transformer =
            new LogEntryResultTransformer( specification.isAllowDuplicateEntries() );

        return transformer.transformList( compiled.list() );

    }

    public LogEntryEntity findByKey( LogEntryKey key )
    {
        return get( LogEntryEntity.class, key );
    }


    private String getLogEntryKeysHQL( LogEntrySpecification specification, String orderBy )
    {

        final SelectBuilder hqlQuery = new SelectBuilder( 0 );

        hqlQuery.addSelect( "le.key" );
        hqlQuery.addSelectColumn( "le.keyValue" );
        hqlQuery.addSelectColumn( "le.timestamp" );
        hqlQuery.addFromTable( LogEntryEntity.class.getName(), "le", SelectBuilder.NO_JOIN, null );

        if ( specification instanceof ContentLogEntrySpecification )
        {
            ContentLogEntrySpecification conSpecification = (ContentLogEntrySpecification) specification;
            if ( !conSpecification.isAllowDeletedContent() )
            {
                applyNonDeletedContentOnlyFilter( specification, hqlQuery );
            }

        }

        applyTableTypeFilter( specification, hqlQuery );

        applyTypeFilter( specification, hqlQuery );

        applyUserFilter( specification, hqlQuery );

        applyDateFilter( specification, hqlQuery );

        String hql = hqlQuery.toString();

        if ( orderBy != null )
        {
            hqlQuery.addOrderBy( "le." + orderBy );
            hql = hqlQuery.toString();
            hql = hql + " GROUP BY ce.key";
        }

        return hql;
    }

    private void applyTableTypeFilter( LogEntrySpecification specification, SelectBuilder hqlQuery )
    {
        if ( specification.getTableTypes() != null && specification.getTableTypes().length > 0 )
        {

            hqlQuery.addFilter( "AND", "le.tableKey IN ( " + createLogTableTypeString( specification.getTableTypes() ) + " )" );
        }

    }

    private void applyDateFilter( LogEntrySpecification specification, SelectBuilder hqlQuery )
    {
        if ( specification.getDateFilter() != null )
        {
            hqlQuery.addFilter( "AND", "le.timestamp > :dateFilter" );
        }

    }

    private void applyNonDeletedContentOnlyFilter( LogEntrySpecification specification, SelectBuilder hqlQuery )
    {
        hqlQuery.addFromTable( ContentEntity.class.getName(), "ce", SelectBuilder.NO_JOIN, null );
        hqlQuery.addFilter( "AND", "ce.key = le.keyValue" );
        hqlQuery.addFilter( "AND", "ce.deleted = 0" );
    }

    private void applyUserFilter( LogEntrySpecification specification, SelectBuilder hqlQuery )
    {
        if ( specification.getUser() != null )
        {
            hqlQuery.addFilter( "AND", "le.user.key = '" + specification.getUser().getKey() + "'" );
        }
    }

    private void applyTypeFilter( LogEntrySpecification specification, SelectBuilder hqlQuery )
    {
        if ( specification.getTypes() != null && specification.getTypes().length > 0 )
        {

            hqlQuery.addFilter( "AND", "le.type IN ( " + createLogTypesString( specification.getTypes() ) + " )" );
        }
    }

    private String createLogTypesString( LogType[] types )
    {
        String typesString = "";
        for ( int i = 0; i < types.length; i++ )
        {
            typesString += types[i].asInteger();
            if ( ( i + 1 ) != types.length )
            {
                typesString += ",";
            }
        }
        return typesString;
    }

    private String createLogTableTypeString( Table[] tableTypes )
    {
        String typesString = "";
        for ( int i = 0; i < tableTypes.length; i++ )
        {
            typesString += tableTypes[i].asInteger();
            if ( ( i + 1 ) != tableTypes.length )
            {
                typesString += ",";
            }
        }
        return typesString;
    }


}

class LogEntryResultTransformer
    implements ResultTransformer
{

    private boolean allowDuplicates;

    public LogEntryResultTransformer( boolean duplicates )
    {
        allowDuplicates = duplicates;
    }

    @SuppressWarnings("unchecked")
    public List<LogEntryKey> transformList( List list )
    {

        if ( !allowDuplicates )
        {
            return nonDuplicateEntries( list );
        }
        return duplicateEntries( list );

    }

    private List<LogEntryKey> duplicateEntries( List list )
    {
        List<LogEntryKey> keysList = new ArrayList<LogEntryKey>();
        for ( Object object : list )
        {
            Object[] row = (Object[]) object;
            LogEntryKey key = (LogEntryKey) row[0];
            keysList.add( key );
        }

        return keysList;
    }

    private List<LogEntryKey> nonDuplicateEntries( List list )
    {
        List<LogEntryKey> distinctList = new ArrayList<LogEntryKey>();
        Set<Integer> added = new HashSet<Integer>();
        for ( Object object : list )
        {
            Object[] row = (Object[]) object;
            LogEntryKey key = (LogEntryKey) row[0];
            Integer contentKey = (Integer) row[1];
            if ( !isAlreadyAdded( contentKey, added ) )
            {
                distinctList.add( key );
                added.add( contentKey );
            }
        }

        return distinctList;
    }


    public Object transformTuple( Object[] arg0, String[] arg1 )
    {
        throw new IllegalStateException( "Not implemented" );
    }

    private boolean isAlreadyAdded( Integer key, Set<Integer> added )
    {
        if ( added.contains( key ) )
        {
            return true;
        }
        return false;

    }

}

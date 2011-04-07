/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import com.enonic.cms.domain.content.index.ContentIndexConstants;

public final class SearchStringBuffer
{
    private StringBuffer search = new StringBuffer();

    public enum Operator
    {
        AND,
        OR
    }

    public SearchStringBuffer appendTitle( String criteria )
    {
        return appendTitle( Operator.AND, criteria );
    }

    public SearchStringBuffer appendTitle( Operator operator, String criteria )
    {
        return appendContains( operator, "title", criteria );
    }

    public SearchStringBuffer appendData( String criteria )
    {
        return appendData( Operator.AND, criteria );
    }

    public SearchStringBuffer appendData( Operator operator, String criteria )
    {
        return appendContains( operator, "data/*", criteria );
    }

    public SearchStringBuffer appendAttachments( String criteria )
    {
        return appendAttachments( Operator.AND, criteria );
    }

    public SearchStringBuffer appendAttachments( Operator operator, String criteria )
    {
        return appendContains( operator, "fulltext", criteria );
    }

    public SearchStringBuffer appendKey( String criteria )
    {
        return appendKey( Operator.AND, criteria );
    }

    public SearchStringBuffer appendKey( Operator operator, String criteria )
    {
        return appendEquals( operator, "@key", criteria );
    }

    public SearchStringBuffer appendOwner( String criteria )
    {
        return appendOwner( Operator.AND, criteria );
    }

    public SearchStringBuffer appendOwner( Operator operator, String criteria )
    {
        return appendEquals( operator, ContentIndexConstants.F_OWNER_KEY, criteria );
    }

    public SearchStringBuffer appendModifier( String criteria )
    {
        return appendModifier( Operator.AND, criteria );
    }

    public SearchStringBuffer appendModifier( Operator operator, String criteria )
    {
        return appendEquals( operator, ContentIndexConstants.F_MODIFIER_KEY, criteria );
    }

    public SearchStringBuffer appendAssigneeQualifiedName( String criteria )
    {
        return appendEquals( Operator.AND, ContentIndexConstants.F_ASSIGNEE_QUALIFIEDNAME, criteria );
    }

    public SearchStringBuffer appendAssignerQualifiedName( String criteria )
    {
        return appendEquals( Operator.AND, ContentIndexConstants.F_ASSIGNER_QUALIFIEDNAME, criteria );
    }

    public SearchStringBuffer appendAssignmentDueDate( String operator, String criteria )
    {
        return doAppend( Operator.AND, ContentIndexConstants.F_ASSIGNMENT_DUE_DATE, operator, criteria );
    }

    public SearchStringBuffer appendNotEmpty( String field )
    {
        return doAppendRaw( Operator.AND, field + " != ''" );
    }

    public SearchStringBuffer appendStatus( String criteria )
    {
        return appendStatus( Operator.AND, criteria );
    }

    public SearchStringBuffer appendStatus( Operator operator, String criteria )
    {
        return appendEquals( operator, "@status", criteria );
    }


    public SearchStringBuffer appendCreated( String operator, String criteria )
    {
        return doAppend( Operator.AND, "@created", operator, criteria );
    }

    public SearchStringBuffer appendTimestamp( String operator, String criteria )
    {
        return doAppend( Operator.AND, "@timestamp", operator, criteria );
    }

    public SearchStringBuffer appendPublishFrom( String operator, String criteria )
    {
        return doAppend( Operator.AND, "@publishfrom", operator, criteria );
    }

    public SearchStringBuffer appendPublishTo( String operator, String criteria )
    {
        return doAppend( Operator.AND, "@publishto", operator, criteria );
    }

    public SearchStringBuffer appendRaw( String rawSearch )
    {
        return doAppendRaw( Operator.AND, rawSearch );
    }

    public SearchStringBuffer appendRaw( SearchStringBuffer search )
    {
        return doAppendRaw( Operator.AND, search.toString() );
    }

    public String toString()
    {
        return search.toString();
    }


    private SearchStringBuffer appendEquals( Operator operator, String field, String criteria )
    {
        return doAppend( operator, field, " = ", criteria );
    }

    private SearchStringBuffer appendContains( Operator operator, String field, String criteria )
    {
        return doAppend( operator, field, " CONTAINS ", "\"" + criteria + "\"" );
    }

    private SearchStringBuffer doAppendRaw( Operator operator, String rawSearch )
    {
        if ( rawSearch != null && rawSearch.trim().length() > 0 )
        {
            appendOperator( operator );
            search.append( "(" ).append( rawSearch ).append( ")" );
        }
        return this;
    }

    private SearchStringBuffer doAppend( Operator operator1, String field, String operator2, String criteria )
    {
        appendOperator( operator1 );
        search.append( field ).append( operator2 ).append( criteria );
        return this;
    }


    private void appendOperator( Operator o )
    {
        if ( search.length() > 0 )
        {
            if ( o == Operator.AND )
            {
                search.append( " AND " );
            }
            else if ( o == Operator.OR )
            {
                search.append( " OR " );
            }
        }
    }
}
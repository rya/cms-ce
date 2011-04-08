/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.expressionfunctions;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.List;

import com.enonic.cms.core.structure.menuitem.MenuItemService;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.core.preferences.PreferenceService;
import com.enonic.cms.portal.page.PageRequestFactory;

import com.enonic.cms.domain.portal.PortalInstanceKey;
import com.enonic.cms.domain.portal.datasource.expressionfunctions.ExpressionContext;
import com.enonic.cms.domain.preference.PreferenceEntity;
import com.enonic.cms.domain.preference.PreferenceKey;
import com.enonic.cms.domain.preference.PreferenceScope;
import com.enonic.cms.domain.preference.PreferenceScopeKey;
import com.enonic.cms.domain.preference.PreferenceScopeKeyResolver;
import com.enonic.cms.domain.preference.PreferenceScopeResolver;
import com.enonic.cms.domain.preference.PreferenceScopeType;
import com.enonic.cms.domain.structure.portlet.PortletKey;

/**
 *
 */
public class ExpressionFunctions
{
    private PreferenceService preferenceService;

    private ExpressionContext context;

    private TimeService timeService;

    private MenuItemService menuItemService;

    public boolean isnotblank( String str )
    {
        return StringUtils.isNotBlank( str );
    }

    public boolean isblank( String str )
    {
        return StringUtils.isBlank( str );
    }

    public boolean isnotempty( String str )
    {
        return StringUtils.isNotEmpty( str );
    }

    public boolean isempty( String str )
    {
        return StringUtils.isEmpty( str );
    }

    /**
     * Select the first value that is not null and not empty.
     *
     * @param s1 The first value checked
     * @param s2 The second value.
     * @return The first value, if it is not null or empty.  Otherwise, the second value, no matter what it is.
     */
    public String select( String s1, String s2 )
    {
        if ( s1 == null )
        {
            return s2;
        }
        else if ( s1.trim().length() > 0 )
        {
            return s1;
        }
        else
        {
            return s2;
        }
    }

    public String concat( String a, String b )
    {
        return a + b;
    }

    public String replace( String source, String regex, String replacement )
    {
        return source.replaceAll( regex, replacement );
    }

    public String substring( String source, int beginIndex, int endIndex )
    {
        return source.substring( beginIndex, endIndex );
    }

    public int stringlength( String source )
    {
        return source != null ? source.length() : 0;
    }

    public String lower( String source )
    {
        return source.toLowerCase();
    }

    public String upper( String source )
    {
        return source.toUpperCase();
    }

    public String trim( String source )
    {
        return source.trim();
    }

    public int min( int v1, int v2 )
    {
        return Math.min( v1, v2 );
    }

    public int max( int v1, int v2 )
    {
        return Math.max( v1, v2 );
    }

    public String currentDate( String format )
    {
        SimpleDateFormat fmt = new SimpleDateFormat( format );
        return fmt.format( timeService.getNowAsDateTime().toDate() );
    }

    public String currentDatePlusOffset( String format, String periodStr )
    {
        DateTime nowDateTime = timeService.getNowAsDateTime();
        PeriodFormatter periodFormatter = ISOPeriodFormat.standard();
        Period period = periodFormatter.parsePeriod( periodStr );
        DateTime offsetDateTime = nowDateTime.plus( period );

        SimpleDateFormat fmt = new SimpleDateFormat( format );
        return fmt.format( offsetDateTime.toDate() );
    }

    public String currentDateMinusOffset( String format, String periodStr )
    {
        DateTime nowDateTime = timeService.getNowAsDateTime();
        PeriodFormatter periodFormatter = ISOPeriodFormat.standard();
        Period period = periodFormatter.parsePeriod( periodStr );
        DateTime offsetDateTime = nowDateTime.minus( period );

        SimpleDateFormat fmt = new SimpleDateFormat( format );
        return fmt.format( offsetDateTime.toDate() );
    }

    public String periodHoursMinutes( int hours, int minutes )
    {
        Period period = new Period( hours, minutes, 0, 0 );
        return period.toString();
    }

    public String pref( String scope, String key )
    {

        if ( context.getUser() == null || context.getUser().isAnonymous() )
        {
            return "";
        }

        final PortletKey portletKey = PageRequestFactory.getPageRequest().getCurrentPortletKey();

        PreferenceEntity preferenceEntity = getPreferenceEntity( scope, portletKey, key );
        return preferenceEntity != null ? preferenceEntity.getValue() : "";
    }

    private PreferenceEntity getPreferenceEntity( String scope, PortletKey portletKey, String key )
    {
        PreferenceEntity preferenceEntity = null;
        PreferenceScopeKey scopeKey;
        PreferenceScopeType scopeType = PreferenceScopeType.parse( scope );
        PortalInstanceKey instanceKey = context.getPortalInstanceKey();
        if ( scopeType != null )
        {
            /* Scope supplied - try with supplied key and nothing else... */
            scopeKey = PreferenceScopeKeyResolver.resolve( scopeType, instanceKey, instanceKey.getSiteKey() );
            preferenceEntity = preferenceService.getPreference( new PreferenceKey( context.getUser().getKey(), scopeType, scopeKey, key ) );
        }
        else
        {
            /* No scope supplied - try to resolve scope and find preference... */
            List<PreferenceScope> scopes = PreferenceScopeResolver.resolveAllScopes( instanceKey, instanceKey.getSiteKey() );
            for ( int i = 0; i < scopes.size() && preferenceEntity == null; i++ )
            {
                preferenceEntity = preferenceService.getPreference( new PreferenceKey( context.getUser().getKey(), scopes.get( i ), key ) );
            }
        }
        return preferenceEntity;
    }

    /**
     * This method will take a freetext search string and create a valid query that can be used in the getContent* methods.  The search
     * string is spilt into tokens.  Using the operator, it may be specified whether the field must contain all or any of the words in the
     * search string.
     *
     * @param fieldName    The name of the field to search for the words in the search string.
     * @param searchString The words to search for.
     * @param operator     Must be either AND or OR.  Case doesn't matter.
     * @return A syntactically correct search that may be used as the query parameter in getContent* methods on the data source. With care,
     *         it may also be merged with other queries using AND or OR.
     * @throws IllegalArgumentException If any of the parameters are empty or the operator is not AND or OR.
     */
    public String buildFreetextQuery( String fieldName, String searchString, String operator )
    {
        if ( searchString == null || searchString.trim().equals( "" ) )
        {
            return "";
        }
        if ( fieldName == null || fieldName.trim().equals( "" ) )
        {
            throw new IllegalArgumentException( "fieldName can not be empty." );
        }

        String op = "";
        if ( operator != null )
        {
            op = operator.trim().toUpperCase();
        }
        if ( !( op.equals( "AND" ) || op.equals( "OR" ) ) )
        {
            throw new IllegalArgumentException( "Illegal operator: " + operator );
        }

        boolean first = true;
        StringBuffer queryTokens = new StringBuffer();
        Reader searchStringReader = new StringReader( searchString );
        StreamTokenizer searchStringTokens = new StreamTokenizer( searchStringReader );
        searchStringTokens.slashSlashComments( false );
        searchStringTokens.slashStarComments( false );
        searchStringTokens.eolIsSignificant( false );
        searchStringTokens.ordinaryChar( '!' );
        searchStringTokens.ordinaryChars( '#', '}' );
        searchStringTokens.wordChars( '!', '!' );
        searchStringTokens.wordChars( '#', '}' );

        try
        {
            while ( searchStringTokens.nextToken() != StreamTokenizer.TT_EOF )
            {
                String token = searchStringTokens.sval;
                addQueryPart( queryTokens, first, fieldName, token, op );
                if ( first )
                {
                    first = false;
                }
            }
        }
        catch ( IOException e )
        {
            throw new IllegalStateException( "This should never happen, since the IO class is wrapping a string!" );
        }

        return queryTokens.toString();
    }


    public String getPageKey()
    {
        return context.getMenuItem().getMenuItemKey().toString();
    }

    public String getWindowKey()
    {
        return context.getPortalInstanceKey().getWindowKey().asString();
    }

    private void addQueryPart( StringBuffer query, boolean first, String fieldName, String token, String op )
    {
        if ( !first )
        {
            query.append( ' ' );
            query.append( op );
            query.append( ' ' );
        }
        query.append( fieldName );
        query.append( " CONTAINS \"" );
        query.append( token );
        query.append( "\"" );
    }

    public String pageKey(String path)
    {
        return menuItemService.getPageKeyByPath(context.getMenuItem(), path);
    }

    public void setPreferenceService( PreferenceService preferenceService )
    {
        this.preferenceService = preferenceService;
    }

    public void setContext( ExpressionContext context )
    {
        this.context = context;
    }

    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }

    public void setMenuItemService(MenuItemService menuItemService)
    {
        this.menuItemService = menuItemService;
    }

}

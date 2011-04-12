/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.translator.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.ContentIndexEntity;
import com.enonic.cms.core.content.index.ContentIndexConstants;
import org.joda.time.ReadableDateTime;

import com.enonic.cms.core.content.index.queryexpression.ArrayExpr;
import com.enonic.cms.core.content.index.queryexpression.CombinedLogicalOrExpr;
import com.enonic.cms.core.content.index.queryexpression.CompareExpr;
import com.enonic.cms.core.content.index.queryexpression.Expression;
import com.enonic.cms.core.content.index.queryexpression.FieldExpr;
import com.enonic.cms.core.content.index.queryexpression.ValueExpr;
import com.enonic.cms.core.content.index.translator.ContentQueryTranslatorException;
import com.enonic.cms.core.content.index.util.ValueConverter;

public class CompareExprTranslator
{
    private ExpressionTranslator expressionTranslator;

    private Map<String, Object> parameters;

    private String valueSubSelectFilter;

    public CompareExprTranslator( ExpressionTranslator expressionTranslator )
    {
        this.expressionTranslator = expressionTranslator;
    }

    public String translate( CompareExpr expr )
    {

        List<CompareExpr> expressions = new ArrayList<CompareExpr>();
        expressions.add( expr );

        return doTranslateQuery( expressions );
    }

    private String doTranslateQuery( List<CompareExpr> expressions )
    {

        if ( expressions == null )
        {
            throw new IllegalArgumentException( "List of rightexpressions in query cannot be null" );
        }
        else if ( expressions.size() == 1 )
        {
            return handleSingleField( expressions.get( 0 ) );
        }
        else
        {
            return handleMultipleSameFields( expressions );
        }

    }

    private String handleMultipleSameFields( List<CompareExpr> expressions )
    {

        final CompareExpr compareExpr = expressions.get( 0 );
        final FieldExpr leftExpression = (FieldExpr) compareExpr.getLeft();
        final FieldName fieldName = new FieldName( leftExpression.getPath() );

        return handleSubSelectForMultipleFields( expressions, fieldName );


    }

//    private String handleMultipleRightWithoutSubSelect( List<CompareExpr> expressions, FieldName fieldName )
//    {
//        StringBuffer str = new StringBuffer();
//
//        for ( CompareExpr compareExpr : expressions )
//        {
//            ValueExpr[] values = getValues( compareExpr.getRight() );
//            boolean multiple = resolveMultiple( compareExpr.getOperator() );
//
//            boolean valueIsNotString = !values[0].isString();
//
//            boolean useTableAlias = false;
//            ColumnName columnName = expressionTranslator.translateFieldName( fieldName, valueIsNotString, useTableAlias );
//
//
//            str.append( columnName ).append( " " ).append( compareExpr.getToken() ).append( " " );
//
//            if ( !multiple )
//            {
//                str.append( values[0] );
//            }
//            else
//            {
//                str.append( "(" );
//                for ( int i = 0; i < values.length; i++ )
//                {
//                    if ( i > 0 )
//                    {
//                        str.append( "," );
//                    }
//
//                    str.append( values[i] );
//                }
//
//                str.append( ")" );
//            }
//        }
//
//        return str.toString();
//    }
//
//    private boolean resolveUseOfSubSelect( List<CompareExpr> expressions, final FieldName fieldName )
//    {
//
//        for ( CompareExpr expr : expressions )
//        {
//            ValueExpr[] values = getValues( expr.getRight() );
//
//            boolean valueIsNotString = !values[0].isString();
//
//            boolean useTableAlias = false;
//            ColumnName columnName = expressionTranslator.translateFieldName( fieldName, valueIsNotString, useTableAlias );
//
//            if ( !columnName.isOnRootTable() )
//            {
//                return true;
//            }
//        }
//        return false;
//    }

    private String handleSubSelectForMultipleFields( List<CompareExpr> expressions, FieldName fieldName )
    {

        StringBuffer str = new StringBuffer();

        int lastIndex = expressions.size();
        int index = 1;

        for ( CompareExpr compareExpr : expressions )
        {

            ValueExpr[] values = getValues( compareExpr.getRight() );
            boolean multiple = resolveMultiple( compareExpr.getOperator() );

            boolean valueIsNotString = !values[0].isString();

            boolean useTableAlias = false;
            ColumnName columnName = expressionTranslator.translateFieldName( fieldName, valueIsNotString, useTableAlias );

            boolean emptyStrAsHash = ( compareExpr.getOperator() == CompareExpr.EQ ) || ( compareExpr.getOperator() == CompareExpr.NEQ );
            boolean valueIsTyped = !columnName.isOnRootTable() && valueIsNotString;
            String[] strValues = translateValues( fieldName, values, valueIsTyped, emptyStrAsHash );

            str.append( columnName ).append( " " ).append( compareExpr.getToken() ).append( " " );

            if ( !multiple )
            {
                str.append( strValues[0] );
            }
            else
            {
                str.append( "(" );
                for ( int i = 0; i < strValues.length; i++ )
                {
                    if ( i > 0 )
                    {
                        str.append( "," );
                    }

                    str.append( strValues[i] );
                }

                str.append( ")" );
            }

            if ( index != lastIndex )
            {
                str.append( " OR " );
            }
            index++;
        }

        StringBuffer subs = new StringBuffer();
        subs.append( "x.contentKey IN ( " );
        subs.append( "SELECT contentKey FROM " ).append( ContentIndexEntity.class.getName() ).append( " WHERE " );
        if ( valueSubSelectFilter != null && valueSubSelectFilter.length() > 0 )
        {
            subs.append( valueSubSelectFilter );
            subs.append( " AND " );
        }

        subs.append( "path LIKE '" ).append( fieldName.getTranslatedFieldName() ).append( "'" );
        subs.append( " AND (" ).append( str.toString() + ")" );
        subs.append( ")" );

        return subs.toString();
    }

    private String handleSingleField( CompareExpr expr )
    {

        final FieldExpr leftExpression = (FieldExpr) expr.getLeft();
        final FieldName fieldName = new FieldName( leftExpression.getPath() );

        int op = expr.getOperator();
        ValueExpr[] values = getValues( expr.getRight() );

        boolean valueIsNotString = !values[0].isString();

        boolean useTableAlias = false;
        ColumnName columnName = expressionTranslator.translateFieldName( fieldName, valueIsNotString, useTableAlias );

        boolean useSubSelect = !columnName.isOnRootTable();

        boolean emptyStrAsHash = ( op == CompareExpr.EQ ) || ( op == CompareExpr.NEQ );
        boolean valueIsTyped = !columnName.isOnRootTable() && valueIsNotString;
        String[] strValues = translateValues( fieldName, values, valueIsTyped, emptyStrAsHash );

        if ( useSubSelect )
        {
            return handleSubSelectForSingleField( fieldName, columnName, expr.getToken(), strValues,
                                                  resolveMultiple( expr.getOperator() ) );
        }
        else
        {
            return handleSingleFieldWithoutSubSelect( fieldName, columnName, expr.getToken(), strValues,
                                                      resolveMultiple( expr.getOperator() ) );
        }
    }

    private boolean resolveMultiple( int op )
    {
        switch ( op )
        {
            case CompareExpr.EQ:
                return false;
            case CompareExpr.NEQ:
                return false;
            case CompareExpr.GT:
                return false;
            case CompareExpr.GTE:
                return false;
            case CompareExpr.LT:
                return false;
            case CompareExpr.LTE:
                return false;
            case CompareExpr.IN:
                return true;
            case CompareExpr.LIKE:
                return false;
            case CompareExpr.NOT_IN:
                return true;
            case CompareExpr.NOT_LIKE:
                return false;
            default:
                throw new ContentQueryTranslatorException( "Unsupported operation: " + op );
        }
    }

    private ValueExpr[] getValues( Expression expr )
    {
        if ( expr instanceof ValueExpr )
        {
            return new ValueExpr[]{(ValueExpr) expr};
        }
        else if ( expr instanceof ArrayExpr )
        {
            return ( (ArrayExpr) expr ).getValues();
        }
        else
        {
            throw new ContentQueryTranslatorException( expr.getClass().getName() + " not allowed here" );
        }
    }

    private String[] translateValues( FieldName fieldName, ValueExpr[] values, boolean isTyped, boolean emptyStrAsHash )
    {
        boolean isString = isTyped || values[0].isString();
        String[] list = new String[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            list[i] = translateValue( fieldName, values[i], isString, isTyped, emptyStrAsHash );
        }

        return list;
    }

    private String translateValue( FieldName fieldName, ValueExpr expr, boolean isString, boolean isTyped, boolean emptyStrAsHash )
    {

        String strValue;
        Object value = expr.getValue();
        boolean isDate = false;
        if ( value instanceof ReadableDateTime )
        {
            strValue = translateValue( (ReadableDateTime) value, isTyped );
            isDate = true;
        }
        else if ( value instanceof Number )
        {
            strValue = translateValue( ( (Number) value ).doubleValue(), isTyped );
        }
        else if ( fieldName.getUntranslatedFieldName().equals( ContentIndexConstants.F_OWNER_QUALIFIEDNAME ) ||
            fieldName.getUntranslatedFieldName().equals( ContentIndexConstants.F_MODIFIER_QUALIFIEDNAME ) ||
            fieldName.getUntranslatedFieldName().equals( ContentIndexConstants.F_ASSIGNEE_QUALIFIEDNAME ) ||
            fieldName.getUntranslatedFieldName().equals( ContentIndexConstants.F_ASSIGNER_QUALIFIEDNAME ) )
        {
            // have to replace any back slashed with colon, since it is stored as colon
            strValue = value.toString().replace( "\\", ":" );
        }
        else
        {
            strValue = value.toString();
        }

        if ( isString )
        {
            strValue = strValue.trim().toLowerCase();
            String placeHolderName = "v" + parameters.size();

            if ( ( strValue.length() == 0 ) && emptyStrAsHash )
            {
                strValue = ContentIndexConstants.BLANK_REPLACER;
            }

            parameters.put( placeHolderName, strValue );
            return ":" + placeHolderName;
        }
        else if ( isDate )
        {
            String placeHolderName = "v" + parameters.size();

            if ( ( strValue.length() == 0 ) && emptyStrAsHash )
            {
                strValue = ContentIndexConstants.BLANK_REPLACER;
            }

            parameters.put( placeHolderName, ( (ReadableDateTime) value ).toDateTime().toDate() );
            return ":" + placeHolderName;
        }
        else
        {
            strValue = strValue.replace( "'", "\\\'" ).trim().toLowerCase();
            return strValue;
        }
    }

    private String handleSingleFieldWithoutSubSelect( FieldName fieldName, ColumnName columnName, String operator, String[] values,
                                                      boolean multiple )
    {
        StringBuffer str = new StringBuffer();
        if ( !columnName.isOnRootTable() )
        {
            str.append( "(" );
            createPathCondition( str, fieldName, columnName.getAlias() );
            str.append( " AND " );
        }
        str.append( columnName ).append( " " ).append( operator ).append( " " );

        if ( !multiple )
        {
            str.append( values[0] );
        }
        else
        {
            str.append( "(" );
            for ( int i = 0; i < values.length; i++ )
            {
                if ( i > 0 )
                {
                    str.append( "," );
                }

                str.append( values[i] );
            }

            str.append( ")" );
        }

        if ( !columnName.isOnRootTable() )
        {
            str.append( ")" );
        }
        return str.toString();
    }

    private String handleSubSelectForSingleField( FieldName fieldName, ColumnName columnName, String operator, String[] values,
                                                  boolean multiple )
    {

        StringBuffer str = new StringBuffer();
        str.append( columnName ).append( " " ).append( operator ).append( " " );

        if ( !multiple )
        {
            str.append( values[0] );
        }
        else
        {
            str.append( "(" );
            for ( int i = 0; i < values.length; i++ )
            {
                if ( i > 0 )
                {
                    str.append( "," );
                }

                str.append( values[i] );
            }

            str.append( ")" );
        }

        StringBuffer subs = new StringBuffer();
        subs.append( "x.contentKey IN ( " );
        subs.append( "SELECT contentKey FROM " ).append( ContentIndexEntity.class.getName() ).append( " WHERE " );
        if ( valueSubSelectFilter != null && valueSubSelectFilter.length() > 0 )
        {
            subs.append( valueSubSelectFilter );
            subs.append( " AND " );
        }

        subs.append( "path LIKE '" ).append( fieldName.getTranslatedFieldName() ).append( "'" );
        subs.append( " AND " ).append( str.toString() );
        subs.append( ")" );

        return subs.toString();
    }

    private boolean createPathCondition( StringBuffer str, FieldName fieldName, String tableAlias )
    {
        str.append( tableAlias ).append( ".path" );

        if ( fieldName.getUntranslatedFieldName().contains( "*" ) )
        {
            str.append( " LIKE '" ).append( fieldName.getTranslatedFieldName() ).append( "'" );
        }
        else
        {
            str.append( " = '" ).append( fieldName.getTranslatedFieldName() ).append( "'" );
        }
        return true;
    }

    private String translateValue( ReadableDateTime value, boolean isTyped )
    {
        return isTyped
            ? ValueConverter.toTypedString( value.toDateTime().toDate() )
            : ValueConverter.toString( value.toDateTime().toDate() );
    }

    private String translateValue( double value, boolean isTyped )
    {
        return isTyped ? ValueConverter.toTypedString( value ) : ValueConverter.toString( value );
    }

    public void setParameters( Map<String, Object> parameters )
    {
        this.parameters = parameters;
    }

    public void setValueSubSelectFilter( String valueSubSelectFilter )
    {
        this.valueSubSelectFilter = valueSubSelectFilter;
    }

    public String translateQueryForSameFields( CombinedLogicalOrExpr combinedExpr )
    {

        final List<CompareExpr> compareExpressions = combinedExpr.getExpressions();
        return doTranslateQuery( compareExpressions );

    }
}

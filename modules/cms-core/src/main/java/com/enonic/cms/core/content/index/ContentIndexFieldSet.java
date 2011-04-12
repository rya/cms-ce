/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.enonic.cms.core.content.ContentIndexEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import org.joda.time.ReadableDateTime;

import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.util.ValueConverter;

/**
 * This class implements the field set.
 */
public final class ContentIndexFieldSet
    implements ContentIndexConstants
{

    public final static int SPLIT_TRESHOLD = 512;

    private final static int ORDER_TRESHOLD = 15;

    private final static String LINE_SEPARATOR = System.getProperty( "line.separator" );

    private ContentKey key;

    private CategoryKey categoryKey;

    private ContentTypeKey contentTypeKey;

    private Integer status;

    private Date publishFrom;

    private Date publishTo;

    private final ArrayList<ContentIndexEntity> entities = new ArrayList<ContentIndexEntity>();

    private final HashMap<String, List<ContentIndexEntity>> entitiesByPath = new HashMap<String, List<ContentIndexEntity>>();

    public void setKey( ContentKey key )
    {
        this.key = key;
    }

    public void setCategoryKey( CategoryKey value )
    {
        this.categoryKey = value;
    }

    public void setContentTypeKey( ContentTypeKey value )
    {
        this.contentTypeKey = value;
    }

    public void setStatus( Integer value )
    {
        this.status = value;
    }

    public void setPublishFrom( Date value )
    {
        this.publishFrom = value;
    }

    public void setPublishTo( Date value )
    {
        this.publishTo = value;
    }

    public void addFieldWithIntegerValue( String fieldName, int value )
    {

        fieldName = FieldHelper.translateFieldName( fieldName );
        addSingleEntity( fieldName, ValueConverter.toString( value ), value );
    }

    public void addFieldWithStringValue( String fieldName, String value )
    {

        addField( fieldName, value, null );
    }

    public void addFieldWithStringValue( String fieldName, String value, String defaultValue )
    {

        if ( value == null )
        {
            addField( fieldName, defaultValue, null );
        }
        else
        {
            addField( fieldName, value, defaultValue );
        }
    }

    public void addFieldWithDateValue( String fieldName, Date value, String defaultValue )
    {

        if ( value == null )
        {
            addField( fieldName, defaultValue, null );
        }
        else
        {
            fieldName = FieldHelper.translateFieldName( fieldName );
            addSingleEntity( fieldName, ValueConverter.toString( value ), value );
        }
    }

    public void addFieldWithBigTextValue( String fieldName, BigText value )
    {

        fieldName = FieldHelper.translateFieldName( fieldName );

        String orderValue = value.getText();
        List<String> strings = value.getTextSplitted( SPLIT_TRESHOLD, LINE_SEPARATOR );
        for ( String str : strings )
        {
            str = str.trim();
            if ( str.length() > 0 )
            {
                addSingleEntity( fieldName, str, orderValue, null );
            }
        }
    }

    public void addFieldWithAnyValue( String fieldName, String value )
    {

        addField( fieldName, value, null );
    }

    private void addField( String fieldName, String value, String defaultValue )
    {

        fieldName = FieldHelper.translateFieldName( fieldName );

        if ( value == null )
        {
            value = defaultValue;
        }

        if ( value.length() > SPLIT_TRESHOLD )
        {
            // value too big, we split it with the full text split technology :)
            BigText bigTextValue = new BigText( value );
            String orderValue = bigTextValue.getText();
            for ( String string : bigTextValue.getTextSplitted( SPLIT_TRESHOLD, LINE_SEPARATOR ) )
            {
                if ( string.trim().length() > 0 )
                {
                    addSingleEntity( fieldName, string, orderValue, null );
                }
            }
        }
        else
        {
            ReadableDateTime dateTime = ValueConverter.toDate( value );

            Double num = ValueConverter.toDouble( value );

            if ( dateTime != null )
            {
                addSingleEntity( fieldName, value, new Date( dateTime.getMillis() ) );
            }
            else if ( num != null )
            {

                addSingleEntity( fieldName, value, num.floatValue() );
            }
            else
            {

                addSingleEntity( fieldName, value, value, null );
            }
        }
    }

    public List<ContentIndexEntity> getEntitites()
    {
        return this.entities;
    }

    public HashMap<String, List<ContentIndexEntity>> getEntitiesByPath()
    {
        return entitiesByPath;
    }

    private void addSingleEntity( String fieldName, String value, Date orderValue )
    {
        addSingleEntity( fieldName, value, ValueConverter.toTypedString( orderValue ), null );
    }

    private void addSingleEntity( String fieldName, String value, float orderValue )
    {
        addSingleEntity( fieldName, value, ValueConverter.toTypedString( orderValue ), orderValue );
    }

    private void addSingleEntity( String fieldName, String value, String orderValue, Float numValue )
    {

        if ( value == null || value.length() == 0 )
        {
            throw new IllegalArgumentException( "Given value cannot be null or empty, fieldName was: " + fieldName );
        }

        ContentIndexEntity contentIndex = new ContentIndexEntity();
        contentIndex.setKey( generateKey() );
        contentIndex.setContentKey( key );
        contentIndex.setContentStatus( status );
        contentIndex.setPublishFrom( publishFrom );
        contentIndex.setPublishTo( publishTo );
        contentIndex.setCategoryKey( categoryKey );
        contentIndex.setContentTypeKey( contentTypeKey.toInt() );
        contentIndex.setPath( fieldName );
        contentIndex.setValue( value.toLowerCase() );
        contentIndex.setNumValue( numValue );

        if ( orderValue.length() > ORDER_TRESHOLD )
        {
            orderValue = orderValue.substring( 0, ORDER_TRESHOLD );
        }

        contentIndex.setOrderValue( orderValue.toLowerCase() );
        this.entities.add( contentIndex );
        addEntityByPath( fieldName, contentIndex );
    }

    public ContentIndexFieldSet()
    {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    private void addEntityByPath( String fieldName, ContentIndexEntity contentIndex )
    {
        List<ContentIndexEntity> existing = entitiesByPath.get( fieldName );
        if ( existing == null )
        {
            List<ContentIndexEntity> newList = new ArrayList<ContentIndexEntity>();
            newList.add( contentIndex );
            entitiesByPath.put( fieldName, newList );
        }
        else
        {
            existing.add( contentIndex );
        }
    }

    /**
     * @return A 36 char long unique key.
     */
    private String generateKey()
    {
        return UUID.randomUUID().toString();
    }
}

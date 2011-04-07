/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.criteria;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: jvs Date: 12.jun.2003 Time: 16:49:17 To change this template use Options | File Templates.
 */
public class CategoryCriteria
    extends Criteria
    implements Serializable
{

    private Integer siteKey = null;

    private Integer unitKey = null;

    private boolean includeAllUnits = false;

    private boolean useDisableAttribute = true;

    private boolean useRootElement = true;

    private List<Integer> contentTypes = null;

    private int contentTypeCriteriaType = Criteria.AND;

    private List<Integer> categoryKeys = null;

    private int categoryKey = -1;


    public CategoryCriteria()
    {

    }

    public String toString()
    {
        StringBuffer stringRep = new StringBuffer();
        stringRep.append( "CategoryCriteria[siteKey=" );
        stringRep.append( siteKey );
        stringRep.append( ";unitKey=" );
        stringRep.append( unitKey );
        stringRep.append( ";includeAllUnits=" );
        stringRep.append( includeAllUnits );
        stringRep.append( ";useDisableAttribute=" );
        stringRep.append( useDisableAttribute );
        stringRep.append( ";useRootElement=" );
        stringRep.append( useRootElement );
        stringRep.append( ";contentTypes=" );
        stringRep.append( contentTypes );
        stringRep.append( ";contentTypeCriteriaType=" );
        stringRep.append( contentTypeCriteriaType );
        stringRep.append( ";categoryKeys=" );
        stringRep.append( categoryKeys );
        stringRep.append( ";categoryKey=" );
        stringRep.append( categoryKey );
        stringRep.append( "]" );
        return stringRep.toString();
    }


    public List<Integer> getContentTypes()
    {
        return contentTypes;
    }

    public boolean includeAllUnits()
    {
        return includeAllUnits;
    }

    public boolean hasUnitKey()
    {
        return ( unitKey != null );
    }

    public int getUnitKey()
    {
        return unitKey;
    }

    public Integer getUnitKeyAsInteger()
    {
        return unitKey;
    }

    public void setCategoryKey( int value )
    {
        categoryKey = value;
    }

    public int getCategoryKey()
    {
        return categoryKey;
    }

    public List<Integer> getCategoryKeys()
    {
        return categoryKeys;
    }

    public boolean useDisableAttribute()
    {
        return useDisableAttribute;
    }

    public void setUseDisableAttribute( boolean value )
    {
        useDisableAttribute = value;
    }

    public boolean useRootElement()
    {
        return useRootElement;
    }

}

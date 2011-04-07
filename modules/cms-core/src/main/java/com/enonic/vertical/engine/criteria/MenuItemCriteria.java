/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.criteria;

import java.io.Serializable;

/**
 * User: jvs Date: 20.mai.2003 Time: 15:35:18
 */
public class MenuItemCriteria
    extends Criteria
    implements Serializable
{
    private static final long serialVersionUID = 1234356L;

    private int menuKey = -1;

    private Boolean update = null;

    private Boolean delete = null;

    private Boolean create = null;

    private Boolean administrate = null;

    private Boolean publish = null;

    private Boolean add = null;

    private boolean applySecurity = true;

    private boolean disableMenuItemLoadingForUnspecified = false;

    private MenuItemCriteria parentCriteria = null;

    public MenuItemCriteria( int type )
    {
        setType( type );
    }

    public boolean includePublish()
    {
        return ( publish != null ? true : false );
    }

    public int getPublishAsInt()
    {
        if ( publish != null )
        {
            return booleanToInt( publish.booleanValue() );
        }
        else
        {
            return -1;
        }
    }

    public boolean includeAdd()
    {
        return ( add != null ? true : false );
    }

    public int getAddAsInt()
    {
        if ( add != null )
        {
            return booleanToInt( add.booleanValue() );
        }
        else
        {
            return -1;
        }
    }

    public boolean includeUpdate()
    {
        return ( update != null ? true : false );
    }

    public int getUpdateAsInt()
    {
        if ( update != null )
        {
            return booleanToInt( update.booleanValue() );
        }
        else
        {
            return -1;
        }
    }

    public boolean includeAdministrate()
    {
        return ( administrate != null ? true : false );
    }

    public int getAdministrateAsInt()
    {
        if ( administrate != null )
        {
            return booleanToInt( administrate.booleanValue() );
        }
        else
        {
            return -1;
        }
    }

    public boolean includeCreate()
    {
        return ( create != null ? true : false );
    }

    public int getCreateAsInt()
    {
        if ( create != null )
        {
            return booleanToInt( create.booleanValue() );
        }
        else
        {
            return -1;
        }
    }

    public boolean includeDelete()
    {
        return ( delete != null ? true : false );
    }

    public int getDeleteAsInt()
    {
        if ( delete != null )
        {
            return booleanToInt( delete.booleanValue() );
        }
        else
        {
            return -1;
        }
    }

    public boolean applySecurity()
    {
        return applySecurity;
    }

    public boolean hasParentCriteria()
    {
        return ( parentCriteria != null ? true : false );
    }

    public MenuItemCriteria getParentCriteria()
    {
        return parentCriteria;
    }

    public boolean hasMenuKey()
    {
        return ( menuKey != -1 ) ? true : false;
    }

    public Integer getMenuKeyAsInteger()
    {
        return new Integer( menuKey );
    }

    public int getMenuKey()
    {
        return menuKey;
    }

    public boolean getDisableMenuItemLoadingForUnspecified()
    {
        return disableMenuItemLoadingForUnspecified;
    }
}

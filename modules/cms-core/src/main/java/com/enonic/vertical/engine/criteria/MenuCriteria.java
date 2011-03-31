/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.criteria;

import java.io.Serializable;

/**
 * User: jvs Date: 20.mai.2003 Time: 15:35:18
 */
public class MenuCriteria
    extends Criteria
    implements Serializable
{

    private Boolean update = null;

    private int updateAsInt = -1;

    private Boolean delete = null;

    private int deleteAsInt = -1;

    private Boolean create = null;

    private int createAsInt = -1;

    private Boolean administrate = null;

    private int administrateAsInt = -1;

    private boolean applySecurity = true;

    public MenuCriteria( int type )
    {
        setType( type );
    }

    public boolean includeUpdate()
    {
        return ( update != null ? true : false );
    }

    public int getUpdateAsInt()
    {
        return updateAsInt;
    }

    public boolean includeAdministrate()
    {
        return ( administrate != null ? true : false );
    }

    public int getAdministrateAsInt()
    {
        return administrateAsInt;
    }

    public boolean includeCreate()
    {
        return ( create != null ? true : false );
    }

    public int getCreateAsInt()
    {
        return createAsInt;
    }

    public boolean includeDelete()
    {
        return ( delete != null ? true : false );
    }

    public int getDeleteAsInt()
    {
        return deleteAsInt;
    }

    public boolean applySecurity()
    {
        return applySecurity;
    }
}

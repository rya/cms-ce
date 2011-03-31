/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.criteria;

import java.io.Serializable;

/**
 * User: jvs Date: 21.mai.2003 Time: 11:10:29
 * <p/>
 * This class is used as a super class for: CategoryCriteria, MenuCriteria and MenuItemCritera. The class is used for giving information
 * about to different get methods (on categories and menus).
 */
public class Criteria
    implements Serializable
{

    public final static int NONE = 0;

    public final static int AND = 1;

    public final static int OR = 2;

    private final static int TRUE = 1;

    private final static int FALSE = 0;

    public int type = NONE;

    public void setType( int type )
    {
        this.type = type;
    }

    public int getType()
    {
        return type;
    }

    public String getBinding( int value )
    {
        switch ( value )
        {
            case AND:
                return " AND";
            case OR:
                return " OR";
            default:
                return "";
        }
    }

    public String getBinding()
    {
        return getBinding( type );
    }


    protected int booleanToInt( boolean value )
    {
        return ( value ? TRUE : FALSE );
    }

}

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

    public MenuCriteria( int type )
    {
        setType( type );
    }

}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;


public class IllegalQueryException
    extends RuntimeException
{


    public IllegalQueryException( String message, String query )
    {
        super( message + ", query was: " + query );
    }
}

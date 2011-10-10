/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rmy - Date: Sep 3, 2009
 */
public class UserStorageInvalidArgumentException
    extends RuntimeException
{
    private String message;

    public UserStorageInvalidArgumentException( String invalidArgument )
    {
        List<String> invalidArguments = new ArrayList<String>();
        invalidArguments.add( invalidArgument );
        setErrorMessage( invalidArguments, " Invalid argument in user storage-operation: " );
    }

    public UserStorageInvalidArgumentException( List<String> invalidArguments )
    {
        setErrorMessage( invalidArguments, "Invalid arguments in user storage-operation: " );
    }

    public UserStorageInvalidArgumentException( List<String> invalidArguments, String messageText )
    {
        setErrorMessage( invalidArguments, messageText );
    }

    private void setErrorMessage( List<String> invalidArguments, String messageText )
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( messageText );

        boolean isFirst = true;

        for ( String invalidArgument : invalidArguments )
        {
            if ( isFirst )
            {
                isFirst = false;
            }
            else
            {
                buffer.append( ", " );
            }
            buffer.append( invalidArgument );
        }
        message = buffer.toString();
    }

    public String getMessage()
    {
        return message;
    }

}

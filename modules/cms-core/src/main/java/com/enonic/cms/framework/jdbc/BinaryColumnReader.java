/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class BinaryColumnReader
{
    public static byte[] readBinary( String columnName, ResultSet resultSet )
        throws SQLException
    {
        InputStream inputStream = resultSet.getBinaryStream( columnName );
        if ( inputStream == null )
        {
            return null;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( 2048 );
        byte[] buffer = new byte[2048];

        try
        {
            while ( true )
            {
                int amountRead = inputStream.read( buffer );
                if ( amountRead == -1 )
                {
                    break;
                }
                outputStream.write( buffer, 0, amountRead );
            }

            inputStream.close();
            outputStream.close();
        }
        catch ( IOException e )
        {
            throw new SQLException( "Failed to read xml from stream" );
        }

        return outputStream.toByteArray();
    }
}

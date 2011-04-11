/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jdbc;

import java.sql.Connection;
import java.sql.Statement;

import com.enonic.esl.util.ArrayUtil;
import com.enonic.esl.util.Base64Util;
import com.enonic.esl.util.DigestUtil;
import com.enonic.esl.util.UUID;

import com.enonic.cms.domain.security.userstore.UserStoreKey;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Nov 14, 2010
 * Time: 11:52:52 PM
 */
public abstract class DatabaseBaseValuesInitializer
{
    /**
     * This class should not contain any logic that could be model-version dependent,
     * e.g SQL insert-statements.
     */
    protected DatabaseBaseValuesInitializer()
    {
    }

    public abstract void initializeDatabaseValues( Connection conn )
        throws Exception;

    /**
     * Returns the InitDatabaseHandler for the current model.
     * No logic for different modelNumbers at the moment, but when changes to the database
     * init-values will occure, this should be implemented, e.g "if modelNumber >= 121, return new InitDatabase002
     */
    public final static DatabaseBaseValuesInitializer getDatabaseBaseValuesInitializer( int modelNumber )
    {
        return new DatabaseBaseValuesInitializer001();
    }


    public String generateUserKey( UserStoreKey userStoreKey, String syncValue )
    {
        byte[] newSyncByteArray;
        if ( userStoreKey != null )
        {
            newSyncByteArray = ArrayUtil.concat( String.valueOf( userStoreKey.toInt() ).getBytes(), syncValue.getBytes() );
        }
        else
        {
            newSyncByteArray = syncValue.getBytes();
        }

        return DigestUtil.generateSHA( newSyncByteArray );
    }

    public String generateUserSyncValue( byte[] syncValue )
    {
        return Base64Util.encode( syncValue );
    }

    public String generateGroupKey( UserStoreKey userStoreKey, String syncValue )
    {
        byte[] newSyncByteArray;
        if ( userStoreKey != null )
        {
            newSyncByteArray = ArrayUtil.concat( String.valueOf( userStoreKey.toInt() ).getBytes(), syncValue.getBytes() );
        }
        else
        {
            newSyncByteArray = syncValue.getBytes();
        }

        return DigestUtil.generateSHA( newSyncByteArray );
    }

    public String generateGroupSyncValue()
    {
        return UUID.generateValue();
    }


    public void updateKeyTable( Connection conn, String tableName, int lastKey )
        throws Exception
    {

        Statement stmt = conn.createStatement();

        int rowcount = stmt.executeUpdate( "UPDATE tKEY SET key_llastKey = " + lastKey + " WHERE key_stablename = '" + tableName + "'" );

        if ( rowcount == 0 )
        {
            stmt.execute( "INSERT INTO tKey (key_stablename, key_llastkey) VALUES ('" + tableName + "'," + lastKey + ")" );
        }

        stmt.close();
    }
}

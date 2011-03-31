/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.joda.time.DateTime;


public class DateTimeUserType
    extends AbstractBaseUserType
{
    public DateTimeUserType()
    {
        super( DateTime.class, Types.TIMESTAMP );
    }

    public Object nullSafeGet( ResultSet rs, String[] names, Object owner )
        throws HibernateException, SQLException
    {
        Timestamp value = rs.getTimestamp( names[0] );
        if ( rs.wasNull() )
        {
            return null;
        }

        return new DateTime( value.getTime() );
    }

    public void nullSafeSet( PreparedStatement st, Object value, int index )
        throws HibernateException, SQLException
    {
        if ( value == null )
        {
            st.setNull( index, getSqlType() );
        }
        else
        {
            java.sql.Timestamp timestamp = new Timestamp( ( (DateTime) value ).getMillis() );
            st.setTimestamp( index, timestamp );
        }
    }

    public boolean isMutable()
    {
        return false;
    }
}
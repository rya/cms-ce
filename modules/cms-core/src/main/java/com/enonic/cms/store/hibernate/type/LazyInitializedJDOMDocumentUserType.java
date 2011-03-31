/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;

import com.enonic.cms.framework.util.LazyInitializedJDOMDocument;

/**
 * Apr 27, 2010
 */
public class LazyInitializedJDOMDocumentUserType
    extends AbstractBaseUserType
{
    public LazyInitializedJDOMDocumentUserType()
    {
        super( LazyInitializedJDOMDocument.class, Types.VARBINARY );
    }

    public boolean isMutable()
    {
        return false;
    }

    @Override
    public Object deepCopy( Object value )
        throws HibernateException
    {
        if ( value == null )
        {
            return null;
        }

        LazyInitializedJDOMDocument document = (LazyInitializedJDOMDocument) value;
        return document.clone();
    }

    public Object nullSafeGet( ResultSet resultSet, String[] names, Object owner )
        throws HibernateException, SQLException
    {
        byte[] bytes = BinaryColumnReader.readBinary( names[0], resultSet );
        return convertFromBytes( bytes );
    }

    public void nullSafeSet( PreparedStatement stmt, Object value, int index )
        throws HibernateException, SQLException
    {
        byte[] internalValue = convertToBytes( (LazyInitializedJDOMDocument) value );
        if ( internalValue != null )
        {
            if ( Environment.useStreamsForBinary() )
            {
                stmt.setBinaryStream( index, new ByteArrayInputStream( internalValue ), internalValue.length );
            }
            else
            {
                stmt.setBytes( index, internalValue );
            }
        }
        else
        {
            stmt.setNull( index, getSqlType() );
        }
    }

    private byte[] convertToBytes( LazyInitializedJDOMDocument value )
    {
        if ( value == null )
        {
            return null;
        }

        try
        {
            String text = value.getDocumentAsString();
            return text != null ? text.getBytes( "UTF-8" ) : null;
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new HibernateException( e );
        }
    }

    private LazyInitializedJDOMDocument convertFromBytes( byte[] bytes )
    {
        try
        {
            if ( bytes == null )
            {
                return null;
            }
            if ( bytes.length == 0 )
            {
                return null;
            }

            return new LazyInitializedJDOMDocument( new String( bytes, "UTF-8" ) );
        }
        catch ( Exception e )
        {
            throw new HibernateException( e );
        }
    }
}

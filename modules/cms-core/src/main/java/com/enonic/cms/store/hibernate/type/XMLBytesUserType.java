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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocumentParser;


public class XMLBytesUserType
    extends AbstractBaseUserType
{
    private static final Logger LOG = LoggerFactory.getLogger( XMLBytesUserType.class.getName() );

    public XMLBytesUserType()
    {
        super( XMLBytes.class, Types.VARBINARY );
    }

    public boolean isMutable()
    {
        return false;
    }

    public Object nullSafeGet( ResultSet resultSet, String[] names, Object owner )
        throws HibernateException, SQLException
    {
        byte[] bytes = BinaryColumnReader.readBinary( names[0], resultSet );
        return convertFromBinaryFormat( bytes );
    }

    public void nullSafeSet( PreparedStatement stmt, Object value, int index )
        throws HibernateException, SQLException
    {
        byte[] internalValue = convertToBinaryFormat( (XMLBytes) value );
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

    private byte[] convertToBinaryFormat( XMLBytes value )
        throws HibernateException
    {
        try
        {
            if ( value != null )
            {
                return value.getAsString().getBytes( "UTF-8" );
            }
            else
            {
                return null;
            }
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new HibernateException( e );
        }
    }

    private XMLBytes convertFromBinaryFormat( byte[] xmlAsBytes )
        throws HibernateException
    {
        try
        {
            if ( xmlAsBytes == null )
            {
                return null;
            }
            if ( xmlAsBytes.length == 0 )
            {
                return null;
            }

            final String xmlAsString = new String( xmlAsBytes, "UTF-8" );
            final XMLBytes xmlBytes = XMLDocumentParser.getInstance().parseDocument( xmlAsString );
            //test();
            return xmlBytes;
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new HibernateException( e );
        }
    }

    private void test()
    {
        String test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<contentdata><test><mytitle>test1</mytitle><mybinaryfile><binarydata key=\"2078\"/></mybinaryfile><related key=\"193\"/><relatedmultiple><content key=\"194\"/><content key=\"193\"/></relatedmultiple><myxml><balle>rusk</balle></myxml><myxhtmlarea>\n" +
            "        <p>xhtml area</p>\n" + "        <p>ny linje</p>\n" + "        <p>her kommer <strong>fet</strong> skrift</p>\n" +
            "    </myxhtmlarea><myhtmlarea><![CDATA[<p>vanlig cdata html area</p>\n" + "<p>her kommer ny linje</p>\n" +
            "<p>her kommer <strong>fet</strong> skrift</p>]]></myhtmlarea></test></contentdata>";

        try
        {
            XMLBytes otherXMLBytes = XMLDocumentParser.getInstance().parseDocument( new String( test.getBytes(), "UTF-8" ) );
            LOG.info( "otherXMLBytes: " + otherXMLBytes.getAsString() );
        }
        catch ( UnsupportedEncodingException e )
        {
            // 
        }
    }
}

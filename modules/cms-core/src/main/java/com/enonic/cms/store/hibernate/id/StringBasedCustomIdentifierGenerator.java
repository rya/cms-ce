/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.id;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Properties;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.type.Type;
import org.hibernate.util.PropertiesHelper;
import org.hibernate.util.ReflectHelper;

import com.enonic.esl.util.UUID;


/**
 * Class for generating keys for our "user typed" identifiers. Specify table and idClassName, where idClassName is the actual domain class
 * to instantiate passing an Integer to the constructor.
 */
public class StringBasedCustomIdentifierGenerator
    implements IdentifierGenerator, Configurable
{

    private static final String ID_CLASS_NAME = "idClassName";

    private static final String LENGTH = "length";

    private Class idClass;

    private int length = 32;


    public void configure( Type type, Properties params, Dialect d )
    {
        idClass = parseClass( PropertiesHelper.getString( ID_CLASS_NAME, params, null ) );
        length = PropertiesHelper.getInt( LENGTH, params, 32 );
    }


    private Class parseClass( String className )
    {
        try
        {
            return ReflectHelper.classForName( className );
        }
        catch ( ClassNotFoundException e )
        {
            throw new MappingException( "Failed to parse class: " + className, e );
        }
    }

    public Serializable generate( SessionImplementor session, Object object )
    {
        return convertToUserType( UUID.generateValue() );
    }

    private Serializable convertToUserType( String value )
    {
        try
        {
            Constructor constructor = idClass.getConstructor( new Class[]{String.class} );
            return (Serializable) constructor.newInstance( value );
        }
        catch ( Exception e )
        {
            throw new RuntimeException(
                "Failed to instantiate (" + value + "). " + idClass + " probably do not have a constructor that takes only one Integer.",
                e );
        }
    }


}

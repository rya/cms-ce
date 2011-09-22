/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.wrapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.jackrabbit.value.BinaryValue;

class JcrNodeWrapper
    implements JcrNode
{
    private final Node node;

    JcrNodeWrapper( Node node )
    {
        this.node = node;
    }

    @Override
    public String getName()
    {
        try
        {
            return node.getName();
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public String getNodeType()
    {
        throw new UnsupportedOperationException( "Not implemented" );
    }

    @Override
    public String getPath()
    {
        try
        {
            return node.getPath();
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public JcrNode getParent()
    {
        throw new UnsupportedOperationException( "Not implemented" );
    }

    @Override
    public JcrNode getNode( String relPath )
    {
        try
        {
            return JcrWrappers.wrap( node.getNode( relPath ) );
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public boolean hasNode( String relPath )
    {
        try
        {
            return node.hasNode( relPath );
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public JcrNode addNode( String relPath, String primaryNodeTypeName )
    {
        try
        {
            return JcrWrappers.wrap( node.addNode( relPath, primaryNodeTypeName ) );
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public void remove()
    {
        try
        {
            node.remove();
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public void remove( String relPath )
    {
        try
        {
            if ( node.hasNode( relPath ) )
            {
                node.getNode( relPath ).remove();
            }
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public JcrNodeIterator getChildren()
    {
        try
        {
            return JcrWrappers.wrap( node.getNodes() );
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public boolean hasProperties()
    {
        try
        {
            return node.hasProperties();
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public Set<String> getPropertyNames()
    {
        try
        {
            Set<String> names = new HashSet<String>();
            PropertyIterator propIterator = node.getProperties();
            while ( propIterator.hasNext() )
            {
                names.add( propIterator.nextProperty().getName() );
            }
            return names;
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    public int getPropertyType( String name )
    {
        try
        {
            Property property = node.getProperty( name );
            return property.getType();
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public Object getProperty( String name )
    {
        try
        {
            Property property = node.getProperty( name );
            if ( property.isMultiple() )
            {
                Value[] values = property.getValues();
                List propertyList = new ArrayList();
                for ( Value value : values )
                {
                    propertyList.add( getValue( value ) );
                }
                return propertyList;
            }
            else
            {
                return getPropertyValue( property );
            }
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    private Object getValue( Value value )
        throws RepositoryException
    {
        switch ( value.getType() )
        {
            case PropertyType.BINARY:
                return value.getBinary().getSize();

            case PropertyType.STRING:
                return value.getString();

            case PropertyType.LONG:
                return value.getLong();

            case PropertyType.DOUBLE:
                return value.getDouble();

            case PropertyType.DATE:
                return value.getDate();

            case PropertyType.BOOLEAN:
                return value.getBoolean();

            case PropertyType.NAME:
                return value.getString();

            case PropertyType.PATH:
                return value.getString();

            case PropertyType.REFERENCE:
                throw new UnsupportedOperationException( "Not implemented" );

            case PropertyType.WEAKREFERENCE:
                throw new UnsupportedOperationException( "Not implemented" );

            case PropertyType.URI:
                throw new UnsupportedOperationException( "Not implemented" );

            default:
                throw new RepositoryException( "Invalid property type: " + value.getType() );
        }
    }

    private Object getPropertyValue( Property property )
        throws RepositoryException
    {
        switch ( property.getType() )
        {
            case PropertyType.BINARY:
                return property.getBinary().getSize();

            case PropertyType.STRING:
                return property.getString();

            case PropertyType.LONG:
                return property.getLong();

            case PropertyType.DOUBLE:
                return property.getDouble();

            case PropertyType.DATE:
                return property.getDate();

            case PropertyType.BOOLEAN:
                return property.getBoolean();

            case PropertyType.NAME:
                return property.getName();

            case PropertyType.PATH:
                return property.getPath();

            case PropertyType.REFERENCE:
                return JcrWrappers.wrap( property.getNode() );

            case PropertyType.WEAKREFERENCE:
                return JcrWrappers.wrap( property.getNode() );

            case PropertyType.URI:
                throw new UnsupportedOperationException( "Not implemented" );

            default:
                throw new RepositoryException( "Invalid property type: " + property.getType() );
        }
    }

    @Override
    public String getStringProperty( String name )
    {
        throw new UnsupportedOperationException( "Not implemented" );
    }

    @Override
    public String getBooleanProperty( String name )
    {
        throw new UnsupportedOperationException( "Not implemented" );
    }

    @Override
    public Long getLongProperty( String name )
    {
        Object value = getProperty( name );
        if ( !( value instanceof Long ) )
        {
            throw new IllegalArgumentException();
        }
        return (Long) value;
    }

    @Override
    public JcrBinary getBinaryProperty( String name )
    {
        throw new UnsupportedOperationException( "Not implemented" );
    }

    @Override
    public void setProperty( String name, Object value )
    {
        try
        {
            if ( value == null )
            {
                node.setProperty( name, (Value) null );
            }
            else if ( value instanceof String )
            {
                node.setProperty( name, (String) value );
            }
            else if ( value instanceof String[] )
            {
                node.setProperty( name, (String[]) value );
            }
            else if ( value instanceof Integer )
            {
                node.setProperty( name, (Integer) value );
            }
            else if ( value instanceof Long )
            {
                node.setProperty( name, (Long) value );
            }
            else if ( value instanceof Boolean )
            {
                node.setProperty( name, (Boolean) value );
            }
            else if ( value instanceof Date )
            {
                node.setProperty( name, toCalendar( (Date) value ) );
            }
            else if ( value instanceof byte[] )
            {
                BinaryValue binaryValue = new BinaryValue( (byte[]) value );
                node.setProperty( name, binaryValue );
            }
            else
            {
                throw new IllegalArgumentException( "Unsupported property value type [" + value.getClass().getName() + "]" );
            }
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public void setPropertyReference( String name, JcrNode referencedNode, boolean weak )
    {
        if ( ( name == null ) || ( referencedNode == null ) )
        {
            throw new NullPointerException();
        }
        try
        {
            Node refNode = JcrWrappers.unwrap( referencedNode );
            Value value = node.getSession().getValueFactory().createValue( refNode, weak );
            node.setProperty( name, value );
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public JcrNode getChild( String name )
    {
        try
        {
            return JcrWrappers.wrap( node.getNode( name ) );
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    @Override
    public boolean hasChildNodes()
    {
        try
        {
            return node.hasNodes();
        }
        catch ( RepositoryException e )
        {
            throw JcrException.wrap( e );
        }
    }

    private Calendar toCalendar( Date date )
    {
        if ( date == null )
        {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        return cal;
    }

    Node getValue()
    {
        return node;
    }
}

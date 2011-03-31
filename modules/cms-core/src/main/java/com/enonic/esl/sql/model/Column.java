/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

/*
 * Created on 06.apr.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.enonic.esl.sql.model;

import java.io.Serializable;

import com.enonic.esl.sql.model.datatypes.DataType;
import com.enonic.esl.util.StringUtil;

public class Column
    implements Serializable
{
    private String name;

    private String xpath;

    private boolean required;

    private boolean primaryKey;

    private boolean nullColumn;

    private boolean notColumn;

    private DataType type;

    private Object defaultValue;

    private Table table;

    private int size = -1;

    public Column( String name, String xpath )
    {
        this.name = name;
        this.xpath = xpath;
    }

    // A little hack.. We need a way to represent columns in WHERE clauses, WHERE col IS NULL

    private Column( String name, boolean nullColumn, boolean notColumn )
    {
        this.name = name;
        this.nullColumn = nullColumn;
        this.notColumn = notColumn;
    }

    public Column( String name, String xpath, DataType type )
    {
        this.name = name;
        this.xpath = xpath;
        this.type = type;
    }

    public Column( String name, String xpath, boolean required, boolean primaryKey, DataType type, Object defaultValue, int size )
    {
        this.name = name;
        this.xpath = xpath;
        this.required = required;
        this.primaryKey = primaryKey;
        this.type = type;
        this.defaultValue = defaultValue;
        this.size = size;
    }

    public String getName()
    {
        return name;
    }

    public String getRealName( boolean upperCaseFirstLetter )
    {

        String realName;

        // dirtyhacks!

        // for tContentHandler.class
        if ( name.endsWith( "sClass" ) )
        {
            realName = "javaClass";
        }
        // for tLanguage.description
        else if ( "lan_sDescription".equals( name ) )
        {
            realName = "description";
        }
        // for tBinary.bda_blobData
        else if ( "bda_blobData".equals( name ) )
        {
            realName = "data";
        }
        else if ( "pva_bDefault".equals( name ) )
        {
            realName = "defaultValue";
        }
        else if ( xpath != null && xpath.length() > 0 )
        {
            if ( xpath.indexOf( "/" ) > -1 )
            {
                // Check if there are more than one occurence of "/"
                StringBuffer nameTemp = new StringBuffer();
                StringBuffer xpathTemp = new StringBuffer( xpath );

                int pos = xpath.indexOf( "/" );
                while ( pos != -1 )
                {
                    nameTemp.append( xpath.substring( 0, pos ) );
                    xpathTemp.delete( 0, pos + 1 );
                    pos = xpathTemp.toString().indexOf( "/" );
                }

                if ( xpathTemp.toString().indexOf( "@" ) != -1 )
                {
                    nameTemp.append( xpathTemp.toString().substring( 1 ) );
                }
                else
                {
                    nameTemp.append( xpathTemp );
                }
                realName = nameTemp.toString();
            }
            else if ( xpath.indexOf( "@" ) > -1 )
            {
                realName = xpath.substring( xpath.indexOf( "@" ) + 1 );
            }
            else if ( xpath.equals( "." ) )
            {
                realName = "xml";
            }
            else
            {
                realName = xpath;
            }

            // Some really clever uppercase name guessing
            realName = StringUtil.upperCaseWord( realName, "key", false );
            realName = StringUtil.upperCaseWord( realName, "name", false );
            realName = StringUtil.upperCaseWord( realName, "value", false );
            realName = StringUtil.upperCaseWord( realName, "server", false );
            realName = StringUtil.upperCaseWord( realName, "address", false );
            realName = StringUtil.upperCaseWord( realName, "sheet", false );
            realName = StringUtil.upperCaseWord( realName, "date", false );
            realName = StringUtil.upperCaseWord( realName, "uid", false );
            realName = StringUtil.upperCaseWord( realName, "section", false );
            realName = StringUtil.upperCaseWord( realName, "type", false );
            realName = StringUtil.upperCaseWord( realName, "item", false );
            realName = StringUtil.upperCaseWord( realName, "method", false );
            realName = StringUtil.upperCaseWord( realName, "column", false );
            realName = StringUtil.upperCaseWord( realName, "config", false );
            realName = StringUtil.upperCaseWord( realName, "handler", false );
            realName = StringUtil.upperCaseWord( realName, "data", false );
            realName = StringUtil.upperCaseWord( realName, "code", false );
            realName = StringUtil.upperCaseWord( realName, "category", false );
            //realName = StringUtil.upperCaseWordAfter(realName, "super");
        }
        else
        {
            return "";
        }

        if ( upperCaseFirstLetter )
        {
            return realName.substring( 0, 1 ).toUpperCase().concat( realName.substring( 1 ) );
        }
        else
        {
            return realName;
        }
    }

    public DataType getType()
    {
        return type;
    }

    public String getColumnValue( Object xpathValue )
    {
        return getType().getSQLValue( xpathValue );
    }

    public Column getCountColumn()
    {
        return new Column( "count(" + name + ")", null );
    }

    public Column getCustomColumn( String functionName )
    {
        return new Column( functionName + "(" + name + ")", null );
    }

    public Column getMaxColumn()
    {
        return new Column( "max(" + name + ")", null );
    }

    public Column getNullColumn()
    {
        return new Column( name, true, false );
    }

    public Column getNotNullColumn()
    {
        return new Column( name, true, true );
    }

    public String getXPath()
    {
        return xpath;
    }

    public String toString()
    {
        return name;
    }

    public boolean isPrimaryKey()
    {
        return primaryKey;
    }

    public boolean isForeignKey()
    {
        return false;
    }

    public boolean isRequired()
    {
        return required;
    }

    public boolean isNullColumn()
    {
        return nullColumn;
    }

    public boolean isNotColumn()
    {
        return notColumn;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( !( obj instanceof Column ) )
        {
            return false;
        }

        Column other = (Column) obj;

        if ( !this.getName().equalsIgnoreCase( other.getName() ) )
        {
            return false;
        }

        if ( this.getType() != other.getType() )
        {
            return false;
        }

        if ( this.isPrimaryKey() != other.isPrimaryKey() )
        {
            return false;
        }

        if ( this.isForeignKey() != other.isForeignKey() )
        {
            return false;
        }

        if ( this.isRequired() != other.isRequired() )
        {
            return false;
        }

        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        int hashCode = name.toLowerCase().hashCode();
        hashCode ^= type.hashCode();
        if ( primaryKey )
        {
            hashCode ^= 0x80000000;
        }
        if ( isForeignKey() )
        {
            hashCode ^= 0x40000000;
        }
        if ( required )
        {
            hashCode ^= 0x20000000;
        }
        return hashCode;
    }

    public Object getDefaultValue()
    {
        if ( defaultValue != null )
        {
            return defaultValue;
        }
        else
        {
            String lcName = getName().toLowerCase();
            if ( lcName.endsWith( "bdeleted" ) || lcName.endsWith( "bisdeleted" ) )
            {
                return Boolean.FALSE;
            }
            else
            {
                return null;
            }
        }
    }

    public Table getTable()
    {
        return table;
    }

    public void setTable( Table table )
    {
        this.table = table;
    }

    public int getSize()
    {
        return size;
    }
}

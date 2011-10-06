/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype.dataentryconfig;

import com.enonic.cms.core.content.contentdata.custom.DataEntryType;


public enum DataEntryConfigType
{

    BINARY( "uploadfile", DataEntryType.BINARY ),
    CHECKBOX( "checkbox", DataEntryType.BOOLEAN ),
    DATE( "date", DataEntryType.DATE ),
    DROPDOWN( "dropdown", DataEntryType.SELECTOR ),
    FILE( "file", DataEntryType.FILE ),
    FILES( "files", DataEntryType.FILES ),
    HTMLAREA( "htmlarea", DataEntryType.HTML_AREA ),
    IMAGE( "image", DataEntryType.IMAGE ),
    IMAGES( "images", DataEntryType.IMAGES ),
    KEYWORDS( "keywords", new DataEntryType[]{DataEntryType.KEYWORDS} ),
    MULTIPLE_CHOICE( "multiplechoice", DataEntryType.MULTIPLE_CHOICE ),
    RADIOBUTTON( "radiobutton", DataEntryType.SELECTOR ),
    RELATEDCONTENT( "relatedcontent", new DataEntryType[]{DataEntryType.RELATED_CONTENT, DataEntryType.RELATED_CONTENTS} ),
    TEXT( "text", new DataEntryType[]{DataEntryType.TEXT, DataEntryType.KEYWORDS} ),
    TEXT_AREA( "textarea", DataEntryType.TEXT_AREA ),
    URL( "url", DataEntryType.URL ),
    XML( "xml", DataEntryType.XML );

    private String name;

    /**
     * Compatible data entry types
     */
    private DataEntryType[] compatibleDataEntryTypes = new DataEntryType[]{};

    public static DataEntryConfigType parse( String value )
    {

        DataEntryConfigType[] types = DataEntryConfigType.values();
        for ( DataEntryConfigType type : types )
        {
            if ( type.getName().equals( value ) )
            {
                return type;
            }
        }
        return null;
    }

    DataEntryConfigType( String name )
    {
        this.name = name;
    }

    DataEntryConfigType( String name, DataEntryType dataEntryType )
    {
        this.name = name;
        this.compatibleDataEntryTypes = new DataEntryType[]{dataEntryType};
    }

    DataEntryConfigType( String name, DataEntryType[] compatibleDataEntryTypes )
    {
        this.name = name;
        this.compatibleDataEntryTypes = compatibleDataEntryTypes;
    }

    public boolean isCompatible( DataEntryType type )
    {

        if ( compatibleDataEntryTypes == null || compatibleDataEntryTypes.length == 0 )
        {
            return true;
        }

        for ( DataEntryType currType : compatibleDataEntryTypes )
        {
            if ( currType.equals( type ) )
            {
                return true;
            }
        }

        return false;
    }

    public String getName()
    {
        return name;
    }

    public DataEntryType[] getCompatibleDataEntryTypes()
    {
        if ( compatibleDataEntryTypes == null || compatibleDataEntryTypes.length == 0 )
        {
            return null;
        }

        return compatibleDataEntryTypes;
    }

    public String getCompatibleDataEntryTypesAsCommaSeparatedString()
    {
        DataEntryType[] types = getCompatibleDataEntryTypes();
        if ( types == null )
        {
            return "";
        }

        StringBuffer str = new StringBuffer();
        for ( int i = 0; i < types.length; i++ )
        {
            DataEntryType type = types[i];
            str.append( type );
            if ( i < types.length - 1 )
            {
                str.append( "," );
            }
        }
        return str.toString();
    }

    public String toString()
    {
        return getName();
    }


}

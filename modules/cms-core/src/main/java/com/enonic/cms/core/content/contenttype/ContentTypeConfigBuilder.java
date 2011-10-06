/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import java.util.LinkedHashMap;
import java.util.Map;


public class ContentTypeConfigBuilder
{
    private StringBuffer allBlocks = new StringBuffer();

    private String name;

    private StringBuffer currentBlock = new StringBuffer();

    private String titleInputName;

    private String currentBlockName;

    private String currentGroupXPath;

    private Map<String, String> indexParams = new LinkedHashMap<String, String>();

    public ContentTypeConfigBuilder( String name, String titleInputName )
    {
        this.name = name;
        this.titleInputName = titleInputName;
    }

    public void startBlock( String blockName )
    {
        this.currentBlockName = blockName;
        this.currentGroupXPath = null;
        currentBlock = new StringBuffer();
    }

    public void startBlock( String blockName, String groupXPath )
    {
        this.currentBlockName = blockName;
        this.currentGroupXPath = groupXPath;
        currentBlock = new StringBuffer();
    }

    public void endBlock()
    {
        String blockStart = "<block name=\"" + currentBlockName + "\"";
        if ( currentGroupXPath != null )
        {
            blockStart = blockStart + " group=\"" + currentGroupXPath + "\">\n";
        }
        else
        {
            blockStart = blockStart + ">\n";
        }
        currentBlock.insert( 0, blockStart );
        currentBlock.append( "\n</block>" );
        allBlocks.append( currentBlock.toString() );
    }

    public void addInput( String name, String type, String xpath, String display )
    {
        currentBlock.append( "\n" );
        currentBlock.append( "<input name=\"" ).append( name ).append( "\"" ).append( "\n" );
        currentBlock.append( " type=\"" ).append( type ).append( "\">" ).append( "\n" );
        currentBlock.append( "\t<display>" ).append( display ).append( "</display>\n" );
        currentBlock.append( "\t<xpath>" ).append( xpath ).append( "</xpath>\n" );
        currentBlock.append( "</input>" );
    }

    public void addInput( String name, String type, String xpath, String display, boolean required )
    {
        currentBlock.append( "\n" );
        currentBlock.append( "<input name=\"" ).append( name ).append( "\"" ).append( "\n" );
        currentBlock.append( " type=\"" ).append( type ).append( "\"" );
        currentBlock.append( " required=\"" ).append( Boolean.toString( required ) ).append( "\">" ).append( "\n" );
        currentBlock.append( "\t<display>" ).append( display ).append( "</display>\n" );
        currentBlock.append( "\t<xpath>" ).append( xpath ).append( "</xpath>\n" );
        currentBlock.append( "</input>" );
    }

    public void addRelatedContentInput( String name, String xpath, String display, boolean required, boolean multiple,
                                        String... restrictedToContentTypes )
    {
        currentBlock.append( "\n" );
        currentBlock.append( "<input name=\"" ).append( name ).append( "\"" ).append( "\n" );
        currentBlock.append( " type=\"relatedcontent\"" );
        currentBlock.append( " required=\"" ).append( Boolean.toString( required ) ).append( "\"" );
        currentBlock.append( " multiple=\"" ).append( Boolean.toString( multiple ) ).append( "\"" );
        currentBlock.append( ">" ).append( "\n" );
        currentBlock.append( "\t<display>" ).append( display ).append( "</display>\n" );
        currentBlock.append( "\t<xpath>" ).append( xpath ).append( "</xpath>\n" );
        if ( restrictedToContentTypes.length > 0 )
        {
            for ( String contentTypeName : restrictedToContentTypes )
            {
                currentBlock.append( "<contenttype name=\"" ).append( contentTypeName ).append( "\"/>" );
            }
        }
        currentBlock.append( "</input>" );
    }

    /**
     * Adds a dropdown input configuration to the content type XML.
     *
     * @param name     The variable name of the input.
     * @param xpath    The placement of the input in the contenttype XML.
     * @param display  The human readable title of the input.
     * @param required <code>true</code> if this input should be a required field.
     * @param options  A string of key value pairs, where the key should come first.  The method will throw an
     *                 <code>IllegalArgumentException</code> if this parameter does not conatin an even number of entries.
     */
    public void addDropDownInput( String name, String xpath, String display, boolean required, String... options )
    {
        if ( options.length < 2 )
        {
            throw new IllegalArgumentException( "The options list must contain at least one key value pair" );
        }
        if ( options.length % 2 != 0 )
        {
            throw new IllegalArgumentException( "The options list must be divisible by 2" );
        }
        currentBlock.append( "\n" );
        currentBlock.append( "<input name=\"" ).append( name ).append( "\" type=\"dropdown\"" );
        if ( required )
        {
            currentBlock.append( " required=\"true\"" );
        }
        currentBlock.append( ">" ).append( "\n" );
        currentBlock.append( "  <display>" ).append( display ).append( "</display>\n" );
        currentBlock.append( "  <xpath>" ).append( xpath ).append( "</xpath>\n" );
        currentBlock.append( "  <options>\n" );
        for ( int i = 0; i < options.length; i += 2 )
        {
            currentBlock.append( "    <option value=\"" ).append( options[i] ).append( "\">" );
            currentBlock.append( options[i + 1] ).append( "</option>" );
        }
        currentBlock.append( "  </options>\n" );
        currentBlock.append( "</input>" );
    }

    public void addIndexParameter( String name )
    {
        indexParams.put( name, "contentdata/" + name );
    }

    public void addIndexParameter( String name, String xpath )
    {
        indexParams.put( name, xpath );
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "<moduledata>" );

        String configStart = "";
        configStart += "<config name=\"" + name + "\"";
        configStart += " version=\"1.0\">\n";
        configStart += "<form>\n";
        configStart += "<title name=\"" + titleInputName + "\"/>\n";
        s.append( configStart );

        s.append( allBlocks );

        s.append( "\n</form>\n</config>\n" );

        s.append( "<indexparameters>\n" );
        for ( Map.Entry<String, String> indexParamEntry : indexParams.entrySet() )
        {
            s.append( "<index name=\"" + indexParamEntry.getKey() + "\" xpath=\"" + indexParamEntry.getValue() + "\"/>\n" );
        }
        s.append( "</indexparameters>\n" );
        s.append( "</moduledata>" );
        return s.toString();
    }
}

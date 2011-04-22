/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

/**
 * Nov 17, 2009
 */
public class ContentTypeConfigBuilder
{
    private StringBuffer allBlocks = new StringBuffer();

    private String name;

    private StringBuffer currentBlock = new StringBuffer();

    private String titleInputName;

    private String currentBlockName;

    private String currentGroupXPath;

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

    public void addRelatedContentInput( String name, String type, String xpath, String display, boolean required, boolean multiple,
                                        String... restrictedToContentTypes )
    {
        currentBlock.append( "\n" );
        currentBlock.append( "<input name=\"" ).append( name ).append( "\"" ).append( "\n" );
        currentBlock.append( " type=\"" ).append( type ).append( "\"" );
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

    public String toString()
    {
        StringBuffer s = new StringBuffer();

        String configStart = "<config name=\"" + name + "\"";
        configStart += " version=\"1.0\">\n";
        configStart += "<form>\n";
        configStart += "<title name=\"" + titleInputName + "\"/>\n";
        s.append( configStart );

        s.append( allBlocks );

        s.append( "\n</form>\n</config>" );

        return s.toString();
    }
}

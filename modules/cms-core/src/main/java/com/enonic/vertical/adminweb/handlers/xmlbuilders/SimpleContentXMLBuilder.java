/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.xmlbuilders;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.FileItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.util.DateUtil;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.AdminHandlerBaseServlet;
import com.enonic.vertical.adminweb.VerticalAdminException;
import com.enonic.vertical.adminweb.VerticalAdminLogger;
import com.enonic.vertical.adminweb.handlers.SimpleContentHandlerServlet;

import com.enonic.cms.core.content.binary.BinaryData;

import com.enonic.cms.core.security.user.User;

public class SimpleContentXMLBuilder
    extends ContentBaseXMLBuilder
{

    public int[] getRelatedContentKeys( ExtendedMap formItems )
    {

        int contentTypeKey = formItems.getInt( "contenttypekey" );
        Document moduledataDoc = XMLTool.domparse( admin.getContentTypeModuleData( contentTypeKey ) );
        Element elem = moduledataDoc.getDocumentElement();
        elem = XMLTool.getElement( elem, "config" );
        elem = XMLTool.getElement( elem, "form" );
        Element[] blocks = XMLTool.getElements( elem, "block" );

        ArrayList<String> relatedContentFields = new ArrayList<String>();
        for ( Element block : blocks )
        {
            Element[] inputs = XMLTool.getElements( block, "input" );
            for ( Element element : inputs )
            {
                String type = element.getAttribute( "type" );
                String name = element.getAttribute( "name" );
                if ( "image".equals( type ) || "images".equals( type ) || "file".equals( type ) || "files".equals( type ) ||
                    "relatedcontent".equals( type ) )
                {
                    relatedContentFields.add( name );
                }
            }
        }
        return AdminHandlerBaseServlet.getIntArrayFormItems( formItems,
                                                             relatedContentFields.toArray( new String[relatedContentFields.size()] ) );
    }

    public void buildContentTypeXML( User user, Document doc, Element contentdata, ExtendedMap formItems )
        throws VerticalAdminException
    {

        int page = formItems.getInt( "page" );

        try
        {
            Document moduleDoc = XMLTool.domparse( admin.getContentTypeModuleData( page - 999 ) );

            Element rootElement = moduleDoc.getDocumentElement();
            Element moduleElement = XMLTool.getElement( rootElement, "config" );

            // Date conversion objects
            NodeList blockElements;
            blockElements = XMLTool.selectNodes( moduleElement, "//config/form/block" );

            for ( int k = 0; k < blockElements.getLength(); ++k )
            {
                Element blockElement = (Element) blockElements.item( k );
                NodeList inputElements = XMLTool.selectNodes( blockElement, "input" );

                boolean groupBlock = false;
                String groupXPath = blockElement.getAttribute( "group" );
                if ( groupXPath != null && groupXPath.length() > 0 )
                {
                    groupBlock = true;
                }

                if ( !groupBlock )
                {
                    createNormalBlock( formItems, doc, contentdata, inputElements );
                }
                else
                {
                    createGroupBlock( formItems, doc, contentdata, inputElements, groupXPath, k + 1 );
                }
            }

        }
        catch ( ParseException e )
        {
            VerticalAdminLogger.errorAdmin( SimpleContentHandlerServlet.class, 3, "Failed to parse a date: %t", e );
        }


    }

    private void createNormalBlock( ExtendedMap formItems, Document doc, Element contentdata, NodeList inputElements )
        throws ParseException
    {

        ArrayList<Integer> keepBinaries;
        if ( formItems.containsKey( "__keepbinaries" ) )
        {
            keepBinaries = (ArrayList<Integer>) formItems.get( "__keepbinaries" );
        }
        else
        {
            keepBinaries = new ArrayList<Integer>();
        }

        for ( int i = 0; i < inputElements.getLength(); ++i )
        {
            Element inputElem = (Element) inputElements.item( i );
            String name = inputElem.getAttribute( "name" );

            String xpath = XMLTool.getElementText( inputElem, "xpath" );
            String type = inputElem.getAttribute( "type" );

            if ( xpath != null )
            {
                // First, create the elements in the xpath:

                Element tmpElem = createXPathElements( contentdata, xpath, 1 );

                if ( tmpElem == null )
                {
                    VerticalAdminLogger.errorAdmin( SimpleContentXMLBuilder.class, 3, "Incorrect xpath specification : " + xpath, null );
                }

                // Then store the data.
                // Some types may need to be treated separatly.

                // date
                if ( type.equals( "date" ) )
                {
                    String date = formItems.getString( "date" + name, null );
                    if ( date != null )
                    {
                        Date tempDate = DateUtil.parseDate( date );
                        XMLTool.createTextNode( doc, tmpElem, DateUtil.formatISODate( tempDate ) );
                    }
                }

                // images
                else if ( type.equals( "images" ) )
                {
                    if ( AdminHandlerBaseServlet.isArrayFormItem( formItems, name ) )
                    {
                        String[] images = (String[]) formItems.get( name );
                        String[] text = (String[]) formItems.get( name + "text" );
                        for ( int k = 1; k < images.length; k++ )
                        {
                            if ( images[k] == null || images[k].length() == 0 )
                            {
                                continue;
                            }
                            Element image = XMLTool.createElement( doc, tmpElem, "image" );
                            image.setAttribute( "key", images[k] );
                            XMLTool.createElement( doc, image, "text", text[k] );
                        }
                    }
                }

                // image
                else if ( type.equals( "image" ) && formItems.containsKey( name ) )
                {
                    String image = formItems.getString( name );
                    tmpElem.setAttribute( "key", image );
                    if ( "true".equals( inputElem.getAttribute( "imagetext" ) ) )
                    {
                        String text = formItems.getString( name + "text" );
                        XMLTool.createElement( doc, tmpElem, "text", text );
                    }
                }

                // related content
                else if ( type.equals( "relatedcontent" ) )
                {
                    if ( AdminHandlerBaseServlet.isArrayFormItem( formItems, name ) )
                    {
                        String[] content = (String[]) formItems.get( name );
                        for ( String aContent : content )
                        {
                            if ( aContent == null || aContent.length() == 0 )
                            {
                                continue;
                            }
                            Element contentElem = XMLTool.createElement( doc, tmpElem, "content" );
                            contentElem.setAttribute( "key", aContent );
                        }
                    }
                    else if ( formItems.containsKey( name ) )
                    {
                        if ( !"false".equals( inputElem.getAttribute( "multiple" ) ) )
                        {
                            String content = formItems.getString( name );
                            Element contentElem = XMLTool.createElement( doc, tmpElem, "content" );
                            contentElem.setAttribute( "key", content );
                        }
                        else
                        {
                            String content = formItems.getString( name );
                            tmpElem.setAttribute( "key", content );
                        }
                    }

                }

                // files
                else if ( type.equals( "files" ) )
                {
                    if ( AdminHandlerBaseServlet.isArrayFormItem( formItems, name ) )
                    {
                        //logCategory.debug("multiple files");
                        String[] files = (String[]) formItems.get( name );
                        for ( String file1 : files )
                        {
                            if ( file1 != null && file1.length() > 0 )
                            {
                                Element file = XMLTool.createElement( doc, tmpElem, "file" );
                                file.setAttribute( "key", file1 );
                            }
                        }
                    }
                    else
                    {
                        //logCategory.debug("single file");
                        String filekey = formItems.getString( name, null );
                        if ( filekey != null && filekey.length() > 0 )
                        {
                            Element file = XMLTool.createElement( doc, tmpElem, "file" );
                            file.setAttribute( "key", filekey );
                        }
                    }
                }

                // file
                else if ( type.equals( "file" ) )
                {
                    //logCategory.debug("single file");
                    String filekey = formItems.getString( name, null );
                    if ( filekey != null && filekey.length() > 0 )
                    {
                        Element file = XMLTool.createElement( doc, tmpElem, "file" );
                        file.setAttribute( "key", filekey );
                    }
                }

                // uploaded binary file
                else if ( type.equals( "uploadfile" ) )
                {
                    String fileName = formItems.getString( "filename_" + name, "" );
                    int oldKey = -1;

                    // If file was already present (has binary key)
                    if ( formItems.containsKey( name ) )
                    {
                        oldKey = formItems.getInt( name );
                    }

                    boolean isReplaced = false;

                    BinaryData[] binaries = (BinaryData[]) formItems.get( "__binaries", null );
                    if ( binaries != null && binaries.length > 0 )
                    {
                        int index = -1;
                        for ( int counter = 0; counter < binaries.length; counter++ )
                        {
                            if ( fileName.equals( binaries[counter].fileName ) )
                            {
                                index = counter;
                                break;
                            }
                        }
                        if ( index != -1 )
                        {
                            Element binaryElement = XMLTool.createElement( doc, tmpElem, "binarydata" );
                            binaryElement.setAttribute( "key", "%" + index );

                            if ( oldKey != -1 )
                            {
                                isReplaced = true;
                            }
                        }
                    }

                    if ( oldKey != -1 && !isReplaced )
                    {
                        Element binaryElement = XMLTool.createElement( doc, tmpElem, "binarydata" );
                        binaryElement.setAttribute( "key", String.valueOf( oldKey ) );
                        keepBinaries.add( oldKey );
                        formItems.put( "__keepbinaries", keepBinaries );
                    }
                }

                // checkbox
                else if ( type.equals( "checkbox" ) )
                {
                    if ( "true".equals( formItems.getString( name, null ) ) )
                    {
                        XMLTool.createTextNode( doc, tmpElem, "true" );
                    }
                    else
                    {
                        XMLTool.createTextNode( doc, tmpElem, "false" );
                    }
                }

                // multiple choice
                else if ( type.equals( "multiplechoice" ) )
                {
                    // <text>
                    if ( formItems.containsKey( name ) )
                    {
                        XMLTool.createElement( doc, tmpElem, "text", formItems.getString( name ) );

                        // <alternative>'s
                        String[] alternatives = formItems.getStringArray( name + "_alternative" );
                        String[] corrects = formItems.getStringArray( name + "_checkbox_values" );
                        for ( int j = 0; j < alternatives.length; j++ )
                        {
                            Element alternativeElem = XMLTool.createElement( doc, tmpElem, "alternative", alternatives[j] );
                            boolean correct = ( "true".equals( corrects[j] ) );
                            alternativeElem.setAttribute( "correct", String.valueOf( correct ) );
                        }
                    }
                }

                else if ( type.equals( "xml" ) )
                {
                    // xml
                    if ( formItems.containsKey( name ) )
                    {
                        String value = formItems.getString( name, null );
                        if ( value != null )
                        {
                            Document xmlDoc = XMLTool.domparse( value );
                            tmpElem.appendChild( doc.importNode( xmlDoc.getDocumentElement(), true ) );
                        }
                    }
                }

                //JIRA VS-2461 Hack to make radiobuttons work in groups
                // radiobutton
                else if ( type.equals( "radiobutton" ) )
                {
                    String[] radiobuttonName = getFormItemsByRegexp( "rb:[0-9].*:" + name, formItems );
                    if ( radiobuttonName.length == 1 )
                    {
                        if ( formItems.getString( radiobuttonName[0] ) != null &&
                            !formItems.getString( radiobuttonName[0] ).equalsIgnoreCase( radiobuttonName[0] ) )
                        {
                            XMLTool.createTextNode( doc, tmpElem, formItems.getString( radiobuttonName[0] ) );
                        }

                    }
                    else
                    {
                        throw new IllegalArgumentException( "Ambigous input for radiobutton " + name );
                    }

                }

                // normal text
                else
                {
                    if ( formItems.containsKey( name ) )
                    {
                        String value = formItems.getString( name, null );
                        if ( type.equals( "htmlarea" ) || type.equals( "simplehtmlarea" ) )
                        {
                            XMLTool.createXHTMLNodes( doc, tmpElem, value, true );
                        }
                        else
                        {
                            XMLTool.createTextNode( doc, tmpElem, value );
                        }
                    }
                }
            }
        }
    }

    private void createGroupBlock( ExtendedMap formItems, Document doc, Element contentdata, NodeList inputElements, String groupXPath,
                                   int groupCounter )
        throws ParseException
    {
        // get number of block instances
        int instances = 1;
        if ( AdminHandlerBaseServlet.isArrayFormItem( formItems, "group" + groupCounter + "_counter" ) )
        {
            instances = ( (String[]) formItems.get( "group" + groupCounter + "_counter" ) ).length;
        }

        ArrayList<Integer> keepBinaries;
        if ( formItems.containsKey( "__keepbinaries" ) )
        {
            keepBinaries = (ArrayList<Integer>) formItems.get( "__keepbinaries" );
        }
        else
        {
            keepBinaries = new ArrayList<Integer>();
        }

        Element[] blocks = new Element[instances];

        for ( int k = 0; k < instances; ++k )
        {
            // First, create the elements in the xpath:
            blocks[k] = createXPathElements( contentdata, groupXPath, 1 );
        }

        for ( int i = 0; i < inputElements.getLength(); ++i )
        {
            Element inputElem = (Element) inputElements.item( i );
            String name = inputElem.getAttribute( "name" );
            String xpath = XMLTool.getElementText( inputElem, "xpath" );
            String type = inputElem.getAttribute( "type" );

            if ( xpath != null )
            {

                // Then store the data.
                // Some types may need to be treated separatly.

                // date
                if ( type.equals( "date" ) )
                {
                    if ( instances > 1 )
                    {
                        String[] values = (String[]) formItems.get( "date" + name );

                        for ( int j = 0; j < instances; ++j )
                        {
                            if ( values[j] != null && values[j].length() > 0 )
                            {
                                Date tempDate = DateUtil.parseDate( values[j] );
                                XMLTool.createTextNode( doc, createXPathElements( blocks[j], xpath, 0 ),
                                                        DateUtil.formatISODate( tempDate ) );
                            }
                        }
                    }
                    else
                    {
                        if ( formItems.containsKey( "date" + name ) )
                        {
                            Date tempDate = DateUtil.parseDate( formItems.getString( "date" + name ) );
                            XMLTool.createTextNode( doc, createXPathElements( blocks[0], xpath, 0 ), DateUtil.formatISODate( tempDate ) );
                        }
                    }

                }

                // file
                else if ( type.equals( "file" ) )
                {
                    if ( instances > 1 )
                    {
                        String[] values = (String[]) formItems.get( name );

                        for ( int j = 0; j < instances; ++j )
                        {
                            if ( values[j] != null && values[j].length() > 0 )
                            {
                                Element file = XMLTool.createElement( doc, createXPathElements( blocks[j], xpath, 0 ), "file" );
                                file.setAttribute( "key", values[j] );
                            }
                        }
                    }
                    else
                    {
                        String filekey = formItems.getString( name, null );
                        Element fileEl = XMLTool.createElement( doc, createXPathElements( blocks[0], xpath, 0 ), "file" );

                        if ( filekey != null && filekey.length() > 0 )
                        {
                            fileEl.setAttribute( "key", filekey );
                        }
                    }
                }

                // image
                else if ( type.equals( "image" ) )
                {
                    if ( instances > 1 )
                    {
                        String[] values = (String[]) formItems.get( name, null );
                        //String[] textValues = (String[]) formItems.get(name + "text");

                        if ( values == null )
                        {
                            continue;
                        }

                        for ( int j = 0; j < instances; ++j )
                        {
                            Element tmpElem = createXPathElements( blocks[j], xpath, 0 );

                            if ( values[j] != null && values[j].length() > 0 )
                            {
                                tmpElem.setAttribute( "key", values[j] );
                                /*if ("true".equals(inputElem.getAttribute("imagetext")))
                                            XMLTool.createElement(doc, tmpElem, "text", textValues[j]);*/
                            }
                        }
                    }
                    else
                    {
                        String value = formItems.getString( name, null );
                        //String text = formItems.getString(name + "text", null);
                        Element tmpElem = createXPathElements( blocks[0], xpath, 0 );

                        if ( value != null && value.length() > 0 )
                        {
                            tmpElem.setAttribute( "key", value );
                            /*if ("true".equals(inputElem.getAttribute("imagetext")))
                                       XMLTool.createElement(doc, tmpElem, "text", text);*/
                        }
                    }

                }

                // checkbox
                else if ( type.equals( "checkbox" ) )
                {
                    if ( instances > 1 )
                    {
                        String[] values = (String[]) formItems.get( name );

                        for ( int j = 0; j < instances; ++j )
                        {
                            Element tmpElem = createXPathElements( blocks[j], xpath, 0 );

                            if ( "true".equals( values[j] ) )
                            {
                                XMLTool.createTextNode( doc, tmpElem, "true" );
                            }
                            else
                            {
                                XMLTool.createTextNode( doc, tmpElem, "false" );
                            }
                        }
                    }
                    else
                    {
                        Element tmpElem = createXPathElements( blocks[0], xpath, 0 );

                        if ( "true".equals( formItems.getString( name, "false" ) ) )
                        {
                            XMLTool.createTextNode( doc, tmpElem, "true" );
                        }
                        else
                        {
                            XMLTool.createTextNode( doc, tmpElem, "false" );
                        }
                    }
                }

                // uploaded binary file
                else if ( type.equals( "uploadfile" ) )
                {
                    BinaryData[] binaries = (BinaryData[]) formItems.get( "__binaries", null );
                    String[] fileNames = formItems.getStringArray( "filename_" + name );
                    String[] values = formItems.getStringArray( name );

                    for ( int instance = 0; instance < instances; instance++ )
                    {
                        int oldKey = -1;
                        if ( values != null && values.length > 0 && values[instance] != null && values[instance].length() > 0 )
                        {
                            oldKey = Integer.parseInt( values[instance] );
                        }

                        boolean isReplaced = false;

                        if ( instance < fileNames.length )
                        {
                            if ( binaries != null && binaries.length > 0 )
                            {
                                int index = -1;

                                for ( int counter = 0; counter < binaries.length; counter++ )
                                {
                                    if ( fileNames[instance].equals( binaries[counter].fileName ) )
                                    {
                                        index = counter;
                                        break;
                                    }
                                }
                                if ( index != -1 )
                                {
                                    Element tmpElem = createXPathElements( blocks[instance], xpath, 0 );
                                    Element binaryElement = XMLTool.createElement( doc, tmpElem, "binarydata" );
                                    binaryElement.setAttribute( "key", "%" + index );

                                    if ( oldKey != -1 )
                                    {
                                        isReplaced = true;
                                    }
                                }
                            }
                        }
                        if ( oldKey != -1 && !isReplaced )
                        {
                            Element tmpElem = createXPathElements( blocks[instance], xpath, 0 );
                            Element binaryElement = XMLTool.createElement( doc, tmpElem, "binarydata" );
                            binaryElement.setAttribute( "key", String.valueOf( oldKey ) );

                            keepBinaries.add( oldKey );
                            formItems.put( "__keepbinaries", keepBinaries );
                        }
                    }
                    formItems.put( "__keepbinaries", keepBinaries );
                }

                // relatedcontent
                else if ( type.equals( "relatedcontent" ) )
                {
                    if ( !"false".equals( inputElem.getAttribute( "multiple" ) ) )
                    {
                        String[] keys = formItems.getStringArray( name );
                        String[] counters = formItems.getStringArray( name + "_counter" );

                        if ( counters != null && counters.length > 0 )
                        {
                            int index = 0;
                            for ( int j = 0; j < instances; j++ )
                            {
                                int keyCount = Integer.parseInt( counters[j] );
                                Element tmpElem = createXPathElements( blocks[j], xpath, 0 );
                                for ( int k = index; k < index + keyCount; k++ )
                                {
                                    Element contentElem = XMLTool.createElement( doc, tmpElem, "content" );
                                    contentElem.setAttribute( "key", String.valueOf( keys[k] ) );
                                }
                                index = index + keyCount;
                            }
                        }
                    }
                    else if ( formItems.containsKey( name ) )
                    {
                        String[] keys = formItems.getStringArray( name );
                        for ( int j = 0; j < instances; j++ )
                        {
                            if ( !"".equals( keys[j] ) )
                            {
                                Element tmpElem = createXPathElements( blocks[j], xpath, 0 );
                                tmpElem.setAttribute( "key", String.valueOf( keys[j] ) );
                            }
                        }
                    }
                }
                else if ( type.equals( "xml" ) )
                {
                    if ( instances > 1 && formItems.containsKey( name ) )
                    {
                        String[] values = formItems.getStringArray( name );

                        for ( int j = 0; j < instances && j < values.length; ++j )
                        {
                            if ( values[j] != null && values[j].length() > 0 )
                            {
                                Element tmpElem = createXPathElements( blocks[j], xpath, 0 );
                                Document xmlDoc = XMLTool.domparse( values[j] );
                                tmpElem.appendChild( doc.importNode( xmlDoc.getDocumentElement(), true ) );
                            }
                        }
                    }
                    else
                    {
                        Element tmpElem = createXPathElements( blocks[0], xpath, 0 );

                        if ( formItems.containsKey( name ) )
                        {
                            String value = formItems.getString( name );
                            Document xmlDoc = XMLTool.domparse( value );
                            tmpElem.appendChild( doc.importNode( xmlDoc.getDocumentElement(), true ) );
                        }
                    }
                }
                //JIRA VS-2461 Hack to make radiobuttons work in groups
                // radiobutton
                else if ( type.equals( "radiobutton" ) )
                {
                    String[] radiobuttonNames = getFormItemsByRegexp( "rb:[0-9].*:" + name, formItems );

                    for ( int j = 0; j < instances; ++j )
                    {
                        if ( ( radiobuttonNames.length ) > j && radiobuttonNames[j] != null )
                        {
                            if ( !formItems.getString( radiobuttonNames[j] ).equals( radiobuttonNames[j] ) )
                            {
                                Element tmpElem = createXPathElements( blocks[j], xpath, 0 );
                                XMLTool.createTextNode( doc, tmpElem, formItems.getString( radiobuttonNames[j] ) );
                            }
                        }
                    }
                }

                // normal text
                else
                {
                    if ( instances > 1 && formItems.containsKey( name ) )
                    {
                        String[] values = formItems.getStringArray( name );

                        for ( int j = 0; j < instances && j < values.length; ++j )
                        {
                            if ( values[j] != null && values[j].length() > 0 )
                            {
                                Element tmpElem = createXPathElements( blocks[j], xpath, 0 );

                                if ( type.equals( "htmlarea" ) || type.equals( "simplehtmlarea" ) )
                                {
                                    XMLTool.createXHTMLNodes( doc, tmpElem, values[j], true );
                                }
                                else
                                {
                                    XMLTool.createTextNode( doc, tmpElem, values[j] );
                                }
                            }
                        }
                    }
                    else
                    {
                        Element tmpElem = createXPathElements( blocks[0], xpath, 0 );

                        if ( formItems.containsKey( name ) )
                        {
                            String value = formItems.getString( name );

                            if ( type.equals( "htmlarea" ) || type.equals( "simplehtmlarea" ) )
                            {
                                XMLTool.createXHTMLNodes( doc, tmpElem, value, true );
                            }
                            else
                            {
                                XMLTool.createTextNode( doc, tmpElem, value );
                            }
                        }
                    }
                }
            }
        }
    }

    private String[] getFormItemsByRegexp( String regex, ExtendedMap formItems )
    {

        int count = 0;
        for ( Object key : formItems.keySet() )
        {
            String keyStr = (String) key;
            if ( Pattern.matches( regex, keyStr ) )
            {
                count++;
            }

        }

        String[] entries = new String[count];

        for ( Object key : formItems.keySet() )
        {
            String keyStr = (String) key;
            if ( Pattern.matches( regex, keyStr ) )
            {
                int index = formItems.getInt( keyStr + ":index" );
                entries[index] = keyStr;
            }

        }

        return entries;

    }

    private Element createXPathElements( Element parentElement, String xpath, int startIdx )
    {
        Document doc = parentElement.getOwnerDocument();

        // First, create the elements in the xpath:
        String[] xpathSplit = StringUtil.splitString( xpath, '/' );
        Element tmpElem = null;

        for ( int j = startIdx; j < xpathSplit.length; ++j )
        {
            if ( tmpElem == null )
            {
                if ( j != ( xpathSplit.length - 1 ) && XMLTool.getElement( parentElement, xpathSplit[j] ) != null )
                {
                    tmpElem = XMLTool.getElement( parentElement, xpathSplit[j] );
                }
                else
                {
                    tmpElem = XMLTool.createElement( doc, parentElement, xpathSplit[j] );
                }
            }
            else
            {
                if ( j != ( xpathSplit.length - 1 ) && XMLTool.getElement( tmpElem, xpathSplit[j] ) != null )
                {
                    tmpElem = XMLTool.getElement( tmpElem, xpathSplit[j] );
                }
                else
                {
                    tmpElem = XMLTool.createElement( doc, tmpElem, xpathSplit[j] );
                }
            }
        }

        return tmpElem;
    }

    public String getContentTitle( Element contentDataElem, int contentTypeKey )
    {
        Document ctDoc = XMLTool.domparse( admin.getContentType( contentTypeKey ) );
        Element ctElem = XMLTool.getElement( ctDoc.getDocumentElement(), "contenttype" );
        Element moduleDataElem = XMLTool.getElement( ctElem, "moduledata" );
        Element moduleElem = XMLTool.getElement( moduleDataElem, "config" );

        // find title xpath
        Element formElem = XMLTool.getElement( moduleElem, "form" );
        Element titleElem = XMLTool.getElement( formElem, "title" );
        String titleFieldName = titleElem.getAttribute( "name" );
        String titleXPath = null;

        Node[] nodes = XMLTool.filterNodes( formElem.getChildNodes(), Node.ELEMENT_NODE );
        for ( int i = 0; i < nodes.length && titleXPath == null; ++i )
        {
            Element elem = (Element) nodes[i];
            if ( elem.getTagName().equals( "block" ) )
            {
                Node[] inputNodes = XMLTool.filterNodes( elem.getChildNodes(), Node.ELEMENT_NODE );
                for ( Node inputNode : inputNodes )
                {
                    if ( titleFieldName.equals( ( (Element) inputNode ).getAttribute( "name" ) ) )
                    {
                        titleXPath = XMLTool.getElementText( XMLTool.getElement( (Element) inputNode, "xpath" ) );
                        break;
                    }
                }
            }
        }

        return XMLTool.getElementText( (Element) contentDataElem.getParentNode(), titleXPath );
    }

    public String getContentTitle( ExtendedMap formItems )
    {
        return formItems.getString( getTitleFieldName( formItems ) );
    }

    private static String getTitleFieldName( ExtendedMap formItems )
    {
        String titleFieldName = formItems.getString( "titleformkey" );

        // Dates and radiobuttons are not reported with their basic name.  Dates are prefixed with "date", and radiobuttons are
        // prefixed with rb:<code>:, where the code is generated by some Javascript-code which is not accessible here.

        if ( formItems.keySet().contains( titleFieldName ) )
        {
            return titleFieldName;
        }
        else
        {
            if ( formItems.keySet().contains( "date" + titleFieldName ) )
            {
                return "date" + titleFieldName;
            }
            else
            {
                for ( Object key : formItems.keySet() )
                {
                    if ( key instanceof String && ( (String) key ).startsWith( "rb:" ) &&
                        ( (String) key ).endsWith( ":" + titleFieldName ) )
                    {
                        return (String) key;
                    }
                }
            }
        }
        throw new IllegalArgumentException( "No valid title field name on input." );
    }

    public BinaryData[] getBinaries( ExtendedMap formItems )
        throws VerticalAdminException
    {
        BinaryData[] binaryData = null;

        FileItem[] fileItems = formItems.getFileItems();
        if ( fileItems != null && fileItems.length > 0 )
        {
            binaryData = new BinaryData[fileItems.length];

            for ( int i = 0; i < fileItems.length; i++ )
            {
                binaryData[i] = AdminHandlerBaseServlet.createBinaryData( fileItems[i] );
                String fieldName = fileItems[i].getFieldName();

                if ( fieldName.startsWith( "f_" ) )
                {
                    fieldName = fieldName.substring( "f_".length() );
                    if ( !formItems.containsKey( "filename_" + fieldName ) )
                    {
                        formItems.put( "filename_" + fieldName, binaryData[i].fileName );
                    }
                }
            }
        }
        formItems.put( "__binaries", binaryData );
        return binaryData;
    }

    public int[] getDeleteBinaries( ExtendedMap formItems )
    {
        int versionKey = formItems.getInt( "versionkey" );
        ArrayList<Integer> keepBinaries = (ArrayList<Integer>) formItems.get( "__keepbinaries", null );
        int[] oldBinaries = admin.getBinaryDataKeysByVersion( versionKey );

        ArrayList<Integer> deleted = new ArrayList<Integer>();
        for ( int oldBinary : oldBinaries )
        {
            if ( keepBinaries == null || !keepBinaries.contains( oldBinary ) )
            {
                deleted.add( oldBinary );
            }
        }

        int[] deletedArray = new int[deleted.size()];
        int cnt = 0;
        for ( Integer i : deleted )
        {
            deletedArray[cnt++] = i;
        }
        return deletedArray;
    }

}

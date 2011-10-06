/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.xmlbuilders;

import java.text.ParseException;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.util.DateUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.AdminHandlerBaseServlet;
import com.enonic.vertical.adminweb.VerticalAdminException;
import com.enonic.vertical.adminweb.VerticalAdminLogger;
import com.enonic.vertical.adminweb.handlers.ContentBaseHandlerServlet;
import com.enonic.vertical.engine.AccessRight;

import com.enonic.cms.core.content.binary.BinaryData;

import com.enonic.cms.domain.portal.PrettyPathNameCreator;
import com.enonic.cms.domain.security.user.User;

public class ContentBaseXMLBuilder
    extends AbstractBaseXMLBuilder
    implements ContentXMLBuilder
{
    private static final String DUEDATE_DATE_FORMITEM_KEY = "date_assignment_duedate";

    private static final String DUEDATE_TIME_FORMITEM_KEY = "time_assignment_duedate";

    private static final String COMMENT_FORMITEM_KEY = "_comment";

    private static final String ASSIGNMENT_DESCRIPTION_FORMITEM_KEY = "_assignment_description";

    private static final String ASSIGNEE_FORMITEM_KEY = "_assignee";

    private static final String ASSIGNER_FORMITEM_KEY = "_assigner";

    public static final String ASSIGNEE_XML_KEY = "assignee";

    public static final String ASSIGNER_XML_KEY = "assigner";

    public static final String ASSIGNMENT_DESCRIPTION_XML_KEY = "assignment-description";

    public static final String COMMENT_XML_KEY = "comment";

    public static final String ASSIGNMENT_DUEDATE_XML_KEY = "assignment-duedate";

    private static final String DEFAULT_ASSIGNMENT_DUEDATE_HHMM = "23:59";

    public String getContentTitle( ExtendedMap formItems )
    {
        return formItems.getString( getTitleFormKey() );
    }

    public String getTitleFormKey()
    {
        return "title";
    }

    public String getContentTitle( Element contentDataElem, int contentTypeKey )
    {
        return XMLTool.getElementText( XMLTool.getElement( contentDataElem, "title" ) );
    }

    /**
     * <p> A generic method that builds the contentdata block of arbitrary depth. All fields starting with the pattern 'contentdata_foo' and
     * followed by arbitrary patterns like '_bar' are translated to nested xml elements, where 'foo' and 'bar' is the name of the elements.
     * The last element can be specified with one of the following prefix and suffixes: <ul> <li>@      (prefix) - element text will be set
     * as an attribute to the parent element <li>_CDATA (suffix) - element text will be wrapped in a CDATA element <li>_XHTML (suffix) -
     * element text will be turned into XHTML elements </ul> When reaching one of the prefix and suffixes, the element creating process will
     * terminate for this field. </p> <p/> <p>Elements already created with the same path will be reused.</p> <p/> <p>Example:<br> The key
     * 'contentdata_foo_bar_zot_CDATA' with the value '<b>alpha</b>' will transform into the following xml:
     * <pre>
     * &lt;contentdata&gt;
     *   &lt;foo&gt;
     *     &lt;bar&gt;
     *       &lt;zot&gt;
     *         &lt;[!CDATA[&lt;b&gt;alpha&lt;/b&gt;]]&gt;
     *       &lt;/zot&gt;
     *     &lt;/bar&gt;
     *   &lt;/foo&gt;
     * &lt;/contentdata&gt;
     * </pre>
     * </p>
     */
    public void buildContentTypeXML( User user, Document doc, Element contentdata, ExtendedMap formItems )
        throws VerticalAdminException
    {
        for ( Object o : formItems.keySet() )
        {
            String key = (String) o;
            StringTokenizer keyTokenizer = new StringTokenizer( key, "_" );

            if ( "contentdata".equals( keyTokenizer.nextToken() ) )
            {
                Element root = contentdata;
                while ( keyTokenizer.hasMoreTokens() )
                {
                    String keyToken = keyTokenizer.nextToken();
                    if ( "CDATA".equals( keyToken ) )
                    {
                        XMLTool.createCDATASection( doc, root, formItems.getString( key ) );
                        break;
                    }
                    else if ( "XML".equals( keyToken ) )
                    {
                        String xmlDoc = formItems.getString( key );
                        Document tempDoc = XMLTool.domparse( xmlDoc );
                        root.appendChild( doc.importNode( tempDoc.getDocumentElement(), true ) );
                        break;
                    }
                    else if ( "XHTML".equals( keyToken ) )
                    {
                        XMLTool.createXHTMLNodes( doc, root, formItems.getString( key ), true );
                        break;
                    }
                    else if ( keyToken.charAt( 0 ) == '@' )
                    {
                        root.setAttribute( keyToken.substring( 1 ), formItems.getString( key ) );
                        break;
                    }
                    else
                    {
                        Element elem = XMLTool.getElement( root, keyToken );
                        if ( elem == null )
                        {
                            root = XMLTool.createElement( doc, root, keyToken );
                        }
                        else
                        {
                            root = elem;
                        }
                        if ( !keyTokenizer.hasMoreTokens() )
                        {
                            XMLTool.createTextNode( doc, root, formItems.getString( key ) );
                            break;
                        }
                    }
                }
            }
        }
    }

    public final String buildXML( ExtendedMap formItems, User user, boolean create, boolean excludeContendataXML,
                                  boolean usePersistedContendataXML )
        throws VerticalAdminException
    {

        boolean published = formItems.getBoolean( "published", false );
        boolean sentToApproval = formItems.getBoolean( "senttoapproval", false );
        boolean rejected = formItems.getBoolean( "rejected", false );

        int categoryKey;
        if ( formItems.containsKey( "cat" ) )
        {
            categoryKey = formItems.getInt( "cat" );
        }
        else
        {
            categoryKey = formItems.getInt( "category_key" );
        }

        int unitKey = -1;
        if ( formItems.containsKey( "selectedunitkey" ) )
        {
            unitKey = formItems.getInt( "selectedunitkey" );
        }
        else if ( formItems.containsKey( "unitkey" ) )
        {
            unitKey = formItems.getInt( "unitkey" );
        }

        int contenttypekey = ContentBaseHandlerServlet.getContentTypeKey( formItems );

        Document doc = XMLTool.createDocument( "content" );
        Element content = doc.getDocumentElement();

        // General content attributes
        if ( !create )
        {
            int key = formItems.getInt( "key" );

            // @key
            content.setAttribute( "key", String.valueOf( key ) );

            // version/@key
            int versionKey = formItems.getInt( "versionkey" );
            content.setAttribute( "versionkey", String.valueOf( versionKey ) );
        }

        // owner/@key
        if ( !create && formItems.containsKey( "_pubdata_owner" ) )
        {
            Element ownerElem = XMLTool.createElement( doc, content, "owner" );
            ownerElem.setAttribute( "key", formItems.getString( "_pubdata_owner" ) );
        }

        // assignee/@key
        if ( formItems.containsKey( ASSIGNEE_FORMITEM_KEY ) )
        {
            Element assigneeElem = XMLTool.createElement( doc, content, ASSIGNEE_XML_KEY );
            assigneeElem.setAttribute( "key", formItems.getString( ASSIGNEE_FORMITEM_KEY ) );
        }

        if ( formItems.containsKey( ASSIGNER_FORMITEM_KEY ) )
        {
            Element assignerElem = XMLTool.createElement( doc, content, ASSIGNER_XML_KEY );
            assignerElem.setAttribute( "key", formItems.getString( ASSIGNER_FORMITEM_KEY ) );
        }

        // DueDate
        buildDueDateElement( formItems, doc, content );

        // Comment
        if ( formItems.containsKey( COMMENT_FORMITEM_KEY ) )
        {
            XMLTool.createElement( doc, content, COMMENT_XML_KEY, formItems.getString( COMMENT_FORMITEM_KEY ) );
        }

        // Assignment comment
        if ( formItems.containsKey( ASSIGNMENT_DESCRIPTION_FORMITEM_KEY ) )
        {
            XMLTool.createElement( doc, content, ASSIGNMENT_DESCRIPTION_XML_KEY,
                                   formItems.getString( ASSIGNMENT_DESCRIPTION_FORMITEM_KEY ) );
        }

        // modifier/@key
        Element modifierElem = XMLTool.createElement( doc, content, "modifier" );
        modifierElem.setAttribute( "key", String.valueOf( user.getKey() ) );

        if ( unitKey >= 0 )
        {
            content.setAttribute( "unitkey", String.valueOf( unitKey ) );
        }
        content.setAttribute( "contenttypekey", String.valueOf( contenttypekey ) );

        content.setAttribute( "priority", formItems.getString( "_pubdata_priority", "0" ) );

        // language
        int languageKey;
        if ( formItems.containsKey( "_pubdata_languagekey" ) )
        {
            languageKey = formItems.getInt( "_pubdata_languagekey" );
        }
        else
        {
            languageKey = admin.getUnitLanguageKey( unitKey );
        }
        content.setAttribute( "languagekey", String.valueOf( languageKey ) );

        // status
        int status = formItems.getInt( "_pubdata_status", 0 );
        if ( rejected )
        {
            status = 0;
        }
        else if ( sentToApproval )
        {
            status = 1;
        }
        else if ( published )
        {
            status = 2;
        }
        content.setAttribute( "status", String.valueOf( status ) );

        try
        {
            // record the publish dates if set
            Date publishFrom = null;
            Date publishTo = null;
            Date now = new Date();
            if ( formItems.containsKey( "date_pubdata_publishfrom" ) )
            {
                StringBuffer date = new StringBuffer( formItems.getString( "date_pubdata_publishfrom" ) );
                date.append( ' ' );
                date.append( formItems.getString( "time_pubdata_publishfrom", "00:00" ) );
                publishFrom = DateUtil.parseDateTime( date.toString() );
            }

            if ( formItems.containsKey( "date_pubdata_publishto" ) )
            {
                StringBuffer date = new StringBuffer( formItems.getString( "date_pubdata_publishto" ) );
                date.append( ' ' );
                date.append( formItems.getString( "time_pubdata_publishto", "00:00" ) );
                publishTo = DateUtil.parseDateTime( date.toString() );
            }

            if ( published && publishFrom == null )
            {
                publishFrom = now;
            }

            if ( published && publishTo != null && publishTo.before( now ) )
            {
                publishTo = null;
            }

            if ( publishFrom != null )
            {
                content.setAttribute( "publishfrom", DateUtil.formatISODateTime( publishFrom ) );
            }

            if ( publishTo != null )
            {
                content.setAttribute( "publishto", DateUtil.formatISODateTime( publishTo ) );
            }

        }
        catch ( ParseException e )
        {
            VerticalAdminLogger.errorAdmin( ContentBaseHandlerServlet.class, 10, "Error parsing dates: %t", e );
        }

        // created:
        if ( !create )
        {
            content.setAttribute( "created", formItems.getString( "_pubdata_created" ) );
        }

        String contentTitle;
        if ( usePersistedContendataXML || excludeContendataXML )
        {
            int versionKey = formItems.getInt( "versionkey" );
            contentTitle = admin.getContentTitle( versionKey );

            if ( contentTitle == null || contentTitle.length() == 0 )
            {
                Document contentDoc = XMLTool.domparse( admin.getContentVersion( user, versionKey ) );
                Element contentElem = (Element) contentDoc.getDocumentElement().getFirstChild();
                Element contentDataElem = XMLTool.getElement( contentElem, "contentdata" );
                contentTitle = getContentTitle( contentDataElem, contenttypekey );
            }
        }
        else
        {
            contentTitle = getContentTitle( formItems );
        }

        // Content title
        XMLTool.createElement( doc, content, "title", contentTitle );

        String contentName = formItems.getString( "_name", null );

        if ( StringUtils.isBlank( contentName ) )
        {
            contentName = PrettyPathNameCreator.generatePrettyPathName( contentTitle );
        }

        XMLTool.createElement( doc, content, "name", contentName );

        // create the units root
        Element contentdata = XMLTool.createElement( doc, content, "contentdata" );

        // category
        Element categoryname;
        // Not sure if category_name is present in all situations
        if ( formItems.containsKey( "category_name" ) )
        {
            categoryname = XMLTool.createElement( doc, content, "categoryname", formItems.getString( "category_name" ) );
        }
        else
        {
            categoryname = XMLTool.createElement( doc, content, "categoryname" );
        }

        categoryname.setAttribute( "key", String.valueOf( categoryKey ) );

        if ( !excludeContendataXML )
        {
            if ( usePersistedContendataXML )
            {
                Document contentXML = XMLTool.domparse( admin.getContentXMLField( user, formItems.getInt( "versionkey" ) ) );
                XMLTool.replaceElement( contentdata, contentXML.getDocumentElement() );
            }
            else
            {
                // Create the content type specific XML
                buildContentTypeXML( user, doc, contentdata, formItems );
            }
        }

        // add access rights to content xml
        AdminHandlerBaseServlet.buildAccessRightsXML( content, null, formItems, AccessRight.CONTENT );

        return XMLTool.documentToString( doc );
    }

    private void buildDueDateElement( ExtendedMap formItems, Document doc, Element content )
    {
        if ( formItems.containsKey( DUEDATE_DATE_FORMITEM_KEY ) )
        {
            StringBuffer date = new StringBuffer( formItems.getString( DUEDATE_DATE_FORMITEM_KEY ) );
            date.append( ' ' );
            date.append( formItems.getString( DUEDATE_TIME_FORMITEM_KEY, DEFAULT_ASSIGNMENT_DUEDATE_HHMM ) );

            Date dueDate = null;

            try
            {
                dueDate = DateUtil.parseDateTime( date.toString() );
                XMLTool.createElement( doc, content, ASSIGNMENT_DUEDATE_XML_KEY, DateUtil.formatISODateTime( dueDate ) );
            }
            catch ( ParseException e )
            {
                VerticalAdminLogger.errorAdmin( ContentBaseHandlerServlet.class, 10, "Error parsing dates: %t", e );
            }
        }
    }

    public int[] getRelatedContentKeys( ExtendedMap formItems )
    {
        return null;
    }

    public BinaryData[] getBinaries( ExtendedMap formItems )
        throws VerticalAdminException
    {
        return null;
    }

    public int[] getDeleteBinaries( ExtendedMap formItems )
        throws VerticalAdminException
    {
        return null;
    }


}

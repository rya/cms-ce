/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalException;
import com.enonic.vertical.VerticalRuntimeException;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalKeyException;
import com.enonic.vertical.engine.VerticalRemoveException;

import com.enonic.cms.framework.util.TIntArrayList;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.domain.structure.page.PageEntity;
import com.enonic.cms.domain.structure.page.PageWindowEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateKey;

public final class PageHandler
    extends BaseHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( PageHandler.class.getName() );

    private static final String PAG_SELECT_KEY = "SELECT pag_lKey FROM tPage ";

    private static final String PAG_WHERE_PAT = "WHERE PAG_PAT_LKEY = ?";

    private static final String PCO_REMOVE_WHERE_PAG = "DELETE FROM tPageConObj WHERE pco_pag_lKey = ?";

    private static final String PAG_TABLE = "tPage";

    private static final String PAG_CREATE =
        "INSERT INTO tPage (pag_lKey, pag_pat_lKey, pag_sXML) VALUES (?, ?, " + "?"//getSQLConstants().blobString()
            + ")";

    private static final String PCO_CREATE =
        "INSERT INTO tPageConObj (pco_pag_lKey, pco_cob_lKey, " + "pco_lOrder, pco_ptp_lKey, pco_dteTimestamp) VALUES " + "(?, ?, ?, ?, " +
            "@currentTimestamp@" + ")";

    private static final String PCO_SELECT_COB = "SELECT pco_cob_lKey FROM tPageConObj";

    private static final String PCO_WHERE_PTP = " WHERE pco_ptp_lKey = ?";

    private int[] createPage( Document doc )
        throws VerticalCreateException
    {

        Element docElem = doc.getDocumentElement();
        Element[] pageElems;
        if ( "page".equals( docElem.getTagName() ) )
        {
            pageElems = new Element[]{docElem};
        }
        else
        {
            pageElems = XMLTool.getElements( doc.getDocumentElement() );
        }

        Connection con = null;
        PreparedStatement preparedStmt = null;
        TIntArrayList newKeys = new TIntArrayList();

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( PAG_CREATE );

            for ( Element root : pageElems )
            {

                // key
                int key;
                String keyStr = root.getAttribute( "key" );
                if ( keyStr == null || keyStr.length() == 0 )
                {
                    key = getNextKey( PAG_TABLE );
                }
                else
                {
                    key = Integer.parseInt( keyStr );
                }
                newKeys.add( key );

                preparedStmt.setInt( 1, key );

                // attribute: pagetemplatekey
                String tmp = root.getAttribute( "pagetemplatekey" );
                preparedStmt.setInt( 2, Integer.parseInt( tmp ) );

                // get the pagedata element and serialize it
                Element pageDataElement = XMLTool.getElement( root, "pagedata" );
                Document pageDataDoc = XMLTool.createDocument();
                pageDataDoc.appendChild( pageDataDoc.importNode( pageDataElement, true ) );
                byte[] pageDataBytes = XMLTool.documentToBytes( pageDataDoc, "UTF-8" );
                preparedStmt.setBinaryStream( 3, new ByteArrayInputStream( pageDataBytes ), pageDataBytes.length );

                // get the sub-elements
                Map<String, Element> subelems = XMLTool.filterElements( root.getChildNodes() );

                // add the page
                int result = preparedStmt.executeUpdate();
                if ( result <= 0 )
                {
                    String message = "Failed to create page.";

                    VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                                    StringUtil.expandString( message, (Object) null, null ) );
                }

                // create all pageconobj entries for page
                Element contentojectsElem = subelems.get( "contentobjects" );
                Element[] contentobjectElems = XMLTool.getElements( contentojectsElem );
                for ( Element e : contentobjectElems )
                {
                    e.setAttribute( "pagekey", Integer.toString( key ) );
                }

                createPageContentObjects( contentojectsElem );
            }

            preparedStmt.close();
            preparedStmt = null;
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to create page(s) because of database error: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a key field: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                            StringUtil.expandString( message, (Object) null, nfe ), nfe );
        }
        catch ( VerticalKeyException gke )
        {
            String message = "Error generating new key.";

            VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                            StringUtil.expandString( message, (Object) null, gke ), gke );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }

        return newKeys.toArray();
    }

    public int createPage( String xmlData )
        throws VerticalCreateException
    {

        Document doc = XMLTool.domparse( xmlData, "page" );

        int[] keys = createPage( doc );
        if ( keys == null || keys.length == 0 )
        {
            String message = "Failed to create page , no key returned";

            VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                            StringUtil.expandString( message, (Object) null, null ) );
        }

        return ( keys != null && keys.length > 0 ) ? keys[0] : -1;
    }

    private void createPageContentObjects( Element contentobjects )
        throws com.enonic.vertical.engine.VerticalCreateException
    {

        Node[] contentobject = XMLTool.filterNodes( contentobjects.getChildNodes(), Node.ELEMENT_NODE );

        // connection variables
        Connection con = null;
        PreparedStatement preparedStmt = null;
        int pageKey;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( PCO_CREATE );

            for ( Node n : contentobject )
            {
                Element co = (Element) n;
                Map<String, Element> subelems = XMLTool.filterElements( co.getChildNodes() );

                // attribute: key (generated in database)
                String tmp = co.getAttribute( "pagekey" );
                pageKey = Integer.parseInt( tmp );
                preparedStmt.setInt( 1, pageKey );

                // attribute: stylesheet key
                tmp = co.getAttribute( "conobjkey" );
                preparedStmt.setInt( 2, Integer.parseInt( tmp ) );

                // attribute: stylesheet key
                tmp = co.getAttribute( "parameterkey" );
                preparedStmt.setInt( 4, Integer.parseInt( tmp ) );

                // extract order
                Element elem = subelems.get( "order" );
                int order = Integer.parseInt( XMLTool.getElementText( elem ) );
                preparedStmt.setInt( 3, order );

                preparedStmt.executeUpdate();
            }
        }
        catch ( SQLException sqle )
        {

            VerticalRuntimeException.error( this.getClass(), VerticalException.class, StringUtil.expandString(
                    "A database error occured while creating the contentobject page: %t", (Object) null, sqle ), sqle );
        }
        catch ( NumberFormatException nfe )
        {

            VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                            StringUtil.expandString( "Error parsing the key field: %t", (Object) null,
                                                                     nfe ), nfe );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }

    }

    public XMLDocument getPage( int pageKey, boolean complete )
    {
        PageEntity entity = pageDao.findByKey( pageKey );
        Document doc = createPagesDocument( entity != null ? Arrays.asList( entity ) : null, complete );
        return XMLDocumentFactory.create( doc );
    }

    private Document createPageContentObject( PageEntity entity )
    {
        Document doc = XMLTool.createDocument();
        Element root = XMLTool.createRootElement( doc, "contentobjects" );

        for ( PageWindowEntity pageWindow : entity.getPageWindows() )
        {
            Element elem = XMLTool.createElement( doc, root, "contentobject" );
            elem.setAttribute( "pagekey", String.valueOf( entity.getKey() ) );
            elem.setAttribute( "conobjkey", String.valueOf( pageWindow.getPortlet().getKey() ) );
            elem.setAttribute( "parameterkey", String.valueOf( pageWindow.getPageTemplateRegion().getKey() ) );

            Document contentdata = XMLDocumentFactory.create( pageWindow.getPortlet().getXmlDataAsJDOMDocument() ).getAsDOMDocument();
            Node xmldata_root = doc.importNode( contentdata.getDocumentElement(), true );
            elem.appendChild( xmldata_root );

            XMLTool.createElement( doc, elem, "order", String.valueOf( pageWindow.getOrder() ) );
            XMLTool.createElement( doc, elem, "name", pageWindow.getPortlet().getName() );
            XMLTool.createElement( doc, elem, "separator", pageWindow.getPageTemplateRegion().getSeparator() );
            elem = XMLTool.createElement( doc, elem, "parametername", pageWindow.getPageTemplateRegion().getName() );
            elem.setAttribute( "multiple", String.valueOf( pageWindow.getPageTemplateRegion().isMultiple() ) );
            elem.setAttribute( "override", String.valueOf( pageWindow.getPageTemplateRegion().isOverride() ) );
        }

        return doc;
    }

    private Document createPagesDocument( List<PageEntity> entities, boolean complete )
    {
        Document doc = XMLTool.createDocument();
        Element root = XMLTool.createRootElement( doc, "pages" );

        if ( entities != null )
        {
            for ( PageEntity page : entities )
            {
                Element elem = XMLTool.createElement( doc, root, "page" );
                elem.setAttribute( "key", String.valueOf( page.getKey() ) );
                elem.setAttribute( "pagetemplatekey", String.valueOf( page.getTemplate().getKey() ) );

                org.jdom.Document pageXmlDataAsJdomDoc = page.getXmlDataAsDocument();
                if ( pageXmlDataAsJdomDoc != null )
                {
                    Document pageDataDocument = XMLDocumentFactory.create( pageXmlDataAsJdomDoc ).getAsDOMDocument();
                    elem.appendChild( doc.importNode( pageDataDocument.getDocumentElement(), true ) );
                }
                else
                {
                    XMLTool.createElement( doc, elem, "pagedata" );
                }

                if ( complete )
                {
                    Document contentobj = createPageContentObject( page );
                    Node contentobjects_root = doc.importNode( contentobj.getDocumentElement(), true );
                    elem.appendChild( contentobjects_root );
                }
            }
        }

        return doc;
    }

    public int[] getPageKeysByPageTemplateKey( int pageTemplateKey )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        TIntArrayList pageKeys = new TIntArrayList();

        try
        {
            StringBuffer sql = new StringBuffer( PAG_SELECT_KEY );
            sql.append( PAG_WHERE_PAT );

            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, pageTemplateKey );
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {
                pageKeys.add( resultSet.getInt( "pag_lKey" ) );
            }
        }
        catch ( SQLException se )
        {
            String message = "Failed to get page keys by page template key: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, se ), se );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return pageKeys.toArray();
    }

    public PageTemplateKey getPageTemplateKey( int pageKey )
    {
        PageEntity entity = pageDao.findByKey( pageKey );
        PageTemplateEntity templateEntity = entity != null ? entity.getTemplate() : null;
        int pageTemplateKey = templateEntity != null ? templateEntity.getKey() : -1;
        return new PageTemplateKey( pageTemplateKey );
    }

    public int[] getContentObjectKeys( int[] pageTemplateParameterKeys )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        TIntArrayList contentObjectKeys = new TIntArrayList();

        try
        {
            StringBuffer sql = new StringBuffer( PCO_SELECT_COB );
            sql.append( PCO_WHERE_PTP );

            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            for ( int pageTemplateParameterKey : pageTemplateParameterKeys )
            {
                preparedStmt.setInt( 1, pageTemplateParameterKey );
                resultSet = preparedStmt.executeQuery();

                while ( resultSet.next() )
                {
                    contentObjectKeys.add( resultSet.getInt( 1 ) );
                }
            }
        }
        catch ( SQLException se )
        {
            String message = "Failed to get page keys by page template key: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, null ) );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return contentObjectKeys.toArray();
    }

    public void removePageContentObjects( int pageKey, int[] contentObjectKeys )
        throws VerticalRemoveException
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        StringBuffer sql = new StringBuffer( PCO_REMOVE_WHERE_PAG );
        if ( contentObjectKeys != null && contentObjectKeys.length > 0 )
        {
            sql.append( " AND pco_cob_lKey = ?" );
        }

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, pageKey );
            if ( contentObjectKeys != null && contentObjectKeys.length > 0 )
            {
                for ( int contentObjectKey : contentObjectKeys )
                {
                    preparedStmt.setInt( 2, contentObjectKey );
                    preparedStmt.executeUpdate();
                }
            }
            else
            {
                preparedStmt.executeUpdate();
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to remove page content objects because of database error: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }
}

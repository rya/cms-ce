/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.util.ArrayUtil;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalCopyException;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalEngineLogger;
import com.enonic.vertical.engine.VerticalKeyException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalUpdateException;
import com.enonic.vertical.engine.XDG;

import com.enonic.cms.framework.util.TIntArrayList;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.domain.CalendarUtil;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.structure.RunAsType;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;
import com.enonic.cms.domain.structure.page.template.PageTemplateEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateKey;
import com.enonic.cms.domain.structure.page.template.PageTemplatePortletEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateRegionEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateType;
import com.enonic.cms.domain.structure.portlet.PortletEntity;

public final class PageTemplateHandler
    extends BaseHandler
{


    private static final String PAT_SELECT_KEY_BY_MENU = "SELECT pat_lKey FROM tPageTemplate WHERE pat_men_lKey = ?";

    private static final String PAT_REMOVE = "DELETE FROM tPageTemplate WHERE pat_lKey = ?";

    private static final String PAT_TABLE = "tPageTemplate";

    private static final String PAT_CREATE = "INSERT INTO tPageTemplate (pat_lKey, pat_sStyle, pat_men_lKey, pat_sName, " +
        "pat_sDescription, pat_dteTimestamp, pat_xmlData, pat_sCSS, pat_lType, pat_lRunAs) VALUES (?, ?, ?, ?, ?, @currentTimestamp@, ?, ?, ?, ?)";

    private static final String PAT_UPDATE =
        "UPDATE tPageTemplate SET pat_sStyle = ?, pat_men_lKey = ?, pat_sName = ?, pat_sDescription = ?, " +
            "pat_dteTimestamp = @currentTimestamp@, pat_xmlData = ?, pat_sCSS = ?, pat_lType = ?, pat_lRunAs= ? WHERE pat_lKey = ?";

    private static final String PTP_UPDATE =
        "UPDATE tPageTemplParam SET ptp_pat_lKey = ?, ptp_sParamName = ?, ptp_bMultiple = ?, ptp_sSeparator = ?, ptp_bOverride = ? " +
            "WHERE ptp_lKey = ?";

    private static final String PTP_SELECT_PTP_KEY = "SELECT ptp_lKey FROM tPageTemplParam WHERE ptp_pat_lKey=?";

    private static final String PTP_CREATE =
        "INSERT INTO tPageTemplParam (ptp_lKey, ptp_pat_lKey, ptp_sParamName, ptp_bMultiple, ptp_sSeparator, ptp_bOverride) VALUES " +
            "(?, ?, ?, ?, ?, ?)";

    private static final String PTP_TABLE = "tPageTemplParam";

    private static final String PTC_REMOVE_PAT = "DELETE FROM tPageTemplConObj WHERE ptc_pat_lKey = ?";

    private static final String PTC_REMOVE_NOT_COB_MANY =
        "DELETE FROM tPageTemplConObj WHERE ptc_pat_lKey = ? AND ptc_cob_lKey NOT IN (%0)";

    private static final String PTP_REMOVE = "DELETE FROM tPageTemplParam WHERE ptp_lKey = ?";

    private static final String PTP_PAT_REMOVE = "DELETE FROM tPageTemplParam WHERE ptp_pat_lKey = ?";

    // tPageTemplConObj

    private static final String PTC_CREATE =
        "INSERT INTO tPageTemplConObj (ptc_pat_lKey, ptc_cob_lKey, ptc_lOrder, ptc_ptp_lKey, ptc_dteTimestamp) " +
            "VALUES (?, ?, ?, ?, @currentTimestamp@)";

    private static final String PTC_UPDATE =
        "UPDATE tPageTemplConObj SET ptc_lOrder = ?, ptc_ptp_lKey = ?, ptc_dteTimestamp = @currentTimestamp@ " +
            "WHERE ptc_pat_lKey = ? AND ptc_cob_lKey = ?";

    public int createPageTemplate( String xmlData )
        throws VerticalCreateException
    {

        Document doc = XMLTool.domparse( xmlData, "pagetemplate" );

        int[] keys = createPageTemplate( null, doc, true );
        if ( keys == null || keys.length == 0 )
        {
            String message = "Failed to create page template. No key returned.";
            VerticalEngineLogger.errorCreate( this.getClass(), 1, message, null );
            return -1;
        }

        return keys[0];
    }

    private int[] createPageTemplate( CopyContext copyContext, Document doc, boolean useOldKey )
        throws VerticalCreateException
    {

        Element docElem = doc.getDocumentElement();
        Element[] pagetemplateElems;
        if ( "pagetemplate".equals( docElem.getTagName() ) )
        {
            pagetemplateElems = new Element[]{docElem};
        }
        else
        {
            pagetemplateElems = XMLTool.getElements( doc.getDocumentElement() );
        }

        Connection con = null;
        PreparedStatement preparedStmt = null;
        TIntArrayList newKeys = new TIntArrayList();

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( PAT_CREATE );

            for ( Element root : pagetemplateElems )
            {
                Map<String, Element> subelems = XMLTool.filterElements( root.getChildNodes() );

                // key
                int key;
                String keyStr = root.getAttribute( "key" );
                if ( !useOldKey || keyStr == null || keyStr.length() == 0 )
                {
                    key = getNextKey( PAT_TABLE );
                }
                else
                {
                    key = Integer.parseInt( keyStr );
                }
                if ( copyContext != null )
                {
                    copyContext.putPageTemplateKey( Integer.parseInt( keyStr ), key );
                }
                newKeys.add( key );
                preparedStmt.setInt( 1, key );

                // attribute: menukey
                keyStr = root.getAttribute( "menukey" );
                int menuKey = Integer.parseInt( keyStr );
                preparedStmt.setInt( 3, menuKey );

                // element: stylesheet
                Element stylesheet = subelems.get( "stylesheet" );
                String tmp = stylesheet.getAttribute( "stylesheetkey" );
                preparedStmt.setString( 2, tmp );

                // element: name
                Element subelem = subelems.get( "name" );
                String name = XMLTool.getElementText( subelem );
                preparedStmt.setString( 4, name );

                // element: name
                subelem = subelems.get( "description" );
                if ( subelem != null )
                {
                    String description = XMLTool.getElementText( subelem );
                    if ( description != null )
                    {
                        preparedStmt.setString( 5, description );
                    }
                    else
                    {
                        preparedStmt.setNull( 5, Types.VARCHAR );
                    }
                }
                else
                {
                    preparedStmt.setNull( 5, Types.VARCHAR );
                }

                // element: timestamp (using the database timestamp at creation)
                /* no code */

                // element: datasources
                subelem = subelems.get( "pagetemplatedata" );
                Document ptdDoc;
                if ( subelem != null )
                {
                    ptdDoc = XMLTool.createDocument();
                    ptdDoc.appendChild( ptdDoc.importNode( subelem, true ) );
                }
                else
                {
                    ptdDoc = XMLTool.createDocument( "pagetemplatedata" );
                }
                byte[] ptdBytes = XMLTool.documentToBytes( ptdDoc, "UTF-8" );
                ByteArrayInputStream byteStream = new ByteArrayInputStream( ptdBytes );
                preparedStmt.setBinaryStream( 6, byteStream, ptdBytes.length );

                // element: CSS
                subelem = subelems.get( "css" );
                if ( subelem != null )
                {
                    preparedStmt.setString( 7, subelem.getAttribute( "stylesheetkey" ) );
                }
                else
                {
                    preparedStmt.setNull( 7, Types.VARCHAR );
                }

                // pagetemplate type:
                PageTemplateType type = PageTemplateType.valueOf( root.getAttribute( "type" ).toUpperCase() );
                preparedStmt.setInt( 8, type.getKey() );

                RunAsType runAs = RunAsType.INHERIT;
                String runAsStr = root.getAttribute( "runAs" );
                if ( StringUtils.isNotEmpty( runAsStr ) )
                {
                    runAs = RunAsType.valueOf( runAsStr );
                }
                preparedStmt.setInt( 9, runAs.getKey() );

                // add
                int result = preparedStmt.executeUpdate();
                if ( result == 0 )
                {
                    String message = "Failed to create page template. No page template created.";
                    VerticalEngineLogger.errorCreate( this.getClass(), 0, message, null );
                }

                // create page template parameters
                Element ptpsElem = XMLTool.getElement( root, "pagetemplateparameters" );
                int[] ptpKeys = null;
                if ( ptpsElem != null )
                {
                    Element[] ptpElems = XMLTool.getElements( ptpsElem );
                    for ( Element ptpElem : ptpElems )
                    {
                        ptpElem.setAttribute( "pagetemplatekey", Integer.toString( key ) );
                    }

                    Document ptpDoc = XMLTool.createDocument();
                    Node n = ptpDoc.importNode( ptpsElem, true );
                    ptpDoc.appendChild( n );
                    ptpKeys = createPageTemplParam( copyContext, ptpDoc );
                }

                // create all pageconobj entries for page
                Element contentobjectsElem = XMLTool.getElement( root, "contentobjects" );
                if ( contentobjectsElem != null )
                {
                    Element[] contentobjectElems = XMLTool.getElements( contentobjectsElem );

                    for ( Element contentobjectElem : contentobjectElems )
                    {
                        contentobjectElem.setAttribute( "pagetemplatekey", Integer.toString( key ) );
                        if ( copyContext != null )
                        {
                            keyStr = contentobjectElem.getAttribute( "parameterkey" );
                            int newKey = copyContext.getPageTemplateParameterKey( Integer.parseInt( keyStr ) );
                            contentobjectElem.setAttribute( "parameterkey", String.valueOf( newKey ) );
                        }
                        else
                        {
                            int pIndex = Integer.parseInt( contentobjectElem.getAttribute( "parameterkey" ).substring( 1 ) );
                            contentobjectElem.setAttribute( "parameterkey", Integer.toString( ptpKeys[pIndex] ) );
                        }
                    }

                    Document coDoc = XMLTool.createDocument();
                    coDoc.appendChild( coDoc.importNode( contentobjectsElem, true ) );
                    updatePageTemplateCOs( coDoc, key, ptpKeys );
                }

                // element: contenttypes
                subelem = subelems.get( "contenttypes" );
                Element[] ctyElems = XMLTool.getElements( subelem );
                int[] ctys = new int[ctyElems.length];
                for ( int j = 0; j < ctyElems.length; j++ )
                {
                    ctys[j] = Integer.parseInt( ctyElems[j].getAttribute( "key" ) );
                }
                setPageTemplateContentTypes( key, ctys );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to create page template because of database error: %t";
            VerticalEngineLogger.errorCreate( this.getClass(), 1, message, sqle );
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a key field: %t";
            VerticalEngineLogger.errorCreate( this.getClass(), 2, message, nfe );
        }
        catch ( VerticalKeyException gke )
        {
            String message = "Failed generate page template key: %t";
            VerticalEngineLogger.errorCreate( this.getClass(), 3, message, gke );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }

        return newKeys.toArray();
    }

    private void updatePageTemplateCOs( Document contentobjectDoc, int pageTemplateKey, int[] paramKeys )
        throws VerticalCreateException
    {

        // XML DOM
        Element root = contentobjectDoc.getDocumentElement();

        // check: does root element exist?
        if ( root == null )
        {
            String message = "Root element does not exist.";
            VerticalEngineLogger.errorCreate( this.getClass(), 0, message, null );
        }

        // check: if root element is not contentrating, throw create exception
        if ( !"contentobject".equals( root.getTagName() ) && !"contentobjects".equals( root.getTagName() ) )
        {
            String message = "Root element is not a contentobject or contentobjects element: %0";
            VerticalEngineLogger.errorCreate( this.getClass(), 1, message, root.getTagName(), null );
        }

        Node[] node;
        if ( "contentobjects".equals( root.getTagName() ) )
        {
            node = XMLTool.filterNodes( root.getChildNodes(), Node.ELEMENT_NODE );
        }
        else
        {
            node = new Node[]{root};
        }

        // connection variables
        Connection con = null;
        PreparedStatement createStmt = null;
        PreparedStatement updateStmt = null;

        try
        {
            con = getConnection();
            createStmt = con.prepareStatement( PTC_CREATE );
            updateStmt = con.prepareStatement( PTC_UPDATE );
            StringBuffer removeSQLPart = new StringBuffer();

            for ( int i = 0; i < node.length; i++ )
            {
                Element elem = (Element) node[i];
                Map<String, Element> subelems = XMLTool.filterElements( elem.getChildNodes() );

                // attribute: templatekey
                //String tmp = elem.getAttribute("pagetemplatekey");
                //pageTemplateKey = Integer.parseInt(tmp);

                // attribute: conobjkey
                String tmp = elem.getAttribute( "conobjkey" );
                int contentObjectKey = Integer.parseInt( tmp );
                if ( i > 0 )
                {
                    removeSQLPart.append( ',' );
                }
                removeSQLPart.append( contentObjectKey );

                // attribute: parameterkey
                tmp = elem.getAttribute( "parameterkey" );
                int parameterKey;
                if ( tmp.charAt( 0 ) == '_' )
                {
                    parameterKey = paramKeys[Integer.parseInt( tmp.substring( 1 ) )];
                }
                else
                {
                    parameterKey = Integer.parseInt( tmp );
                }

                // extract order
                Element subelem = subelems.get( "order" );
                int order = Integer.parseInt( XMLTool.getElementText( subelem ) );

                updateStmt.setInt( 1, order );
                updateStmt.setInt( 2, parameterKey );
                updateStmt.setInt( 3, pageTemplateKey );
                updateStmt.setInt( 4, contentObjectKey );

                // update page template content object
                int rowCount = updateStmt.executeUpdate();

                // if no page template content object were updated, create a new one
                if ( rowCount == 0 )
                {
                    createStmt.setInt( 1, pageTemplateKey );
                    createStmt.setInt( 2, contentObjectKey );
                    createStmt.setInt( 3, order );
                    createStmt.setInt( 4, parameterKey );
                    createStmt.executeUpdate();
                }
            }
            updateStmt.close();

            if ( removeSQLPart.length() > 0 )
            {
                String sql = StringUtil.expandString( PTC_REMOVE_NOT_COB_MANY, removeSQLPart );
                updateStmt = con.prepareStatement( sql );
                updateStmt.setInt( 1, pageTemplateKey );
                updateStmt.executeUpdate();
            }
            else if ( node.length == 0 )
            {
                updateStmt = con.prepareStatement( PTC_REMOVE_PAT );
                updateStmt.setInt( 1, pageTemplateKey );
                updateStmt.executeUpdate();
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to link page template to content object because of database error: %t";
            VerticalEngineLogger.errorCreate( this.getClass(), 2, message, sqle );
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a key field: %t";
            VerticalEngineLogger.errorCreate( this.getClass(), 3, message, nfe );
        }
        finally
        {
            close( createStmt );
            close( updateStmt );
            close( con );
        }

    }

    private int[] createPageTemplParam( CopyContext copyContext, Document ptpDoc )
        throws VerticalCreateException
    {

        // XML DOM
        Element root = ptpDoc.getDocumentElement();

        // check: does root element exist?
        if ( root == null )
        {
            String message = "Root element does not exist.";
            VerticalEngineLogger.errorCreate( this.getClass(), 0, message, null );
        }

        // check: if root element is not contentrating, throw create exception
        if ( !"pagetemplateparameter".equals( root.getTagName() ) && !"pagetemplateparameters".equals( root.getTagName() ) )
        {
            String message = "Root element is not a pagetemplate or pagetemplates element: %0";
            VerticalEngineLogger.errorCreate( this.getClass(), 1, message, root.getTagName(), null );
        }

        Node[] node;
        if ( "pagetemplateparameters".equals( root.getTagName() ) )
        {
            node = XMLTool.filterNodes( root.getChildNodes(), Node.ELEMENT_NODE );
            if ( node == null || node.length == 0 )
            {
                String message = "No page template parameters to create";
                VerticalEngineLogger.warn( this.getClass(), 2, message, null );
            }
        }
        else
        {
            node = new Node[]{root};
        }
        int[] key = new int[node.length];

        // connection variables
        Connection con = null;
        PreparedStatement preparedStmt = null;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( PTP_CREATE );

            for ( int i = 0; i < node.length; i++ )
            {

                Element elem = (Element) node[i];
                Map<String, Element> subelems = XMLTool.filterElements( elem.getChildNodes() );

                // attribute: key (generated in database)
                key[i] = getNextKey( PTP_TABLE );
                preparedStmt.setInt( 1, key[i] );
                if ( copyContext != null )
                {
                    String keyStr = elem.getAttribute( "key" );
                    if ( keyStr != null && keyStr.length() > 0 )
                    {
                        copyContext.putPageTemplateParameterKey( Integer.parseInt( keyStr ), key[i] );
                    }
                }

                // element: stylesheet key
                String tmp = elem.getAttribute( "pagetemplatekey" );
                preparedStmt.setInt( 2, Integer.parseInt( tmp ) );
                tmp = elem.getAttribute( "multiple" );
                preparedStmt.setInt( 4, Integer.parseInt( tmp ) );
                tmp = elem.getAttribute( "override" );
                preparedStmt.setInt( 6, Integer.parseInt( tmp ) );

                // element: name
                Element subelem = subelems.get( "name" );
                String name = XMLTool.getElementText( subelem );
                preparedStmt.setCharacterStream( 3, new StringReader( name ), name.length() );

                // element: separator
                subelem = subelems.get( "separator" );
                String separator = XMLTool.getElementText( subelem );
                if ( separator == null || separator.length() == 0 )
                {
                    separator = "";
                }
                preparedStmt.setCharacterStream( 5, new StringReader( separator ), separator.length() );

                // element: timestamp (using the database timestamp at creation)
                /* no code */

                preparedStmt.executeUpdate();
            }

            preparedStmt.close();
            preparedStmt = null;
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to create page template parameter because of database error: %t";
            VerticalEngineLogger.errorCreate( this.getClass(), 4, message, sqle );
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a key field: %t";
            VerticalEngineLogger.errorCreate( this.getClass(), 5, message, nfe );
        }
        catch ( VerticalKeyException gke )
        {
            String message = "Failed to generate page template parameter key: %t";
            VerticalEngineLogger.errorCreate( this.getClass(), 6, message, gke );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }

        return key;
    }

    public XMLDocument getPageTemplate( PageTemplateKey pageTemplateKey )
    {
        PageTemplateEntity entity = pageTemplateDao.findByKey( pageTemplateKey.toInt() );
        Document doc = createPageTemplatesDocument( entity != null ? Arrays.asList( entity ) : null );
        return XMLDocumentFactory.create( doc );
    }

    public PageTemplateType getPageTemplateType( PageTemplateKey pageTemplateKey )
    {
        PageTemplateEntity entity = pageTemplateDao.findByKey( pageTemplateKey.toInt() );
        if ( entity == null )
        {
            return null;
        }
        return entity.getType();
    }

    private Document createPageTemplatesDocument( Collection<PageTemplateEntity> pageTemplates )
    {
        Document doc = XMLTool.createDocument( "pagetemplates" );
        if ( pageTemplates == null )
        {
            return doc;
        }

        for ( PageTemplateEntity pageTemplate : pageTemplates )
        {
            Element root = doc.getDocumentElement();
            Document ptdDoc = null;

            org.jdom.Document pageTemplateXmlDataAsJdomDoc = pageTemplate.getXmlData();
            if ( pageTemplateXmlDataAsJdomDoc != null )
            {
                ptdDoc = XMLDocumentFactory.create( pageTemplateXmlDataAsJdomDoc ).getAsDOMDocument();
                Element docElem = XMLTool.getElement( ptdDoc.getDocumentElement(), "document" );
                if ( docElem != null )
                {
                    Node firstChild = docElem.getFirstChild();
                    if ( firstChild == null || firstChild.getNodeType() != Node.CDATA_SECTION_NODE )
                    {
                        docElem.setAttribute( "mode", "xhtml" );
                    }
                }
            }

            Element elem = XMLTool.createElement( doc, root, "pagetemplate" );
            elem.setAttribute( "key", String.valueOf( pageTemplate.getKey() ) );
            elem.setAttribute( "menukey", String.valueOf( pageTemplate.getSite().getKey() ) );

            // sub-elements
            XMLTool.createElement( doc, elem, "name", pageTemplate.getName() );
            XMLTool.createElement( doc, elem, "description", pageTemplate.getDescription() );
            Element tmp = XMLTool.createElement( doc, elem, "stylesheet" );
            tmp.setAttribute( "stylesheetkey", pageTemplate.getStyleKey().toString() );
            tmp.setAttribute( "exists", resourceDao.getResourceFile( pageTemplate.getStyleKey() ) != null ? "true" : "false" );

            // element conobjects for pagetemplate
            Document contentobj = getPageTemplateCO( pageTemplate );
            elem.appendChild( doc.importNode( contentobj.getDocumentElement(), true ) );

            // get page template parameters
            Document ptpDoc = getPageTemplParams( pageTemplate );
            Node ptpNode = doc.importNode( ptpDoc.getDocumentElement(), true );
            elem.appendChild( ptpNode );

            // element timestamp
            XMLTool.createElement( doc, elem, "timestamp", CalendarUtil.formatTimestamp( pageTemplate.getTimestamp(), true ) );

            // element: pagetemplatedata
            if ( ptdDoc != null )
            {
                elem.appendChild( doc.importNode( ptdDoc.getDocumentElement(), true ) );
            }

            // element: css
            ResourceKey cssKey = pageTemplate.getCssKey();
            if ( cssKey != null )
            {
                tmp = XMLTool.createElement( doc, elem, "css" );
                tmp.setAttribute( "stylesheetkey", cssKey.toString() );
                tmp.setAttribute( "exists", resourceDao.getResourceFile( cssKey ) != null ? "true" : "false" );
            }

            // attribute: runAs & defaultRunAsUser
            elem.setAttribute( "runAs", pageTemplate.getRunAs().toString() );
            UserEntity defaultRunAsUser = pageTemplate.getSite().resolveDefaultRunAsUser();
            String defaultRunAsUserName = "NA";
            if ( defaultRunAsUser != null )
            {
                defaultRunAsUserName = defaultRunAsUser.getDisplayName();
            }
            elem.setAttribute( "defaultRunAsUser", defaultRunAsUserName );

            // attribute: type
            elem.setAttribute( "type", pageTemplate.getType().getName() );

            // contenttypes
            int[] contentTypes = getContentTypesByPageTemplate( pageTemplate );
            Document contentTypesDoc = getContentHandler().getContentTypesDocument( contentTypes );
            XMLTool.mergeDocuments( elem, contentTypesDoc, true );
        }

        return doc;
    }

    private Document getPageTemplateCO( PageTemplateEntity pageTemplate )
    {
        Document doc = XMLTool.createDocument();
        Element root = XMLTool.createRootElement( doc, "contentobjects" );

        final List<PageTemplatePortletEntity> objects = pageTemplate.getPortlets();
        for ( PageTemplatePortletEntity pageTemplateObject : objects )
        {
            final PortletEntity portlet = pageTemplateObject.getPortlet();
            final PageTemplateRegionEntity pageTemplateParam = pageTemplateObject.getPageTemplateRegion();

            Element elem = XMLTool.createElement( doc, root, "contentobject" );
            elem.setAttribute( "pagetemplatekey", String.valueOf( pageTemplate.getKey() ) );
            elem.setAttribute( "conobjkey", String.valueOf( portlet.getKey() ) );

            elem.setAttribute( "parameterkey", String.valueOf( pageTemplateParam.getKey() ) );

            // element: contentobjectdata
            Document contentdata = XMLDocumentFactory.create( portlet.getXmlDataAsJDOMDocument() ).getAsDOMDocument();
            Node xmldata_root = doc.importNode( contentdata.getDocumentElement(), true );
            elem.appendChild( xmldata_root );

            // sub-elements
            XMLTool.createElement( doc, elem, "order", String.valueOf( pageTemplateObject.getOrder() ) );
            XMLTool.createElement( doc, elem, "name", portlet.getName() );
            XMLTool.createElement( doc, elem, "separator", pageTemplateParam.getSeparator() );
            elem = XMLTool.createElement( doc, elem, "parametername", pageTemplateParam.getName() );
            elem.setAttribute( "multiple", String.valueOf( pageTemplateParam.isMultiple() ? "1" : "0" ) );
            elem.setAttribute( "override", String.valueOf( pageTemplateParam.isOverride() ? "1" : "0" ) );
        }

        return doc;
    }

    private int[] getPageTemplateKeysByMenu( Connection _con, int menuKey )
    {

        Connection con = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;
        TIntArrayList pageTemplateKeys = new TIntArrayList();

        try
        {
            if ( _con == null )
            {
                con = getConnection();
            }
            else
            {
                con = _con;
            }
            prepStmt = con.prepareStatement( PAT_SELECT_KEY_BY_MENU );
            prepStmt.setInt( 1, menuKey );
            resultSet = prepStmt.executeQuery();
            while ( resultSet.next() )
            {
                pageTemplateKeys.add( resultSet.getInt( "pat_lKey" ) );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get page template keys by menu: %t";
            VerticalEngineLogger.error( this.getClass(), 0, message, sqle );
        }
        finally
        {
            close( resultSet );
            close( prepStmt );
            if ( _con == null )
            {
                close( con );
            }
        }

        return pageTemplateKeys.toArray();
    }

    public boolean hasContentPageTemplates( int menuKey, int contentTypeKey )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tPageTemplate, db.tPageTemplate.pat_lKey, false, (Column) null );
        XDG.appendJoinSQL( sql, db.tPageTemplate.pat_lKey, db.tPageTemplateCty, db.tPageTemplateCty.ptt_pat_lKey );
        XDG.appendWhereSQL( sql, db.tPageTemplate.pat_men_lKey, XDG.OPERATOR_EQUAL, menuKey );
        XDG.appendWhereSQL( sql, db.tPageTemplate.pat_lType, XDG.OPERATOR_EQUAL, PageTemplateType.CONTENT.getKey() );
        XDG.appendWhereSQL( sql, db.tPageTemplateCty.ptt_cty_lKey, XDG.OPERATOR_EQUAL, contentTypeKey );
        return getCommonHandler().hasRows( sql.toString() );
    }

    public Document getPageTemplatesByMenu( int siteKey, int[] excludeTypeKeys )
    {
        List<PageTemplateEntity> list = pageTemplateDao.findBySiteKey( siteKey );
        ArrayList<PageTemplateEntity> filtered = new ArrayList<PageTemplateEntity>();

        HashSet<Integer> excludedTypeSet = null;
        if ( excludeTypeKeys != null && excludeTypeKeys.length > 0 )
        {
            excludedTypeSet = new HashSet<Integer>();
            for ( int key : excludeTypeKeys )
            {
                excludedTypeSet.add( key );
            }
        }

        for ( PageTemplateEntity entity : list )
        {
            if ( ( excludedTypeSet == null ) || !excludedTypeSet.contains( entity.getType().getKey() ) )
            {
                filtered.add( entity );
            }
        }

        return createPageTemplatesDocument( filtered );
    }

    public Document getPageTemplates( PageTemplateType typeKey )
    {
        List<PageTemplateEntity> list = pageTemplateDao.findByTypes( Arrays.asList( typeKey ) );
        return createPageTemplatesDocument( list );
    }

    public Document getPageTemplatesByContentObject( int contentObjectKey )
    {
        Collection<PageTemplateEntity> list = pageTemplateDao.
            findByContentObjectKeys( Arrays.asList( contentObjectKey ) );
        return createPageTemplatesDocument( list );
    }

    public int[] getPageTemplParamKeys( int pageTemplateKey )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        int[] pageTemplParamKey = new int[20];

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( PTP_SELECT_PTP_KEY );
            preparedStmt.setInt( 1, pageTemplateKey );
            resultSet = preparedStmt.executeQuery();

            int size = 0;
            while ( resultSet.next() )
            {
                if ( size == pageTemplParamKey.length )
                {
                    int temp[] = new int[pageTemplParamKey.length * 2];
                    System.arraycopy( pageTemplParamKey, 0, temp, 0, pageTemplParamKey.length );
                    pageTemplParamKey = temp;
                }
                pageTemplParamKey[size++] = resultSet.getInt( "ptp_lKey" );
            }

            if ( size == 0 )
            {
                pageTemplParamKey = new int[0];
            }
            else if ( size < pageTemplParamKey.length )
            {
                int temp[] = new int[size];
                System.arraycopy( pageTemplParamKey, 0, temp, 0, size );
                pageTemplParamKey = temp;
            }

            resultSet.close();
            preparedStmt.close();
            preparedStmt = null;
        }
        catch ( SQLException se )
        {
            System.err.println( "[Error] Could not get page template parameter keys from the database:\n" );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return pageTemplParamKey;
    }

    public String getPageTemplParams( int pageTemplateKey )
    {
        PageTemplateEntity entity = pageTemplateDao.findByKey( pageTemplateKey );
        return XMLTool.documentToString( getPageTemplParams( entity ) );
    }

    private Document getPageTemplParams( PageTemplateEntity pageTemplate )
    {
        Document doc = XMLTool.createDocument();
        Element root = XMLTool.createRootElement( doc, "pagetemplateparameters" );

        if ( pageTemplate != null )
        {
            for ( PageTemplateRegionEntity entity : pageTemplate.getPageTemplateRegions() )
            {
                Element elem = XMLTool.createElement( doc, root, "pagetemplateparameter" );
                elem.setAttribute( "key", String.valueOf( entity.getKey() ) );
                elem.setAttribute( "pagetemplatekey", String.valueOf( entity.getPageTemplate().getKey() ) );
                elem.setAttribute( "multiple", entity.isMultiple() ? "1" : "0" );
                elem.setAttribute( "override", entity.isOverride() ? "1" : "0" );

                // sub-elements
                XMLTool.createElement( doc, elem, "name", entity.getName() );
                XMLTool.createElement( doc, elem, "separator", entity.getSeparator() );
            }
        }

        return doc;
    }

    public void removePageTemplate( int pageTemplateKey )
        throws VerticalRemoveException
    {
        getCommonHandler().cascadeDelete( db.tPageTemplate, pageTemplateKey );

        removePageTemplateCOs( pageTemplateKey, null );
        removePageTemplParams( pageTemplateKey );

        // Can't use XDG since we need custom exception handling
        //StringBuffer sql = XDG.generateRemoveSQL(db.tPageTemplate, db.tPageTemplate.pat_lKey);
        //getCommonHandler().executeSQL(sql.toString(), pageTemplateKey);
        Connection con = null;
        PreparedStatement preparedStmt = null;
        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( PAT_REMOVE );
            preparedStmt.setInt( 1, pageTemplateKey );
            preparedStmt.executeUpdate();
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to remove page template because of database error: %t";
            VerticalEngineLogger.errorRemove( this.getClass(), 0, message, sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }


    private void removePageTemplates( int[] pageTemplateKeys )
        throws VerticalRemoveException
    {
        for ( int pageTemplateKey : pageTemplateKeys )
        {
            removePageTemplate( pageTemplateKey );
        }
    }

    public void removePageTemplatesByMenu( int menuKey )
        throws VerticalRemoveException
    {
        int[] pageTemplateKeys = getPageTemplateKeysByMenu( null, menuKey );
        if ( pageTemplateKeys != null && pageTemplateKeys.length > 0 )
        {
            removePageTemplates( pageTemplateKeys );
        }
    }

    private void removePageTemplateCOs( int pageTemplateKey, int[] pageTemplParamKeys )
        throws VerticalRemoveException
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;

        try
        {
            StringBuffer sql = new StringBuffer( PTC_REMOVE_PAT );
            if ( pageTemplParamKeys != null && pageTemplParamKeys.length > 0 )
            {
                sql.append( " AND ptc_ptp_lkey = ?" );
            }
            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, pageTemplateKey );
            if ( pageTemplParamKeys != null && pageTemplParamKeys.length > 0 )
            {
                for ( int pageTemplParamKey : pageTemplParamKeys )
                {
                    preparedStmt.setInt( 2, pageTemplParamKey );
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
            String message = "Failed to remove link from page template to content object because of database error: %t";
            VerticalEngineLogger.errorRemove( this.getClass(), 0, message, sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    /**
     * Remove pagetemplate parameters.
     *
     * @param pageTemplParamKey The keys of the pagetemplate parameters that are to be removed.
     * @throws VerticalRemoveException The exception description.
     */
    private void removePageTemplParams( int[] pageTemplParamKey )
        throws VerticalRemoveException
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        String sql = PTP_REMOVE;

        try
        {
            con = getConnection();

            preparedStmt = con.prepareStatement( sql );
            for ( int aPageTemplParamKey : pageTemplParamKey )
            {
                preparedStmt.setInt( 1, aPageTemplParamKey );
                preparedStmt.executeUpdate();
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to remove page template parameters because of database error: %t";
            VerticalEngineLogger.errorRemove( this.getClass(), 0, message, sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    public void removePageTemplParams( int pageTemplateKey )
        throws VerticalRemoveException
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( PTP_PAT_REMOVE );
            preparedStmt.setInt( 1, pageTemplateKey );
            preparedStmt.executeUpdate();
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to remove page template parameters because of database error: %t";
            VerticalEngineLogger.errorRemove( this.getClass(), 0, message, sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    public void updatePageTemplate( String xmlData )
        throws VerticalUpdateException
    {

        Document doc = XMLTool.domparse( xmlData, "pagetemplate" );
        updatePageTemplate( doc );
    }

    private int[] getContentTypesByPageTemplate( PageTemplateEntity entity )
    {
        if ( entity != null )
        {
            Set<ContentTypeEntity> list = entity.getContentTypes();
            int[] array = new int[list.size()];
            int pos = 0;

            for ( ContentTypeEntity value : list )
            {
                array[pos] = value.getKey();
                pos++;
            }

            return array;
        }
        else
        {
            return new int[0];
        }
    }

    public void setPageTemplateContentTypes( int pageTemplateKey, int[] contentTypeKeys )
    {
        // first delete the contenttypes for this page template
        StringBuffer sql = XDG.generateRemoveSQL( db.tPageTemplateCty, db.tPageTemplateCty.ptt_pat_lKey );
        getCommonHandler().executeSQL( sql.toString(), pageTemplateKey );

        // now insert the new values
        if ( contentTypeKeys != null && contentTypeKeys.length > 0 )
        {
            sql = XDG.generateInsertSQL( db.tPageTemplateCty );

            for ( int contentTypeKey : contentTypeKeys )
            {
                getCommonHandler().executeSQL( sql.toString(), new int[]{pageTemplateKey, contentTypeKey} );
            }
        }

        // do the same for all menuitems using this page template
        int[] menuItemKeys = getMenuItemKeysByPageTemplate( pageTemplateKey );
        for ( int menuItemKey : menuItemKeys )
        {
            getMenuHandler().setMenuItemContentTypes( menuItemKey, contentTypeKeys );
        }
    }

    public int[] getMenuItemKeysByPageTemplate( int pageTemplateKey )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tMenuItem, db.tMenuItem.mei_lKey, false, (Column) null );
        XDG.appendJoinSQL( sql, db.tMenuItem.mei_pag_lKey );
        XDG.appendWhereSQL( sql, db.tPage.pag_pat_lKey, XDG.OPERATOR_EQUAL, pageTemplateKey );
        return getCommonHandler().getIntArray( sql.toString() );
    }

    /**
     * Update the pagetemplate in the database.
     *
     * @param doc The pagetemplate XML document.
     * @throws VerticalUpdateException Indicates that the update was not successfull.
     */
    private void updatePageTemplate( Document doc )
        throws VerticalUpdateException
    {

        Element docElem = doc.getDocumentElement();
        Element[] pagetemplateElems;
        if ( "pagetemplate".equals( docElem.getTagName() ) )
        {
            pagetemplateElems = new Element[]{docElem};
        }
        else
        {
            pagetemplateElems = XMLTool.getElements( doc.getDocumentElement() );
        }

        Connection con = null;
        PreparedStatement preparedStmt = null;
        int pageTemplateKey;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( PAT_UPDATE );

            for ( Element root : pagetemplateElems )
            {
                Map<String, Element> subelems = XMLTool.filterElements( root.getChildNodes() );

                // attribute: key
                String tmp = root.getAttribute( "key" );
                pageTemplateKey = Integer.parseInt( tmp );
                preparedStmt.setInt( 9, pageTemplateKey );

                // attribute: type
                PageTemplateType pageTemplateType = PageTemplateType.valueOf( root.getAttribute( "type" ).toUpperCase() );
                preparedStmt.setInt( 7, pageTemplateType.getKey() );

                RunAsType runAs = RunAsType.INHERIT;
                String runAsStr = root.getAttribute( "runAs" );
                if ( StringUtils.isNotEmpty( runAsStr ) )
                {
                    runAs = RunAsType.valueOf( runAsStr );
                }
                preparedStmt.setInt( 8, runAs.getKey() );

                // attribute: menukey
                tmp = root.getAttribute( "menukey" );
                int menuKey = Integer.parseInt( tmp );
                preparedStmt.setInt( 2, menuKey );

                // element: stylesheet
                Element stylesheet = subelems.get( "stylesheet" );
                String styleSheetKey = stylesheet.getAttribute( "stylesheetkey" );
                preparedStmt.setString( 1, styleSheetKey );

                // element: name
                Element subelem = subelems.get( "name" );
                String name = XMLTool.getElementText( subelem );
                preparedStmt.setString( 3, name );

                subelem = subelems.get( "description" );
                if ( subelem != null )
                {
                    String description = XMLTool.getElementText( subelem );
                    if ( description != null )
                    {
                        preparedStmt.setString( 4, description );
                    }
                    else
                    {
                        preparedStmt.setNull( 4, Types.VARCHAR );
                    }
                }
                else
                {
                    preparedStmt.setNull( 4, Types.VARCHAR );
                }

                // element: timestamp (using the database timestamp at creation)
                /* no code */

                // element: contenttypes
                subelem = subelems.get( "contenttypes" );
                Element[] ctyElems = XMLTool.getElements( subelem );
                int[] ctys = new int[ctyElems.length];
                for ( int j = 0; j < ctyElems.length; j++ )
                {
                    ctys[j] = Integer.parseInt( ctyElems[j].getAttribute( "key" ) );
                }
                setPageTemplateContentTypes( pageTemplateKey, ctys );

                // element: datasources
                subelem = subelems.get( "pagetemplatedata" );
                Document ptdDoc = XMLTool.createDocument();
                ptdDoc.appendChild( ptdDoc.importNode( subelem, true ) );
                byte[] ptdBytes = XMLTool.documentToBytes( ptdDoc, "UTF-8" );
                ByteArrayInputStream byteStream = new ByteArrayInputStream( ptdBytes );
                preparedStmt.setBinaryStream( 5, byteStream, ptdBytes.length );

                // element: CSS
                subelem = subelems.get( "css" );
                if ( subelem != null )
                {
                    String foo = subelem.getAttribute( "stylesheetkey" );
                    preparedStmt.setString( 6, foo );
                }
                else
                {
                    preparedStmt.setNull( 6, Types.VARCHAR );
                }

                int result = preparedStmt.executeUpdate();
                if ( result <= 0 )
                {
                    String message = "Failed to update page template. No page template updated.";
                    VerticalEngineLogger.errorUpdate( this.getClass(), 0, message, null );
                }

                // If page template is of type "section", we need to create sections for menuitems
                // that does not have one
                if ( pageTemplateType == PageTemplateType.SECTIONPAGE )
                {
                    int[] menuItemKeys = getMenuItemKeysByPageTemplate( pageTemplateKey );
                    for ( int menuItemKey : menuItemKeys )
                    {
                        MenuItemKey sectionKey = getSectionHandler().getSectionKeyByMenuItem( new MenuItemKey( menuItemKey ) );
                        if ( sectionKey == null )
                        {
                            getSectionHandler().createSection( menuItemKey, true, ctys );
                        }
                    }
                }

                Element contentobjects = XMLTool.getElement( root, "contentobjects" );
                Element ptp = XMLTool.getElement( root, "pagetemplateparameters" );

                if ( ptp != null )
                {
                    int[] paramKeys = new int[0];

                    // update all ptp entries for page
                    try
                    {
                        int[] oldPTPKey = getPageTemplParamKeys( pageTemplateKey );
                        Node[] ptpNode = XMLTool.filterNodes( ptp.getChildNodes(), Node.ELEMENT_NODE );
                        int[] updatedPTPKey = new int[ptpNode.length];
                        int updatedPTPs = 0, newPTPs = 0;
                        Document updatedPTPDoc = XMLTool.createDocument( "pagetemplateparameters" );
                        Element updatedPTP = updatedPTPDoc.getDocumentElement();
                        Document newPTPDoc = XMLTool.createDocument( "pagetemplateparameters" );
                        Element newPTP = newPTPDoc.getDocumentElement();

                        for ( Node aPtpNode : ptpNode )
                        {
                            ptp = (Element) aPtpNode;
                            String keyStr = ptp.getAttribute( "key" );
                            int key;
                            if ( keyStr != null && keyStr.length() > 0 )
                            {
                                key = Integer.parseInt( keyStr );
                            }
                            else
                            {
                                key = -1;
                            }
                            if ( key >= 0 )
                            {
                                updatedPTP.appendChild( updatedPTPDoc.importNode( ptp, true ) );
                                updatedPTPKey[updatedPTPs++] = key;
                            }
                            else
                            {
                                newPTP.appendChild( newPTPDoc.importNode( ptp, true ) );
                                newPTPs++;
                            }
                        }

                        // remove old
                        if ( updatedPTPs == 0 )
                        {
                            PageHandler pageHandler = getPageHandler();
                            int[] pageKeys = pageHandler.getPageKeysByPageTemplateKey( pageTemplateKey );
                            for ( int pageKey : pageKeys )
                            {
                                pageHandler.removePageContentObjects( pageKey, null );
                            }
                            removePageTemplateCOs( pageTemplateKey, null );
                            removePageTemplParams( pageTemplateKey );
                        }
                        else if ( updatedPTPs < oldPTPKey.length )
                        {
                            int temp1[] = new int[updatedPTPs];
                            System.arraycopy( updatedPTPKey, 0, temp1, 0, updatedPTPs );
                            updatedPTPKey = temp1;

                            Arrays.sort( oldPTPKey );
                            oldPTPKey = ArrayUtil.removeDuplicates( oldPTPKey );
                            Arrays.sort( updatedPTPKey );
                            updatedPTPKey = ArrayUtil.removeDuplicates( updatedPTPKey );
                            int temp2[][] = ArrayUtil.diff( oldPTPKey, updatedPTPKey );

                            PageHandler pageHandler = getPageHandler();
                            int[] contentObjectKeys = pageHandler.getContentObjectKeys( temp2[0] );
                            int[] pageKeys = pageHandler.getPageKeysByPageTemplateKey( pageTemplateKey );
                            if ( contentObjectKeys != null && contentObjectKeys.length > 0 )
                            {
                                for ( int pageKey : pageKeys )
                                {
                                    pageHandler.removePageContentObjects( pageKey, contentObjectKeys );
                                }
                            }
                            removePageTemplateCOs( pageTemplateKey, temp2[0] );
                            removePageTemplParams( temp2[0] );
                        }

                        updatePageTemplParam( updatedPTPDoc );
                        if ( newPTPs > 0 )
                        {
                            paramKeys = createPageTemplParam( null, newPTPDoc );
                        }
                    }
                    catch ( VerticalRemoveException vre )
                    {
                        String message = "Failed to remove old page template parameters: %t";
                        VerticalEngineLogger.errorUpdate( this.getClass(), 3, message, vre );
                    }
                    catch ( VerticalCreateException vce )
                    {
                        String message = "Failed to create new page template parameters: %t";
                        VerticalEngineLogger.errorUpdate( this.getClass(), 4, message, vce );
                    }

                    if ( contentobjects != null )
                    {
                        // update all pageconobj entries for page
                        try
                        {
                            Document cobsDoc = XMLTool.createDocument();
                            cobsDoc.appendChild( cobsDoc.importNode( contentobjects, true ) );
                            updatePageTemplateCOs( cobsDoc, pageTemplateKey, paramKeys );
                        }
                        catch ( VerticalCreateException vce )
                        {
                            String message = "Failed to create new link from page template to content objects: %t";
                            VerticalEngineLogger.errorUpdate( this.getClass(), 2, message, vce );
                        }
                    }
                }
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to update page template because of database error: %t";
            VerticalEngineLogger.errorUpdate( this.getClass(), 5, message, sqle );
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a key field: %t";
            VerticalEngineLogger.errorUpdate( this.getClass(), 6, message, nfe );
        }
        catch ( VerticalCreateException vce )
        {
            String message = "Failed to create sections for page template: %t";
            VerticalEngineLogger.errorUpdate( this.getClass(), 6, message, vce );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    public void updatePageTemplParam( Document ptpDoc )
        throws VerticalUpdateException
    {

        Element root = ptpDoc.getDocumentElement();
        String tmp;

        // check: does root element exist?
        if ( root == null )
        {
            String message = "Root element does not exist";
            VerticalEngineLogger.errorUpdate( this.getClass(), 0, message, null );
        }

        // check: if root element is not contentrating, throw create exception
        if ( !"pagetemplateparameter".equals( root.getTagName() ) && !"pagetemplateparameters".equals( root.getTagName() ) )
        {
            String message = "Root element is not the \"pagetemplateparameter\" or \"pagetemplateparameters\" element: %0";
            VerticalEngineLogger.errorUpdate( this.getClass(), 1, message, root.getTagName(), null );
        }

        Node[] node;
        if ( "pagetemplateparameters".equals( root.getTagName() ) )
        {
            node = XMLTool.filterNodes( root.getChildNodes(), Node.ELEMENT_NODE );
            if ( node == null || node.length == 0 )
            {
                return;
                //String message = "No page template parameters to create.";
                //VerticalEngineLogger.warn(2, message, null);
            }
        }
        else
        {
            node = new Node[]{root};
        }

        // connection variables
        Connection con = null;
        PreparedStatement preparedStmt = null;
        //int result = -1;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( PTP_UPDATE );

            for ( Node aNode : node )
            {

                Element elem = (Element) aNode;
                Map<String, Element> subelems = XMLTool.filterElements( elem.getChildNodes() );

                // attribute: key
                tmp = elem.getAttribute( "key" );
                int pageTemplParamKey = Integer.parseInt( tmp );
                preparedStmt.setInt( 6, pageTemplParamKey );

                tmp = elem.getAttribute( "pagetemplatekey" );
                preparedStmt.setInt( 1, Integer.parseInt( tmp ) );

                tmp = elem.getAttribute( "multiple" );
                preparedStmt.setInt( 3, Integer.parseInt( tmp ) );

                tmp = elem.getAttribute( "override" );
                preparedStmt.setInt( 5, Integer.parseInt( tmp ) );

                // element: name
                Element subelem = subelems.get( "name" );
                String name = XMLTool.getElementText( subelem );
                preparedStmt.setCharacterStream( 2, new StringReader( name ), name.length() );

                subelem = subelems.get( "separator" );
                String separator = XMLTool.getElementText( subelem );
                if ( separator == null || separator.length() == 0 )
                {
                    separator = "";
                }
                preparedStmt.setCharacterStream( 4, new StringReader( separator ), separator.length() );

                int result = preparedStmt.executeUpdate();
                if ( result <= 0 )
                {
                    String message = "Failed to update page template parameters. None updated.";
                    VerticalEngineLogger.errorUpdate( this.getClass(), 3, message, null );
                }
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to update page template parameters because of database error: %t";
            VerticalEngineLogger.errorUpdate( this.getClass(), 4, message, sqle );
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a key field: %t";
            VerticalEngineLogger.errorUpdate( this.getClass(), 5, message, nfe );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }

    }

    public void copyPageTemplates( int oldMenuKey, CopyContext copyContext )
        throws VerticalCopyException
    {
        //int oldSiteKey = copyContext.getOldSiteKey();
        //int newSiteKey = copyContext.getNewSiteKey();
        int newMenuKey = copyContext.getMenuKey( oldMenuKey );

        Document doc = getPageTemplatesByMenu( oldMenuKey, null );

        Element[] pageTemplateElems = XMLTool.getElements( doc.getDocumentElement() );

        for ( Element pageTemplateElem : pageTemplateElems )
        {
            pageTemplateElem.setAttribute( "menukey", Integer.toString( newMenuKey ) );

            Element contentobjectsElem = XMLTool.getElement( pageTemplateElem, "contentobjects" );
            Element[] contentobjectElems = XMLTool.getElements( contentobjectsElem );
            for ( Element contentobjectElem : contentobjectElems )
            {
                String oldContentObjectKey = contentobjectElem.getAttribute( "conobjkey" );
                int newContentObjectKey = copyContext.getContentObjectKey( Integer.parseInt( oldContentObjectKey ) );
                contentobjectElem.setAttribute( "conobjkey", String.valueOf( newContentObjectKey ) );
            }

            // stylesheet (xsl)
            Element stylesheetElem = XMLTool.getElement( pageTemplateElem, "stylesheet" );
            String oldStyleSheetKey = stylesheetElem.getAttribute( "stylesheetkey" );
            //int newStyleSheetKey = copyContext.getResourceKey(Integer.parseInt(oldStyleSheetKey));
            if ( oldStyleSheetKey != null && oldStyleSheetKey.length() > 0 )
            {
                stylesheetElem.setAttribute( "stylesheetkey", oldStyleSheetKey );
            }

            // stylesheet (css)
            stylesheetElem = XMLTool.getElement( pageTemplateElem, "css" );
            if ( stylesheetElem != null )
            {
                oldStyleSheetKey = stylesheetElem.getAttribute( "stylesheetkey" );
                if ( oldStyleSheetKey != null && oldStyleSheetKey.length() > 0 )
                {
                    stylesheetElem.setAttribute( "stylesheetkey", oldStyleSheetKey );
                }
            }
        }

        try
        {
            createPageTemplate( copyContext, doc, false );
        }
        catch ( VerticalCreateException vce )
        {
            String message = "Failed to copy page templates: %t";
            VerticalEngineLogger.errorCopy( this.getClass(), 0, message, vce );
        }

        //VerticalEngineLogger.debug(1, map.toString(), null);
    }

    public int copyPageTemplate( User user, PageTemplateKey pageTemplateKey )
        throws VerticalCopyException
    {

        Document doc = getPageTemplate( pageTemplateKey ).getAsDOMDocument();
        Element root = doc.getDocumentElement();
        Element pagetemplateElem = XMLTool.getFirstElement( root );
        int newPageTemplateKey = -1;
        if ( pagetemplateElem != null )
        {
            // rename copy
            Element nameElem = XMLTool.getElement( pagetemplateElem, "name" );
            Text nameNode = (Text) nameElem.getFirstChild();
            nameNode.setData( nameNode.getData() + " (copy)" );

            // remove old parameter keys and save position
            Map<String, String> paramKeyMap = new HashMap<String, String>();
            Element[] paramElems = XMLTool.getElements( XMLTool.getElement( pagetemplateElem, "pagetemplateparameters" ) );
            for ( int i = 0; i < paramElems.length; i++ )
            {
                String key = paramElems[i].getAttribute( "key" );
                paramKeyMap.put( key, "_" + i );
                paramElems[i].removeAttribute( "key" );
            }

            // replace old parameter keys with saved position
            Element[] contentobjectElems = XMLTool.getElements( XMLTool.getElement( pagetemplateElem, "contentobjects" ) );
            for ( Element contentobjectElem : contentobjectElems )
            {
                String key = contentobjectElem.getAttribute( "parameterkey" );
                contentobjectElem.setAttribute( "parameterkey", paramKeyMap.get( key ) );
            }

            try
            {
                int[] keys = createPageTemplate( null, doc, false );
                if ( keys != null && keys.length == 1 )
                {
                    newPageTemplateKey = keys[0];
                }
            }
            catch ( VerticalCreateException vce )
            {
                String message = "Failed to create copy of framework: %t";
                VerticalEngineLogger.errorCopy( this.getClass(), 0, message, vce );
            }
        }
        return newPageTemplateKey;
    }

    public void copyPageTemplatesPostOp( int oldMenuKey, CopyContext copyContext )
        throws VerticalCopyException
    {

        int newMenuKey = copyContext.getMenuKey( oldMenuKey );

        Document doc = getPageTemplatesByMenu( newMenuKey, null );
        Element[] pageTemplateElems = XMLTool.getElements( doc.getDocumentElement() );

        try
        {
            for ( Element pageTemplateElem : pageTemplateElems )
            {

                // datasource
                NodeList parameterList = XMLTool.selectNodes( pageTemplateElem, "datasources/datasource/parameters/parameter" );
                for ( int j = 0; j < parameterList.getLength(); j++ )
                {
                    Element parameterElem = (Element) parameterList.item( j );
                    String name = parameterElem.getAttribute( "name" );
                    if ( "cat".equalsIgnoreCase( name ) )
                    {
                        Text text = (Text) parameterElem.getFirstChild();
                        if ( text != null )
                        {
                            String oldCategoryKey = text.getData();
                            if ( oldCategoryKey != null && oldCategoryKey.length() > 0 )
                            {
                                int newCategoryKey = copyContext.getCategoryKey( Integer.parseInt( oldCategoryKey ) );
                                if ( newCategoryKey >= 0 )
                                {
                                    text.setData( String.valueOf( newCategoryKey ) );
                                }
                            }
                        }
                    }
                    else if ( "menu".equalsIgnoreCase( name ) )
                    {
                        Text text = (Text) parameterElem.getFirstChild();
                        if ( text != null )
                        {
                            String oldKey = text.getData();
                            if ( oldKey != null && oldKey.length() > 0 )
                            {
                                int newKey = copyContext.getMenuKey( Integer.parseInt( oldKey ) );
                                if ( newKey >= 0 )
                                {
                                    text.setData( String.valueOf( newKey ) );
                                }
                            }
                        }
                    }
                }
            }

            updatePageTemplate( doc );
        }
        catch ( VerticalUpdateException vue )
        {
            String message = "Failed to copy page templates (post operation): %t";
            VerticalEngineLogger.errorCopy( this.getClass(), 0, message, vue );
        }
    }
}

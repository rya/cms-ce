/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.SectionCriteria;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.dbmodel.SectionView;
import com.enonic.vertical.engine.processors.AttributeElementProcessor;
import com.enonic.vertical.engine.processors.ElementProcessor;
import com.enonic.vertical.event.ContentHandlerListener;

import com.enonic.cms.framework.util.TIntArrayList;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;

public class SectionHandler
    extends BaseHandler
    implements ContentHandlerListener
{
    private static final Logger LOG = LoggerFactory.getLogger( SectionHandler.class.getName() );

    private class FilteredAttributeElementProcessor
        extends AttributeElementProcessor
    {

        private int[] sectionKeys;

        private FilteredAttributeElementProcessor( String attributeName, String attributeValue, int[] sectionKeys )
        {
            super( attributeName, attributeValue );
            this.sectionKeys = sectionKeys;
            Arrays.sort( this.sectionKeys );
        }

        public void process( Element elem )
        {
            // sections in the array are processes by the AttributeElementProcessor
            if ( sectionKeys.length > 0 )
            {
                int sectionKey = Integer.parseInt( elem.getAttribute( "key" ) );
                if ( Arrays.binarySearch( sectionKeys, sectionKey ) >= 0 )
                {
                    super.process( elem );
                }
            }
        }

    }

    private class SectionProcessor
        implements ElementProcessor
    {

        public void process( Element elem )
        {
            int key = Integer.parseInt( elem.getAttribute( "key" ) );

            Document contentTypesDoc = getContentTypesDocumentForSection( key );
            XMLTool.mergeDocuments( elem, contentTypesDoc, true );
        }
    }

    public Document getContentTypesDocumentForSection( int menuItemSectionKey )
    {
        int[] contentTypeKeys = getContentTypesForSection( menuItemSectionKey );
        return getContentHandler().getContentTypesDocument( contentTypeKeys );
    }

    private class CollectionProcessor
        implements ElementProcessor
    {

        private Map<String, Element> elemMap;

        private List<Element> elemList;

        private Element lastElem;

        public CollectionProcessor( Map<String, Element> elemMap, List<Element> elemList )
        {
            this.elemMap = elemMap;
            this.elemList = elemList;
        }

        public void process( Element elem )
        {
            if ( elemMap != null )
            {
                elemMap.put( elem.getTagName() + "_" + elem.getAttribute( "key" ), elem );
            }
            if ( elemList != null )
            {
                elemList.add( elem );
            }
            lastElem = elem;
        }
    }

    private class ChildCountProcessor
        implements ElementProcessor
    {

        public void process( Element elem )
        {
            SectionView sectionView = SectionView.getInstance();
            int key = Integer.parseInt( elem.getAttribute( "key" ) );
            StringBuffer sql = XDG.generateSelectSQL( sectionView, sectionView.mei_lKey.getCountColumn(), false, sectionView.mei_lParent );

            int childCount = getCommonHandler().getInt( sql.toString(), key );
            elem.setAttribute( "childcount", Integer.toString( childCount ) );
        }
    }

    private int[] getContentTypesForSection( int menuItemSectionKey )
    {
        StringBuffer sql =
            XDG.generateSelectSQL( db.tSecConTypeFilter2, db.tSecConTypeFilter2.sctf_cty_lKey, false, db.tSecConTypeFilter2.sctf_mei_lKey );
        return getCommonHandler().getIntArray( sql.toString(), new Object[]{menuItemSectionKey} );
    }

    public MenuItemKey getMenuItemKeyBySection( int sectionKey )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tMenuItem, db.tMenuItem.mei_lKey, false,
                                                  new Column[]{db.tMenuItem.mei_lKey, db.tMenuItem.mei_bSection} );
        return new MenuItemKey( getCommonHandler().getInt( sql.toString(), new Object[]{sectionKey, 1} ) );
    }

    public MenuItemKey getSectionKeyByMenuItem( MenuItemKey menuItemKey )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tMenuItem, db.tMenuItem.mei_lKey, false,
                                                  new Column[]{db.tMenuItem.mei_lKey, db.tMenuItem.mei_bSection} );
        if ( getCommonHandler().hasRows( sql.toString(), new int[]{menuItemKey.toInt(), 1} ) )
        {
            return menuItemKey;
        }
        else
        {
            return null;
        }
    }

    public Document getSectionByMenuItem( int menuItemKey )
    {
        MenuItemEntity entity = menuItemDao.findByKey( menuItemKey );
        Document doc = XMLTool.createDocument( "section" );

        if ( entity != null && entity.isSection() )
        {
            Element elem = doc.getDocumentElement();
            elem.setAttribute( "key", String.valueOf( entity.getKey() ) );
            elem.setAttribute( "menukey", String.valueOf( entity.getSite().getKey() ) );
            elem.setAttribute( "menuitemkey", String.valueOf( entity.getKey() ) );
            elem.setAttribute( "ordered", String.valueOf( entity.isOrderedSection() ) );
        }

        Document contentTypesDoc = getContentTypesDocumentForSection( menuItemKey );
        XMLTool.mergeDocuments( doc.getDocumentElement(), contentTypesDoc, true );

        return doc;
    }

    public Document getSections( User user, SectionCriteria criteria )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Document doc = null;
        SectionView sectionView = SectionView.getInstance();

        try
        {
            con = getConnection();
            StringBuffer sql;
            SiteKey[] siteKeys = criteria.getSiteKeys();
            MenuItemKey[] menuItemKeys = criteria.getMenuItemKeys();
            int sectionKey = criteria.getSectionKey();
            int superSectionKey = criteria.getSuperSectionKey();
            boolean includeSection = criteria.isIncludeSection();
            int level = criteria.getLevel();
            int[] sectionKeys = criteria.getSectionKeys();
            int contentKey = criteria.getContentKey();
            int contentKeyExcludeFilter = criteria.getContentKeyExcludeFilter();
            int contentTypeKeyFilter = criteria.getContentTypeKeyFilter();
            boolean treeStructure = criteria.isTreeStructure();
            boolean markContentFilteredSections = criteria.isMarkContentFilteredSections();
            boolean includeAll = criteria.isIncludeAll();

            // Generate SQL
            if ( siteKeys != null )
            {
                if ( siteKeys.length == 0 )
                {
                    return XMLTool.createDocument( "sections" );
                }
                sql = XDG.generateSelectWhereInSQL( sectionView, (Column[]) null, false, sectionView.mei_men_lKey, siteKeys.length );
            }
            else if ( menuItemKeys != null )
            {
                if ( menuItemKeys.length == 0 )
                {
                    return XMLTool.createDocument( "sections" );
                }
                sql = XDG.generateSelectWhereInSQL( sectionView, (Column[]) null, false, sectionView.mei_lKey, menuItemKeys.length );
            }
            else if ( sectionKey != -1 )
            {
                sql = XDG.generateSelectSQL( sectionView, sectionView.mei_lKey );
            }
            else if ( sectionKeys != null )
            {
                if ( sectionKeys.length == 0 )
                {
                    return XMLTool.createDocument( "sections" );
                }
                sql = XDG.generateSelectWhereInSQL( sectionView, (Column[]) null, false, sectionView.mei_lKey, sectionKeys.length );
            }
            else if ( superSectionKey != -1 )
            {
                if ( includeSection )
                {
                    sql = XDG.generateSelectSQL( sectionView, sectionView.mei_lKey );
                    if ( level > 0 )
                    {
                        level += 1;
                    }
                }
                else
                {
                    sql = XDG.generateSelectSQL( sectionView, sectionView.mei_lParent );
                }
            }
            else if ( contentKey != -1 )
            {
                sql = XDG.generateSelectSQL( sectionView );
                sql.append( " WHERE mei_lKey IN (SELECT sco_mei_lKey FROM tSectionContent2 WHERE sco_con_lKey = ?)" );
            }
            else
            {
                sql = XDG.generateSelectSQL( sectionView );
            }

            if ( contentKeyExcludeFilter > -1 && !markContentFilteredSections )
            {
                if ( sql.toString().toLowerCase().indexOf( "where" ) < 0 )
                {
                    sql.append( " WHERE" );
                }
                else
                {
                    sql.append( " AND" );
                }
                sql.append( " mei_lKey NOT IN (SELECT sco_mei_lKey FROM tSectionContent2 WHERE sco_con_lKey = ?)" );
            }

            if ( contentTypeKeyFilter > -1 )
            {
                if ( sql.toString().toLowerCase().indexOf( "where" ) < 0 )
                {
                    sql.append( " WHERE" );
                }
                else
                {
                    sql.append( " AND" );
                }
                sql.append( " (" );
                sql.append( " mei_lKey IN (SELECT sctf_mei_lKey FROM tSecConTypeFilter2 WHERE sctf_cty_lkey = ?)" );
                if ( criteria.isIncludeSectionsWithoutContentTypeEvenWhenFilterIsSet() )
                {
                    sql.append( " OR NOT EXISTS (SELECT sctf_mei_lKey FROM tSecConTypeFilter2 WHERE sctf_mei_lkey = mei_lKey)" );
                }
                sql.append( " )" );
            }

            SecurityHandler securityHandler = getSecurityHandler();
            if ( !includeAll )
            {
                securityHandler.appendSectionSQL( user, sql, criteria );
            }

            sql.append( " ORDER BY mei_sName" );

            preparedStmt = con.prepareStatement( sql.toString() );

            int index = 1;
            // Set parameters
            if ( siteKeys != null )
            {
                for ( SiteKey siteKey : siteKeys )
                {
                    preparedStmt.setInt( index++, siteKey.toInt() );
                }
            }
            else if ( menuItemKeys != null )
            {
                for ( MenuItemKey menuItemKey : menuItemKeys )
                {
                    preparedStmt.setInt( index++, menuItemKey.toInt() );
                }
            }
            else if ( sectionKey != -1 )
            {
                preparedStmt.setInt( index++, sectionKey );
            }
            else if ( sectionKeys != null )
            {
                for ( int loopSectionKey : sectionKeys )
                {
                    preparedStmt.setInt( index++, loopSectionKey );
                }
            }
            else if ( superSectionKey != -1 )
            {
                preparedStmt.setInt( index++, superSectionKey );
            }
            else if ( contentKey != -1 )
            {
                preparedStmt.setInt( index++, contentKey );
            }

            if ( contentKeyExcludeFilter > -1 && !markContentFilteredSections )
            {
                preparedStmt.setInt( index++, contentKeyExcludeFilter );
            }
            if ( contentTypeKeyFilter > -1 )
            {
                preparedStmt.setInt( index, contentTypeKeyFilter );
            }

            resultSet = preparedStmt.executeQuery();

            ElementProcessor childCountProcessor = null;
            if ( criteria.getIncludeChildCount() )
            {
                childCountProcessor = new ChildCountProcessor();
            }

            Map<String, Element> elemMap = new HashMap<String, Element>();
            CollectionProcessor collectionProcessor = new CollectionProcessor( elemMap, null );
            ElementProcessor[] processors;
            ElementProcessor aep = null;
            if ( contentKeyExcludeFilter >= 0 )
            {
                if ( markContentFilteredSections )
                {
                    int[] keys = getSectionKeysByContent( contentKeyExcludeFilter, -1 );
                    aep = new FilteredAttributeElementProcessor( "filtered", "true", keys );
                }
            }
            if ( contentKey >= 0 && markContentFilteredSections )
            {
                int[] keys = getSectionKeysByContent( contentKey, -1 );
                aep = new FilteredAttributeElementProcessor( "filtered", "true", keys );
            }

            SectionProcessor sectionProcessorThatIncludesContentTypes = null;

            if ( criteria.isIncludeSectionContentTypesInfo() )
            {
                sectionProcessorThatIncludesContentTypes = new SectionProcessor();
            }

            processors = new ElementProcessor[]{aep,
                // mark content in original query
                new AttributeElementProcessor( "marked", "true" ), sectionProcessorThatIncludesContentTypes, collectionProcessor,
                childCountProcessor};

            doc = XDG.resultSetToXML( sectionView, resultSet, null, processors, null, -1 );
            close( resultSet );
            close( preparedStmt );

            // If treeStructure is true, all parents of the retrieved section
            // are retrieved:
            if ( treeStructure )
            {
                sql = XDG.generateSelectSQL( sectionView, sectionView.mei_lKey );
                preparedStmt = con.prepareStatement( sql.toString() );

                Element sectionsRootElement = doc.getDocumentElement();
                List<Element> sectionsElementList = XMLTool.getElementsAsList( sectionsRootElement );
                collectionProcessor.elemList = sectionsElementList;

                // remove marking of sections ("filtered"(0) and "marked"(1) attributes)
                processors[0] = null;
                processors[1] = null;

                for ( int i = 0; i < sectionsElementList.size(); i++ )
                {
                    Element sectionElement = sectionsElementList.get( i );
                    String superKey = sectionElement.getAttribute( "supersectionkey" );
                    if ( superKey != null && superKey.length() > 0 )
                    {
                        sectionsRootElement.removeChild( sectionElement );

                        // find parent element and append current element
                        Element parentElem = elemMap.get( "section_" + superKey );
                        if ( parentElem == null )
                        {
                            int key = Integer.parseInt( superKey );
                            preparedStmt.setInt( 1, key );
                            resultSet = preparedStmt.executeQuery();
                            XDG.resultSetToXML( sectionView, resultSet, doc.getDocumentElement(), processors, "name", -1 );
                            close( resultSet );
                            parentElem = collectionProcessor.lastElem;
                        }
                        Element elem = XMLTool.createElementIfNotPresent( doc, parentElem, "sections" );
                        elem.appendChild( sectionElement );
                    }
                }
            }

            // If specified, get sections recursivly, i.e. retrieve all sections below the
            // the retrieved sections:
            else if ( criteria.getSectionsRecursivly() )
            {
                if ( level > 1 || level == 0 )
                {
                    Element sectionsElement = doc.getDocumentElement();
                    Element[] sections = XMLTool.getElements( sectionsElement, "section" );

                    for ( Element section : sections )
                    {
                        int currentSectionKey = Integer.parseInt( section.getAttribute( "key" ) );
                        int[] subSectionKeys = getSectionKeysBySuperSection( currentSectionKey, false );

                        if ( subSectionKeys.length > 0 )
                        {

                            criteria.setSectionKey( -1 );
                            criteria.setSectionKeys( subSectionKeys );
                            criteria.setLevel( ( level > 1 ? level - 1 : 0 ) );

                            Document subSectionsDoc = getSections( user, criteria );
                            section.appendChild( doc.importNode( subSectionsDoc.getDocumentElement(), true ) );
                        }
                    }
                }
            }

            if ( criteria.appendAccessRights() )
            {
                securityHandler.appendAccessRights( user, doc, true, true );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get sections: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
            doc = XMLTool.createDocument( "sections" );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return doc;
    }

    public int[] getSectionKeysByContent( int contentKey, int menuKey )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tSectionContent2, db.tSectionContent2.sco_mei_lKey, false, (Column) null );

        if ( menuKey != -1 )
        {
            XDG.appendJoinSQL( sql, db.tSectionContent2.sco_mei_lKey );
            XDG.appendJoinSQL( sql, db.tMenuItem.mei_men_lKey );
            XDG.appendWhereSQL( sql, db.tMenu.men_lKey, XDG.OPERATOR_EQUAL, menuKey );
        }

        XDG.appendWhereSQL( sql, db.tSectionContent2.sco_con_lKey, XDG.OPERATOR_EQUAL, contentKey );
        return getCommonHandler().getIntArray( sql.toString() );
    }

    private int[] getSectionKeysBySuperSection( int superSectionKey, boolean recursive )
    {

        return getSectionKeysBySuperSections( new int[]{superSectionKey}, recursive );
    }

    private int[] getSectionKeysBySuperSections( int[] superSectionKeys, boolean recursive )
    {
        SectionView sectionView = SectionView.getInstance();
        int[] sectionKeys;
        StringBuffer sql = XDG.generateSelectSQL( sectionView, sectionView.mei_lKey, false, (Column[]) null );
        sql.append( " WHERE mei_lParent IN (" );
        for ( int i = 0; i < superSectionKeys.length; i++ )
        {
            if ( i > 0 )
            {
                sql.append( "," );
            }
            sql.append( superSectionKeys[i] );
        }
        sql.append( ")" );
        sectionKeys = getCommonHandler().getIntArray( sql.toString(), (int[]) null );
        if ( recursive && sectionKeys.length > 0 )
        {
            TIntArrayList keys = new TIntArrayList();
            keys.add( sectionKeys );
            keys.add( getSectionKeysBySuperSections( sectionKeys, recursive ) );
        }
        return sectionKeys;
    }

    public void appendSectionNames( int contentKey, Element contentElem )
    {

        StringBuffer sql = new StringBuffer();
        sql.append( "SELECT " ).append( "sec." ).append( db.tMenuItem.mei_lKey ).append( ", " ).append( "sec." ).append(
            db.tMenuItem.mei_men_lKey ).append( ", " ).append( "sec." ).append( db.tMenuItem.mei_lKey ).append( ", " ).append(
            "sec." ).append( db.tMenuItem.mei_sName ).append( ", " ).append( "sec." ).append( db.tMenuItem.mei_lParent ).append(
            ", " ).append( db.tSectionContent2.sco_bApproved ).append( ", " ).append( db.tContentHome.cho_mei_lKey ).append( ", " ).append(
            "secparent." ).append( db.tMenuItem.mei_lKey );
        sql.append( " FROM " ).append( db.tMenuItem ).append( " sec" );
        sql.append( " JOIN " ).append( db.tSectionContent2 ).append( " ON " ).append( "sec." ).append( db.tMenuItem.mei_lKey ).append(
            " = " ).append( db.tSectionContent2.sco_mei_lKey );
        sql.append( " LEFT JOIN " ).append( db.tContentHome ).append( " ON " ).append( db.tContentHome.cho_con_lKey ).append(
            " = " ).append( db.tSectionContent2.sco_con_lKey ).append( " AND " ).append( db.tContentHome.cho_men_lKey ).append(
            " = " ).append( db.tMenuItem.mei_men_lKey );
        sql.append( " LEFT JOIN " ).append( db.tMenuItem ).append( " secparent" ).append( " ON " ).append( "secparent." ).append(
            db.tMenuItem.mei_lKey ).append( " = sec." ).append( db.tMenuItem.mei_lParent );
        XDG.appendWhereSQL( sql, db.tSectionContent2.sco_con_lKey, XDG.OPERATOR_EQUAL );
        sql.append( "?" );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Document doc = contentElem.getOwnerDocument();
        Element root = XMLTool.createElement( doc, contentElem, "sectionnames" );
        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, contentKey );
            resultSet = preparedStmt.executeQuery();
            while ( resultSet.next() )
            {
                int sectionKey = resultSet.getInt( 1 );
                int menuKey = resultSet.getInt( 2 );
                int menuItemKey = resultSet.getInt( 3 );
                String name = resultSet.getString( 4 );
//                int parentKey = resultSet.getInt(5);
                boolean approved = resultSet.getBoolean( 6 );
                Integer homeMenuItemKey = resultSet.getInt( 7 );
                if ( resultSet.wasNull() )
                {
                    homeMenuItemKey = null;
                }
                Integer parentSectionKey = resultSet.getInt( 8 );
                if ( resultSet.wasNull() )
                {
                    parentSectionKey = null;
                }
                Element sectionName = XMLTool.createElement( doc, root, "sectionname", name );
                sectionName.setAttribute( "key", Integer.toString( sectionKey ) );
                sectionName.setAttribute( "menukey", Integer.toString( menuKey ) );
                sectionName.setAttribute( "menuitemkey", Integer.toString( menuItemKey ) );
                boolean home = homeMenuItemKey != null && homeMenuItemKey == menuItemKey;
                if ( home )
                {
                    sectionName.setAttribute( "home", "true" );
                }
                if ( parentSectionKey != null )
                {
                    sectionName.setAttribute( "supersectionkey", Integer.toString( parentSectionKey ) );
                }
                sectionName.setAttribute( "approved", String.valueOf( approved ) );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to append section names to content: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
    }

}

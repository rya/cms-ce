/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.event.ContentHandlerListener;

import com.enonic.cms.core.structure.menuitem.MenuItemEntity;

public class SectionHandler
    extends BaseHandler
    implements ContentHandlerListener
{
    private static final Logger LOG = LoggerFactory.getLogger( SectionHandler.class.getName() );

    public Document getContentTypesDocumentForSection( int menuItemSectionKey )
    {
        int[] contentTypeKeys = getContentTypesForSection( menuItemSectionKey );
        return getContentHandler().getContentTypesDocument( contentTypeKeys );
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

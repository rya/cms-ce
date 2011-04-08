/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalException;
import com.enonic.vertical.VerticalRuntimeException;
import com.enonic.vertical.engine.SectionCriteria;
import com.enonic.vertical.engine.VerticalCopyException;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalKeyException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalSecurityException;
import com.enonic.vertical.engine.VerticalUpdateException;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.dbmodel.ContentMinimalView;
import com.enonic.vertical.engine.dbmodel.ContentView;
import com.enonic.vertical.engine.dbmodel.SectionView;
import com.enonic.vertical.engine.processors.AttributeElementProcessor;
import com.enonic.vertical.engine.processors.ElementProcessor;
import com.enonic.vertical.engine.processors.ProcessElementException;
import com.enonic.vertical.event.ContentHandlerListener;

import com.enonic.cms.framework.util.TIntArrayList;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;
import com.enonic.cms.domain.structure.menuitem.section.SectionContentKey;

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

    public void updateSection( int sectionKey, boolean ordered, int[] contentTypes )
        throws VerticalCreateException, VerticalSecurityException
    {

        int orderedInt = ordered ? 1 : 0;

        StringBuffer sql = XDG.generateUpdateSQL( db.tMenuItem, db.tMenuItem.mei_bOrderedSection, db.tMenuItem.mei_lKey );
        getCommonHandler().executeSQL( sql.toString(), new int[]{orderedInt, sectionKey} );

        setContentTypesForSection( sectionKey, contentTypes );
    }

    public void createSection( int menuItemKey, boolean ordered, int[] contentTypes )
        throws VerticalCreateException
    {
        StringBuffer sql = XDG.generateUpdateSQL( db.tMenuItem, new Column[]{db.tMenuItem.mei_bSection, db.tMenuItem.mei_bOrderedSection},
                                                  new Column[]{db.tMenuItem.mei_lKey}, null );
        getCommonHandler().executeSQL( sql.toString(), new int[]{1, ordered ? 1 : 0, menuItemKey} );
        setContentTypesForSection( menuItemKey, contentTypes );
    }

    public int createSection( Document doc )
        throws VerticalCreateException, VerticalSecurityException
    {

        Element sectionElem = doc.getDocumentElement();

        int menuKey = Integer.parseInt( sectionElem.getAttribute( "menukey" ) );
        if ( menuKey < 0 )
        {

            VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                            StringUtil.expandString( "No menukey specified.", (Object) null, null )
                                             );
        }

        // Call general create method in BaseHandler
        CommonHandler commonHandler = getCommonHandler();
        int sectionKey = -1;
        try
        {
            sectionKey = (Integer) commonHandler.createEntities( null, doc, null )[0];
        }
        catch ( ProcessElementException pee )
        {
            // should not be thrown when no processors asigned

            VerticalRuntimeException.error( this.getClass(), VerticalRuntimeException.class,
                                            StringUtil.expandString( "Ignored exception!", (Object) null, pee ), pee );
        }

        // Set content types for this section
        Element contentTypesElem = XMLTool.getElement( sectionElem, "contenttypes" );
        Element[] contentTypes = XMLTool.getElements( contentTypesElem, "contenttype" );
        TIntArrayList contentTypeKeys = new TIntArrayList();
        for ( Element contentType : contentTypes )
        {
            contentTypeKeys.add( Integer.parseInt( contentType.getAttribute( "key" ) ) );
        }
        setContentTypesForSection( sectionKey, contentTypeKeys.toArray() );

        // set section access rights
        SecurityHandler securityHandler = getSecurityHandler();
        Element accessrightsElem = XMLTool.getElement( doc.getDocumentElement(), "accessrights" );
        if ( accessrightsElem != null )
        {
            accessrightsElem.setAttribute( "key", Integer.toString( sectionKey ) );
            securityHandler.createAccessRights( accessrightsElem );
        }

        return sectionKey;
    }

    public void updateSection( User user, Document doc )
        throws VerticalUpdateException, VerticalSecurityException
    {

        Element elem = doc.getDocumentElement();
        int sectionKey = Integer.parseInt( elem.getAttribute( "key" ) );

        boolean ordered = isSectionOrdered( sectionKey );

        CommonHandler commonHandler = getCommonHandler();
        try
        {
            commonHandler.updateEntities( doc, null );
        }
        catch ( ProcessElementException pee )
        {
            // NOTE!! Ignored exception. Never thrown.

            VerticalRuntimeException.error( this.getClass(), VerticalRuntimeException.class,
                                            StringUtil.expandString( "Ignored exception: %t", (Object) null, pee ),
                                            pee );
        }

        Element sectionElem = null;
        try
        {
            // Remove old content types for this section
            removeContentTypesForSection( sectionKey );

            sectionElem = doc.getDocumentElement();
            Element contentTypesElem = XMLTool.getElement( sectionElem, "contenttypes" );
            Element[] contentTypes = XMLTool.getElements( contentTypesElem, "contenttype" );
            TIntArrayList contentTypeKeys = new TIntArrayList();
            for ( Element contentType : contentTypes )
            {
                contentTypeKeys.add( Integer.parseInt( contentType.getAttribute( "key" ) ) );
            }

            // Set the new ones
            setContentTypesForSection( sectionKey, contentTypeKeys.toArray() );

            // Apply the filter
            applySectionFilter( sectionKey );

            // set section content order if section is ordered
            if ( !ordered && "true".equals( sectionElem.getAttribute( "ordered" ) ) )
            {
                int[] contentKeys = getContentKeysBySection( sectionKey );
                for ( int i = 0; i < contentKeys.length; i++ )
                {
                    updateSectionContent( user, sectionKey, contentKeys[i], i + 1, true, null );
                }
            }
            else if ( ordered && "false".equals( sectionElem.getAttribute( "ordered" ) ) )
            {
                int[] contentKeys = getContentKeysBySection( sectionKey );
                for ( int contentKey : contentKeys )
                {
                    updateSectionContent( user, sectionKey, contentKey, 0, true, null );
                }
            }
        }
        catch ( VerticalCreateException vce )
        {
            String message = "Failed to create section contenttype filter: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                            StringUtil.expandString( message, (Object) null, vce ), vce );
        }
        catch ( VerticalRemoveException vre )
        {
            String message = "Failed to create section contenttype filter: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                            StringUtil.expandString( message, (Object) null, vre ), vre );
        }

        // set section access rights
        SecurityHandler securityHandler = getSecurityHandler();
        Element accessrightsElem = XMLTool.getElement( elem, "accessrights" );
        if ( accessrightsElem != null )
        {
            accessrightsElem.setAttribute( "key", sectionElem.getAttribute( "key" ) );

            Document tempDoc = XMLTool.createDocument();
            tempDoc.appendChild( tempDoc.importNode( accessrightsElem, true ) );
            securityHandler.updateAccessRights( user, tempDoc );
        }

    }

    private int[] getContentKeysBySection( int sectionKey )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tSectionContent2, new Column[]{db.tSectionContent2.sco_con_lKey}, false,
                                                  new Column[]{db.tSectionContent2.sco_mei_lKey, db.tSectionContent2.sco_bApproved} );
        return getCommonHandler().getIntArray( sql.toString(), new Object[]{sectionKey, Boolean.TRUE} );
    }

    public boolean removeSection( int sectionKey )
        throws VerticalRemoveException, VerticalSecurityException
    {
        StringBuffer sql = XDG.generateUpdateSQL( db.tMenuItem, db.tMenuItem.mei_bSection, db.tMenuItem.mei_lKey );
        return ( getCommonHandler().executeSQL( sql.toString(), new int[]{0, sectionKey} ) > 0 );
    }

    public void removeSection( int sectionKey, boolean recursive )
        throws VerticalRemoveException, VerticalSecurityException
    {
        if ( recursive )
        {
            removeSectionRecursive( sectionKey );
        }
        else
        {
            removeSection( sectionKey );
        }
    }

    private boolean removeSectionRecursive( int sectionKey )
        throws VerticalRemoveException, VerticalSecurityException
    {
        boolean success = true;
        int[] keys = getSectionKeysBySuperSection( sectionKey, false );
        for ( int key : keys )
        {
            success = success && removeSectionRecursive( key );
        }

        return success && removeSection( sectionKey );
    }

    private void applySectionFilter( int sectionKey )
        throws VerticalUpdateException
    {

        TIntArrayList contentTypes = new TIntArrayList();
        contentTypes.add( getContentTypesForSection( sectionKey ) );

        if ( contentTypes.size() == 0 )
        {
            // This section has no filter
            return;
        }

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet;

        try
        {
            con = getConnection();
            StringBuffer sql = new StringBuffer( "SELECT sco_con_lKey, cat_cty_lKey FROM tSectionContent2" );
            sql.append( " JOIN tContent ON sco_con_lkey = con_lKey" );
            sql.append( " JOIN tCategory ON con_cat_lkey = cat_lKey" );
            sql.append( " WHERE sco_mei_lKey = ?" );
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, sectionKey );
            resultSet = preparedStmt.executeQuery();

            TIntArrayList removeContentKeys = new TIntArrayList();
            while ( resultSet.next() )
            {
                int contentKey = resultSet.getInt( 1 );
                int contentTypeKey = resultSet.getInt( 2 );

                if ( !contentTypes.contains( contentTypeKey ) )
                {
                    removeContentKeys.add( contentKey );
                }
            }
            // Remove all contents in this section that does not have right content types
            if ( removeContentKeys.size() > 0 )
            {
                sql = XDG.generateRemoveSQL( db.tSectionContent2, db.tSectionContent2.sco_con_lKey, removeContentKeys.size() );
                sql.append( " AND sco_mei_lKey = ?" );
                preparedStmt = con.prepareStatement( sql.toString() );
                for ( int i = 0; i < removeContentKeys.size(); i++ )
                {
                    preparedStmt.setInt( i + 1, removeContentKeys.get( i ) );
                }
                preparedStmt.setInt( removeContentKeys.size() + 1, sectionKey );
                preparedStmt.executeUpdate();
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to apply section filter %0: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                            StringUtil.expandString( message, sectionKey, sqle ), sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    private int[] getContentTypesForSection( int menuItemSectionKey )
    {
        StringBuffer sql =
            XDG.generateSelectSQL( db.tSecConTypeFilter2, db.tSecConTypeFilter2.sctf_cty_lKey, false, db.tSecConTypeFilter2.sctf_mei_lKey );
        return getCommonHandler().getIntArray( sql.toString(), new Object[]{menuItemSectionKey} );
    }

    public void setContentTypesForSection( int sectionKey, int[] contentTypeKeys )
        throws VerticalCreateException
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;

        try
        {
            removeContentTypesForSection( sectionKey );
            con = getConnection();
            StringBuffer sql = XDG.generateInsertSQL( db.tSecConTypeFilter2 );
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 3, sectionKey );

            for ( int contentTypeKey : contentTypeKeys )
            {
                int sectionFilterKey = getCommonHandler().getNextKey( db.tSecConTypeFilter2.getName() );
                preparedStmt.setInt( 1, sectionFilterKey );
                preparedStmt.setInt( 2, contentTypeKey );
                int result = preparedStmt.executeUpdate();
                if ( result == 0 )
                {
                    String message = "Failed to create section contenttype filter.";

                    VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                                    StringUtil.expandString( message, (Object) null, null ) );
                }
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to create section contenttype filter: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        catch ( VerticalRemoveException vre )
        {
            String message = "Failed to create section contenttype filter: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                            StringUtil.expandString( message, (Object) null, vre ), vre );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    private void removeContentTypesForSection( int sectionKey )
        throws VerticalRemoveException
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;

        try
        {
            con = getConnection();
            StringBuffer sql = XDG.generateRemoveSQL( db.tSecConTypeFilter2, db.tSecConTypeFilter2.sctf_mei_lKey );
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, sectionKey );
            preparedStmt.executeUpdate();
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to remove section contenttype filter: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalRemoveException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    public Document getSuperSectionNames( int sectionKey, boolean includeSection )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Document doc = XMLTool.createDocument( "sectionnames" );
        Element root = doc.getDocumentElement();

        try
        {
            SectionView sectionView = SectionView.getInstance();
            con = getConnection();
            StringBuffer sql = XDG.generateSelectSQL( sectionView, null, false, sectionView.mei_lKey );

            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, sectionKey );
            resultSet = preparedStmt.executeQuery();

            boolean firstTime = true;
            Element lastElem = null;
            // Loop as long as supersections are found
            while ( resultSet.next() )
            {
                sectionKey = resultSet.getInt( "mei_lKey" );
                String name = resultSet.getString( "mei_sName" );
                int parentKey = resultSet.getInt( "mei_lParent" );
                boolean lastTime = resultSet.wasNull();

                if ( !firstTime || includeSection )
                {
                    Element sectionName;
                    if ( lastElem == null )
                    {
                        sectionName = XMLTool.createElement( doc, root, "sectionname", name );
                    }
                    else
                    {
                        sectionName = XMLTool.createElementBeforeChild( root, lastElem, "sectionname", name );
                    }

                    sectionName.setAttribute( "key", Integer.toString( sectionKey ) );
                    if ( !lastTime )
                    {
                        sectionName.setAttribute( "supersectionkey", Integer.toString( parentKey ) );
                    }
                    lastElem = sectionName;
                }
                firstTime = false;
                if ( !lastTime )
                {
                    preparedStmt.setInt( 1, parentKey );
                    resultSet = preparedStmt.executeQuery();
                }
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get sections: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return doc;
    }

    public int getMenuKeyBySection( int sectionKey )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tMenuItem, db.tMenuItem.mei_men_lKey, false,
                                                  new Column[]{db.tMenuItem.mei_lKey, db.tMenuItem.mei_bSection} );
        return getCommonHandler().getInt( sql.toString(), new Object[]{sectionKey, 1} );
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

    private void addSectionContent( User user, int sectionKey, int contentKey, int order, boolean approved )
        throws VerticalCreateException, VerticalSecurityException
    {

        SecurityHandler securyHandler = getSecurityHandler();
        if ( !securyHandler.validateContentAddToSection( user, sectionKey ) )
        {
            String message = "User is not allowed to add content to this section: %0";

            VerticalRuntimeException.error( this.getClass(), VerticalSecurityException.class,
                                            StringUtil.expandString( message, sectionKey, null ) );
        }

        // First, check for contenttype filters
        TIntArrayList contentTypes = new TIntArrayList();
        contentTypes.add( getContentTypesForSection( sectionKey ) );
        if ( contentTypes.size() > 0 )
        {
            int contentTypeKey = getContentHandler().getContentTypeKey( contentKey );
            if ( !contentTypes.contains( contentTypeKey ) )
            {
                String message = "Section does not allow this content type: %0";

                VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                                StringUtil.expandString( message, contentTypeKey, null ) );
            }
        }
        Connection con = null;
        PreparedStatement preparedStmt = null;

        try
        {
            con = getConnection();
            StringBuffer sql = XDG.generateInsertSQL( db.tSectionContent2 );

            preparedStmt = con.prepareStatement( sql.toString() );
            int scoPrimaryKey = getCommonHandler().getNextKey( db.tSectionContent2.getName() );
            preparedStmt.setInt( 1, scoPrimaryKey );
            preparedStmt.setInt( 2, contentKey );
            preparedStmt.setInt( 3, sectionKey );
            preparedStmt.setInt( 4, order );
            preparedStmt.setBoolean( 5, approved );

            int result = preparedStmt.executeUpdate();
            if ( result == 0 )
            {
                String message = "Failed to add content to section.";

                VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                                StringUtil.expandString( message, (Object) null, null ) );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to add content to section: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    public void updateSectionContent( User user, int sectionKey, int contentKey, int order, boolean approved, SectionContentKey scKey )
    {
        updateSectionContent( user, sectionKey, contentKey, order, approved, scKey, true );
    }

    public void updateSectionContent( User user, int sectionKey, int contentKey, int order, boolean approved, SectionContentKey scKey,
                                      boolean checkAccess )
        throws VerticalUpdateException, VerticalSecurityException
    {
        if ( checkAccess )
        {
            checkSectionApproveAccess( user, sectionKey );
        }

        Connection con = null;
        PreparedStatement preparedStmt = null;

        if ( scKey == null )
        {
            scKey = getSectionContentKey( sectionKey, contentKey );
        }
        try
        {
            con = getConnection();
            StringBuffer sql = XDG.generateUpdateSQL( db.tSectionContent2 );

//            UPDATE tSectionContent2
//            SET sco_con_lKey = ?, sco_mei_lKey = ?, sco_lOrder = ?, sco_bApproved = ?, sco_dteTimestamp = @currentTimestamp@
//            WHERE sco_lkey = ?

            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, contentKey );
            preparedStmt.setInt( 2, sectionKey );
            preparedStmt.setInt( 3, order );
            preparedStmt.setBoolean( 4, approved );
            preparedStmt.setInt( 5, scKey.toInt() );

            int result = preparedStmt.executeUpdate();
            if ( result == 0 )
            {
                String message = "Failed to add content to section.";

                VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                                StringUtil.expandString( message, (Object) null, null ) );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to update content section: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    private void checkSectionApproveAccess( User user, int sectionKey )
    {
        SecurityHandler securyHandler = getSecurityHandler();
        if ( !securyHandler.validateSectionApprove( user, sectionKey ) )
        {
            String message = "User is not allowed to update content in this section: %0";

            VerticalRuntimeException.error( this.getClass(), VerticalSecurityException.class,
                                            StringUtil.expandString( message, sectionKey, null ) );
        }
    }

    public void updateSectionContentIncreasePosition( final User user, final int sectionKey, int fromOrder )
    {
        checkSectionApproveAccess( user, sectionKey );

        Connection con = null;
        PreparedStatement preparedStmt = null;

        try
        {
            con = getConnection();
            final StringBuffer sql = new StringBuffer();
            sql.append( "update " ).append( db.tSectionContent2 ).append( " set " );
            sql.append( db.tSectionContent2.sco_lOrder ).append( " = " ).append( db.tSectionContent2.sco_lOrder ).append( " + 1" );
            sql.append( " where " );
            sql.append( db.tSectionContent2.sco_lOrder ).append( " >= " ).append( fromOrder );
            sql.append( " and " );
            sql.append( db.tSectionContent2.sco_mei_lKey ).append( " = " ).append( sectionKey );

            preparedStmt = con.prepareStatement( sql.toString() );

            preparedStmt.executeUpdate();
        }
        catch ( final SQLException sqle )
        {
            final String message = "Failed to update content section: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    public void setSectionContentsApproved( User user, int sectionKey, int[] contentKeys, boolean approved )
        throws VerticalUpdateException, VerticalSecurityException
    {

        SecurityHandler securyHandler = getSecurityHandler();
        if ( !securyHandler.validateSectionApprove( user, sectionKey ) )
        {
            String message = "User is not allowed to update content in this section: %0";

            VerticalRuntimeException.error( this.getClass(), VerticalSecurityException.class,
                                            StringUtil.expandString( message, sectionKey, null ) );
        }

        StringBuffer sql =
            XDG.generateUpdateSQL( db.tSectionContent2, db.tSectionContent2.sco_bApproved, db.tSectionContent2.sco_mei_lKey );
        XDG.appendWhereInSQL( sql, db.tSectionContent2.sco_con_lKey, contentKeys );

        getCommonHandler().executeSQL( sql.toString(), new int[]{approved ? 1 : 0, sectionKey} );
    }

    public void addContentToSections( User user, Document doc )
        throws VerticalCreateException, VerticalSecurityException
    {

        Element sectionsElem = doc.getDocumentElement();
        int newContentKey = Integer.parseInt( sectionsElem.getAttribute( "contentkey" ) );

        Connection con = null;
        try
        {
            con = getConnection();
            Element[] sectionElems = XMLTool.getElements( sectionsElem );

            for ( final Element sectionElem : sectionElems )
            {
                final int sectionKey = Integer.parseInt( sectionElem.getAttribute( "key" ) );
                final boolean approved = Boolean.valueOf( sectionElem.getAttribute( "approved" ) );
                final boolean ordered = isSectionOrdered( sectionElem, sectionKey );
                final boolean manuallyOrdered = isManuallyOrdered( sectionElem );

                if ( approved && ordered )
                {
                    /* Approved && ordered */
                    if ( manuallyOrdered )
                    {
                        /* Manually ordered - order might have been changed - update all */
                        boolean checkAccess = true;
                        final Element contentsElem = XMLTool.getElement( sectionElem, "contents" );
                        final Element[] contentElems = XMLTool.getElements( contentsElem );
                        for ( int j = 0; j < contentElems.length; j++ )
                        {
                            final int keyOfExistingContent = Integer.parseInt( contentElems[j].getAttribute( "key" ) );
                            final SectionContentKey sectionContentKey = getSectionContentKey( sectionKey, keyOfExistingContent );
                            if ( keyOfExistingContent != newContentKey || sectionContentKey != null )
                            {
                                /* This is not the content we're handling, or the one we're handling already exist, either way, update */
                                updateSectionContent( user, sectionKey, keyOfExistingContent, j + 1, true, sectionContentKey, checkAccess );
                                checkAccess = false; // Only check access once for each section
                            }
                            else
                            {
                                /* This is the content we're handling - and it doesn't exist - add */
                                addSectionContent( user, sectionKey, keyOfExistingContent, j + 1, true );
                            }
                        }
                    }
                    else
                    {
                        /* Manually order skipped */
                        final SectionContentKey sectionContentKey = getSectionContentKey( sectionKey, newContentKey );
                        if ( sectionContentKey == null )
                        {
                            /* Content doesn't exist - move the rest one position down - insert first */
                            updateSectionContentIncreasePosition( user, sectionKey, 1 );
                            addSectionContent( user, sectionKey, newContentKey, 1, approved );
                        }
                        else
                        {
                            /* Content exist - do nothing, move on */
                        }
                    }
                }
                else
                {
                    /* Not approved or not ordered */
                    final SectionContentKey sectionContentKey = getSectionContentKey( sectionKey, newContentKey );
                    if ( sectionContentKey == null )
                    {
                        /* Content doesn't exist - add with pos 0 */
                        addSectionContent( user, sectionKey, newContentKey, 0, approved );
                    }
                    else
                    {
                        /* Content exist - update with pos 0 */
                        updateSectionContent( user, sectionKey, newContentKey, 0, approved, sectionContentKey );
                    }
                }
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get connection: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        catch ( VerticalUpdateException vue )
        {
            String message = "%t";

            VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                            StringUtil.expandString( message, (Object) null, vue ), vue );
        }
        finally
        {
            close( con );
        }
    }

    private boolean isSectionOrdered( final Element sectionEl, final int sectionKey )
    {
        final String orderedStr = sectionEl.getAttribute( "ordered" );
        final boolean ordered;
        if ( StringUtils.isBlank( orderedStr ) )
        {
            ordered = isSectionOrdered( sectionKey );
        }
        else
        {
            ordered = Boolean.valueOf( orderedStr );
        }
        return ordered;
    }

    private boolean isManuallyOrdered( final Element sectionEl )
    {
        final String manuallyOrderedStr = sectionEl.getAttribute( "manuallyOrder" );
        if ( StringUtils.isNotBlank( manuallyOrderedStr ) )
        {
            return Boolean.valueOf( manuallyOrderedStr );
        }
        return true;
    }

    public SectionContentKey getSectionContentKey( int sectionKey, int contentKey )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tSectionContent2, db.tSectionContent2.sco_lkey, false, (Column) null );
        XDG.appendWhereSQL( sql, db.tSectionContent2.sco_mei_lKey, XDG.OPERATOR_EQUAL, sectionKey );
        XDG.appendWhereSQL( sql, db.tSectionContent2.sco_con_lKey, XDG.OPERATOR_EQUAL, contentKey );
        int sectionContentKey = getCommonHandler().getInt( sql.toString(), (int[]) null );
        if ( sectionContentKey < 0 )
        {
            return null;
        }
        else
        {
            return new SectionContentKey( sectionContentKey );
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

    public long getSectionContentTimestamp( int sectionKey )
    {
        long timestamp = 0;

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        try
        {
            con = getConnection();
            StringBuffer sql =
                XDG.generateSelectSQL( db.tSectionContent2, db.tSectionContent2.sco_dteTimestamp, false, db.tSectionContent2.sco_mei_lKey );
            sql.append( " ORDER BY sco_dteTimestamp DESC" );
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, sectionKey );

            resultSet = preparedStmt.executeQuery();
            if ( resultSet.next() )
            {
                Timestamp time = resultSet.getTimestamp( "sco_dteTimestamp" );
                timestamp = time.getTime();
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get section content timestamp: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return timestamp;
    }

    public boolean isSectionOrdered( int sectionKey )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        boolean ordered = false;

        try
        {
            con = getConnection();
            StringBuffer sql = XDG.generateSelectSQL( db.tMenuItem, db.tMenuItem.mei_bOrderedSection, false, db.tMenuItem.mei_lKey );
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, sectionKey );
            resultSet = preparedStmt.executeQuery();
            if ( resultSet.next() )
            {
                ordered = resultSet.getBoolean( 1 );
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

        return ordered;
    }

    public XMLDocument getContentTitlesBySection( int sectionKey, String orderBy, int fromIndex, int count, boolean includeTotalCount,
                                                  boolean approvedOnly )
    {
        ContentView contentView = ContentView.getInstance();
        StringBuffer sql = XDG.generateSelectSQL( this.db.tSectionContent2, new Column[]{this.db.tSectionContent2.sco_con_lKey,
            this.db.tSectionContent2.sco_bApproved}, false, null );
        sql.append( " LEFT JOIN " ).append( ContentMinimalView.getInstance().getReplacementSql() ).append( " ON " );
        sql.append( this.db.tSectionContent2.sco_con_lKey.getName() ).append( " = " );
        sql.append( contentView.con_lKey.getName() );
        sql = XDG.generateWhereSQL( sql, new Column[]{this.db.tSectionContent2.sco_mei_lKey} );

        if ( approvedOnly )
        {
            sql.append( " AND " );
            sql.append( this.db.tSectionContent2.sco_bApproved.getName() );
            sql.append( " = 1" );
        }

        if ( orderBy == null )
        {
            orderBy = contentView.cov_sTitle.getName();
        }

        sql.append( " ORDER BY " );
        if ( !approvedOnly )
        {
            sql.append( this.db.tSectionContent2.sco_bApproved.getName() );
            sql.append( ", " );
        }

        if ( isSectionOrdered( sectionKey ) )
        {
            sql.append( this.db.tSectionContent2.sco_lOrder.getName() );
            sql.append( " ASC, " );
        }

        sql.append( orderBy );

        Connection con = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;
        TIntArrayList contentKeys;

        int totalCount = 0;
        if ( count > 20 )
        {
            contentKeys = new TIntArrayList();
        }
        else
        {
            contentKeys = new TIntArrayList();
        }

        HashMap<String, String> contentApprovedMap = new HashMap<String, String>();

        try
        {
            con = getConnection();
            prepStmt = con.prepareStatement( sql.toString() );
            prepStmt.setInt( 1, sectionKey );
            resultSet = prepStmt.executeQuery();

            boolean moreResults = resultSet.next();
            int i = fromIndex;

            // Skip rows:
            try
            {
                if ( fromIndex > 0 )
                {
                    resultSet.relative( fromIndex );
                }
            }
            catch ( SQLException e )
            {
                // ResultSet is not scrollable
                i = 0;
            }

            totalCount = fromIndex;
            for (; ( ( includeTotalCount || i < fromIndex + count ) && moreResults ); i++ )
            {
                if ( i < fromIndex )
                {
                    moreResults = resultSet.next();
                    continue;
                }

                if ( i < fromIndex + count )
                {
                    int contentKey = resultSet.getInt( 1 );
                    boolean approved = resultSet.getBoolean( 2 );
                    contentKeys.add( contentKey );
                    contentApprovedMap.put( Integer.toString( contentKey ), Boolean.toString( approved ) );
                }

                totalCount++;
                moreResults = resultSet.next();
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get content keys for content in sections: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( prepStmt );
            close( con );
        }

        if ( contentKeys.size() == 0 )
        {
            org.jdom.Element contentsEl = new org.jdom.Element( "contenttitles" );
            if ( includeTotalCount )
            {
                contentsEl.setAttribute( "totalcount", "0" );
            }
            return XMLDocumentFactory.create( new org.jdom.Document( contentsEl ) );
        }

        ContentHandler contentHandler = getContentHandler();
        MenuItemEntity section = menuItemDao.findByKey( sectionKey );
        XMLDocument doc = contentHandler.getContentTitles( contentKeys.toArray(), true, section );

        if ( includeTotalCount )
        {
            org.jdom.Document jdomDoc = doc.getAsJDOMDocument();
            jdomDoc.getRootElement().setAttribute( "totalcount", Integer.toString( totalCount ) );
        }

        return doc;
    }

    public void copySection( int sectionKey )
        throws VerticalCopyException, VerticalSecurityException
    {
        copySections( null, new int[]{sectionKey} );
    }

    private void copySections( CopyContext copyContext, int[] sectionKeys )
        throws VerticalCopyException, VerticalSecurityException
    {
        SectionView sectionView = SectionView.getInstance();
        String sql = XDG.generateSelectSQL( sectionView, new Column[]{sectionView.mei_men_lKey, sectionView.mei_lParent}, false,
                                            new Column[]{sectionView.mei_lKey} ).toString();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;

        try
        {
            conn = getConnection();
            stmt = conn.prepareStatement( sql );
            for ( int sectionKey : sectionKeys )
            {
                stmt.setInt( 1, sectionKey );

                result = stmt.executeQuery();
                if ( result.next() )
                {
                    MenuItemKey superKey = new MenuItemKey( result.getInt( 2 ) );
                    if ( result.wasNull() )
                    {
                        superKey = null;
                    }
                    copySectionAtSameLevel( copyContext, sectionKey, superKey );
                }
            }
        }
        catch ( SQLException e )
        {
            LOG.error( StringUtil.expandString( "Failed to find section: %t", (Object) null, e ), e );
        }
        finally
        {
            close( result );
            close( stmt );
            close( conn );
        }
    }

    public int[] getSectionKeysByMenu( int menuKey )
    {
        SectionView view = SectionView.getInstance();
        Column selectColumn = view.mei_lKey;
        Column[] whereColumns = {view.mei_men_lKey, view.mei_lParent.getNullColumn()};
        StringBuffer sql = XDG.generateSelectSQL( view, selectColumn, false, whereColumns );
        CommonHandler commonHandler = getCommonHandler();
        return commonHandler.getIntArray( sql.toString(), menuKey );
    }

    private void copySectionAtSameLevel( CopyContext copyContext, int sectionKey, MenuItemKey superKey )
        throws VerticalCopyException, VerticalSecurityException
    {
        try
        {
            copySection( copyContext, sectionKey, superKey );
        }
        catch ( VerticalKeyException e )
        {

            VerticalRuntimeException.error( this.getClass(), VerticalCopyException.class,
                                            StringUtil.expandString( "Failed to generate section key: %t",
                                                                     (Object) null, e ), e );
        }
        catch ( SQLException e )
        {

            VerticalRuntimeException.error( this.getClass(), VerticalCopyException.class,
                                            StringUtil.expandString( "Failed to copy section: %t", (Object) null, e ),
                                            e );
        }
    }

    private void copySection( CopyContext copyContext, int sourceKey, MenuItemKey superKey )
        throws SQLException, VerticalKeyException
    {
        if ( copyContext != null )
        {
            copyContext.putSectionKey( sourceKey, superKey.toInt() );
        }

        // Copy content
        copySectionData( sourceKey, superKey );
        copySectionContent( sourceKey, superKey );
        copySecConTypeFilter( sourceKey, superKey );

        // Copy children
        int[] subKeys = getSectionKeysBySuperSection( sourceKey, false );
        for ( int subKey : subKeys )
        {
            copySection( copyContext, subKey, superKey );
        }
    }

    private void copySectionData( int sourceKey, MenuItemKey superKey )
        throws SQLException
    {
        String sql1 = XDG.generateSelectSQL( db.tMenuItem, db.tMenuItem.mei_bOrderedSection, false,
                                             new Column[]{db.tMenuItem.mei_lKey, db.tMenuItem.mei_bSection} ).toString();
        int ordered = getCommonHandler().getInt( sql1, new int[]{sourceKey, 1} );
        if ( ordered < 0 )
        {
            ordered = 0;
        }
        String sql2 = XDG.generateUpdateSQL( db.tMenuItem, new Column[]{db.tMenuItem.mei_bSection, db.tMenuItem.mei_bOrderedSection},
                                             new Column[]{db.tMenuItem.mei_lKey}, null ).toString();
        getCommonHandler().executeSQL( sql2, new int[]{1, ordered, superKey.toInt()} );

    }

    private void copySectionContent( int sourceKey, MenuItemKey targetKey )
        throws SQLException
    {
        String sql1 = XDG.generateSelectSQL( this.db.tSectionContent2, this.db.tSectionContent2.sco_mei_lKey ).toString();
        String sql2 = XDG.generateInsertSQL( this.db.tSectionContent2 ).toString();

        Connection con = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        ResultSet result = null;
        try
        {
            con = getConnection();
            stmt1 = con.prepareStatement( sql1 );
            stmt2 = con.prepareStatement( sql2 );

            stmt1.setInt( 1, sourceKey );
            result = stmt1.executeQuery();

            while ( result.next() )
            {
                stmt2.clearParameters();

                int newSectionContentPK = getCommonHandler().getNextKey( db.tSectionContent2 );
                stmt2.setInt( 1, newSectionContentPK );
                stmt2.setInt( 2, result.getInt( 2 ) );
                stmt2.setInt( 3, targetKey.toInt() );
                stmt2.setInt( 4, result.getInt( 4 ) );
                stmt2.setInt( 5, result.getInt( 5 ) );
                // No.6 = Timestamp!

                stmt2.executeUpdate();
            }
        }
        finally
        {
            close( result );
            close( stmt2 );
            close( stmt1 );
            close( con );
        }
    }

    private void copySecConTypeFilter( int sourceKey, MenuItemKey targetKey )
        throws SQLException
    {
        String sql1 = XDG.generateSelectSQL( this.db.tSecConTypeFilter2, this.db.tSecConTypeFilter2.sctf_mei_lKey ).toString();
        String sql2 = XDG.generateInsertSQL( this.db.tSecConTypeFilter2 ).toString();

        Connection con = null;
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        ResultSet result = null;
        try
        {
            con = getConnection();
            stmt1 = con.prepareStatement( sql1 );
            stmt2 = con.prepareStatement( sql2 );

            stmt1.setInt( 1, sourceKey );
            result = stmt1.executeQuery();

            while ( result.next() )
            {
                stmt2.clearParameters();

                int sectionFilterKey = getCommonHandler().getNextKey( db.tSecConTypeFilter2.getName() );
                stmt2.setInt( 1, sectionFilterKey );
                stmt2.setInt( 2, targetKey.toInt() );
                stmt2.setInt( 3, result.getInt( 2 ) );

                stmt2.executeUpdate();
            }
        }
        finally
        {
            close( result );
            close( stmt2 );
            close( stmt1 );
            close( con );
        }
    }

}

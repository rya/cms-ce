/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.dbmodel.ContentView;
import com.enonic.vertical.engine.filters.ContentFilter;
import com.enonic.vertical.event.ContentHandlerListener;

import com.enonic.cms.store.dao.ContentTypeDao;

import com.enonic.cms.domain.CalendarUtil;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.security.user.User;

public final class ContentHandler
    extends BaseHandler
{
    private static final Map<String, String> orderByMap;

    static
    {
        orderByMap = new HashMap<String, String>();

        orderByMap.put( "category", "cat_lKey" );
        orderByMap.put( "owneruid", "usr_sOwnerUID" );
        orderByMap.put( "ownername", "usr_sOwnerName" );
        orderByMap.put( "created", "con_dteCreated" );
        orderByMap.put( "@created", "con_dteCreated" );
        orderByMap.put( "publishfrom", "con_dtePublishFrom" );
        orderByMap.put( "@publishfrom", "con_dtePublishFrom" );
        orderByMap.put( "publishto", "con_dtePublishTo" );
        orderByMap.put( "@publishto", "con_dtePublishTo" );
        orderByMap.put( "languagekey", "con_lan_lKey" );
        orderByMap.put( "title", "cov_sTitle" );
        orderByMap.put( "priority", "con_lPriority" );
        orderByMap.put( "@priority", "con_lPriority" );
        orderByMap.put( "timestamp", "cov_dteTimeStamp" );
        orderByMap.put( "@timestamp", "cov_dteTimeStamp" );
    }

    @Autowired
    private ContentTypeDao contentTypeDao;

    public Document getContentTypesDocument( int[] contentTypeKeys )
    {
        Document doc = XMLTool.createDocument( "contenttypes" );
        if ( contentTypeKeys.length > 0 )
        {
            for ( int contentTypeKey : contentTypeKeys )
            {
                Element contentTypeElem = XMLTool.createElement( doc, doc.getDocumentElement(), "contenttype" );
                contentTypeElem.setAttribute( "key", Integer.toString( contentTypeKey ) );
                XMLTool.createElement( doc, contentTypeElem, "name", getContentHandler().getContentTypeName( contentTypeKey ) );
            }
        }
        return doc;
    }

    public synchronized void addListener( ContentHandlerListener chl )
    {
    }

    public Document getContent( User user, int contentKey, boolean publishedOnly, int parentLevel, int childrenLevel,
                                int parentChildrenLevel, boolean relatedTitlesOnly, boolean includeStatistics, ContentFilter contentFilter )
    {
        throw new IllegalArgumentException("Need to hibernate this method. Disabled for now.");
        /*
        return getContents( user, new int[]{contentKey}, publishedOnly, false, parentLevel, childrenLevel, parentChildrenLevel,
                            relatedTitlesOnly, includeStatistics, contentFilter );
        */
    }

    public Document getContentType( int contentTypeKey, boolean includeContentCount )
    {
        return getContentTypes( new int[]{contentTypeKey}, includeContentCount );
    }

    public int getContentTypeKey( int contentKey )
    {
        ContentEntity entity = contentDao.findByKey( new ContentKey( contentKey ) );
        return entity != null ? entity.getCategory().getContentType().getKey() : -1;
    }

    public String getContentTypeName( int contentTypeKey )
    {
        ContentTypeEntity entity = contentTypeDao.findByKey( contentTypeKey );
        return entity != null ? entity.getName() : null;
    }

    public Document getContentTypes( int[] contentTypeKeys, boolean includeContentCount )
    {
        List<ContentTypeEntity> list;

        if ( contentTypeKeys != null && contentTypeKeys.length > 0 )
        {
            list = new ArrayList<ContentTypeEntity>();

            for ( int key : contentTypeKeys )
            {
                ContentTypeEntity entity = contentTypeDao.findByKey( key );
                if ( entity != null )
                {
                    list.add( entity );
                }
            }
        }
        else
        {
            list = contentTypeDao.getAll();
        }

        return createContentTypesDoc( list, includeContentCount );
    }

    private Document createContentTypesDoc( List<ContentTypeEntity> list, boolean includeContentCount )
    {
        Document doc = XMLTool.createDocument( "contenttypes" );
        Element root = doc.getDocumentElement();

        if ( list == null )
        {
            return doc;
        }

        for ( ContentTypeEntity entity : list )
        {
            Element elem = XMLTool.createElement( doc, root, "contenttype" );
            elem.setAttribute( "key", String.valueOf( entity.getKey() ) );
            elem.setAttribute( "contenthandlerkey", String.valueOf( entity.getHandler().getKey() ) );
            elem.setAttribute( "handler", entity.getHandler().getClassName() );

            if ( entity.getDefaultCssKey() != null )
            {
                elem.setAttribute( "csskey", entity.getDefaultCssKey().toString() );
                elem.setAttribute( "csskeyexists", resourceDao.getResourceFile( entity.getDefaultCssKey() ) != null ? "true" : "false" );
            }

            if ( includeContentCount )
            {
                int count = getContentCountByContentType( entity.getKey() );
                elem.setAttribute( "contentcount", String.valueOf( count ) );
            }

            XMLTool.createElement( doc, elem, "name", entity.getName() );
            if ( entity.getDescription() != null )
            {
                XMLTool.createElement( doc, elem, "description", entity.getDescription() );
            }

            if ( entity.getData() != null )
            {
                Document modDoc = entity.getData().getAsDOMDocument();
                Element mdElem = modDoc.getDocumentElement();

                if ( mdElem.getTagName().equals( "module" ) )
                {
                    Element tempElem = XMLTool.createElement( doc, elem, "moduledata" );
                    tempElem.appendChild( doc.importNode( mdElem, true ) );
                }
                else
                {
                    elem.appendChild( doc.importNode( mdElem, true ) );
                }
            }
            else
            {
                XMLTool.createElement( doc, elem, "moduledata" );
            }

            XMLTool.createElement( doc, elem, "timestamp", CalendarUtil.formatTimestamp( entity.getTimestamp(), true ) );
        }

        return doc;
    }

    public int getContentCountByContentType( int contentTypeKey )
    {
        ContentView contentView = ContentView.getInstance();
        StringBuffer countSQL =
            XDG.generateSelectSQL( contentView, contentView.con_lKey.getCountColumn(), false, contentView.cat_cty_lKey );
        return getCommonHandler().getInt( countSQL.toString(), contentTypeKey );
    }

    public StringBuffer getCategoryPathString( int contentKey )
    {

        CategoryKey categoryKey;

        ContentEntity content = contentDao.findByKey( new ContentKey( contentKey ) );
        if ( content != null )
        {
            categoryKey = content.getCategory().getKey();
            return getCategoryHandler().getPathString( categoryKey );
        }
        else
        {
            return new StringBuffer();
        }

    }

    public int getCurrentVersionKey( int contentKey )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tContent, db.tContent.con_cov_lKey, false, db.tContent.con_lKey );
        return getCommonHandler().getInt( sql.toString(), contentKey );
    }
}

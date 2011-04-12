/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalProperties;
import com.enonic.vertical.engine.filters.ContentFilter;
import com.enonic.vertical.engine.handlers.BinaryDataHandler;
import com.enonic.vertical.engine.handlers.CategoryHandler;
import com.enonic.vertical.engine.handlers.CommonHandler;
import com.enonic.vertical.engine.handlers.ContentHandler;
import com.enonic.vertical.engine.handlers.ContentObjectHandler;
import com.enonic.vertical.engine.handlers.GroupHandler;
import com.enonic.vertical.engine.handlers.LanguageHandler;
import com.enonic.vertical.engine.handlers.LogHandler;
import com.enonic.vertical.engine.handlers.MenuHandler;
import com.enonic.vertical.engine.handlers.PageHandler;
import com.enonic.vertical.engine.handlers.PageTemplateHandler;
import com.enonic.vertical.engine.handlers.SectionHandler;
import com.enonic.vertical.engine.handlers.SecurityHandler;
import com.enonic.vertical.engine.handlers.UserHandler;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.core.calendar.CalendarService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.content.binary.BinaryData;
import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.structure.SiteEntity;

public class PresentationEngine
    extends BaseEngine
    implements InitializingBean
{
    private static final Logger LOG = LoggerFactory.getLogger( PresentationEngine.class.getName() );

    private final static int DEFAULT_CONNECTION_TIMEOUT = 2000;

    private BinaryDataHandler binaryDataHandler;

    private CalendarService calendarService;

    private CategoryHandler categoryHandler;

    private CommonHandler commonHandler;

    private ContentHandler contentHandler;

    private ContentObjectHandler contentObjectHandler;

    private GroupHandler groupHandler;

    private LanguageHandler languageHandler;

    private LogHandler logHandler;

    private MenuHandler menuHandler;

    private PageHandler pageHandler;

    private PageTemplateHandler pageTemplateHandler;

    private SectionHandler sectionHandler;

    private SecurityHandler securityHandler;

    private UserHandler userHandler;

    @Autowired
    private SiteDao siteDao;

    public void afterPropertiesSet()
        throws Exception
    {
        init();
    }

    private void init()
    {
        // event listeners
        contentHandler.addListener( logHandler );
        contentHandler.addListener( sectionHandler );
        menuHandler.addListener( logHandler );
    }

    public CategoryHandler getCategoryHandler()
    {
        return categoryHandler;
    }

    public CommonHandler getCommonHandler()
    {
        return commonHandler;
    }

    public ContentHandler getContentHandler()
    {
        return contentHandler;
    }

    public GroupHandler getGroupHandler()
    {
        return groupHandler;
    }

    public LanguageHandler getLanguageHandler()
    {
        return languageHandler;
    }

    public LogHandler getLogHandler()
    {
        return logHandler;
    }

    public PageHandler getPageHandler()
    {
        return pageHandler;
    }

    public PageTemplateHandler getPageTemplateHandler()
    {
        return pageTemplateHandler;
    }

    public SectionHandler getSectionHandler()
    {
        return sectionHandler;
    }

    public SecurityHandler getSecurityHandler()
    {
        return securityHandler;
    }

    public UserHandler getUserHandler()
    {
        return userHandler;
    }

    public BinaryData getBinaryData( User user, int binaryDataKey, long timestamp )
    {

        if ( user == null )
        {
            user = userHandler.getAnonymousUser();
        }

        return binaryDataHandler.getBinaryData( user, binaryDataKey, timestamp );
    }

    public Document getFusionBotQuery( String fusionBotUrl, String query, int siteNum, int page )
    {
        final String MESSAGE_00 = "Failed to query FusionBot search engine";

        Document doc;
        try
        {
            StringBuffer urlStr = new StringBuffer( fusionBotUrl );
            urlStr.append( "?keys=" );
            StringBuffer sb = new StringBuffer( query.replace( ' ', '+' ) );
            for ( int i = 0; i < sb.length(); i++ )
            {
                char c = sb.charAt( i );
                if ( c == '<' )
                {
                    sb.replace( i, i + 1, "&#x3c;" );
                    i += 5;
                }
            }
            urlStr.append( sb.toString() );
            urlStr.append( "&sitenbr=" );
            urlStr.append( siteNum );
            urlStr.append( "&ct=0&xml=1&pos=" );
            urlStr.append( page );

            URL url = new URL( urlStr.toString() );
            URLConnection urlConn = url.openConnection();
            InputStream in = urlConn.getInputStream();

            doc = XMLTool.domparse( in );
        }
        catch ( Exception e )
        {
            LOG.error( StringUtil.expandString( MESSAGE_00, (Object) null,
                                                                                            e ), e );
            doc = null;
        }

        if ( doc == null )
        {
            doc = XMLTool.createDocument( "noresult" );
        }

        return doc;
    }

    private Document getURL( String address, String encoding, int timeoutMs )
    {
        InputStream in = null;
        BufferedReader reader = null;
        Document result;
        try
        {
            URL url = new URL( address );
            URLConnection urlConn = url.openConnection();
            urlConn.setConnectTimeout( timeoutMs > 0 ? timeoutMs : DEFAULT_CONNECTION_TIMEOUT );
            urlConn.setRequestProperty( "User-Agent", VerticalProperties.getVerticalProperties().getDataSourceUserAgent() );
            String userInfo = url.getUserInfo();
            if ( StringUtils.isNotBlank( userInfo ) )
            {
                String userInfoBase64Encoded = new String( Base64.encodeBase64( userInfo.getBytes() ) );
                urlConn.setRequestProperty( "Authorization", "Basic " + userInfoBase64Encoded );
            }
            in = urlConn.getInputStream();

            // encoding == null: XML file
            if ( encoding == null )
            {
                result = XMLTool.domparse( in );
            }
            else
            {
                StringBuffer sb = new StringBuffer( 1024 );
                reader = new BufferedReader( new InputStreamReader( in, encoding ) );
                char[] line = new char[1024];
                int charCount = reader.read( line );
                while ( charCount > 0 )
                {
                    sb.append( line, 0, charCount );
                    charCount = reader.read( line );
                }

                result = XMLTool.createDocument( "urlresult" );
                Element root = result.getDocumentElement();
                XMLTool.createCDATASection( result, root, sb.toString() );
            }
        }
        catch ( SocketTimeoutException ste )
        {
            String message = "Socket timeout when trying to get url: " + address;
            LOG.warn( message);
            result = null;
        }
        catch ( IOException ioe )
        {
            String message = "Failed to get URL: %t";
            LOG.warn( StringUtil.expandString( message, null, ioe ), ioe );
            result = null;
        }
        catch ( RuntimeException re )
        {
            String message = "Failed to get URL: %t";
            LOG.warn( StringUtil.expandString( message, null, re ), re );
            result = null;
        }
        finally
        {
            try
            {
                if ( reader != null )
                {
                    reader.close();
                }
                else if ( in != null )
                {
                    in.close();
                }
            }
            catch ( IOException ioe )
            {
                String message = "Failed to close URL connection: %t";
                LOG.warn( StringUtil.expandString( message, null, ioe ), ioe );
            }
        }

        if ( result == null )
        {
            result = XMLTool.createDocument( "noresult" );
        }

        return result;
    }

    public Document getURLAsXML( String address, int timeout )
    {
        return getURL( address, null, timeout );
    }

    public Document getURLAsText( String address, String encoding, int timeout )
    {
        return getURL( address, encoding, timeout );
    }

    public boolean hasErrorPage( int menuKey )
    {
        return menuHandler.getErrorPage( menuKey ) >= 0;
    }

    public int getErrorPage( int menuKey )
    {
        return menuHandler.getErrorPage( menuKey );
    }

    public Document getSections( User user, SiteKey siteKey )
    {
        SectionCriteria criteria = new SectionCriteria();
        criteria.setTreeStructure( true );
        criteria.setSiteKey( siteKey );
        return sectionHandler.getSections( user, criteria );
    }

    public Document getSections( User user, int superSectionKey, int level, boolean includeSection )
    {
        Document doc;
        if ( superSectionKey >= 0 )
        {
            SectionCriteria criteria = new SectionCriteria();
            criteria.setSuperSectionKey( superSectionKey );
            criteria.setSectionRecursivly( true );
            criteria.setIncludeSection( includeSection );
            criteria.setLevel( level );
            doc = sectionHandler.getSections( user, criteria );
        }
        else
        {
            doc = XMLTool.createDocument( "sections" );
        }

        return doc;
    }

    public int getLoginPage( int menuKey )
    {
        return menuHandler.getLoginPage( menuKey );
    }

    public Document getSuperCategoryNames( int categoryKey, boolean withContentCount, boolean includeCategory )
    {
        return categoryHandler.getSuperCategoryNames( CategoryKey.parse( categoryKey ), withContentCount, includeCategory );
    }

    public Document getSearchBloxQuery( String searchBloxURL, String query, int collectionId, int page, String sort )
    {
        com.enonic.esl.net.URL sbURL = new com.enonic.esl.net.URL( searchBloxURL );
        sbURL.setParameter( "query", query );
        sbURL.setParameter( "col", collectionId );
        sbURL.setParameter( "page", page );
        sbURL.setParameter( "xsl", "xml" );
        if ( sort != null )
        {
            sbURL.setParameter( "sort", sort );
        }

        return getSearchBloxQueryInternal( sbURL );
    }

    public Document getSearchBloxQuery( String searchBloxURL, String queryAll, String queryExactPhrase, String queryLeastOneWord,
                                        String queryWithoutTheWords, String language, String contentType, int startDate, String occurance,
                                        int collectionId, int page, int pageSize, String sort )
    {
        com.enonic.esl.net.URL sbURL = new com.enonic.esl.net.URL( searchBloxURL );
        sbURL.setParameter( "st", "adv" );

        if ( queryAll != null )
        {
            sbURL.setParameter( "q_all", queryAll );
        }
        else
        {
            sbURL.setParameter( "q_all", "" );
        }

        if ( queryExactPhrase != null )
        {
            sbURL.setParameter( "q_phr", queryExactPhrase );
        }
        else
        {
            sbURL.setParameter( "q_phr", "" );
        }

        if ( queryLeastOneWord != null )
        {
            sbURL.setParameter( "q_low", queryLeastOneWord );
        }
        else
        {
            sbURL.setParameter( "q_low", "" );
        }

        if ( queryWithoutTheWords != null )
        {
            sbURL.setParameter( "q_not", queryWithoutTheWords );
        }
        else
        {
            sbURL.setParameter( "q_not", "" );
        }

        if ( language != null )
        {
            sbURL.setParameter( "language", language );
        }

        if ( contentType != null )
        {
            sbURL.setParameter( "contenttype", contentType );
        }

        if ( startDate > 0 )
        {
            sbURL.setParameter( "startdate", startDate );
        }
        else
        {
            sbURL.setParameter( "startdate", 0 );
        }

        if ( occurance != null )
        {
            sbURL.setParameter( "oc", occurance );
        }

        sbURL.setParameter( "col", collectionId );
        sbURL.setParameter( "page", page );

        if ( pageSize > 0 )
        {
            sbURL.setParameter( "pagesize", pageSize );
        }
        sbURL.setParameter( "xsl", "xml" );

        if ( sort != null )
        {
            sbURL.setParameter( "sort", sort );
        }

        return getSearchBloxQueryInternal( sbURL );
    }

    private Document getSearchBloxQueryInternal( com.enonic.esl.net.URL sbURL )
    {
        Document doc;
        try
        {
            URL url = new URL( sbURL.toString() );
            URLConnection urlConn = url.openConnection();
            InputStream in = urlConn.getInputStream();
            doc = XMLTool.domparse( in );
        }
        catch ( MalformedURLException mue )
        {
            String message = "Invalid url: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, mue ), mue );
            doc = null;
        }
        catch ( IOException ioe )
        {
            String message = "Failed to query SearchBlox search engine: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, ioe ), ioe );
            doc = null;
        }
        catch ( RuntimeException re )
        {
            String message = "Failed to query SearchBlox search engine: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, re ), re );
            doc = null;
        }

        if ( doc == null )
        {
            doc = XMLTool.createDocument( "noresult" );
        }

        return doc;
    }


    public Document getCategories( User user, int key, int levels, boolean topLevel, boolean details, boolean catCount,
                                   boolean contentCount )
    {
        return getCategoryHandler().getCategories( checkUser( user ), CategoryKey.parse( key ), levels, topLevel, details, catCount,
                                                   contentCount );
    }

    /**
     * Checks if user is set and returns the user if not null, otherwise returns the anonymous user.
     *
     * @param user The user to check.
     * @return If the user passed in was <code>null</code>, the anonymous user, otherwise, the same user as was passed in.
     */
    private User checkUser( User user )
    {
        if ( user == null )
        {
            return userHandler.getAnonymousUser();
        }
        else
        {
            return user;
        }
    }

    public int getBinaryDataKey( int contentKey, String label )
    {
        return binaryDataHandler.getBinaryDataKey( contentKey, label );
    }

    public String getPathString( int type, int key, boolean includeRoot )
    {
        if ( type == Types.MENUITEM )
        {
            return menuHandler.getPathString( key, includeRoot, false ).toString();
        }
        return null;
    }

    public void setBinaryDataHandler( BinaryDataHandler binaryDataHandler )
    {
        this.binaryDataHandler = binaryDataHandler;
    }

    public void setCalendarService( CalendarService service )
    {
        calendarService = service;
    }

    public void setCategoryHandler( CategoryHandler categoryHandler )
    {
        this.categoryHandler = categoryHandler;
    }

    public void setCommonHandler( CommonHandler commonHandler )
    {
        this.commonHandler = commonHandler;
    }

    public void setContentHandler( ContentHandler contentHandler )
    {
        this.contentHandler = contentHandler;
    }

    public void setContentObjectHandler( ContentObjectHandler contentObjectHandler )
    {
        this.contentObjectHandler = contentObjectHandler;
    }

    public void setGroupHandler( GroupHandler groupHandler )
    {
        this.groupHandler = groupHandler;
    }

    public void setLanguageHandler( LanguageHandler languageHandler )
    {
        this.languageHandler = languageHandler;
    }

    public void setLogHandler( LogHandler logHandler )
    {
        this.logHandler = logHandler;
    }

    public void setMenuHandler( MenuHandler menuHandler )
    {
        this.menuHandler = menuHandler;
    }

    public void setPageHandler( PageHandler pageHandler )
    {
        this.pageHandler = pageHandler;
    }

    public void setPageTemplateHandler( PageTemplateHandler pageTemplateHandler )
    {
        this.pageTemplateHandler = pageTemplateHandler;
    }

    public void setSectionHandler( SectionHandler sectionHandler )
    {
        this.sectionHandler = sectionHandler;
    }

    public void setSecurityHandler( SecurityHandler securityHandler )
    {
        this.securityHandler = securityHandler;
    }

    public void setUserHandler( UserHandler userHandler )
    {
        this.userHandler = userHandler;
    }


    public boolean siteExists( SiteKey siteKey )
    {
        SiteEntity site = siteDao.findByKey( siteKey.toInt() );
        return ( site != null );
    }

    public String getContents( User user, int[] contentKeys, int parentLevel, int childrenLevel, int parentChildrenLevel,
                               boolean includeAccessRights, boolean includeUserRights, ContentFilter contentFilter )
    {

        if ( user == null )
        {
            user = userHandler.getAnonymousUser();
        }

        Document doc =
            contentHandler.getContents( user, contentKeys, true, false, parentLevel, childrenLevel, parentChildrenLevel, false, false,
                                        contentFilter );

        if ( includeAccessRights || includeUserRights )
        {
            securityHandler.appendAccessRights( user, doc, includeAccessRights, includeUserRights );
        }

        return XMLTool.documentToString( doc );
    }
}

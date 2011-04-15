/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalProperties;
import com.enonic.vertical.engine.handlers.UserHandler;

import com.enonic.cms.framework.blob.BlobStoreObject;

import com.enonic.cms.core.content.ContentAccessType;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.core.content.binary.access.BinaryAccessResolver;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.store.dao.BinaryDataDao;
import com.enonic.cms.store.dao.ContentBinaryDataDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.domain.SiteKey;

public class PresentationEngine
    extends BaseEngine
{
    private static final Logger LOG = LoggerFactory.getLogger( PresentationEngine.class.getName() );

    private final static int DEFAULT_CONNECTION_TIMEOUT = 2000;

    @Inject
    protected ContentBinaryDataDao contentBinaryDataDao;

    @Inject
    protected SecurityService securityService;

    @Inject
    protected ContentDao contentDao;

    @Inject
    protected BinaryDataDao binaryDataDao;

    @Inject
    private UserHandler userHandler;

    @Inject
    private SiteDao siteDao;

    @Inject
    private BinaryAccessResolver binaryAccessResolver;

    public BinaryData getBinaryData( User user, int binaryDataKey, long timestamp )
    {

        if ( user == null )
        {
            user = userHandler.getAnonymousUser();
        }

        UserEntity newUser = securityService.getUser( user );
        BinaryDataEntity binaryData = binaryDataDao.findByKey( new BinaryDataKey( binaryDataKey ) );
        if ( !binaryAccessResolver.hasReadAndIsAccessibleOnline( binaryData, newUser, new DateTime() ) )
        {
            return null;
        }

        ContentBinaryDataEntity contentBinaryData = contentBinaryDataDao.findByBinaryKey( binaryData.getKey() );
        ContentVersionEntity contentVersion = contentBinaryData.getContentVersion();
        ContentEntity content = contentVersion.getContent();

        // fast check if anonymous have read
        UserEntity anonymousUser = securityService.getUser( securityService.getAnonymousUserKey() );
        boolean anonAccess = content.hasAccessRightSet( anonymousUser.getUserGroup(), ContentAccessType.READ );
        return getBinaryData( contentBinaryData, anonAccess, timestamp );
    }

    private BinaryData getBinaryData( ContentBinaryDataEntity contentBinaryData, boolean anonAccess, long timestamp )
    {
        BinaryData binaryData = new BinaryData();
        binaryData.key = contentBinaryData.getBinaryData().getKey();
        binaryData.contentKey = contentBinaryData.getContentVersion().getContent().getKey().toInt();
        binaryData.setSafeFileName( contentBinaryData.getBinaryData().getName() );
        binaryData.timestamp = contentBinaryData.getBinaryData().getCreatedAt();
        binaryData.anonymousAccess = anonAccess;

        if ( binaryData.timestamp.getTime() > timestamp )
        {
            BlobStoreObject blob = this.binaryDataDao.getBlob( contentBinaryData.getBinaryData() );
            binaryData.data = blob.getData();
        }

        return binaryData;
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
        int result;
        SiteEntity entity = siteDao.findByKey( menuKey );
        if ( ( entity == null ) || ( entity.getErrorPage() == null ) )
        {
            result = -1;
        }
        else
        {
            result = entity.getErrorPage().getKey();
        }
        return result >= 0;
    }

    public int getErrorPage( int menuKey )
    {
        SiteEntity entity = siteDao.findByKey( menuKey );
        if ( ( entity == null ) || ( entity.getErrorPage() == null ) )
        {
            return -1;
        }
        else
        {
            return entity.getErrorPage().getKey();
        }
    }

    public int getLoginPage( int menuKey )
    {
        SiteEntity entity = siteDao.findByKey( menuKey );
        if ( ( entity == null ) || ( entity.getLoginPage() == null ) )
        {
            return -1;
        }
        else
        {
            return entity.getLoginPage().getKey();
        }
    }

    public int getBinaryDataKey( int contentKey, String label )
    {
        ContentEntity content = contentDao.findByKey( new ContentKey( contentKey ) );
        if ( content != null )
        {
            BinaryDataEntity binaryData = content.getMainVersion().getSingleBinaryData( label );
            if ( binaryData != null )
            {
                return binaryData.getKey();
            }
        }
        return -1;
    }

    public boolean siteExists( SiteKey siteKey )
    {
        SiteEntity site = siteDao.findByKey( siteKey.toInt() );
        return ( site != null );
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.mvc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.cms.core.SitePathResolver;
import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.UrlPathHelperManager;
import com.enonic.cms.core.security.AutoLoginService;
import com.enonic.cms.core.structure.SiteService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserType;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.matches;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

//@RunWith( SpringJUnit4ClassRunner.class )
//@ContextConfiguration ( loader = XmlWebApplicationContextLoader.class )
//@TransactionConfiguration( defaultRollback = true )
//@Transactional
public abstract class AttachmentControllerTest
    extends AbstractControllerTest
{

    @Autowired
    private AttachmentController controller;

    private SiteService siteService;

    private SitePropertiesService sitePropertiesService;

    private AutoLoginService autoLoginService;

    //@Before

    public void setup()
        throws Exception
    {

        initSystemData();

        SitePathResolver sitePathResolver = new SitePathResolver();

        UrlPathHelperManager urlPathHelperManager = new UrlPathHelperManager();
        sitePropertiesService = createMock( SitePropertiesService.class );
        urlPathHelperManager.setSitePropertiesService( sitePropertiesService );
        sitePathResolver.setSitePathPrefix( "/site" );
        sitePathResolver.setUrlPathHelperManager( urlPathHelperManager );

        siteService = createMock( SiteService.class );
        autoLoginService = createMock( AutoLoginService.class );

        // set up return values for sitePropertyService
        expect( sitePropertiesService.getProperty( matches( "cms.site.url.defaultCharacterEncoding" ), isA( SiteKey.class ) ) ).andReturn(
            "UTF-8" ).times( 1 );
        // siteService method is then called
        siteService.checkSiteExist( isA( SiteKey.class ) );

        // autologin should return anonymous user
        UserEntity userEntity = new UserEntity();
        userEntity.setType( UserType.ANONYMOUS );
//        expect( autoLoginService.autologinWithAllMeans( isA( SiteKey.class ), isA( HttpServletRequest.class ),
//                                                        isA( HttpServletResponse.class ) ) ).andReturn( userEntity ).times( 1 );
        // create mock for binaryService
        controller.setSitePathResolver( sitePathResolver );
        controller.setSiteService( siteService );
        controller.setAutoLoginService( autoLoginService );

        replay( sitePropertiesService );
        replay( siteService );
        replay( autoLoginService );

    }

//    @Test

    //    @ExpectedException( InvalidBinaryPathException.class )

    public void invalidPathTooFewArguments()
        throws Exception
    {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setMethod( "GET" );
        request.setRequestURI( "http://localhost/site/0/binary" );

        controller.handleRequest( request, response );

    }

    //@Test
    /*public void urlWithBinaryId()
        throws Exception
    {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setMethod( "GET" );
        request.setRequestURI( "http://localhost/site/0/binary" );
        request.setParameter( "id", "1" );

        Resource file = new ClassPathResource( "test.jpg" );
        byte[] content = getBytesFromFile( file.getFile() );
        BinaryEntity binaryEntity = new BinaryEntity();
        binaryEntity.setData( content );
        binaryEntity.setKey( 1 );
        CachedObject cachedObject = new CachedObject( binaryEntity );

        expect( binaryService.getBinary( isA( SiteKey.class ), isA( UserEntity.class ), isA( BinaryDataKey.class ) ) ).andReturn(
            cachedObject ).times( 1 );

        BinaryDataEntity binaryDataEntity = new BinaryDataEntity();
        binaryDataEntity.setCreatedAt( new Date( 2009, 1, 1 ) );
        binaryDataEntity.setKey( 1 );
        binaryDataEntity.setName( "test.jpg" );
        binaryDataEntity.setSize( content.length );
        expect( binaryService.getBinaryData( isA( BinaryDataKey.class ) ) ).andReturn( binaryDataEntity ).times( 1 );

        replay( binaryService );

        // call the method to test
        controller.handleRequest( request, response );

        verify( binaryService );
        verify( sitePropertiesService );
        verify( siteService );
        verify( autoLoginService );

    }*/

    //@Test

    public void urlWithBinaryIdAndDownloadTrue()
    {

    }

    //@Test

    public void urlWithContentKeyAndLabel()
    {

    }

    //@Test

    public void urlWithContentKeyAndLabelAndDownloadTrue()
    {

    }

    public byte[] getBytesFromFile( File file )
        throws IOException
    {
        InputStream is = new FileInputStream( file );

        long length = file.length();

        if ( length > Integer.MAX_VALUE )
        {
            // File is too large
        }

        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead;
        while ( offset < bytes.length && ( numRead = is.read( bytes, offset, bytes.length - offset ) ) >= 0 )
        {
            offset += numRead;
        }

        if ( offset < bytes.length )
        {
            throw new IOException( "Could not completely read file " + file.getName() );
        }

        is.close();
        return bytes;
    }

}

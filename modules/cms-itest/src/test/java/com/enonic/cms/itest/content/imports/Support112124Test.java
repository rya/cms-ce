package com.enonic.cms.itest.content.imports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.hibernate3.HibernateTemplate;

import junit.framework.Assert;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.command.ImportContentCommand;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.core.content.imports.ImportJob;
import com.enonic.cms.core.content.imports.ImportJobFactory;
import com.enonic.cms.core.content.imports.ImportResult;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

import static org.junit.Assert.*;

public class Support112124Test
    extends AbstractSpringTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private DomainFixture fixture;

    @Autowired
    private ImportJobFactory importJobFactory;

    @Before
    public void setUp()
        throws IOException
    {
        String kontaktContentTypeXml = resourceToString(
            new ClassPathResource( Support112124Test.class.getName().replace( ".", "/" ) + "-innholdstype-kontakt.xml" ) );
        String statistikkContentTypeXml = resourceToString(
            new ClassPathResource( Support112124Test.class.getName().replace( ".", "/" ) + "-innholdstype-statistikk.xml" ) );

        fixture = new DomainFixture( hibernateTemplate );
        DomainFactory factory = new DomainFactory( fixture );

        fixture.initSystemData();

        fixture.createAndStoreNormalUserWithUserGroup( "testuser", "Test user", "testuserstore" );

        fixture.save( factory.createContentHandler( "MyHandler", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        fixture.save( factory.createContentType( "kontaktCty", ContentHandlerName.CUSTOM.getHandlerClassShortName(),
                                                 XMLDocumentFactory.create( kontaktContentTypeXml ).getAsJDOMDocument() ) );
        fixture.save( factory.createContentType( "statistikkCty", ContentHandlerName.CUSTOM.getHandlerClassShortName(),
                                                 XMLDocumentFactory.create( statistikkContentTypeXml ).getAsJDOMDocument() ) );

        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "Kontakt", "kontaktCty", "MyUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "Kontakt", "testuser", "read, create, approve" ) );

        fixture.save( factory.createCategory( "Statistikk", "statistikkCty", "MyUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "Statistikk", "testuser", "read, create, approve" ) );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        PortalSecurityHolder.setUser( fixture.findUserByName( "testuser" ).getKey() );
        PortalSecurityHolder.setImpersonatedUser( fixture.findUserByName( "testuser" ).getKey() );
    }

    @Test
    public void importing_related_content()
        throws IOException
    {
        ImportResult result = doImport( "Kontakt", "SRKontaktListeImport", "kontakter.xml" );

        // verify
        assertEquals( 4, result.getInserted().size() );
        assertEquals( 4, fixture.countAllContent() );

        assertEquals( 1, fixture.countContentVersionsByTitle( "Bibliotek og Informasjonssenteret" ) );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Ingen Mann" ) );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Ell Emelle" ) );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Inte Nett" ) );

        // import 1 pass
        result = doImport( "Statistikk", "SRImport", "statistikk.xml" );
        assertEquals( 1, result.getInserted().size() );
        assertEquals( 0, result.getUpdated().size() );
        assertEquals( 5, fixture.countAllContent() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Veitrafikkulykker" ) );

        ContentEntity content = fixture.findContentByKey( new ContentKey( "5" ) );
        Collection<ContentKey> actualOrder = getRelatedContentKeys( content, "kontakter" );
        assertOrderedEquals( "0: 2, 1: 4, 2: 3", actualOrder );

        // reimport the same
        result = doImport( "Statistikk", "SRImport", "statistikk.xml" );
        assertEquals( 1, result.getSkipped().size() );
        assertEquals( 5, fixture.countAllContent() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Veitrafikkulykker" ) );

        content = fixture.findContentByKey( new ContentKey( "5" ) );
        actualOrder = getRelatedContentKeys( content, "kontakter" );

        assertOrderedEquals( "0: 2, 1: 4, 2: 3", actualOrder );

        // import with new order
        result = doImport( "Statistikk", "SRImport", "statistikk-endret.xml" );
        assertEquals( 1, result.getUpdated().size() );
        assertEquals( 5, fixture.countAllContent() );
        assertEquals( 2, fixture.countContentVersionsByTitle( "Veitrafikkulykker" ) );

        fixture.flushAndClearHibernateSesssion();
    }

    private ImportResult doImport( String categoryName, String importName, String fileName )
        throws IOException
    {
        String importData =
            resourceToString( new ClassPathResource( Support112124Test.class.getName().replace( ".", "/" ) + "-" + fileName ) );

        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( categoryName );
        command.importName = importName;
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        return result;
    }

    private Collection<ContentKey> getRelatedContentKeys( ContentEntity content, String fieldName )
    {
        ContentVersionEntity contentVersion = content.getMainVersion();
        CustomContentData contentData = (CustomContentData) contentVersion.getContentData();
        RelatedContentsDataEntry relatedContents = (RelatedContentsDataEntry) contentData.getEntry( fieldName );

        return relatedContents.getRelatedContentKeys();
    }

    private String resourceToString( Resource resource )
        throws IOException
    {
        return IOUtils.toString( resource.getInputStream() );
    }

    private static void assertOrderedEquals( String expected, Collection<ContentKey> actualSet )
    {
        String[] actual = new String[3];
        int i = 0;

        for ( ContentKey contentKey : actualSet )
        {
            actual[i++] = contentKey.toString();
        }

        Assert.assertEquals( expected, arrayToString( actual ) );
    }

    private static String arrayToString( Object[] a )
    {
        StringBuilder result = new StringBuilder();

        for ( int i = 0; i < a.length; i++ )
        {
            result.append( i ).append( ": " ).append( a[i] );
            if ( i < a.length - 1 )
            {
                result.append( ", " );
            }
        }

        return result.toString();
    }
}

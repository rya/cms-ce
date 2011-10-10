/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import org.junit.Test;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration

//@TransactionConfiguration(defaultRollback = true)
//@Transactional
public class ResourceServiceImplTest
{

    @Test
    public void dummyTest()
    {

    }

    /* @Autowired
    @Qualifier("resourceService")
    private ResourceService resourceService;

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private PageTemplateDao pageTemplateDao;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private PortletDao contentObjectDao;

    @Autowired
    private LanguageDao languageDao;

    @Autowired
    private ContentTypeDao contentTypeDao;


    @AfterTransaction
    public void afterTransaction()
    {
        deleteResourceTree();
    }

    //@Test
    public void testMoveSourceMissing()
    {
        createResourceTree( new String[]{"/a", "/b"} );

        ResourceFile source = resourceService.getResourceRoot().getFile( "/a/a.txt" );
        ResourceFolder destination = resourceService.getResourceRoot().getFolder( "/b" );
        try
        {
            resourceService.moveResource( source, destination );
            Assert.fail( "Expected IllegalArgumentException" );
        }
        catch ( RuntimeException e )
        {
            Assert.assertTrue( e instanceof IllegalArgumentException );
        }
    }

    // @Test
    public void testMoveDestinationMissing()
    {

        createResourceTree( new String[]{"/a", "/a/a.txt", "/b"} );

        ResourceFile source = resourceService.getResourceRoot().getFile( "/a/a.txt" );
        ResourceFolder destination = resourceService.getResourceRoot().getFolder( "/b" );
        resourceService.getResourceRoot().removeFolder( "/b" );
        try
        {
            resourceService.moveResource( source, destination );
            Assert.fail( "Expected IllegalArgumentException" );
        }
        catch ( RuntimeException e )
        {
            Assert.assertTrue( e.getCause() instanceof IllegalArgumentException );
        }
    }

    // @Test
    public void testMoveDestinationAllreadyContainSource()
    {
        createResourceTree( new String[]{"/a", "/a/a.txt", "/b", "/b/a.txt"} );

        ResourceFile source = resourceService.getResourceRoot().getFile( "/a/a.txt" );
        ResourceFolder destination = resourceService.getResourceRoot().getFolder( "/b" );
        try
        {
            resourceService.moveResource( source, destination );
            Assert.fail( "Expected IllegalArgumentException" );
        }
        catch ( RuntimeException e )
        {
            Assert.assertTrue( e.getCause() instanceof IllegalArgumentException );
        }
    }

    // @Test
    public void testMoveSourceFileEqualsDestinationFile()
    {
        createResourceTree( new String[]{"/a", "/a/a.txt"} );

        ResourceFile source = resourceService.getResourceRoot().getFile( "/a/a.txt" );
        ResourceFolder destination = resourceService.getResourceRoot().getFolder( "/a" );
        try
        {
            resourceService.moveResource( source, destination );
            Assert.fail( "Expected IllegalArgumentException" );
        }
        catch ( RuntimeException e )
        {
            Assert.assertTrue( e.getCause() instanceof IllegalArgumentException );
        }
    }

    // @Test
    public void testMoveSourceFolderEqualsDestinationFolder()
    {
        createResourceTree( new String[]{"/a", "/a/a.txt"} );

        ResourceFolder source = resourceService.getResourceRoot().getFolder( "/a" );
        ResourceFolder destination = resourceService.getResourceRoot().getFolder( "/" );
        try
        {
            resourceService.moveResource( source, destination );
            Assert.fail( "Expected IllegalArgumentException" );
        }
        catch ( RuntimeException e )
        {
            Assert.assertTrue( e.getCause() instanceof IllegalArgumentException );
        }
    }

    // @Test
    public void testMoveFolderToSubFolder()
    {
        createResourceTree( new String[]{"/a", "/a/a.txt", "/a/b", "/a/b/b.txt"} );

        ResourceFolder source = resourceService.getResourceRoot().getFolder( "/a" );
        ResourceFolder destination = resourceService.getResourceRoot().getFolder( "/a/b" );
        try
        {
            resourceService.moveResource( source, destination );
            Assert.fail( "Expected IllegalArgumentException" );
        }
        catch ( RuntimeException e )
        {
            Assert.assertTrue( e.getCause() instanceof IllegalArgumentException );
        }
    }

    //@Test
    public void testMovePageTemplateReferedFile()
    {
        //SetupTest
        createResourceTree( new String[]{"/a", "/a/a.txt", "/b"} );

        printResourceFolder( resourceService.getResourceRoot() );

        int pageTemplateKey = 123;
        ResourceKey cssKey = new ResourceKey( "/a/a.txt" );
        ResourceKey styleKey = new ResourceKey( "/a/a.txt" );

        PageTemplateEntity pageTemplate = createPageTemplate( pageTemplateKey, "testName", cssKey, styleKey, PageTemplateType.CONTENT );
        pageTemplateDao.store( pageTemplate );

        //ExecuteTest
        ResourceFile source = resourceService.getResourceRoot().getFile( "/a/a.txt" );
        ResourceFolder destination = resourceService.getResourceRoot().getFolder( "/b" );
        resourceService.moveResource( source, destination );
        hibernateTemplate.flush();
        hibernateTemplate.clear();

        //VerifyTest
        verifyResourceTree( new String[]{"/a", "/b", "/b/a.txt"} );

        PageTemplateEntity pageTemplate2 = pageTemplateDao.findByKey( pageTemplateKey );
        ResourceKey cssKey2 = new ResourceKey( "/b/a.txt" );
        ResourceKey styleKey2 = new ResourceKey( "/b/a.txt" );
        Assert.assertEquals( cssKey2, pageTemplate2.getCssKey() );
        Assert.assertEquals( styleKey2, pageTemplate2.getStyleKey() );
    }


    //@Test
    public void testMovePageTemplateReferedFolder()
    {
        //SetupTest
        createResourceTree( new String[]{"/a", "/b", "/a/c", "/a/c/c.txt", "/a/c/d", "/a/c/d/d.txt"} );

        int key = 123;
        ResourceKey cssKey = new ResourceKey( "/a/c/c.txt" );
        ResourceKey styleKey = new ResourceKey( "/a/c/d/d.txt" );

        PageTemplateEntity pageTemplate = createPageTemplate( key, "testName", cssKey, styleKey, PageTemplateType.CONTENT );
        pageTemplateDao.store( pageTemplate );

        //ExecuteTest
        ResourceFolder source = resourceService.getResourceRoot().getFolder( "/a/c" );
        ResourceFolder destination = resourceService.getResourceRoot().getFolder( "/b" );
        resourceService.moveResource( source, destination );
        hibernateTemplate.flush();
        hibernateTemplate.clear();

        //VerifyTest
        verifyResourceTree( new String[]{"/a", "/b", "/b/c", "/b/c/c.txt", "/b/c/d", "/b/c/d/d.txt"} );

        PageTemplateEntity pageTemplate2 = pageTemplateDao.findByKey( key );
        ResourceKey cssKey2 = new ResourceKey( "/b/c/c.txt" );
        ResourceKey styleKey2 = new ResourceKey( "/b/c/d/d.txt" );
        Assert.assertEquals( cssKey2, pageTemplate2.getCssKey() );
        Assert.assertEquals( styleKey2, pageTemplate2.getStyleKey() );
    }

    //@Test
    public void testMoveContentObjectReferedFile()
    {
        //SetupTest
        createResourceTree( new String[]{"/a", "/a/a.txt", "/b"} );

        int key = 123;
        ResourceKey styleKey = new ResourceKey( "/a/a.txt" );
        ResourceKey borderKey = new ResourceKey( "/a/a.txt" );

        PortletEntity contentObject = createContentObject( key, "testName", styleKey, borderKey );
        contentObjectDao.store( contentObject );

        //ExecuteTest
        ResourceFile source = resourceService.getResourceRoot().getFile( "/a/a.txt" );
        ResourceFolder destination = resourceService.getResourceRoot().getFolder( "/b" );
        resourceService.moveResource( source, destination );
        hibernateTemplate.flush();
        hibernateTemplate.clear();

        //VerifyTest
        verifyResourceTree( new String[]{"/a", "/b", "/b/a.txt"} );

        PortletEntity contentObject2 = contentObjectDao.findByKey( key );
        ResourceKey styleKey2 = new ResourceKey( "/b/a.txt" );
        ResourceKey borderKey2 = new ResourceKey( "/b/a.txt" );
        Assert.assertEquals( styleKey2, contentObject2.getStyleKey() );
        Assert.assertEquals( borderKey2, contentObject2.getBorderKey() );
    }

    //@Test
    public void testMoveResourceContentObjectReferedFolder()
    {
        //SetupTest
        createResourceTree( new String[]{"/a", "/b", "/a/c", "/a/c/c.txt", "/a/c/d", "/a/c/d/d.txt"} );

        int key = 123;
        ResourceKey styleKey = new ResourceKey( "/a/c/c.txt" );
        ResourceKey borderKey = new ResourceKey( "/a/c/d/d.txt" );

        PortletEntity contentObject = createContentObject( key, "testName", styleKey, borderKey );
        contentObjectDao.store( contentObject );

        //ExecuteTest
        ResourceFolder source = resourceService.getResourceRoot().getFolder( "/a/c" );
        ResourceFolder destination = resourceService.getResourceRoot().getFolder( "/b" );
        resourceService.moveResource( source, destination );
        hibernateTemplate.flush();
        hibernateTemplate.clear();

        //VerifyTest
        verifyResourceTree( new String[]{"/a", "/b", "/b/c", "/b/c/c.txt", "/b/c/d", "/b/c/d/d.txt"} );

        PortletEntity contentObject2 = contentObjectDao.findByKey( key );
        ResourceKey styleKey2 = new ResourceKey( "/b/c/c.txt" );
        ResourceKey borderKey2 = new ResourceKey( "/b/c/d/d.txt" );
        Assert.assertEquals( styleKey2, contentObject2.getStyleKey() );
        Assert.assertEquals( borderKey2, contentObject2.getBorderKey() );
    }

    //@Test
    public void testMoveContentTypeReferedFile()
    {
        //SetupTest
        createResourceTree( new String[]{"/a", "/a/a.txt", "/b"} );

        int key = 123;
        ResourceKey cssKey = new ResourceKey( "/a/a.txt" );

        ContentHandlerEntity contentHandler = createContentHandler( 245, "testHandlerName", "testClassName" );
        hibernateTemplate.save( contentHandler );
        ContentTypeEntity contentType = createContentType( key, "testName", cssKey, contentHandler );
        contentTypeDao.store( contentType );

        //ExecuteTest
        ResourceFile source = resourceService.getResourceRoot().getFile( "/a/a.txt" );
        ResourceFolder destination = resourceService.getResourceRoot().getFolder( "/b" );
        resourceService.moveResource( source, destination );
        hibernateTemplate.flush();
        hibernateTemplate.clear();

        //VerifyTest
        verifyResourceTree( new String[]{"/a", "/b", "/b/a.txt"} );

        ContentTypeEntity contentType2 = contentTypeDao.findByKey( key );
        ResourceKey cssKey2 = new ResourceKey( "/b/a.txt" );
        Assert.assertEquals( cssKey2, contentType2.getDefaultCssKey() );
    }

    //@Test
    public void testMoveContentTypeReferedFolder()
    {

        //SetupTest
        createResourceTree( new String[]{"/a", "/b", "/a/c", "/a/c/d", "/a/c/d/d.txt"} );

        int key = 123;
        ResourceKey cssKey = new ResourceKey( "/a/c/d/d.txt" );

        ContentHandlerEntity contentHandler = createContentHandler( 245, "testHandlerName", "testClassName" );
        hibernateTemplate.save( contentHandler );
        ContentTypeEntity contentType = createContentType( key, "testName", cssKey, contentHandler );
        contentTypeDao.store( contentType );

        //ExecuteTest
        ResourceFolder source = resourceService.getResourceRoot().getFolder( "/a/c" );
        ResourceFolder destination = resourceService.getResourceRoot().getFolder( "/b" );
        resourceService.moveResource( source, destination );
        hibernateTemplate.flush();
        hibernateTemplate.clear();

        //VerifyTest
        verifyResourceTree( new String[]{"/a", "/b", "/b/c", "/b/c/d", "/b/c/d/d.txt"} );

        ContentTypeEntity contentType2 = contentTypeDao.findByKey( key );
        ResourceKey cssKey2 = new ResourceKey( "/b/c/d/d.txt" );
        Assert.assertEquals( cssKey2, contentType2.getDefaultCssKey() );
    }

    //@Test
    public void testMoveSiteReferedFile()
    {
        //SetupTest
        createResourceTree( new String[]{"/a", "/a/a.txt", "/b"} );

        int key = 123;
        ResourceKey cssKey = new ResourceKey( "/a/a.txt" );

        LanguageEntity language = createLanguage( "testCode" );
        languageDao.store( language );
        SiteEntity site = createSite( key, "testName", cssKey, language );
        siteDao.store( site );

        //ExecuteTest
        ResourceFile source = resourceService.getResourceRoot().getFile( "/a/a.txt" );
        ResourceFolder destination = resourceService.getResourceRoot().getFolder( "/b" );
        resourceService.moveResource( source, destination );
        hibernateTemplate.flush();
        hibernateTemplate.clear();

        //VerifyTest
        verifyResourceTree( new String[]{"/a", "/b", "/b/a.txt"} );

        SiteEntity site2 = siteDao.findByKey( key );
        ResourceKey cssKey2 = new ResourceKey( "/b/a.txt" );
        Assert.assertEquals( cssKey2, site2.getDefaultCssKey() );
    }

    //@Test
    public void testMoveSiteReferedFolder()
    {
        //SetupTest
        createResourceTree( new String[]{"/a", "/b", "/a/c", "/a/c/d", "/a/c/d/d.txt"} );

        int key = 123;
        ResourceKey cssKey = new ResourceKey( "/a/c/d/d.txt" );

        LanguageEntity language = createLanguage( "testCode" );
        languageDao.store( language );
        SiteEntity site = createSite( key, "testName", cssKey, language );
        siteDao.store( site );

        //ExecuteTest
        ResourceFolder source = resourceService.getResourceRoot().getFolder( "/a/c" );
        ResourceFolder destination = resourceService.getResourceRoot().getFolder( "/b" );
        resourceService.moveResource( source, destination );
        hibernateTemplate.flush();
        hibernateTemplate.clear();

        //VerifyTest
        verifyResourceTree( new String[]{"/a", "/b", "/b/c", "/b/c/d", "/b/c/d/d.txt"} );

        SiteEntity site2 = siteDao.findByKey( key );
        ResourceKey cssKey2 = new ResourceKey( "/b/c/d/d.txt" );
        Assert.assertEquals( cssKey2, site2.getDefaultCssKey() );
    }

    private ContentTypeEntity createContentType( int key, String name, ResourceKey cssKey, ContentHandlerEntity handler )
    {
        ContentTypeEntity contentType = new ContentTypeEntity();
        contentType.setKey( key );
        contentType.setName( name );
        contentType.setTimestamp( new Date() );
        contentType.setDefaultCssKey( cssKey );
        contentType.setHandler( handler );
        return contentType;
    }

    private ContentHandlerEntity createContentHandler( int key, String name, String className )
    {
        ContentHandlerEntity contentHandler = new ContentHandlerEntity();
        contentHandler.setKey( new ContentHandlerKey( key ) );
        contentHandler.setName( name );
        contentHandler.setTimestamp( new Date() );
        contentHandler.setClassName( className );
        return contentHandler;
    }

    private PortletEntity createContentObject( int key, String name, ResourceKey styleKey, ResourceKey borderKey )
    {
        PortletEntity contentObject = new PortletEntity();
        contentObject.setKey( key );
        contentObject.setStyleKey( styleKey );
        contentObject.setBorderKey( borderKey );
        contentObject.setCreated( new Date() );
        contentObject.setName( name );
        contentObject.setRunAs( RunAsType.INHERIT );
        return contentObject;
    }

    private PageTemplateEntity createPageTemplate( int key, String name, ResourceKey cssKey, ResourceKey styleKey, PageTemplateType type )
    {
        PageTemplateEntity pageTemplate = new PageTemplateEntity();
        pageTemplate.setKey( key );
        pageTemplate.setCssKey( cssKey );
        pageTemplate.setStyleKey( styleKey );
        pageTemplate.setName( name );
        pageTemplate.setTimestamp( new Date() );
        pageTemplate.setType( type );
        pageTemplate.setRunAs( RunAsType.INHERIT );
        return pageTemplate;
    }

    private SiteEntity createSite( int key, String name, ResourceKey cssKey, LanguageEntity language )
    {
        SiteEntity site = new SiteEntity();
        site.setKey( key );
        site.setName( name );
        String initXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<menudata><pagetypes><allow type=\"url\"/><allow type=\"label\"/><allow type=\"section\"/></pagetypes><defaultcss key=\"/x/x.txt\"/></menudata>";
        site.setXmlData( initXml.getBytes() );
        site.setDefaultCssKey( cssKey );
        site.setTimestamp( new Date() );
        site.setLanguage( language );
        return site;
    }

    private LanguageEntity createLanguage( String code )
    {
        LanguageEntity language = new LanguageEntity();
        language.setCode( code );
        language.setTimestamp( new Date() );
        return language;
    }

    private void createResourceTree( String[] tree )
    {
        ResourceFolder root = resourceService.getResourceRoot();
        for ( String node : tree )
        {
            if ( node.endsWith( ".txt" ) )
            {
                root.createFile( node );
            }
            else
            {
                root.createFolder( node );
            }
        }
    }

    private void verifyResourceTree( String[] tree )
    {
        ResourceFolder root = resourceService.getResourceRoot();
        for ( String node : tree )
        {
            if ( node.endsWith( ".txt" ) )
            {
                ResourceFile f = root.getFile( node );
                Assert.assertEquals( node, f.getPath() );
            }
            else
            {
                ResourceFolder f = root.getFolder( node );
                Assert.assertEquals( node, f.getPath() );
            }
        }
    }

    private void deleteResourceTree()
    {
        ResourceFolder root = resourceService.getResourceRoot();
        for ( ResourceFile file : root.getFiles() )
        {
            root.removeFile( file.getName() );
        }
        for ( ResourceFolder folder : root.getFolders() )
        {
            root.removeFolder( folder.getName() );
        }
    }

    private void printResourceFolder( ResourceFolder folder )
    {
        for ( ResourceFile file : folder.getFiles() )
        {
            System.out.println( file.getPath() );
        }
        for ( ResourceFolder subFolder : folder.getFolders() )
        {
            System.out.println( subFolder.getPath() );
            printResourceFolder( subFolder );
        }
    }*/
}
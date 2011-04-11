/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.domain.resource.ResourceFile;
import com.enonic.cms.domain.resource.ResourceFolder;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ResourceDaoTest
{
    private static final Logger LOG = LoggerFactory.getLogger( ResourceDaoTest.class.getName() );

    @Autowired
    private ResourceDao resourceDao;

    @Test
    public void testOperations()
        throws Exception
    {

        // Retreive root
        ResourceFolder root = resourceDao.getResourceRoot();
        assertEquals( "/", root.getPath() );

        // Folders

        ResourceFolder a = root.createFolder( "a" );
        assertEquals( "/a", a.getPath() );
        assertEquals( "a", a.getName() );

        ResourceFolder b = a.createFolder( "b" );
        assertEquals( "/a/b", b.getPath() );
        assertEquals( "b", b.getName() );

        b = root.getFolder( "/a/b" );
        assertEquals( "/a/b", b.getPath() );
        assertEquals( "b", b.getName() );

        a = b.getParentFolder();
        assertEquals( "/a", a.getPath() );
        assertEquals( "a", a.getName() );

        assertEquals( "", a.getParentFolder().getName() );
        ResourceFolder c = a.createFolder( "c" );
        assertEquals( "/a/c", c.getPath() );
        assertEquals( "c", c.getName() );

        List<ResourceFolder> rootList = root.getFolders();
        assertEquals( 1, rootList.size() );
        List<ResourceFolder> aList = rootList.get( 0 ).getFolders();
        assertEquals( 2, aList.size() );
/*
        assertEquals(a.getPath(), rootList.get(0).getPath());
        assertEquals(b.getPath(), aList.get(0).getPath());
        assertEquals(c.getPath(), aList.get(1).getPath());
*/
        try
        {
            root.getFolder( "/a/1/2/3" );
        }
        catch ( Exception ex )
        {
            assertEquals( DataRetrievalFailureException.class, ex.getClass() );
        }

        ResourceFolder e = root.createFolder( "/a/b/c/d/e" );
        assertEquals( "/a/b/c/d/e", e.getPath() );
        assertEquals( "e", e.getName() );

        ResourceFolder d = e.getParentFolder();
        assertEquals( "/a/b/c/d", d.getPath() );
        assertEquals( "d", d.getName() );

        // Files

        ResourceFile f1 = a.createFile( "f1.txt" );
        assertEquals( "/a/f1.txt", f1.getPath() );
        assertEquals( "f1.txt", f1.getName() );
        assertEquals( 0, f1.getSize() );

        Calendar t = Calendar.getInstance();
        Thread.sleep( 100 );

        f1.setData( "content" );

        LOG.info( f1.getDataAsString() );
        assertEquals( "content", f1.getDataAsString() );
        assertEquals( "content".length(), f1.getSize() );
        assertTrue( f1.getLastModified().getTimeInMillis() > t.getTimeInMillis() );

        ResourceFile f2 = a.getFile( "f1.txt" );
        assertEquals( f1.getDataAsString(), f2.getDataAsString() );
        assertEquals( f1.getMimeType(), f2.getMimeType() );
        assertEquals( f1.getLastModified(), f2.getLastModified() );
        assertEquals( f1.getSize(), f2.getSize() );

        t = f1.getLastModified();
        Thread.sleep( 100 );

        f2.setData( "updated content" );
        assertEquals( "updated content".length(), f2.getSize() );
        assertTrue( f2.getLastModified().getTimeInMillis() > t.getTimeInMillis() );

        assertEquals( "updated content", f1.getDataAsString() );

        assertEquals( f1.getDataAsString(), f2.getDataAsString() );
        assertEquals( f1.getMimeType(), f2.getMimeType() );
        assertEquals( f1.getMimeType(), f2.getMimeType() );
        assertEquals( f1.getLastModified(), f2.getLastModified() );
        assertEquals( f1.getSize(), f2.getSize() );

        ResourceFile f3 = root.createFile( "/a/b/f3.txt" );
        assertEquals( "/a/b/f3.txt", f3.getPath() );
        assertEquals( "f3.txt", f3.getName() );
        assertEquals( 0, f3.getSize() );

        f3 = root.getFile( "/a/b/f3.txt" );
        assertEquals( "/a/b/f3.txt", f3.getPath() );
        assertEquals( "f3.txt", f3.getName() );
        assertEquals( 0, f3.getSize() );

        try
        {
            root.getFile( "/a/1/2/f4.txt" );
        }
        catch ( Exception ex )
        {
            assertEquals( DataRetrievalFailureException.class, ex.getClass() );
        }

        root.removeFile( "/a/b/f3.txt" );

        try
        {
            f3.getPath();
        }
        catch ( Exception ex )
        {
            assertEquals( ConcurrencyFailureException.class, ex.getClass() );
        }

        try
        {
            root.getFile( "/a/b/f3.txt" );
        }
        catch ( Exception ex )
        {
            assertEquals( DataRetrievalFailureException.class, ex.getClass() );
        }

        ResourceFile f6 = root.createFile( "/1/2/3/4/5.txt" );
        assertEquals( "/1/2/3/4/5.txt", f6.getPath() );
        assertEquals( "5.txt", f6.getName() );
        assertEquals( 0, f6.getSize() );

        try
        {
            root.createFile( "/1/2/3/4/5.txt" );
        }
        catch ( Exception ex )
        {
            assertEquals( DataIntegrityViolationException.class, ex.getClass() );
        }
    }


    @Test
    public void testMoveOperations()
        throws Exception
    {
        // Retreive root
        ResourceFolder root = resourceDao.getResourceRoot();
        assertEquals( "/", root.getPath() );

        //Create destination folder
        ResourceFolder aaFolder = root.createFolder( "aa" );
        assertEquals( "/aa", aaFolder.getPath() );
        assertEquals( "aa", aaFolder.getName() );

        //Create source folder
        ResourceFolder bbFolder = root.createFolder( "bb" );
        assertEquals( "/bb", bbFolder.getPath() );
        assertEquals( "bb", bbFolder.getName() );

        //Create a file in source folder
        ResourceFile ccFile = bbFolder.createFile( "cc.txt" );
        assertEquals( "/bb/cc.txt", ccFile.getPath() );
        assertEquals( "cc.txt", ccFile.getName() );
        assertEquals( 0, ccFile.getSize() );

        Calendar t = Calendar.getInstance();
        Thread.sleep( 100 );

        ccFile.setData( "cc" );
        LOG.info( ccFile.getDataAsString() );
        assertEquals( "cc", ccFile.getDataAsString() );
        assertEquals( "cc".length(), ccFile.getSize() );
        long ccTime = ccFile.getLastModified().getTimeInMillis();
        assertTrue( ccTime > t.getTimeInMillis() );

        //Create a sub folder in source folder
        ResourceFolder ddFolder = bbFolder.createFolder( "dd" );
        assertEquals( "/bb/dd", ddFolder.getPath() );
        assertEquals( "dd", ddFolder.getName() );

        //Create a file in source sub folder
        ResourceFile eeFile = ddFolder.createFile( "ee.txt" );
        assertEquals( "/bb/dd/ee.txt", eeFile.getPath() );
        assertEquals( "ee.txt", eeFile.getName() );
        assertEquals( 0, eeFile.getSize() );

        t = Calendar.getInstance();
        Thread.sleep( 100 );

        eeFile.setData( "ee" );

        LOG.info( eeFile.getDataAsString() );

        assertEquals( "ee", eeFile.getDataAsString() );
        assertEquals( "ee".length(), eeFile.getSize() );
        long eeTime = eeFile.getLastModified().getTimeInMillis();
        assertTrue( eeTime > t.getTimeInMillis() );

        //move source folder with content (both file and folder content) to destination folder
        bbFolder.moveTo( aaFolder );

        //check folder structure
        bbFolder = root.getFolder( "/aa/bb" );
        assertEquals( "/aa/bb", bbFolder.getPath() );
        assertEquals( "bb", bbFolder.getName() );

        ccFile = root.getFile( "/aa/bb/cc.txt" );
        assertEquals( "/aa/bb/cc.txt", ccFile.getPath() );
        assertEquals( "cc.txt", ccFile.getName() );
        assertEquals( "cc", ccFile.getDataAsString() );
        assertEquals( "cc".length(), ccFile.getSize() );
        //assertEquals(ccFile.getLastModified().getTimeInMillis(), ccTime);
        //Files will get new date (move = copy and delete)

        ddFolder = root.getFolder( "/aa/bb/dd" );
        assertEquals( "/aa/bb/dd", ddFolder.getPath() );
        assertEquals( "dd", ddFolder.getName() );

        eeFile = root.getFile( "/aa/bb/dd/ee.txt" );
        assertEquals( "/aa/bb/dd/ee.txt", eeFile.getPath() );
        assertEquals( "ee.txt", eeFile.getName() );
        assertEquals( "ee", eeFile.getDataAsString() );
        assertEquals( "ee".length(), eeFile.getSize() );
        //assertEquals(eeFile.getLastModified().getTimeInMillis(), eeTime);
        //Files will get new date (move = copy and delete)
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;

import org.apache.commons.fileupload.FileItem;

import com.enonic.esl.io.zip.ZipEntry;
import com.enonic.esl.io.zip.ZipFile;
import com.enonic.esl.io.zip.ZipInputStream;
import com.enonic.esl.util.RegexpUtil;

public class FileUtil
{

    private final static int BUF_SIZE = 8092;

    /**
     * Returns the contents of the file in a byte array.
     *
     * @param file the file info
     * @return a byte array containing the file's contents
     * @throws IOException
     */
    public static byte[] getBytesFromFile( File file )
        throws IOException
    {
        InputStream is = new FileInputStream( file );

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if ( length > Integer.MAX_VALUE )
        {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while ( offset < bytes.length && ( numRead = is.read( bytes, offset, bytes.length - offset ) ) >= 0 )
        {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if ( offset < bytes.length )
        {
            throw new IOException( "Could not completely read file " + file.getName() );
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    /**
     * Get bytes from an input stream.
     *
     * @param inputStream the input stream
     * @param sizeHint    a hint of the stream's size
     * @return a byte array containing the stream's contents
     * @throws IOException
     */
    public static byte[] getBytesFromStream( InputStream inputStream, int sizeHint )
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream( sizeHint );
        int numRead = 0;
        byte[] buffer = new byte[16 * 1024];
        while ( ( numRead = inputStream.read( buffer ) ) >= 0 )
        {
            baos.write( buffer, 0, numRead );
        }
        return baos.toByteArray();
    }

    /**
     * Inflates a zip into a directory. Creates the directory and/or its parents if they don't exists.
     *
     * @param zipFile ZipFile zip file to inflate
     * @param dir     File destination directory
     */
    public static void inflateZipFile( ZipFile zipFile, File dir )
        throws IOException
    {
        Enumeration entries = zipFile.entries();
        while ( entries.hasMoreElements() )
        {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            inflateFile( dir, zipFile, entry );
        }
    }

    public static void inflateZipFile( ZipInputStream zipIn, File dir, String filterRegExp )
        throws IOException
    {
        ZipEntry zipEntry = zipIn.getNextEntry();
        while ( zipEntry != null )
        {
            if ( !RegexpUtil.match( zipEntry.getName(), filterRegExp ).matches() )
            {
                inflateFile( dir, zipIn, zipEntry );
            }
            zipEntry = zipIn.getNextEntry();
        }
    }

    private static void inflateFile( File dir, InputStream is, ZipEntry entry )
        throws IOException
    {
        String entryName = entry.getName();
        File f = new File( dir, entryName );

        if ( entryName.endsWith( "/" ) )
        {
            f.mkdirs();
        }
        else
        {
            File parentDir = f.getParentFile();
            if ( parentDir != null && !parentDir.exists() )
            {
                parentDir.mkdirs();
            }
            FileOutputStream os = null;
            try
            {
                os = new FileOutputStream( f );
                byte[] buffer = new byte[BUF_SIZE];
                for ( int n = 0; ( n = is.read( buffer ) ) > 0; )
                {
                    os.write( buffer, 0, n );
                }
            }
            finally
            {
                if ( os != null )
                {
                    os.close();
                }
            }
        }
    }

    private static void inflateFile( File dir, ZipFile zipFile, ZipEntry entry )
        throws IOException
    {
        InputStream is = null;
        try
        {
            is = zipFile.getInputStream( entry );
            inflateFile( dir, is, entry );
        }
        finally
        {
            if ( is != null )
            {
                is.close();
            }
        }
    }

    public static File createTempDir( String name )
    {
        File dir = new File( System.getProperty( "java.io.tmpdir" ), name );
        dir.mkdir();
        return dir;
    }

    /**
     * Does recursive delete. If directory, checks for subdirectories/-files and deletes all of them. If file, deletes the file.
     *
     * @param f File file or directory to delete
     * @return boolean true if delettion was succesful, false otherwise
     */
    public static boolean recursiveDelete( File f )
    {
        boolean success = true;
        if ( f.isDirectory() )
        {
            File[] files = f.listFiles();
            if ( files.length > 0 )
            {
                for ( int i = 0; i < files.length; i++ )
                {
                    success &= recursiveDelete( files[i] );
                    if ( !success )
                    {
                        break;
                    }
                }
            }
        }
        return success && f.delete();
    }

    public static String getFileName( FileItem fileItem )
    {
        String fileName = fileItem.getName();
        StringTokenizer nameTokenizer = new StringTokenizer( fileName, "\\/:" );
        while ( nameTokenizer.hasMoreTokens() )
        {
            fileName = nameTokenizer.nextToken();
        }
        return fileName;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.fileupload.FileItem;
import com.enonic.esl.util.RegexpUtil;

public class FileUtil
{

    private final static int BUF_SIZE = 8092;

    public static void inflateZipFile( InputStream in, File dir, String filterRegExp )
        throws IOException
    {
        final ZipInputStream zipIn = new ZipInputStream(in);

        ZipEntry zipEntry = zipIn.getNextEntry();
        while ( zipEntry != null )
        {
            if ( !RegexpUtil.match( zipEntry.getName(), filterRegExp ).matches() )
            {
                inflateFile( dir, zipIn, zipEntry );
            }
            zipEntry = zipIn.getNextEntry();
        }

        zipIn.close();
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

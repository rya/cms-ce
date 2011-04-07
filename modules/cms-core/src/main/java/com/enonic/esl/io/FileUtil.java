/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.io;

import java.util.StringTokenizer;
import org.apache.commons.fileupload.FileItem;

public class FileUtil
{
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

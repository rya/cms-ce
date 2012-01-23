/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

public interface MimeTypeResolver
{
    String getMimeType( String fileName );

    String getMimeTypeByExtension( String ext );

    String getExtension( String mimeType );
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import java.util.List;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.PathAndParams;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentKey;

public abstract class AttachmentRequestResolver
{

    public AttachmentRequest resolveBinaryDataKey( PathAndParams localPathAndParams )
    {

        String keyStr = localPathAndParams.getParameter( "id" );

        if ( keyStr != null )
        {
            throw new InvalidBinaryPathException( localPathAndParams.getPath(), "Parameter 'id' no longer supported" );
        }

        Path binaryPath = localPathAndParams.getPath();

        if ( binaryPath.isEmpty() )
        {
            throw new InvalidBinaryPathException( null, "Path is empty." );
        }

        List<String> pathList = binaryPath.getPathElements();

        if ( pathList.size() < 2 )
        {
            throw new InvalidBinaryPathException( binaryPath, "Too few arguments on path." );
        }

        AttachmentNativeLinkKey nativeKey = AttachmentNativeLinkKeyInPathParser.resolveFromUrlPath( localPathAndParams.getPath() );

        if ( nativeKey instanceof AttachmentNativeLinkKeyWithBinaryKey )
        {
            return handleAttachmentNativeLinkKeyWithBinary( (AttachmentNativeLinkKeyWithBinaryKey) nativeKey, binaryPath );
        }
        else if ( nativeKey instanceof AttachmentNativeLinkKeyWithLabel )
        {
            return handleAttachmentNativeLinkKeyWithLabel( (AttachmentNativeLinkKeyWithLabel) nativeKey, binaryPath );
        }
        else if ( nativeKey instanceof AttachmentNativeLinkKey )
        {
            return handleAttachmentNativeLinkKey( nativeKey, binaryPath );
        }

        throw new InvalidBinaryPathException( binaryPath, "Unable to resolve path." );


    }

    private AttachmentRequest handleAttachmentNativeLinkKeyWithBinary( AttachmentNativeLinkKeyWithBinaryKey nativeKey, Path binaryPath )
    {
        if ( nativeKey.getBinaryKey() == null )
        {
            throw new InvalidBinaryPathException( binaryPath, "Unable to resolve path. Binary key not specified." );
        }

        return new AttachmentRequest( nativeKey, nativeKey.getBinaryKey() );
    }

    private AttachmentRequest handleAttachmentNativeLinkKeyWithLabel( AttachmentNativeLinkKeyWithLabel nativeKey, Path binaryPath )
    {
        String label = nativeKey.getLabel();

        checkLabel( binaryPath, label );

        ContentEntity content = getContent( nativeKey.getContentKey() );
        if ( content == null )
        {
            throw AttachmentNotFoundException.notFound( binaryPath.toString() );
        }

        checkContentType( binaryPath, content );

        BinaryDataKey binaryDataKey = getBinaryData( content, label );

        if ( binaryDataKey == null )
        {
            throw AttachmentNotFoundException.notFound( binaryPath.toString() );
        }
        return new AttachmentRequest( nativeKey, binaryDataKey );
    }

    private AttachmentRequest handleAttachmentNativeLinkKey( AttachmentNativeLinkKey nativeKey, Path binaryPath )
    {
        ContentEntity content = getContent( nativeKey.getContentKey() );
        if ( content == null )
        {
            throw AttachmentNotFoundException.notFound( binaryPath.toString() );
        }

        checkContentType( binaryPath, content );

        String label = "source";
        BinaryDataKey binaryDataKey = getBinaryData( content, label );

        if ( binaryDataKey == null )
        {
            throw AttachmentNotFoundException.notFound( binaryPath.toString() );
        }
        return new AttachmentRequest( nativeKey, binaryDataKey );
    }

    private void checkLabel( Path binaryPath, String label )
    {
        if ( "file".equals( label ) )
        {
            throw new InvalidBinaryPathException( binaryPath, "Label 'file' no longer supported. Use '/label/<label>' instead." );
        }

        if ( !label.equals( "small" ) && !label.equals( "medium" ) && !label.equals( "large" ) && !label.equals( "source" ) )
        {
            throw new InvalidBinaryPathException( binaryPath, "Unsupported label '" + label + "'" );
        }
    }

    private void checkContentType( Path binaryPath, ContentEntity content )
    {
        ContentHandlerName ctyName = content.getContentType().getContentHandlerName();
        if ( !ContentHandlerName.FILE.equals( ctyName ) && !ContentHandlerName.IMAGE.equals( ctyName ) )
        {
            throw new InvalidBinaryPathException( binaryPath,
                                                  "Content is of type " + ctyName.name() + ".  Excpected type 'file' or 'image'." );
        }
    }

    protected abstract BinaryDataKey getBinaryData( ContentEntity content, String label );

    protected abstract ContentEntity getContent( ContentKey contentKey );
}


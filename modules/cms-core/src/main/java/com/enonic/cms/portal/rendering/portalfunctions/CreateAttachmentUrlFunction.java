/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.portalfunctions;

import java.util.Set;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.binary.*;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.portal.instruction.CreateAttachmentUrlInstruction;
import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.domain.Path;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.binary.AttachmentNativeLinkKey;
import com.enonic.cms.core.content.binary.AttachmentNativeLinkKeyWithBinaryKey;
import com.enonic.cms.core.content.binary.AttachmentNativeLinkKeyWithLabel;
import com.enonic.cms.core.content.binary.BinaryDataKey;

/**
 * Jan 21, 2010
 */
public class CreateAttachmentUrlFunction
{
    private ContentDao contentDao;

    public CreateAttachmentUrlInstruction createAttachmentUrl( String nativeLinkKey, String[] params, MenuItemKey menuItemKey )
    {
        Path nativeLinkKeyAsPath = new Path( nativeLinkKey );
        AttachmentNativeLinkKey nativeKey = (AttachmentNativeLinkKey) AttachmentNativeLinkKeyParser.parse( nativeLinkKeyAsPath );
        if ( nativeKey == null )
        {
            final String failureReason = "the attachment key is not valid: " + nativeLinkKey;
            throw new PortalFunctionException( failureReason );
        }

        ContentEntity content = checkContentKey( nativeKey.getContentKey() );

        if ( nativeKey instanceof AttachmentNativeLinkKeyWithBinaryKey )
        {
            AttachmentNativeLinkKeyWithBinaryKey binaryNativeLinkKey = (AttachmentNativeLinkKeyWithBinaryKey) nativeKey;
            checkBinaryKey( binaryNativeLinkKey.getBinaryKey(), content );
        }
        else if ( nativeKey instanceof AttachmentNativeLinkKeyWithLabel )
        {
            String label = ( (AttachmentNativeLinkKeyWithLabel) nativeKey ).getLabel();
            checkLabel( label );
            checkContentType( content );
        }

        CreateAttachmentUrlInstruction instruction = new CreateAttachmentUrlInstruction();
        instruction.setNativeLinkKey( nativeLinkKey );
        instruction.setParams( params );

        if ( menuItemKey != null )
        {
            instruction.setRequestedMenuItemKey( menuItemKey.toString() );
        }

        return instruction;
    }

    private ContentEntity checkContentKey( ContentKey contentKey )
    {
        ContentEntity entity = contentDao.findByKey( contentKey );
        if ( entity == null )
        {
            throw new IllegalArgumentException( "Unknown content key: " + contentKey );
        }
        return entity;
    }

    private void checkBinaryKey( BinaryDataKey binaryKey, ContentEntity contentEntity )
    {
        if ( binaryKey == null )
        {
            throw new IllegalArgumentException( "Argument is not a valid binary key: " + binaryKey );
        }

        Set<BinaryDataKey> binaryDataKeys = contentEntity.getMainVersion().getContentBinaryDataKeys();
        if ( !binaryDataKeys.contains( binaryKey ) )
        {
            throw new IllegalArgumentException( "Invalid binary key: " + binaryKey + " for content key: " + contentEntity.getKey() );
        }
    }

    private void checkLabel( String label )
    {
        // JIRA VS-2554 File label skal ikke fungere lenger
        if ( "file".equals( label ) )
        {
            throw new IllegalArgumentException( "Label 'file' no longer supported. Use '/label/<label>' instead." );
        }

        if ( !label.equals( "small" ) && !label.equals( "medium" ) && !label.equals( "large" ) && !label.equals( "source" ) )
        {
            throw new IllegalArgumentException( "Unsupported label '" + label + "'" );
        }
    }

    private void checkContentType( ContentEntity content )
    {
        ContentHandlerName ctyName = content.getContentType().getContentHandlerName();
        if ( !ContentHandlerName.FILE.equals( ctyName ) && !ContentHandlerName.IMAGE.equals( ctyName ) )
        {
            throw new IllegalArgumentException( "Content is of type " + ctyName.name() + ".  Expected type 'file' or 'image'." );
        }
    }

    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}

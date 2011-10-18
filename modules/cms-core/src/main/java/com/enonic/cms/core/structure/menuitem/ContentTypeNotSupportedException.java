package com.enonic.cms.core.structure.menuitem;


import com.enonic.cms.core.content.contenttype.ContentTypeEntity;

public class ContentTypeNotSupportedException
    extends RuntimeException
{
    public ContentTypeNotSupportedException( ContentTypeEntity contentType, MenuItemEntity section )
    {
        super( buildMessage( contentType, section ) );
    }

    private static String buildMessage( ContentTypeEntity contentType, MenuItemEntity section )
    {
        return "Content of type '" + contentType.getName() + "' not supported for section: " + section.getPathAsString();
    }
}

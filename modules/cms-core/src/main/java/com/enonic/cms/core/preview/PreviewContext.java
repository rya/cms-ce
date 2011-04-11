/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preview;

import com.google.common.base.Preconditions;

/**
 * Sep 29, 2010
 */
public class PreviewContext
{
    public static final PreviewContext NO_PREVIEW = new PreviewContext();

    private ContentPreviewContext contentPreviewContext;

    private MenuItemPreviewContext menuItemPreviewContext;

    private PreviewContext()
    {
    }

    public PreviewContext( ContentPreviewContext context )
    {
        this.contentPreviewContext = context;
    }

    public PreviewContext( MenuItemPreviewContext context )
    {
        this.menuItemPreviewContext = context;
    }

    public boolean isPreviewing()
    {
        return isPreviewingContent() || isPreviewingMenuItem();
    }

    public boolean isPreviewingContent()
    {
        return contentPreviewContext != null;
    }

    public boolean isPreviewingMenuItem()
    {
        return menuItemPreviewContext != null;
    }

    public ContentPreviewContext getContentPreviewContext()
    {
        Preconditions.checkNotNull( contentPreviewContext, "Unexpected call when not previewing a content" );
        return contentPreviewContext;
    }

    public MenuItemPreviewContext getMenuItemPreviewContext()
    {
        Preconditions.checkNotNull( menuItemPreviewContext, "Unexpected call when not previewing a menuitem" );
        return menuItemPreviewContext;
    }
}

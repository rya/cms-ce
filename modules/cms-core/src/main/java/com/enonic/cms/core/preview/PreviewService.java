/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preview;

/**
 * Sep 30, 2010
 */
public interface PreviewService
{
    boolean isInPreview();

    PreviewContext getPreviewContext();

    void setPreviewContext( PreviewContext previewContext );
}

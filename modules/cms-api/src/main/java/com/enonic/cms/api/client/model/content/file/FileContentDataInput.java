/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content.file;

import java.io.Serializable;

public class FileContentDataInput
    implements Serializable
{

    private static final long serialVersionUID = 6131548337474176616L;

    public FileNameInput name;

    public FileDescriptionInput description;

    public FileKeywordsInput keywords;

    public FileBinaryInput binary;

    public String getContentHandlerName()
    {
        return "ContentFileHandlerServlet";
    }

}

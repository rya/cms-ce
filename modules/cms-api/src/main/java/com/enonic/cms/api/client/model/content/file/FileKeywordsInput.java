/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content.file;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.api.client.model.content.AbstractInput;
import com.enonic.cms.api.client.model.content.InputType;

public class FileKeywordsInput
    extends AbstractInput
    implements Serializable
{

    private static final long serialVersionUID = -3751570560346446119L;

    private List<String> keywords = new ArrayList<String>();

    public FileKeywordsInput()
    {
        super( InputType.KEYWORDS, "keywords" );
    }

    public FileKeywordsInput addKeyword( String value )
    {
        keywords.add( value );
        return this;
    }

    public List<String> getKeywords()
    {
        return keywords;
    }
}

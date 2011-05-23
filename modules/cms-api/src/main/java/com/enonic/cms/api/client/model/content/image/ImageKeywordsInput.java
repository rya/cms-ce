package com.enonic.cms.api.client.model.content.image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.api.client.model.content.AbstractInput;
import com.enonic.cms.api.client.model.content.InputType;

public class ImageKeywordsInput
        extends AbstractInput
        implements Serializable
{

    private List<String> keywords = new ArrayList<String>();

    public ImageKeywordsInput()
    {
        super( InputType.KEYWORDS, "keywords" );
    }

    public ImageKeywordsInput addKeyword( String value )
    {
        keywords.add( value );
        return this;
    }

    public List<String> getKeywords()
    {
        return keywords;
    }
}
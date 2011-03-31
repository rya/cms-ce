/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RelatedContentsInput
    extends AbstractInput
    implements Serializable
{

    private static final long serialVersionUID = -5440598741791755353L;

    private List<RelatedContentInput> relatedContents = new ArrayList<RelatedContentInput>();

    public RelatedContentsInput( String name )
    {
        super( InputType.RELATED_CONTENTS, name );
    }

    public RelatedContentsInput addRelatedContent( Integer contentKey )
    {
        relatedContents.add( new RelatedContentInput( getName(), contentKey ) );
        return this;
    }

    public List<RelatedContentInput> getRelatedContents()
    {
        return relatedContents;
    }
}
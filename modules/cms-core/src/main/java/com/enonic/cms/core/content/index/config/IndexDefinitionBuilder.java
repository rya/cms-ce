/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.config;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;

/**
 * This class implements a builder for index definitions.
 */
public final class IndexDefinitionBuilder
{
    /**
     * Build a single index definition.
     */
    private IndexDefinition buildSingle( Element elem )
    {
        String xpath = elem.getAttributeValue( "xpath" );
        if ( xpath == null )
        {
            /* Xpath missing - invalid index def */
            return null;
        }

        String name = elem.getAttributeValue( "name" );
        if ( name == null )
        {
            /* Name missing - old index def */
            return new IndexDefinition( IndexPathHelper.transformName( xpath ), IndexPathHelper.transformOldPath( xpath ) );
        }

        /* Name present - new index def */
        return new IndexDefinition( name, IndexPathHelper.transformNewPath( xpath ) );
    }

    /**
     * Build a set of index definitions.
     */
    public List<IndexDefinition> buildList( Element elem )
    {
        ArrayList<IndexDefinition> list = new ArrayList<IndexDefinition>();
        for ( Object child : elem.getChildren( "index" ) )
        {
            IndexDefinition def = buildSingle( (Element) child );
            if ( def != null )
            {
                list.add( def );
            }
        }

        return list;
    }

    /**
     * Build a set of index definitions.
     */
    public List<IndexDefinition> buildList( ContentTypeEntity contentType )
    {
        return buildList( contentType.getIndexingParametersXML() );
    }
}

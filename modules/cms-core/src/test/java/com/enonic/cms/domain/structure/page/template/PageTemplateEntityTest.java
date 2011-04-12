/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.structure.page.template;

import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.portal.datasource.Datasources;
import org.jdom.Document;
import org.jdom.Element;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Nov 26, 2010
 */
public class PageTemplateEntityTest
{
    @Test
    public void getDatasources_returns_empty_datasources_object_even_if_pagetemplatedata_element_does_not_contain_the_datasources_element()
    {
        // setup
        PageTemplateEntity pageTemplate = new PageTemplateEntity();
        pageTemplate.setXmlData( new Document( new Element( "pagetemplatedata" ) ) );

        // exercise
        Datasources actualDatasources = pageTemplate.getDatasources();

        // verify
        assertNotNull( actualDatasources );
        assertEquals( 0, actualDatasources.getDatasourceElements().size() );
    }
}

package com.enonic.cms.core.structure.page.template;


import org.jdom.Element;

public class PageTemplateXmlCreator
{
    public Element createPageTemlateElement( PageTemplateEntity pageTemplate )
    {
        return doCreatePageTemplateElement( pageTemplate );
    }

    private Element doCreatePageTemplateElement( final PageTemplateEntity pageTemplate )
    {
        Element pageTemplateEl = new Element( "page-template" );
        pageTemplateEl.setAttribute( "key", String.valueOf( pageTemplate.getKey() ) );
        pageTemplateEl.addContent( new Element( "name" ).addContent( pageTemplate.getName() ) );
        pageTemplateEl.addContent( new Element( "description" ).addContent( pageTemplate.getDescription() ) );

        Element typeEl = new Element( "type" );
        typeEl.setAttribute( "key", String.valueOf( pageTemplate.getType().getKey() ) );
        typeEl.addContent( new Element( "name" ).addContent( pageTemplate.getType().getName() ) );
        pageTemplateEl.addContent( typeEl );

        return pageTemplateEl;
    }

}

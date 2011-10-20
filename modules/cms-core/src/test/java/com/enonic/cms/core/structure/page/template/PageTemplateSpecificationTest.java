package com.enonic.cms.core.structure.page.template;


import org.junit.Test;

import static org.junit.Assert.*;

public class PageTemplateSpecificationTest
{

    @Test
    public void satisfies_returns_true_when_nothing_specified()
    {
        PageTemplateEntity pageTemplate = new PageTemplateEntity();

        PageTemplateSpecification spec = new PageTemplateSpecification();

        assertEquals( true, spec.satisfies( pageTemplate ) );
    }

    @Test
    public void satisfies_returns_true_when_key_is_same()
    {
        PageTemplateEntity pageTemplate = new PageTemplateEntity();
        pageTemplate.setKey( 1 );

        PageTemplateSpecification spec = new PageTemplateSpecification();
        spec.setKey( new PageTemplateKey( 1 ) );

        assertEquals( true, spec.satisfies( pageTemplate ) );
    }

    @Test
    public void satisfies_returns_false_when_key_is_not_same()
    {
        PageTemplateEntity pageTemplate = new PageTemplateEntity();
        pageTemplate.setKey( 1 );

        PageTemplateSpecification spec = new PageTemplateSpecification();
        spec.setKey( new PageTemplateKey( 2 ) );

        assertEquals( false, spec.satisfies( pageTemplate ) );
    }

    @Test
    public void satisfies_returns_true_when_pageTemplate_have_same_type()
    {
        PageTemplateEntity pageTemplate = new PageTemplateEntity();
        pageTemplate.setType( PageTemplateType.SECTIONPAGE );

        PageTemplateSpecification spec = new PageTemplateSpecification();
        spec.setType( PageTemplateType.SECTIONPAGE );

        assertEquals( true, spec.satisfies( pageTemplate ) );
    }

    @Test
    public void satisfies_returns_false_when_pageTemplate_not_have_same_type()
    {
        PageTemplateEntity pageTemplate = new PageTemplateEntity();
        pageTemplate.setType( PageTemplateType.CONTENT );

        PageTemplateSpecification spec = new PageTemplateSpecification();
        spec.setType( PageTemplateType.SECTIONPAGE );

        assertEquals( false, spec.satisfies( pageTemplate ) );
    }

    @Test
    public void satisfies_returns_true_when_pageTemplate_have_same_type_and_key()
    {
        PageTemplateEntity pageTemplate = new PageTemplateEntity();
        pageTemplate.setKey( 1 );
        pageTemplate.setType( PageTemplateType.SECTIONPAGE );

        PageTemplateSpecification spec = new PageTemplateSpecification();
        spec.setType( PageTemplateType.SECTIONPAGE );
        spec.setKey( new PageTemplateKey( 1 ) );

        assertEquals( true, spec.satisfies( pageTemplate ) );
    }

    @Test
    public void satisfies_returns_false_when_pageTemplate_have_same_type_bot_not_same_key()
    {
        PageTemplateEntity pageTemplate = new PageTemplateEntity();
        pageTemplate.setKey( 2 );
        pageTemplate.setType( PageTemplateType.SECTIONPAGE );

        PageTemplateSpecification spec = new PageTemplateSpecification();
        spec.setType( PageTemplateType.SECTIONPAGE );
        spec.setKey( new PageTemplateKey( 1 ) );

        assertEquals( false, spec.satisfies( pageTemplate ) );
    }


}

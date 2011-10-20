/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.xmlbuilders;

public class ContentXMLBuildersSpringManagedBeansBridge
{
    private static ContentXMLBuildersSpringManagedBeansBridge instance;

    private ContentBaseXMLBuilder contentBaseXMLBuilder;

    private ContentArticle3XMLBuilder contentArticle3XMLBuilder;

    private ContentDocumentXMLBuilder contentDocumentXMLBuilder;

    private ContentEnhancedImageXMLBuilder contentEnhancedImageXMLBuilder;

    private ContentFileXMLBuilder contentFileXMLBuilder;

    private ContentNewsletterXMLBuilder contentNewsletterXMLBuilder;

    private ContentPollXMLBuilder contentPollXMLBuilder;

    private SimpleContentXMLBuilder simpleContentXMLBuilder;


    public ContentXMLBuildersSpringManagedBeansBridge()
    {
        instance = this;
    }


    public static ContentBaseXMLBuilder getContentBaseXMLBuilder()
    {
        return instance.contentBaseXMLBuilder;
    }

    public void setContentBaseXMLBuilder( ContentBaseXMLBuilder contentBaseXMLBuilder )
    {
        this.contentBaseXMLBuilder = contentBaseXMLBuilder;
    }

    public static ContentArticle3XMLBuilder getContentArticle3XMLBuilder()
    {
        return instance.contentArticle3XMLBuilder;
    }

    public void setContentArticle3XMLBuilder( ContentArticle3XMLBuilder contentArticle3XMLBuilder )
    {
        this.contentArticle3XMLBuilder = contentArticle3XMLBuilder;
    }

    public static ContentDocumentXMLBuilder getContentDocumentXMLBuilder()
    {
        return instance.contentDocumentXMLBuilder;
    }

    public void setContentDocumentXMLBuilder( ContentDocumentXMLBuilder contentDocumentXMLBuilder )
    {
        this.contentDocumentXMLBuilder = contentDocumentXMLBuilder;
    }

    public static ContentEnhancedImageXMLBuilder getContentEnhancedImageXMLBuilder()
    {
        return instance.contentEnhancedImageXMLBuilder;
    }

    public void setContentEnhancedImageXMLBuilder( ContentEnhancedImageXMLBuilder contentEnhancedImageXMLBuilder )
    {
        this.contentEnhancedImageXMLBuilder = contentEnhancedImageXMLBuilder;
    }

    public static ContentFileXMLBuilder getContentFileXMLBuilder()
    {
        return instance.contentFileXMLBuilder;
    }

    public void setContentFileXMLBuilder( ContentFileXMLBuilder contentFileXMLBuilder )
    {
        this.contentFileXMLBuilder = contentFileXMLBuilder;
    }

    public static ContentNewsletterXMLBuilder getContentNewsletterXMLBuilder()
    {
        return instance.contentNewsletterXMLBuilder;
    }

    public void setContentNewsletterXMLBuilder( ContentNewsletterXMLBuilder contentNewsletterXMLBuilder )
    {
        this.contentNewsletterXMLBuilder = contentNewsletterXMLBuilder;
    }

    public static ContentPollXMLBuilder getContentPollXMLBuilder()
    {
        return instance.contentPollXMLBuilder;
    }

    public void setContentPollXMLBuilder( ContentPollXMLBuilder contentPollXMLBuilder )
    {
        this.contentPollXMLBuilder = contentPollXMLBuilder;
    }

    public static SimpleContentXMLBuilder getSimpleContentXMLBuilder()
    {
        return instance.simpleContentXMLBuilder;
    }

    public void setSimpleContentXMLBuilder( SimpleContentXMLBuilder simpleContentXMLBuilder )
    {
        this.simpleContentXMLBuilder = simpleContentXMLBuilder;
    }

}

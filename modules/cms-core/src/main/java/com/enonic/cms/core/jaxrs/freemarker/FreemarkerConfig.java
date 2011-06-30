package com.enonic.cms.core.jaxrs.freemarker;

import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

@Component
public final class FreemarkerConfig
    extends Configuration implements ServletContextAware
{
    public void setServletContext(final ServletContext context)
    {
        setTemplateLoader(new WebappTemplateLoader(context, "/WEB-INF/freemarker"));
    }
}

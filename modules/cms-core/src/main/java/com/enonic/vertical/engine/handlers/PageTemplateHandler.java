/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.enonic.esl.xml.XMLTool;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.domain.CalendarUtil;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateKey;
import com.enonic.cms.domain.structure.page.template.PageTemplatePortletEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateRegionEntity;
import com.enonic.cms.domain.structure.page.template.PageTemplateType;
import com.enonic.cms.domain.structure.portlet.PortletEntity;

public final class PageTemplateHandler
    extends BaseHandler
{

    public PageTemplateType getPageTemplateType( PageTemplateKey pageTemplateKey )
    {
        PageTemplateEntity entity = pageTemplateDao.findByKey( pageTemplateKey.toInt() );
        if ( entity == null )
        {
            return null;
        }
        return entity.getType();
    }

}

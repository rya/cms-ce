/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateKey;
import com.enonic.cms.core.structure.page.template.PageTemplateType;

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

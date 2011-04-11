/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.domain.CalendarUtil;
import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.LanguageKey;

public final class LanguageHandler
    extends BaseHandler
{

    public String getLanguageCode( LanguageKey languageKey )
    {
        LanguageEntity entity = languageDao.findByKey( languageKey );
        return entity != null ? entity.getCode() : null;
    }

}

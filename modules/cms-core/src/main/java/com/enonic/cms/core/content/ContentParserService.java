/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.List;

import javax.inject.Inject;

import org.jdom.Element;

import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.LanguageDao;
import com.enonic.cms.store.dao.UserDao;


public class ContentParserService
{
    @Inject
    private LanguageDao languageDao;

    @Inject
    private CategoryDao categoryDao;

    @Inject
    private UserDao userDao;

    @Inject
    private GroupDao groupDao;

    @Inject
    private ContentVersionDao contentVersionDao;

    @Inject
    private ContentDao contentDao;

    public ContentAndVersion parseContentAndVersion( String contentXml, List<BinaryDataKey> binaryDatas, boolean parseContentData )
    {
        ContentParser parser = new ContentParser( contentDao, contentVersionDao, categoryDao, groupDao, userDao, languageDao, contentXml );
        parser.setBinaryDatas( binaryDatas );
        parser.setParseContentData( parseContentData );
        return parser.parseContentAndVersion();
    }

    public ContentAndVersion parseContentAndVersionForPreview( Element contentEl, List<BinaryDataKey> binaryDatas,
                                                               boolean parseContentData )
    {
        ContentParser parser = new ContentParser( contentDao, contentVersionDao, categoryDao, groupDao, userDao, languageDao, null );
        parser.setBinaryDatas( binaryDatas );
        parser.setParseContentData( parseContentData );
        parser.setParseContentCreatedAt( true );

        ContentAndVersion contentAndVersion = parser.parseContentAndVersion( contentEl );
        ContentEntity content = contentAndVersion.getContent();
        ContentVersionEntity version = contentAndVersion.getVersion();
        content.setMainVersion( version );
        if ( version.isDraft() )
        {
            content.setDraftVersion( version );
        }
        return new ContentAndVersion( content, version );
    }
}

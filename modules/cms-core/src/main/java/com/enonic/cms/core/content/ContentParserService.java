/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.List;

import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.LanguageDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.domain.content.ContentAndVersion;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.binary.BinaryDataKey;


public class ContentParserService
{
    @Autowired
    private LanguageDao languageDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private ContentVersionDao contentVersionDao;

    @Autowired
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

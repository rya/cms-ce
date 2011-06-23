package com.enonic.cms.business.core.content;

import com.enonic.cms.store.dao.ContentEntityDao;

import com.enonic.cms.domain.content.ContentEntity;

public class ContentNameForCopiesResolver
{
    private ContentEntityDao contentEntityDao;

    public ContentNameForCopiesResolver( ContentEntityDao contentEntityDao )
    {
        this.contentEntityDao = contentEntityDao;
    }

    public String findUniqueNameInCategory( ContentEntity sourceContent ) {
        ContentNameForCopiesIterator contentNameIterator = new ContentNameForCopiesIterator( sourceContent.getName() );

        for ( String nextName : contentNameIterator )
        {
            if ( !contentEntityDao.checkNameExists( sourceContent.getCategory(), nextName ) )
            {
                break;
            }
        }

        return contentNameIterator.currentName();
    }
}

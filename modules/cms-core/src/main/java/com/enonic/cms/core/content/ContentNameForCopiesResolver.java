package com.enonic.cms.core.content;

import com.enonic.cms.store.dao.ContentDao;

public class ContentNameForCopiesResolver
{
    private ContentDao contentEntityDao;

    public ContentNameForCopiesResolver( ContentDao contentEntityDao )
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

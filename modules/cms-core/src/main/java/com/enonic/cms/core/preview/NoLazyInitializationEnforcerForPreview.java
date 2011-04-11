/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preview;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.security.user.UserEntity;

/**
 * This is to prevent LazyInitializationException occurring when lazy-initialized objects/collections are requested
 * after this Hibernate session. I.e. when the previewed content object is picked from the HTTP session and used.
 */
public class NoLazyInitializationEnforcerForPreview
{
    public static void enforceNoLazyInitialization( ContentEntity content )
    {
        content.setCategory( content.getCategory() != null ? new CategoryEntity( content.getCategory() ) : null );
        content.setAssignee( content.getAssignee() != null ? new UserEntity( content.getAssignee() ) : null );
        content.setAssigner( content.getAssigner() != null ? new UserEntity( content.getAssigner() ) : null );
        content.setOwner( content.getOwner() != null ? new UserEntity( content.getOwner() ) : null );
        content.setLanguage( content.getLanguage() != null ? new LanguageEntity( content.getLanguage() ) : null );
        content.setDraftVersion( content.getDraftVersion() != null ? new ContentVersionEntity( content.getDraftVersion() ) : null );
        content.setMainVersion( content.getMainVersion() != null ? new ContentVersionEntity( content.getMainVersion() ) : null );
    }
}

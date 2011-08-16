/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;

import com.enonic.cms.core.content.category.command.DeleteCategoryCommand;

/**
 * Mar 9, 2010
 */
public interface CategoryService
{
    public CategoryKey storeNewCategory( StoreNewCategoryCommand command );

    void deleteCategory( DeleteCategoryCommand command );
}

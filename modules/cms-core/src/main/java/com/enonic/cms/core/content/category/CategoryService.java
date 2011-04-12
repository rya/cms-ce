/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;

/**
 * Mar 9, 2010
 */
public interface CategoryService
{
    public CategoryKey storeNewCategory( StoreNewCategoryCommand command );
}

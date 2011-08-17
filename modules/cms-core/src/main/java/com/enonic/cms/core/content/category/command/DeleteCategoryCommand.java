package com.enonic.cms.core.content.category.command;


import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.security.user.UserKey;

public class DeleteCategoryCommand
{
    private UserKey deleter;

    private CategoryKey categoryKey;

    private boolean recursive;

    private boolean includeContent;

    public UserKey getDeleter()
    {
        return deleter;
    }

    public void setDeleter( UserKey deleter )
    {
        this.deleter = deleter;
    }

    public CategoryKey getCategoryKey()
    {
        return categoryKey;
    }

    public void setCategoryKey( CategoryKey categoryKey )
    {
        this.categoryKey = categoryKey;
    }

    public boolean isRecursive()
    {
        return recursive;
    }

    public void setRecursive( boolean recursive )
    {
        this.recursive = recursive;
    }

    public boolean isIncludeContent()
    {
        return includeContent;
    }

    public void setIncludeContent( boolean includeContent )
    {
        this.includeContent = includeContent;
    }
}
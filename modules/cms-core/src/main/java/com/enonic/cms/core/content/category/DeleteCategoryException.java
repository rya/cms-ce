package com.enonic.cms.core.content.category;


public class DeleteCategoryException
    extends RuntimeException
{
    public DeleteCategoryException( RuntimeException cause )
    {
        super( "Failed to delete category: " + cause.getMessage(), cause );
    }

    public RuntimeException getRuntimeExceptionCause()
    {
        return (RuntimeException) getCause();
    }
}

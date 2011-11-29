package com.enonic.cms.api.client.model;

public class DeleteUserParams
    extends AbstractParams
{
    private static final long serialVersionUID = 7026327283398704712L;

    /**
     * Specify user either by qualified name ([userStoreKey:]&lt;user name&gt;) or key. When specifying a key, prefix with a hash (user = #xxx).
     */
    public String user = null;
}
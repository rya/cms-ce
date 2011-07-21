package com.enonic.cms.admin.userstore;

import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;


public class UserStoreConfigFieldModel
{

    private String type;

    private boolean required = false;

    private boolean readOnly = false;

    private boolean remote = false;

    private boolean iso = true;


    public UserStoreConfigFieldModel( UserStoreUserFieldConfig config )
    {
        if ( config == null )
        {
            throw new IllegalArgumentException( "UserStoreUserFieldConfig can't be null." );
        }
        this.type = config.getType().getName();
        this.required = config.isRequired();
        this.readOnly = config.isReadOnly();
        this.remote = config.isRemote();
        this.iso = config.useIso();
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public boolean isRequired()
    {
        return required;
    }

    public void setRequired( boolean required )
    {
        this.required = required;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly( boolean readOnly )
    {
        this.readOnly = readOnly;
    }

    public boolean isRemote()
    {
        return remote;
    }

    public void setRemote( boolean remote )
    {
        this.remote = remote;
    }

    public boolean isIso()
    {
        return iso;
    }

    public void setIso( boolean iso )
    {
        this.iso = iso;
    }
}

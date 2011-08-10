package com.enonic.cms.admin.account;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

public final class AccountModel
{
    private final static String IS_USER = "isUser";

    private boolean isUser;

    private String key;

    private String name;

    private String email;

    private String qualifiedName;

    private String displayName;

    private String userStore;

    private Date lastModified;

    @JsonProperty(IS_USER)
    public boolean isUser()
    {
        return isUser;
    }

    public void setUser( boolean user )
    {
        isUser = user;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public void setQualifiedName( String qualifiedName )
    {
        this.qualifiedName = qualifiedName;
    }

    public void setUserStore( String userStore )
    {
        this.userStore = userStore;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public void setLastModified( Date lastModified )
    {
        this.lastModified = lastModified;
    }

    public String getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public String getQualifiedName()
    {
        return qualifiedName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getUserStore()
    {
        return userStore;
    }

    public Date getLastModified()
    {
        return lastModified;
    }
}

package com.enonic.cms.admin.user;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonValue;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonAutoDetect
public final class UserModel
{
    private String key;

    @JsonProperty("username")
    private String name;

    private String email;

    private String qualifiedName;

    @JsonProperty("display_name")
    private String displayName;

    private String userStore;

    private Date lastModified;


    private UserInfoModel userInfo;

    @JsonProperty("userInfo")
    public UserInfoModel getUserInfo()
    {
        return userInfo;
    }

    public void setUserInfo( UserInfoModel userInfo )
    {
        this.userInfo = userInfo;
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

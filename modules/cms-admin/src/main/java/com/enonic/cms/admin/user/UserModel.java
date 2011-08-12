package com.enonic.cms.admin.user;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect
public final class UserModel
{
    public final static String USER_NAME = "username";

    public final static String EMAIL = "email";

    public final static String KEY = "key";

    public final static String DISPLAY_NAME = "display-name";

    public final static String USER_INFO = "userInfo";

    public final static String USERSTORE = "userStore";

    private String key;

    @JsonCreator
    public UserModel( @JsonProperty(USER_NAME) String name, @JsonProperty(EMAIL) String email,
                      @JsonProperty(KEY) String key, @JsonProperty(DISPLAY_NAME) String displayName,
                      @JsonProperty(USERSTORE) String userStore, @JsonProperty(USER_INFO) Map<String, Object> userInfo )
    {
        this.name = name;
        this.email = email;
        this.key = key;
        this.displayName = displayName;
        this.userStore = userStore;
        this.userInfo = new UserInfoModel( userInfo );
    }

    public UserModel()
    {
    }

    @JsonProperty(USER_NAME)
    private String name;

    private String email;

    private String qualifiedName;

    @JsonProperty(DISPLAY_NAME)
    private String displayName;

    private String userStore;

    private Date lastModified;


    private UserInfoModel userInfo;

    private String lastLogged;

    private List<Map<String, String>> groups;

    public List<Map<String, String>> getGroups()
    {
        return groups;
    }

    public void setGroups( List<Map<String, String>> groups )
    {
        this.groups = groups;
    }

    public String getLastLogged()
    {
        return lastLogged;
    }

    public void setLastLogged( String lastLogged )
    {
        this.lastLogged = lastLogged;
    }

    public String getCreated()
    {
        return created;
    }

    public void setCreated( String created )
    {
        this.created = created;
    }

    private String created;

    @JsonProperty(USER_INFO)
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

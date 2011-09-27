package com.enonic.cms.core.jcr.accounts;

import java.util.HashMap;
import java.util.Map;

public class User extends Account
{
    public User()
    {
        super();
    }

    private String email;

    private byte[] photo;

    private final Map<String, String> fieldMap = new HashMap<String, String>();

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public Map<String, String> getFieldMap()
    {
        return fieldMap;
    }

    public byte[] getPhoto()
    {
        return photo;
    }

    public void setPhoto( byte[] photo )
    {
        this.photo = photo;
    }
}

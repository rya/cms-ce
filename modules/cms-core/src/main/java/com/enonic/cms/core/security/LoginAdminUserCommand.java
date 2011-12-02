package com.enonic.cms.core.security;


import com.enonic.cms.core.security.user.QualifiedUsername;

public class LoginAdminUserCommand
{
    private QualifiedUsername qualifiedUsername;

    private String password;

    private boolean verifyPassword = true;

    public LoginAdminUserCommand( QualifiedUsername qualifiedUsername, String password )
    {
        this.qualifiedUsername = qualifiedUsername;
        this.password = password;
    }

    public LoginAdminUserCommand( QualifiedUsername qualifiedUsername, String password, boolean verifyPassword )
    {
        this.qualifiedUsername = qualifiedUsername;
        this.password = password;
        this.verifyPassword = verifyPassword;
    }

    public QualifiedUsername getQualifiedUsername()
    {
        return qualifiedUsername;
    }

    public boolean isVerifyPassword()
    {
        return verifyPassword;
    }

    public String getPassword()
    {
        return password;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.mail;

public final class MessageSettings
{
    private String fromMail;

    private String fromName;

    private String subject;

    private String body;

    public String getFromMail()
    {
        return fromMail;
    }

    public void setFromMail( String fromMail )
    {
        this.fromMail = fromMail;
    }

    public String getFromName()
    {
        return fromName;
    }

    public void setFromName( String fromName )
    {
        this.fromName = fromName;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject( String subject )
    {
        this.subject = subject;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody( String body )
    {
        this.body = body;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.mail;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.enonic.cms.domain.security.user.UserEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: May 6, 2010
 * Time: 2:40:17 PM
 */
public abstract class AbstractMailTemplate
{
    protected final static Logger LOG = LoggerFactory.getLogger( AbstractMailTemplate.class );

    private List<MailRecipient> mailRecipients = new ArrayList<MailRecipient>();

    private MailRecipient from;

    public List<MailRecipient> getMailRecipients()
    {
        return mailRecipients;
    }

    public void setMailRecipients( List<MailRecipient> mailRecipients )
    {
        this.mailRecipients = mailRecipients;
    }

    public void addMailRecipients( List<UserEntity> mailRecipients )
    {
        for ( UserEntity recipient : mailRecipients )
        {
            this.mailRecipients.add( new MailRecipient( recipient.getDisplayName(), recipient.getEmail() ) );
        }
    }

    public MailRecipient getFrom()
    {
        return from;
    }

    public void setFrom( MailRecipient from )
    {
        this.from = from;
    }

    public abstract String getBody();

    public abstract String getSubject();

    protected void addNewLine( StringBuffer buffer )
    {
        buffer.append( "\n" );
    }

    protected String createUserName( UserEntity user )
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append( user.getDisplayName() );
        buffer.append( " (" );
        buffer.append( user.getQualifiedName() );
        buffer.append( ")" );

        return buffer.toString();
    }
}

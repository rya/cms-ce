/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.mail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.core.servlet.ServletRequestAccessor;

import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.security.user.UserEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: May 6, 2010
 * Time: 2:40:17 PM
 */
public abstract class AbstractMailTemplate
{
    public static final String ADMIN_URL = "/adminpage?page=0&editContent=";

    protected final static Logger LOG = LoggerFactory.getLogger( AbstractMailTemplate.class );

    private List<MailRecipient> mailRecipients = new ArrayList<MailRecipient>();

    private MailRecipient from;

    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat( "dd.MM.yyyy HH:mm" );

    protected String getAdminUrl( final ContentKey contentKey )
    {
        HttpServletRequest request = ServletRequestAccessor.getRequest();
        String adminUrl = AdminHelper.getAdminPath( request, false );
        if ( adminUrl != null )
        {
            adminUrl += ADMIN_URL + contentKey.toString();
        }
        return adminUrl;
    }

    public void addRecipient( MailRecipient recipient )
    {
        mailRecipients.add( recipient );
    }

    public void addRecipient( UserEntity recipient )
    {
        mailRecipients.add( new MailRecipient( recipient ) );
    }

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

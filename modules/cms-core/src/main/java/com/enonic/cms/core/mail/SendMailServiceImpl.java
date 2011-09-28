/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.enonic.vertical.VerticalProperties;

import com.enonic.cms.domain.security.user.UserEntity;

public final class SendMailServiceImpl
    extends AbstractSendMailService
    implements SendMailService
{
    @Autowired
    private VerticalProperties properties;

    protected void composeChangePasswordMail( MimeMessageHelper message, UserEntity user, String newPassword, MessageSettings settings )
        throws Exception
    {
        String subject = settings.getSubject();
        if ( subject == null )
        {
            subject = this.properties.getAdminNewPasswordMailSubject();
        }

        String body = settings.getBody();
        if ( body == null )
        {
            body = this.properties.getAdminNewPasswordMailBody();
        }

        message.addTo( user.getEmail(), user.getDisplayName() );
        message.setSubject( subject );

        body = body.replaceAll( "%password%", newPassword );
        body = body.replaceAll( "%uid%", user.getName() );

        message.setText( body );
    }

}

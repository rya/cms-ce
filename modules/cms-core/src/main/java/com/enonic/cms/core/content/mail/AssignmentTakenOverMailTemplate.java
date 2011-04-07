/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.mail;

import com.enonic.cms.business.mail.MailRecipient;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.security.user.UserEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Jun 18, 2010
 * Time: 10:56:21 AM
 */
public class AssignmentTakenOverMailTemplate
    extends AbstractAssignmentMailTemplate
{
    private UserEntity newAssignee;

    public AssignmentTakenOverMailTemplate( ContentEntity content, ContentVersionEntity contentVersion )
    {
        super( contentVersion, content );
    }

    @Override
    public String getBody()
    {
        StringBuffer body = new StringBuffer();

        body.append( getTranslation( "%contentAssignmentTakenOverBody%", getLanguageCode() ) + " " + createUserName( assigner ) );

        /* if ( StringUtils.isNotBlank( assignmentDescription ) )
        {
            addNewLine( body );
            body.append( assignmentDescription );
        }
        */

        addNewLine( body );
        addNewLine( body );

        body.append( createAssignmentMailInfoElement() );

        return body.toString();
    }

    @Override
    public MailRecipient getFrom()
    {
        return new MailRecipient( assigner.getDisplayName(), assigner.getEmail() );
    }

    @Override
    public String getSubject()
    {
        return getTranslation( "%contentAssignmentTakenOverSubject%", getLanguageCode() ) + ": " + contentVersion.getTitle();
    }

}

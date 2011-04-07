/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.mail;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.business.mail.AbstractMailTemplate;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.security.user.UserEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Oct 26, 2010
 * Time: 12:38:34 PM
 */
public class ImportedContentAssignmentMailTemplate
    extends AbstractMailTemplate
{
    private Set<ContentKey> importedContent;

    private ContentDao contentDao;

    private String assignmentDescription;

    private Date assignmentDueDate;

    private UserEntity assigner;

    public ImportedContentAssignmentMailTemplate( Set<ContentKey> importedContent, ContentDao contentDao )
    {
        this.importedContent = importedContent;
        this.contentDao = contentDao;
    }

    @Override
    public String getBody()
    {
        StringBuffer body = new StringBuffer();

        applyAssignmentInfo( body );

        addNewLine( body );
        addNewLine( body );

        appendContentListHeading( body );

        addNewLine( body );

        appendContentList( body );

        return body.toString();
    }

    private void appendContentList( StringBuffer body )
    {
        for ( ContentKey contentKey : importedContent )
        {
            ContentEntity content = contentDao.findByKey( contentKey );

            addNewLine( body );
            body.append( content.getCategory().getPathAsString() + "/" );
            body.append( content.getMainVersion().getTitle() );
            body.append( " (" + content.getKey() + ")" );
        }
    }

    private void applyAssignmentInfo( StringBuffer body )
    {
        if ( StringUtils.isNotBlank( assignmentDescription ) )
        {
            body.append( assignmentDescription );
        }

        if ( assigner != null )
        {
            addNewLine( body );
            body.append( " - " );
            body.append( createUserName( assigner ) );
        }

        if ( assignmentDueDate != null )
        {
            addNewLine( body );
            body.append( "Due date: " + dateFormat.format( assignmentDueDate ) );
        }
    }

    private void appendContentListHeading( StringBuffer body )
    {
        String contentListHeading = "The following content was assigned to you during import";
        int contentListMsgLength = contentListHeading.length();

        body.append( contentListHeading );
        addNewLine( body );
        body.append( StringUtils.rightPad( "", contentListMsgLength, '-' ) );
    }

    @Override
    public String getSubject()
    {
        return "Enonic CMS - Drafts assigned to you";
    }

    public void setAssignmentDescription( String assignmentDescription )
    {
        this.assignmentDescription = assignmentDescription;
    }

    public void setAssignmentDueDate( Date assignmentDueDate )
    {
        this.assignmentDueDate = assignmentDueDate;
    }

    public void setAssigner( UserEntity assigner )
    {
        this.assigner = assigner;
    }

    public String getAssignmentDescription()
    {
        return assignmentDescription;
    }

    public Date getAssignmentDueDate()
    {
        return assignmentDueDate;
    }

    public UserEntity getAssigner()
    {
        return assigner;
    }
}

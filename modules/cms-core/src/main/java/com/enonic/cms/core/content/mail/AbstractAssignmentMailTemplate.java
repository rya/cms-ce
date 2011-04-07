/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.mail;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Maps;

import com.enonic.cms.business.mail.AbstractMailTemplate;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.security.user.UserEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Oct 25, 2010
 * Time: 1:38:57 PM
 */
public abstract class AbstractAssignmentMailTemplate
    extends AbstractMailTemplate
{
    protected ContentEntity content;

    protected ContentVersionEntity contentVersion;

    protected String assignmentDescription;

    protected UserEntity assigner;

    protected Date assignmentDueDate;

    public AbstractAssignmentMailTemplate( ContentVersionEntity contentVersion, ContentEntity content )
    {
        this.contentVersion = contentVersion;
        this.content = content;
    }

    public abstract String getBody();

    public abstract String getSubject();

    protected String createAssignmentMailInfoElement()
    {
        String contentPath = content.getPathAsString();

        StringBuffer body = new StringBuffer();

        Map<String, String> keyValues = Maps.newLinkedHashMap();

        if ( StringUtils.isNotBlank( contentPath ) )
        {
            addKeyValue( keyValues, "%fldStatus%", getTranslatedStatus( contentVersion.getStatus() ) );

            if ( assignmentDueDate != null )
            {
                addKeyValue( keyValues, "%contentAssignmentDuedate%", dateFormat.format( assignmentDueDate ) );
            }

            addKeyValue( keyValues, "%fldDisplayName%", contentVersion.getTitle() );
            addKeyValue( keyValues, "%fldContentType%", content.getContentType().getName() );
            addKeyValue( keyValues, "%contentAssignedBy%", createUserName( assigner ) );
            addKeyValue( keyValues, "%contentAssignmentPath%", contentPath );
        }

        String adminUrl = getAdminUrl( content.getKey() );

        if ( StringUtils.isNotBlank( adminUrl ) )
        {
            addKeyValue( keyValues, "%blockURL%", adminUrl );
        }

        appendKeyValuesWithPadding( body, keyValues );

        return body.toString();
    }

    private void appendKeyValuesWithPadding( StringBuffer body, Map<String, String> keyValues )
    {
        int maxLength = findKeyMaxLength( keyValues );

        addNewLine( body );

        for ( String key : keyValues.keySet() )
        {
            String paddedKey = StringUtils.rightPad( key, maxLength );

            body.append( paddedKey + "\t" + keyValues.get( key ) );
            addNewLine( body );
        }
    }

    private int findKeyMaxLength( Map<String, String> keyValues )
    {
        int maxLength = 0;

        for ( String key : keyValues.keySet() )
        {
            if ( key.length() > maxLength )
            {
                maxLength = key.length();
            }
        }

        return maxLength;
    }

    private String getTranslatedStatus( ContentStatus status )
    {
        return getTranslation( "%txtContentState" + status.getKey() + "%", getLanguageCode() );
    }

    protected void addKeyValue( Map<String, String> keyValueMap, String translatableKey, String value )
    {
        String translatedKey = getTranslation( translatableKey, getLanguageCode() ) + ":";

        keyValueMap.put( translatedKey, value );
    }

    protected String getLanguageCode()
    {
        return content.getLanguage() == null ? null : content.getLanguage().getCode();
    }

    public void setAssignmentDescription( String assignmentDescription )
    {
        this.assignmentDescription = assignmentDescription;
    }

    public void setAssigner( UserEntity assigner )
    {
        this.assigner = assigner;
    }

    public Date getAssignmentDueDate()
    {
        return assignmentDueDate;
    }

    public void setAssignmentDueDate( Date assignmentDueDate )
    {
        this.assignmentDueDate = assignmentDueDate;
    }
}

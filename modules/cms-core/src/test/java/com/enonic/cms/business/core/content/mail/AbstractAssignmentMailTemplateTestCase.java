package com.enonic.cms.business.core.content.mail;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.core.servlet.ServletRequestAccessor;

import com.enonic.cms.business.mail.AbstractMailTemplate;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.security.user.QualifiedUsername;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.userstore.UserStoreKey;

import static org.junit.Assert.*;

public abstract class AbstractAssignmentMailTemplateTestCase
{
    private static int CONTENT_KEY = 555;

    private AbstractAssignmentMailTemplate mailTemplate;

    private String adminUrl;

    protected abstract String getExpectedSubject();
    protected abstract void appendExpectedTitle( StringBuffer expected );
    protected abstract AbstractAssignmentMailTemplate instantiateMailTemplate(
            ContentEntity content,
            ContentVersionEntity contentVersion);

    @Before
    public void setUp()
    {
        UserEntity assigner = new UserEntity()
        {
            @Override
            public String getDisplayName()
            {
                return "dislpayNmae-mock";
            }

            @Override
            public QualifiedUsername getQualifiedName()
            {
                return new QualifiedUsername( new UserStoreKey( "200" ), "username-mock" );
            }
        };

        ContentEntity content = new ContentEntity()
        {
            @Override
            public String getPathAsString()
            {
                return "path-mock";
            }

            @Override
            public ContentTypeEntity getContentType()
            {
                return new ContentTypeEntity( 100, "ct_name-mock" );
            }

            @Override
            public ContentKey getKey()
            {
                return new ContentKey( CONTENT_KEY );
            }
        };
        ContentVersionEntity contentVersion = new ContentVersionEntity()
        {
            @Override
            public ContentStatus getStatus()
            {
                return ContentStatus.DRAFT;
            }

            @Override
            public String getTitle()
            {
                return "title-mock";
            }
        };

        mailTemplate = instantiateMailTemplate( content, contentVersion );
        mailTemplate.setAssigner( assigner );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        adminUrl = AdminHelper.getAdminPath( request, false );
    }

    @Test
    public void testGetBody()
    {
        StringBuffer expected = new StringBuffer();

        appendExpectedTitle( expected );
        addNewLine( expected );
        addNewLine( expected );
        addNewLine( expected );

        appendWithPadding( expected, "Status:", "Draft" );
        appendWithPadding( expected, "Display name:", "title-mock" );
        appendWithPadding( expected, "Content type:", "ct_name-mock" );
        appendWithPadding( expected, "Modifier:", "dislpayNmae-mock (#200\\username-mock)" );
        appendWithPadding( expected, "Content path:", "path-mock" );
        appendWithPadding( expected, "Link:",
                           adminUrl + AbstractMailTemplate.ADMIN_URL + String.valueOf( CONTENT_KEY ) );

        assertEquals( expected.toString(), mailTemplate.getBody() );
    }

    @Test
    public void testGetSubject()
    {
        assertEquals( getExpectedSubject(), mailTemplate.getSubject() );
    }

    protected void addNewLine( StringBuffer buffer )
    {
        buffer.append( "\n" );
    }

    private void appendWithPadding( StringBuffer body, String key, String value )
    {
        int maxLength = "Display name:".length();
        String paddedKey = StringUtils.rightPad( key, maxLength );

        body.append( paddedKey + "\t" + value );
        addNewLine( body );
    }
}

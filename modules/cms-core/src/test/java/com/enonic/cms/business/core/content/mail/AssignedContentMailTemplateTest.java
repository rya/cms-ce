package com.enonic.cms.business.core.content.mail;

import com.enonic.cms.core.content.mail.AbstractAssignmentMailTemplate;
import com.enonic.cms.core.content.mail.AssignedContentMailTemplate;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentVersionEntity;

public class AssignedContentMailTemplateTest
        extends AbstractAssignmentMailTemplateTestCase
{
    protected String getExpectedSubject()
    {
        return "Enonic CMS - Draft assigned to you: title-mock";
    }

    protected void appendExpectedTitle( StringBuffer expected )
    {
        addNewLine( expected );
        expected.append( " - dislpayNmae-mock (#200\\username-mock)" );
    }

    protected AbstractAssignmentMailTemplate instantiateMailTemplate( ContentEntity content,
                                                                      ContentVersionEntity contentVersion )
    {
        return new AssignedContentMailTemplate(content, contentVersion);
    }
}

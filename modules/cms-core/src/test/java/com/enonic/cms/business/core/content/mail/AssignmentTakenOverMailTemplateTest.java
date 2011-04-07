package com.enonic.cms.business.core.content.mail;

import com.enonic.cms.core.content.mail.AbstractAssignmentMailTemplate;
import com.enonic.cms.core.content.mail.AssignmentTakenOverMailTemplate;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentVersionEntity;

public class AssignmentTakenOverMailTemplateTest extends AbstractAssignmentMailTemplateTestCase
{
    protected String getExpectedSubject()
    {
        return "Enonic CMS - Draft unassigned: title-mock";
    }

    protected void appendExpectedTitle( StringBuffer expected )
    {
        expected.append( "A draft previously assigned to you has been modified by dislpayNmae-mock (#200\\username-mock)" );
    }

    protected AbstractAssignmentMailTemplate instantiateMailTemplate( ContentEntity content,
                                                                      ContentVersionEntity contentVersion )
    {
        return new AssignmentTakenOverMailTemplate(content, contentVersion);
    }
}


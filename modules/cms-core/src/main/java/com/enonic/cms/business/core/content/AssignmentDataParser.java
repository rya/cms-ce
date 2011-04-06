/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.core.content;

import java.text.ParseException;
import java.util.Date;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.util.DateUtil;


/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Oct 25, 2010
 * Time: 8:58:44 AM
 */
public class AssignmentDataParser
{

    private static final String DUEDATE_DATE_FORMITEM_KEY = "date_assignment_duedate";

    private static final String DUEDATE_TIME_FORMITEM_KEY = "time_assignment_duedate";

    private static final String DEFAULT_ASSIGNMENT_DUEDATE_HHMM = "23:59";

    private static final String ASSIGNMENT_DESCRIPTION_FORMITEM_KEY = "_assignment_description";

    private static final String ASSIGNEE_FORMITEM_KEY = "_assignee";

    private static final String ASSIGNER_FORMITEM_KEY = "_assigner";

    private ExtendedMap formItems;

    public AssignmentDataParser( ExtendedMap formItems )
    {
        this.formItems = formItems;
    }

    public String getAssigneeKey()
    {
        return formItems.getString( ASSIGNEE_FORMITEM_KEY, null );
    }

    public String getAssignerKey()
    {
        return formItems.getString( ASSIGNER_FORMITEM_KEY, null );
    }

    public String getAssignmentDescription()
    {
        return formItems.getString( ASSIGNMENT_DESCRIPTION_FORMITEM_KEY, null );
    }

    public Date getAssignmentDueDate()
    {
        if ( formItems.containsKey( DUEDATE_DATE_FORMITEM_KEY ) )
        {
            StringBuffer date = new StringBuffer( formItems.getString( DUEDATE_DATE_FORMITEM_KEY ) );
            date.append( ' ' );
            date.append( formItems.getString( DUEDATE_TIME_FORMITEM_KEY, DEFAULT_ASSIGNMENT_DUEDATE_HHMM ) );

            try
            {
                return DateUtil.parseDateTime( date.toString() );
            }
            catch ( ParseException e )
            {
                /* todo: replace with log4j
                VerticalAdminLogger.errorAdmin( ContentBaseHandlerServlet.class, 10, "Error parsing assignment duedate: %t", e );
                */
            }
        }

        return null;
    }

}

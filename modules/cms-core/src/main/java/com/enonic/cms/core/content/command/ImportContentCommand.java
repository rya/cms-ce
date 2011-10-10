/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.command;

import java.io.InputStream;
import java.util.Date;

import org.joda.time.DateTime;

import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;

public class ImportContentCommand
{
    // 
    public UserEntity importer;

    public CategoryEntity categoryToImportTo;

    public String importName;

    public DateTime publishFrom;

    public DateTime publishTo;

    public InputStream inputStream;

    public boolean executeInOneTransaction = false;

    public UserKey assigneeKey;

    public Date assignmentDueDate;

    public String assignmentDescription;

}

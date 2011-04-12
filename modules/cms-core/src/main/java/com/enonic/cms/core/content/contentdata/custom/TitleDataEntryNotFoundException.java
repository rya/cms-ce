/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

/**
 * Apr 7, 2010
 */
public class TitleDataEntryNotFoundException
    extends RuntimeException
{
    private String titleInputName;

    public TitleDataEntryNotFoundException( String titleInputName )
    {
        super( "Could not find title data entry, title input name was: " + titleInputName );
        this.titleInputName = titleInputName;
    }

    public String getTitleInputName()
    {
        return titleInputName;
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata;

import com.enonic.cms.core.content.contentdata.custom.GroupDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class MissingRequiredContentDataException
    extends RuntimeException
{
    private String inputName;

    private MissingRequiredContentDataException( String message, String inputName )
    {
        super( message );
        this.inputName = inputName;
    }

    public static MissingRequiredContentDataException missingDataEntry( final DataEntryConfig dataEntryConfig )
    {
        String inputName = dataEntryConfig.getName();

        MissingRequiredContentDataException e =
            new MissingRequiredContentDataException( "Missing data for required input (missing data entry): " + inputName, inputName );
        e.inputName = inputName;
        return e;
    }

    public static MissingRequiredContentDataException missingDataEntryInGroup( final DataEntryConfig dataEntryConfig,
                                                                               GroupDataEntry groupDataEntry )
    {
        String inputName = dataEntryConfig.getName();

        final String message =
            "Missing data for required input " + "(missing data entry): " + inputName + " in group " + groupDataEntry.getName() + "[" +
                groupDataEntry.getGroupIndex() + "]";
        MissingRequiredContentDataException e = new MissingRequiredContentDataException( message, inputName );
        e.inputName = inputName;
        return e;
    }

    public static MissingRequiredContentDataException missingDataEntryValue( final DataEntryConfig dataEntryConfig )
    {
        String inputName = dataEntryConfig.getName();

        MissingRequiredContentDataException e =
            new MissingRequiredContentDataException( "Missing data for required input (missing value in data entry): " + inputName,
                                                     inputName );
        e.inputName = inputName;
        return e;
    }

    public static MissingRequiredContentDataException missingDataEntryValueInGroup( final DataEntryConfig dataEntryConfig,
                                                                                    GroupDataEntry groupDataEntry )
    {
        String inputName = dataEntryConfig.getName();

        final String message =
            "Missing data for required input " + "(missing value in data entry): " + inputName + " in group " + groupDataEntry.getName() +
                "[" + groupDataEntry.getGroupIndex() + "]";

        MissingRequiredContentDataException e = new MissingRequiredContentDataException( message, inputName );
        e.inputName = inputName;
        return e;
    }

    public String getInputName()
    {
        return inputName;
    }
}
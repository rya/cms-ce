package com.enonic.cms.domain.content.contentdata.custom;

/**
 * For a field to be used as a title, it must be a single line text string.  All implementing classes have data that are or can be converted
 * to a single line string, and thus be used as the title of the content.
 */
public interface TitleDataEntry
    extends DataEntry
{
    /**
     * @return Returns the value as a single line string.
     */
    public String getValueAsTitle();
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.translator.expression;

import com.enonic.cms.core.content.index.FieldHelper;


public class FieldName
{
    private String untranslatedFieldName;

    private String translatedFieldName;

    private boolean userDefinedField;

    public FieldName( String untranslatedFieldName )
    {
        this.untranslatedFieldName = untranslatedFieldName;
        this.translatedFieldName = FieldHelper.translateFieldName( untranslatedFieldName );
        this.userDefinedField = FieldHelper.isUserDefinedField(untranslatedFieldName);
    }

    public boolean isUserDefinedField()
    {
        return userDefinedField;
    }

    public String getUntranslatedFieldName()
    {
        return untranslatedFieldName;
    }

    public String getTranslatedFieldName()
    {
        return translatedFieldName;
    }

    public int hashCode()
    {
        return this.translatedFieldName.hashCode();
    }

    public boolean equals( Object o )
    {
        return ( o instanceof FieldName ) && equals( (FieldName) o );
    }

    public boolean equals( FieldName o )
    {
        return o.translatedFieldName.equals( this.translatedFieldName );
    }

    @Override
    public String toString()
    {
        return "FieldName{" + "translatedFieldName='" + translatedFieldName + '\'' + '}';
    }
}

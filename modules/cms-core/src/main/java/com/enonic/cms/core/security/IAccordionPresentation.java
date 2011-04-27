package com.enonic.cms.core.security;

import java.util.Date;

public interface IAccordionPresentation
{

    String getTypeName();

    String getDisplayName();

    QualifiedName getQualifiedName();

    Date getLastModified();

    String getISODate();
}

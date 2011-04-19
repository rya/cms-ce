package com.enonic.cms.admin.tab;

import java.lang.annotation.Annotation;

import com.vaadin.ui.VerticalLayout;

import com.enonic.cms.admin.tab.annotations.Options;

public class BaseTab
        extends VerticalLayout
{
    public BaseTab()
    {
        setMargin( true );
        Annotation[] annotations = getClass().getAnnotations();
        Options options = getClass().getAnnotation( Options.class );
        // TODO i18n. http://code.google.com/p/tpt/ ?
        setCaption( options.title() );
    }
}

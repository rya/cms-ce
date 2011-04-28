package com.enonic.cms.admin.spring;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Qualifier
@Scope("vaadin")
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface VaadinComponent
{
}

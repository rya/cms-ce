package com.enonic.cms.core.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public @interface PrototypeScope
{
}

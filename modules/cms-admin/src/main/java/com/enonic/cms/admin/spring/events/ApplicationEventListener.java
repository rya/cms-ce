package com.enonic.cms.admin.spring.events;

import java.util.EventListener;

public interface ApplicationEventListener extends EventListener
{
    <T extends ApplicationEvent> void onApplicationEvent(T e);
}

package com.enonic.cms.core.jcr.wrapper;

import java.util.Set;

public interface JcrNode
{
    public String getName();

    public String getNodeType();

    public String getPath();

    public JcrNode getParent();

    public Set<String> getPropertyNames();

    public Object getProperty(String name);

    public String getStringProperty(String name);

    public String getBooleanProperty(String name);

    public JcrBinary getBinaryProperty(String name);

    public void setProperty(String name, Object value);

    public JcrNode getChild(String name);
}

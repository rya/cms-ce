package com.enonic.cms.core.jcr.wrapper;

import java.util.Set;

public interface JcrNode
{
    public String getName();

    public String getNodeType();

    public String getPath();

    public JcrNode getParent();

    public JcrNode getNode( String relPath );

    public boolean hasNode( String relPath );

    public JcrNode addNode(String relPath, String primaryNodeTypeName);

    public void remove();

    public void remove( String relPath );

    public boolean hasProperties();

    public Set<String> getPropertyNames();

    public Object getProperty( String name );

    public String getStringProperty( String name );

    public String getBooleanProperty( String name );

    public JcrBinary getBinaryProperty( String name );

    public void setProperty( String name, Object value );

    public void setPropertyReference( String name, JcrNode referencedNode, boolean weak );

    public JcrNodeIterator getChildren();

    public JcrNode getChild( String name );

    public boolean hasChildNodes();
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

/**
 * This interface defines the relation visitor.
 */
public interface RelationVisitor
{
    /**
     * Visit the specified node.
     */
    public Object visit( RelationNode node );
}

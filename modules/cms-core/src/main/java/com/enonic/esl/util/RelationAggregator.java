/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import java.util.Iterator;

/**
 * This implements the base relation aggregator.
 */
public class RelationAggregator
    implements RelationVisitor
{
    /**
     * Visit the specified node.
     */
    public Object visit( RelationNode node )
    {
        double num = 0;
        Object data = node.getData();

        if ( data instanceof Number )
        {
            num += ( (Number) data ).doubleValue();
        }

        for ( Iterator i = node.getChildren().iterator(); i.hasNext(); )
        {
            Object tmp = ( (RelationNode) i.next() ).accept( this );

            if ( tmp instanceof Number )
            {
                num += ( (Number) tmp ).doubleValue();
            }
        }

        return new Double( num );
    }
}

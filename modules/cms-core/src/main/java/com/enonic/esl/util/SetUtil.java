/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SetUtil
{

    private SetUtil()
    {
        // Prohibit construction    
    }

    public static List splitSetIntoSets( Set originalSet, int maxSetSize )
    {
        if ( maxSetSize < 2 )
        {
            throw new IllegalArgumentException( "Given maxSetSize has no meaning being so low" );
        }
        List sets = new ArrayList();
        if ( originalSet.size() <= maxSetSize )
        {
            sets.add( originalSet );
        }
        else
        {
            HashSet currSet = new HashSet( maxSetSize );
            sets.add( currSet );
            int elementCountInCurrSet = 0;
            for ( Iterator it = originalSet.iterator(); it.hasNext(); )
            {
                Object element = it.next();

                if ( elementCountInCurrSet >= maxSetSize )
                {
                    currSet = new HashSet( maxSetSize );
                    sets.add( currSet );
                    elementCountInCurrSet = 0;
                }

                currSet.add( element );
                elementCountInCurrSet++;
            }

        }
        return sets;
    }
}

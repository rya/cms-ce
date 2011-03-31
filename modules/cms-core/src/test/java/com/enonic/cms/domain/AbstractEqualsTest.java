/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import org.junit.Assert;


public abstract class AbstractEqualsTest
{
    protected void assertEqualsContract()
    {
        testAllUnequalsAreUnequal();

        testReflexive();

        testSymmetric();

        testTransitive();

        testConsistent();
    }

    private void testAllUnequalsAreUnequal()
    {
        Object x = getObjectX();

        for ( Object unequal : getObjectsThatNotEqualsX() )
        {
            Assert.assertTrue( "expected to be unequal", !x.equals( unequal ) );
        }
    }


    /**
     * For any non-null reference value x, x.equals(x) should return true.
     */
    private void testReflexive()
    {
        // positive test
        Object x = getObjectX();
        Assert.assertTrue( "reflexive", x.equals( x ) );
    }

    /**
     * For any non-null reference values x and y, x.equals(y)  should return true if and only if y.equals(x) returns true.
     */
    private void testSymmetric()
    {
        Object x = getObjectX();
        Object y = getObjectThatEqualsXButNotTheSame();

        Assert.assertTrue( "symmetric", ( x.equals( y ) && y.equals( x ) ) );

        // negative test
        Object unequalToX = getObjectsThatNotEqualsX()[0];
        Assert.assertTrue( "reflexive", !x.equals( unequalToX ) && !unequalToX.equals( x ) );
    }

    /**
     * For any non-null reference values x, y, and z, if x.equals(y) returns true and y.equals(z) returns true, then x.equals(z) should
     * return true.
     */
    private void testTransitive()
    {
        Object x = getObjectX();
        Object y = getObjectThatEqualsXButNotTheSame();
        Object z = getObjectThatEqualsXButNotTheSame2();

        Assert.assertTrue( "symmetric", ( x.equals( y ) && y.equals( z ) ) && x.equals( z ) );
    }

    /**
     * For any non-null reference values x and y, multiple invocations of x.equals(y) consistently return true  or consistently return
     * false, provided no information used in equals comparisons on the objects is modified.
     */
    private void testConsistent()
    {
        Object x = getObjectX();
        Object y = getObjectThatEqualsXButNotTheSame();

        boolean firstCheck = x.equals( y );
        boolean secondCheck = x.equals( y );

        Assert.assertTrue( "consistent", firstCheck && secondCheck );

        Object unequalToX = getObjectsThatNotEqualsX()[0];
        firstCheck = x.equals( unequalToX );
        secondCheck = x.equals( unequalToX );

        Assert.assertTrue( "consistent", !firstCheck && !secondCheck );
    }


    public abstract Object getObjectX();

    public abstract Object[] getObjectsThatNotEqualsX();

    public abstract Object getObjectThatEqualsXButNotTheSame();

    public abstract Object getObjectThatEqualsXButNotTheSame2();
}

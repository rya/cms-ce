/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

/**
 * This class implements the UUID.
 */
public final class UUID
    implements Comparable
{
    /**
     * Wait time (ms).
     */
    private final static int WAIT_TIME = 1000;

    /**
     * Lock for generating UID.
     */
    private final static Object LOCK = new Object();

    /**
     * Host unique number.
     */
    private final static int HOST_UNIQUE = getHostUniqueNumber();

    /**
     * Last time.
     */
    private static long LAST_TIME = System.currentTimeMillis();

    /**
     * Last sequence.
     */
    private static short LAST_SEQ = Short.MIN_VALUE;

    /**
     * Time of generation.
     */
    private final long time;

    /**
     * Sequence of generation.
     */
    private final short seq;

    /**
     * Number that is uniqe for VM.
     */
    private final int host;

    /**
     * Constructs the UUID.
     */
    private UUID( long time, short seq, int host )
    {
        this.time = time;
        this.seq = seq;
        this.host = host;
    }

    /**
     * Return as string.
     */
    public String getValue()
    {
        return StringUtil.toHex( this.time ) + StringUtil.toHex( this.seq ) + StringUtil.toHex( this.host );
    }

    /**
     * Returns the hash code.
     */
    public int hashCode()
    {
        return (int) this.time + (int) this.seq;
    }

    /**
     * Return true if equals.
     */
    public boolean equals( Object o )
    {
        if ( o == this )
        {
            return true;
        }
        else if ( o instanceof UUID )
        {
            return equals( (UUID) o );
        }
        else
        {
            return false;
        }
    }

    /**
     * Return true if equals.
     */
    private boolean equals( UUID o )
    {
        return ( this.time == o.time ) && ( this.seq == o.seq ) && ( this.host == o.host );
    }

    /**
     * Compare to object.
     */
    public int compareTo( Object o )
    {
        return compareTo( (UUID) o );
    }

    /**
     * Compare to object.
     */
    private int compareTo( UUID o )
    {
        if ( this.time > o.time )
        {
            return 1;
        }
        else if ( this.time < o.time )
        {
            return -1;
        }
        else if ( this.seq > o.seq )
        {
            return 1;
        }
        else if ( this.seq < o.seq )
        {
            return -1;
        }
        else if ( this.host > o.host )
        {
            return 1;
        }
        else if ( this.host < o.host )
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }

    /**
     * Return the string value.
     */
    public String toString()
    {
        return getValue();
    }

    /**
     * Return the unique VM number.
     */
    private static int getHostUniqueNumber()
    {
        return ( new Object() ).hashCode();
    }

    /**
     * Return a new UID.
     */
    public static UUID generate()
    {
        synchronized ( LOCK )
        {
            if ( LAST_SEQ == Short.MAX_VALUE )
            {
                boolean done = false;
                while ( !done )
                {
                    long now = System.currentTimeMillis();
                    if ( now < ( LAST_TIME + WAIT_TIME ) )
                    {
                        try
                        {
                            Thread.sleep( WAIT_TIME );
                        }
                        catch ( InterruptedException e )
                        {
                            // Ignore
                        }

                        continue;
                    }
                    else
                    {
                        LAST_TIME = now;
                        LAST_SEQ = Short.MIN_VALUE;
                        done = true;
                    }
                }
            }

            return new UUID( LAST_TIME, LAST_SEQ++, HOST_UNIQUE );
        }
    }

    /**
     * Return a new UID.
     */
    public static String generateValue()
    {
        return generate().toString();
    }
}

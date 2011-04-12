/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

/**
 * This class implements the aggregated result.
 */
public final class AggregatedResultImpl
    implements AggregatedResult
{
    /**
     * Count.
     */
    private final int count;

    /**
     * Min.
     */
    private final float minValue;

    /**
     * Max.
     */
    private final float maxValue;

    /**
     * Sum.
     */
    private final float sumValue;

    /**
     * Average.
     */
    private final float averageValue;

    /**
     * Construct the result.
     */
    public AggregatedResultImpl( int count, float minValue, float maxValue, float sumValue, float averageValue )
    {
        this.count = count;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.sumValue = sumValue;
        this.averageValue = averageValue;
    }

    /**
     * Return the count.
     */
    public int getCount()
    {
        return this.count;
    }

    /**
     * Return min value.
     */
    public double getMinValue()
    {
        return this.minValue;
    }

    /**
     * Return max value.
     */
    public double getMaxValue()
    {
        return this.maxValue;
    }

    /**
     * Return average value.
     */
    public double getAverageValue()
    {
        return this.averageValue;
    }

    /**
     * Return sum value.
     */
    public double getSumValue()
    {
        return this.sumValue;
    }
}

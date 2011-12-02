package com.enonic.cms.core.portal.livetrace;


public interface Trace
{
    static final int CONCURRENCY_BLOCK_THRESHOLD = 5;

    SimpleDuration getDuration();
}

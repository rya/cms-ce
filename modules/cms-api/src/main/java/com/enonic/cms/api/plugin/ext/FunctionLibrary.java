package com.enonic.cms.api.plugin.ext;

/**
 * This class implements a function library extension.
 */
public class FunctionLibrary
    extends ExtensionBase
{
    private Object target;

    private Class<?> targetClass;

    /**
     * A string, representing the namespace of the function library.
     * <p/>
     * This namespace is also the prefix to use with a method call in a data source.
     */
    private String name;


    /**
     * A string representing the namespace of the function library. This namespace is also the
     * prefix to use with a method call in a data source.
     */
    public final String getName()
    {
        return name;
    }

    public final void setName( final String name )
    {
        this.name = name;
    }

    public final Object getTarget()
    {
        return this.target;
    }

    public final void setTarget( final Object target )
    {
        this.target = target;
    }

    public final Class<?> getTargetClass()
    {
        return this.targetClass != null ? this.targetClass : this.target.getClass();
    }

    public final void setTargetClass( final Class<?> targetClass )
    {
        this.targetClass = targetClass;
    }

    public String toString()
    {
        return this.target.toString();
    }
}

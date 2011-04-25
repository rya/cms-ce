package com.enonic.esl.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class UncheckedCastUtil
{
    /**
     * Make an unchecked cast of a List.
     * <p/>
     * This is useful because it allows you to make an unchecked
     * cast within a method, without having to taint the whole method
     * with a SuppressWarnings("unchecked") annotation.
     * <p/>
     * See Bug #311: Remove compile warnings
     *
     * @param <T>   the element type
     * @param list  the List to cast
     * @param clazz the element class
     * @return a cast List
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> castList( List list, Class<T> clazz )
    {
        return list;
    }

    /**
     * Make an unchecked cast of a Set.
     * <p/>
     * This is useful because it allows you to make an unchecked
     * cast within a method, without having to taint the whole method
     * with a SuppressWarnings("unchecked") annotation.
     * <p/>
     * See Bug #311: Remove compile warnings
     *
     * @param <T>   the element type
     * @param set   the Set to cast
     * @param clazz the element class
     * @return a cast Set
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> castSet( Set set, Class<T> clazz )
    {
        return set;
    }

    /**
     * Make an unchecked cast of a Collection.
     * <p/>
     * This is useful because it allows you to make an unchecked
     * cast within a method, without having to taint the whole method
     * with a SuppressWarnings("unchecked") annotation.
     * <p/>
     * See Bug #311: Remove compile warnings
     *
     * @param <T>        the element type
     * @param collection the Collection to cast
     * @param clazz      the element class
     * @return a cast Collection
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> castCollection( Collection collection, Class<T> clazz )
    {
        return collection;
    }

    /**
     * Make an unchecked cast of an object.
     * <p/>
     * This is useful because it allows you to make an unchecked
     * cast within a method, without having to taint the whole method
     * with a SuppressWarnings("unchecked") annotation.
     * <p/>
     * See Bug #311: Remove compile warnings
     *
     * @param <T>    the object type
     * @param object the object to class
     * @return the cast object
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast( Object object )
    {
        return (T) object;
    }

    /**
     * Make an unchecked cast of an serializable object.
     * <p/>
     * This is useful because it allows you to make an unchecked
     * cast within a method, without having to taint the whole method
     * with a SuppressWarnings("unchecked") annotation.
     * <p/>
     * See Bug #311: Remove compile warnings
     *
     * @param <T>    the object type
     * @param object the object to class
     * @return the cast object
     */
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T castSerializable( Object object )
    {
        return (T) object;
    }
}

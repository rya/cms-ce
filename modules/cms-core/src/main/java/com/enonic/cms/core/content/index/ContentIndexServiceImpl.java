/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.ContentIndexEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.jdbc.dialect.Dialect;

import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentIndexDao;

import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.translator.AggregatedQueryTranslator;
import com.enonic.cms.core.content.index.translator.ContentQueryTranslator;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.ContentResultSetLazyFetcher;
import com.enonic.cms.core.content.resultset.ContentResultSetNonLazy;

/**
 * This class implements the content index service based on hibernate.
 */
public final class ContentIndexServiceImpl
    implements ContentIndexService, ContentIndexConstants
{
    @Autowired
    private ContentIndexDao contentIndexDao;

    @Autowired
    private ContentDao contentDao;

    private Dialect dialect;

    private enum IndexState
    {
        CHANGED,
        UNCHANGED,
        NEW,
        CHANGED_AND_SHORTENED,
        CHANGED_AND_LENGTHENED
    }

    public void setDialect( Dialect dialect )
    {
        this.dialect = dialect;
    }

    /**
     * @inheritDoc
     */
    public int remove( ContentKey contentKey )
    {
        return contentIndexDao.removeByContentKey( contentKey );
    }

    /**
     * @inheritDoc
     */
    public void removeByCategory( CategoryKey categoryKey )
    {
        contentIndexDao.removeByCategoryKey( categoryKey );
    }

    /**
     * @inheritDoc
     */
    public void removeByContentType( ContentTypeKey contentTypeKey )
    {
        contentIndexDao.removeByContentTypeKey( contentTypeKey );
    }

    /**
     * @inheritDoc
     */
    public void index( ContentDocument doc, boolean deleteExisting )
    {
        doIndex( doc, deleteExisting );
    }

    /**
     * @inheritDoc
     */
    public boolean isIndexed( ContentKey contentKey )
    {
        return contentIndexDao.findCountByContentKey( contentKey ) > 0;
    }

    /**
     * @inheritDoc
     */
    public ContentResultSet query( ContentIndexQuery contentIndexQuery )
    {
        preventExecutionOfTooOpenQuery( contentIndexQuery );

        if ( isFilterBlockingAllContent( contentIndexQuery ) )
        {
            return new ContentResultSetLazyFetcher( new ContentEntityFetcherImpl( contentDao ), new LinkedList<ContentKey>(), 0, 0 );
        }

        TranslatedQuery translated;

        try
        {
            translated = new ContentQueryTranslator( this.dialect ).translate( contentIndexQuery );
        }
        catch ( Throwable e )
        {
            final ContentResultSetNonLazy rs = new ContentResultSetNonLazy( contentIndexQuery.getIndex() );
            rs.addError( "Failed to translate contentQuery ( " + contentIndexQuery + " ): " + e.getMessage() );
            return rs;
        }

        final String hqlStr = translated.getQuery();

        // Important: if we query on any given date (that changes) there is no use in caching the the content query
        boolean cacheable = contentIndexQuery.getContentOnlineAtFilter() == null;
        List<ContentKey> keys = contentIndexDao.findContentKeysByQuery( hqlStr, translated.getParameters(), cacheable );

        final int queryResultTotalSize = keys.size();

        if ( translated.getIndex() > queryResultTotalSize )
        {
            final ContentResultSetNonLazy rs = new ContentResultSetNonLazy( contentIndexQuery.getIndex() );
            rs.addError( "Index greater than result count: " + translated.getIndex() + " greater than " + queryResultTotalSize );
            return rs;
        }

        int fromIndex = Math.max( translated.getIndex(), 0 );
        int toIndex = Math.min( queryResultTotalSize, fromIndex + translated.getCount() );
        final List<ContentKey> actualKeysWanted = keys.subList( fromIndex, toIndex );

        return new ContentResultSetLazyFetcher( new ContentEntityFetcherImpl( contentDao ), actualKeysWanted, fromIndex,
                                                queryResultTotalSize );
    }

    /**
     * Check the filters to see if they may be set so that everything is filtered out. This happens if the filters are not <code>null</code>
     * so that they are applied, but does not contain any elements. If so, there's no point in running the query to the database, as all
     * results will be filtered out anyway.
     *
     * @param query The query, containing all the filters.
     * @return <code>true</code> if the filter does have openings. <code>false</code> if the filters are set so that no result will be let
     *         through the filter, and running the query is superfluous.
     */
    private boolean isFilterBlockingAllContent( ContentIndexQuery query )
    {
        final boolean isCategoryFilterBlocked = ( ( query.getCategoryFilter() != null ) && ( query.getCategoryFilter().size() == 0 ) );
        final boolean isContentFilterBlocked = ( ( query.getContentFilter() != null ) && ( query.getContentFilter().size() == 0 ) );
        final boolean isContentTypeFilterBlocked =
            ( ( query.getContentTypeFilter() != null ) && ( query.getContentTypeFilter().size() == 0 ) );
        final boolean isSectionFilterBlocked = ( ( query.getSectionFilter() != null ) && ( query.getSectionFilter().size() == 0 ) );

        return isCategoryFilterBlocked || isContentFilterBlocked || isContentTypeFilterBlocked || isSectionFilterBlocked;
    }

    private void doIndex( ContentDocument doc, boolean deleteExisting )
    {

        if ( deleteExisting )
        {
            // Remove the contents
            int removeCount = contentIndexDao.removeByContentKey( doc.getContentKey() );
            if ( removeCount > 0 )
            {
                contentIndexDao.getHibernateTemplate().flush();
            }
        }

        // Create a list of entities
        ContentIndexFieldSet set = new ContentIndexFieldSet();
        set.setKey( doc.getContentKey() );
        set.setCategoryKey( doc.getCategoryKey() );
        set.setContentTypeKey( doc.getContentTypeKey() );
        set.setStatus( doc.getStatus() );
        set.setPublishFrom( doc.getPublishFrom() );
        set.setPublishTo( doc.getPublishTo() );
        set.addFieldWithDateValue( F_CREATED, doc.getCreated(), BLANK_REPLACER );
        set.addFieldWithStringValue( F_CONTENT_TYPE_NAME, doc.getContentTypeName() == null ? null : doc.getContentTypeName().getText() );
        set.addFieldWithDateValue( F_TIMESTAMP, doc.getTimestamp(), BLANK_REPLACER );
        set.addFieldWithDateValue( F_MODIFIED, doc.getModified(), BLANK_REPLACER );
        set.addFieldWithStringValue( F_OWNER_KEY, doc.getOwnerKey() == null ? null : doc.getOwnerKey().getText(), BLANK_REPLACER );

        set.addFieldWithStringValue( F_OWNER_QUALIFIEDNAME, translateAnyEscapeCharacterToColon(
            doc.getOwnerQualifiedName() == null ? null : doc.getOwnerQualifiedName().getText() ), BLANK_REPLACER );

        set.addFieldWithStringValue( F_MODIFIER_KEY, doc.getModifierKey() == null ? null : doc.getModifierKey().getText(), BLANK_REPLACER );

        set.addFieldWithStringValue( F_MODIFIER_QUALIFIEDNAME, translateAnyEscapeCharacterToColon(
            doc.getModifierQualifiedName() == null ? null : doc.getModifierQualifiedName().getText() ), BLANK_REPLACER );

        set.addFieldWithStringValue( F_ASSIGNEE_QUALIFIEDNAME, translateAnyEscapeCharacterToColon(
            doc.getAssigneeQualifiedName() == null ? null : doc.getAssigneeQualifiedName().getText() ), BLANK_REPLACER );

        set.addFieldWithStringValue( F_ASSIGNER_QUALIFIEDNAME, translateAnyEscapeCharacterToColon(
            doc.getAssignerQualifiedName() == null ? null : doc.getAssignerQualifiedName().getText() ), BLANK_REPLACER );

        set.addFieldWithDateValue( F_ASSIGNMENT_DUE_DATE, doc.getAssignmentDueDate(), BLANK_REPLACER );

        set.addFieldWithStringValue( F_TITLE, getOracleSafeValue( doc.getTitle() == null ? null : doc.getTitle().getText() ) );
        set.addFieldWithIntegerValue( F_PRIORITY, doc.getPriority() );

        // Add user defined fields
        for ( UserDefinedField userDefinedField : doc.getUserDefinedFields() )
        {
            SimpleText value = userDefinedField.getValue();
            set.addFieldWithAnyValue( userDefinedField.getName(), getOracleSafeValue( value == null ? null : value.getText() ) );
        }

        // Index full text
        final BigText binaryExtractedText = doc.getBinaryExtractedText();
        if ( binaryExtractedText != null )
        {
            set.addFieldWithBigTextValue( F_FULLTEXT, binaryExtractedText );
        }

        if ( deleteExisting )
        {
            contentIndexDao.storeNew( set.getEntitites() );
        }
        else
        {
            handleUpdateIndexes( doc, set.getEntitiesByPath() );

        }

    }

    private void handleUpdateIndexes( ContentDocument doc, HashMap<String, List<ContentIndexEntity>> newEntitiesByPath )
    {
        List<ContentIndexEntity> existingEntities = contentIndexDao.findByContentKey( doc.getContentKey() );
        HashMap<String, List<ContentIndexEntity>> existingEntitiesByPath = createEntitiesByPath( existingEntities );

        Map<String, List<ContentIndexEntity>> entities = createIndexLists( existingEntitiesByPath, newEntitiesByPath );
//        List<ContentIndexEntity> unchangedIndexes = entities.get( "unchanged" );
        List<ContentIndexEntity> changedIndexes = entities.get( "changed" );
        List<ContentIndexEntity> newIndexes = entities.get( "new" );
        List<ContentIndexEntity> removedIndexes = entities.get( "removed" );

        if ( changedIndexes.size() > 0 )
        {
            contentIndexDao.storeAll( changedIndexes );
        }

        if ( newIndexes.size() > 0 )
        {
            contentIndexDao.storeNew( newIndexes );
        }

        if ( removedIndexes.size() > 0 )
        {
            contentIndexDao.remove( removedIndexes );
        }
    }

    private HashMap<String, List<ContentIndexEntity>> createEntitiesByPath( List<ContentIndexEntity> existingEntities )
    {
        HashMap<String, List<ContentIndexEntity>> entitiesByPath = new HashMap<String, List<ContentIndexEntity>>();
        for ( ContentIndexEntity existingEntity : existingEntities )
        {
            List<ContentIndexEntity> existingPathList = entitiesByPath.get( existingEntity.getPath() );
            if ( existingPathList == null )
            {
                List<ContentIndexEntity> newList = new ArrayList<ContentIndexEntity>();
                newList.add( existingEntity );
                entitiesByPath.put( existingEntity.getPath(), newList );
            }
            else
            {
                existingPathList.add( existingEntity );
            }
        }

        return entitiesByPath;
    }

    public Map<String, List<ContentIndexEntity>> createIndexLists( final HashMap<String, List<ContentIndexEntity>> existingEntities,
                                                                   final HashMap<String, List<ContentIndexEntity>> newEntities )
    {
        Map<String, List<ContentIndexEntity>> values = new HashMap<String, List<ContentIndexEntity>>();

        final List<ContentIndexEntity> unchanged = new ArrayList<ContentIndexEntity>();
        final List<ContentIndexEntity> changed = new ArrayList<ContentIndexEntity>();
        final List<ContentIndexEntity> newIndexes = new ArrayList<ContentIndexEntity>();
        List<ContentIndexEntity> removed = new ArrayList<ContentIndexEntity>();

//        for ( ContentIndexEntity contentIndexEntity : newEntities.keySet() )
        for ( String path : newEntities.keySet() )
        {
            IndexAndState indexAndState =
                findMatchingContentIndexEntityAndCheckIndexState( existingEntities.get( path ), newEntities.get( path ) );
            if ( indexAndState.state == IndexState.UNCHANGED )
            {
                unchanged.addAll( indexAndState.entity );
            }
            else if ( indexAndState.state == IndexState.CHANGED )
            {
                changed.addAll( indexAndState.entity );
            }
            else if ( indexAndState.state == IndexState.NEW )
            {
                newIndexes.addAll( indexAndState.entity );
            }
            else if ( indexAndState.state == IndexState.CHANGED_AND_LENGTHENED )
            {
                changed.addAll( indexAndState.entity );
                newIndexes.addAll( indexAndState.extra );
            }
            else if ( indexAndState.state == IndexState.CHANGED_AND_SHORTENED )
            {
                changed.addAll( indexAndState.entity );
                removed.addAll( indexAndState.removed );
            }
        }

        HashSet<String> removedPaths = new HashSet<String>( existingEntities.keySet() );
        removedPaths.removeAll( newEntities.keySet() );
        for ( String removedPath : removedPaths )
        {
            removed.addAll( existingEntities.get( removedPath ) );
        }

        values.put( "unchanged", unchanged );
        values.put( "changed", changed );
        values.put( "new", newIndexes );
        values.put( "removed", removed );
        return values;
    }

    private IndexAndState findMatchingContentIndexEntityAndCheckIndexState( List<ContentIndexEntity> existingEntityValues,
                                                                            List<ContentIndexEntity> newEntityValues )
    {

        if ( existingEntityValues == null )
        {
            IndexAndState indexAndState = new IndexAndState();
            indexAndState.state = IndexState.NEW;
            indexAndState.entity = newEntityValues;
            return indexAndState;
        }

        IndexAndState indexAndState = new IndexAndState();
        if ( isContentIndexEntitySetEquals( existingEntityValues, newEntityValues ) )
        {
            indexAndState.state = IndexState.UNCHANGED;
            indexAndState.entity = existingEntityValues;
        }
        else
        {
            if ( existingEntityValues.size() == newEntityValues.size() )
            {
                indexAndState.state = IndexState.CHANGED;
                indexAndState.entity = copyValues( newEntityValues, existingEntityValues, newEntityValues.size() );
            }
            else
            {
                if ( existingEntityValues.size() > newEntityValues.size() )
                {
                    indexAndState.state = IndexState.CHANGED_AND_SHORTENED;
                    indexAndState.entity = copyValues( newEntityValues, existingEntityValues, newEntityValues.size() );
                    indexAndState.removed = existingEntityValues.subList( newEntityValues.size(), existingEntityValues.size() );
                }
                else
                {
                    indexAndState.state = IndexState.CHANGED_AND_LENGTHENED;
                    indexAndState.entity = copyValues( newEntityValues, existingEntityValues, existingEntityValues.size() );
                    indexAndState.extra = newEntityValues.subList( existingEntityValues.size(), newEntityValues.size() );
                }
            }
        }
        return indexAndState;
    }

    private boolean isContentIndexEntitySetEquals( List<ContentIndexEntity> existingEntityValues, List<ContentIndexEntity> newEntityValues )
    {

        if ( newEntityValues.size() != existingEntityValues.size() )
        {
            return false;
        }

        if ( newEntityValues.size() == 1 )
        {
            return newEntityValues.get( 0 ).valueEquals( existingEntityValues.get( 0 ) );
        }

        for ( ContentIndexEntity newEntity : newEntityValues )
        {
            boolean foundMatch = false;
            for ( ContentIndexEntity existingEntity : existingEntityValues )
            {
                if ( newEntity.valueEquals( existingEntity ) )
                {
                    foundMatch = true;
                    break;
                }
            }
            if ( !foundMatch )
            {
                return false;
            }
        }
        return true;
    }

    private List<ContentIndexEntity> copyValues( List<ContentIndexEntity> fromList, List<ContentIndexEntity> toList, int numValuesToCopy )
    {

        List<ContentIndexEntity> toListWithNewValues = new ArrayList<ContentIndexEntity>();

        for ( int i = 0; i < numValuesToCopy; i++ )
        {
            ContentIndexEntity from = fromList.get( i );
            ContentIndexEntity to = toList.get( i );
            verifyContent( from, to, numValuesToCopy );
            to.setCategoryKey( from.getCategoryKey() );
            to.setContentKey( from.getContentKey() );
            to.setContentStatus( from.getContentStatus() );
            to.setContentTypeKey( from.getContentTypeKey() );
            to.setNumValue( from.getNumValue() );
            to.setOrderValue( from.getOrderValue() );
            to.setPath( from.getPath() );
            to.setPublishFrom( from.getContentPublishFrom() );
            to.setPublishTo( from.getContentPublishTo() );
            to.setValue( from.getValue() );
            toListWithNewValues.add( to );
        }
        return toListWithNewValues;
    }

    private void verifyContent( ContentIndexEntity from, ContentIndexEntity to, int numValuesToCopy )
    {
        if ( from == null && to == null )
        {
            throw new ArrayIndexOutOfBoundsException(
                "ContentIndexServiceImpl.copyValues(): Neither fromList nor toList contains " + numValuesToCopy + " elements." );
        }
        if ( from == null )
        {
            throw new ArrayIndexOutOfBoundsException(
                "ContentIndexServiceImpl.copyValues(): fromList does not contain " + numValuesToCopy + " elements.  Path in toList is:" +
                    to.getPath() );
        }
        if ( to == null )
        {
            throw new ArrayIndexOutOfBoundsException(
                "ContentIndexServiceImpl.copyValues(): toList does not contain " + numValuesToCopy + " elements.  Path in fromList is:" +
                    from.getPath() );
        }
    }

    private String translateAnyEscapeCharacterToColon( String value )
    {
        if ( value == null )
        {
            return null;
        }
        return value.replace( "\\", ":" );
    }

    /**
     * Oracle translate empty string to null, so we can not let that happen since our value field cannot be null.
     *
     * @param value The value to check.
     * @return If the input value was <code>null</code> or empty string, a '#' is returned. Otherwise the input value is returned without
     *         changes.
     */
    private String getOracleSafeValue( String value )
    {

        if ( value == null || value.length() == 0 )
        {
            return BLANK_REPLACER;
        }

        return value;
    }

    /**
     * @inheritDoc
     */
    public IndexValueResultSet query( IndexValueQuery query )
    {
        IndexValueQueryTranslator translator = new IndexValueQueryTranslator();
        TranslatedQuery translated = translator.translate( query );
        List<Object[]> list = contentIndexDao.findIndexValues( translated.getQuery() );

        int totalSize = list.size();
        int fromIndex = Math.min( Math.max( 0, translated.getIndex() ), Math.max( list.size() - 1, 0 ) );
        int toIndex = Math.max( list.size() - 1, 0 );

        if ( translated.getCount() > -1 )
        {
            toIndex = Math.min( fromIndex + translated.getCount(), Math.max( list.size() - 1, 0 ) );
        }

        list.subList( fromIndex, toIndex );

        IndexValueResultSetImpl result = new IndexValueResultSetImpl( fromIndex, totalSize );
        for ( Object[] entry : list )
        {
            result.add( createIndexValueResult( entry ) );
        }

        return result;
    }

    /**
     * Create index value result.
     *
     * @param values The entries to get index values for.
     * @return An index value result holder for the given values.
     */
    private IndexValueResultImpl createIndexValueResult( Object[] values )
    {
        ContentKey contentKey = (ContentKey) values[0];
        String value = (String) values[1];
        return new IndexValueResultImpl( contentKey, value );
    }

    /**
     * @inheritDoc
     */
    public AggregatedResult query( AggregatedQuery query )
    {
        AggregatedQueryTranslator translator = new AggregatedQueryTranslator();
        TranslatedQuery translated = translator.translate( query );
        List<Object[]> list = contentIndexDao.findIndexValues( translated.getQuery() );

        Object[] values = list.get( 0 );
        Number count = (Number) values[0];
        Number minValue = (Number) values[1];
        Number maxValue = (Number) values[2];
        Number sumValue = (Number) values[3];
        Number averageValue = (Number) values[4];

        return new AggregatedResultImpl( count.intValue(), minValue == null ? 0 : minValue.floatValue(),
                                         maxValue == null ? 0 : maxValue.floatValue(), sumValue == null ? 0 : sumValue.floatValue(),
                                         averageValue == null ? 0 : averageValue.floatValue() );
    }

    class IndexAndState
    {
        protected IndexState state;

        protected List<ContentIndexEntity> entity;

        protected List<ContentIndexEntity> extra;

        protected List<ContentIndexEntity> removed;
    }

    private void preventExecutionOfTooOpenQuery( ContentIndexQuery contentQuery )
    {
        boolean noCategoryFilter = contentQuery.getCategoryFilter() == null || contentQuery.getCategoryFilter().size() == 0;
        boolean noContentFilter = contentQuery.getContentFilter() == null || contentQuery.getContentFilter().size() == 0;
        boolean noSectionFilter = contentQuery.getSectionFilter() == null || contentQuery.getSectionFilter().size() == 0;
        //boolean noContentTypeFilter = contentQuery.getContentTypeFilter() == null || contentQuery.getContentTypeFilter().size() == 0;

        if ( noCategoryFilter && noSectionFilter && noContentFilter )
        {
            boolean noQueryFilter = StringUtils.isBlank( contentQuery.getQuery() );

            // only allow a non-restricted search if count is lower than 100
            if ( noQueryFilter && contentQuery.getCount() > 100 )
            {
                throw new IllegalArgumentException(
                    "Prevented executing a content index query that is too open (i.e. possibly fetching all content): " +
                        contentQuery.toString() );
            }
        }

    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.security.user.UserKey;

/**
 * This class implements the content resource.
 */
public final class ContentDocument
    implements ContentIndexConstants
{
    private final static HashSet<String> PROTECTED_FIELDS = new HashSet<String>( Arrays.asList( ALL_FIELDS ) );

    private final ContentKey contentKey;

    private CategoryKey categoryKey;

    private ContentTypeKey contentTypeKey;

    private SimpleText contentTypeName;

    private SimpleText title;

    private SimpleText ownerKey;

    private SimpleText ownerQualifiedName;

    private SimpleText modifierKey;

    private SimpleText modifierQualifiedName;

    private UserKey assigneeKey;

    private SimpleText assigneeQualifiedName;

    private UserKey assignerKey;

    private SimpleText assignerQualifiedName;

    private Date assignmentDueDate;

    private Date created;

    private Date publishFrom;

    private Integer status;

    private Integer priority;

    private Date publishTo;

    /**
     * The date time when the content as whole was last modifed.
     */
    private Date timestamp;

    /**
     * The date time when the content data was last modified.
     */
    private Date modified;

    private final Collection<UserDefinedField> userDefinedFields;

    private BigText binaryExtractedText;

    public ContentDocument( ContentKey contentKey )
    {
        this.contentKey = contentKey;
        this.userDefinedFields = new ArrayList<UserDefinedField>();
    }

    public ContentKey getContentKey()
    {
        return this.contentKey;
    }

    public CategoryKey getCategoryKey()
    {
        return this.categoryKey;
    }

    public void setCategoryKey( CategoryKey categoryKey )
    {
        this.categoryKey = categoryKey;
    }

    public ContentTypeKey getContentTypeKey()
    {
        return this.contentTypeKey;
    }

    public void setContentTypeKey( ContentTypeKey contentTypeKey )
    {
        this.contentTypeKey = contentTypeKey;
    }

    public SimpleText getContentTypeName()
    {
        return contentTypeName;
    }

    public void setContentTypeName( String name )
    {
        contentTypeName = new SimpleText( name );
    }

    public void addUserDefinedField( UserDefinedField field )
    {
        if ( !isProtectedField( field.getName() ) )
        {
            this.userDefinedFields.add( field );
        }
    }

    public void addUserDefinedField( String name, String value )
    {
        addUserDefinedField( new UserDefinedField( name, new SimpleText( value ) ) );
    }

    public void addUserDefinedField( String name, SimpleText value )
    {
        addUserDefinedField( new UserDefinedField( name, value ) );
    }


    public Collection<UserDefinedField> getUserDefinedFields()
    {
        return this.userDefinedFields;
    }

    public BigText getBinaryExtractedText()
    {
        return this.binaryExtractedText;
    }

    public void setBinaryExtractedText( BigText value )
    {
        this.binaryExtractedText = value;
    }

    public SimpleText getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = new SimpleText( title );
    }

    public SimpleText getOwnerKey()
    {
        return ownerKey;
    }

    public void setOwnerKey( String key )
    {
        ownerKey = new SimpleText( key );
        // If the key contains ASCII control characters, something is seriously wrong.
        assert ( ownerKey.getText().equals( key.trim() ) );
    }

    public SimpleText getModifierKey()
    {
        return modifierKey;
    }

    public void setModifierKey( String key )
    {
        modifierKey = new SimpleText( key );
        // If the key contains ASCII control characters, something is seriously wrong.
        assert ( modifierKey.getText().equals( key.trim() ) );
    }

    public UserKey getAssigneeKey()
    {
        return assigneeKey;
    }

    public void setAssigneeKey( UserKey assigneeKey )
    {
        this.assigneeKey = assigneeKey;
    }

    public SimpleText getAssigneeQualifiedName()
    {
        return assigneeQualifiedName;
    }

    public void setAssigneeQualifiedName( String value )
    {
        this.assigneeQualifiedName = new SimpleText( value );
    }

    public UserKey getAssignerKey()
    {
        return assignerKey;
    }

    public void setAssignerKey( UserKey value )
    {
        this.assignerKey = value;
    }

    public SimpleText getAssignerQualifiedName()
    {
        return assignerQualifiedName;
    }

    public void setAssignerQualifiedName( String value )
    {
        this.assignerQualifiedName = new SimpleText( value );
    }

    public Date getAssignmentDueDate()
    {
        return assignmentDueDate;
    }

    public void setAssignmentDueDate( Date assignmentDueDate )
    {
        this.assignmentDueDate = assignmentDueDate;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated( Date created )
    {
        this.created = created;
    }

    public Date getPublishFrom()
    {
        return publishFrom;
    }

    public void setPublishFrom( Date publishFrom )
    {
        this.publishFrom = publishFrom;
    }

    public Date getPublishTo()
    {
        return publishTo;
    }

    public void setPublishTo( Date publishTo )
    {
        this.publishTo = publishTo;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

    public Date getModified()
    {
        return modified;
    }

    public void setModified( Date modified )
    {
        this.modified = modified;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus( Integer value )
    {
        this.status = value;
    }

    public Integer getPriority()
    {
        return priority;
    }

    public void setPriority( Integer value )
    {
        this.priority = value;
    }

    public SimpleText getOwnerQualifiedName()
    {
        return ownerQualifiedName;
    }

    public void setOwnerQualifiedName( String value )
    {
        ownerQualifiedName = new SimpleText( value );
    }

    public SimpleText getModifierQualifiedName()
    {
        return modifierQualifiedName;
    }

    public void setModifierQualifiedName( String value )
    {
        modifierQualifiedName = new SimpleText( value );
    }

    private boolean isProtectedField( String field )
    {
        return PROTECTED_FIELDS.contains( field );
    }
}

/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.fieldtypes;

import org.w3c.dom.Element;

public class FieldFactory
{

    public static Field getField( Element inputElem )
    {
        String fieldType = inputElem.getAttribute( "type" );

        if ( "text".equals( fieldType ) )
        {
            return new Text( inputElem );
        }
        else if ( "textarea".equals( fieldType ) )
        {
            return new TextArea( inputElem );
        }
        else if ( "htmlarea".equals( fieldType ) )
        {
            return new HtmlArea( inputElem );
        }
        else if ( "simplehtmlarea".equals( fieldType ) )
        {
            return new HtmlArea( inputElem ); // no special treatment
        }
        else if ( "url".equals( fieldType ) )
        {
            return new Url( inputElem );
        }
        else if ( "date".equals( fieldType ) )
        {
            return new Date( inputElem );
        }
        else if ( "checkbox".equals( fieldType ) )
        {
            return new CheckBox( inputElem );
        }
        else if ( "radiobutton".equals( fieldType ) )
        {
            return new RadioButton( inputElem );
        }
        else if ( "dropdown".equals( fieldType ) )
        {
            return new DropDown( inputElem );
        }
        else if ( "relatedcontent".equals( fieldType ) )
        {
            return new RelatedContent( inputElem );
        }
        else if ( "file".equals( fieldType ) )
        {
            return new File( inputElem );
        }
        else if ( "files".equals( fieldType ) )
        {
            return new Files( inputElem );
        }
        else if ( "uploadfile".equals( fieldType ) )
        {
            return new UploadFile( inputElem );
        }
        else if ( "image".equals( fieldType ) )
        {
            return new Image( inputElem );
        }
        else if ( "images".equals( fieldType ) )
        {
            return new Images( inputElem );
        }
        else if ( "multiplechoice".equals( fieldType ) )
        {
            return new MultipleChoice( inputElem );
        }
        else if ( "xml".equals( fieldType ) )
        {
            return new XML( inputElem );
        }
        else
        {
            return null;
        }
    }
}

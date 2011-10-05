/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.xmlbuilders;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.AdminHandlerBaseServlet;
import com.enonic.vertical.adminweb.VerticalAdminException;
import com.enonic.vertical.adminweb.VerticalAdminLogger;

import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.business.core.content.image.ContentImageUtil;
import com.enonic.cms.business.core.content.image.ImageUtil;

import com.enonic.cms.domain.content.binary.BinaryData;
import com.enonic.cms.domain.security.user.User;

public class ContentEnhancedImageXMLBuilder
    extends ContentBaseXMLBuilder
{

    public String getTitleFormKey()
    {
        return "name";
    }

    public String getContentTitle( Element contentDataElem, int contentTypeKey )
    {
        return XMLTool.getElementText( XMLTool.getElement( contentDataElem, "name" ) );
    }

    public int[] getRelatedContentKeys( ExtendedMap formItems )
    {
        return AdminHandlerBaseServlet.getIntArrayFormItem( formItems, "relatedfile" );
    }

    public void buildContentTypeXML( User user, Document doc, Element contentdata, ExtendedMap formItems )
        throws VerticalAdminException
    {

        // Name
        XMLTool.createElement( doc, contentdata, "name", formItems.getString( "name" ) );

        // Description
        Element tempElement = XMLTool.createElement( doc, contentdata, "description" );
        XMLTool.createCDATASection( doc, tempElement, formItems.getString( "description", "" ) );

        // Photographer
        tempElement = XMLTool.createElement( doc, contentdata, "photographer" );
        tempElement.setAttribute( "name", formItems.getString( "photographername", "" ) );
        tempElement.setAttribute( "email", formItems.getString( "photographeremail", "" ) );

        // Copyright
        XMLTool.createElement( doc, contentdata, "copyright", formItems.getString( "copyright", "" ) );

        // keywords
        XMLTool.createElement( doc, contentdata, "keywords", formItems.getString( "keywords", "" ) );

        // sourceimage
        //String sourceKey = (String) ;
        if ( formItems.containsKey( "sourceimagekey" ) )
        {
            tempElement = XMLTool.createElement( doc, contentdata, "sourceimage" );
            tempElement.setAttribute( "width", formItems.getString( "originalwidth" ) );
            tempElement.setAttribute( "height", formItems.getString( "originalheight" ) );
            tempElement = XMLTool.createElement( doc, tempElement, "binarydata" );
            tempElement.setAttribute( "key", formItems.getString( "sourceimagekey" ) );
        }

        // Images
        Element images = XMLTool.createElement( doc, contentdata, "images" );
        if ( "on".equals( formItems.getString( "chkorgimageborder", null ) ) )
        {
            images.setAttribute( "border", "yes" );
        }
        else
        {
            images.setAttribute( "border", "no" );
        }

        // Original image
        Element image = XMLTool.createElement( doc, images, "image" );
        image.setAttribute( "type", "original" );
        String tmp = formItems.getString( "rotate", null );
        if ( tmp != null )
        {
            image.setAttribute( "rotation", tmp );
        }

        // Check if image is set. This should be done in the client for better control.
        if ( !formItems.containsKey( "originalwidth" ) )
        {
            throw new VerticalAdminException( "Image is not set. Please choose an image to upload." );
        }

        String width = formItems.getString( "originalwidth" );
        String height = formItems.getString( "originalheight" );

        XMLTool.createElement( doc, image, "width", width );
        XMLTool.createElement( doc, image, "height", height );
        tempElement = XMLTool.createElement( doc, image, "binarydata" );
        tempElement.setAttribute( "key", formItems.getString( "originalbinarydatakey" ) );

        // related file
        if ( formItems.containsKey( "relatedfile" ) )
        {
            tempElement = XMLTool.createElement( doc, contentdata, "file" );
            tempElement.setAttribute( "key", formItems.getString( "relatedfile" ) );
        }
    }

    public void applyImageData( ExtendedMap formItems )
    {

        if ( "on".equals( formItems.getString( "chkorigimageborder", null ) ) )
        {
            // original width
            int tmp = formItems.getInt( "originalwidth" );
            formItems.put( "originalwidth", new Integer( tmp - 2 ) );

            // original height
            tmp = formItems.getInt( "originalheight" );
            formItems.put( "originalheight", new Integer( tmp - 2 ) );

            // custom width
            tmp = formItems.getInt( "customwidth" );
            formItems.put( "customwidth", new Integer( tmp - 2 ) );

            // custom height
            tmp = formItems.getInt( "customheight" );
            formItems.put( "customheight", new Integer( tmp - 2 ) );
        }

        ArrayList<String[]> otherImages = new ArrayList<String[]>();
        formItems.put( "otherimages", otherImages );
        String[] keys = AdminHandlerBaseServlet.getArrayFormItem( formItems, "scaledimage" );
        String[] widths = AdminHandlerBaseServlet.getArrayFormItem( formItems, "scaledimagewidth" );
        String[] heights = AdminHandlerBaseServlet.getArrayFormItem( formItems, "scaledimageheight" );

        int i = 0;
        for (; i < keys.length; i++ )
        {
            otherImages.add( new String[]{keys[i], widths[i], heights[i]} );
        }
    }

    public int[] getDeleteBinaries( ExtendedMap formItems )
        throws VerticalAdminException
    {
        if ( formItems.getBoolean( "newimage", false ) )
        {
            int versionKey = formItems.getInt( "versionkey" );
            return admin.getBinaryDataKeysByVersion( versionKey );
        }
        else
        {
            return null;
        }
    }

    public BinaryData[] getBinaries( ExtendedMap formItems )
        throws VerticalAdminException
    {
        ArrayList<BinaryData> binaryList = null;

        if ( formItems.getBoolean( "newimage", false ) )
        {
            FileItem imageFile = formItems.getFileItem( "origimagefilename" );
            BinaryData image = AdminHandlerBaseServlet.createBinaryData( imageFile, "source" );

            try
            {
                // find file type
                String type = null;
                int idx = image.fileName.lastIndexOf( "." );
                if ( idx != -1 )
                {
                    type = image.fileName.substring( idx + 1 ).toLowerCase();
                    if ( "jpg".equals( type ) )
                    {
                        type = "jpeg";
                    }
                }

                // Rotate source image?
                String rotate = formItems.getString( "rotate", "none" );
                if ( !"none".equals( rotate ) )
                {
                    image.data = rotateImage( rotate, image.data, ContentImageUtil.getEncodeType( type ) );
                }

                String filenameWithoutExtension = image.fileName.substring( 0, idx );

                BufferedImage origImage = ImageUtil.readImage( image.data );
                if ( !formItems.containsKey( "originalwidth" ) )
                {
                    formItems.put( "originalwidth", origImage.getWidth() );
                }
                if ( !formItems.containsKey( "originalheight" ) )
                {
                    formItems.put( "originalheight", origImage.getHeight() );
                }

                // scale all images
                binaryList =
                    scaleAndAddImages( formItems, admin, origImage, ContentImageUtil.getEncodeType( type ), filenameWithoutExtension );

                // add source image
                binaryList.add( image );
                formItems.put( "sourceimagekey", "%" + ( binaryList.size() - 1 ) );
            }
            catch ( IOException ioe )
            {
                VerticalAdminLogger.errorAdmin( this.getClass(), 10, "I/O error processing file \"" + image.fileName + "\": %t", ioe );
            }
        }
        else
        {
            applyImageData( formItems );
        }
        if ( binaryList == null )
        {
            return null;
        }
        else
        {
            return binaryList.toArray( new BinaryData[binaryList.size()] );
        }
    }

    private byte[] rotateImage( String rotate, byte[] image, String encodeType )
        throws IOException
    {
        BufferedImage bufferedImage = ImageUtil.readImage( image );
        if ( "90left".equals( rotate ) )
        {
            bufferedImage = ImageUtil.rotateImage270( bufferedImage, ContentImageUtil.getBufferedImageType( encodeType ) );
        }
        else if ( "90right".equals( rotate ) )
        {
            bufferedImage = ImageUtil.rotateImage90( bufferedImage, ContentImageUtil.getBufferedImageType( encodeType ) );
        }
        else if ( "180".equals( rotate ) )
        {
            bufferedImage = ImageUtil.rotateImage180( bufferedImage, ContentImageUtil.getBufferedImageType( encodeType ) );
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageUtil.writeImage( bufferedImage, encodeType, baos, 1.0f );
        return baos.toByteArray();
    }

    private ArrayList<BinaryData> scaleAndAddImages( ExtendedMap formItems, AdminService admin, BufferedImage origImage, String encodeType,
                                                     String originalFilenameWithoutExtension )
        throws VerticalAdminException, IOException
    {
        ArrayList<BinaryData> binaryData = new ArrayList<BinaryData>();

        binaryData.add( getOriginalImage( origImage, encodeType, originalFilenameWithoutExtension ) );

        formItems.put( "originalbinarydatakey", "%0" );

        final List<BinaryData> newStyleScaledImages =
            ContentImageUtil.createStandardSizeImages( origImage, encodeType, originalFilenameWithoutExtension );
        binaryData.addAll( newStyleScaledImages );

        return binaryData;
    }

    private BinaryData getOriginalImage( BufferedImage origImage, String encodeType, String originalFilenameWithoutExtension )
        throws IOException
    {
        // Original image:
        BufferedImage tmpImage = origImage;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageUtil.writeImage( tmpImage, encodeType, baos );
        final String originalfileFilename =
            ContentImageUtil.resolveFilenameForScaledImage( originalFilenameWithoutExtension, tmpImage, encodeType );

        return BinaryData.createBinaryDataFromStream( baos, originalfileFilename );
    }
}

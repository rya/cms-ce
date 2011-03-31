/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.xmlbuilders;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.fileupload.FileItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.util.ImageUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.AdminHandlerBaseServlet;
import com.enonic.vertical.adminweb.VerticalAdminException;
import com.enonic.vertical.adminweb.VerticalAdminLogger;
import com.enonic.vertical.adminweb.handlers.ContentBaseHandlerServlet;
import com.enonic.vertical.adminweb.handlers.ContentEnhancedImageHandlerServlet;

import com.enonic.cms.framework.util.TIntArrayList;

import com.enonic.cms.core.service.AdminService;

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

        // Custom image
        width = formItems.getString( "customwidth", null );
        height = formItems.getString( "customheight", null );
        if ( width != null && height != null )
        {
            image = XMLTool.createElement( doc, images, "image" );
            image.setAttribute( "type", "custom" );
            XMLTool.createElement( doc, image, "width", width );
            XMLTool.createElement( doc, image, "height", height );
            tempElement = XMLTool.createElement( doc, image, "binarydata" );
            tempElement.setAttribute( "key", formItems.getString( "custombinarydatakey" ) );
        }

        // All the other images:
        ArrayList scaledImages = (ArrayList) formItems.get( "otherimages" );
        for ( Object scaledImage : scaledImages )
        {
            image = XMLTool.createElement( doc, images, "image" );
            image.setAttribute( "type", "scaled" );
            XMLTool.createElement( doc, image, "width", ( (String[]) scaledImage )[1] );
            XMLTool.createElement( doc, image, "height", ( (String[]) scaledImage )[2] );
            XMLTool.createElement( doc, image, "binarydata" ).setAttribute( "key", ( (String[]) scaledImage )[0] );
        }

        // related file
        if ( formItems.containsKey( "relatedfile" ) )
        {
            tempElement = XMLTool.createElement( doc, contentdata, "file" );
            tempElement.setAttribute( "key", formItems.getString( "relatedfile" ) );
        }
    }

    private static class SizeData
    {
        int[] maxSizes;

        int[] fixedWidths;

        String customType = null;

        int customValue = -1;
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
                    image.data = rotateImage( rotate, image.data, getEncodeType( type ) );
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
                binaryList = scaleAndAddImages( formItems, admin, origImage, getEncodeType( type ), filenameWithoutExtension );

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

    private String getEncodeType( String type )
    {
        String encodeType;

        if ( "png".equals( type ) || "gif".equals( type ) )
        {
            encodeType = "png";
        }
        else
        {
            encodeType = "jpeg";
        }
        return encodeType;
    }

    private byte[] rotateImage( String rotate, byte[] image, String encodeType )
        throws IOException
    {
        BufferedImage bufferedImage = ImageUtil.readImage( image );
        if ( "90left".equals( rotate ) )
        {
            bufferedImage = ImageUtil.rotateImage270( bufferedImage, getBufferedImageType( encodeType ) );
        }
        else if ( "90right".equals( rotate ) )
        {
            bufferedImage = ImageUtil.rotateImage90( bufferedImage, getBufferedImageType( encodeType ) );
        }
        else if ( "180".equals( rotate ) )
        {
            bufferedImage = ImageUtil.rotateImage180( bufferedImage, getBufferedImageType( encodeType ) );
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageUtil.writeImage( bufferedImage, encodeType, baos, 1.0f );
        return baos.toByteArray();
    }


    public SizeData getImageSizeConfiguration( AdminService admin, int contentTypeKey )
        throws VerticalAdminException
    {

        SizeData sd = new SizeData();
        TIntArrayList fixedWidths = new TIntArrayList();
        TIntArrayList maxSizes = new TIntArrayList();

        // Is it configured in the contenttype configuration?
        Document doc = XMLTool.domparse( admin.getContentTypeModuleData( contentTypeKey ) );
        Element rootElement = doc.getDocumentElement();
        Element sizesElement;
        sizesElement = (Element) XMLTool.selectNode( rootElement, "/moduledata/config/sizes" );
        VerticalAdminLogger.debug( ContentEnhancedImageHandlerServlet.class, 10, doc );
        if ( sizesElement == null )
        {
            doc = XMLTool.domparse( admin.getContentHandlerByContentType( contentTypeKey ) );
            rootElement = doc.getDocumentElement();

            VerticalAdminLogger.debug( ContentEnhancedImageHandlerServlet.class, 10, doc );
        }

        NodeList nodeList = XMLTool.selectNodes( rootElement, "//config/sizes/size" );
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element tmpElement = (Element)nodeList.item( i );
            if ( "max".equals( tmpElement.getAttribute( "type" ) ) )
            {
                maxSizes.add( Integer.parseInt( tmpElement.getAttribute( "value" ) ) );
            }
            else if ( "width".equals( tmpElement.getAttribute( "type" ) ) )
            {
                fixedWidths.add( Integer.parseInt( tmpElement.getAttribute( "value" ) ) );
            }
        }

        Element defaultCustomElement = (Element) XMLTool.selectNode( rootElement, "//config/sizes/defaultcustom" );
        if ( defaultCustomElement != null )
        {
            sd.customType = defaultCustomElement.getAttribute( "type" );
            sd.customValue = Integer.parseInt( defaultCustomElement.getAttribute( "value" ) );
        }

//        // check for valid sizes:
//        if ( sd.customType == null || sd.customValue == -1 )
//        {
//            VerticalAdminLogger.errorAdmin( ContentEnhancedImageHandlerServlet.class, 30,
//                                            "No default custom size configured. Image upload aborted.", null );
//        }

        sd.fixedWidths = fixedWidths.toArray();
        sd.maxSizes = maxSizes.toArray();

        return sd;
    }

    public ArrayList<BinaryData> scaleAndAddImages( ExtendedMap formItems, AdminService admin, BufferedImage origImage, String encodeType,
                                                    String originalFilenameWithoutExtension )
        throws VerticalAdminException, IOException
    {

        int contentTypeKey = ContentBaseHandlerServlet.getContentTypeKey( formItems );

        // Get image size configuration:
        SizeData sd = getImageSizeConfiguration( admin, contentTypeKey );

        int index = 0;
        ArrayList<BinaryData> binaryData = new ArrayList<BinaryData>();

        HashMap<String, String> scaled = new HashMap<String, String>();

        // Original image:
        BufferedImage tmpImage = origImage;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageUtil.writeImage( tmpImage, encodeType, baos );
        final String originalfileFilename = resolveFilenameForScaledImage( originalFilenameWithoutExtension, tmpImage, encodeType );
        binaryData.add( AdminHandlerBaseServlet.createBinaryDataFromStream( baos, originalfileFilename ) );
        formItems.put( "originalbinarydatakey", "%" + String.valueOf( index ) );
        index++;

        if ( sd.customType != null )
        {
            int[] customSize = generateCustomSize( origImage, sd );
            BufferedImage customImage = ImageUtil.scaleImage( origImage, customSize[0], customSize[1], getBufferedImageType( encodeType ) );
            formItems.put( "customwidth", String.valueOf( customSize[0] ) );
            formItems.put( "customheight", String.valueOf( customSize[1] ) );
            scaled.put( String.valueOf( customSize[0] ) + "x" + String.valueOf( customSize[1] ), "" );
            baos = new ByteArrayOutputStream();
            ImageUtil.writeImage( customImage, encodeType, baos );
            final String customsizedfileFilename =
                resolveFilenameForScaledImage( originalFilenameWithoutExtension, customImage, encodeType );
            binaryData.add( AdminHandlerBaseServlet.createBinaryDataFromStream( baos, customsizedfileFilename ) );
            formItems.put( "custombinarydatakey", "%" + String.valueOf( index ) );
            index++;
        }
        // Other images:
        ArrayList<String[]> scaledImages = new ArrayList<String[]>();
        formItems.put( "otherimages", scaledImages );

        // Scale one image down to 800xN:
        BufferedImage preScaledImage = origImage;
        if ( origImage.getWidth() > 800 )
        {
            preScaledImage =
                ImageUtil.scaleImage( origImage, 800, (int) Math.round( ( 800.0 / origImage.getWidth() ) * origImage.getHeight() ),
                                      getBufferedImageType( encodeType ) );
        }

        // - fixed width:
        for ( int i = 0; i < sd.fixedWidths.length; i++ )
        {
            int newWidth = sd.fixedWidths[i];

            // skip if new width is larger or equal to original:
            if ( newWidth >= origImage.getWidth() )
            {
                continue;
            }

            double ratio = (double) newWidth / (double) preScaledImage.getWidth();
            int newHeight = (int) Math.max( 1.0, Math.round( ratio * preScaledImage.getHeight() ) );
            if ( scaled.get( String.valueOf( newWidth ) + "x" + String.valueOf( newHeight ) ) == null )
            {
                tmpImage = ImageUtil.scaleImage( preScaledImage, newWidth, newHeight, getBufferedImageType( encodeType ) );

                baos = new ByteArrayOutputStream();
                ImageUtil.writeImage( tmpImage, encodeType, baos );
                final String filename = resolveFilenameForScaledImage( originalFilenameWithoutExtension, tmpImage, encodeType );
                binaryData.add( AdminHandlerBaseServlet.createBinaryDataFromStream( baos, filename ) );
                scaledImages.add( new String[]{"%" + String.valueOf( index ), String.valueOf( newWidth ), String.valueOf( newHeight )} );
                index++;

                scaled.put( String.valueOf( newWidth ) + "x" + String.valueOf( newHeight ), "" );
            }
        }

        // max both width and height:
        for ( int i = 0; i < sd.maxSizes.length; i++ )
        {
            double maxSize = sd.maxSizes[i];
            double oldWidth = preScaledImage.getWidth();
            double oldHeight = preScaledImage.getHeight();
            double newWidth;
            double newHeight;

            if ( oldWidth > oldHeight )
            {
                newWidth = maxSize;
                double ratio = newWidth / oldWidth;
                newHeight = Math.max( 1.0, Math.round( ratio * oldHeight ) );
            }
            else
            {
                newHeight = maxSize;
                double ratio = newHeight / oldHeight;
                newWidth = Math.max( 1.0, Math.round( ratio * oldWidth ) );
            }

            // skip if new size is larger or equal to original:
            if ( newWidth >= origImage.getWidth() || newHeight >= origImage.getHeight() )
            {
                continue;
            }

            if ( scaled.get( String.valueOf( (int) newWidth ) + "x" + String.valueOf( (int) newHeight ) ) == null )
            {
                tmpImage = ImageUtil.scaleImage( preScaledImage, (int) newWidth, (int) newHeight, getBufferedImageType( encodeType ) );

                baos = new ByteArrayOutputStream();
                ImageUtil.writeImage( tmpImage, encodeType, baos );
                final String filename = resolveFilenameForScaledImage( originalFilenameWithoutExtension, tmpImage, encodeType );
                binaryData.add( AdminHandlerBaseServlet.createBinaryDataFromStream( baos, filename ) );
                scaledImages.add(
                    new String[]{"%" + String.valueOf( index ), String.valueOf( (int) newWidth ), String.valueOf( (int) newHeight )} );
                index++;

                scaled.put( String.valueOf( (int) newWidth ) + "x" + String.valueOf( (int) newHeight ), "" );
            }
        }

        // Fixed width images
        final int[] fixedWidthSizes = {256, 512, 1024};
        final String[] fizedWidthLabels = {"small", "medium", "large"};
        for ( int i = 0; i < fixedWidthSizes.length; i++ )
        {
            final int newWidth = fixedWidthSizes[i];
            if ( newWidth < origImage.getWidth() )
            {
                final double ratio = (double) newWidth / (double) origImage.getWidth();
                final int newHeight = (int) Math.max( 1.0, Math.round( ratio * origImage.getHeight() ) );
                tmpImage = ImageUtil.scaleImage( origImage, newWidth, newHeight, getBufferedImageType( encodeType ) );

                baos = new ByteArrayOutputStream();
                ImageUtil.writeImage( tmpImage, encodeType, baos, 1.0f );
                final String filename =
                    resolveFilenameForFixedWidthImage( originalFilenameWithoutExtension, fizedWidthLabels[i], encodeType );
                binaryData.add( AdminHandlerBaseServlet.createBinaryDataFromStream( baos, filename, fizedWidthLabels[i] ) );
            }
        }

        return binaryData;
    }

    private int getBufferedImageType( String encodeType )
    {
        if ( encodeType.equals( "png" ) )
        {
            return BufferedImage.TYPE_INT_ARGB;
        }
        else
        {
            return BufferedImage.TYPE_INT_RGB;
        }
    }

    private static String resolveFilenameForScaledImage( String originalImageName, BufferedImage image, String fileType )
    {
        StringBuffer name = new StringBuffer();
        name.append( originalImageName ).append( "_" ).append( image.getWidth() ).append( "x" ).append( image.getHeight() );
        name.append( "." ).append( fileType );
        return name.toString();
    }

    private static String resolveFilenameForFixedWidthImage( String originalImageName, String label, String fileType )
    {
        return originalImageName + ( label != null ? "_" + label : "" ) + "." + fileType;
    }

    private int[] generateCustomSize( RenderedImage origImage, SizeData sd )
        throws VerticalAdminException
    {
        float[] customSize = new float[2];

        String type = sd.customType;

        String value = null;
        if ( value == null )
        {
            value = String.valueOf( sd.customValue );
        }

        if ( "max".equals( type ) )
        {
            if ( origImage.getWidth() > origImage.getHeight() )
            {
                type = "width";
            }
            else
            {
                type = "height";
            }
        }

        if ( type.equals( "width" ) )
        {
            customSize[0] = Float.parseFloat( value );
            double ratio = customSize[0] / origImage.getWidth();
            customSize[1] = Math.round( ratio * origImage.getHeight() );
        }
        else if ( type.equals( "height" ) )
        {
            customSize[1] = Float.parseFloat( value );
            double ratio = customSize[1] / origImage.getHeight();
            customSize[0] = Math.round( ratio * origImage.getWidth() );
        }

        return new int[]{(int) customSize[0], (int) customSize[1]};
    }
}

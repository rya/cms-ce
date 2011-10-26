/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import com.enonic.cms.core.log.StoreNewLogEntryCommand;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.io.FileUtil;
import com.enonic.esl.net.URL;
import com.enonic.esl.net.URLUtil;
import com.enonic.esl.util.ParamsInTextParser;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalException;
import com.enonic.vertical.VerticalRuntimeException;
import com.enonic.vertical.engine.VerticalEngineException;
import com.enonic.vertical.engine.VerticalSecurityException;

import com.enonic.cms.framework.util.TIntArrayList;

import com.enonic.cms.core.content.UnitEntity;
import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.log.LogType;
import com.enonic.cms.core.resource.ResourceFile;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateSpecification;
import com.enonic.cms.core.structure.page.template.PageTemplateType;
import com.enonic.cms.core.xslt.XsltProcessorException;
import com.enonic.cms.core.xslt.XsltProcessorHelper;


public abstract class AdminHandlerBaseServlet
    extends AbstractAdminwebServlet
{
    protected static final int[] EXCLUDED_TYPE_KEYS_IN_PREVIEW = new int[]{1, 2, 3, 4, 6};

    private Vector<ErrorCode> errorCodes = new Vector<ErrorCode>();

    private DiskFileUpload fileUpload;

    // SMTP server to use when sending mail:

    protected String SMTP_HOST;


    public void init( ServletConfig servletConfig )
        throws ServletException
    {
        super.init( servletConfig );

        fileUpload = new DiskFileUpload();
        fileUpload.setHeaderEncoding( "UTF-8" );
        fileUpload.setSizeMax( verticalProperties.getMultiPartRequestMaxSize() );
        fileUpload.setSizeThreshold( 64000 );

        // Parameters for the mail sent to users when generating a new password:
        SMTP_HOST = verticalProperties.getMailSmtpHost();
        if ( SMTP_HOST == null )
        {
            SMTP_HOST = "mail.enonic.com";
        }
    }

    public void addError( int errorCode, String fieldName, String fieldValue )
    {
        // Check whether the error is already reported
        for ( ErrorCode ec : errorCodes )
        {
            if ( ec.code == errorCode && ec.name.equals( fieldName ) )
            {
                return;
            }
        }

        ErrorCode errCode = new ErrorCode( errorCode, fieldName, fieldValue );
        errorCodes.add( errCode );
    }

    public void addFeedback( Document doc, int feedbackCode )
    {
        Element feedbackElem = XMLTool.createElementIfNotPresent( doc, doc.getDocumentElement(), "feedback" );
        feedbackElem.setAttribute( "code", String.valueOf( feedbackCode ) );
    }

    public void addFeedback( Document doc, ExtendedMap formItems )
    {
        if ( formItems.containsKey( "feedback" ) )
        {
            addFeedback( doc, formItems.getInt( "feedback" ) );
        }
    }

    public URL getReferer( HttpServletRequest request )
    {
        String refererStr = request.getHeader( "referer" );
        if ( refererStr == null )
        {
            return null;
        }

        URL referer = new URL( refererStr );

        // Remove feedback parameter
        referer.removeParameter( "feedback" );

        return referer;
    }

    public void addErrorsXML( Document doc )
    {
        if ( errorCodes.size() > 0 )
        {
            Element errorsElem = XMLTool.createElement( doc, doc.getDocumentElement(), "errors" );

            for ( int i = 0; i < errorCodes.size(); i++ )
            {
                ErrorCode ec = errorCodes.get( i );
                Element errorElem = XMLTool.createElement( doc, errorsElem, "error" );
                errorElem.setAttribute( "code", Integer.toString( ec.code ) );
                errorElem.setAttribute( "name", ec.name );

                Element valueElem = XMLTool.createElement( doc, errorElem, "value" );
                XMLTool.createCDATASection( doc, valueElem, ec.value );
            }
        }
        errorCodes.removeAllElements();
    }

    public boolean hasErrors()
    {
        return !errorCodes.isEmpty();
    }

    public void clearErrors()
    {
        errorCodes.removeAllElements();
    }

    protected void closeWindow( HttpServletResponse response )
        throws VerticalAdminException
    {
        try
        {
            PrintWriter out = response.getWriter();

            out.print( "<html><head><script language=\"javascript\">window.close();</script></head></html>" );
        }
        catch ( IOException ioe )
        {
            String message = "Failed to print html response: %t";
            VerticalAdminLogger.errorAdmin( message, ioe );
        }
    }

    public static BinaryData createBinaryData( final FileItem fileItem )
        throws VerticalAdminException
    {
        return createBinaryData( fileItem, null );
    }

    public static BinaryData createBinaryData( final FileItem fileItem, final String label )
        throws VerticalAdminException
    {
        BinaryData binaryData = new BinaryData();
        InputStream fis = null;
        try
        {
            binaryData.fileName = FileUtil.getFileName( fileItem );

            fis = fileItem.getInputStream();
            ByteArrayOutputStream bao = new ByteArrayOutputStream();

            byte[] buf = new byte[1024 * 10];
            int size;
            while ( ( size = fis.read( buf ) ) > 0 )
            {
                bao.write( buf, 0, size );
            }
            binaryData.data = bao.toByteArray();
            binaryData.label = label;
        }
        catch ( IOException e )
        {
            VerticalAdminLogger.errorAdmin( "I/O error: %t", e );
        }
        finally
        {
            try
            {
                if ( fis != null )
                {
                    fis.close();
                }
            }
            catch ( IOException ioe )
            {
                String message = "Failed to close file input stream: %t";
                VerticalAdminLogger.warn( message, ioe );
            }
        }
        return binaryData;
    }

    /**
     * Process incoming HTTP GET requests
     *
     * @param request  Object that encapsulates the request to the servlet
     * @param response Object that encapsulates the response from the servlet
     */
    public void doGet( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {

        performTask( request, response );
    }

    /**
     * Process incoming HTTP POST requests
     *
     * @param request  Object that encapsulates the request to the servlet
     * @param response Object that encapsulates the response from the servlet
     */
    public void doPost( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {

        performTask( request, response );

    }

    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, TransformerException, IOException
    {

        handlerBrowse( request, response, session, admin, formItems );
    }

    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {

        String message = "OperationWrapper BROWSE is not implemented (page={0})";
        VerticalAdminLogger.errorAdmin( message, formItems.get( "page" ), null );
    }

    public void handlerCopy( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems, User user, int key )
        throws VerticalAdminException, VerticalEngineException
    {

        String message = "OperationWrapper COPY is not implemented (page={0},key={1})";

        Object[] msgData = new Object[]{formItems.getString( "page" ), key};
        VerticalAdminLogger.errorAdmin( message, msgData, null );
    }

    public void handlerCreate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        String message = "OperationWrapper CREATE is not implemented (page={0})";
        VerticalAdminLogger.errorAdmin( message, formItems.get( "page" ), null );
    }

    public void handlerReport( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String subop )
        throws VerticalAdminException, VerticalEngineException
    {

        String message = "OperationWrapper REPORT is not implemented (page={0})";
        VerticalAdminLogger.errorAdmin( message, formItems.get( "page" ), null );
    }

    public void handlerPreview( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        String message = "OperationWrapper PREVIEW is not implemented (page={0})";
        VerticalAdminLogger.errorAdmin( message, formItems.get( "page" ), null );
    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, VerticalEngineException
    {

        handlerCustom( request, response, session, admin, formItems, operation );
    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation )
        throws VerticalAdminException, VerticalEngineException
    {

        String message = "Custom operation is not implemented (page={0}): {1}";

        Object[] msgData = new Object[]{formItems.get( "page" ), operation};
        VerticalAdminLogger.errorAdmin( message, msgData, null );
    }

    public void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        String message = "OperationWrapper FORM is not implemented (page={0})";
        VerticalAdminLogger.errorAdmin( message, formItems.get( "page" ), null );

    }

    public void addCommonParameters( AdminService admin, User user, HttpServletRequest request, Map<String, Object> parameters, int unitKey,
                                     int menuKey )
    {

        if ( user != null )
        {
            UserEntity userEntity = securityService.getUser( user );
            parameters.put( "currentuser_key", userEntity.getKey().toString() );
            parameters.put( "current_uid", userEntity.getName() );
            parameters.put( "currentuser_uid", userEntity.getName() );
            parameters.put( "currentuser_fullname", userEntity.getDisplayName() );
            parameters.put( "currentuser_qualifiedname", userEntity.getQualifiedName() );
            parameters.put( "currentuser_email", userEntity.getEmail() != null ? userEntity.getEmail() : "" );
            parameters.put( "currentuser_has_photo", userEntity.hasPhoto() );
        }

        if ( unitKey != -1 )
        {
            UnitEntity unit = unitDao.findByKey( unitKey );
            parameters.put( "unitname", unit.getName() );
            parameters.put( "selectedunitkey", String.valueOf( unitKey ) );
        }

        if ( menuKey != -1 )
        {
            SiteEntity site = siteDao.findByKey( menuKey );
            if ( site != null )
            {
                parameters.put( "menuname", site.getName() );
                parameters.put( "menukey", String.valueOf( menuKey ) );
            }

        }
        if ( !parameters.containsKey( "referer" ) && request.getHeader( "referer" ) != null )
        {
            parameters.put( "referer", request.getHeader( "referer" ) );
        }
    }

    public void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, int key )
        throws VerticalAdminException, VerticalEngineException
    {

        String message = "OperationWrapper REMOVE is not implemented (page={0},key={1})";

        Object[] msgData = new Object[]{formItems.get( "page" ), key};
        VerticalAdminLogger.errorAdmin( message, msgData, null );
    }

    public void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String key )
        throws VerticalAdminException, VerticalEngineException
    {

        String message = "OperationWrapper REMOVE is not implemented (page={0},key={1})";

        Object[] msgData = new Object[]{formItems.get( "page" ), key};
        VerticalAdminLogger.errorAdmin( message, msgData, null );
    }

    public void handlerSearch( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {

        String message = "OperationWrapper SEARCH is not implemented (page={0})";
        VerticalAdminLogger.errorAdmin( message, formItems.get( "page" ), null );
    }

    public void handlerSearchResults( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                      ExtendedMap formItems )
        throws VerticalAdminException
    {

        String message = "OperationWrapper SEARCH RESULTS is not implemented (page={0})";
        VerticalAdminLogger.errorAdmin( message, formItems.get( "page" ), null );
    }

    public void handlerMenu( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, TransformerException, IOException
    {
        String message = "OperationWrapper MENU is not implemented (page={0})";
        VerticalAdminLogger.errorAdmin( message, formItems.get( "page" ), null );
    }

    public void handlerWizard( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, ExtendedMap parameters, User user, String wizardName )
        throws VerticalAdminException, VerticalEngineException, TransformerException, IOException
    {
        String message = "OperationWrapper WIZARD is not implemented (page={0},wizardName={1})";
        Object[] msgData = {formItems.get( "page" ), wizardName};
        VerticalAdminLogger.errorAdmin( message, msgData, null );
    }

    public void handlerNotify( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, User user )
        throws VerticalAdminException
    {

        String message = "OperationWrapper NOTIFY is not implemented (page={0})";

        VerticalAdminLogger.errorAdmin( message, formItems.get( "page" ), null );
    }

    public boolean handlerSelect( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                  ExtendedMap formItems )
        throws VerticalAdminException
    {

        String message = "OperationWrapper SELECT is not implemented (page={0})";
        VerticalAdminLogger.errorAdmin( message, formItems.get( "page" ), null );
        return false;
    }

    public void handlerShow( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems )
        throws VerticalAdminException
    {

        String message = "OperationWrapper SHOW is not implemented (page={0})";
        VerticalAdminLogger.errorAdmin( message, formItems.get( "page" ), null );
    }

    public void handlerUpdate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        String message = "OperationWrapper UPDATE is not implemented (page={0})";
        VerticalAdminLogger.errorAdmin( message, formItems.get( "page" ), null );
    }

    public static boolean isArrayFormItem( Map formItems, String string )
    {
        if ( !formItems.containsKey( string ) )
        {
            return false;
        }

        if ( formItems.get( string ) == null )
        {
            return false;
        }

        return formItems.get( string ).getClass() == String[].class;
    }

    public static String[] getArrayFormItem( Map formItems, String string )
    {
        if ( !formItems.containsKey( string ) )
        {
            return new String[0];
        }

        Object item = formItems.get( string );

        if ( item == null )
        {
            return new String[0];
        }

        if ( item.getClass() == String[].class )
        {
            return (String[]) item;
        }
        else
        {
            return new String[]{(String) item};
        }
    }

    private ExtendedMap parseSimpleRequest( HttpServletRequest request, boolean keepEmpty )
    {
        ExtendedMap formItems = new ExtendedMap( keepEmpty );

        Enumeration paramNames = request.getParameterNames();
        while ( paramNames.hasMoreElements() )
        {
            String key = paramNames.nextElement().toString();
            String[] paramValues = request.getParameterValues( key );

            if ( paramValues != null )
            {
                if ( paramValues.length == 1 && paramValues[0] != null )
                {
                    String value = paramValues[0];
                    if ( value.length() > 0 )
                    {
                        if ( "true".equals( value ) )
                        {
                            formItems.putBoolean( key, true );
                        }
                        else if ( "false".equals( value ) )
                        {
                            formItems.putBoolean( key, false );
                        }
                        else
                        {
                            formItems.putString( key, value );
                        }
                    }
                    else if ( keepEmpty )
                    {
                        formItems.putString( key, value );
                    }
                }
                else if ( paramValues.length > 1 )
                {
                    formItems.put( key, paramValues );
                }
            }
        }

        return formItems;
    }

    protected ExtendedMap parseForm( HttpServletRequest request, boolean keepEmpty )
        throws FileUploadException, IOException
    {
        if ( FileUpload.isMultipartContent( request ) )
        {
            return parseMultiPartRequest( request );
        }
        else
        {
            return parseSimpleRequest( request, keepEmpty );
        }
    }

    private ExtendedMap parseMultiPartRequest( HttpServletRequest request )
        throws FileUploadException, IOException
    {
        ExtendedMap formItems = new ExtendedMap();
        List paramList = fileUpload.parseRequest( request );
        for ( Object parameter : paramList )
        {
            FileItem fileItem = (FileItem) parameter;

            String name = fileItem.getFieldName();

            if ( fileItem.isFormField() )
            {
                String value = fileItem.getString( "UTF-8" );
                if ( formItems.containsKey( name ) )
                {
                    ArrayList<Object> values = new ArrayList<Object>();
                    Object obj = formItems.get( name );
                    if ( obj instanceof Object[] )
                    {
                        String[] objArray = (String[]) obj;
                        for ( int i = 0; i < objArray.length; i++ )
                        {
                            values.add( objArray[i] );
                        }
                    }
                    else
                    {
                        values.add( obj );
                    }
                    values.add( value );
                    formItems.put( name, values.toArray( new String[values.size()] ) );
                }
                else
                {
                    formItems.put( name, value );
                }
            }
            else
            {
                if ( fileItem.getSize() > 0 )
                {
                    if ( formItems.containsKey( name ) )
                    {
                        ArrayList<Object> values = new ArrayList<Object>();
                        Object obj = formItems.get( name );
                        if ( obj instanceof FileItem[] )
                        {
                            FileItem[] objArray = (FileItem[]) obj;
                            for ( int i = 0; i < objArray.length; i++ )
                            {
                                values.add( objArray[i] );
                            }
                        }
                        else
                        {
                            values.add( obj );
                        }
                        values.add( fileItem );
                        formItems.put( name, values.toArray( new FileItem[values.size()] ) );
                    }
                    else
                    {
                        formItems.put( name, fileItem );
                    }
                }
            }
        }

        // Add parameters from url
        Map paramMap = URLUtil.decodeParameterMap( request.getParameterMap() );
        for ( Object parameterEntry : paramMap.entrySet() )
        {
            Map.Entry entry = (Map.Entry) parameterEntry;
            String key = (String) entry.getKey();
            String[] values = (String[]) entry.getValue();
            for ( String value : values )
            {
                formItems.put( key, value );
            }
        }

        // Remove all empty parameters that are NOT in an array
        ArrayList<String> remove = new ArrayList<String>();
        for ( Object parameterKey : formItems.keySet() )
        {
            String key = (String) parameterKey;
            Object value = formItems.get( key );
            if ( !( value instanceof String[] ) && value instanceof String && ( (String) value ).length() == 0 )
            {
                remove.add( key );
            }
        }
        for ( String key : remove )
        {
            formItems.remove( key );
        }

        return formItems;
    }

    /**
     * Process incoming requests for information
     *
     * @param request  Object that encapsulates the request to the servlet
     * @param response Object that encapsulates the response from the servlet
     */
    protected void performTask( HttpServletRequest request, HttpServletResponse response )
    {
        HttpSession session;
        session = request.getSession( false );
        response.setContentType( "text/html;charset=UTF-8" );

        // Make IE 9 behave like IE 8
        // http://msdn.microsoft.com/en-us/library/cc288325%28v=vs.85%29.aspx#Servers
        response.setHeader( "X-UA-Compatible", "IE=EmulateIE8" );

        if ( session == null )
        {
            VerticalAdminLogger.debug( "Session is null. Redirecting to login.", null );

            // failed to get session, redirect to login page
            try
            {
                redirectClientToAdminPath( "login", (MultiValueMap) null, request, response );
            }
            catch ( VerticalAdminException vae )
            {
                String message = "Failed to redirect to login page: %t";
                VerticalAdminLogger.errorAdmin( message, vae );
            }
        }
        else
        {
            // lookup admin bean
            AdminService admin = lookupAdminBean();
            User user = securityService.getLoggedInAdminConsoleUser();
            if ( user == null )
            {
                // no logged in user, invalidate session and redirect to login page
                String message = "No user logged in. Redirecting to login.";
                VerticalAdminLogger.debug( message, null );
                try
                {
                    redirectClientToAdminPath( "login", (MultiValueMap) null, request, response );
                }
                catch ( VerticalAdminException vae )
                {
                    message = "Failed to redirect to login page: %t";
                    VerticalAdminLogger.errorAdmin( message, vae );
                }
            }
            else
            {
                response.setContentType( "text/html; charset=UTF-8" );
                try
                {
                    ExtendedMap formItems = parseForm( request, false );

                    String operation;
                    if ( formItems.containsKey( "op" ) )
                    {
                        operation = formItems.getString( "op" );
                    }
                    else
                    {
                        operation = request.getParameter( "op" );
                    }

                    // Common parameters and variables
                    ExtendedMap parameters = new ExtendedMap();
                    int unitKey = formItems.getInt( "selectedunitkey", -1 );
                    int menuKey = formItems.getInt( "selectedmenukey", -1 );
                    int page = formItems.getInt( "page", -1 );
                    if ( page == 993 )
                    {
                        int contentKey = -1;

                        String contentKeyStr = request.getParameter( "key" );
                        if ( contentKeyStr != null )
                        {
                            contentKey = Integer.parseInt( contentKeyStr );
                        }

                        if ( contentKey == -1 )
                        {
                            String versionKeyStr = request.getParameter( "versionkey" );
                            if ( versionKeyStr != null )
                            {
                                int versionKey = Integer.parseInt( versionKeyStr );
                                contentKey = admin.getContentKeyByVersionKey( versionKey );
                            }
                        }

                        if ( contentKey != -1 )
                        {
                            int contentTypeKey = admin.getContentTypeKey( contentKey );
                            page = contentTypeKey + 999;
                            formItems.put( "page", page );
                        }
                    }

                    if ( page == 991 )
                    {
                        int categoryKey = formItems.getInt( "categorykey", -1 );
                        if ( categoryKey == -1 )
                        {
                            categoryKey = formItems.getInt( "cat", -1 );
                        }

                        if ( categoryKey != -1 )
                        {
                            int contentTypeKey = admin.getContentTypeKeyByCategory( categoryKey );
                            if ( contentTypeKey != -1 )
                            {
                                page = contentTypeKey + 999;
                            }
                        }
                    }
                    parameters.put( "page", Integer.toString( page ) );
                    addCommonParameters( admin, user, request, parameters, unitKey, menuKey );
                    Document verticalDoc = XMLTool.createDocument( "data" );

                    if ( "create".equals( operation ) )
                    {
                        handlerCreate( request, response, session, admin, formItems );
                    }
                    else if ( "update".equals( operation ) )
                    {
                        handlerUpdate( request, response, session, admin, formItems );
                    }
                    else if ( "remove".equals( operation ) )
                    {
                        String keyStr = request.getParameter( "key" );
                        if ( StringUtil.isIntegerString( keyStr ) )
                        {
                            int key = -1;
                            try
                            {
                                key = Integer.parseInt( keyStr );
                            }
                            catch ( NumberFormatException nfe )
                            {
                                String message = "Failed to parse key ({0}): %t";
                                VerticalAdminLogger.errorAdmin( message, keyStr, nfe );
                            }
                            handlerRemove( request, response, session, admin, formItems, key );
                        }
                        else
                        {
                            handlerRemove( request, response, session, admin, formItems, keyStr );
                        }
                    }
                    else if ( "copy".equals( operation ) )
                    {
                        String keyStr = request.getParameter( "key" );
                        int key = -1;
                        try
                        {
                            key = Integer.parseInt( keyStr );
                        }
                        catch ( NumberFormatException nfe )
                        {
                            String message = "Failed to parse key ({0}): %t";
                            VerticalAdminLogger.errorAdmin( message, keyStr, nfe );
                        }

                        handlerCopy( request, response, session, admin, formItems, user, key );
                    }
                    else if ( "import".equals( operation ) )
                    {
                        throw new IllegalArgumentException( "Unsupported operation: import" );
                    }
                    else if ( "browse".equals( operation ) )
                    {
                        handlerBrowse( request, response, session, admin, formItems, parameters, user, verticalDoc );
                    }
                    else if ( "select".equals( operation ) )
                    {
                        handlerSelect( request, response, session, admin, formItems );
                    }
                    else if ( "show".equals( operation ) )
                    {
                        handlerShow( request, response, session, admin, formItems );
                    }
                    else if ( "form".equals( operation ) )
                    {
                        this.clearErrors();
                        handlerForm( request, response, session, admin, formItems );
                    }
                    else if ( "searchform".equals( operation ) )
                    {
                        handlerSearch( request, response, session, admin, formItems );
                    }
                    else if ( "searchresults".equals( operation ) )
                    {
                        handlerSearchResults( request, response, session, admin, formItems );
                    }
                    else if ( "report".equals( operation ) )
                    {
                        String subOp = formItems.getString( "subop" );
                        handlerReport( request, response, session, admin, formItems, subOp );
                    }
                    else if ( "closewindow".equals( operation ) )
                    {
                        closeWindow( response );
                    }
                    else if ( "preview".equals( operation ) )
                    {
                        handlerPreview( request, response, session, admin, formItems );
                    }
                    else if ( "menu".equals( operation ) )
                    {
                        handlerMenu( request, response, session, admin, formItems, parameters, user, verticalDoc );
                    }
                    else if ( "notify".equals( operation ) )
                    {
                        handlerNotify( request, response, session, admin, formItems, user );
                    }
                    else if ( "wizard".equals( operation ) )
                    {
                        String wizardName = formItems.getString( "name" );
                        handlerWizard( request, response, session, admin, formItems, parameters, user, wizardName );
                    }
                    else if ( operation != null )
                    {
                        handlerCustom( request, response, session, admin, formItems, operation, parameters, user, verticalDoc );
                    }
                    else
                    {
                        handlerCustom( request, response, session, admin, formItems, "missing" );
                    }
                }
                catch ( Exception e )
                {
                    try
                    {
                        if ( !( e instanceof VerticalException ) && !( e instanceof VerticalRuntimeException ) )
                        {
                            String message = "Unexpected error occurred during handling of admin page: %t";
                            VerticalAdminLogger.error( message, e );
                        }
                        ErrorPageServlet.Error error = new ErrorPageServlet.ThrowableError( e );
                        session.setAttribute( "com.enonic.vertical.error", error );
                        redirectClientToAdminPath( "errorpage", (MultiValueMap) null, request, response );
                    }
                    catch ( VerticalAdminException vae )
                    {
                        String message = "Failed to redirect to error page: %t";
                        VerticalAdminLogger.errorAdmin( message, vae );
                    }
                }
            }
        }
    }

    protected void transformXML( HttpServletRequest request, HttpServletResponse response, Document doc, Source xslSource,
                                 Map<String, Object> parameters )
        throws VerticalAdminException
    {
        final HttpSession session = request.getSession();
        final String languageCode = (String) session.getAttribute( "languageCode" );

        final URIResolver uriResolver = AdminStore.getURIResolver( languageCode );

        new XsltProcessorHelper().stylesheet( xslSource, uriResolver ).input( doc ).params( parameters ).process( response );
    }

    protected void transformXML( HttpServletRequest request, HttpServletResponse response, org.jdom.Document doc, Source xslSource,
                                 Map<String, Object> parameters )
        throws VerticalAdminException
    {
        final HttpSession session = request.getSession();
        final String languageCode = (String) session.getAttribute( "languageCode" );

        final URIResolver uriResolver = AdminStore.getURIResolver( languageCode );

        new XsltProcessorHelper().stylesheet( xslSource, uriResolver ).input( doc ).params( parameters ).process( response );
    }

    protected void transformXML( HttpServletRequest request, HttpServletResponse response, Document doc, String xslPath, Map parameters )
        throws VerticalAdminException
    {
        try
        {
            final HttpSession session = request.getSession();
            final String languageCode = (String) session.getAttribute( "languageCode" );

            final Source xslDoc = AdminStore.getStylesheet( languageCode, xslPath, false );
            final URIResolver uriResolver = AdminStore.getURIResolver( languageCode );

            new XsltProcessorHelper().stylesheet( xslDoc, uriResolver ).input( doc ).params( parameters ).process( response );
        }
        catch ( XsltProcessorException xpe )
        {
            String msg = "Failed to transform xml: %t";
            VerticalAdminLogger.errorAdmin( msg, xpe );
        }
    }

    protected void transformXML( HttpServletRequest request, HttpServletResponse response, org.jdom.Document doc, String xslPath,
                                 Map<String, Object> parameters )
        throws VerticalAdminException
    {
        final HttpSession session = request.getSession();
        final String languageCode = (String) session.getAttribute( "languageCode" );
        final Source xslSource = AdminStore.getStylesheet( languageCode, xslPath, false );

        transformXML( request, response, doc, xslSource, parameters );
    }

    protected void transformXML( HttpSession session, Writer writer, Source xmlSource, Source xslSource, Map<String, Object> parameters )
        throws TransformerException
    {
        final String languageCode = (String) session.getAttribute( "languageCode" );
        final URIResolver uriResolver = AdminStore.getURIResolver( languageCode );

        new XsltProcessorHelper().stylesheet( xslSource, uriResolver ).input( xmlSource ).params( parameters, false ).process( writer );
    }

    protected URIResolver getStylesheetURIResolver( final AdminService adminBean )
    {
        return new URIResolver()
        {

            public Source resolve( String href, String base )
                throws TransformerException
            {
                Source source = null;
                ResourceKey key = new ResourceKey( href );
                ResourceFile res = resourceService.getResourceFile( key );
                if ( res != null )
                {
                    String xsl = res.getDataAsString();
                    source = new StreamSource( new StringReader( xsl ) );
                }
                return source;
            }
        };
    }

    protected String buildAccessRightsXML( ExtendedMap formItems )
    {
        return buildAccessRightsXML( null, formItems, Integer.MIN_VALUE );
    }

    protected String buildAccessRightsXML( String key, ExtendedMap formItems, int accessrightsType )
    {

        Document doc = buildAccessRightsXML( null, key, formItems, accessrightsType );
        if ( doc != null )
        {
            return XMLTool.documentToString( doc );
        }

        return null;
    }

    public static final Document buildAccessRightsXML( Element rootElem, String key, ExtendedMap formItems, int accessrightsType )
    {

        // Handle this in calling methods instead
        //if (!formItems.containsKey("updateaccessrights"))
        //    return null;

        Document doc;
        Element elmAccessRights;
        if ( rootElem != null )
        {
            doc = rootElem.getOwnerDocument();
            elmAccessRights = XMLTool.createElement( doc, rootElem, "accessrights" );
        }
        else
        {
            doc = XMLTool.createDocument( "accessrights" );
            elmAccessRights = doc.getDocumentElement();
        }

        if ( key != null )
        {
            elmAccessRights.setAttribute( "key", key );
        }
        if ( accessrightsType != Integer.MIN_VALUE )
        {
            elmAccessRights.setAttribute( "type", String.valueOf( accessrightsType ) );
        }

        for ( Object parameterKey : formItems.keySet() )
        {
            String paramName = (String) parameterKey;
            if ( paramName.startsWith( "accessright[key=" ) )
            {
                String paramValue = formItems.getString( paramName );
                ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                ExtendedMap paramsInValue = ParamsInTextParser.parseParamsInText( paramValue, "[", "]", ";" );

                buildAccessRightElement( doc, elmAccessRights, paramsInName.getString( "key" ), paramsInValue );
            }
        }

        return doc;
    }

    protected static final Element buildAccessRightElement( Document doc, Element root, String key, ExtendedMap paramsInValue )
    {

        Element element = XMLTool.createElement( doc, root, "accessright" );

        if ( key != null )
        {
            element.setAttribute( "groupkey", key );
        }
        element.setAttribute( "grouptype", paramsInValue.getString( "grouptype", "" ) );
        element.setAttribute( "adminread", paramsInValue.getString( "adminread", "false" ) );
        element.setAttribute( "read", paramsInValue.getString( "read", "false" ) );
        element.setAttribute( "update", paramsInValue.getString( "update", "false" ) );
        element.setAttribute( "delete", paramsInValue.getString( "delete", "false" ) );
        element.setAttribute( "create", paramsInValue.getString( "create", "false" ) );
        element.setAttribute( "publish", paramsInValue.getString( "publish", "false" ) );
        element.setAttribute( "administrate", paramsInValue.getString( "administrate", "false" ) );
        element.setAttribute( "approve", paramsInValue.getString( "approve", "false" ) );
        element.setAttribute( "add", paramsInValue.getString( "add", "false" ) );

        String displayName = paramsInValue.getString( "name", null );
        if ( displayName != null )
        {
            element.setAttribute( "displayname", displayName );
        }

        return element;
    }

    public void addAccessLevelParameters( User user, Map<String, Object> parameters )
    {
        UserEntity userEntity = userDao.findByKey( user.getKey() );
        UserKey userKey = user.getKey();
        UserStoreKey userStoreKey = userEntity.getUserStoreKey();

        boolean hasEnterpriseAdminPowers = memberOfResolver.hasEnterpriseAdminPowers( userKey );
        boolean hasAdministratorPowers = memberOfResolver.hasAdministratorPowers( userKey );
        boolean hasDeveloperPowers = memberOfResolver.hasDeveloperPowers( userKey );
        boolean hasExpertContributorPowers = memberOfResolver.hasExpertContributorPowers( userKey );
        boolean hasContributorPowers = memberOfResolver.hasContributorPowers( userKey );
        boolean hasUserStoreAdministratorPowers = memberOfResolver.hasUserStoreAdministratorPowers( userKey, userStoreKey );

        parameters.put( "userKey", String.valueOf( user.getKey() ) );
        parameters.put( "enterpriseadmin", ( hasEnterpriseAdminPowers ? "true" : "false" ) );
        parameters.put( "siteadmin", ( hasAdministratorPowers ? "true" : "false" ) );
        parameters.put( "sitecontributor", ( hasContributorPowers ? "true" : "false" ) );
        parameters.put( "expertcontributor", ( hasExpertContributorPowers ? "true" : "false" ) );
        parameters.put( "developer", ( hasDeveloperPowers ? "true" : "false" ) );
        parameters.put( "userstoreadmin", ( hasUserStoreAdministratorPowers ? "true" : "false" ) );
    }

    public int getDomainKey( HttpSession session )
    {
        int domainKey = -1;
        String strDomainKey = (String) session.getAttribute( "selecteddomainkey" );
        try
        {
            domainKey = Integer.parseInt( strDomainKey );
        }
        catch ( NumberFormatException e )
        {
        }
        return domainKey;
    }

    protected void addSortParamteres( String defaultSortBy, String defaultSortByDirection, ExtendedMap inParams, HttpSession session,
                                      HashMap<String, Object> outParams )
    {

        // Get choosen sort by user
        String sortBy = inParams.getString( "sortby", null );
        String sortByDirection = inParams.getString( "sortby-direction", null );

        String page = inParams.getString( "page" );
        String op = inParams.getString( "op" );

        // s[page="+page+",op=browse,s]
        StringBuffer sb_sortByKey = new StringBuffer( "s[page=" );
        sb_sortByKey.append( page );
        sb_sortByKey.append( ",op=" );
        sb_sortByKey.append( op );
        sb_sortByKey.append( ",s]" );
        String sortByKey = sb_sortByKey.toString();
        // s[page="+page+",op=browse,sd]
        StringBuffer sb_sortByDirectionKey = new StringBuffer( "s[page=" );
        sb_sortByDirectionKey.append( page );
        sb_sortByDirectionKey.append( ",op=" );
        sb_sortByDirectionKey.append( op );
        sb_sortByDirectionKey.append( ",sd]" );
        String sortByDirectionKey = sb_sortByDirectionKey.toString();

        //("sortByKey = " + sortByKey);
        //("sortByDirectionKey = " + sortByDirectionKey);

        // There is no choosen sort by user, get last sort from session
        if ( sortBy == null || sortByDirection == null )
        {
            sortBy = (String) session.getAttribute( sortByKey );
            sortByDirection = (String) session.getAttribute( sortByDirectionKey );

            // If there is no last sort from session, use default
            if ( sortBy == null )
            {
                sortBy = defaultSortBy;
            }
            if ( sortByDirection == null )
            {
                sortByDirection = defaultSortByDirection;
            }
        }
        // User have specified a sort-direction, store it until next time
        else
        {
            // Hack in order to sort timestamp columns in descending direction by default
            if ( "timestamp".equals( sortBy ) )
            {
                if ( !"timestamp".equals( session.getAttribute( sortByKey ) ) )
                {
                    sortByDirection = "descending";
                }
            }
            else if ( "/content/@timestamp".equals( sortBy ) )
            {
                if ( !"/content/@timestamp".equals( session.getAttribute( sortByKey ) ) )
                {
                    sortByDirection = "DESC";
                }
            }

            session.setAttribute( sortByKey, sortBy );
            session.setAttribute( sortByDirectionKey, sortByDirection );
        }

        outParams.put( "sortby", sortBy );
        outParams.put( "sortby-direction", sortByDirection );
    }

    protected Document buildChangedAccessRightsXML( ExtendedMap formItems )
    {

        Document doc = XMLTool.createDocument( "changedaccessrights" );
        Element changed = doc.getDocumentElement();

        // Lager xml for diffen - de som er fjernet
        for ( Object parameterKey : formItems.keySet() )
        {

            String paramName = (String) parameterKey;
            if ( paramName.startsWith( "original_accessright[key=" ) )
            {

                ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                String key = paramsInName.getString( "key" );

                if ( !formItems.containsKey( "accessright[key=" + key + "]" ) )
                {
                    String paramValue = formItems.getString( paramName );
                    ExtendedMap paramsInValue = ParamsInTextParser.parseParamsInText( paramValue, "[", "]", ";" );
                    Element elmAccessRight = buildAccessRightElement( doc, changed, key, paramsInValue );
                    elmAccessRight.setAttribute( "diffinfo", "removed" );
                }
            }
        }
        // Lager xml for diffen - de som er lagt til
        for ( Object parameterKey : formItems.keySet() )
        {
            String paramName = (String) parameterKey;
            if ( paramName.startsWith( "accessright[key=" ) )
            {

                ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                String key = paramsInName.getString( "key" );

                if ( !formItems.containsKey( "original_accessright[key=" + key + "]" ) )
                {

                    String paramValue = formItems.getString( paramName );
                    ExtendedMap paramsInValue = ParamsInTextParser.parseParamsInText( paramValue, "[", "]", ";" );

                    Element elmAccessRight = buildAccessRightElement( doc, changed, key, paramsInValue );
                    elmAccessRight.setAttribute( "diffinfo", "added" );
                }
            }
        }
        // Lager xml for diffen - de som er endret
        for ( Object paramKey : formItems.keySet() )
        {

            String paramName = (String) paramKey;

            if ( paramName.startsWith( "accessright[key=" ) )
            {

                ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                String key = paramsInName.getString( "key" );

                if ( formItems.containsKey( "original_accessright[key=" + key + "]" ) )
                {

                    String originalValue = formItems.getString( "original_accessright[key=" + key + "]" );
                    String currentValue = formItems.getString( paramName );
                    if ( !currentValue.equals( originalValue ) )
                    {

                        ExtendedMap paramsInValue = ParamsInTextParser.parseParamsInText( currentValue, "[", "]", ";" );
                        Element elmAccessRight = buildAccessRightElement( doc, changed, key, paramsInValue );
                        elmAccessRight.setAttribute( "diffinfo", "modified" );
                    }
                }
            }
        }

        return doc;
    }

    protected Document applyChangesInAccessRights( Document docExistingAccessRights, Map<String, ExtendedMap> removedAccessRights,
                                                   Map<String, ExtendedMap> modifiedAccessRights,
                                                   Map<String, ExtendedMap> addedAccessRights )
    {

        // We have to make a clone of this hashtable, because we may have to remove some elements
        // and we don't want to affect this on the original hashtable.
        addedAccessRights = new HashMap<String, ExtendedMap>( addedAccessRights );

        //("removedAccessRights = " + removedAccessRights);
        //("modifiedAccessRights = " + modifiedAccessRights);
        //("addedAccessRights = " + addedAccessRights);

        Element elExistingAccessRights = docExistingAccessRights.getDocumentElement();

        //("antall accessrights: " + elExistingAccessRights.getChildNodes().getLength());
        // Loop thru existing accessrights and check if there is anyone to remove or modify
        Element curAccessRight = (Element) elExistingAccessRights.getFirstChild();
        while ( curAccessRight != null )
        {

            String groupKey = curAccessRight.getAttribute( "groupkey" );
            //("checking accessright, groupkey = " + groupKey);

            boolean remove = removedAccessRights.containsKey( groupKey );
            boolean modify = modifiedAccessRights.containsKey( groupKey );
            boolean add = addedAccessRights.containsKey( groupKey );
            boolean overwrite = ( modify || add );

            // Remove accessright
            if ( remove )
            {

                //("removing accessright, groupkey = " + groupKey);
                curAccessRight = XMLTool.removeChildFromParent( elExistingAccessRights, curAccessRight );
            }
            // Overwrite accessright
            else if ( overwrite )
            {

                ExtendedMap params;
                if ( modify )
                {
                    params = modifiedAccessRights.get( groupKey );
                    //("modifying/overwriting accessright, groupkey = " + groupKey);
                }
                else // add == true:
                {
                    params = addedAccessRights.get( groupKey );
                    //("adding/overwriting accessright, groupkey = " + groupKey);
                }

                Document docNewAccessRight = XMLTool.createDocument( "foo" );
                Element elNewAccessRight =
                    buildAccessRightElement( docNewAccessRight, docNewAccessRight.getDocumentElement(), groupKey, params );

                Element imported = (Element) docExistingAccessRights.importNode( elNewAccessRight, true );
                elExistingAccessRights.replaceChild( imported, curAccessRight );
                curAccessRight = imported;

                // Hvis vi overskriver eksisterende rettighet i stedet for å legge til, fordi den finnes fra før
                // må vi fjerne rettigheten fra addedAccessRights, slik at vi ikke legger til den to ganger.
                if ( add )
                {
                    //("Found an accessright that we wanted to add, that existed - we overwrite it
                    // inseated, and removes the groupkey ("+groupKey+")from the addAccessRights hashtable so that it
                    // want be added later");
                    addedAccessRights.remove( groupKey );
                }

                //
                curAccessRight = (Element) curAccessRight.getNextSibling();
            }
            else
            {
                curAccessRight = (Element) curAccessRight.getNextSibling();
            }
        }
        // Add new accessrights
        for ( Object addedAccessRightKey : addedAccessRights.keySet() )
        {
            String currentGroupKey = (String) addedAccessRightKey;

            //("adding new accessright, groupkey = " + currentGroupKey);

            ExtendedMap params = addedAccessRights.get( currentGroupKey );
            Document docNewAccessRight = XMLTool.createDocument( "foo" );
            Element elNewAccessRight =
                buildAccessRightElement( docNewAccessRight, docNewAccessRight.getDocumentElement(), currentGroupKey, params );

            elExistingAccessRights.appendChild( docExistingAccessRights.importNode( elNewAccessRight, true ) );
        }

        return docExistingAccessRights;
    }

    public static int[] getIntArrayFormItem( ExtendedMap formItems, String formKey )
    {
        return getIntArrayFormItems( formItems, new String[]{formKey} );
    }

    public static int[] getIntArrayFormItems( ExtendedMap formItems, String[] formKeys )
    {
        TIntArrayList keys = new TIntArrayList();
        for ( int i = 0; i < formKeys.length; i++ )
        {
            String[] items = getArrayFormItem( formItems, formKeys[i] );
            for ( int j = 0; j < items.length; j++ )
            {
                if ( items[j] != null && items[j].length() > 0 )
                {
                    int value = Integer.parseInt( items[j] );
                    if ( !keys.contains( value ) )
                    {
                        keys.add( value );
                    }
                }
            }
        }
        return keys.toArray();
    }

    protected void addPageTemplatesOfUserSitesToDocument( AdminService admin, UserEntity user, PageTemplateType allowedPageTemplateType,
                                                          Document verticalDoc )
    {
        final Document doc = XMLTool.createDocument( "pagetemplates-in-sites" );
        Document sitesUserHaveAccessToDoc = admin.getAdminMenu( user, -1 ).getAsDOMDocument();
        Element[] allSiteElements = XMLTool.getElements( sitesUserHaveAccessToDoc.getDocumentElement() );
        for ( Element siteElement : allSiteElements )
        {
            final int siteKey = Integer.valueOf( siteElement.getAttribute( "key" ) );
            final Document supportedPageTemplatesOfSite = getSupportedPageTemplatesOfSite( siteKey, allowedPageTemplateType );
            XMLTool.mergeDocuments( doc, supportedPageTemplatesOfSite, true );
        }
        XMLTool.mergeDocuments( verticalDoc, doc, true );
    }

    protected Document getSupportedPageTemplatesOfSite( int siteKey, PageTemplateType allowedPageTemplateType )
    {
        final Document doc = XMLTool.createDocument( "pagetemplates-in-site" );
        final Element rootEl = doc.getDocumentElement();
        rootEl.setAttribute( "site", String.valueOf( siteKey ) );

        final SiteEntity site = siteDao.findByKey( siteKey );
        final Set<PageTemplateEntity> pageTemplates = site.getPageTemplates();
        final PageTemplateSpecification pageTemplateSpecification = new PageTemplateSpecification();
        pageTemplateSpecification.setType( allowedPageTemplateType );

        for ( PageTemplateEntity pageTemplate : pageTemplates )
        {
            if ( pageTemplateSpecification.satisfies( pageTemplate ) )
            {
                final Element elem = XMLTool.createElement( doc, rootEl, "pagetemplate" );
                elem.setAttribute( "key", String.valueOf( pageTemplate.getKey() ) );
                final Element contentTypesEl = XMLTool.createElement( doc, elem, "contenttypes" );

                for ( ContentTypeEntity contentTypeEntity : pageTemplate.getContentTypes() )
                {
                    final Element contentTypeEl = XMLTool.createElement( doc, contentTypesEl, "contenttype" );
                    contentTypeEl.setAttribute( "key", String.valueOf( contentTypeEntity.getKey() ) );
                }
            }
        }
        return doc;
    }

    private static class ErrorCode
    {
        int code;

        String name;

        String value;

        protected ErrorCode( int errorCode, String fieldName, String fieldValue )
        {
            code = errorCode;
            name = fieldName;
            value = fieldValue;
        }
    }

}

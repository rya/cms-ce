/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.net.URL;
import com.enonic.esl.servlet.http.CookieUtil;
import com.enonic.esl.servlet.http.HttpServletRequestWrapper;
import com.enonic.esl.util.DateUtil;
import com.enonic.esl.util.ParamsInTextParser;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.handlers.ListCountResolver;
import com.enonic.vertical.adminweb.wizard.Wizard;
import com.enonic.vertical.adminweb.wizard.WizardException;
import com.enonic.vertical.adminweb.wizard.WizardLogger;
import com.enonic.vertical.engine.AccessRight;
import com.enonic.vertical.engine.CategoryAccessRight;
import com.enonic.vertical.engine.MenuItemAccessRight;
import com.enonic.vertical.engine.SectionCriteria;
import com.enonic.vertical.engine.Types;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalEngineException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalSecurityException;
import com.enonic.vertical.engine.VerticalUpdateException;
import com.enonic.vertical.engine.criteria.CategoryCriteria;

import com.enonic.cms.framework.util.TIntArrayList;
import com.enonic.cms.framework.util.TIntHashSet;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.CmsDateAndTimeFormats;
import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.command.UnassignContentCommand;
import com.enonic.cms.core.mail.ApproveAndRejectMailTemplate;
import com.enonic.cms.core.mail.MailRecipient;
import com.enonic.cms.core.mail.SendMailService;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.structure.MenuItemXMLCreatorSetting;
import com.enonic.cms.core.structure.MenuItemXmlCreator;
import com.enonic.cms.core.structure.SiteProperties;
import com.enonic.cms.core.structure.SiteService;
import com.enonic.cms.core.structure.SiteXmlCreator;
import com.enonic.cms.core.structure.access.MenuItemAccessResolver;
import com.enonic.cms.core.structure.menuitem.MenuItemAccessType;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.menuitem.MenuItemSpecification;
import com.enonic.cms.core.structure.menuitem.MenuItemType;
import com.enonic.cms.core.structure.page.PageSpecification;
import com.enonic.cms.core.structure.page.template.PageTemplateSpecification;
import com.enonic.cms.core.structure.page.template.PageTemplateType;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.SiteDao;

import com.enonic.cms.business.DeploymentPathResolver;
import com.enonic.cms.business.SitePropertiesService;
import com.enonic.cms.core.content.ContentService;

import com.enonic.cms.core.security.SecurityService;

import com.enonic.cms.business.portal.cache.PageCacheService;
import com.enonic.cms.business.portal.cache.SiteCachesService;

import com.enonic.cms.domain.core.structure.menuitem.ApproveSectionContentCommand;
import com.enonic.cms.domain.core.structure.menuitem.RemoveContentFromSectionCommand;
import com.enonic.cms.domain.core.structure.menuitem.UnapproveSectionContentCommand;
import com.enonic.cms.core.structure.SiteEntity;


public class SectionHandlerServlet
    extends AdminHandlerBaseServlet
{
    private static final long serialVersionUID = -983028077176679176L;

    private static final String WIZARD_CONFIG_ADD_CONTENT = "wizardconfig_add_to_section.xml";

    private static final String WIZARD_CONFIG_PUBLISH = "wizardconfig_publish_to_section.xml";

    private static final String WIZARD_CONFIG_CREATE_UPDATE = "wizardconfig_create_update_section.xml";

    public static final int COOKIE_TIMEOUT = 60 * 60 * 24 * 365 * 50;

    public static class CreateUpdateSectionWizard
        extends Wizard
    {
        @Autowired
        private SiteDao siteDao;

        protected void initialize( AdminService admin, Document wizardconfigDoc )
            throws WizardException
        {
        }

        protected boolean evaluate( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                    String testCondition )
            throws WizardException
        {

            if ( testCondition.equals( "propagateRights" ) )
            {
                if ( formItems.getString( "propagate", "" ).equals( "true" ) )
                {
                    return true;
                }
            }

            return false;
        }

        private void addDefaultAccessRights( User user, int menuKey, Element rootElement, ExtendedMap formItems, AdminService admin )
        {
            Document doc = rootElement.getOwnerDocument();

            // Add default access rights...
            // - if no supersectionkey: map rights from default menurights
            // - if supersectionkey: inherit rights from supersection
            if ( !formItems.containsKey( "supersectionkey" ) )
            {
                Element newAccessRightsElem = XMLTool.createElement( doc, rootElement, "accessrights" );

                String xmlData = admin.getAccessRights( user, AccessRight.MENUITEM_DEFAULT, menuKey, true );
                Document defaultAR = XMLTool.domparse( xmlData );

                Element accessRightsElem = defaultAR.getDocumentElement();
                Element[] accessRights = XMLTool.getElements( accessRightsElem, "accessright" );
                for ( Element arElement : accessRights )
                {
                    Element newArElement = XMLTool.createElement( doc, newAccessRightsElem, "accessright" );

                    newArElement.setAttribute( "fullname", arElement.getAttribute( "fullname" ) );
                    newArElement.setAttribute( "uid", arElement.getAttribute( "uid" ) );
                    newArElement.setAttribute( "groupname", arElement.getAttribute( "groupname" ) );
                    newArElement.setAttribute( "grouptype", arElement.getAttribute( "grouptype" ) );
                    newArElement.setAttribute( "groupkey", arElement.getAttribute( "groupkey" ) );
                    newArElement.setAttribute( "read", "true" );

                    String tmp = arElement.getAttribute( "administrate" );
                    if ( "true".equals( tmp ) )
                    {
                        newArElement.setAttribute( "administrate", "true" );
                        newArElement.setAttribute( "approve", "true" );
                    }
                    else
                    {
                        newArElement.setAttribute( "administrate", "false" );
                        newArElement.setAttribute( "approve", "false" );
                    }

                    tmp = arElement.getAttribute( "create" );
                    if ( "true".equals( tmp ) )
                    {
                        newArElement.setAttribute( "publish", "true" );
                    }
                    else
                    {
                        newArElement.setAttribute( "publish", "false" );
                    }
                }
            }
            else
            {
                String xmlData = admin.getAccessRights( user, AccessRight.SECTION, formItems.getInt( "supersectionkey" ), true );
                Document defaultAR = XMLTool.domparse( xmlData );
                rootElement.appendChild( doc.importNode( defaultAR.getDocumentElement(), true ) );
            }

        }

        protected void appendCustomData( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                         ExtendedMap parameters, User user, Document dataconfigDoc, Document wizarddataDoc )
            throws WizardException
        {

            Element wizarddataElem = wizarddataDoc.getDocumentElement();

            int menuKey = formItems.getInt( "menukey" );
            SiteEntity site = siteDao.findByKey( new SiteKey( menuKey ) );
            formItems.put( "menuname", site.getName() );

            String stepName = wizardState.getCurrentStep().getName();

            // Firste step: section information
            if ( "step0".equals( stepName ) )
            {
                XMLDocument xmlDoc = admin.getContentTypes( false );
                Document doc = xmlDoc.getAsDOMDocument();
                wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );

                if ( !wizardState.getCurrentStepState().isErrorState() )
                {
                    if ( formItems.containsKey( "key" ) )
                    {
                        SectionCriteria sc = new SectionCriteria();
                        sc.setSectionKey( formItems.getInt( "key" ) );
                        sc.setAppendAccessRights( true );
                        sc.setIncludeChildCount( true );

                        String xmlData = admin.getSections( user, sc );
                        doc = XMLTool.domparse( xmlData );

                        wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );
                    }
                    else
                    {
                        addDefaultAccessRights( user, menuKey, wizarddataElem, formItems, admin );

                        int superSectionKey;
                        if ( formItems.containsKey( "supersectionkey" ) &&
                            ( superSectionKey = formItems.getInt( "supersectionkey" ) ) != -1 )
                        {

                            SectionCriteria sc = new SectionCriteria();
                            sc.setSectionKey( superSectionKey );

                            String xmlData = admin.getSections( user, sc );
                            Document superSectionDoc = XMLTool.domparse( xmlData );
                            Element parentCtyElement = XMLTool.createElement( wizarddataDoc, wizarddataElem, "parentcontenttypes" );

                            Element ctysElement = (Element) XMLTool.selectNode( superSectionDoc, "/sections/section/contenttypes" );

                            if ( ctysElement != null )
                            {
                                parentCtyElement.appendChild( wizarddataDoc.importNode( ctysElement, true ) );
                            }

                            parentCtyElement.setAttribute( "ordered",
                                                           XMLTool.getElementText( superSectionDoc, "/sections/section/@ordered" ) );
                        }
                    }
                }
            }

            // Second step: propagation of accessrights
            if ( "step1".equals( stepName ) )
            {
                SectionCriteria sc = new SectionCriteria();
                sc.setSuperSectionKey( formItems.getInt( "key" ) );
                sc.setAppendAccessRights( true );
                sc.setAdminRight( true );
                sc.setSectionRecursivly( true );

                String xmlData = admin.getSections( user, sc );
                Document sectionDoc = XMLTool.domparse( xmlData );

                wizarddataElem.appendChild( wizarddataDoc.importNode( sectionDoc.getDocumentElement(), true ) );

                Document changedDoc = servlet.buildChangedAccessRightsXML( formItems );
                wizarddataElem.appendChild( wizarddataDoc.importNode( changedDoc.getDocumentElement(), true ) );
            }

            if ( formItems.containsKey( "supersectionkey" ) )
            {
                MenuItemKey superSectionKey = new MenuItemKey( formItems.getString( "supersectionkey" ) );
                Document sectionNamesDoc = XMLTool.domparse( admin.getSuperSectionNames( superSectionKey, true ) );
                XMLTool.mergeDocuments( wizarddataDoc, sectionNamesDoc, true );
            }
            else if ( formItems.containsKey( "key" ) )
            {
                MenuItemKey sectionKey = new MenuItemKey( formItems.getString( "key" ) );
                Document sectionNamesDoc = XMLTool.domparse( admin.getSuperSectionNames( sectionKey, false ) );
                XMLTool.mergeDocuments( wizarddataDoc, sectionNamesDoc, true );
            }
        }

        private void generateSectionData( Document doc, Element sectionElement, ExtendedMap formItems, AdminService admin )
        {
            Element contentTypesElement = XMLTool.createElement( doc, sectionElement, "contenttypes" );
            if ( formItems.containsKey( "contenttypekey" ) )
            {
                String[] ctys = formItems.getStringArray( "contenttypekey" );
                for ( String cty : ctys )
                {
                    Element ctyElement = XMLTool.createElement( doc, contentTypesElement, "contenttype" );
                    ctyElement.setAttribute( "key", cty );

                    XMLTool.createElement( doc, ctyElement, "name", admin.getContentTypeName( Integer.parseInt( cty ) ) );
                }
            }

            AdminHandlerBaseServlet.buildAccessRightsXML( sectionElement, null, formItems, AccessRight.SECTION );
        }

        protected void saveState( WizardState wizardState, HttpServletRequest request, HttpServletResponse response, AdminService admin,
                                  User user, ExtendedMap formItems )
            throws WizardException
        {

            super.saveState( wizardState, request, response, admin, user, formItems );

            String stepName = wizardState.getCurrentStep().getName();
            if ( stepName.equals( "step0" ) && wizardState.getCurrentStepState().getButtonPressed().equals( "save" ) )
            {
                Document stateDoc = wizardState.getCurrentStepState().getStateDoc();
                Element stepStateElement = stateDoc.getDocumentElement();

                Element sectionElement = XMLTool.getElement( stepStateElement, "section" );
                sectionElement.setAttribute( "name", formItems.getString( "stepstate_section_name", "" ) );
                sectionElement.setAttribute( "childcount", formItems.getString( "childcount", "0" ) );

                if ( "on".equals( formItems.getString( "stepstate_section_ordered", "" ) ) )
                {
                    sectionElement.setAttribute( "ordered", "true" );
                }
                else
                {
                    sectionElement.setAttribute( "ordered", "false" );
                }

                XMLTool.createElement( stateDoc, "description", formItems.getString( "stepstate_section_description", "" ) );

                sectionElement.setAttribute( "menukey", formItems.getString( "menukey" ) );
                sectionElement.setAttribute( "supersectionkey", formItems.getString( "supersectionkey", "" ) );

                int key;
                if ( formItems.containsKey( "key" ) )
                {
                    key = formItems.getInt( "key" );
                    sectionElement.setAttribute( "key", String.valueOf( key ) );
                }

                Element ownerElement = XMLTool.createElement( stateDoc, sectionElement, "owner" );
                ownerElement.setAttribute( "key", String.valueOf( user.getKey() ) );

                Element modifierElement = XMLTool.createElement( stateDoc, sectionElement, "modifier" );
                modifierElement.setAttribute( "key", String.valueOf( user.getKey() ) );
                generateSectionData( stateDoc, sectionElement, formItems, admin );
            }
            else if ( stepName.equals( "step1" ) && wizardState.getCurrentStepState().getButtonPressed().equals( "save" ) )
            {
                Document arpDoc = buildAccessRightsForPropagation( admin, user, formItems );

                Document stateDoc = wizardState.getCurrentStepState().getStateDoc();
                stateDoc.getDocumentElement().appendChild( stateDoc.importNode( arpDoc.getDocumentElement(), true ) );
            }
        }

        private Document buildAccessRightsForPropagation( AdminService admin, User user, ExtendedMap formItems )
        {
            String applyOnlyChanges = formItems.getString( "applyonlychanges", "off" );

            Document accessRightsCollectionDoc = XMLTool.createDocument( "accessrightscollection" );
            Element arcElement = accessRightsCollectionDoc.getDocumentElement();

            // If applyOnlyChanges is "off", we overwrite existing accessrights for the subsections:
            if ( "off".equals( applyOnlyChanges ) )
            {
                Document newSectionAccessRightsDoc =
                    AdminHandlerBaseServlet.buildAccessRightsXML( null, null, formItems, AccessRight.SECTION );
                Element newSARElement = newSectionAccessRightsDoc.getDocumentElement();

                // Run through each (selected) section...
                for ( Object o : formItems.keySet() )
                {
                    String paramName = (String) o;
                    if ( paramName.startsWith( "chkPropagate[key=" ) )
                    {

                        ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                        int curSectionKey = Integer.parseInt( paramsInName.getString( "key" ) );
                        newSARElement.setAttribute( "key", String.valueOf( curSectionKey ) );

                        // Add it to the new accessrightscollection
                        arcElement.appendChild( accessRightsCollectionDoc.importNode( newSARElement, true ) );
                    }
                }
            }

            // If applyOnlyChanges is "on", we must only apply the accessrights that the user
            // actually changed to the subsections. This is a little more complicated. The code
            // below is largely copy/paste from the category servlet, modified for sections:
            else
            {
                Hashtable<String, ExtendedMap> removedSectionAccessRights = new Hashtable<String, ExtendedMap>();
                Hashtable<String, ExtendedMap> addedSectionAccessRights = new Hashtable<String, ExtendedMap>();
                Hashtable<String, ExtendedMap> modifiedSectionAccessRights = new Hashtable<String, ExtendedMap>();

                for ( Object o1 : formItems.keySet() )
                {
                    String paramName = (String) o1;
                    if ( paramName.startsWith( "arc[key=" ) )
                    {
                        ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                        String paramValue = formItems.getString( paramName );

                        ExtendedMap sectionAccessRight = ParamsInTextParser.parseParamsInText( paramValue, "[", "]", ";" );

                        String diffinfo = sectionAccessRight.getString( "diffinfo" );
                        if ( "removed".equals( diffinfo ) )
                        {
                            removedSectionAccessRights.put( paramsInName.getString( "key" ), sectionAccessRight );
                        }
                        else if ( "added".equals( diffinfo ) )
                        {
                            String groupKey = paramsInName.getString( "key" );
                            addedSectionAccessRights.put( groupKey, sectionAccessRight );
                        }
                        else if ( "modified".equals( diffinfo ) )
                        {
                            String groupKey = paramsInName.getString( "key" );
                            modifiedSectionAccessRights.put( groupKey, sectionAccessRight );
                        }
                    }
                }

                // Run through each (selected) section...
                for ( Object o : formItems.keySet() )
                {

                    String paramName = (String) o;
                    if ( paramName.startsWith( "chkPropagate[key=" ) )
                    {

                        ExtendedMap paramsInName = ParamsInTextParser.parseParamsInText( paramName, "[", "]", ";" );
                        int curSectionKey = Integer.parseInt( paramsInName.getString( "key" ) );

                        // Get existing accessrights:
                        Document docCurrentSectionAR =
                            XMLTool.domparse( admin.getAccessRights( user, AccessRight.SECTION, curSectionKey, false ) );

                        // Apply changes
                        Document docChangedSectionAR = servlet.applyChangesInAccessRights( docCurrentSectionAR, removedSectionAccessRights,
                                                                                           modifiedSectionAccessRights,
                                                                                           addedSectionAccessRights );

                        // Add to document:
                        arcElement.appendChild( accessRightsCollectionDoc.importNode( docChangedSectionAR.getDocumentElement(), true ) );
                    }
                }
            }

            return accessRightsCollectionDoc;
        }

        /**
         * @see com.enonic.vertical.adminweb.wizard.Wizard#validateState(com.enonic.vertical.adminweb.wizard.Wizard.WizardState,
         *      javax.servlet.http.HttpSession, com.enonic.cms.core.service.AdminService, com.enonic.esl.containers.ExtendedMap)
         */
        protected boolean validateState( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems )
        {

            if ( wizardState.getCurrentStep().getName().equals( "step0" ) )
            {
                if ( !formItems.containsKey( "stepstate_section_name" ) )
                {
                    wizardState.addError( "6", "stepstate_section_name" );
                    return false;
                }
            }

            return true;
        }

        protected void processWizardData( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                          User user, Document dataDoc )
            throws WizardException, VerticalEngineException
        {

            try
            {
                Document firstStepStateDoc = wizardState.getFirstStepState().getStateDoc();
                Element sectionElement = (Element) XMLTool.selectNode( firstStepStateDoc.getDocumentElement(), "/stepstate/section" );

                Document sectionDoc = XMLTool.createDocument();
                sectionDoc.appendChild( sectionDoc.importNode( sectionElement, true ) );

                int key = -1;
                String tmp = sectionElement.getAttribute( "key" );
                if ( tmp != null && tmp.length() > 0 )
                {
                    key = Integer.parseInt( tmp );
                }

                if ( key != -1 )
                {
                    admin.updateSection( user, XMLTool.documentToString( sectionDoc ) );
                }
                else
                {
                    admin.createSection( XMLTool.documentToString( sectionDoc ) );
                }

                // Propagate accessrights (if appliccable):
                StepState secondState = wizardState.getFirstStepState().getNextStepState();
                Document secondStateDoc;
                if ( secondState != null && ( secondStateDoc = secondState.getStateDoc() ) != null )
                {
                    Element stateElement = secondStateDoc.getDocumentElement();
                    Element arcElement = XMLTool.getElement( stateElement, "accessrightscollection" );

                    Element[] accessRights = XMLTool.getElements( arcElement, "accessrights" );
                    for ( Element accessRight : accessRights )
                    {
                        String keyStr = String.valueOf( key );
                        if ( !keyStr.equals( accessRight.getAttribute( "key" ) ) )
                        {
                            Document arDoc = XMLTool.createDocument();
                            arDoc.appendChild( arDoc.importNode( accessRight, true ) );

                            admin.updateAccessRights( user, XMLTool.documentToString( arDoc ) );
                        }
                    }
                }
            }
            catch ( VerticalEngineException e )
            {
                WizardLogger.errorWizard( this.getClass(), 20, "Backend error: %t", e );
            }
        }
    }

    public static class AddContentWizard
        extends Wizard
    {
        @Autowired
        private ContentDao contentDao;

        public AddContentWizard()
        {
            super();
        }

        /**
         * @see com.enonic.vertical.adminweb.wizard.Wizard#initialize(com.enonic.cms.core.service.AdminService, org.w3c.dom.Document)
         */
        protected void initialize( AdminService admin, Document wizardConfigDoc )
            throws WizardException
        {
        }

        /**
         * @see com.enonic.vertical.adminweb.wizard.Wizard#validateState(com.enonic.vertical.adminweb.wizard.Wizard.WizardState,
         *      javax.servlet.http.HttpSession, com.enonic.cms.core.service.AdminService, com.enonic.esl.containers.ExtendedMap)
         */
        protected boolean validateState( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems )
        {
            return true;
        }

        /**
         * @see com.enonic.vertical.adminweb.wizard.Wizard#evaluate(com.enonic.vertical.adminweb.wizard.Wizard.WizardState,
         *      javax.servlet.http.HttpSession, com.enonic.cms.core.service.AdminService, com.enonic.esl.containers.ExtendedMap,
         *      java.lang.String)
         */
        protected boolean evaluate( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                    String testCondition )
            throws WizardException
        {

            boolean result;
            if ( "moreOrder".equals( testCondition ) )
            {
                result = moreOrder( wizardState );
            }
            else
            {
                String message = "Unknown test condition: %0";
                WizardLogger.errorWizard( this.getClass(), 0, message, testCondition, null );
                result = false;
            }
            return result;
        }

        protected void processWizardData( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                          User user, Document dataDoc )
            throws WizardException, VerticalEngineException
        {

            admin.addContentToSections( user, XMLTool.documentToString( dataDoc ) );
        }

        protected void appendCustomData( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                         ExtendedMap parameters, User user, Document dataconfigDoc, Document wizarddataDoc )
            throws WizardException
        {
            Element wizarddataElem = wizarddataDoc.getDocumentElement();

            if ( formItems.containsKey( "selectedunitkey" ) )
            {
                int unitKey = formItems.getInt( "selectedunitkey" );
                formItems.put( "unitname", admin.getUnitName( unitKey ) );
            }

            int categoryKey = formItems.getInt( "cat" );
            Document doc = XMLTool.domparse( admin.getSuperCategoryNames( categoryKey, false, true ) );
            wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );

            int contentKey = formItems.getInt( "contentkey" );
            final ContentEntity content = contentDao.findByKey( new ContentKey( contentKey ) );
            final ContentVersionEntity contentMainVersion = content.getMainVersion();
            formItems.put( "contenttitle", contentMainVersion.getTitle() );
            formItems.put( "contenttypekey", String.valueOf( content.getCategory().getContentType().getKey() ) );

            Step currentStep = wizardState.getCurrentStep();
            if ( "step0".equals( currentStep.getName() ) || "step1".equals( currentStep.getName() ) )
            {
                // get site's menus
                wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );

                // get sections
                if ( "step0".equals( currentStep.getName() ) )
                {
                    SectionCriteria criteria = new SectionCriteria();

                    // allow only sections with publish right
                    criteria.setPublishRight( true );

                    // create tree structure and get missing sections
                    criteria.setTreeStructure( true );

                    // append all sections' access right elements
                    criteria.setAppendAccessRights( true );

                    // marks filter sections that with filtered="true"
                    criteria.setContentKeyExcludeFilter( contentKey );
                    criteria.setMarkContentFilteredSections( true );

                    // remove sections which does not allow this content type
                    int contentTypeKey = content.getCategory().getContentType().getKey();
                    criteria.setContentTypeKeyFilter( contentTypeKey );

                    doc = XMLTool.domparse( admin.getSections( user, criteria ) );
                }
                else
                {
                    // get first step's selected section keys
                    StepState stepState = wizardState.getFirstStepState();
                    Document stateDoc = stepState.getStateDoc();
                    Element stepstateElem = stateDoc.getDocumentElement();
                    Element[] sectionElems = XMLTool.getElements( stepstateElem, "section" );
                    int[] sectionKeys = new int[sectionElems.length];
                    for ( int i = 0; i < sectionElems.length; i++ )
                    {
                        sectionKeys[i] = Integer.parseInt( sectionElems[i].getAttribute( "key" ) );
                    }

                    SectionCriteria criteria = new SectionCriteria();
                    criteria.setSectionKeys( sectionKeys );
                    // criteria.setApproveRight(true);
                    criteria.setTreeStructure( true );
                    criteria.setAppendAccessRights( true );
                    doc = XMLTool.domparse( admin.getSections( user, criteria ) );
                }
                wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );
            }
            else if ( "step2".equals( currentStep.getName() ) )
            {
                StepState stepState = wizardState.getCurrentStepState();
                NormalStep step;
                int stepIdx = -1;
                do
                {
                    stepIdx++;
                    stepState = stepState.getPreviousStepState();
                    step = stepState.getStep();
                }
                while ( !"step1".equals( step.getName() ) );

                Document stateDoc = stepState.getStateDoc();
                Element stepstateElem = stateDoc.getDocumentElement();
                Element[] sectionElems = XMLTool.getElements( stepstateElem, "section" );
                int idx = 0;
                int i = -1;
                while ( idx < sectionElems.length )
                {
                    if ( "true".equals( sectionElems[idx].getAttribute( "ordered" ) ) )
                    {
                        i++;
                    }
                    if ( i < stepIdx )
                    {
                        idx++;
                    }
                    else
                    {
                        break;
                    }
                }
                MenuItemKey sectionKey = new MenuItemKey( sectionElems[idx].getAttribute( "key" ) );
                formItems.putInt( "currentsectionkey", sectionKey.toInt() );

                // get super sections for calulating section path
                doc = XMLTool.domparse( admin.getSuperSectionNames( sectionKey, true ) );

                // add section path
                Element[] sectionnameElems = XMLTool.getElements( doc.getDocumentElement() );
                int menuKey = admin.getMenuKeyBySection( sectionKey );
                StringBuffer sectionPath = new StringBuffer( admin.getMenuName( menuKey ) );
                for ( Element sectionnameElem : sectionnameElems )
                {
                    sectionPath.append( " / " );
                    sectionPath.append( XMLTool.getElementText( sectionnameElem ) );
                }
                formItems.putString( "currentsectionpath", sectionPath.toString() );

                StepState currentStepState = wizardState.getCurrentStepState();
                String buttonPressed = currentStepState.getButtonPressed();
                if ( "moveup".equals( buttonPressed ) || "movedown".equals( buttonPressed ) )
                {
                    stateDoc = currentStepState.getStateDoc();
                    stepstateElem = stateDoc.getDocumentElement();

                    // get content index (which content to move)
                    Element contentidxElem = XMLTool.getElement( stepstateElem, "contentidx" );
                    int contentIdx = Integer.parseInt( contentidxElem.getAttribute( "value" ) );

                    // move content up/down
                    Element[] contentElems = XMLTool.getElements( stepstateElem, "content" );
                    if ( "moveup".equals( buttonPressed ) )
                    {
                        stepstateElem.removeChild( contentElems[contentIdx] );
                        if ( contentIdx > 0 )
                        {
                            stepstateElem.insertBefore( contentElems[contentIdx], contentElems[contentIdx - 1] );
                            Element tempElem = contentElems[contentIdx];
                            contentElems[contentIdx] = contentElems[contentIdx - 1];
                            contentElems[contentIdx - 1] = tempElem;
                        }
                        else
                        {
                            stepstateElem.appendChild( contentElems[0] );
                            Element tempElem = contentElems[0];
                            System.arraycopy( contentElems, 1, contentElems, 0, contentElems.length - 1 );
                            contentElems[contentElems.length - 1] = tempElem;
                        }
                    }
                    else
                    {
                        stepstateElem.removeChild( contentElems[contentIdx] );
                        if ( contentIdx < contentElems.length - 1 )
                        {
                            stepstateElem.insertBefore( contentElems[contentIdx], contentElems[contentIdx + 1] );
                            Element tempElem = contentElems[contentIdx];
                            contentElems[contentIdx] = contentElems[contentIdx + 1];
                            contentElems[contentIdx + 1] = tempElem;
                        }
                        else
                        {
                            stepstateElem.insertBefore( contentElems[contentElems.length - 1], contentElems[0] );
                            Element tempElem = contentElems[contentElems.length - 1];
                            for ( int j = contentElems.length - 2; j >= 0; j-- )
                            {
                                contentElems[j + 1] = contentElems[j];
                            }
                            contentElems[0] = tempElem;
                        }
                    }

                    int[] contentKeys = new int[contentElems.length];
                    for ( int j = 0; j < contentElems.length; j++ )
                    {
                        contentKeys[j] = Integer.parseInt( contentElems[j].getAttribute( "key" ) );
                    }

                    doc = admin.getContentTitles( contentKeys ).getAsDOMDocument();
                    wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );
                }
                else
                {
                    stateDoc = currentStepState.getStateDoc();
                    stepstateElem = stateDoc.getDocumentElement();
                    Element[] contentElems = XMLTool.getElements( stepstateElem, "content" );

                    Element rootElem;
                    if ( contentElems.length > 0 )
                    {
                        int[] contentKeys = new int[contentElems.length];
                        for ( int j = 0; j < contentElems.length; j++ )
                        {
                            contentKeys[j] = Integer.parseInt( contentElems[j].getAttribute( "key" ) );
                        }

                        doc = admin.getContentTitles( contentKeys ).getAsDOMDocument();
                        rootElem = (Element) wizarddataDoc.importNode( doc.getDocumentElement(), true );
                    }
                    else
                    {
                        // get section contents
                        doc = admin.getContentTitlesBySection( sectionKey, null, 0, Integer.MAX_VALUE, false, true ).getAsDOMDocument();

                        // get content to add
                        Document tempDoc = admin.getContentTitles( new int[]{contentKey} ).getAsDOMDocument();
                        Element contenttitleElem = XMLTool.getFirstElement( tempDoc.getDocumentElement() );

                        // add content to section contents
                        rootElem = (Element) wizarddataDoc.importNode( doc.getDocumentElement(), true );
                        Element elem = XMLTool.getFirstElement( rootElem );
                        if ( elem != null )
                        {
                            rootElem.insertBefore( wizarddataDoc.importNode( contenttitleElem, true ), elem );
                        }
                        else
                        {
                            rootElem.appendChild( wizarddataDoc.importNode( contenttitleElem, true ) );
                        }
                    }
                    wizarddataElem.appendChild( rootElem );
                }
            }
            else if ( "step3".equals( currentStep.getName() ) )
            {
                // get site's menus
                // doc = XMLTool.domparse(admin.getMenuDataBySite(user, siteKey));
                wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );

                // get first step's selected section keys
                StepState stepState = wizardState.getFirstStepState();
                Document stateDoc = stepState.getStateDoc();
                Element stepstateElem = stateDoc.getDocumentElement();
                Element[] sectionElems = XMLTool.getElements( stepstateElem, "section" );
                int[] sectionKeys = new int[sectionElems.length];
                for ( int i = 0; i < sectionElems.length; i++ )
                {
                    sectionKeys[i] = Integer.parseInt( sectionElems[i].getAttribute( "key" ) );
                }

                // get selected sections
                SectionCriteria criteria = new SectionCriteria();
                criteria.setSectionKeys( sectionKeys );
                criteria.setTreeStructure( true );
                criteria.setAppendAccessRights( true );
                doc = XMLTool.domparse( admin.getSections( user, criteria ) );
                wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );
            }

            VerticalAdminLogger.debug( this.getClass(), 0, wizarddataDoc );
        }

        protected void saveState( WizardState wizardState, HttpServletRequest request, HttpServletResponse response, AdminService admin,
                                  User user, ExtendedMap formItems )
            throws WizardException
        {

            // get step state document
            StepState stepState = wizardState.getCurrentStepState();
            Document stepstateDoc = stepState.getStateDoc();
            Element rootElem = stepstateDoc.getDocumentElement();

            Step currentStep = wizardState.getCurrentStep();
            if ( "step0".equals( currentStep.getName() ) || "step1".equals( currentStep.getName() ) )
            {
                if ( "step0".equals( currentStep.getName() ) )
                {
                    Element contentElem = XMLTool.createElement( stepstateDoc, rootElem, "content" );
                    contentElem.setAttribute( "key", formItems.getString( "contentkey" ) );
                }

                String[] sectionKeys = formItems.getStringArray( "sectionkey" );
                for ( String sectionKey1 : sectionKeys )
                {
                    Element sectionElem = XMLTool.createElement( stepstateDoc, rootElem, "section" );
                    sectionElem.setAttribute( "key", sectionKey1 );
                    int sectionKey = Integer.parseInt( sectionKey1 );
                    sectionElem.setAttribute( "ordered", String.valueOf( admin.isSectionOrdered( sectionKey ) ) );
                }
            }
            else if ( "step2".equals( currentStep.getName() ) )
            {
                Element elem = XMLTool.createElement( stepstateDoc, rootElem, "section" );
                elem.setAttribute( "key", formItems.getString( "sectionkey", null ) );

                if ( formItems.containsKey( "contentidx" ) )
                {
                    elem = XMLTool.createElement( stepstateDoc, rootElem, "contentidx" );
                    elem.setAttribute( "value", formItems.getString( "contentidx", null ) );
                }

                String[] contentKeys = formItems.getStringArray( "content" );
                for ( String contentKey : contentKeys )
                {
                    Element contentElem = XMLTool.createElement( stepstateDoc, rootElem, "content" );
                    contentElem.setAttribute( "key", contentKey );
                }
            }
        }

        private boolean moreOrder( WizardState wizardState )
        {

            StepState stepState = wizardState.getCurrentStepState();
            NormalStep step = stepState.getStep();
            int sectionIndex = 0;
            while ( !"step1".equals( step.getName() ) )
            {
                stepState = stepState.getPreviousStepState();
                step = stepState.getStep();
                sectionIndex++;
            }

            Document stateDoc = stepState.getStateDoc();
            Element[] sectionElems = XMLTool.getElements( stateDoc.getDocumentElement(), "section" );
            int idx = 0;
            int i = -1;
            while ( idx < sectionElems.length )
            {
                if ( "true".equals( sectionElems[idx].getAttribute( "ordered" ) ) )
                {
                    i++;
                }
                if ( i < sectionIndex )
                {
                    idx++;
                }
                else
                {
                    return true;
                }
            }
            return false;
        }
    }

    public static class PublishWizard
        extends Wizard
    {
        @Autowired
        private ContentService contentService;

        @Autowired
        private SecurityService securityService;

        @Autowired
        private MenuItemDao menuItemDao;

        @Autowired
        private SendMailService sendMailService;

        @Autowired
        private SiteCachesService siteCachesService;

        @Autowired
        private SiteService siteService;

        @Autowired
        private SitePropertiesService sitePropertiesService;

        @Autowired
        private GroupDao groupDao;

        public PublishWizard()
        {
            super();
        }

        protected void initialize( AdminService admin, Document wizardConfigDoc )
            throws WizardException
        {
        }

        protected boolean validateState( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems )
        {
            return true;
        }

        protected boolean evaluate( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                    String testCondition )
            throws WizardException
        {
            User user = securityService.getLoggedInAdminConsoleUser();
            boolean result;
            if ( "moreOrder".equals( testCondition ) )
            {
                result = moreOrder( user, wizardState, admin );
            }
            else if ( "noSites".equals( testCondition ) )
            {
                result = noSites( wizardState );
            }
            else
            {
                String message = "Unknown test condition: %0";
                WizardLogger.errorWizard( this.getClass(), 0, message, testCondition, null );
                result = false;
            }

            return result;
        }

        protected void processWizardData( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                          User user, Document dataDoc )
            throws WizardException, VerticalEngineException
        {
            Step step = wizardState.getCurrentStep();
            String finishName = step.getName();

            try
            {
                int contentKey;
                int versionKey = formItems.getInt( "versionkey", -1 );
                if ( versionKey < 0 )
                {
                    contentKey = formItems.getInt( "contentkey" );
                    versionKey = admin.getCurrentVersionKey( contentKey );
                    formItems.put( "versionkey", versionKey );
                }
                else
                {
                    contentKey = admin.getContentKeyByVersionKey( versionKey );
                    formItems.put( "contentkey", contentKey );
                }

                if ( "finish0".equals( finishName ) )
                {
                    processWizardData0( wizardState, admin, user, contentKey, versionKey );
                }
                else
                {
                    processWizardData0( wizardState, admin, user, contentKey, versionKey );
                    processWizardData1( wizardState, admin, user );
                }
            }
            catch ( ParseException pe )
            {
                String message = "Failed to parse a date: %t";
                WizardLogger.errorWizard( this.getClass(), 0, message, pe );
            }
        }

        private void processWizardData0( WizardState wizardState, AdminService admin, User user, int contentKey, int versionKey )
            throws ParseException, VerticalUpdateException
        {
            Document stateDoc = wizardState.getFirstStepState().getStateDoc();
            Element rootElem = stateDoc.getDocumentElement();
            Element statusElem = XMLTool.getElement( rootElem, "status" );
            int status;
            if ( statusElem != null )
            {
                status = Integer.valueOf( XMLTool.getElementText( statusElem ) );
            }
            else
            {
                status = -1;
            }
            int originalStatus = admin.getContentStatus( versionKey );

            switch ( status )
            {
                case 0:
                {
                    if ( originalStatus == 1 )
                    {
                        // reject approval
                        sendMessage( user, stateDoc, contentKey, originalStatus );
                        admin.updateContentPublishing( user, contentKey, versionKey, 0, null, null );
                    }
                    break;
                }
                case 1:
                {
                    if ( originalStatus == 0 )
                    {
                        // send to approval
                        sendMessage( user, stateDoc, contentKey, originalStatus );
                        admin.updateContentPublishing( user, contentKey, versionKey, 1, null, null );
                    }
                    break;
                }
                case 2:
                {
                    Date from = null;
                    Date to = null;

                    // approve content/keep approval
                    final Element publishingElem = XMLTool.getElement( rootElem, "publishing" );
                    if ( publishingElem != null )
                    {
                        final String fromStr = publishingElem.getAttribute( "from" );
                        if ( fromStr != null && fromStr.length() > 0 )
                        {
                            from = DateUtil.parseISODateTime( fromStr );
                        }
                        final String toStr = publishingElem.getAttribute( "to" );
                        if ( toStr != null && toStr.length() > 0 )
                        {
                            to = DateUtil.parseISODateTime( toStr );
                        }
                    }

                    admin.updateContentPublishing( user, contentKey, versionKey, 2, from, to );

                    UnassignContentCommand unassignCommand = new UnassignContentCommand();
                    unassignCommand.setContentKey( new ContentKey( contentKey ) );
                    unassignCommand.setUnassigner( user.getKey() );
                    contentService.unassignContent( unassignCommand );

                    break;
                }
                case 3:
                {
                    // archive content
                    admin.updateContentPublishing( user, contentKey, versionKey, 3, null, null );

                    UnassignContentCommand unassignCommand = new UnassignContentCommand();
                    unassignCommand.setContentKey( new ContentKey( contentKey ) );
                    unassignCommand.setUnassigner( user.getKey() );
                    contentService.unassignContent( unassignCommand );

                    break;
                }
            }
        }


        private void sendMessage( User user, Document stateDoc, int contentKey, int originalStatus )
        {

            Element rootElem = stateDoc.getDocumentElement();
            Element recipientsElem = XMLTool.getElement( rootElem, "recipients" );
            Element[] recipientElems = XMLTool.getElements( recipientsElem );
            if ( recipientElems.length > 0 )
            {

                Element messageElem = XMLTool.getElement( rootElem, "message" );
                if ( messageElem != null )
                {

                    final UserEntity userEntity = securityService.getUser( user );

                    String body = XMLTool.getElementText( messageElem );

                    ApproveAndRejectMailTemplate mailCreator =
                        new ApproveAndRejectMailTemplate( body, new ContentKey( contentKey ), userEntity );

                    //reject
                    if ( originalStatus == 1 )
                    {
                        mailCreator.setReject( true );
                    }

                    //send to approval
                    if ( originalStatus == 0 )
                    {
                        mailCreator.setReject( false );
                    }

                    mailCreator.setFrom( new MailRecipient( user.getDisplayName(), user.getEmail() ) );

                    for ( Element recipientElem : recipientElems )
                    {
                        String recipientName = recipientElem.getAttribute( "name" );
                        String recipientEmail = recipientElem.getAttribute( "email" );
                        mailCreator.addRecipient( new MailRecipient( recipientName, recipientEmail ) );
                    }

                    sendMailService.sendMail( mailCreator );
                }
            }
        }

        private void processWizardData1( WizardState wizardState, AdminService admin, User user )
            throws VerticalCreateException, VerticalUpdateException, VerticalSecurityException
        {
            Document stateDoc = wizardState.getFirstStepState().getStateDoc();
            Element elem = XMLTool.getElement( stateDoc.getDocumentElement(), "content" );
            int contentKey = Integer.parseInt( elem.getAttribute( "key" ) );

            Document sectionsDoc = XMLTool.createDocument( "sections" );
            Element sectionsElem = sectionsDoc.getDocumentElement();
            sectionsElem.setAttribute( "contentkey", String.valueOf( contentKey ) );

            stateDoc = wizardState.getStepState( "step1" ).getStateDoc();
            Element[] menuElems = XMLTool.getElements( stateDoc.getDocumentElement(), "menu" );
            Map<SiteKey, List<MenuItemKey>> listOfMenuItemKeysBySiteKey = new HashMap<SiteKey, List<MenuItemKey>>();
            int manualOrderIndex = 0;
            for ( Element menuElem : menuElems )
            {
                int menuKey = Integer.parseInt( menuElem.getAttribute( "key" ) );
                SiteKey siteKey = new SiteKey( menuKey );
                // set content home (menu item) and framework
                int categoryKey = admin.getCategoryKey( contentKey );
                CategoryAccessRight categoryAccessRight = admin.getCategoryAccessRight( user, categoryKey );
                if ( categoryAccessRight.getPublish() )
                {
                    Element homeElem = XMLTool.getElement( menuElem, "home" );
                    int homeKey;
                    if ( homeElem != null )
                    {
                        homeKey = Integer.parseInt( homeElem.getAttribute( "key" ) );
                    }
                    else
                    {
                        homeKey = -1;
                    }
                    Element pageTemplateElem = XMLTool.getElement( menuElem, "pagetemplate" );
                    int pageTemplateKey;
                    if ( pageTemplateElem != null )
                    {
                        pageTemplateKey = Integer.parseInt( pageTemplateElem.getAttribute( "key" ) );
                    }
                    else
                    {
                        pageTemplateKey = -1;
                    }
                    admin.setContentHome( user, contentKey, menuKey, homeKey, pageTemplateKey );
                }

                Element[] menuitemElems = XMLTool.getElements( menuElem, "menuitem" );
                for ( Element menuitemElem : menuitemElems )
                {
                    boolean manuallyOrder = Boolean.valueOf( menuitemElem.getAttribute( "manuallyOrder" ) );
                    boolean ordered = Boolean.valueOf( menuitemElem.getAttribute( "ordered" ) );
                    MenuItemKey menuItemKey = new MenuItemKey( menuitemElem.getAttribute( "key" ) );
                    MenuItemKey sectionKey = admin.getSectionKeyByMenuItemKey( menuItemKey );

                    List<MenuItemKey> menuItemKeysBySiteKey = listOfMenuItemKeysBySiteKey.get( siteKey );
                    if ( menuItemKeysBySiteKey == null )
                    {
                        menuItemKeysBySiteKey = new ArrayList<MenuItemKey>();
                        listOfMenuItemKeysBySiteKey.put( siteKey, menuItemKeysBySiteKey );
                    }
                    menuItemKeysBySiteKey.add( menuItemKey );

                    Element sectionElem = XMLTool.createElement( sectionsDoc, sectionsElem, "section" );
                    sectionElem.setAttribute( "key", String.valueOf( sectionKey ) );
                    MenuItemAccessRight menuItemAccessRight = admin.getMenuItemAccessRight( user, menuItemKey );
                    sectionElem.setAttribute( "approved", String.valueOf( menuItemAccessRight.getPublish() ) );
                    sectionElem.setAttribute( "ordered", Boolean.toString( ordered ) );
                    sectionElem.setAttribute( "manuallyOrder", Boolean.toString( manuallyOrder ) );

                    if ( ordered && manuallyOrder )
                    {
                        StepState stepState = wizardState.getStepState( "step1" );
                        int k = -1;
                        do
                        {
                            stepState = stepState.getNextStepState();
                            k++;
                        }
                        while ( k < manualOrderIndex );
                        Element contentsElem = XMLTool.createElement( sectionsDoc, sectionElem, "contents" );
                        Document tempStateDoc = stepState.getStateDoc();
                        Element tempSectionElem = XMLTool.getFirstElement( tempStateDoc.getDocumentElement() );
                        Element[] tempContentElems = XMLTool.getElements( tempSectionElem );
                        for ( Element tempContentElem : tempContentElems )
                        {
                            contentsElem.appendChild( sectionsDoc.importNode( tempContentElem, true ) );
                        }
                        manualOrderIndex++;
                    }
                }
            }

            admin.addContentToSections( user, XMLTool.documentToString( sectionsDoc ) );

            for ( SiteKey siteKey : listOfMenuItemKeysBySiteKey.keySet() )
            {
                PageCacheService pageCacheService = siteCachesService.getPageCacheService( siteKey );
                List<MenuItemKey> menuItemKeys = listOfMenuItemKeysBySiteKey.get( siteKey );
                for ( MenuItemKey menuItemKeyToRemoveCacheEntriesFor : menuItemKeys )
                {
                    pageCacheService.removeEntriesByMenuItem( menuItemKeyToRemoveCacheEntriesFor );
                }
            }
        }

        protected void appendCustomData( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                         ExtendedMap parameters, User user, Document dataconfigDoc, Document wizarddataDoc )
            throws WizardException
        {
            if ( formItems.containsKey( "selectedunitkey" ) )
            {
                int unitKey = formItems.getInt( "selectedunitkey" );
                formItems.put( "unitname", admin.getUnitName( unitKey ) );
            }

            int categoryKey = formItems.getInt( "cat" );
            Document doc = XMLTool.domparse( admin.getSuperCategoryNames( categoryKey, false, true ) );
            Element wizarddataElem = wizarddataDoc.getDocumentElement();
            wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );

            int contentKey;
            int versionKey = formItems.getInt( "versionkey", -1 );
            if ( versionKey < 0 )
            {
                contentKey = formItems.getInt( "contentkey" );
                versionKey = admin.getCurrentVersionKey( contentKey );
                formItems.put( "versionkey", versionKey );
            }
            else
            {
                contentKey = admin.getContentKeyByVersionKey( versionKey );
                formItems.put( "contentkey", contentKey );
            }

            formItems.put( "contenttitle", admin.getContentTitle( versionKey ) );
            int contentTypeKey = admin.getContentTypeKey( contentKey );
            formItems.put( "contenttypekey", String.valueOf( contentTypeKey ) );

            Step currentStep = wizardState.getCurrentStep();
            if ( "step0".equals( currentStep.getName() ) )
            {
                appendCustomDataStep0( user, admin, wizardState, wizarddataDoc, parameters, categoryKey, contentKey, versionKey );
            }
            else if ( "step1".equals( currentStep.getName() ) )
            {
                appendCustomDataStep1( user, admin, wizardState, wizarddataDoc, contentKey, versionKey );
            }
            else if ( "step2".equals( currentStep.getName() ) )
            {
                appendCustomDataStep2( admin, wizardState, wizarddataDoc, formItems, versionKey );
            }
            else if ( "step3".equals( currentStep.getName() ) )
            {
                appendCustomDataStep3( user, admin, wizardState, wizarddataDoc, contentKey, versionKey );
            }

            VerticalAdminLogger.debug( this.getClass(), 0, wizarddataDoc );
        }

        private void appendCustomDataStep0( User user, AdminService admin, WizardState wizardState, Document wizarddataDoc,
                                            ExtendedMap parameters, int categoryKey, int contentKey, int versionKey )
        {
            Element wizarddataElem = wizarddataDoc.getDocumentElement();

            // get content version
            Document doc = XMLTool.domparse( admin.getContentVersion( user, versionKey ) );
            Element contentElem = XMLTool.getFirstElement( doc.getDocumentElement() );
            int originalStatus = Integer.valueOf( contentElem.getAttribute( "status" ) );
            wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );

            // sites
            int contentTypeKey = admin.getContentTypeKey( contentKey );

            List<SiteEntity> sites = siteService.getSitesToPublishTo( contentTypeKey, user );
            SiteXmlCreator siteXmlCreator = new SiteXmlCreator( null );
            siteXmlCreator.setIncludeMenuItems( false );

            Map<SiteKey, SiteProperties> sitePropertyMap = new HashMap<SiteKey, SiteProperties>();

            for ( SiteEntity site : sites )
            {
                SiteProperties siteProperties = sitePropertiesService.getSiteProperties( site.getKey() );
                sitePropertyMap.put( site.getKey(), siteProperties );
            }

            XMLDocument sitesToPublishTo = siteXmlCreator.createLegacyGetMenus( sites, sitePropertyMap );

            doc = sitesToPublishTo.getAsDOMDocument();
            wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );

            if ( !admin.isContentVersionApproved( versionKey ) )
            {
                Document stateDoc = wizardState.getCurrentStepState().getStateDoc();
                Element publishingElem = XMLTool.getElement( stateDoc.getDocumentElement(), "publishing" );
                if ( publishingElem == null )
                {
                    // we need to keep the current publishFrom and publishTo dates if they are set
                    publishingElem = XMLTool.createElement( stateDoc, stateDoc.getDocumentElement(), "publishing" );
                    if ( !"".equals( contentElem.getAttribute( "publishfrom" ) ) )
                    {
                        publishingElem.setAttribute( "from", contentElem.getAttribute( "publishfrom" ) );
                    }
                    else
                    {
                        publishingElem.setAttribute( "from", CmsDateAndTimeFormats.printAs_STORE_DATE( ( new Date() ) ) );
                    }
                    if ( !"".equals( contentElem.getAttribute( "publishto" ) ) )
                    {
                        publishingElem.setAttribute( "to", contentElem.getAttribute( "publishto" ) );
                    }
                }
            }

            Document stateDoc = wizardState.getCurrentStepState().getStateDoc();
            Element statusElem = XMLTool.getElement( stateDoc.getDocumentElement(), "status" );
            int status = ( statusElem == null ? originalStatus : Integer.valueOf( XMLTool.getElementText( statusElem ) ) );
            if ( ( originalStatus == 0 && status == 1 ) || "loadRecipients".equals( wizardState.getCurrentStepState().getButtonPressed() ) )
            {
                doc = XMLTool.domparse( admin.getUsersWithPublishRight( categoryKey ) );
                wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );
                parameters.put( "notify", "sendtoapproval" );
            }
            else if ( ( originalStatus == 1 && status == 0 ) || "loadOwner".equals( wizardState.getCurrentStepState().getButtonPressed() ) )
            {
                doc = XMLTool.domparse( admin.getContentOwner( contentKey ) );
                wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );
                parameters.put( "notify", "reject" );
            }
        }

        // publishing

        private void appendCustomDataStep1( User user, AdminService admin, WizardState wizardState, Document wizarddataDoc, int contentKey,
                                            int versionKey )
        {
            Element wizarddataElem = wizarddataDoc.getDocumentElement();

            // get first step's selected menu keys
            Document stateDoc = wizardState.getFirstStepState().getStateDoc();
            Element stepstateElem = stateDoc.getDocumentElement();
            Element[] menuElems = XMLTool.getElements( stepstateElem, "menu" );
            int[] menuKeys;
            if ( menuElems.length > 0 )
            {
                menuKeys = new int[menuElems.length];
                for ( int i = 0; i < menuElems.length; i++ )
                {
                    menuKeys[i] = Integer.parseInt( menuElems[i].getAttribute( "key" ) );

                    Document doc = XMLTool.domparse( admin.getPageTemplatesByMenu( menuKeys[i], EXCLUDED_TYPE_KEYS_IN_PREVIEW ) );
                    wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );
                }

                // sites
                final List<MenuItemEntity> menuItems = getAccessibleMenuItems( user, menuKeys );
                Document menuItemsDoc = createElementsToList( menuItems );
                wizarddataElem.appendChild( wizarddataDoc.importNode( menuItemsDoc.getDocumentElement(), true ) );

                // sections
                SectionCriteria criteria = new SectionCriteria();
                criteria.setSiteKeys( menuKeys );
                criteria.setTreeStructure( false );
                criteria.setAppendAccessRights( false );
                criteria.setContentKeyExcludeFilter( contentKey );
                criteria.setMarkContentFilteredSections( true );
                int contentTypeKey = admin.getContentTypeKey( contentKey );
                criteria.setContentTypeKeyFilter( contentTypeKey );
                criteria.setIncludeSectionsWithoutContentTypeEvenWhenFilterIsSet( false );
                criteria.setIncludeSectionContentTypesInfo( false );
                Document sectionsDoc = XMLTool.domparse( admin.getSections( user, criteria ) );
                wizarddataElem.appendChild( wizarddataDoc.importNode( sectionsDoc.getDocumentElement(), true ) );
            }

            // get content version
            Document doc = XMLTool.domparse( admin.getContentVersion( user, versionKey ) );
            wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );

            doc = XMLTool.domparse( admin.getContentHomes( contentKey ) );
            wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );
        }

        // position content in section

        private void appendCustomDataStep2( AdminService admin, WizardState wizardState, Document wizarddataDoc, ExtendedMap formItems,
                                            int versionKey )
        {
            StepState stepState = wizardState.getCurrentStepState();
            NormalStep step;
            int sectionIndex = -1;
            do
            {
                sectionIndex++;
                stepState = stepState.getPreviousStepState();
                step = stepState.getStep();
            }
            while ( !"step1".equals( step.getName() ) );
            formItems.put( "sectionnumber", String.valueOf( sectionIndex + 1 ) );

            MenuItemKey menuItemKey = null;
            Document stateDoc = stepState.getStateDoc();
            Element stepstateElem = stateDoc.getDocumentElement();
            Element[] menuElems = XMLTool.getElements( stepstateElem, "menu" );
            int idx = 0;
            outer:
            for ( Element menuElem : menuElems )
            {
                Element[] menuitemElems = XMLTool.getElements( menuElem, "menuitem" );
                for ( Element menuitemElem : menuitemElems )
                {
                    boolean manuallyOrder = Boolean.valueOf( menuitemElem.getAttribute( "manuallyOrder" ) );
                    boolean ordered = Boolean.valueOf( menuitemElem.getAttribute( "ordered" ) );
                    if ( manuallyOrder && ordered )
                    {
                        if ( idx == sectionIndex )
                        {
                            menuItemKey = new MenuItemKey( Integer.parseInt( menuitemElem.getAttribute( "key" ) ) );
                            break outer;
                        }
                        else
                        {
                            idx++;
                        }
                    }
                }
            }
            formItems.putInt( "menuitemkey", menuItemKey.toInt() );
            String path = admin.getPathString( Types.MENUITEM, menuItemKey.toInt() );
            formItems.put( "path", path );

            Element wizarddataElem = wizarddataDoc.getDocumentElement();
            StepState currentStepState = wizardState.getCurrentStepState();
            String buttonPressed = currentStepState.getButtonPressed();
            if ( "moveup".equals( buttonPressed ) || "movedown".equals( buttonPressed ) )
            {
                stateDoc = currentStepState.getStateDoc();
                stepstateElem = stateDoc.getDocumentElement();

                // get content index (which content to move)
                Element contentidxElem = XMLTool.getElement( stepstateElem, "contentidx" );
                int contentIdx = Integer.parseInt( contentidxElem.getAttribute( "value" ) );

                // move content up/down
                Element sectionElem = XMLTool.getElement( stepstateElem, "section" );
                Element[] contentElems = XMLTool.getElements( sectionElem );
                if ( "moveup".equals( buttonPressed ) )
                {
                    sectionElem.removeChild( contentElems[contentIdx] );
                    if ( contentIdx > 0 )
                    {
                        sectionElem.insertBefore( contentElems[contentIdx], contentElems[contentIdx - 1] );
                        Element tempElem = contentElems[contentIdx];
                        contentElems[contentIdx] = contentElems[contentIdx - 1];
                        contentElems[contentIdx - 1] = tempElem;
                    }
                    else
                    {
                        sectionElem.appendChild( contentElems[0] );
                        Element tempElem = contentElems[0];
                        System.arraycopy( contentElems, 1, contentElems, 0, contentElems.length - 1 );
                        contentElems[contentElems.length - 1] = tempElem;
                    }
                }
                else
                {
                    sectionElem.removeChild( contentElems[contentIdx] );
                    if ( contentIdx < contentElems.length - 1 )
                    {
                        sectionElem.insertBefore( contentElems[contentIdx], contentElems[contentIdx + 1] );
                        Element tempElem = contentElems[contentIdx];
                        contentElems[contentIdx] = contentElems[contentIdx + 1];
                        contentElems[contentIdx + 1] = tempElem;
                    }
                    else
                    {
                        sectionElem.insertBefore( contentElems[contentElems.length - 1], contentElems[0] );
                        Element tempElem = contentElems[contentElems.length - 1];
                        for ( int j = contentElems.length - 2; j >= 0; j-- )
                        {
                            contentElems[j + 1] = contentElems[j];
                        }
                        contentElems[0] = tempElem;
                    }
                }

                int[] contentKeys = new int[contentElems.length];
                for ( int j = 0; j < contentElems.length; j++ )
                {
                    contentKeys[j] = Integer.parseInt( contentElems[j].getAttribute( "key" ) );
                }

                Document doc = admin.getContentTitles( contentKeys ).getAsDOMDocument();
                wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );
            }
            else
            {
                stateDoc = currentStepState.getStateDoc();
                stepstateElem = stateDoc.getDocumentElement();
                Element[] contentElems = XMLTool.getElements( stepstateElem, "content" );

                Element rootElem;
                if ( contentElems.length > 0 )
                {
                    int[] contentKeys = new int[contentElems.length];
                    for ( int j = 0; j < contentElems.length; j++ )
                    {
                        contentKeys[j] = Integer.parseInt( contentElems[j].getAttribute( "key" ) );
                    }

                    Document doc = admin.getContentTitles( contentKeys ).getAsDOMDocument();
                    rootElem = (Element) wizarddataDoc.importNode( doc.getDocumentElement(), true );
                }
                else
                {
                    // get section contents
                    MenuItemKey sectionKey = admin.getSectionKeyByMenuItemKey( menuItemKey );
                    Document doc =
                        admin.getContentTitlesBySection( sectionKey, null, 0, Integer.MAX_VALUE, false, true ).getAsDOMDocument();

                    // get content to add
                    Document tempDoc = XMLTool.domparse( admin.getContentTitleXML( versionKey ) );
                    Element contenttitleElem = XMLTool.getFirstElement( tempDoc.getDocumentElement() );

                    // add content to section contents
                    rootElem = (Element) wizarddataDoc.importNode( doc.getDocumentElement(), true );
                    Element elem = XMLTool.getFirstElement( rootElem );
                    if ( elem != null )
                    {
                        rootElem.insertBefore( wizarddataDoc.importNode( contenttitleElem, true ), elem );
                    }
                    else
                    {
                        rootElem.appendChild( wizarddataDoc.importNode( contenttitleElem, true ) );
                    }
                }
                wizarddataElem.appendChild( rootElem );
            }
        }

        // confirm publishing

        private void appendCustomDataStep3( User user, AdminService admin, WizardState wizardState, Document wizarddataDoc, int contentKey,
                                            int versionKey )
        {
            Element wizarddataElem = wizarddataDoc.getDocumentElement();

            // get content version
            Document doc = XMLTool.domparse( admin.getContentVersion( user, versionKey ) );
            wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );

            // get step 1's menu and section keys
            Document stateDoc = wizardState.getStepState( "step1" ).getStateDoc();
            Element stepstateElem = stateDoc.getDocumentElement();
            Element[] menuElems = XMLTool.getElements( stepstateElem, "menu" );
            TIntArrayList menuKeyList = new TIntArrayList();
            if ( menuElems.length > 0 )
            {
                for ( Element menuElem : menuElems )
                {
                    int menuKey = Integer.parseInt( menuElem.getAttribute( "key" ) );
                    menuKeyList.add( menuKey );

                    Element pagetemplateElem = XMLTool.getElement( menuElem, "pagetemplate" );
                    if ( pagetemplateElem != null )
                    {
                        int pageTemplateKey = Integer.parseInt( pagetemplateElem.getAttribute( "key" ) );
                        Document tempDoc = XMLTool.domparse( admin.getPageTemplate( pageTemplateKey ) );
                        Element pagetemplatesElem = XMLTool.getElement( wizarddataElem, "pagetemplates" );
                        if ( pagetemplatesElem != null )
                        {
                            pagetemplatesElem.appendChild(
                                wizarddataDoc.importNode( XMLTool.getFirstElement( tempDoc.getDocumentElement() ), true ) );
                        }
                        else
                        {
                            wizarddataElem.appendChild( wizarddataDoc.importNode( tempDoc.getDocumentElement(), true ) );
                        }
                    }
                }

                // Added menu items
                final TIntArrayList menuItemKeys = getSelectedMenuItemKeys( stateDoc );

                // Added sections
                SectionCriteria criteria = new SectionCriteria();
                criteria.setMenuItemKeys( menuItemKeys.toArray() );
                criteria.setTreeStructure( false );
                criteria.setAppendAccessRights( false );
                criteria.setMarkContentFilteredSections( true );
                final int contentTypeKey = admin.getContentTypeKey( contentKey );
                criteria.setContentTypeKeyFilter( contentTypeKey );
                criteria.setIncludeSectionsWithoutContentTypeEvenWhenFilterIsSet( false );
                criteria.setIncludeSectionContentTypesInfo( false );
                doc = XMLTool.domparse( admin.getSections( user, criteria ) );
                wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );

                // Previous sections
                criteria = new SectionCriteria();
                criteria.setTreeStructure( false );
                criteria.setAppendAccessRights( false );
                criteria.setContentKey( contentKey );
                criteria.setMarkContentFilteredSections( true );
                criteria.setIncludeSectionsWithoutContentTypeEvenWhenFilterIsSet( false );
                criteria.setIncludeSectionContentTypesInfo( false );
                doc = XMLTool.domparse( admin.getSections( user, criteria ) );
                wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );

                // Previous menu items
                menuItemKeys.add( getPreviousSelectedMenuItemKeys( doc ).toArray() );

                doc = createElementsToList( menuItemKeys );
                wizarddataElem.appendChild( wizarddataDoc.importNode( doc.getDocumentElement(), true ) );
            }
        }

        protected void saveState( WizardState wizardState, HttpServletRequest request, HttpServletResponse response, AdminService admin,
                                  User user, ExtendedMap formItems )
            throws WizardException
        {
            // get step state document
            StepState stepState = wizardState.getCurrentStepState();
            Document stepstateDoc = stepState.getStateDoc();

            try
            {
                Step currentStep = wizardState.getCurrentStep();
                if ( "step0".equals( currentStep.getName() ) )
                {
                    int contentKey;
                    int versionKey = formItems.getInt( "versionkey", -1 );
                    if ( versionKey < 0 )
                    {
                        contentKey = formItems.getInt( "contentkey" );
                        versionKey = admin.getCurrentVersionKey( contentKey );
                        formItems.put( "versionkey", versionKey );
                    }
                    else
                    {
                        contentKey = admin.getContentKeyByVersionKey( versionKey );
                        formItems.put( "contentkey", contentKey );
                    }
                    saveStateStep0( admin, stepstateDoc, formItems, contentKey, versionKey );
                }
                else if ( "step1".equals( currentStep.getName() ) )
                {
                    saveStateStep1( wizardState, admin, stepstateDoc, formItems );
                }
                else if ( "step2".equals( currentStep.getName() ) )
                {
                    saveStateStep2( admin, stepstateDoc, formItems );
                }
            }
            catch ( ParseException pe )
            {
                String message = "Failed to parse a date: %t";
                WizardLogger.errorWizard( this.getClass(), 0, message, pe );
            }
        }

        // approval and site selection

        private void saveStateStep0( AdminService admin, Document stepstateDoc, ExtendedMap formItems, int contentKey, int versionKey )
            throws ParseException, WizardException
        {
            Element rootElem = stepstateDoc.getDocumentElement();

            Element contentElem = XMLTool.createElement( stepstateDoc, rootElem, "content" );
            contentElem.setAttribute( "key", String.valueOf( contentKey ) );

            // status
            int status = formItems.getInt( "status", -1 );
            if ( status >= 0 )
            {
                XMLTool.createElement( stepstateDoc, rootElem, "status", String.valueOf( status ) );
            }
            int originalStatus = admin.getContentStatus( versionKey );

            switch ( status )
            {
                case 0:
                {
                    if ( originalStatus == 1 )
                    {
                        // reject approval
                        saveRecipients( stepstateDoc, formItems );
                        saveMessage( stepstateDoc, formItems );
                    }
                    break;
                }
                case 1:
                {
                    if ( originalStatus == 0 )
                    {
                        // send to approval
                        saveRecipients( stepstateDoc, formItems );
                        saveMessage( stepstateDoc, formItems );
                    }
                    break;
                }
                case 2:
                {
                    // publish from/to
                    if ( formItems.containsKey( "datepublishfrom" ) || formItems.containsKey( "datepublishto" ) ||
                        formItems.containsKey( "publishfrom_now" ) )
                    {
                        Element publishingElem = XMLTool.createElement( stepstateDoc, rootElem, "publishing" );
                        String date = formItems.getString( "datepublishfrom", null );
                        if ( date != null )
                        {
                            String time = formItems.getString( "timepublishfrom", null );
                            String datetime;
                            if ( time != null )
                            {
                                datetime = date + " " + time;
                            }
                            else
                            {
                                datetime = date + " 00:00";
                            }
                            Date publishFrom = DateUtil.parseDateTime( datetime );
                            publishingElem.setAttribute( "from", DateUtil.formatISODateTime( publishFrom ) );
                        }
                        date = formItems.getString( "datepublishto", null );
                        if ( date != null )
                        {
                            String time = formItems.getString( "timepublishto", null );
                            String datetime;
                            if ( time != null )
                            {
                                datetime = date + " " + time;
                            }
                            else
                            {
                                datetime = date + " 00:00";
                            }
                            Date publishto = DateUtil.parseDateTime( datetime );
                            publishingElem.setAttribute( "to", DateUtil.formatISODateTime( publishto ) );
                        }
                    }
                    break;
                }
                case 3:
                {
                    // ignore, nothing to save
                    break;
                }
                default:
                {
                    if ( originalStatus != 2 )
                    {
                        WizardLogger.errorWizard( this.getClass(), 0, "Unknown status: %0", String.valueOf( status ), null );
                    }
                    break;
                }
            }

            if ( status == 1 || status == 2 || ( originalStatus == 2 && status == -1 ) )
            {
                // sites
                String[] menuKeys = formItems.getStringArray( "menukey" );
                for ( String menuKey : menuKeys )
                {
                    Element sectionElem = XMLTool.createElement( stepstateDoc, rootElem, "menu" );
                    sectionElem.setAttribute( "key", menuKey );
                }
            }
        }

        private void saveRecipients( Document stepstateDoc, ExtendedMap formItems )
        {
            Element rootElem = stepstateDoc.getDocumentElement();
            Element recipientsElem = XMLTool.createElement( stepstateDoc, rootElem, "recipients" );
            String[] recipientKeys = formItems.getStringArray( "recipientkeys" );
            for ( String recipientKey : recipientKeys )
            {
                Element recipientElem = XMLTool.createElement( stepstateDoc, recipientsElem, "recipient" );
                recipientElem.setAttribute( "key", recipientKey );
                recipientElem.setAttribute( "name", formItems.getString( "name_" + recipientKey ) );
                recipientElem.setAttribute( "email", formItems.getString( "email_" + recipientKey ) );
            }
        }

        private void saveMessage( Document stepstateDoc, ExtendedMap formItems )
        {
//            if ( formItems.containsKey( "subject" )  )
//            {
            Element rootElem = stepstateDoc.getDocumentElement();
            Element messageElem = XMLTool.createElement( stepstateDoc, rootElem, "message" );
            //messageElem.setAttribute( "subject", formItems.getString( "subject" ) );
            if ( formItems.containsKey( "body" ) )
            {
                XMLTool.createCDATASection( stepstateDoc, messageElem, formItems.getString( "body" ) );
            }
//            }

        }

        // publishing

        private void saveStateStep1( WizardState wizardState, AdminService admin, Document stepstateDoc, ExtendedMap formItems )
        {
            Document stateDoc = wizardState.getFirstStepState().getStateDoc();
            Element stepstateElem = stateDoc.getDocumentElement();
            Element[] menuElems = XMLTool.getElements( stepstateElem, "menu" );
            if ( menuElems.length > 0 )
            {
                // select
                for ( Element menuElem1 : menuElems )
                {
                    int menuKey = Integer.parseInt( menuElem1.getAttribute( "key" ) );
                    Element rootElem = stepstateDoc.getDocumentElement();
                    Element menuElem = XMLTool.createElement( stepstateDoc, rootElem, "menu" );
                    menuElem.setAttribute( "key", String.valueOf( menuKey ) );

                    // framework
                    if ( formItems.containsKey( "contentframework_" + menuKey ) )
                    {
                        Element pagetemplateElem = XMLTool.createElement( stepstateDoc, menuElem, "pagetemplate" );
                        int pageTemplateKey = Integer.parseInt( formItems.getString( "contentframework_" + menuKey ) );
                        pagetemplateElem.setAttribute( "key", String.valueOf( pageTemplateKey ) );
                    }

                    String[] menuItemSelectedKeys = formItems.getStringArray( "menuitem_select_" + menuKey );
                    List<String> menuItemManuallyOrderKeys =
                        Arrays.asList( formItems.getStringArray( "menuitem_manually_order_" + menuKey ) );

                    for ( String menuItemSelectedKey : menuItemSelectedKeys )
                    {
                        Element menuitemElem = XMLTool.createElement( stepstateDoc, menuElem, "menuitem" );
                        menuitemElem.setAttribute( "key", menuItemSelectedKey );
                        menuitemElem.setAttribute( "publish", "true" );
                        menuitemElem.setAttribute( "manuallyOrder",
                                                   String.valueOf( menuItemManuallyOrderKeys.contains( menuItemSelectedKey ) ) );
                        MenuItemKey menuItemKey = new MenuItemKey( menuItemSelectedKey );
                        MenuItemKey sectionKey = admin.getSectionKeyByMenuItemKey( menuItemKey );
                        menuitemElem.setAttribute( "ordered", String.valueOf( admin.isSectionOrdered( sectionKey.toInt() ) ) );
                    }

                    // home
                    String homeKey = formItems.getString( "menuitem_home_" + menuKey, null );
                    if ( homeKey != null )
                    {
                        Element homeElem = XMLTool.createElement( stepstateDoc, menuElem, "home" );
                        homeElem.setAttribute( "key", homeKey );
                    }
                }
            }
        }

        // position content in section

        private void saveStateStep2( AdminService admin, Document stepstateDoc, ExtendedMap formItems )
        {
            Element rootElem = stepstateDoc.getDocumentElement();
            Element sectionElem = XMLTool.createElement( stepstateDoc, rootElem, "section" );
            MenuItemKey menuItemKey = new MenuItemKey( formItems.getString( "menuitemkey" ) );
            MenuItemKey sectionKey = admin.getSectionKeyByMenuItemKey( menuItemKey );
            sectionElem.setAttribute( "key", sectionKey.toString() );

            if ( formItems.containsKey( "contentidx" ) )
            {
                Element elem = XMLTool.createElement( stepstateDoc, rootElem, "contentidx" );
                elem.setAttribute( "value", formItems.getString( "contentidx", null ) );
            }

            String[] contentKeys = formItems.getStringArray( "content" );
            for ( String contentKey : contentKeys )
            {
                Element contentElem = XMLTool.createElement( stepstateDoc, sectionElem, "content" );
                contentElem.setAttribute( "key", contentKey );
            }
        }

        private boolean moreOrder( User user, WizardState wizardState, AdminService admin )
        {
            StepState stepState = wizardState.getCurrentStepState();
            NormalStep step = stepState.getStep();
            int sectionIndex = 0;
            while ( !"step1".equals( step.getName() ) )
            {
                stepState = stepState.getPreviousStepState();
                step = stepState.getStep();
                sectionIndex++;
            }

            Document stateDoc = stepState.getStateDoc();
            Element[] menuElems = XMLTool.getElements( stateDoc.getDocumentElement(), "menu" );

            int idx = 0;
            for ( Element menuElem : menuElems )
            {
                Element[] menuitemElems = XMLTool.getElements( menuElem, "menuitem" );
                for ( Element menuitemElem : menuitemElems )
                {
                    boolean manuallyOrder = Boolean.valueOf( menuitemElem.getAttribute( "manuallyOrder" ) );
                    boolean ordered = Boolean.valueOf( menuitemElem.getAttribute( "ordered" ) );
                    MenuItemKey menuItemKey = new MenuItemKey( Integer.parseInt( menuitemElem.getAttribute( "key" ) ) );
                    MenuItemAccessRight menuItemAccessRight = admin.getMenuItemAccessRight( user, menuItemKey );
                    if ( manuallyOrder && ordered && menuItemAccessRight.getPublish() )
                    {
                        if ( idx == sectionIndex )
                        {
                            return true;
                        }
                        else
                        {
                            idx++;
                        }
                    }
                }
            }
            return false;
        }

        private boolean noSites( WizardState wizardState )
        {
            Document stepstateDoc = wizardState.getCurrentStepState().getStateDoc();
            Element stepstateElem = stepstateDoc.getDocumentElement();
            Element[] menuElems = XMLTool.getElements( stepstateElem, "menu" );
            return menuElems.length == 0;
        }

        private Document createElementsToList( final TIntArrayList menuItemKeys )
        {
            final MenuItemXmlCreator creator = getMenuItemXmlCreator();

            final org.jdom.Document doc = new org.jdom.Document();
            final org.jdom.Element rootEl = new org.jdom.Element( "menus" );
            doc.setRootElement( rootEl );

            final Map<SiteKey, org.jdom.Element> siteElSiteKeyMap = new HashMap<SiteKey, org.jdom.Element>();

            for ( final Integer menuItemKey : menuItemKeys.toArray() )
            {
                final MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey );
                final SiteEntity site = menuItem.getSite();
                final SiteKey siteKey = site.getKey();

                final org.jdom.Element siteEl;
                if ( siteElSiteKeyMap.containsKey( siteKey ) )
                {
                    siteEl = siteElSiteKeyMap.get( siteKey );
                }
                else
                {
                    siteEl = createSiteElement( site );
                    siteElSiteKeyMap.put( siteKey, siteEl );
                    rootEl.addContent( siteEl );
                }

                siteEl.addContent( creator.createMenuItemElement( menuItem ) );
            }

            return XMLDocumentFactory.create( doc ).getAsDOMDocument();
        }

        private Document createElementsToList( final List<MenuItemEntity> menuItems )
        {
            final MenuItemXmlCreator creator = getMenuItemXmlCreator();

            final org.jdom.Document doc = new org.jdom.Document();
            final org.jdom.Element rootEl = new org.jdom.Element( "menus" );
            doc.setRootElement( rootEl );

            final Map<SiteKey, org.jdom.Element> siteElSiteKeyMap = new HashMap<SiteKey, org.jdom.Element>();

            for ( final MenuItemEntity menuItem : menuItems )
            {
                final SiteEntity site = menuItem.getSite();
                final SiteKey siteKey = site.getKey();

                final org.jdom.Element siteEl;
                if ( siteElSiteKeyMap.containsKey( siteKey ) )
                {
                    siteEl = siteElSiteKeyMap.get( siteKey );
                }
                else
                {
                    siteEl = createSiteElement( site );
                    siteElSiteKeyMap.put( siteKey, siteEl );
                    rootEl.addContent( siteEl );
                }

                siteEl.addContent( creator.createMenuItemElement( menuItem ) );
            }

            return XMLDocumentFactory.create( doc ).getAsDOMDocument();
        }


        private MenuItemXmlCreator getMenuItemXmlCreator()
        {
            final MenuItemXMLCreatorSetting setting = new MenuItemXMLCreatorSetting();
            setting.includeTypeSpecificXML = false;
            final MenuItemXmlCreator creator = new MenuItemXmlCreator( setting, null );
            creator.setIncludePathInfo( true );
            return creator;
        }

        private org.jdom.Element createSiteElement( final SiteEntity site )
        {
            final org.jdom.Element siteEl = new org.jdom.Element( "menu" );
            siteEl.setAttribute( "key", site.getKey().toString() );
            siteEl.setAttribute( "name", site.getName() );
            return siteEl;
        }

        private List<MenuItemEntity> getAccessibleMenuItems( final User oldUser, final int[] menuKeys )
        {
            final List<MenuItemEntity> menuItemsOfTypeSection = new ArrayList<MenuItemEntity>();

            for ( final int menuKey : menuKeys )
            {
                /* Sections */
                final MenuItemSpecification specSection = new MenuItemSpecification();
                specSection.setSiteKey( new SiteKey( menuKey ) );
                specSection.setType( MenuItemType.SECTION );
                menuItemsOfTypeSection.addAll( menuItemDao.findBySpecification( specSection ) );

                /* Page Section */
                final MenuItemSpecification specSectionPage = new MenuItemSpecification();
                specSectionPage.setSiteKey( new SiteKey( menuKey ) );
                specSectionPage.setPageSpecification( new PageSpecification() );
                specSectionPage.getPageSpecification().setTemplateSpecification( new PageTemplateSpecification() );
                specSectionPage.getPageSpecification().getTemplateSpecification().setType( PageTemplateType.SECTIONPAGE );
                menuItemsOfTypeSection.addAll( menuItemDao.findBySpecification( specSectionPage ) );

                /* Newsletter Section */
                final MenuItemSpecification newsletterSectionSpec = new MenuItemSpecification();
                newsletterSectionSpec.setSiteKey( new SiteKey( menuKey ) );
                newsletterSectionSpec.setPageSpecification( new PageSpecification() );
                newsletterSectionSpec.getPageSpecification().setTemplateSpecification( new PageTemplateSpecification() );
                newsletterSectionSpec.getPageSpecification().getTemplateSpecification().setType( PageTemplateType.NEWSLETTER );
                menuItemsOfTypeSection.addAll( menuItemDao.findBySpecification( newsletterSectionSpec ) );
            }

            final UserEntity newUser = securityService.getUser( oldUser );
            final List<MenuItemEntity> accessibleMenuItems = new ArrayList<MenuItemEntity>();
            MenuItemAccessResolver menuItemAccessResolver =
                new MenuItemAccessResolver( groupDao );
            for ( final MenuItemEntity menuItem : menuItemsOfTypeSection )
            {
                if ( menuItemAccessResolver.hasAccess( newUser, menuItem, MenuItemAccessType.PUBLISH ) ||
                    menuItemAccessResolver.hasAccess( newUser, menuItem, MenuItemAccessType.ADD ) )
                {
                    accessibleMenuItems.add( menuItem );
                }
            }
            return accessibleMenuItems;
        }

        private TIntArrayList getSelectedMenuItemKeys( final Document stateDoc )
        {
            final TIntArrayList menuItemKeys = new TIntArrayList();
            final Element[] menuEls = XMLTool.getElements( stateDoc.getDocumentElement(), "menu" );
            for ( int j = 0; j < menuEls.length; j++ )
            {
                final Element[] menuItemEls = XMLTool.getElements( menuEls[j], "menuitem" );
                for ( int i = 0; i < menuItemEls.length; i++ )
                {
                    final int menuItemKey = Integer.parseInt( menuItemEls[i].getAttribute( "key" ) );
                    menuItemKeys.add( menuItemKey );
                }
            }
            return menuItemKeys;
        }

        private TIntArrayList getPreviousSelectedMenuItemKeys( final Document sectionDoc )
        {
            final TIntArrayList menuItemKeys = new TIntArrayList();
            final Element[] sectionEls = XMLTool.getElements( sectionDoc.getDocumentElement(), "section" );
            for ( int i = 0; i < sectionEls.length; i++ )
            {
                final boolean filtered = Boolean.parseBoolean( sectionEls[i].getAttribute( "filtered" ) );
                if ( filtered )
                {
                    final int menuItemKey = Integer.parseInt( sectionEls[i].getAttribute( "menuitemkey" ) );
                    menuItemKeys.add( menuItemKey );
                }
            }
            return menuItemKeys;
        }
    }

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation )
        throws VerticalAdminException, VerticalEngineException
    {

        if ( "moveup".equals( operation ) )
        {
            moveContentUp( request, response, session, formItems );
        }
        else if ( "movedown".equals( operation ) )
        {
            moveContentDown( request, response, session, formItems );
        }
        else if ( "approve".equals( operation ) )
        {
            approveContent( request, response, session, admin, formItems );
        }
        else if ( "unapprove".equals( operation ) )
        {
            unapproveContent( request, response, session, admin, formItems );
        }
        else if ( "add".equals( operation ) )
        {
            addContent( request, response, admin, formItems );
        }
        else if ( "save".equals( operation ) )
        {
            saveContents( request, response, session, formItems );
        }
        else if ( "removecontent".equals( operation ) )
        {
            removeContent( request, response, session, formItems );
        }
        else if ( "batchremove".equals( operation ) )
        {
            batchRemove( request, response, session, formItems );
        }
        else if ( "batchactivate".equals( operation ) )
        {
            batchActivate( request, response, session, admin, formItems, true );
        }
        else if ( "batchdeactivate".equals( operation ) )
        {
            batchActivate( request, response, session, admin, formItems, false );
        }
        else
        {
            super.handlerCustom( request, response, session, admin, formItems, operation );
        }
    }

    public void batchRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        MenuItemKey sectionKey = new MenuItemKey( formItems.getString( "menuitemkey" ) );
        MenuItemEntity section = menuItemDao.findByKey( sectionKey );
        assert section.isSection();

        String[] contentKeyStrings = formItems.getStringArray( "batch_operation" );
        Set<ContentKey> contentKeys = new HashSet<ContentKey>();
        if ( contentKeyStrings != null && contentKeyStrings.length > 0 )
        {
            for ( String contentKeyString : contentKeyStrings )
            {
                contentKeys.add( new ContentKey( contentKeyString ) );
            }
        }

        boolean topLevel = "true".equals( formItems.getString( "toplevel", "" ) );
        boolean reordered = "true".equals( formItems.getString( "reordered", "" ) );
        boolean ordered = false;

        RemoveContentFromSectionCommand command = new RemoveContentFromSectionCommand();
        command.setRemover( user.getKey() );
        command.setSection( sectionKey );
        for ( ContentKey contentKey : contentKeys )
        {
            command.addContentToRemove( contentKey );
        }

        if ( topLevel )
        {
            menuItemService.removeContentFromSection( command );
        }
        else
        {
            Document doc = XMLTool.domparse( (String) session.getAttribute( "sectionxml" ) );
            Element sectionElem = XMLTool.getElement( doc.getDocumentElement(), "section" );
            ordered = Boolean.valueOf( sectionElem.getAttribute( "ordered" ) );

            // Set @removed=false
            Element[] contentTitleElems = XMLTool.getElements( doc.getDocumentElement(), "contenttitle" );
            for ( Element contentTitleElem : contentTitleElems )
            {
                ContentKey key = new ContentKey( Integer.parseInt( contentTitleElem.getAttribute( "key" ) ) );
                if ( contentKeys.contains( key ) )
                {
                    contentTitleElem.setAttribute( "removed", "true" );
                }
            }

            if ( !( ordered && reordered ) )
            {
                menuItemService.removeContentFromSection( command );
            }
            session.setAttribute( "sectionxml", XMLTool.documentToString( doc ) );
        }
        int siteKey = formItems.getInt( "menukey", -1 );
        if ( siteKey == -1 )
        {
            siteKey = formItems.getInt( "sitekey", -1 );
            if ( siteKey == -1 )
            {
                siteKey = section.getSite().getKey().toInt();
            }
        }

        invalidatePageCache( sectionKey );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        queryParams.put( "menukey", siteKey );
        queryParams.put( "sitekey", siteKey );
        queryParams.put( "menuitemkey", sectionKey.toInt() );
        if ( !topLevel )
        {
            queryParams.put( "sec", sectionKey );
        }
        if ( ordered && reordered )
        {
            queryParams.put( "keepxml", "yes" );
            queryParams.put( "reordered", "true" );
        }
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    public void batchActivate( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, boolean approved )
        throws VerticalAdminException, VerticalEngineException
    {

        MenuItemKey menuItemKey = new MenuItemKey( formItems.getString( "menuitemkey" ) );
        String[] contentKeyStrings = formItems.getStringArray( "batch_operation" );
        int[] contentKeys = new int[contentKeyStrings.length];
        for ( int i = 0; i < contentKeyStrings.length; i++ )
        {
            contentKeys[i] = Integer.parseInt( contentKeyStrings[i] );
        }
        TIntHashSet contentKeysSet = new TIntHashSet();
        contentKeysSet.add( contentKeys );

        User user = securityService.getLoggedInAdminConsoleUser();
        Document doc = XMLTool.domparse( (String) session.getAttribute( "sectionxml" ) );
        Element sectionElem = XMLTool.getElement( doc.getDocumentElement(), "section" );
        boolean ordered = Boolean.valueOf( sectionElem.getAttribute( "ordered" ) );
        int sectionKey = Integer.parseInt( formItems.getString( "sec" ) );

        // Set approved attribute
        Element[] contentTitleElems = XMLTool.getElements( doc.getDocumentElement(), "contenttitle" );
        for ( int i = 0; i < contentTitleElems.length; i++ )
        {
            int key = Integer.parseInt( contentTitleElems[i].getAttribute( "key" ) );
            if ( contentKeysSet.contains( key ) )
            {
                contentTitleElems[i].setAttribute( "approved", String.valueOf( approved ) );

                if ( ordered )
                {
                    if ( approved )
                    {
                        // Move the element to the top in the xml
                        Element parent = (Element) contentTitleElems[i].getParentNode();
                        contentTitleElems[i] = (Element) parent.removeChild( contentTitleElems[i] );
                        doc.importNode( contentTitleElems[i], true );
                        parent.insertBefore( contentTitleElems[i], parent.getFirstChild() );
                    }
                    else
                    {
                        NodeList unapprovedContents =
                            XMLTool.selectNodes( doc, "/contenttitles/contenttitle[@approved = 'false' and not(@removed = 'true')]" );

                        Element parent = doc.getDocumentElement();
                        String title = XMLTool.getElementText( contentTitleElems[i] );

                        Element next = null;
                        for ( int j = 0; j < unapprovedContents.getLength(); j++ )
                        {
                            Element current = (Element) unapprovedContents.item( j );
                            if ( XMLTool.getElementText( current ).compareTo( title ) > 0 )
                            {
                                next = current;
                                break;
                            }
                        }

                        if ( next != null )
                        {
                            parent.insertBefore( contentTitleElems[i], next );
                        }
                        else
                        {
                            parent.insertBefore( contentTitleElems[i],
                                                 unapprovedContents.item( unapprovedContents.getLength() - 1 ).getNextSibling() );
                        }
                    }
                }
            }
        }

        if ( !ordered )
        {
            admin.setSectionContentsApproved( user, sectionKey, contentKeys, approved );
        }

        invalidatePageCache( menuItemKey );

        session.setAttribute( "sectionxml", XMLTool.documentToString( doc ) );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        queryParams.put( "sec", formItems.get( "sec" ) );
        queryParams.put( "menukey", formItems.get( "menukey" ) );
        queryParams.put( "menuitemkey", menuItemKey.toInt() );
        if ( ordered )
        {
            queryParams.put( "keepxml", "yes" );
            queryParams.put( "reordered", "true" );
        }
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    public void addContent( HttpServletRequest request, HttpServletResponse response, AdminService admin, ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        MenuItemKey sectionKey = new MenuItemKey( formItems.getInt( "sec", -1 ) );
        String[] contentKeys = formItems.getStringArray( "key" );

        for ( String contentKeyStr : contentKeys )
        {
            int contentKey = Integer.valueOf( contentKeyStr );
            Document doc = XMLTool.createDocument( "sections" );
            Element sectionsElem = doc.getDocumentElement();
            Element sectionElem = XMLTool.createElement( doc, doc.getDocumentElement(), "section" );
            sectionElem.setAttribute( "key", sectionKey.toString() );
            sectionsElem.setAttribute( "contentkey", Integer.toString( contentKey ) );
            admin.addContentToSections( user, XMLTool.documentToString( doc ) );
        }

        MultiValueMap parameters = new MultiValueMap();
        parameters.put( "page", formItems.get( "page" ) );
        parameters.put( "op", "browse" );
        parameters.put( "sec", sectionKey.toString() );
        parameters.put( "menukey", formItems.get( "menukey" ) );
        parameters.put( "menuitemkey", admin.getMenuItemKeyBySection( sectionKey ).toString() );

        redirectClientToAdminPath( "adminpage", parameters, request, response );
    }

    public void handlerForm( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        URL url = new URL( request.getHeader( "referer" ) );
        url.setParameter( "reload", "true" );
        formItems.put( "redirect", url.toString() );

        ExtendedMap parameters = new ExtendedMap();
        User user = securityService.getLoggedInAdminConsoleUser();

        Wizard createUpdateWizard = Wizard.getInstance( admin, applicationContext, this, session, formItems, WIZARD_CONFIG_CREATE_UPDATE );
        createUpdateWizard.processRequest( request, response, session, admin, formItems, parameters, user );
    }

    public void handlerWizard( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, ExtendedMap parameters, User user, String wizardName )
        throws VerticalAdminException, VerticalEngineException, TransformerException, IOException
    {
        if ( "addcontent".equals( wizardName ) )
        {
            Wizard addContentWizard = Wizard.getInstance( admin, applicationContext, this, session, formItems, WIZARD_CONFIG_ADD_CONTENT );
            addContentWizard.processRequest( request, response, session, admin, formItems, parameters, user );
        }
        else if ( "publish".equals( wizardName ) )
        {
            Wizard publishWizard = Wizard.getInstance( admin, applicationContext, this, session, formItems, WIZARD_CONFIG_PUBLISH );
            publishWizard.processRequest( request, response, session, admin, formItems, parameters, user );
        }
        else
        {
            super.handlerWizard( request, response, session, admin, formItems, parameters, user, wizardName );
        }
    }

    public void handlerBrowse( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems )
        throws VerticalAdminException
    {

        Document doc;
        User user = securityService.getLoggedInAdminConsoleUser();
        // int sectionKey = formItems.getInt("sec", -1);
        int menuKey = formItems.getInt( "menukey" );
        MenuItemKey menuItemKey = new MenuItemKey( formItems.getString( "menuitemkey" ) );
        MenuItemKey sectionKey = admin.getSectionKeyByMenuItemKey( menuItemKey );

        String keepXML = request.getParameter( "keepxml" );

        if ( formItems.containsKey( "browsemode" ) )
        {
            String deploymentPath = DeploymentPathResolver.getAdminDeploymentPath( request );
            CookieUtil.setCookie( response, "browsemode" + menuItemKey, formItems.getString( "browsemode" ), -1, deploymentPath );
        }
        else
        {
            Cookie c = CookieUtil.getCookie( request, "browsemode" + menuItemKey );
            if ( c != null && "menuitem".equals( c.getValue() ) )
            {
                // int sectionKey = admin.getSectionKeyByMenuItemKey(menuItemKey);
                // redirectToSectionBrowse(request, response, siteKey, menuKey, sectionKey,
                // formItems.getBoolean("reload", false));
//                ( "SwitchMode section" );
            }
        }

        final String cookieName = "sectionBrowseItemsPerPage";
        int index = formItems.getInt( "index", 0 );
        int count = ListCountResolver.resolveCount( request, formItems, cookieName );
        CookieUtil.setCookie( response, cookieName, Integer.toString( count ), COOKIE_TIMEOUT,
                              DeploymentPathResolver.getAdminDeploymentPath( request ) );

        if ( keepXML != null && keepXML.equals( "yes" ) )
        {
            doc = XMLTool.domparse( (String) session.getAttribute( "sectionxml" ) );
        }
        else
        {
            doc = getSectionDocument( admin, user, index, count, sectionKey, menuKey, menuItemKey );
            addPageTemplatesOfSiteToDocument( menuKey, PageTemplateType.CONTENT, doc );
            session.setAttribute( "sectionxml", XMLTool.documentToString( doc ) );
        }

        try
        {
            VerticalAdminLogger.debug( this.getClass(), 0, doc );
            DOMSource xmlSource = new DOMSource( doc );

            Source xslSource = AdminStore.getStylesheet( session, "section_browse.xsl" );

            // Parameters
            HashMap<String, Object> parameters = formItems;
            parameters.put( "index", index );
            parameters.put( "count", count );
            parameters.put( "menuitemkey", menuItemKey.toString() );
            parameters.put( "sec", sectionKey.toString() );
            parameters.put( "debugpath", MenuHandlerServlet.getSiteUrl( request, menuKey ) );
            addCommonParameters( admin, user, request, parameters, -1, menuKey );
            transformXML( session, response.getWriter(), xmlSource, xslSource, parameters );
        }

        catch ( IOException ioe )
        {
            String message = "Failed to get response writer: %t";
            VerticalAdminLogger.errorAdmin( this.getClass(), 0, message, ioe );
        }
        catch ( TransformerException te )
        {
            String message = "Failed to transmform XML document: %t";
            VerticalAdminLogger.errorAdmin( this.getClass(), 0, message, te );
        }
    }

    private void moveContentUp( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems )
        throws VerticalAdminException
    {

        Document doc = XMLTool.domparse( (String) session.getAttribute( "sectionxml" ) );
        Element parent = doc.getDocumentElement();
        String key = formItems.getString( "key" );

        NodeList approvedContents =
            XMLTool.selectNodes( doc, "/contenttitles/contenttitle[@approved = 'true' and not(@removed = 'true')]" );

        // elem is the node we are going to move
        Element elem = null;
        // next is the element that 'elem' should be inserted before
        Element next = null;
        for ( int i = approvedContents.getLength() - 1; i >= 0; i-- )
        {
            Element current = (Element) approvedContents.item( i );

            // If we have not found the node we are going to move
            if ( elem == null )
            {
                if ( key.equals( current.getAttribute( "key" ) ) )
                {
                    elem = current;
                }
            }
            // If we have found the node, it should be inserted before the current
            else
            {
                next = current;
                break;
            }
        }
        if ( next == null )
        {
            // The element should be inserted last (wrapping)
            parent.insertBefore( elem, approvedContents.item( approvedContents.getLength() - 1 ).getNextSibling() );
        }
        else
        {
            parent.insertBefore( elem, next );
        }

        session.setAttribute( "sectionxml", XMLTool.documentToString( doc ) );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        queryParams.put( "sec", formItems.get( "sec" ) );
        queryParams.put( "keepxml", "yes" );
        queryParams.put( "reordered", "true" );
        queryParams.put( "menukey", formItems.get( "menukey" ) );
        queryParams.put( "menuitemkey", formItems.getString( "menuitemkey" ) );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    private void moveContentDown( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems )
        throws VerticalAdminException
    {

        Document doc = XMLTool.domparse( (String) session.getAttribute( "sectionxml" ) );
        Element parent = doc.getDocumentElement();
        String key = formItems.getString( "key" );

        NodeList approvedContents =
            XMLTool.selectNodes( doc, "/contenttitles/contenttitle[@approved = 'true' and not(@removed = 'true')]" );

        boolean insertFirst = false;
        // elem is the node we are going to move
        Element elem = null;
        // next is the element that 'elem' should be inserted before
        Element next = null;
        for ( int i = 0; i < approvedContents.getLength(); i++ )
        {
            Element current = (Element) approvedContents.item( i );

            // If we have not found the node we are going to move
            if ( elem == null )
            {
                if ( key.equals( current.getAttribute( "key" ) ) )
                {
                    elem = current;
                    if ( i == approvedContents.getLength() - 1 )
                    {
                        // If this is the last element in the list, it should be inserted first
                        insertFirst = true;
                    }
                }
            }
            // If we have found the node, take the next element
            else
            {
                next = (Element) current.getNextSibling();
                break;
            }
        }
        if ( insertFirst )
        {
            // wrapping
            parent.insertBefore( elem, approvedContents.item( 0 ) );
        }
        else
        {
            // Next can be null, but then elem is inserted last (which is correct)
            parent.insertBefore( elem, next );
        }

        session.setAttribute( "sectionxml", XMLTool.documentToString( doc ) );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        queryParams.put( "sec", formItems.get( "sec" ) );
        queryParams.put( "keepxml", "yes" );
        queryParams.put( "reordered", "true" );
        queryParams.put( "menukey", formItems.get( "menukey" ) );
        queryParams.put( "menuitemkey", formItems.getString( "menuitemkey" ) );
        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    public void approveContent( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                ExtendedMap formItems )
        throws VerticalAdminException, VerticalUpdateException, VerticalSecurityException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        MenuItemKey sectionKey = new MenuItemKey( formItems.getString( "sec" ) );
        int contentKey = formItems.getInt( "key" );
        int menuKey = formItems.getInt( "menukey", -1 );
        if ( menuKey == -1 )
        {
            menuKey = admin.getMenuKeyBySection( sectionKey );
        }

        boolean topLevel = "true".equals( formItems.getString( "toplevel", "" ) );
        Document doc;
        MenuItemKey menuItemKey = admin.getMenuItemKeyBySection( sectionKey );

        if ( topLevel )
        {
            doc = getSectionDocument( admin, user, 0, 20, sectionKey, menuKey, menuItemKey );
        }
        else
        {
            doc = XMLTool.domparse( (String) session.getAttribute( "sectionxml" ) );
        }

        Element sectionElem = XMLTool.getElement( doc.getDocumentElement(), "section" );
        boolean ordered = Boolean.valueOf( sectionElem.getAttribute( "ordered" ) );

        // Set approved attribute = true
        String xpath = "/contenttitles/contenttitle[@key = '" + contentKey + "']";
        Element elem = (Element) XMLTool.selectNode( doc, xpath );
        elem.setAttribute( "approved", "true" );
        elem.setAttribute( "modified", "true" );

        if ( ordered )
        {
            // Move the element to the top in the xml
            Element parent = (Element) elem.getParentNode();
            elem = (Element) parent.removeChild( elem );
            doc.importNode( elem, true );
            parent.insertBefore( elem, parent.getFirstChild() );
        }
        else
        {
            admin.updateSectionContent( user, sectionKey, contentKey, 0, true );
        }
        session.setAttribute( "sectionxml", XMLTool.documentToString( doc ) );

        invalidatePageCache( menuItemKey );

        String useRedirect = formItems.getString( "useredirect", null );
        if ( "referer".equals( useRedirect ) && !ordered )
        {
            redirectClientToReferer( request, response );
        }
        else
        {
            MultiValueMap queryParams = new MultiValueMap();
            queryParams.put( "page", formItems.get( "page" ) );
            queryParams.put( "op", "browse" );
            queryParams.put( "menukey", formItems.get( "menukey", null ) );
            queryParams.put( "menuitemkey", menuItemKey.toString() );
            if ( !topLevel || ordered )
            {
                queryParams.put( "sec", sectionKey );
            }
            if ( ordered )
            {
                queryParams.put( "keepxml", "yes" );
                queryParams.put( "reordered", "true" );
            }
            redirectClientToAdminPath( "adminpage", queryParams, request, response );
        }
    }

    public void unapproveContent( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                  ExtendedMap formItems )
        throws VerticalAdminException, VerticalUpdateException, VerticalSecurityException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        Document doc = XMLTool.domparse( (String) session.getAttribute( "sectionxml" ) );
        Element sectionElem = XMLTool.getElement( doc.getDocumentElement(), "section" );
        boolean ordered = Boolean.valueOf( sectionElem.getAttribute( "ordered" ) );
        MenuItemKey sectionKey = new MenuItemKey( formItems.getString( "sec" ) );
        int contentKey = Integer.parseInt( formItems.getString( "key" ) );
        MenuItemKey menuItemKey = admin.getMenuItemKeyBySection( sectionKey );

        // Set approved attribute = false
        String xpath = "/contenttitles/contenttitle[@key = '" + contentKey + "']";
        Element elem = (Element) XMLTool.selectNode( doc, xpath );
        elem.setAttribute( "approved", "false" );
        elem.setAttribute( "modified", "true" );

        if ( ordered )
        {
            NodeList unapprovedContents =
                XMLTool.selectNodes( doc, "/contenttitles/contenttitle[@approved = 'false' and not(@removed = 'true')]" );

            Element parent = doc.getDocumentElement();
            String title = XMLTool.getElementText( elem );

            Element next = null;
            for ( int i = 0; i < unapprovedContents.getLength(); i++ )
            {
                Element current = (Element) unapprovedContents.item( i );
                if ( XMLTool.getElementText( current ).compareTo( title ) > 0 )
                {
                    next = current;
                    break;
                }
            }

            if ( next != null )
            {
                parent.insertBefore( elem, next );
            }
            else
            {
                parent.insertBefore( elem, unapprovedContents.item( unapprovedContents.getLength() - 1 ).getNextSibling() );
            }
        }
        else
        {
            admin.updateSectionContent( user, sectionKey, contentKey, 0, false );
        }

        session.setAttribute( "sectionxml", XMLTool.documentToString( doc ) );

        invalidatePageCache( menuItemKey );

        String useRedirect = formItems.getString( "useredirect", null );
        if ( "referer".equals( useRedirect ) && !ordered )
        {
            redirectClientToReferer( request, response );
        }
        else
        {
            MultiValueMap queryParams = new MultiValueMap();
            queryParams.put( "page", formItems.get( "page" ) );
            queryParams.put( "op", "browse" );
            queryParams.put( "sec", formItems.get( "sec" ) );
            queryParams.put( "menukey", formItems.get( "menukey" ) );
            queryParams.put( "menuitemkey", menuItemKey.toString() );
            if ( ordered )
            {
                queryParams.put( "keepxml", "yes" );
                queryParams.put( "reordered", "true" );
            }
            redirectClientToAdminPath( "adminpage", queryParams, request, response );
        }
    }

    public void saveContents( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems )
        throws VerticalAdminException, VerticalUpdateException, VerticalRemoveException, VerticalSecurityException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        Document doc = XMLTool.domparse( (String) session.getAttribute( "sectionxml" ) );
        MenuItemKey menuItemKey = new MenuItemKey( Integer.parseInt( formItems.getString( "sec" ) ) );
        MenuItemEntity section = menuItemDao.findByKey( menuItemKey );
        assert ( section.isSection() );

        long pageLoadedTimestamp = Long.parseLong( formItems.getString( "timestamp" ) );

        RemoveContentFromSectionCommand removeCommand = new RemoveContentFromSectionCommand();
        UnapproveSectionContentCommand updateUnapprovedCommand = new UnapproveSectionContentCommand();
        ApproveSectionContentCommand updateApprovedCommand = new ApproveSectionContentCommand();

        Element[] contents = XMLTool.getElements( doc.getDocumentElement(), "contenttitle" );
        for ( Element content : contents )
        {
            int contentKey = Integer.parseInt( content.getAttribute( "key" ) );
            if ( "true".equals( content.getAttribute( "removed" ) ) )
            {
                removeCommand.addContentToRemove( new ContentKey( contentKey ) );
            }
            else if ( "true".equals( content.getAttribute( "approved" ) ) )
            {
                //approvedContents.add( contentKey );
                updateApprovedCommand.addApprovedContentToUpdate( new ContentKey( contentKey ) );
            }
            else
            {
                //unapprovedContents.add( contentKey );
                updateUnapprovedCommand.addUnapprovedContentToUpdate( new ContentKey( contentKey ) );
            }
        }

        if ( section.getLastUpdatedSectionContentTimestamp().after( new Date( pageLoadedTimestamp ) ) )
        {
            throw new IllegalStateException( "Content in this section has been changed. Please reload and try again." );
        }

        if ( removeCommand.getContentToRemove().size() > 0 )
        {
            removeCommand.setRemover( user.getKey() );
            removeCommand.setSection( section.getMenuItemKey() );
            Integer numberOfRemovedContentFromSection = menuItemService.removeContentFromSection( removeCommand );

            assert removeCommand.getContentToRemove().size() ==
                numberOfRemovedContentFromSection : "Not all the content that should have been removed were removed.";
        }

        if ( updateApprovedCommand.getApprovedContentToUpdate().size() > 0 )
        {
            updateApprovedCommand.setUpdater( user.getKey() );
            updateApprovedCommand.setSection( section.getMenuItemKey() );
            Integer approvedContentInSection = menuItemService.approveSectionContent( updateApprovedCommand );

            assert updateApprovedCommand.getApprovedContentToUpdate().size() ==
                approvedContentInSection : "Incorrect number of approved content in secton after database update";
        }

        if ( updateUnapprovedCommand.getUnapprovedContentToUpdate().size() > 0 )
        {
            updateUnapprovedCommand.setUpdater( user.getKey() );
            updateUnapprovedCommand.setSection( section.getMenuItemKey() );
            Integer unapprovedContentInSection = menuItemService.unapproveSectionContent( updateUnapprovedCommand );

            assert updateUnapprovedCommand.getUnapprovedContentToUpdate().size() ==
                unapprovedContentInSection : "Incorrect number of unapproved content in section after database update";
        }

        //admin.updateSectionContents( user, menuItemKey.toInt(), approvedContents.toNativeArray(), unapprovedContents.toNativeArray(),
        //                             new int[]{}, pageLoadedTimestamp );

        invalidatePageCache( menuItemKey );

        MultiValueMap queryParams = new MultiValueMap();
        queryParams.put( "page", formItems.get( "page" ) );
        queryParams.put( "op", "browse" );
        queryParams.put( "sec", formItems.get( "sec" ) );
        queryParams.put( "menukey", formItems.get( "menukey" ) );
        queryParams.put( "menuitemkey", menuItemKey.toString() );

        redirectClientToAdminPath( "adminpage", queryParams, request, response );
    }

    public void removeContent( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems )
        throws VerticalAdminException, VerticalRemoveException, VerticalSecurityException
    {

        User user = securityService.getLoggedInAdminConsoleUser();
        MenuItemKey sectionKey = new MenuItemKey( Integer.parseInt( formItems.getString( "sec" ) ) );
        MenuItemKey menuItemKey = new MenuItemKey( formItems.getString( "menuitemkey" ) );
        ContentKey contentKey = new ContentKey( Integer.parseInt( formItems.getString( "key" ) ) );
        boolean topLevel = "true".equals( formItems.getString( "toplevel", "" ) );
        boolean reordered = "true".equals( formItems.getString( "reordered", "" ) );
        boolean ordered = false;

        RemoveContentFromSectionCommand command = new RemoveContentFromSectionCommand();
        command.setRemover( user.getKey() );
        command.setSection( sectionKey );
        command.addContentToRemove( contentKey );

        if ( topLevel )
        {
            menuItemService.removeContentFromSection( command );
//            admin.removeContentFromSection( user, sectionKey, contentKey );
        }
        else
        {
            Document doc = XMLTool.domparse( (String) session.getAttribute( "sectionxml" ) );
            Element sectionElem = XMLTool.getElement( doc.getDocumentElement(), "section" );
            ordered = Boolean.valueOf( sectionElem.getAttribute( "ordered" ) );

            // Set approved attribute = false
            String xpath = "/contenttitles/contenttitle[@key = '" + contentKey + "']";
            Element elem = (Element) XMLTool.selectNode( doc, xpath );
            elem.setAttribute( "removed", "true" );

            if ( !( ordered && reordered ) )
            {
                menuItemService.removeContentFromSection( command );
                //admin.removeContentFromSection( user, sectionKey, contentKey );
            }
            session.setAttribute( "sectionxml", XMLTool.documentToString( doc ) );
        }
        int siteKey = formItems.getInt( "menukey", -1 );
        if ( siteKey == -1 )
        {
            siteKey = menuItemDao.findByKey( menuItemKey ).getSite().getKey().toInt();
        }

        invalidatePageCache( menuItemKey );

        String useRedirect = formItems.getString( "useredirect", null );
        if ( "referer".equals( useRedirect ) && !( ordered && reordered ) )
        {
            redirectClientToReferer( request, response );
        }
        else
        {
            MultiValueMap queryParams = new MultiValueMap();
            queryParams.put( "page", formItems.get( "page" ) );
            queryParams.put( "op", "browse" );
            queryParams.put( "menukey", siteKey );
            queryParams.put( "menuitemkey", menuItemKey.toString() );
            if ( !topLevel )
            {
                queryParams.put( "sec", sectionKey );
            }
            if ( ordered && reordered )
            {
                queryParams.put( "keepxml", "yes" );
                queryParams.put( "reordered", "true" );
            }
            redirectClientToAdminPath( "adminpage", queryParams, request, response );
        }
    }

    private Document getSectionDocument( AdminService admin, User user, int index, int count, MenuItemKey sectionKey, int menuKey,
                                         MenuItemKey menuItemKey )
        throws VerticalAdminException
    {

        Document doc;
        SectionCriteria section = new SectionCriteria();
        section.setSectionKey( sectionKey.toInt() );
        section.setAppendAccessRights( true );
        section.setIncludeChildCount( true );
        section.setIncludeAll( true );

        Document sectionDoc = XMLTool.domparse( admin.getSections( user, section ) );
        Element sectionElem = XMLTool.getElement( sectionDoc.getDocumentElement(), "section" );
        long timestamp = admin.getSectionContentTimestamp( sectionKey );
        sectionElem.setAttribute( "timestamp", Long.toString( timestamp ) );
        boolean ordered = Boolean.valueOf( sectionElem.getAttribute( "ordered" ) );
        // boolean parentAdmin = false;

        // get administrate access right on parent
        // String parentKeyStr = sectionElem.getAttribute("supersectionkey");
        // if (parentKeyStr != null && parentKeyStr.length() > 0) {
        // //int parentKey = Integer.parseInt(parentKeyStr);
        // //int parentMenuItemKey = admin.getMenuItemKeyBySection(parentKey);
        // //MenuItemAccessRight parentAccessRights = admin.getMenuItemAccessRight(user, parentMenuItemKey);
        // //parentAdmin = parentAccessRights.getAdministrate();
        // }
        // else {
        // // Get default access rights on menu
        // //MenuAccessRight parentAccessRights = admin.getMenuAccessRight(user, menuKey);
        // //parentAdmin = parentAccessRights.getAdministrate();
        // }

        if ( ordered )
        {
            doc = admin.getContentTitlesBySection( sectionKey, null, 0, Integer.MAX_VALUE, false, false ).getAsDOMDocument();
        }
        else
        {
            doc = admin.getContentTitlesBySection( sectionKey, null, index, count, true, false ).getAsDOMDocument();
        }

        // Get contenttypes and categories
        Element[] contentTitleElems = XMLTool.getElements( doc.getDocumentElement() );
        TIntHashSet contentTypeKeys = new TIntHashSet();
        List<Integer> categoryKeys = new ArrayList<Integer>();
        for ( Element contentTitleElem : contentTitleElems )
        {
            contentTypeKeys.add( Integer.parseInt( contentTitleElem.getAttribute( "contenttypekey" ) ) );
            categoryKeys.add( Integer.parseInt( contentTitleElem.getAttribute( "categorykey" ) ) );
        }
        Document contentTypeDoc = XMLTool.domparse( admin.getData( user, Types.CONTENTTYPE, contentTypeKeys.toArray() ) );
        XMLTool.mergeDocuments( doc, contentTypeDoc, true );
        if ( categoryKeys.size() > 0 )
        {
            CategoryCriteria categoryCriteria = new CategoryCriteria();
            categoryCriteria.addCategoryKeys( categoryKeys );
            Document categoriesDoc = admin.getMenu( user, categoryCriteria ).getAsDOMDocument();
            XMLTool.mergeDocuments( doc, categoriesDoc, false );
        }

        /*
         * // Default browse config Document defaultBrowseConfig = XMLTool.domparse(AdminStore.getXML(session,
         * "defaultbrowseconfig.xml")); XMLTool.mergeDocuments(doc, defaultBrowseConfig, true);
         */

        // Document accessRightsDoc =
        // XMLTool.domparse(admin.getAccessRights(user, AccessRight.SECTION, sectionKey, true));
        // set a parent administrate attribute
        /*
         * Element userRightElem = XMLTool.getElement(accessRightsDoc.getDocumentElement(), "userright"); if
         * (userRightElem != null) { userRightElem.setAttribute("parentadministrate", String.valueOf(parentAdmin)); }
         * doc.getDocumentElement().appendChild(doc.importNode(accessRightsDoc.getDocumentElement(), true));
         */

        // Import the sections to the content doc
        doc.getDocumentElement().appendChild( doc.importNode( sectionElem, true ) );
        // Document sectionNamesDoc = XMLTool.domparse(admin.getSuperSectionNames(sectionKey, true));
        // doc.getDocumentElement().appendChild(doc.importNode(sectionNamesDoc.getDocumentElement(), true));

        final UserEntity userEntity = securityService.getUser( user.getKey() );
        final Document newMenusDoc = buildModelForBrowse( userEntity, menuKey, menuItemKey );

        XMLTool.mergeDocuments( doc, newMenusDoc, true );

        return doc;
    }

    private Document buildModelForBrowse( final UserEntity user, final int menuKey, MenuItemKey menuItemKey )
    {
        final SiteKey siteKey = new SiteKey( menuKey );

        final MenuBrowseModelFactory menuBrowseModelFactory =
            new MenuBrowseModelFactory( securityService, siteDao, menuItemDao, sitePropertiesService );
        final MenuBrowseContentModel model = menuBrowseModelFactory.createContentModel( user, siteKey, menuItemKey );
        return model.toXML().getAsDOMDocument();
    }

    public void handlerRemove( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, int key )
        throws VerticalRemoveException, VerticalSecurityException, VerticalAdminException
    {

        User user = securityService.getLoggedInAdminConsoleUser();

        // First, get supersection key
        SectionCriteria section = new SectionCriteria();
        section.setSectionKey( key );
        Document sectionDoc = XMLTool.domparse( admin.getSections( user, section ) );
        Element sectionElem = (Element) sectionDoc.getDocumentElement().getFirstChild();
        String superSectionKey = sectionElem.getAttribute( "supersectionkey" );

        // Remove all sections recursive. This will silently skip sections
        // that the user is not allowed to remove.
        admin.removeSection( key, true );

        URL redirectURL = new URL( request.getHeader( "referer" ) );
        if ( superSectionKey != null && superSectionKey.length() > 0 )
        {
            redirectURL.setParameter( "sec", superSectionKey );
        }
        else
        {
            redirectURL.removeParameter( "sec" );
        }
        redirectURL.setParameter( "reload", "true" );

        redirectClientToURL( redirectURL, response );
    }

    /**
     * Copy section to another section.
     */
    public void handlerCopy( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                             ExtendedMap formItems, User user, int key )
        throws VerticalSecurityException, VerticalAdminException
    {
        // First, get supersection key
        SectionCriteria section = new SectionCriteria();
        section.setSectionKey( key );
        Document sectionDoc = XMLTool.domparse( admin.getSections( user, section ) );
        Element sectionElem = (Element) sectionDoc.getDocumentElement().getFirstChild();
        String superSectionKey = sectionElem.getAttribute( "supersectionkey" );

        admin.copySection( key );

        URL redirectURL = new URL( request.getHeader( "referer" ) );
        if ( superSectionKey != null && superSectionKey.length() > 0 )
        {
            redirectURL.setParameter( "sec", superSectionKey );
        }
        else
        {
            redirectURL.removeParameter( "sec" );
        }

        redirectURL.setParameter( "reload", "true" );
        redirectClientToURL( redirectURL, response );
    }

    public void handlerPreview( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                ExtendedMap formItems )
        throws VerticalAdminException, VerticalEngineException
    {

        // The preview button is currently disabled in sectionoperations.xsl (dunno why), so this method should not be
        // in use..

        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper( request );
        requestWrapper.setParamsMasked( false );
        requestWrapper.setParameter( "op", "preview" );
        requestWrapper.setParameter( "subop", "frameset" );
        ServletRequestAccessor.setRequest( request );

        User user = securityService.getLoggedInAdminConsoleUser();

        // get content
        int contentKey = formItems.getInt( "contentkey" );
        int contentTypeKey = admin.getContentTypeKey( contentKey );
        String xmlData = admin.getContent( user, contentKey, 1, 1, 0 );
        Document doc = XMLTool.domparse( xmlData );
        requestWrapper.setParameter( "contentkey", String.valueOf( contentKey ) );
        requestWrapper.setParameter( "page", String.valueOf( 999 + contentTypeKey ) );
        session.setAttribute( "_xml", XMLTool.documentToString( doc ) );

        requestWrapper.setParameter( "menukey", formItems.getString( "menukey" ) );
        if ( formItems.containsKey( "pagetemplatekey" ) )
        {
            requestWrapper.setParameter( "pagetemplatekey", formItems.getString( "pagetemplatekey" ) );
        }

        String servletPath = "/adminpage";
        forwardRequest( servletPath, requestWrapper, response );
    }

    private void invalidatePageCache( MenuItemKey menuItemKey )
    {
        MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey );
        SiteEntity site = menuItem.getSite();
        PageCacheService pageCacheService = siteCachesService.getPageCacheService( site.getKey() );
        pageCacheService.removeEntriesByMenuItem( menuItemKey );
    }
}

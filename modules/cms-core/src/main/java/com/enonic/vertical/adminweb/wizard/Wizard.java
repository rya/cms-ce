/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.wizard;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.collect.Maps;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.AdminHandlerBaseServlet;
import com.enonic.vertical.adminweb.AdminStore;
import com.enonic.vertical.adminweb.VerticalAdminException;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.service.AdminService;
import com.enonic.cms.core.xslt.XsltProcessorHelper;

public abstract class Wizard
{
    /**
     * XSL used for creating the wizard xsl
     */
    private final static String BUILD_XSL = "__build_wizard_form_xsl.xsl";

    public final static int BUTTON_NONE = -1;

    public final static int BUTTON_PREVIOUS = 0;

    public final static int BUTTON_NEXT = 1;

    public final static int BUTTON_CANCEL = 2;

    public final static int BUTTON_PROCESS = 3;

    public final static int BUTTON_RELOAD = 4;

    public final static int BUTTON_CLOSE = 5;

    private final static class NextButton
    {
        private String name;

        private String[] testConditions;

        private Step[] nextSteps;

        private Step defaultNextStep;

        private NextButton( Map<String, Step> stepMap, Element buttonElem )
            throws WizardException
        {
            this.name = buttonElem.getAttribute( "name" );

            Element[] conditionElems = XMLTool.getElements( buttonElem, "condition" );
            testConditions = new String[conditionElems.length];
            nextSteps = new Step[conditionElems.length];
            for ( int i = 0; i < conditionElems.length; i++ )
            {
                testConditions[i] = conditionElems[i].getAttribute( "test" );
                if ( testConditions[i] == null || testConditions[i].length() == 0 )
                {
                    String message = "Missing test condition attribute.";
                    WizardLogger.errorWizard(message, null );
                }
                String stepName = conditionElems[i].getAttribute( "goto" );

                Step nextStep = stepMap.get( stepName );
                if ( nextStep != null )
                {
                    conditionElems[i].setAttribute( "gotoid", String.valueOf( nextStep.id ) );
                    nextSteps[i] = nextStep;
                }
                else
                {
                    String message = "Unknown step name in next step condition: {0}";
                    WizardLogger.errorWizard(message, stepName );
                }
            }

            Element defaultElem = XMLTool.getElement( buttonElem, "default" );
            if ( defaultElem != null )
            {
                String stepName = defaultElem.getAttribute( "goto" );

                this.defaultNextStep = stepMap.get( stepName );
                if ( this.defaultNextStep != null )
                {
                    defaultElem.setAttribute( "gotoid", String.valueOf( this.defaultNextStep.id ) );
                }
                else
                {
                    String message = "Unknown step name in default next step condition: {0}";
                    WizardLogger.errorWizard(message, stepName );
                }
            }
            else
            {
                String message = "Missing default next step condition.";
                WizardLogger.errorWizard(message, null );
            }
        }
    }

    protected abstract static class Step
    {
        public static final int NORMAL = 0;

        public static final int FINISH = 1;

        protected int id;

        protected String name;

        protected int type;

        protected Step( int id, String name, int type )
        {
            this.id = id;
            this.name = name;
            this.type = type;
        }

        /**
         * @return
         */
        public int getId()
        {
            return id;
        }

        /**
         * @return
         */
        public String getName()
        {
            return name;
        }

        /**
         * @return
         */
        public int getType()
        {
            return type;
        }

    }

    protected final static class NormalStep
        extends Step
    {
        private boolean stateDependent;

        private Document dataconfigDoc;

        // buttons

        private String previousButtonName;

        private Map<String, NextButton> nextButtons = new HashMap<String, NextButton>();

        private String cancelButtonName;

        private String closeButtonName;

        private Map<String, String> processButtonSrcs = new HashMap<String, String>();

        protected NormalStep( int id, Element stepElem )
            throws WizardException
        {
            super( id, stepElem.getAttribute( "name" ), NORMAL );

            Element dataconfigElem = XMLTool.getElement( stepElem, "dataconfig" );
            if ( dataconfigElem == null )
            {
                String message = "Step \"{0}\" does not have a data configuration.";
                WizardLogger.errorWizard(message, name );
            }
            this.dataconfigDoc = XMLTool.createDocument();
            this.dataconfigDoc.appendChild( this.dataconfigDoc.importNode( dataconfigElem, true ) );
        }
    }

    protected final static class FinishStep
        extends Step
    {
        private String styleSheetSrc;

        protected FinishStep( int id, Element stepElem )
            throws WizardException
        {
            super( id, stepElem.getAttribute( "name" ), FINISH );

            Element stylesheetElem = XMLTool.getElement( stepElem, "stylesheet" );
            if ( stylesheetElem != null )
            {
                this.styleSheetSrc = stylesheetElem.getAttribute( "src" );
                if ( this.styleSheetSrc == null )
                {
                    String message = "Missing XSL source definition for stylesheet in finish step \"{0}\".";
                    WizardLogger.errorWizard(message, name );
                }
            }
        }
    }

    protected final static class StepState
    {
        private NormalStep step;

        private StepState previousStepState, nextStepState;

        private Document stateDoc;

        private String buttonPressed;

        private boolean errorState;

        private StepState( NormalStep step )
        {
            this.step = step;
            this.stateDoc = XMLTool.createDocument( "stepstate" );
            Element stepstateElem = this.stateDoc.getDocumentElement();
            stepstateElem.setAttribute( "stepid", String.valueOf( step.id ) );
        }

        private void clearState()
        {
            Element rootElem = this.stateDoc.getDocumentElement();
            rootElem.removeAttribute( "buttonpressed" );
            XMLTool.removeChildNodes( rootElem, true );
        }

        private void saveCustomState( ExtendedMap formItems )
        {
            Element rootElem = this.stateDoc.getDocumentElement();
            XMLTool.removeChildNodes( rootElem, true );
            XMLTool.buildSubTree( stateDoc, rootElem, "stepstate", formItems );
        }

        private void saveCommonState( ExtendedMap formItems )
        {
            this.buttonPressed = formItems.getString( "__wizard_button" );
            Element rootElem = this.stateDoc.getDocumentElement();
            rootElem.setAttribute( "buttonpressed", this.buttonPressed );
        }

        /**
         * @return
         */
        public StepState getNextStepState()
        {
            return nextStepState;
        }

        /**
         * @return
         */
        public StepState getPreviousStepState()
        {
            return previousStepState;
        }

        /**
         * @return
         */
        public Document getStateDoc()
        {
            return stateDoc;
        }

        /**
         * @return
         */
        public NormalStep getStep()
        {
            return step;
        }

        /**
         * @return
         */
        public String getButtonPressed()
        {
            return buttonPressed;
        }

        /**
         * @param string
         */
        public void setButtonPressed( String string )
        {
            buttonPressed = string;
        }

        public boolean isErrorState()
        {
            return errorState;
        }

        public void setErrorState( boolean errorState )
        {
            this.errorState = errorState;
            Element rootElem = stateDoc.getDocumentElement();
            if ( errorState )
            {
                rootElem.setAttribute( "errorstate", "true" );
            }
            else
            {
                rootElem.removeAttribute( "errorstate" );
            }
        }

    }

    protected final static class WizardState
    {
        private String redirectURL;

        private Map<String, StepState> stepStateMap = Maps.newHashMap();

        private StepState firstStepState, currentStepState;

        private Step currentStep;

        private Map<String, String> errorCodes = Maps.newHashMap();

        private Map<String, String[]> errorParts = Maps.newHashMap();

        private WizardState()
        {
        }

        private void saveCommonState( ExtendedMap formItems )
        {
            errorCodes.clear();
            errorParts.clear();
            if ( currentStepState == null )
            {
                throw new IllegalStateException(
                    "The wizard has completed it's work and is no longer accessible.  This error message is normally caused by pressing" +
                        " the finish button too many times, or the back or refresh button in the browser after the last step of the wizard." );
            }
            currentStepState.clearState();
            currentStepState.saveCommonState( formItems );
        }

        private void saveCustomState( ExtendedMap formItems )
        {
            currentStepState.saveCustomState( formItems );
        }

        public int getPressedButtonType()
        {
            // when the wizard are in its first state, no button is pressed
            if ( currentStepState == null || currentStepState.buttonPressed == null )
            {
                return BUTTON_NONE;
            }

            String buttonName = currentStepState.buttonPressed;
            NormalStep currentStep = currentStepState.step;
            if ( buttonName.equals( currentStep.previousButtonName ) )
            {
                return BUTTON_PREVIOUS;
            }
            else if ( buttonName.equals( currentStep.cancelButtonName ) )
            {
                return BUTTON_CANCEL;
            }
            else if ( buttonName.equals( currentStep.closeButtonName ) )
            {
                return BUTTON_CLOSE;
            }
            else if ( currentStep.nextButtons.containsKey( buttonName ) )
            {
                return BUTTON_NEXT;
            }
            else if ( currentStep.processButtonSrcs.containsKey( buttonName ) )
            {
                return BUTTON_PROCESS;
            }
            else
            {
                return BUTTON_RELOAD;
            }
        }

        /**
         * Returns an xml document representation of the wizard state.
         *
         * @return
         */
        public Document toDocument()
        {
            Document wizardstateDoc = XMLTool.createDocument( "wizardstate" );
            Element wizardstateElem = wizardstateDoc.getDocumentElement();

            // set current step and step state id
            wizardstateElem.setAttribute( "currentstepid", String.valueOf( currentStep.id ) );

            // add state documents
            int nextId = 0;
            StepState stepState = firstStepState;
            while ( stepState != null )
            {
                Element stepstateElem = stepState.stateDoc.getDocumentElement();
                stepstateElem = (Element) wizardstateDoc.importNode( stepstateElem, true );
                int currentStepStateId = nextId++;
                stepstateElem.setAttribute( "id", String.valueOf( currentStepStateId ) );
                wizardstateElem.appendChild( stepstateElem );
                if ( stepState == currentStepState )
                {
                    wizardstateElem.setAttribute( "currentstepstateid", String.valueOf( currentStepStateId ) );
                }

                stepState = stepState.nextStepState;
            }

            Element errorsElem = XMLTool.createElement( wizardstateDoc, wizardstateElem, "errors" );
            for ( Iterator<String> iter = errorCodes.keySet().iterator(); iter.hasNext(); )
            {
                String errorCode = iter.next();
                String fieldName = errorCodes.get( errorCode );
                Element errorElem = XMLTool.createElement( wizardstateDoc, errorsElem, "error" );
                errorElem.setAttribute( "code", errorCode );
                errorElem.setAttribute( "name", fieldName );
                if ( errorParts.containsKey( errorCode ) )
                {
                    String[] parts = errorParts.get( errorCode );
                    for ( int i = 0; i < parts.length; i++ )
                    {
                        XMLTool.createElement( wizardstateDoc, errorElem, "part", parts[i] );
                    }
                }
            }

            return wizardstateDoc;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString()
        {
            return XMLTool.documentToString( toDocument(), 2 );
        }

        public void addError( String errorCode, String fieldName )
        {
            addError( errorCode, fieldName, (String[]) null );
        }

        public void addError( String errorCode, String fieldName, String part )
        {
            addError( errorCode, fieldName, new String[]{part} );
        }

        public void addError( String errorCode, String fieldName, String[] parts )
        {
            if ( !errorCodes.containsKey( errorCode ) )
            {
                errorCodes.put( errorCode, fieldName );
            }
            if ( parts != null )
            {
                errorParts.put( errorCode, parts );
            }
        }

        public boolean hasErrors()
        {
            return errorCodes.size() > 0;
        }

        public boolean hasError( String errorCode )
        {
            return errorCodes.containsKey( errorCode );
        }

        /**
         * @return
         */
        public Step getCurrentStep()
        {
            return currentStep;
        }

        /**
         * @return
         */
        public StepState getCurrentStepState()
        {
            return currentStepState;
        }

        public StepState getStepState( String stepName )
        {
            return stepStateMap.get( stepName );
        }

        /**
         * @return
         */
        public String getRedirectURL()
        {
            return redirectURL;
        }

        /**
         * @param string
         */
        public void setRedirectURL( String string )
        {
            redirectURL = string;
        }

        /**
         * @return
         */
        public StepState getFirstStepState()
        {
            return firstStepState;
        }

    }

    private static int nextWizardId;

    private int wizardId = nextWizardId++;

    private String wizardConfigFilename;

    private Document wizardconfigDoc;

    protected AdminHandlerBaseServlet servlet;

    private NormalStep firstStep;

    private static Wizard createWizard( AdminService admin, ApplicationContext applicationContext, AdminHandlerBaseServlet servlet, HttpSession session,
                                        String wizardConfigFilename )
        throws WizardException
    {
        Document wizardconfigDoc = AdminStore.getXml( session, wizardConfigFilename ).getAsDOMDocument();
        Element rootElem = wizardconfigDoc.getDocumentElement();
        String className = rootElem.getAttribute( "class" );

        // create new wizard
        Wizard wizard = null;
        Class wizardClass = null;
        try
        {
            wizardClass = Class.forName( className );
        }
        catch ( ClassNotFoundException cnfe )
        {
            String message = "Wizard class not found: %t";
            WizardLogger.errorWizard(message, cnfe );
        }
        catch ( ClassCastException cce )
        {
            String message = "Wizard class does not extend Wizard: %t";
            WizardLogger.errorWizard(message, cce );
        }

        wizard = (Wizard)applicationContext.getBean( className, wizardClass );

        wizard.wizardConfigFilename = wizardConfigFilename;
        wizard.servlet = servlet;

        // initialize wizard
        wizard.initializeInternal( admin, wizardconfigDoc );

        return wizard;
    }

    public static Wizard getInstance( AdminService admin, ApplicationContext applicationContext, AdminHandlerBaseServlet servlet, HttpSession session, ExtendedMap formItems,
                                      String wizardConfigFilename )
        throws WizardException
    {
        String buttonName = formItems.getString( "__wizard_button", null );
        if ( buttonName == null )
        {
            Wizard wizard = createWizard( admin, applicationContext, servlet, session, wizardConfigFilename );
            session.setAttribute( "__" + wizardConfigFilename, wizard );
            return wizard;
        }
        else
        {
            return (Wizard) session.getAttribute( "__" + wizardConfigFilename );
        }
    }

    protected Wizard()
    {
    }

    private void initializeInternal( AdminService admin, Document wizardconfigDoc )
        throws WizardException
    {
        Element rootElem = wizardconfigDoc.getDocumentElement();

        // set wizard config document
        this.wizardconfigDoc = wizardconfigDoc;

        // steps config
        Element stepsElem = XMLTool.getElement( rootElem, "steps" );
        if ( stepsElem == null )
        {
            String message = "No steps defined.";
            WizardLogger.errorWizard(message, null );
        }

        Element[] stepElems = XMLTool.getElements( stepsElem );
        Element[] buttonsElems = new Element[stepElems.length];
        Step[] steps = new Step[stepElems.length];
        if ( stepElems.length == 0 )
        {
            String message = "No steps defined.";
            WizardLogger.errorWizard(message, null );
        }

        // create steps
        Map<String, Step> stepMap = new HashMap<String, Step>();
        for ( int i = 0; i < stepElems.length; i++ )
        {
            // set id
            stepElems[i].setAttribute( "id", String.valueOf( i ) );

            // get type and create step
            String type = stepElems[i].getAttribute( "type" );
            if ( "normal".equals( type ) )
            {
                steps[i] = new NormalStep( i, stepElems[i] );
            }
            else if ( "finish".equals( type ) )
            {
                steps[i] = new FinishStep( i, stepElems[i] );
                if ( i == 0 )
                {
                    String message = "First step cannot be a finish step";
                    WizardLogger.errorWizard(message, null );
                }
            }
            else
            {
                String message = "Unknown step type: {0}";
                WizardLogger.errorWizard(message, type );
            }

            // save step for later
            String name = stepElems[i].getAttribute( "name" );
            stepMap.put( name, steps[i] );

            // save button definitions for later processing
            Element buttonsElem = XMLTool.getElement( stepElems[i], "buttons" );
            if ( buttonsElem == null && "normal".equals( type ) )
            {
                String message = "Normal steps must include buttons.";
                WizardLogger.errorWizard(message, null );
            }
            else if ( "normal".equals( type ) )
            {
                buttonsElems[i] = buttonsElem;
            }
        }
        firstStep = (NormalStep) steps[0];

        // configure buttons
        for ( int i = 0; i < buttonsElems.length; i++ )
        {
            // finish steps does not have buttons
            if ( buttonsElems[i] == null )
            {
                continue;
            }

            Element[] buttonElems = XMLTool.getElements( buttonsElems[i] );
            if ( buttonElems.length > 0 )
            {
                NormalStep normalStep = (NormalStep) steps[i];

                for ( int j = 0; j < buttonElems.length; j++ )
                {
                    String name = buttonElems[j].getAttribute( "name" );
                    int type = getButtonType( buttonElems[j].getAttribute( "type" ) );

                    if ( type == BUTTON_PREVIOUS )
                    {
                        if ( normalStep.previousButtonName != null )
                        {
                            String message = "Only one previous button allowed";
                            WizardLogger.errorWizard(message, null );
                        }
                        normalStep.previousButtonName = name;
                    }
                    else if ( type == BUTTON_NEXT )
                    {
                        normalStep.nextButtons.put( name, new NextButton( stepMap, buttonElems[j] ) );
                    }
                    else if ( type == BUTTON_CANCEL )
                    {
                        if ( normalStep.cancelButtonName != null )
                        {
                            String message = "Only one cancel button allowed for each step.";
                            WizardLogger.errorWizard(message, null );
                        }
                        else if ( normalStep.closeButtonName != null )
                        {
                            String message = "Only one cancel or close button allowed for each step.";
                            WizardLogger.errorWizard(message, null );
                        }
                        normalStep.cancelButtonName = name;
                    }
                    else if ( type == BUTTON_CLOSE )
                    {
                        if ( normalStep.closeButtonName != null )
                        {
                            String message = "Only one close button allowed for each step.";
                            WizardLogger.errorWizard(message, null );
                        }
                        else if ( normalStep.cancelButtonName != null )
                        {
                            String message = "Only one cancel or close button allowed for each step.";
                            WizardLogger.errorWizard(message, null );
                        }
                        normalStep.closeButtonName = name;
                    }
                    else if ( type == BUTTON_PROCESS )
                    {
                        Element elem = XMLTool.getElement( buttonElems[j], "stylesheet" );
                        if ( elem != null )
                        {
                            String styleSheetSrc = elem.getAttribute( "src" );
                            normalStep.processButtonSrcs.put( name, styleSheetSrc );
                        }
                        else
                        {
                            normalStep.processButtonSrcs.put( name, null );
                        }
                    }
                }

                if ( normalStep.nextButtons.size() == 0 )
                {
                    String message = "Each step must contain at least one next button.";
                    WizardLogger.errorWizard(message, null );
                }
                if ( normalStep.cancelButtonName == null && normalStep.closeButtonName == null )
                {
                    normalStep.cancelButtonName = "cancel";
                }
            }
            else
            {
                String message = "Each step must contain at least one next button.";
                WizardLogger.errorWizard(message, null );
            }
        }

        initialize( admin, wizardconfigDoc );
    }

    /**
     * Custom init.
     */
    protected abstract void initialize( AdminService admin, Document wizardconfigDoc )
        throws WizardException;

    private WizardState firstStep()
    {
        WizardState wizardState = new WizardState();
        wizardState.currentStep = firstStep;
        wizardState.firstStepState = new StepState( firstStep );
        wizardState.currentStepState = wizardState.firstStepState;
        wizardState.stepStateMap.put( firstStep.name, wizardState.firstStepState );

        return wizardState;
    }

    private void nextStep( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems )
        throws WizardException
    {
        // validate state
        boolean validated = validateState( wizardState, session, admin, formItems );

        StepState currentStepState = wizardState.currentStepState;
        if ( validated )
        {
            currentStepState.setErrorState( false );
            NormalStep currentStep = currentStepState.step;

            // select next step
            String nextButtonName = currentStepState.buttonPressed;
            NextButton nextButton = currentStep.nextButtons.get( nextButtonName );
            if ( nextButton != null )
            {
                Step nextStep = null;
                for ( int i = 0; i < nextButton.testConditions.length; i++ )
                {
                    String testCondition = nextButton.testConditions[i];
                    if ( evaluate( wizardState, session, admin, formItems, testCondition ) )
                    {
                        nextStep = nextButton.nextSteps[i];
                        break;
                    }
                }

                if ( nextStep == null )
                {
                    nextStep = nextButton.defaultNextStep;
                }

                if ( nextStep.type == Step.NORMAL )
                {
                    StepState stepState = new StepState( (NormalStep) nextStep );
                    currentStepState.nextStepState = stepState;
                    stepState.previousStepState = currentStepState;
                    wizardState.currentStepState = stepState;
                    wizardState.stepStateMap.put( nextStep.name, stepState );
                }
                else
                {
                    wizardState.currentStepState = null;
                }

                wizardState.currentStep = nextStep;
            }
            else
            {
                String message = "Unknown next button pressed: {0}";
                WizardLogger.errorWizard(message, nextButtonName );
            }
        }
        else
        {
            currentStepState.setErrorState( true );
        }
    }

    private void previousStep( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems )
        throws WizardException
    {
        StepState currentStepState = wizardState.currentStepState;

        // set new current state
        wizardState.currentStepState = currentStepState.previousStepState;
        currentStepState.nextStepState = null;
        wizardState.currentStep = wizardState.currentStepState.step;

        // save current step state for later if not state dependent
        //NormalStep currentStep = currentStepState.step;
        //if ( !currentStep.stateDependent ) {
        //    wizardState.stepStateMap.put( currentStep.name, currentStepState );
        //    currentStepState.previousStepState = null;
        //    currentStepState.nextStepState = null;
        //}
    }

    protected abstract boolean evaluate( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                         String testCondition )
        throws WizardException;

    protected abstract void appendCustomData( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                              ExtendedMap parameters, User user, Document dataconfigDoc, Document wizarddataDoc )
        throws WizardException;

    protected abstract boolean validateState( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems );

    protected abstract void processWizardData( WizardState wizardState, HttpSession session, AdminService admin, ExtendedMap formItems,
                                               User user, Document dataDoc )
        throws VerticalAdminException, VerticalEngineException;

    public final void processRequest( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                                      ExtendedMap formItems, ExtendedMap parameters, User user )
        throws VerticalAdminException, VerticalEngineException
    {

        // get wizard state
        WizardState wizardState = (WizardState) session.getAttribute( "wizardstate_" + wizardId );
        String buttonName = formItems.getString( "__wizard_button", null );

        // first step
        if ( wizardState == null || buttonName == null )
        {
            // initialize wizard state
            wizardState = firstStep();
            session.setAttribute( "wizardstate_" + wizardId, wizardState );

            // set redirect url
            wizardState.redirectURL = formItems.getString( "redirect", null );
            if ( wizardState.redirectURL == null )
            {
                String referer = request.getHeader( "referer" );
                if ( referer == null )
                {
                    String message = "No support for referer, please add redirect parameter.";
                    WizardLogger.errorWizard(message, null );
                }
                wizardState.redirectURL = referer;
            }

            processCurrentStep( wizardState, request, response, session, admin, formItems, parameters, user );
        }

        // previous, next, cancel, reload or process
        else
        {
            // save common state
            wizardState.saveCommonState( formItems );

            // button type pressed
            int pressedButtonType = wizardState.getPressedButtonType();
            if ( pressedButtonType == BUTTON_CANCEL || pressedButtonType == BUTTON_CLOSE )
            {
                // fire events
                if ( pressedButtonType == BUTTON_CANCEL )
                {
                    cancelClicked( wizardState );
                }
                else
                {
                    closeClicked( wizardState );
                }

                try
                {
                    response.sendRedirect( response.encodeRedirectURL( wizardState.redirectURL ) );
                }
                catch ( IOException ioe )
                {
                    String message = "Failed to redirect client: %t";
                    WizardLogger.errorWizard(message, ioe );
                }
            }
            else
            {
                // save custom state
                saveState( wizardState, request, response, admin, user, formItems );

                if ( pressedButtonType == BUTTON_PREVIOUS )
                {
                    previousStep( wizardState, session, admin, formItems );
                    processCurrentStep( wizardState, request, response, session, admin, formItems, parameters, user );
                }
                else if ( pressedButtonType == BUTTON_NEXT )
                {
                    nextStep( wizardState, session, admin, formItems );
                    processCurrentStep( wizardState, request, response, session, admin, formItems, parameters, user );
                }
                else
                {
                    // reload or process
                    processCurrentStep( wizardState, request, response, session, admin, formItems, parameters, user );
                }
            }
        }
    }

    protected void cancelClicked( WizardState wizardState )
    {
        // default: do nothing
    }

    /**
     * Event fired when close button is clicked.
     *
     * @param wizardState
     */
    protected void closeClicked( WizardState wizardState )
    {
        // default: do nothing
    }

    private void processCurrentStep( WizardState wizardState, HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                     AdminService admin, ExtendedMap formItems, ExtendedMap parameters, User user )
        throws VerticalAdminException, VerticalEngineException
    {

        Step currentStep = wizardState.currentStep;

        // finish step
        if ( currentStep.type == Step.FINISH )
        {
            FinishStep finishStep = (FinishStep) currentStep;

            // process wizard data
            processWizardDataInternal( wizardState, finishStep.styleSheetSrc, session, admin, formItems, user );

            // redirect
            try
            {
                response.sendRedirect( response.encodeRedirectURL( wizardState.redirectURL ) );
            }
            catch ( IOException ioe )
            {
                String message = "Failed to redirect client: %t";
                WizardLogger.errorWizard(message, ioe );
            }
        }

        // normal step, display wizard step
        else
        {
            NormalStep normalStep = (NormalStep) currentStep;

            // if button type is "process", process wizard data
            int pressedButtonType = wizardState.getPressedButtonType();
            if ( pressedButtonType == BUTTON_PROCESS )
            {
                // validate state
                boolean validated = validateState( wizardState, session, admin, formItems );
                if ( validated )
                {
                    String buttonName = wizardState.currentStepState.buttonPressed;
                    String styleSheetSrc = normalStep.processButtonSrcs.get( buttonName );
                    processWizardDataInternal( wizardState, styleSheetSrc, session, admin, formItems, user );
                }
            }

            Document wizarddataDoc = XMLTool.createDocument( "wizarddata" );
            Element rootElem = wizarddataDoc.getDocumentElement();

            // wizard data: add wizard config
            rootElem.appendChild( wizarddataDoc.importNode( wizardconfigDoc.getDocumentElement(), true ) );

            // wizard data: add custom data
            appendCustomData( wizardState, session, admin, formItems, parameters, user, normalStep.dataconfigDoc, wizarddataDoc );

            int unitKey = formItems.getInt( "selectedunitkey", -1 );
            int menuKey = formItems.getInt( "menukey", -1 );
            servlet.addCommonParameters( admin, user, request, parameters, unitKey, menuKey );

            // wizard data: add wizard state
            Document wizardstateDoc = wizardState.toDocument();
            rootElem.appendChild( wizarddataDoc.importNode( wizardstateDoc.getDocumentElement(), true ) );

            // transform
            Source xslSource = buildWizardXSL( session, wizardState );
            Source xmlSource = new DOMSource( wizarddataDoc );
            try
            {
                ExtendedMap transformParams = new ExtendedMap( formItems );
                transformParams.putAll( parameters );
                String languageCode = (String) session.getAttribute( "languageCode" );

                new XsltProcessorHelper()
                        .stylesheet( xslSource, AdminStore.getURIResolver( languageCode ))
                        .input( xmlSource )
                        .params( transformParams )
                        .process(response.getWriter());
            }
            catch ( IOException ioe )
            {
                String message = "Failed to get response writer: %t";
                WizardLogger.errorWizard(message, ioe );
            }
        }
    }

    private void processWizardDataInternal( WizardState wizardState, String styleSheetSrc, HttpSession session, AdminService admin,
                                            ExtendedMap formItems, User user )
        throws WizardException, VerticalAdminException, VerticalEngineException
    {

        // transform wizard state (optional)
        Document dataDoc;
        if ( styleSheetSrc != null )
        {
            Source xslSource = AdminStore.getStylesheet( session, styleSheetSrc );
            Source xmlSource = new DOMSource( wizardState.toDocument() );

            String languageCode = (String) session.getAttribute( "languageCode" );

            dataDoc = new XsltProcessorHelper()
                    .stylesheet( xslSource, AdminStore.getURIResolver( languageCode ) )
                    .params( formItems )
                    .input( xmlSource )
                    .processDom();
        }
        else
        {
            dataDoc = null;
        }

        // process data
        processWizardData( wizardState, session, admin, formItems, user, dataDoc );
    }

    private Source buildWizardXSL( HttpSession session, WizardState wizardState )
        throws WizardException
    {
        Source source = null;
        Source xmlSource = new DOMSource( wizardconfigDoc );
        Source xslSource = AdminStore.getStylesheet( session, BUILD_XSL );
        Map<String, String> xslParams = new HashMap<String, String>();

        xslParams.put( "xsl_prefix", "" );

        xslParams.put( "wizard_stepid", String.valueOf( wizardState.currentStep.id ) );
        String languageCode = (String) session.getAttribute( "languageCode" );
        StringWriter sw = new StringWriter( 4 * 1024 );

        new XsltProcessorHelper()
                .stylesheet( xslSource, AdminStore.getURIResolver( languageCode ))
                .input( xmlSource )
                .params( xslParams )
                .process(sw);


        source = new StreamSource( new StringReader( sw.toString() ) );
        source.setSystemId( xslSource.getSystemId() );

        return source;
    }

    protected void saveState( WizardState wizardState, HttpServletRequest request, HttpServletResponse response, AdminService admin,
                              User user, ExtendedMap formItems )
        throws WizardException
    {
        wizardState.saveCustomState( formItems );
    }

    private int getButtonType( String buttonTypeName )
    {
        if ( "previous".equals( buttonTypeName ) )
        {
            return BUTTON_PREVIOUS;
        }
        else if ( "next".equals( buttonTypeName ) )
        {
            return BUTTON_NEXT;
        }
        else if ( "cancel".equals( buttonTypeName ) )
        {
            return BUTTON_CANCEL;
        }
        else if ( "close".equals( buttonTypeName ) )
        {
            return BUTTON_CLOSE;
        }
        else if ( "process".equals( buttonTypeName ) )
        {
            return BUTTON_PROCESS;
        }
        else if ( "reload".equals( buttonTypeName ) )
        {
            return BUTTON_RELOAD;
        }
        else
        {
            return BUTTON_NONE;
        }
    }
}
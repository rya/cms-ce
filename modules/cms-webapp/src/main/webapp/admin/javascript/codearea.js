/*
    Requires:

        XSL:

        <xsl:include href="common/codearea.xsl"/>

        CSS:

        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
        <link rel="stylesheet" type="text/css" href="css/codearea.css"/>

        JS:
        
        <script type="text/javascript" src="codemirror/js/codemirror.js">//</script>
        <script type="text/javascript" src="javascript/codearea.js">//</script>
        <script type="text/javascript" src="javascript/admin.js">//</script>
*/

if ( !cms ) var cms = {};
if ( !cms.ui ) cms.ui = {};

cms.ui.CodeArea = function( options )
{

    //////////////////////////////// Private ////////////////////////////////


    var inst = this;
    var codemirror = null;
    var width = options.width;
    var height = options.height;
    var readOnly = options.readOnly;
    var hasStatusBar = options.statusBar;
    var textareaId = options.textareaId;
    var buttonsConfig = options.buttons;
    var useLineNumbers = options.lineNumbers;
    var createButtons = buttonsConfig.length > 0 && !readOnly;
    var textarea = document.getElementById(textareaId);
    var editorContainer = document.getElementById('code-area-container-' + textareaId);
    var buttonsContainer = document.getElementById('code-area-buttons-container-' + textareaId);
    var documentContainer = document.getElementById('code-area-document-container-' + textareaId);
    var currentLineNumberContainer = document.getElementById('ca-current-line-number-' + textareaId);
    var currentColumnNumberContainer = document.getElementById('ca-current-column-number-' + textareaId);
    var availableButtons = {
        indentall : {
            tooltip: 'Indent all',
            icon: '',
            command: function () {
                inst.indentAll();
            }
        },
        indentselection : {
            tooltip: 'Indent selection',
            icon: '',
            command: function () {
                inst.indentSelection();
            }
        },
        find : {
            tooltip: 'Find...',
            icon: '',
            command: function () {
                inst.find();
            }
        },
        replace : {
            tooltip: 'Find and replace...',
            icon: '',
            command: function () {
                inst.replace();
            }
        },
        gotoline : {
            tooltip: 'Go to line...',
            icon: '',
            command: function () {
                inst.gotoLine();
            }
        },
        toggleeditor : {
            tooltip: 'Toggle',
            icon: '',
            command: function () {
                inst.toggleEditor();
            }
        }
    };

    var configuredButtons = {};

    var isIE = document.all;

    function trim( str )
    {
        return str.replace( /^\s+|\s+$/g, '' );
    }

    function createReadOnlyShim()
    {
        var shimElem = document.createElement( 'div' );

        shimElem.className = 'code-area-readonly-shim';
        shimElem.style.width = width;
        shimElem.style.height = height;

        documentContainer.insertBefore( shimElem, documentContainer.firstChild );
    }


    //////////////////////////////// Public ////////////////////////////////

    
    this.editorOn = true;

    this.renderButtons = function()
    {
        var buttons = buttonsConfig.split( ',' );
        var buttonName, buttonElem, buttonIsAvailable, id, tooltip, command;
        var i;

        for ( i = 0; i < buttons.length; i++ )
        {
            buttonName = trim( buttons[i] );
            buttonIsAvailable = buttonName in availableButtons;

            if ( buttonIsAvailable )
            {
                id = buttonsContainer.id + '_' + buttonName;
                tooltip = availableButtons[buttonName].tooltip;
                command = availableButtons[buttonName].command;

                buttonElem = document.createElement( 'a' );
                buttonElem.id = id;
                buttonElem.href = 'javascript:';
                buttonElem.className = 'ca-button ca-button-' + buttonName;
                buttonElem.title = tooltip;
                /* admin.js */ addEvent( buttonElem, 'click', command, false );
                /* admin.js */ addEvent( buttonElem, 'click', function( event )
                {
                    var target = event.target ? event.target : event.srcElement; 
                    // event.target.blur()
                }, false );

                buttonsContainer.appendChild( buttonElem );

                if ( buttonName === 'toggleeditor' )
                {
                    buttonElem.className += ' ca-button-active';
                }

                configuredButtons[buttonName] = true;
            }
        }

        buttonsContainer.style.display = 'block';
    };


    this.caretChange = function( event )
    {
        var cursorPosition = codemirror.cursorPosition();
        var lineNumber = ( event.type == 'blur' ) ? 0 : codemirror.lineNumber(cursorPosition.line);
        var columnNumber = ( event.type == 'blur' ) ? 0 : cursorPosition.character + 1;

        if ( hasStatusBar )
        {
            inst.updateLineAndColumnNumber( lineNumber, columnNumber )
        }
    };


    this.updateLineAndColumnNumber = function( lineNumber, columnNumber )
    {
        currentLineNumberContainer.innerHTML = lineNumber;
        currentColumnNumberContainer.innerHTML = columnNumber;
    };


    this.focusDocument = function( event )
    {
        inst.caretChange( event );
    };


    this.blurDocument = function( event )
    {
        inst.caretChange( event );
    };


    this.setCode = function( str )
    {
        codemirror.setCode( str );
    };


    this.getCode = function()
    {
        return codemirror.getCode();
    };


    this.indentAll = function()
    {
        codemirror.reindent();
    };


    this.indentSelection = function()
    {
        codemirror.reindentSelection();
    };


    this.find = function()
    {
        var text = prompt( "Enter search term:", "" );
        if ( !text )
        {
            return;
        }

        var first = true;
        do {
            var cursor = codemirror.getSearchCursor( text, first );
            first = false;
            while ( cursor.findNext() )
            {
                cursor.select();
                if ( !confirm( "Search again?" ) )
                {
                    return;
                }
            }
        }
        while ( confirm( "End of document reached. Start over?" ) );
    };


    this.replace = function()
    {
        // This is a replace-all, but it is possible to implement a
        // prompting replace.
        var count = 0;

        var from = prompt( "Enter search string:", "" ), to;
        if ( from )
        {
            to = prompt( "What should it be replaced with?", "" );
        }
        if ( to == null )
        {
            return;
        }

        var cursor = codemirror.getSearchCursor( from, false );
        
        while ( cursor.findNext() )
        {
            cursor.replace( to );
            count++;
        }

        alert(count + " occurences replaced");
    };


    this.gotoLine = function()
    {
        var line = prompt( "Go to line:", "" );

        if ( line && !isNaN( Number( line ) ) && line > 1 )
        {
            codemirror.jumpToLine( Number( line ) );
        }
    };


    this.toggleEditor = function()
    {                                            
        var toggleBtn = document.getElementById(buttonsContainer.id + '_toggleeditor');

        if ( this.editorOn )
        {
            textarea.style.display = '';
            codemirror.wrapping.style.display = 'none';
            textarea.value = this.getCode();
            toggleBtn.className = toggleBtn.className.replace( /ca-button-active/g, '' );

            this.editorOn = false;
        }
        else
        {
            textarea.style.display = 'none';
            codemirror.wrapping.style.display = 'block';
            this.setCode(textarea.value);
            toggleBtn.className = toggleBtn.className + ' ca-button-active';

            this.editorOn = true;
        }
    };


    this.init = function()
    {
        if ( createButtons )
        {
            this.renderButtons();
        }

        if ( readOnly )
        {
            createReadOnlyShim();
        }

        codemirror = CodeMirror.fromTextArea( textareaId, {
            lineNumbers: useLineNumbers,
            textWrapping: true,
            path: "codemirror/js/",
            tabMode: 'shift',
            readOnly: readOnly,
            indentUnit: 2,
            parserfile: ["parsexml.js"],
            stylesheet: ["codemirror/css/cms.xmlcolors.css"],
            width: width = ( width === '100%' ) ? '' : width, // Due to browser quirks It is better to leave width blank if width is "100%".
            height: height,
            parserConfig: { useHTMLKludges: false },
            reindentOnLoad: true
        } );

        // Add input events to the editor document.

        if ( hasStatusBar )
        {
            /* admin.js */ addEvent(codemirror.win, 'click', inst.caretChange, false);
            /* admin.js */ addEvent(codemirror.win, 'keyup', inst.caretChange, false);
        }

        /* admin.js */ addEvent(codemirror.win, 'click', inst.focusDocument, false);
        /* admin.js */ addEvent(codemirror.win, 'focus', inst.focusDocument, false);
        /* admin.js */ addEvent(codemirror.win, 'blur', inst.blurDocument, false);
        /* admin.js */ addEvent(codemirror.win, 'keydown', function(event)
                        {
                            var command_f_pressed = event.metaKey && event.keyCode === 70;
                            var command_r_pressed = event.metaKey && event.keyCode === 82;
                            var command_g_pressed = event.metaKey && event.keyCode === 71;

                            if ( command_f_pressed || command_r_pressed || command_g_pressed )
                            {
                                if ( command_f_pressed && 'find' in configuredButtons )
                                {
                                    inst.find();
                                }
                                else if ( command_r_pressed && 'replace' in configuredButtons )
                                {
                                    inst.replace();
                                }
                                else if ( command_g_pressed && 'gotoline' in configuredButtons )
                                {
                                    inst.gotoLine();
                                }
                                else
                                {
                                    /**/
                                }

                                if ( isIE ) event.returnValue = false;
                                else event.preventDefault();
                            }

                        }, false);
    };
};
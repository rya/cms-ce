<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:x="mailto:vro@enonic.com?subject=foobar"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:namespace-alias stylesheet-prefix="x" result-prefix="xsl"/>
  <xsl:output method="xml"/>
  <xsl:param name="xsl_prefix"/>

  <xsl:template match="/">

  <x:stylesheet version="1.0" exclude-result-prefixes="#all"
          xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
          xmlns:exslt-common="http://exslt.org/common"
          xmlns:saxon="http://saxon.sf.net/"
          xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <x:output method="html"/>

  <x:include>
    <xsl:attribute name="href">
      <xsl:value-of select="concat($xsl_prefix, 'common/standard_form_templates.xsl')"/>
    </xsl:attribute>
  </x:include>

  <x:include>
    <xsl:attribute name="href">
      <xsl:value-of select="concat($xsl_prefix, 'common/textfield.xsl')"/>
    </xsl:attribute>
  </x:include>

  <x:include>
    <xsl:attribute name="href">
      <xsl:value-of select="concat($xsl_prefix, 'common/textarea.xsl')"/>
    </xsl:attribute>
  </x:include>

  <x:include>
    <xsl:attribute name="href">
      <xsl:value-of select="concat($xsl_prefix, 'common/textfielddate.xsl')"/>
    </xsl:attribute>
  </x:include>

  <x:include>
    <xsl:attribute name="href">
      <xsl:value-of select="concat($xsl_prefix, 'editor/xhtmleditor.xsl')"/>
    </xsl:attribute>
  </x:include>

  <x:include>
  <xsl:attribute name="href">
  <xsl:value-of select="concat($xsl_prefix, 'common/tablecolumnheader.xsl')"/>
  </xsl:attribute>
  </x:include>

  <x:include>
    <xsl:attribute name="href">
      <xsl:value-of select="concat($xsl_prefix, 'common/labelcolumn.xsl')"/>
    </xsl:attribute>
  </x:include>

  <x:include>
    <xsl:attribute name="href">
      <xsl:value-of select="concat($xsl_prefix, 'common/displayhelp.xsl')"/>
    </xsl:attribute>
  </x:include>

  <x:param name="create"/>
  <x:param name="contenttypekey"/>
  <x:param name="reload"/>
  <x:param name="csskey"/>
  <x:param name="expertcontributor"/>
  <x:param name="developer"/>
  <x:param name="accessToHtmlSource">
    <x:choose>
      <x:when test="$expertcontributor = 'true' or $developer = 'true'">
        <x:value-of select="true()"/>
      </x:when>
      <x:otherwise>
        <x:value-of select="false()"/>
      </x:otherwise>
    </x:choose>
  </x:param>

  <x:template match="/">

    <html>
      <head>
        <title>%headVerticalSite% - %txtContentRepository%</title>
        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/content_form.js">//</script>
        <script type="text/javascript" src="javascript/relatedcontent.js">//</script>
        <script type="text/javascript" src="javascript/accessrights.js">//</script>
        <script type="text/javascript" src="javascript/validate.js">//</script>
        <script type="text/javascript" src="javascript/tabpane.js">//</script>

        <script type="text/javascript" src="javascript/cms/core.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Event.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Cookie.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Css.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Dimensions.js">//</script>
        <script type="text/javascript" src="javascript/cms/ui/SplitButton.js">//</script>

        <script type="text/javascript" src="javascript/calendar_picker.js">//</script>
        <script type="text/javascript" src="javascript/menu.js">//</script>
        <script type="text/javascript" src="javascript/properties.js">//</script>

        <script type="text/javascript" src="tinymce/jscripts/cms/Util.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/cms/Editor.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/tiny_mce/tiny_mce.js">//</script>

        <x:if test="$subop != ''">
          <script type="text/javascript" src="javascript/window.js"/>
          <script type="text/javascript">
            cms.window.attatchKeyEvent('close');
          </script>
        </x:if>
        <link rel="stylesheet" type="text/css" href="javascript/tab.webfx.css"/>
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>

        <x:comment>[if IE]&gt;&lt;link href="css/admin_ie.css" rel="stylesheet" type="text/css"/&gt;&lt;![endif]</x:comment>

        <link rel="stylesheet" type="text/css" href="css/assignment.css"/>
        <link rel="stylesheet" type="text/css" href="javascript/cms/ui/style.css"/>
        <link rel="stylesheet" type="text/css" href="css/menu.css"/>
        <link rel="stylesheet" type="text/css" href="css/calendar_picker.css"/>

        <x:call-template name="waitsplash"/>

        <script type="text/javascript" language="JavaScript">
          var branchOpen = new Array();
          var cookiename = "contentform";
          var idx = 0;
          var validatedFields = new Array();

          <xsl:if test="//input[@name = /config/form/title/@name and not (@readonly = 'true')]">
              validatedFields[idx] = new Array("<xsl:value-of select="//input[@name = /config/form/title/@name]/display"/>", "<xsl:value-of select="/config/form/title/@name"/>", validateMaxLength);
              ++idx;
          </xsl:if>

          <xsl:for-each select="//input[@required = 'true' and not(@type = 'dropdown') and not(@type = 'date') and not(@type = 'radiobutton') and not(@type = 'checkbox') and not(@type = 'relatedcontent')]">
            <xsl:text>validatedFields[idx] = new Array("</xsl:text>
            <xsl:value-of select="display"/>
            <xsl:text>", "</xsl:text>
            <xsl:if test="@type = 'uploadfile'">
              <xsl:text>filename_</xsl:text>
            </xsl:if>
            <xsl:value-of select="@name"/>
            <xsl:text>", validateRequired</xsl:text>
            <xsl:if test="@type = 'images'">, true</xsl:if>
            <xsl:text>);</xsl:text>
            ++idx;
          </xsl:for-each>

          <xsl:for-each select="//input[@type = 'date']">
            <xsl:text>validatedFields[idx] = new Array("</xsl:text>
            <xsl:value-of select="display"/>
            <xsl:text>", "date</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>", validateDate);</xsl:text>
            ++idx;
            <xsl:if test="@required = 'true'">
              <xsl:text>validatedFields[idx] = new Array("</xsl:text>
              <xsl:value-of select="display"/>
              <xsl:text>", "date</xsl:text>
              <xsl:value-of select="@name"/>
              <xsl:text>", validateRequired);</xsl:text>
              ++idx;
            </xsl:if>
          </xsl:for-each>
          <xsl:for-each select="//input[@type = 'url']">
            <xsl:text>validatedFields[idx] = new Array("</xsl:text>
            <xsl:value-of select="display"/>
            <xsl:text>", "</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>", validateURL);</xsl:text>
            ++idx;
          </xsl:for-each>

          <xsl:for-each select="//input[@required = 'true' and @type = 'relatedcontent']">
            <xsl:text>validatedFields[idx] = new Array("</xsl:text>
            <xsl:value-of select="display"/>
            <xsl:text>", "</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>", </xsl:text>
            <xsl:choose>
              <xsl:when test="@multiple = 'false'">validateRequired</xsl:when>
              <xsl:otherwise>validateRelatedContent</xsl:otherwise>
            </xsl:choose>
            <xsl:text>);</xsl:text>
            ++idx;
          </xsl:for-each>

          <xsl:for-each select="//input[@required = 'true' and @type = 'dropdown']">
            <xsl:text>validatedFields[idx] = new Array("</xsl:text>
            <xsl:value-of select="display"/>
            <xsl:text>", "</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>", validateDropdown);</xsl:text>
            ++idx;
          </xsl:for-each>
          <xsl:for-each select="//input[@required = 'true' and @type = 'multiplechoice']">
            <xsl:text>validatedFields[idx] = new Array("</xsl:text>
            <xsl:value-of select="display"/>
            <xsl:text>", "</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>_checkbox</xsl:text>
            <xsl:text>", validateAtLeastOne);</xsl:text>
            ++idx;
            <xsl:text>validatedFields[idx] = new Array("</xsl:text>
            <xsl:value-of select="displayalternatives"/>
            <xsl:text>", "</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>_alternative</xsl:text>
            <xsl:text>", validateAllRequired);</xsl:text>
            ++idx;
          </xsl:for-each>
          <xsl:for-each select="//input[@validate = 'integer']">
            <xsl:text>validatedFields[idx] = new Array("</xsl:text>
            <xsl:value-of select="display"/>
            <xsl:text>", "</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>", validateInt);</xsl:text>
            ++idx;
          </xsl:for-each>
          <xsl:for-each select="//input[@type = 'xml']">
            <xsl:text>validatedFields[idx] = new Array("</xsl:text>
            <xsl:value-of select="display"/>
            <xsl:text>", "</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>", validateXML);</xsl:text>
            ++idx;
          </xsl:for-each>

          var customValidatedFields = new Array();

          /*
            Method: addCustomValidation
          */
          function addCustomValidation(fieldName, fieldDescription, regExp) {
            customValidatedFields[customValidatedFields.length] = new Array(fieldDescription, fieldName, validateCustom, regExp);
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Method: displayPublishTab
          */
          function displayPublishTab()
          {
            if ( document.getElementById('tab-page-publishing') )
              tabPane1.setSelectedPage('tab-page-publishing');
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Method: validateAll
          */
          function validateAll(formName) {
            var f = document.forms[formName];

            // Copy the content from all editor instances to their textareas.
            tinyMCE.triggerSave();

            <x:if test="not(/contents/userright) or /contents/userright/@publish = 'true'">
              if ( !checkAll(formName, extraValidatedFields, tabPane1 ) )
                return;
            </x:if>

            if ( !checkAll(formName, validatedFields, tabPane1) )
              return;

            if ( !checkAll(formName, customValidatedFields) )
              return;

            if ( f['date_pubdata_publishfrom'].value == '' &amp;&amp; f['time_pubdata_publishfrom'].value != '' )
            {
              alert('%msgTimeCanNotBeSetWithoutDate%');

              displayPublishTab();
              f['date_pubdata_publishfrom'].focus();
              return;
            }

            if ( f['date_pubdata_publishto'].value == '' &amp;&amp; f['time_pubdata_publishto'].value != '' )
            {
              alert('%msgTimeCanNotBeSetWithoutDate%');

              displayPublishTab();
              f['date_pubdata_publishto'].focus();
              return;
            }

            // dateTimeRangeValidator is instantiated in publishing.xsl
            if ( dateTimeRangeValidator.isStartDateTimeLaterThanOrSameAsEndDateTime() )
            {
              displayPublishTab();
              alert('%errOnlineFromIsLaterThanOnlineTo%');
              return;
            }

            checkRadioButtonGroupsForAnyChecked();
            addIndexFieldForEachRadioButtonGroup();

            disableFormButtons();
            f.submit();
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Method: destroyEditors
          */
          function destroyEditors(parent) {
            try {
              var textarea = parent.getElementsByTagName('textarea');
              var textareaLn = textarea.length;
              for (var i = 0; i &lt; textareaLn; i++) {
                if (textarea[i].className.indexOf('editor-textarea') &gt; -1) {
                  tinyMCE.execCommand('mceRemoveControl', true, textarea[i].id);
                }
              }
            } catch(err) {}

          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Method: rebuildEditor
          */
          function rebuildEditors(parent) {
            try {
              var textarea = parent.getElementsByTagName('textarea');
              var textareaLn = textarea.length;
              for (var i = 0; i &lt; textareaLn; i++) {
                if (textarea[i].className.indexOf('editor-textarea') &gt; -1) {
                  var s = textarea[i].className.split(' ');
                  var settings = eval(s[1]);
                  var ed = new tinymce.Editor(textarea[i].id, settings);
                  ed.render();
                }
              }
            } catch(err) { /**/ }
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Function: itemcount
          */
          function itemcount(elName) {
            var lItems;

            if (elName.length!=null) {
              lItems = elName.length;
            }
            else {
              lItems = 1;
            }

            return lItems;
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Function: GetCurrentObjectIndex
          */
          function GetCurrentObjectIndex(objThis) {
            var lNumRows = itemcount(document.formAdmin[objThis.name])
            if( lNumRows > 1 ) {
              for( var i=0; i &lt; lNumRows; i++ ) {
                if( document.formAdmin[objThis.name][i] == objThis ) {
                  return i;
                }
              }
            } else {
              return 0;
            }
          }
          // -------------------------------------------------------------------------------------------------------------------------------
          /*
            Function: __removeFile
          */
          function __removeFile( tableName, object, viewname, keyname )
          {
              var index = getObjectIndex(object);
              document.getElementsByName( viewname )[ index ].value = "";
              document.getElementsByName( keyname )[ index ].value = "";

              return;
          }
          // -------------------------------------------------------------------------------------------------------------------------------
          /*
            Function: __removeRow
          */
          function __removeRow( tableName, object, viewname, keyname ) {
	          count = itemcount(document.formAdmin[object.name]);
            var index;

            if ( count == 1 )
            {
              document.formAdmin[keyname].value = "";
              document.formAdmin[viewname].value = "";

              return;
            }
          
            index = GetCurrentObjectIndex(object)
            document.getElementById(tableName).deleteRow(index);
          }

          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Function: addRelatedFiles
          */
          function addRelatedFiles(fieldName, fieldRow, content_key, content_title ) {
            var keyField;
            var nameField;

            if (fieldRow == 'none') {
              keyField = document.getElementById(fieldName);
              nameField = document.getElementById('filename'+fieldName);
            } else {
              keyField = document.getElementsByName(fieldName)[fieldRow];
              nameField = document.getElementsByName('filename'+fieldName)[fieldRow];
            }
            keyField.value = content_key;
						nameField.value = content_title;
          }
        // ---------------------------------------------------------------------------------------------------------------------------------

          /*
            Function: GetCurrentObjectIndex
          */
          function insert_file_onclick(view, key, object, subop) {
            var row = getObjectIndex(object);
            OpenContentPopupByHandler(-1, -1, subop, key, row, "com.enonic.vertical.adminweb.handlers.ContentFileHandlerServlet");
          }

          /*
            Function: getGroupIndex

              This function takes an object as parameter, and returns its array
              index brackets (like "[1]") if there is more than one object with
              the same name. Otherwise it returns an empty string.
          */
          function getGroupIndex(object) {
            var lNumRows = itemcount(document.getElementsByName(object.name));
						if (lNumRows != 1) {
              for( var i=0; i &lt; lNumRows; i++ ) {
                if( document.getElementsByName(object.name)[i] == object ) {
                  return "["+i+"]";
                }
              }
            } else {
              return "";
            }
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Function: toggleCheckbox
          */
          function toggleCheckbox(obj, name) {
            var container;
            if (document.getElementsByName(name).length &gt; 1) {
              container = document.getElementsByName(name)[GetCurrentObjectIndex(obj)];
            } else {
              container = document.getElementsByName(name)[0];
            }
            if (container.value == 'true') {
              container.value = 'false';
            } else {
              container.value = 'true';
            }
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Function: GetCurrentIFrameIndex
          */
          function GetCurrentIFrameIndex(objThis) {
            var lNumRows = itemcount(document.getElementsByName(objThis.name))
            var iframes = document.getElementsByName(objThis.name);
            if( lNumRows > 1 ) {
              for( var i=0; i &lt; lNumRows; i++ ) {
                if( iframes[i] == objThis ) {
                  return i;
                }
              }
            } else {
              return 0;
            }
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Function: moveBlockUp
          */
          function moveBlockUp(name, fieldRow) {
            var parent = document.getElementById(name + '_table');

            var tb = document.getElementsByTagName('tbody');
            var blocks = new Array();
            for (var i = 0; i &lt; tb.length; i++) {
              if (tb[i].className == name + '_tbody') {
                blocks.push(tb[i]);
              }
            }

            var thisBlock = blocks[fieldRow];
            var numBlocks = blocks.length;
            if (fieldRow == 0) {
              return;
            }

            var insertBeforeBlock = blocks[fieldRow - 1];

            // *****************************************************************************************************************************
            // *** WYSIWYG Editor
            // *****************************************************************************************************************************
            // Since moving iframes/editors around in the DOM does not work very in all browseres well we
            // need to remove them and then rebuild them.

            if (!document.all) {
              // Destroy editors in the current block. Only needed for Fx.
              destroyEditors(thisBlock);

              // Destroy editors before block.
              destroyEditors(insertBeforeBlock);
            }

            // *********************************************************************************************************
            // *** Radiobuttons note!
            // *********************************************************************************************************
            // IE has a serious bug regarding checked state when moving radiobuttons.
            // Each time a radiobutton group is moved in the DOM, IE resets the checked state.
            markRadioButtonAsChecked(thisBlock);
            markRadioButtonAsChecked(insertBeforeBlock);

            // Swap the block position.
            parent.insertBefore(thisBlock, insertBeforeBlock);

            if (!document.all) {
              // Rebuild editors the current editor.
              rebuildEditors(thisBlock);

              // Rebuild editors in the sibling editor.
              rebuildEditors(insertBeforeBlock);
            }

            // *****************************************************************************************************************************
            // END: WYSIWYG Editor
            // *****************************************************************************************************************************

            // *********************************************************************************************************
            // *** Radiobuttons note!
            // *********************************************************************************************************
            // IE has a serious bug regarding checked state when moving radiobuttons.
            // Each time a radiobutton group is moved in the DOM, IE resets the checked state.
            // This restores the checked state back to what it was before the block was moved.
            restoreRadioButtonCheckState(thisBlock);
            restoreRadioButtonCheckState(insertBeforeBlock);

            // Enable and disable move buttons.
            setGroupButtonsDisabled(name);
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Method: moveBlockDown
          */
          function moveBlockDown(name, fieldRow) {
            var parent = document.getElementById(name + '_table');

            var tb = document.getElementsByTagName('tbody');
            var blocks = new Array();
            for (var i = 0; i &lt; tb.length; i++) {
              if (tb[i].className == name + '_tbody') {
                blocks.push(tb[i]);
              }
            }

            var thisBlock = blocks[fieldRow];
            var numBlocks = blocks.length;

            if (fieldRow &gt;= (numBlocks - 1)) {
              return
            }

            var insertAfterBlock = blocks[fieldRow + 1];

            // *****************************************************************************************************************************
            // *** WYSIWYG Editor
            // *****************************************************************************************************************************
            // Since moving iframes/editors around in the DOM does not work very in all browseres well we
            // need to remove them and then rebuild them.

            if (!document.all) {
              // Destroy editors in the current block.
              destroyEditors(thisBlock);

              // Destroy editors in the before block.
              destroyEditors(insertAfterBlock);
            }

            markRadioButtonAsChecked(thisBlock);
            markRadioButtonAsChecked(insertAfterBlock);

            // Swap block positions.
            parent.insertBefore(insertAfterBlock, thisBlock);

            if (!document.all) {
              // Rebuild editors the current editor.
              rebuildEditors(thisBlock);

              // Rebuild editors in the before block.
              rebuildEditors(insertAfterBlock);
            }

            // *****************************************************************************************************************************
            // END: WYSIWYG Editor
            // *****************************************************************************************************************************

            // *********************************************************************************************************
            // *** Radiobuttons note!
            // *********************************************************************************************************
            // IE has a serious bug regarding checked state when moving radiobuttons.
            // Each time a radiobutton group is moved in the DOM, IE resets the checked state.
            // This restores the checked state back to what it was before the block was moved.
            restoreRadioButtonCheckState(thisBlock);
            restoreRadioButtonCheckState(insertAfterBlock);

            // Enable and disable move buttons.
            setGroupButtonsDisabled(name);
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Method: setGroupButtonsDisabled
          */
          function setGroupButtonsDisabled(name) {

            var tb = document.getElementsByTagName('tbody');
            var blocks = new Array();
            for (var i = 0; i &lt; tb.length; i++) {
              if (tb[i].className == name + '_tbody') {
                blocks.push(tb[i]);
              }
            }

            var numBlocks = blocks.length;

            var addButtons = document.getElementsByName(name+"_addblockbutton");
            var removeButtons = document.getElementsByName(name+"_removeblockbutton");
            var moveUpButtons = document.getElementsByName(name+"_moveblockupbutton");
            var moveDownButtons = document.getElementsByName(name+"_moveblockdownbutton");

            // rows in between
            for (var i=0; i &lt; numBlocks; i++) {
              setImageButtonEnabled(removeButtons[i], (numBlocks &gt; 1));
              setImageButtonEnabled(moveUpButtons[i], (i &gt; 0));
              setImageButtonEnabled(moveDownButtons[i], (i &lt; numBlocks - 1));
              addButtons[i].blur();
              removeButtons[i].blur();
              moveUpButtons[i].blur();
              moveDownButtons[i].blur();
            }
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          /*
            Method: addBlockGroup

            Parameters:

              name - String, group name.
              fieldRow - Integer, row index of the block to insert after.
          */
          function addBlockGroup(name, fieldRow) {
            var tb = document.getElementsByTagName('tbody');
            var groupName = name;

            var tBody = new Array();
            for (var i = 0; i &lt; tb.length; i++) {
              if (tb[i].className == name + '_tbody') {
                tBody.push(tb[i]);
              }
            }

            var selected;
            if (tBody.length &amp;&amp; tBody.length &gt; 0) {
              selected = tBody[fieldRow];
            } else {
              selected = tBody[0];
            }

            var parent = document.getElementById(name + '_table');
            var insertAfterObj = selected;

            // *****************************************************************************************************************************
            // WYSIWYG Editor
            // *****************************************************************************************************************************
            // Since some browsers does not handle cloned WYSIWYG editors(iframes) very well we have to remove the
            // editor and build it up again.

            var textarea, textareaLn;

            // Remove all instances of the editor.
            try {
              textarea = selected.getElementsByTagName('textarea');
              textareaLn = textarea.length;
              for (var i = 0; i &lt; textareaLn; i++) {
                if (textarea[i].className.indexOf('editor-textarea') &gt; -1)
                  tinyMCE.execCommand('mceRemoveControl', false, textarea[i].id);
              }
            } catch(err) { /**/ }

            // Clone the block.
            var newTBody = selected.cloneNode(true);

            // Radio buttons needs a new name to separate the rb groups from the original block.
            renameRadioButtonsInNewBlock(newTBody);
            removeRadioButtonGroupCounterFromBlock(newTBody);

            // Rebuild the editors in the original.
            try {
              textarea = selected.getElementsByTagName('textarea');
              textareaLn = textarea.length;
              for (var i = 0; i &lt; textareaLn; i++) {
                if (textarea[i].className.indexOf('editor-textarea') &gt; -1) {
                  var s = textarea[i].className.split(' ');
                  var settings = eval(s[1]);
                  var ed = new tinymce.Editor(textarea[i].id, settings);
                  ed.render();
                }
              }
            } catch(err) { /**/ }

            // Insert the clone.
            parent.insertBefore(newTBody,insertAfterObj.nextSibling);

            // Build the editors in the new block.
            //try {
              textarea = newTBody.getElementsByTagName('textarea');
              textareaLn = textarea.length;

              for (var i = 0; i &lt; textareaLn; i++) {
                if (textarea[i].className.indexOf('editor-textarea') &gt; -1) {
                  textarea[i].value = '';
                  var s = textarea[i].className.split(' ');
                  var settings = eval(s[1]);

                  // Give it a new ID straight away.
                  var ran = Math.random();
                  var id = ran.toString().split('.');
                  var edKey = 'id_' + id[1];

                  var editorInitMsg, editorContainer  = null;
                  var cmsDiv = newTBody.getElementsByTagName('div');
                  for (var d = 0; d &lt; cmsDiv.length; d++) {
                    if (cmsDiv[d].id == 'editor_cms_init_msg_' + textarea[i].id) {
                      editorInitMsg = cmsDiv[d];
                    }
                    if (cmsDiv[d].id == 'editor_cms_container_' + textarea[i].id) {
                      editorContainer = cmsDiv[d];
                    }
                  }

                  editorInitMsg.id = 'editor_cms_init_msg_' + edKey;
                  editorContainer.id = 'editor_cms_container_' + edKey;

                  textarea[i].id = edKey;
                  var ed = new tinymce.Editor(edKey, settings);
                  ed.render();
                }
              }
            //} catch(err) { /**/ }
            // *****************************************************************************************************************************
            // END: WYSIWYG Editor
            // *****************************************************************************************************************************

            var idx = document.getElementsByName(name).length;

            // related content.
            var tableRows = newTBody.getElementsByTagName('tr');
            if (tableRows != null) {
              for(var i = 0; i &lt; tableRows.length; i++) {
                if (tableRows[i].id != null &amp;&amp; "relatedcontent" == tableRows[i].id.substring(0,14)) {
                  tableRows[i].parentNode.removeChild(tableRows[i]);
                  i = i - 1;
                }
              }
            }

            var inputs = newTBody.getElementsByTagName('input');

            for(var i = 0; i &lt; inputs.length; ++i) {

              if (inputs[i].getAttribute('type') == 'hidden' ) {

                // relatedcontent hack.. needs {name}_counter=0 variable
                if ( inputs[i].name.indexOf('_counter') &gt; -1 )
                {
                  inputs[i].value = 0;
                }

                if ( inputs[i].name.indexOf('_counter') == -1 )
                {
                  inputs[i].value = '';
                }

              } else if (inputs[i].getAttribute('type') != 'button') {
                if (inputs[i].getAttribute('type') == 'text') {

                // Get the default value described in the content type (not the initial input element value)

                if (document.getElementsByName('default_value_' + inputs[i].name)[fieldRow] &amp;&amp; document.getElementsByName('default_value_' + inputs[i].name)[fieldRow].value != '') {
                  var defaultVal = document.getElementsByName('default_value_' + inputs[i].name)[fieldRow].value;
                } else {
                  var defaultVal = '';
                }
                inputs[i].value = defaultVal;
              }

              // Reset checkboxes

              if (inputs[i].getAttribute('type') == 'checkbox') {
                inputs[i].checked = false;
              }
            }

            if (inputs[i].getAttribute('type') == 'radio') {
              // inputs[i].name = inputs[i].name + idx;
            }
          }

          var hrefs = newTBody.getElementsByTagName('a');

          for (var i = 0; i &lt; hrefs.length; ++i) {
            if (hrefs[i].name.indexOf('_link') != -1)
                hrefs[i].parentNode.removeChild(hrefs[i]);
          }

          var divs = newTBody.getElementsByTagName('img');

          for (var i = 0; i &lt; divs.length; ++i) {
            if (divs[i].name == 'imagepreview')
              divs[i].parentNode.removeChild(divs[i]);
          }

          var inputs = newTBody.getElementsByTagName('textarea');
          for(var i = 0; i &lt; inputs.length; ++i) {

            // Check if default value is decleared in the content type.
            // Since HTML Textarea does not have a default value attribute we store this in a hidden input field.
            var defaultTextAreaValue = document.getElementsByName('default_value_' + inputs[i].name)[fieldRow];

            var textAreaValue;

            if (defaultTextAreaValue &amp;&amp; defaultTextAreaValue.value != '') {
              textAreaValue = defaultTextAreaValue.value;
            } else {
              textAreaValue = '';
            }

            inputs[i].value = textAreaValue;
          }

          var div = newTBody.getElementsByTagName('div');
          var divLn = div.length;
          for (var i = 0; i &lt; divLn; i++) {
            if (div[i].id.substring(0,3) == 'div') {
              div[i].innerHTML = '';
            }
          }

          setGroupButtonsDisabled(groupName);
        }
        // ---------------------------------------------------------------------------------------------------------------------------------

        /*
          Method: removeBlockGroup
        */
        function removeBlockGroup(name, fieldRow) {
          var tb = document.getElementsByTagName('tbody');
          var blocks = new Array();
          for (var i = 0; i &lt; tb.length; i++) {
            if (tb[i].className == name + '_tbody') {
              blocks.push(tb[i]);
            }
          }

          var thisBlock = blocks[fieldRow];
          var numBlocks = blocks.length;

          // *******************************************************************************************************************************
          // *** WYSIWYG Editor
          // *******************************************************************************************************************************
          // Remove editor instances.
          destroyEditors(thisBlock);
          // *******************************************************************************************************************************
          // // WYSIWYG Editor
          // *******************************************************************************************************************************

          if (numBlocks &gt; 1) {
            thisBlock.parentNode.removeChild(thisBlock);
          } else {
            // Remove multiple related contents (removes all rows that starts with relatedcontent_)
            var tableRows = thisBlock.getElementsByTagName('tr');
            for(var i = 0; i &lt; tableRows.length; i++) {
              if (tableRows[i].id != null &amp;&amp; "relatedcontent" == tableRows[i].id.substring(0,14)) {
                tableRows[i].parentNode.removeChild(tableRows[i]);
                i = i - 1;
              }
            }

						// Reset dropdowns
						var selects = thisBlock.getElementsByTagName('select');
            for(var i = 0; i &lt; selects.length; i++) {
						  selects[i].selectedIndex = 0;
            }

            var hrefs = thisBlock.getElementsByTagName('a');
						for (var i = 0; i &lt; hrefs.length; ++i) {
						  if (hrefs[i].name.indexOf('_link') != -1)
                hrefs[i].parentNode.removeChild(hrefs[i]);
            }

            var inputs = thisBlock.getElementsByTagName('input');
            for(var i = 0; i &lt; inputs.length; ++i) {
						  // relatedcontent hack.. needs {name}_counter=0 variable
              if (inputs[i].getAttribute('type') == 'hidden' &amp;&amp; inputs[i].name.indexOf('_counter') &gt; -1) {
		            inputs[i].value = 0;
              } else {
							  inputs[i].value = '';
              }
            }

            var inputs = thisBlock.getElementsByTagName('textarea');
            for(var i = 0; i &lt; inputs.length; ++i) {
              inputs[i].value = '';
            }
          }
          setGroupButtonsDisabled(name);
        }
        // ---------------------------------------------------------------------------------------------------------------------------------

        /*
          Method: moveTableRowUpFS

            Moves a related file row up.
        */
        function moveTableRowUpFS(tbName, object) {
          var index = getObjectIndex(object);
          var tBody = document.getElementById(tbName);
          if (!tBody)
            return;

          var tr = tBody.getElementsByTagName('tr');

          var row1, row2;
          if (index == 0) {
            row1 = tr[index];
            row2 = tr[tr.length];
          } else {
            row1 = tr[index];
            row2 = tr[(index-1)];
          }
          tBody.insertBefore(row1, row2);

        }
        // ---------------------------------------------------------------------------------------------------------------------------------

        /*
          Method: moveTableRowDownFS

            Moves a related file row down.
        */
        function moveTableRowDownFS(tbName, object) {
          var index = getObjectIndex(object);
          var tBody = document.getElementById(tbName);
          if (!tBody)
            return;

          var tr = tBody.getElementsByTagName('tr');

          if ((index+1) == tr.length) {
            row1 = tr[index];
            row2 = tr[0];
          } else {
            var row1 = tr[(index+1)];
            var row2 = tr[index];
          }
          tBody.insertBefore(row1, row2);
        }
        // ---------------------------------------------------------------------------------------------------------------------------------

        </script>
      </head>

      <body>

      <x:variable name="readonly" select="not($enableform)"/>

      <x:if test="not($readonly)">
        <script type="text/javascript">waitsplash();</script>
      </x:if>

      <x:call-template name="version-form">
        <x:with-param name="contentxpath" select="/contents/content"/>
        <x:with-param name="referer" select="$referer"/>
      </x:call-template>

      <form name="formAdmin" method="post" target="_blank" enctype="multipart/form-data">
        <x:attribute name="action">
          <x:if test="$create=1">
            <x:text>adminpage?page=</x:text>
            <x:value-of select="$page"/>
            <x:text>&amp;op=create</x:text>
            <x:text>&amp;cat=</x:text>
            <x:value-of select="$cat"/>
          </x:if>
          <x:if test="$create=0">
            <x:text>adminpage?page=</x:text>
            <x:value-of select="$page"/>
            <x:text>&amp;op=update</x:text>
            <x:text>&amp;cat=</x:text>
            <x:value-of select="$cat"/>
          </x:if>
        </x:attribute>

        <input type="hidden" name="titleformkey" value="{/config/form/title/@name}"/>

        <x:call-template name="generalhiddenfields"/>

        <x:call-template name="contentheader">
          <x:with-param name="links" select="not($subop = 'popup')"/>
          <x:with-param name="subop" select="$subop"/>
          <x:with-param name="fieldrow" select="$fieldrow"/>
          <x:with-param name="fieldname" select="$fieldname"/>
          <x:with-param name="minoccurrence" select="$minoccurrence"/>
          <x:with-param name="maxoccurrence" select="$maxoccurrence"/>
        </x:call-template>

        <x:call-template name="displayfeedback">
          <x:with-param name="addbr" select="true()"/>
        </x:call-template>

        <x:call-template name="contentformbuttons">
          <x:with-param name="enablepreview" select="true()"/>
          <x:with-param name="topbuttons" select="true()"/>
        </x:call-template>

        <x:call-template name="assignee-status">
          <x:with-param name="content" select="/contents/content"/>
          <x:with-param name="selected-version-is-unsaved-draft" select="not(/contents/content/@state) or ( $editlockedversionmode and not(/contents/content/@has-draft = 'true') )"/>
          <x:with-param name="editlockedversionmode" select="$editlockedversionmode"/>
        </x:call-template>

        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td class="form_title_form_seperator">
              <img src="images/1x1.gif"/>
            </td>
          </tr>
          <tr>
            <td>
              <div class="tab-pane" id="tab-container">
                <script type="text/javascript" language="JavaScript">
                  <xsl:text>var tabPane1 = new WebFXTabPane( document.getElementById( "tab-container" ), true);</xsl:text>
                </script>
                <div class="tab-page" id="tab-page-1">
                  <span class="tab">%blockContent%</span>
                  <script type="text/javascript" language="JavaScript">
                    <xsl:text>tabPane1.addTabPage( document.getElementById( "tab-page-1" ));</xsl:text>
                  </script>
                  <x:call-template name="statusAndVersionstuff"/>
                  <xsl:call-template name="generateform"/>
                </div>
                <!--x:if test="/contents/content/@status != 1"-->
                  <x:call-template name="properties"/>
                  <x:call-template name="publishing"/>
                  <x:call-template name="content_accessrights"/>
                  <x:call-template name="content_usedby">
                    <x:with-param name="contentKey" select="/contents/content/@key"/>
                  </x:call-template>
                <!--/x:if-->
                <x:call-template name="content_source"/>
              </div>
              <script type="text/javascript">
                setupAllTabs();
              </script>
            </td>
          </tr>
          <tr>
            <td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
          </tr>
        </table>
        <x:call-template name="contentformbuttons">
          <x:with-param name="enablepreview" select="true()"/>
        </x:call-template>
      </form>

        <x:if test="not($readonly)">
          <!-- er block påkrevd i cty-en? -->
          <xsl:if test="count(/config/form/block/input[@type = 'htmlarea']) = 0">
            <script type="text/javascript">removeWaitsplash();</script>
          </xsl:if>
        </x:if>

        <x:variable name="contenttype-title-default-value">
          <xsl:value-of select="/config/form/descendant::input[@name = /config/form/title/@name]/default"/>
        </x:variable>

      </body>
    </html>
  </x:template>

  <x:template name="multiplechoice_alternative">
    <x:param name="name"/>
    <x:param name="input"/>
    <x:param name="size" select="30"/>
    <x:param name="alternatives"/>
    <x:param name="iteration" select="1"/>
    <x:param name="readonly"/>
    <x:if test="$iteration &lt;= $alternatives">
      <tr>
        <td style="text-align:center">
          <input type="checkbox" onclick="storeCheckboxStates()">
            <x:attribute name="name">
              <x:value-of select="$name"/>
              <x:text>_checkbox</x:text>
            </x:attribute>
            <x:if test="not($input = '') and $input[$iteration]/@correct = 'true'">
              <x:attribute name="checked">checked</x:attribute>
            </x:if>
            <x:if test="$readonly">
              <x:attribute name="disabled">disabled</x:attribute>
            </x:if>
          </input>
          <input type="hidden">
            <x:attribute name="name">
              <x:value-of select="$name"/>
              <x:text>_checkbox_values</x:text>
            </x:attribute>
            <x:if test="not($input = '') and $input[$iteration]/@correct = 'true'">
              <x:attribute name="value">true</x:attribute>
            </x:if>
          </input>
        </td>
        <td>
          <input type="text">
            <x:attribute name="size">
              <x:value-of select="$size"/>
            </x:attribute>
            <x:attribute name="name">
              <x:value-of select="$name"/>
              <x:text>_alternative</x:text>
            </x:attribute>
            <x:if test="not($input = '') and $input[$iteration]">
              <x:attribute name="value">
                <x:value-of select="$input[$iteration]"/>
              </x:attribute>
            </x:if>
            <x:if test="$readonly">
              <x:attribute name="disabled">disabled</x:attribute>
            </x:if>
          </input>
        </td>
        <td>
          <x:call-template name="button">
            <x:with-param name="name" select="concat('moverdowner_', $name)"/>
            <x:with-param name="image" select="'images/icon_move_down.gif'"/>
            <x:with-param name="type" select="'button'"/>
            <x:with-param name="disabled" select="$readonly"/>
            <x:with-param name="onclick">
              <x:text>javascript:moveTableRowDown('</x:text>
              <x:value-of select="$name"/>
              <x:text>_table', getObjectIndex(this) + 1);setDisabledEnabledButtons('</x:text>
              <x:value-of select="$name"/>
              <x:text>');updateCheckboxStates();</x:text>
            </x:with-param>
          </x:call-template>
          <x:call-template name="button">
            <x:with-param name="name" select="concat('moverupper_', $name)"/>
            <x:with-param name="image" select="'images/icon_move_up.gif'"/>
            <x:with-param name="type" select="'button'"/>
            <x:with-param name="disabled" select="$readonly"/>
            <x:with-param name="onclick">
              <x:text>javascript:moveTableRowUp('</x:text>
              <x:value-of select="$name"/>
              <x:text>_table', getObjectIndex(this) + 1);setDisabledEnabledButtons('</x:text>
              <x:value-of select="$name"/>
              <x:text>');updateCheckboxStates();</x:text>
            </x:with-param>
          </x:call-template>
          <x:call-template name="button">
            <x:with-param name="name" select="'removeButton'"/>
            <x:with-param name="image" select="'images/icon_remove.gif'"/>
            <x:with-param name="type" select="'button'"/>
            <x:with-param name="disabled" select="$readonly"/>
            <x:with-param name="onclick">
              <x:text>javascript:clearOrRemove(this);setDisabledEnabledButtons('</x:text>
              <x:value-of select="$name"/>
              <x:text>');updateCheckboxStates();</x:text>
            </x:with-param>
          </x:call-template>
        </td>
      </tr>
      <x:call-template name="multiplechoice_alternative">
        <x:with-param name="name" select="$name"/>
        <x:with-param name="size" select="$size"/>
        <x:with-param name="input" select="$input"/>
        <x:with-param name="alternatives" select="$alternatives"/>
        <x:with-param name="iteration" select="$iteration + 1"/>
        <x:with-param name="readonly" select="$readonly"/>
      </x:call-template>
    </x:if>
  </x:template>

  <x:template name="contenttypeform"/>
  </x:stylesheet>
  </xsl:template>

  <xsl:template name="generateform">
    <!-- Add custom validation fields -->
    <script type="text/javascript" language="JavaScript">
      <xsl:for-each select="/config/form/block/input[regexp != '' and @type = 'text']">
        <xsl:text>addCustomValidation("</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>", "</xsl:text>
        <xsl:value-of select="display"/>
        <xsl:text>", "</xsl:text>
        <xsl:call-template name="replaceSubstring">
          <xsl:with-param name="inputString" select="regexp"/>
          <xsl:with-param name="inputSubstring" select="'\'"/>
          <xsl:with-param name="outputSubstring" select="'\\'"/>
        </xsl:call-template>
        <xsl:text>");</xsl:text>
      </xsl:for-each>
    </script>

    <xsl:for-each select="/config/form/block">
      <xsl:variable name="groupname" select="concat('group', position())"/>
      <xsl:variable name="grouphelpelem" select="help"/>

      <fieldset>
        <legend>
          <xsl:value-of select="@name"/>

          <xsl:if test="$grouphelpelem">
            <a title="Click to view"
                 style="margin: 0 2px; vertical-align: bottom"
                 onclick="showHideHelp(this, '{$groupname}')"
                 class="hand"
                 name="{$groupname}_helplink"
                 id="{$groupname}_helplink">
              <img id="{$groupname}_helpicon" style="vertical-align: bottom;" src="images/icon_help_compact.gif"/>
            </a>
          </xsl:if>
        </legend>

        <xsl:if test="$grouphelpelem">
          <x:call-template name="displayhelp">
            <x:with-param name="fieldname">
              <xsl:value-of select="$groupname"/>
            </x:with-param>
            <x:with-param name="helpelement">
              <xsl:copy-of select="$grouphelpelem"/>
            </x:with-param>
          </x:call-template>
        </xsl:if>

        <!-- div id="{concat($groupname,'_div')}" style="display:none">This block has been folded. Click <a href="javascript:;" onclick="blockFold('{$groupname}')">here</a> to unfold.</div-->

        <table class="block" border="0" width="100%" id="{concat($groupname,'_table')}" name="{concat($groupname,'_table')}">
          <xsl:choose>
            <xsl:when test="@group">
              <x:choose>
                <x:when>
                  <xsl:attribute name="test">
                    <xsl:value-of select="concat('/contents/content[1]/', @group)"/>
                  </xsl:attribute>
                  <x:for-each>
                    <xsl:attribute name="select">
                      <xsl:value-of select="concat('/contents/content[1]/', @group)"/>
                    </xsl:attribute>
                    <tbody class="{concat($groupname,'_tbody')}">
                      <xsl:for-each select="input">
                        <xsl:call-template name="displayinput">
                          <xsl:with-param name="input" select="."/>
                        </xsl:call-template>
                      </xsl:for-each>
                      <tr>
                        <td colspan="5" align="right">
                          <input type="hidden" name="{concat($groupname, '_counter')}" value=""/>

                          <!-- Default value hack for FF &lt;= 2 -->
                          <xsl:for-each select="input[@type = 'text' or @type='textarea' and default !='']">
                            <input type="hidden" name="default_value_{@name}" value="{default}"/>
                          </xsl:for-each>

                          <x:call-template name="button">
                            <x:with-param name="type" select="'button'"/>
                            <x:with-param name="image" select="'images/icon_move_up.gif'"/>
                            <x:with-param name="name" select="'{$groupname}_moveblockupbutton'"/>
                            <x:with-param name="tooltip" select="'%cmdMoveBlockUp%'"/>
                            <x:with-param name="disabled" select="$readonly"/>
                            <x:with-param name="onclick">
                              <x:text>javascript:moveBlockUp('<xsl:value-of select="$groupname"/><xsl:text>', getObjectIndex(this));</xsl:text>
                              </x:text>
                            </x:with-param>
                          </x:call-template>
                          <x:call-template name="button">
                            <x:with-param name="type" select="'button'"/>
                            <x:with-param name="image" select="'images/icon_move_down.gif'"/>
                            <x:with-param name="name" select="'{$groupname}_moveblockdownbutton'"/>
                            <x:with-param name="disabled" select="$readonly"/>
                            <x:with-param name="tooltip" select="'%cmdMoveBlockDown%'"/>
                            <x:with-param name="onclick">
                              <x:text>javascript:moveBlockDown('<xsl:value-of select="$groupname"/><xsl:text>', getObjectIndex(this));</xsl:text>
                              </x:text>
                            </x:with-param>
                          </x:call-template>
                          <x:call-template name="button">
                            <x:with-param name="type" select="'button'"/>
                            <x:with-param name="image" select="'images/icon_remove.gif'"/>
                            <x:with-param name="name" select="'{$groupname}_removeblockbutton'"/>
                            <x:with-param name="disabled" select="$readonly"/>
                            <x:with-param name="tooltip" select="'%cmdRemoveBlock%'"/>
                            <x:with-param name="onclick">
                              <x:text>javascript:removeBlockGroup('<xsl:value-of select="$groupname"/><xsl:text>', getObjectIndex(this));</xsl:text>
                              </x:text>
                            </x:with-param>
                          </x:call-template>
                        </td>
                      </tr>
                      <tr>
                        <td colspan="5">
                          <table border="0" cellspacing="0" cellpadding="0" style="width:100%">
                            <tr>
                              <td style="width: 12px; white-space: nowrap;border-bottom: 1px solid #CCCCCC;">
                                <img height="5" width="12" src="images/shim.gif"/>
                              </td>
                              <td style="padding: 0px 5px 0px 5px; white-space: nowrap;" rowspan="2">
                                <x:call-template name="button">
                                  <x:with-param name="type" select="'button'"/>
                                  <x:with-param name="image" select="'images/icon_plus.gif'"/>
                                  <x:with-param name="name" select="'{$groupname}_addblockbutton'"/>
                                  <x:with-param name="disabled" select="$readonly"/>
                                  <x:with-param name="tooltip" select="'%cmdAddBlock%'"/>
                                  <x:with-param name="onclick">
                                    <x:text>javascript:addBlockGroup('<xsl:value-of select="$groupname"/><xsl:text>', getObjectIndex(this));</xsl:text>
                                    </x:text>
                                  </x:with-param>
                                </x:call-template>
                              </td>
                              <td style="width: 100%;white-space: nowrap;border-bottom: 1px solid #CCCCCC;">
                                <img class="shim" src="images/shim.gif"/>
                              </td>
                            </tr>
                            <tr>
                              <td>
                                <img height="5" width="1" src="images/shim.gif"/>
                              </td>
                              <td>
                                <img src="images/shim.gif"/>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </tbody>
                  </x:for-each>
                </x:when>
                <x:otherwise>
                  <tbody class="{concat($groupname,'_tbody')}">
                    <xsl:for-each select="input">
                      <xsl:call-template name="displayinput">
                        <xsl:with-param name="input" select="."/>
                      </xsl:call-template>
                    </xsl:for-each>
                    <tr>
                      <td colspan="5" align="right">
                        <input type="hidden" name="{concat($groupname, '_counter')}" value=""/>

                        <!-- Default value hack for FF &lt;= 2 -->
                        <xsl:for-each select="input[@type = 'text' or @type = 'textarea' and default !='']">
                          <input type="hidden" name="default_value_{@name}" value="{default}"/>
                        </xsl:for-each>

                        <x:call-template name="button">
                          <x:with-param name="type" select="'button'"/>
                          <x:with-param name="image" select="'images/icon_move_up.gif'"/>
                          <x:with-param name="name" select="'{$groupname}_moveblockupbutton'"/>
                          <x:with-param name="tooltip" select="'%cmdMoveBlockUp%'"/>
                          <x:with-param name="disabled" select="$readonly"/>
                          <x:with-param name="onclick">
                            <x:text>javascript:moveBlockUp('<xsl:value-of select="$groupname"/>', getObjectIndex(this));
                            </x:text>
                          </x:with-param>
                        </x:call-template>
                        <x:call-template name="button">
                          <x:with-param name="type" select="'button'"/>
                          <x:with-param name="image" select="'images/icon_move_down.gif'"/>
                          <x:with-param name="name" select="'{$groupname}_moveblockdownbutton'"/>
                          <x:with-param name="tooltip" select="'%cmdMoveBlockDown%'"/>
                          <x:with-param name="disabled" select="$readonly"/>
                          <x:with-param name="onclick">
                            <x:text>javascript:moveBlockDown('<xsl:value-of select="$groupname"/>',
                              getObjectIndex(this));
                            </x:text>
                          </x:with-param>
                        </x:call-template>
                        <x:call-template name="button">
                          <x:with-param name="type" select="'button'"/>
                          <x:with-param name="image" select="'images/icon_remove.gif'"/>
                          <x:with-param name="name" select="'{$groupname}_removeblockbutton'"/>
                          <x:with-param name="tooltip" select="'%cmdRemoveBlock%'"/>
                          <x:with-param name="disabled" select="$readonly"/>
                          <x:with-param name="onclick">
                            <x:text>javascript:removeBlockGroup('<xsl:value-of select="$groupname"/>',
                              getObjectIndex(this));
                            </x:text>
                          </x:with-param>
                        </x:call-template>
                      </td>
                    </tr>
                    <tr>
                      <td colspan="5">
                        <table border="0" cellspacing="0" cellpadding="0" style="width:100%">
                          <tr>
                            <td style="width: 12px; white-space: nowrap;border-bottom: 1px solid #CCCCCC;">
                              <img height="5" width="12" src="images/shim.gif"/>
                            </td>
                            <td style="padding: 0px 5px 0px 5px; white-space: nowrap;" rowspan="2">
                              <x:call-template name="button">
                                <x:with-param name="type" select="'button'"/>
                                <x:with-param name="image" select="'images/icon_plus.gif'"/>
                                <x:with-param name="name" select="'{$groupname}_addblockbutton'"/>
                                <x:with-param name="tooltip" select="'%cmdAddBlock%'"/>
                                <x:with-param name="disabled" select="$readonly"/>
                                <x:with-param name="onclick">
                                  <x:text>javascript:addBlockGroup('<xsl:value-of select="$groupname"/>',
                                    getObjectIndex(this));
                                  </x:text>
                                </x:with-param>
                              </x:call-template>
                            </td>
                            <td style="width: 100%;white-space: nowrap;border-bottom: 1px solid #CCCCCC;">
                              <img class="shim" src="images/shim.gif"/>
                            </td>
                          </tr>
                          <tr>
                            <td>
                              <img height="5" width="1" src="images/shim.gif"/>
                            </td>
                            <td>
                              <img src="images/shim.gif"/>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </tbody>
                </x:otherwise>
              </x:choose>
              <x:if test="not($readonly)">
                <script type="text/javascript" language="JavaScript">
                  <xsl:text>setGroupButtonsDisabled('</xsl:text><xsl:value-of select="$groupname"/><xsl:text>');</xsl:text>
                </script>
              </x:if>
            </xsl:when>
            <xsl:otherwise>
              <xsl:for-each select="input">
                <xsl:variable name="temp_input">
                  <xsl:call-template name="appended_input">
                    <xsl:with-param name="input" select="."/>
                  </xsl:call-template>
                </xsl:variable>
                <xsl:call-template name="displayinput">
                  <xsl:with-param name="input" select="exslt-common:node-set($temp_input)/input"/>
                </xsl:call-template>
              </xsl:for-each>
            </xsl:otherwise>
          </xsl:choose>
        </table>
      </fieldset>
    </xsl:for-each>
  </xsl:template>

  <xsl:template name="displayinput">
    <xsl:param name="input"/>

    <xsl:choose>
      <xsl:when test="$input/@readonly = 'true' and ($input/@type = 'text' or $input/@type = 'date' or $input/@type = 'url' or $input/@type = 'xml' or $input/@type = 'textarea')">
        <xsl:call-template name="displayreadonly">
          <xsl:with-param name="input" select="$input"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$input/@type = 'text'">
        <xsl:call-template name="displaytext">
          <xsl:with-param name="input" select="$input"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$input/@type = 'textarea'">
        <xsl:call-template name="displaytextarea">
          <xsl:with-param name="input" select="$input"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$input/@type = 'xml'">
        <xsl:call-template name="displaytextarea">
          <xsl:with-param name="input" select="$input"/>
          <xsl:with-param name="xml" select="'true'"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$input/@type = 'simplehtmlarea'">
        <xsl:call-template name="displaysimplehtmlarea">
          <xsl:with-param name="input" select="$input"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$input/@type = 'htmlarea'">
        <xsl:call-template name="displayhtmlarea">
          <xsl:with-param name="input" select="$input"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$input/@type = 'images'">
        <xsl:call-template name="displayimages">
          <xsl:with-param name="input" select="$input"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$input/@type = 'uploadfile'">
        <xsl:call-template name="uploadfile">
          <xsl:with-param name="input" select="$input"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$input/@type = 'image'">
        <xsl:call-template name="displayimage">
          <xsl:with-param name="input" select="$input"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$input/@type = 'files'">
        <xsl:call-template name="displayfiles">
          <xsl:with-param name="input" select="$input"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$input/@type = 'file'">
        <xsl:call-template name="displayfile">
          <xsl:with-param name="input" select="$input"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$input/@type = 'relatedcontent'">
        <xsl:variable name="is-multiple" select="$input/@multiple = 'true' or not($input/@multiple)"/>
        <xsl:choose>
          <xsl:when test="$is-multiple">
            <xsl:call-template name="relatedcontent">
              <xsl:with-param name="input" select="$input"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="relatedcontent_multiple_false">
              <xsl:with-param name="input" select="$input"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>

      <xsl:when test="$input/@type = 'checkbox'">
        <xsl:call-template name="checkbox">
          <xsl:with-param name="input" select="$input"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$input/@type = 'dropdown'">
        <xsl:call-template name="dropdown">
          <xsl:with-param name="input" select="$input"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$input/@type = 'date'">
        <xsl:call-template name="displaydatefield">
          <xsl:with-param name="input" select="$input"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$input/@type = 'url'">
        <xsl:call-template name="displaytext">
          <xsl:with-param name="input" select="$input"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$input/@type = 'radiobutton'">
        <xsl:call-template name="displayradiobutton">
          <xsl:with-param name="input" select="$input"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="$input/@type = 'multiplechoice'">
        <xsl:call-template name="displaymultiplechoice">
          <xsl:with-param name="input" select="$input"/>
        </xsl:call-template>
      </xsl:when>

    </xsl:choose>
  </xsl:template>

  <xsl:template name="checkbox">
    <xsl:param name="input"/>
    <tr>
      <x:call-template name="labelcolumn">
        <x:with-param name="label">
          <xsl:value-of select="$input/display"/>
          <xsl:text>:</xsl:text>
        </x:with-param>
        <x:with-param name="fieldname">
            <xsl:value-of select="$input/@name"/>
        </x:with-param>
          <xsl:if test="$input/help">
              <x:with-param name="helpelement">
                  <xsl:copy-of select="$input/help"/>
              </x:with-param>
          </xsl:if>
      </x:call-template>
      <td valign="baseline">
        <xsl:if test="$input/help">
          <x:call-template name="displayhelp">
            <x:with-param name="fieldname">
              <xsl:value-of select="$input/@name"/>
            </x:with-param>
            <x:with-param name="helpelement">
              <xsl:copy-of select="$input/help"/>
            </x:with-param>
          </x:call-template>
        </xsl:if>
        <input type="hidden" name="{$input/@name}">
          <x:attribute name="value">
            <x:choose>
              <x:when>
                <xsl:attribute name="test">
                  <xsl:value-of select="$input/xpath"/>
                  <xsl:text>= 'true'</xsl:text>
                </xsl:attribute>
                <x:text>true</x:text>
              </x:when>
              <x:otherwise>
                <x:text>false</x:text>
              </x:otherwise>
            </x:choose>
          </x:attribute>
        </input>
        <input type="checkbox" name="{concat($input/@name, '_cb')}">
          <xsl:attribute name="onclick">
            <xsl:text>javascript:toggleCheckbox(this, '</xsl:text><xsl:value-of select="$input/@name"/><xsl:text>');</xsl:text>
          </xsl:attribute>
          <x:if>
            <xsl:attribute name="test">
              <xsl:value-of select="$input/xpath"/>
              <xsl:text>= 'true'</xsl:text>
            </xsl:attribute>
            <x:attribute name="checked">checked</x:attribute>
          </x:if>
          <x:if test="$readonly">
            <x:attribute name="disabled">disabled</x:attribute>
          </x:if>
        </input>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="dropdown">
    <xsl:param name="input"/>
    <tr>
      <x:call-template name="labelcolumn">
        <x:with-param name="label">
          <xsl:value-of select="$input/display"/>
          <xsl:text>:</xsl:text>
        </x:with-param>
        <x:with-param name="required">
          <xsl:value-of select="($input/@required = 'true')"/>
        </x:with-param>
        <x:with-param name="fieldname">
          <xsl:value-of select="$input/@name"/>
        </x:with-param>
          <xsl:if test="$input/help">
              <x:with-param name="helpelement">
                  <xsl:copy-of select="$input/help"/>
              </x:with-param>
          </xsl:if>
        <x:with-param name="valign" select="'middle'"/>
      </x:call-template>
      <td valign="baseline">
        <xsl:if test="$input/help">
          <x:call-template name="displayhelp">
            <x:with-param name="fieldname">
              <xsl:value-of select="$input/@name"/>
            </x:with-param>
            <x:with-param name="helpelement">
              <xsl:copy-of select="$input/help"/>
            </x:with-param>
          </x:call-template>
        </xsl:if>

        <x:choose>
          <x:when test="$readonly">
            <xsl:for-each select="$input/options/option">
              <x:if>
                <xsl:attribute name="test">
                  <xsl:value-of select="$input/xpath"/>
                  <xsl:text>= '</xsl:text>
                  <xsl:value-of select="@value"/>
                  <xsl:text>'</xsl:text>
                </xsl:attribute>

                <input type="text" name="_dummy_{$input/@name}" value="{.}" disabled="true"/>
                <input type="hidden" name="{$input/@name}" value="{@value}"/>
              </x:if>
            </xsl:for-each>
          </x:when>
          <x:otherwise>
            <select name="{$input/@name}">
              <xsl:for-each select="$input/options/option">
                <option>
                  <xsl:attribute name="value">
                    <xsl:value-of select="@value"/>
                  </xsl:attribute>
                  <x:if>
                    <xsl:attribute name="test">
                      <xsl:value-of select="$input/xpath"/>
                      <xsl:text>= '</xsl:text>
                      <xsl:value-of select="@value"/>
                      <xsl:text>'</xsl:text>
                    </xsl:attribute>
                    <x:attribute name="selected">selected</x:attribute>
                  </x:if>
                  <xsl:value-of select="."/>
                </option>
              </xsl:for-each>
            </select>
          </x:otherwise>
        </x:choose>

      </td>
    </tr>
  </xsl:template>

  <xsl:template name="relatedcontent_multiple_false">
    <xsl:param name="input"/>

    <xsl:variable name="min-occurrence" select="0" />
    <xsl:variable name="max-occurrence" select="1" />

    <x:variable name="select-content-button-onclick">
      <x:text>javascript:OpenContentPopup(</x:text>
      <x:value-of select="$selectedunitkey"/>
      <x:text>,</x:text>
      <x:text>- 1, 'relatedcontent',</x:text>
      <x:text>'</x:text>
      <xsl:value-of select="$input/@name"/>
      <x:text>', </x:text>
      <x:text>getObjectIndex(this)</x:text>
      <x:text>, </x:text>
      <xsl:text>[</xsl:text>
      <xsl:for-each select="$input/contenttype">
        <xsl:choose>
          <!-- legacy support for contentype - key -->
          <xsl:when test="@key">
            <xsl:value-of select="@key"/>
          </xsl:when>
          <xsl:when test="@name">
            <xsl:text>'</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>'</xsl:text>
          </xsl:when>
          <xsl:otherwise/>
        </xsl:choose>
        <xsl:if test="position() != count($input/contenttype)">
          <xsl:text>,</xsl:text>
        </xsl:if>
      </xsl:for-each>
      <x:text>], </x:text>
      <xsl:value-of select="$min-occurrence"/>
      <x:text>, </x:text>
      <xsl:value-of select="$max-occurrence"/>
      <x:text>);</x:text>
    </x:variable>

    <tr>
      <x:call-template name="labelcolumn">
        <x:with-param name="label">
          <xsl:value-of select="$input/display"/>
          <xsl:text>:</xsl:text>
        </x:with-param>
        <x:with-param name="required">
          <xsl:value-of select="($input/@required = 'true')"/>
        </x:with-param>
        <x:with-param name="fieldname">
          <xsl:value-of select="$input/@name"/>
        </x:with-param>
          <xsl:if test="$input/help">
              <x:with-param name="helpelement">
                  <xsl:copy-of select="$input/help"/>
              </x:with-param>
          </xsl:if>

      </x:call-template>

      <td valign="top">
        <xsl:if test="$input/help">
          <x:call-template name="displayhelp">
            <x:with-param name="fieldname">
              <xsl:value-of select="$input/@name"/>
            </x:with-param>
            <x:with-param name="helpelement">
              <xsl:copy-of select="$input/help"/>
            </x:with-param>
          </x:call-template>
        </xsl:if>

        <input type="hidden">
          <xsl:attribute name="name">
            <xsl:value-of select="$input/@name"/>
            <xsl:text>_counter</xsl:text>
          </xsl:attribute>
          <x:attribute name="value">
            <x:value-of>
              <xsl:attribute name="select">
                <xsl:text>count(</xsl:text>
                <xsl:value-of select="$input/xpath"/>
                <xsl:text>/content[@key = /contents/relatedcontents/content/@key])</xsl:text>
              </xsl:attribute>
            </x:value-of>
          </x:attribute>
        </input>

        <table cellpadding="0" cellspacing="0">
          <tbody id="{$input/@name}_table" name="{$input/@name}_table">
            <tr>
              <td>

                <input type="hidden">
                  <xsl:attribute name="name">
                    <xsl:value-of select="$input/@name"/>
                  </xsl:attribute>
                  <x:attribute name="value">
                    <x:value-of>
                      <xsl:attribute name="select">
                        <xsl:value-of select="$input/xpath"/>
                        <xsl:text>/@key</xsl:text>
                      </xsl:attribute>
                    </x:value-of>
                  </x:attribute>
                </input>

                <x:variable name="tmpkey">
                  <x:value-of>
                    <xsl:attribute name="select">
                      <xsl:value-of select="$input/xpath"/>
                      <xsl:text>/@key</xsl:text>
                    </xsl:attribute>
                  </x:value-of>
                </x:variable>

                <input type="text" readonly="readonly" size="40">
                  <xsl:attribute name="name">
                    <xsl:value-of select="$input/@name"/>
                    <xsl:text>_title_placeholder</xsl:text>
                  </xsl:attribute>
                  <x:attribute name="value">
                    <x:value-of>
                      <xsl:attribute name="select">
                        <xsl:text>//content[@key = $tmpkey]/title</xsl:text>
                      </xsl:attribute>
                    </x:value-of>
                  </x:attribute>
                  <x:if test="$readonly">
                    <x:attribute name="disabled">disabled</x:attribute>
                  </x:if>
                </input>

                <x:call-template name="button">
                  <x:with-param name="type" select="'button'"/>
                  <x:with-param name="name" select="'relatedcontent_{$input/@name}'"/>
                  <x:with-param name="image" select="'images/icon_browse.gif'"/>
                  <x:with-param name="disabled" select="$readonly"/>
                  <x:with-param name="tooltip" select="'%cmdSelectContent%'"/>
                  <x:with-param name="onclick">
                    <x:value-of select="$select-content-button-onclick"/>
                  </x:with-param>
                </x:call-template>

                <x:call-template name="button">
                  <x:with-param name="name">
                    <xsl:value-of select="$input/@name"/>
                    <xsl:text>removebutton</xsl:text>
                  </x:with-param>
                  <x:with-param name="image" select="'images/icon_remove.gif'"/>
                  <x:with-param name="type" select="'button'"/>
                  <x:with-param name="disabled" select="$readonly"/>
                  <x:with-param name="tooltip" select="'%btnRemoveContent%'"/>

                  <x:with-param name="onclick">
                    <xsl:text>javascript: removeRelatedContentMultipleFalse( getObjectIndex(this), '</xsl:text>
                    <xsl:value-of select="$input/@name"/>
                    <xsl:text>');</xsl:text>
                  </x:with-param>
                </x:call-template>

              </td>
            </tr>
          </tbody>
        </table>

      </td>
    </tr>
  </xsl:template>

  <xsl:template name="relatedcontent">
    <xsl:param name="input"/>

    <xsl:variable name="min-occurrence" select="0" />
    <xsl:variable name="max-occurrence" select="-1" />

    <x:variable name="add-content-button-onclick">
      <x:text>javascript:OpenContentPopup(</x:text>
      <x:value-of select="$selectedunitkey"/>
      <x:text>,</x:text>
      <x:text>- 1, 'relatedcontent',</x:text>
      <x:text>'</x:text>
      <xsl:value-of select="$input/@name"/>
      <x:text>', </x:text>
      <x:text>getObjectIndex(this)</x:text>
      <x:text>, </x:text>
      <xsl:text>[</xsl:text>
      <xsl:for-each select="$input/contenttype">
        <xsl:choose>
          <!-- legacy support for contentype - key -->
          <xsl:when test="@key">
            <xsl:value-of select="@key"/>
          </xsl:when>
          <xsl:when test="@name">
            <xsl:text>'</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>'</xsl:text>
          </xsl:when>
          <xsl:otherwise/>
        </xsl:choose>
        <xsl:if test="position() != count($input/contenttype)">
          <xsl:text>,</xsl:text>
        </xsl:if>
      </xsl:for-each>
      <x:text>], </x:text>
      <xsl:value-of select="$min-occurrence"/>
      <x:text>, </x:text>
      <xsl:value-of select="$max-occurrence"/>
      <x:text>);</x:text>
    </x:variable>

    <tr>
      <x:call-template name="labelcolumn">
        <x:with-param name="label">
          <xsl:value-of select="$input/display"/>
          <xsl:text>:</xsl:text>
        </x:with-param>
        <x:with-param name="required">
          <xsl:value-of select="($input/@required = 'true')"/>
        </x:with-param>
        <x:with-param name="fieldname">
          <xsl:value-of select="$input/@name"/>
        </x:with-param>
          <xsl:if test="$input/help">
              <x:with-param name="helpelement">
                  <xsl:copy-of select="$input/help"/>
              </x:with-param>
          </xsl:if>

      </x:call-template>

      <td valign="top">
        <xsl:if test="$input/help">
          <x:call-template name="displayhelp">
            <x:with-param name="fieldname">
              <xsl:value-of select="$input/@name"/>
            </x:with-param>
            <x:with-param name="helpelement">
              <xsl:copy-of select="$input/help"/>
            </x:with-param>
          </x:call-template>
        </xsl:if>

        <input type="hidden">
          <xsl:attribute name="name">
            <xsl:value-of select="$input/@name"/>
            <xsl:text>_counter</xsl:text>
          </xsl:attribute>
          <x:attribute name="value">
            <x:value-of>
              <xsl:attribute name="select">
                <xsl:text>count(</xsl:text>
                <xsl:value-of select="$input/xpath"/>
                <xsl:text>/content[@key = /contents/relatedcontents/content/@key])</xsl:text>
              </xsl:attribute>
            </x:value-of>
          </x:attribute>
        </input>

        <table cellpadding="0" cellspacing="0">
          <tbody id="{$input/@name}_table" name="{$input/@name}_table">
            <x:for-each>
              <xsl:attribute name="select">
                <xsl:value-of select="$input/xpath"/>
                <xsl:text>/content</xsl:text>
              </xsl:attribute>
              <x:variable name="ckey" select="@key"/>
              <x:if test="/contents/relatedcontents/content[@key = $ckey]">
                <tr>
                  <x:attribute name="id">relatedcontent_<x:value-of select="@key"/></x:attribute>
                  <td class="related-content-title">

                    <input type="hidden">
                      <xsl:attribute name="name">
                        <xsl:value-of select="$input/@name"/>
                      </xsl:attribute>
                      <x:attribute name="value">
                        <x:value-of select="@key"/>
                      </x:attribute>
                    </input>

                    <x:variable name="tmpkey">
                      <x:value-of select="@key"/>
                    </x:variable>

                    <span>
                      <x:if test="$readonly">
                        <x:attribute name="class">
                          <x:text>disabled-element</x:text>
                        </x:attribute>
                      </x:if>
                      <x:value-of>
                        <xsl:attribute name="select">
                          <xsl:text>//content[@key = $tmpkey]/title</xsl:text>
                        </xsl:attribute>
                      </x:value-of>
                    </span>

                  </td>
                  <td align="right">

                    <x:call-template name="button">
                      <x:with-param name="name">
                        <xsl:value-of select="$input/@name"/>
                        <xsl:text>moveupbutton</xsl:text>
                      </x:with-param>
                      <x:with-param name="image" select="'images/icon_move_up.gif'"/>
                      <x:with-param name="type" select="'button'"/>
                      <x:with-param name="disabled" select="$readonly"/>
                      <x:with-param name="tooltip" select="'%altContentMoveUp%'"/>
                      <x:with-param name="onclick">
                        <xsl:text>javaScript:moveRelatedContentUp( this, '</xsl:text>
                        <xsl:value-of select="$input/@name"/>
                        <xsl:text>');</xsl:text>
                      </x:with-param>
                    </x:call-template>
                    <x:call-template name="button">
                      <x:with-param name="name">
                        <xsl:value-of select="$input/@name"/>
                        <xsl:text>movedownbutton</xsl:text>
                      </x:with-param>
                      <x:with-param name="image" select="'images/icon_move_down.gif'"/>
                      <x:with-param name="type" select="'button'"/>
                      <x:with-param name="disabled" select="$readonly"/>
                      <x:with-param name="tooltip" select="'%altContentMoveDown%'"/>
                      <x:with-param name="onclick">
                        <xsl:text>javaScript:moveRelatedContentDown( this, '</xsl:text>
                        <xsl:value-of select="$input/@name"/>
                        <xsl:text>');</xsl:text>
                      </x:with-param>
                    </x:call-template>

                    <x:call-template name="button">
                      <x:with-param name="name">
                        <xsl:value-of select="$input/@name"/>
                        <xsl:text>removebutton</xsl:text>
                      </x:with-param>
                      <x:with-param name="image" select="'images/icon_remove.gif'"/>
                      <x:with-param name="type" select="'button'"/>
                      <x:with-param name="disabled" select="$readonly"/>
                      <x:with-param name="tooltip" select="'%btnRemoveContent%'"/>

                      <x:with-param name="onclick">
                        <xsl:text>javascript: removeRelatedContent( this, '</xsl:text>
                        <xsl:value-of select="$input/@name"/>
                        <xsl:text>');</xsl:text>
                      </x:with-param>
                    </x:call-template>
                  </td>
                </tr>
              </x:if>
            </x:for-each>
          </tbody>
        </table>

        <p>
          <x:call-template name="button">
            <x:with-param name="type" select="'button'"/>
            <x:with-param name="caption" select="'%cmdAddContent%'"/>
            <x:with-param name="name">
              <xsl:value-of select="$input/@name"/>
              <xsl:text>addbutton</xsl:text>
            </x:with-param>
            <x:with-param name="disabled" select="$readonly"/>
            <x:with-param name="onclick">
              <x:value-of select="$add-content-button-onclick"/>
            </x:with-param>
          </x:call-template>
        </p>

      </td>
    </tr>
  </xsl:template>

  <xsl:template name="displayfiles">
    <xsl:param name="input"/>

    <x:if test="not($readonly)">
      <script type="text/javascript" language="JavaScript">
        <xsl:text>function clearNewRow</xsl:text><xsl:value-of select="$input/@name"/><xsl:text>() {</xsl:text>
        <xsl:text>var row = document.formAdmin['</xsl:text><xsl:value-of select="$input/@name"/><xsl:text>'].length - 1;</xsl:text>
        <xsl:text>document.formAdmin['</xsl:text><xsl:value-of select="concat('filename', $input/@name)"/><xsl:text>'][row].value = "";</xsl:text>
        <xsl:text>document.formAdmin['</xsl:text><xsl:value-of select="$input/@name"/><xsl:text>'][row].value = "";}</xsl:text>
      </script>
    </x:if>

    <tr>
      <x:call-template name="labelcolumn">
        <x:with-param name="label">
          <xsl:value-of select="$input/display"/>
          <xsl:text>:</xsl:text>
        </x:with-param>
        <x:with-param name="required">
          <xsl:value-of select="($input/@required = 'true')"/>
        </x:with-param>
        <x:with-param name="fieldname">
          <xsl:value-of select="$input/@name"/>
        </x:with-param>
          <xsl:if test="$input/help">
              <x:with-param name="helpelement">
                  <xsl:copy-of select="$input/help"/>
              </x:with-param>
          </xsl:if>

      </x:call-template>
      <td>
        <xsl:if test="$input/help">
          <x:call-template name="displayhelp">
            <x:with-param name="fieldname">
              <xsl:value-of select="$input/@name"/>
            </x:with-param>
            <x:with-param name="helpelement">
              <xsl:copy-of select="$input/help"/>
            </x:with-param>
          </x:call-template>
        </xsl:if>
        <table cellspacing="2" cellpadding="0" border="0">
          <tbody>
            <xsl:attribute name="name">
              <xsl:value-of select="concat('filetable', $input/@name)"/>
            </xsl:attribute>
            <xsl:attribute name="id">
              <xsl:value-of select="concat('filetable', $input/@name)"/>
            </xsl:attribute>

            <x:choose>
              <x:when>
                <xsl:attribute name="test">
                  <xsl:text>not(boolean(</xsl:text>
                  <xsl:value-of select="concat($input/xpath, '/file')"/>
                  <xsl:text>))</xsl:text>
                </xsl:attribute>
                <tr>
                  <td>
                    <input type="text" readonly="readonly" size="40">
                      <xsl:attribute name="name">
                        <xsl:value-of select="concat('filename', $input/@name)"/>
                      </xsl:attribute>
                      <x:if test="$readonly">
                        <x:attribute name="disabled">disabled</x:attribute>
                      </x:if>
                    </input>
                    <input type="hidden">
                      <xsl:attribute name="name">
                        <xsl:value-of select="$input/@name"/>
                      </xsl:attribute>
                    </input>
                    <x:call-template name="button">
                      <x:with-param name="type" select="'button'"/>
                      <x:with-param name="name" select="'choosefile{$input/@name}'"/>
                      <x:with-param name="image" select="'images/icon_browse.gif'"/>
                      <x:with-param name="disabled" select="$readonly"/>
                      <x:with-param name="tooltip" select="'%fldChooseFile%'"/>
                      <x:with-param name="onclick">
                        <xsl:text>javascript:insert_file_onclick('</xsl:text>
                        <xsl:value-of select="concat('filename', $input/@name)"/>
                        <xsl:text>', '</xsl:text>
                        <xsl:value-of select="$input/@name"/>
                        <xsl:text>', this, 'relatedfiles');</xsl:text>
                      </x:with-param>
                    </x:call-template>

                    <x:call-template name="button">
                      <x:with-param name="name" select="'moverupper_{$input/@name}'"/>
                      <x:with-param name="image" select="'images/icon_move_up.gif'"/>
                      <x:with-param name="type" select="'button'"/>
                      <x:with-param name="disabled" select="$readonly"/>
                      <x:with-param name="tooltip" select="'%altContentMoveUp%'"/>
                      <x:with-param name="onclick">
                        <xsl:text>javascript:moveTableRowUpFS('</xsl:text>
                        <xsl:value-of select="concat('filetable', $input/@name)"/>
                        <xsl:text>', this)</xsl:text>
                      </x:with-param>
                    </x:call-template>
                    <x:call-template name="button">
                      <x:with-param name="name" select="'moverdowner_{$input/@name}'"/>
                      <x:with-param name="image" select="'images/icon_move_down.gif'"/>
                      <x:with-param name="type" select="'button'"/>
                      <x:with-param name="disabled" select="$readonly"/>
                      <x:with-param name="tooltip" select="'%altContentMoveDown%'"/>
                      <x:with-param name="onclick">
                        <xsl:text>javascript:moveTableRowDownFS('</xsl:text>
                        <xsl:value-of select="concat('filetable', $input/@name)"/>
                        <xsl:text>', this)</xsl:text>
                      </x:with-param>
                    </x:call-template>

                    <x:call-template name="button">
                      <x:with-param name="type" select="'button'"/>
                      <x:with-param name="name" select="'removefilerow{$input/@name}'"/>
                      <x:with-param name="image" select="'images/icon_remove.gif'"/>
                      <x:with-param name="disabled" select="$readonly"/>
                      <x:with-param name="tooltip" select="'%cmdRemoveFile%'"/>

                      <x:with-param name="onclick">
                        <xsl:text>javascript:__removeRow</xsl:text>
                        <xsl:text>('</xsl:text>
                        <xsl:value-of select="concat('filetable', $input/@name)"/>
                        <xsl:text>', this, '</xsl:text>
                        <xsl:value-of select="concat('filename', $input/@name)"/>
                        <xsl:text>', '</xsl:text>
                        <xsl:value-of select="$input/@name"/>
                        <xsl:text>' );</xsl:text>
                      </x:with-param>
                    </x:call-template>
                  </td>
                </tr>
              </x:when>
              <x:otherwise>
                <x:for-each>
                  <xsl:attribute name="select">
                    <xsl:value-of select="concat($input/xpath, '/file')"/>
                  </xsl:attribute>

                  <x:variable name="ckey" select="@key"/>

                  <x:choose>
                    <x:when test="/contents/relatedcontents/content[@key = $ckey]">
                      <x:variable name="key" select="@key"/>
                      <tr>
                        <td>

                          <input readonly="readonly" type="text" size="40">
                            <xsl:attribute name="name">
                              <xsl:value-of select="concat('filename', $input/@name)"/>
                            </xsl:attribute>
                            <xsl:attribute name="value">
                              <xsl:text>{//content[@key = $key]/contentdata/name}</xsl:text>
                            </xsl:attribute>
                            <x:if test="$readonly">
                              <x:attribute name="disabled">disabled</x:attribute>
                            </x:if>
                          </input>

                          <input type="hidden">
                            <xsl:attribute name="name">
                              <xsl:value-of select="$input/@name"/>
                            </xsl:attribute>
                            <x:attribute name="value">
                              <x:value-of select="@key"/>
                            </x:attribute>
                          </input>

                          <x:call-template name="button">
                            <x:with-param name="type" select="'button'"/>
                            <x:with-param name="name" select="'choosefile{$input/@name}'"/>
                            <x:with-param name="image" select="'images/icon_browse.gif'"/>
                            <x:with-param name="disabled" select="$readonly"/>
                            <x:with-param name="tooltip" select="'%fldChooseFile%'"/>
                            <x:with-param name="onclick">
                              <xsl:text>javascript:insert_file_onclick('</xsl:text>
                              <xsl:value-of select="concat('filename', $input/@name)"/>
                              <xsl:text>', '</xsl:text>
                              <xsl:value-of select="$input/@name"/>
                              <xsl:text>', this, 'relatedfiles');</xsl:text>
                            </x:with-param>
                          </x:call-template>

                          <x:call-template name="button">
                            <x:with-param name="name" select="'moverupper_{$input/@name}'"/>
                            <x:with-param name="image" select="'images/icon_move_up.gif'"/>
                            <x:with-param name="type" select="'button'"/>
                            <x:with-param name="disabled" select="$readonly"/>
                            <x:with-param name="tooltip" select="'%altContentMoveUp%'"/>
                            <x:with-param name="onclick">
                              <xsl:text>javascript:moveTableRowUpFS('</xsl:text>
                              <xsl:value-of select="concat('filetable', $input/@name)"/>
                              <xsl:text>', this)</xsl:text>
                            </x:with-param>
                          </x:call-template>

                          <x:call-template name="button">
                            <x:with-param name="name" select="'moverdowner_{$input/@name}'"/>
                            <x:with-param name="image" select="'images/icon_move_down.gif'"/>
                            <x:with-param name="type" select="'button'"/>
                            <x:with-param name="disabled" select="$readonly"/>
                            <x:with-param name="tooltip" select="'%altContentMoveDown%'"/>
                            <x:with-param name="onclick">
                              <xsl:text>javascript:moveTableRowDownFS('</xsl:text>
                              <xsl:value-of select="concat('filetable', $input/@name)"/>
                              <xsl:text>', this)</xsl:text>
                            </x:with-param>
                          </x:call-template>

                          <x:call-template name="button">
                            <x:with-param name="type" select="'button'"/>
                            <x:with-param name="name" select="'removefilerow{$input/@name}'"/>
                            <x:with-param name="image" select="'images/icon_remove.gif'"/>
                            <x:with-param name="disabled" select="$readonly"/>
                            <x:with-param name="tooltip" select="'%cmdRemoveFile%'"/>
                            <x:with-param name="onclick">
                              <xsl:text>javascript:__removeRow</xsl:text>
                              <xsl:text>('</xsl:text>
                              <xsl:value-of select="concat('filetable', $input/@name)"/>
                              <xsl:text>', this, '</xsl:text>
                              <xsl:value-of select="concat('filename', $input/@name)"/>
                              <xsl:text>', '</xsl:text>
                              <xsl:value-of select="$input/@name"/>
                              <xsl:text>' );</xsl:text>
                            </x:with-param>
                          </x:call-template>

                        </td>
                      </tr>
                    </x:when>
                    <x:otherwise>
                      <tr>
                        <td>

                          <input type="text" readonly="readonly" size="40">
                            <xsl:attribute name="name">
                              <xsl:value-of select="concat('filename', $input/@name)"/>
                            </xsl:attribute>
                            <x:if test="$readonly">
                              <x:attribute name="disabled">disabled</x:attribute>
                            </x:if>
                          </input>

                          <input type="hidden">
                            <xsl:attribute name="name">
                              <xsl:value-of select="$input/@name"/>
                            </xsl:attribute>
                          </input>

                          <x:call-template name="button">
                            <x:with-param name="type" select="'button'"/>
                            <x:with-param name="name" select="'choosefile{$input/@name}'"/>
                            <x:with-param name="image" select="'images/icon_browse.gif'"/>
                            <x:with-param name="disabled" select="$readonly"/>
                            <x:with-param name="onclick">
                              <xsl:text>javascript:insert_file_onclick('</xsl:text>
                              <xsl:value-of select="concat('filename', $input/@name)"/>
                              <xsl:text>', '</xsl:text>
                              <xsl:value-of select="$input/@name"/>
                              <xsl:text>', this, 'relatedfiles');</xsl:text>
                            </x:with-param>
                          </x:call-template>

                          <x:call-template name="button">
                            <x:with-param name="name" select="'moverupper'"/>
                            <x:with-param name="image" select="'images/icon_move_up.gif'"/>
                            <x:with-param name="type" select="'button'"/>
                            <x:with-param name="disabled" select="false()"/>
                            <x:with-param name="onclick">
                              <xsl:text>javascript:moveTableRowUpFS('</xsl:text>
                              <xsl:value-of select="concat('filetable', $input/@name)"/>
                              <xsl:text>', this)</xsl:text>
                            </x:with-param>
                          </x:call-template>

                          <x:call-template name="button">
                            <x:with-param name="name" select="'moverdowner'"/>
                            <x:with-param name="image" select="'images/icon_move_down.gif'"/>
                            <x:with-param name="type" select="'button'"/>
                            <x:with-param name="disabled" select="false()"/>
                            <x:with-param name="onclick">
                              <xsl:text>javascript:moveTableRowDownFS('</xsl:text>
                              <xsl:value-of select="concat('filetable', $input/@name)"/>
                              <xsl:text>', this)</xsl:text>
                            </x:with-param>
                          </x:call-template>

                          <x:call-template name="button">
                            <x:with-param name="type" select="'button'"/>
                            <x:with-param name="name" select="'removefilerow{$input/@name}'"/>
                            <x:with-param name="image" select="'images/icon_remove.gif'"/>
                            <x:with-param name="disabled" select="$readonly"/>
                            <x:with-param name="onclick">
                              <xsl:text>javascript:__removeRow</xsl:text>
                              <xsl:text>('</xsl:text>
                              <xsl:value-of select="concat('filetable', $input/@name)"/>
                              <xsl:text>', this, '</xsl:text>
                              <xsl:value-of select="concat('filename', $input/@name)"/>
                              <xsl:text>', '</xsl:text>
                              <xsl:value-of select="$input/@name"/>
                              <xsl:text>' );</xsl:text>
                            </x:with-param>
                          </x:call-template>

                        </td>
                      </tr>
                    </x:otherwise>
                  </x:choose>
                </x:for-each>
              </x:otherwise>
            </x:choose>
          </tbody>
        </table>

        <x:call-template name="button">
          <x:with-param name="type" select="'button'"/>
          <x:with-param name="name" select="copyfilerow"/>
          <x:with-param name="caption" select="'%cmdNewFile%'"/>
          <x:with-param name="disabled" select="$readonly"/>
          <x:with-param name="onclick">
            <xsl:text>javascript:addTableRow</xsl:text>
            <xsl:text>('</xsl:text>
            <xsl:value-of select="concat('filetable', $input/@name)"/>
            <xsl:text>', 0, 1); clearNewRow</xsl:text>
            <xsl:value-of select="$input/@name"/>
            <xsl:text>();</xsl:text>
          </x:with-param>
        </x:call-template>

      </td>
    </tr>

  </xsl:template>

  <xsl:template name="displayfile">
    <xsl:param name="input"/>
    <tr>

      <x:call-template name="labelcolumn">
        <x:with-param name="label">
          <xsl:value-of select="$input/display"/>
          <xsl:text>:</xsl:text>
        </x:with-param>
        <x:with-param name="required">
          <xsl:value-of select="($input/@required = 'true')"/>
        </x:with-param>
        <x:with-param name="fieldname">
          <xsl:value-of select="$input/@name"/>
        </x:with-param>
          <xsl:if test="$input/help">
              <x:with-param name="helpelement">
                  <xsl:copy-of select="$input/help"/>
              </x:with-param>
          </xsl:if>

        <x:with-param name="valign" select="'middle'"/>
      </x:call-template>

      <td>
        <xsl:if test="$input/help">

          <x:call-template name="displayhelp">
            <x:with-param name="fieldname">
              <xsl:value-of select="$input/@name"/>
            </x:with-param>
            <x:with-param name="helpelement">
              <xsl:copy-of select="$input/help"/>
            </x:with-param>
          </x:call-template>

        </xsl:if>

        <table cellspacing="0" cellpadding="0" border="0">
          <tbody>
            <xsl:attribute name="name">
              <xsl:value-of select="concat('filetable', $input/@name)"/>
            </xsl:attribute>
            <xsl:attribute name="id">
              <xsl:value-of select="concat('filetable', $input/@name)"/>
            </xsl:attribute>

            <x:variable name="fileContentKey">
              <xsl:attribute name="select">
                <xsl:value-of select="concat($input/xpath, '/file/@key')"/>
              </xsl:attribute>
            </x:variable>

            <x:choose>
              <x:when>
                <xsl:attribute name="test">
                  <xsl:text>not(/contents/relatedcontents/content[@key = $fileContentKey])</xsl:text>
                </xsl:attribute>
                <tr>
                  <td>
                    <input type="text" readonly="readonly" size="40">
                      <xsl:attribute name="name">
                        <xsl:value-of select="concat('filename', $input/@name)"/>
                      </xsl:attribute>
                      <x:if test="$readonly">
                        <x:attribute name="disabled">disabled</x:attribute>
                      </x:if>
                    </input>

                    <input type="hidden">
                      <xsl:attribute name="name">
                        <xsl:value-of select="$input/@name"/>
                      </xsl:attribute>
                      <xsl:attribute name="class">
                        <xsl:text>related-file-key</xsl:text>
                      </xsl:attribute>
                    </input>

                    <x:call-template name="button">
                      <x:with-param name="type" select="'button'"/>
                      <x:with-param name="name" select="'choosefile{$input/@name}'"/>
                      <x:with-param name="image" select="'images/icon_browse.gif'"/>
                      <x:with-param name="disabled" select="$readonly"/>
                      <x:with-param name="onclick">
                        <xsl:text>javascript:insert_file_onclick('</xsl:text>
                        <xsl:value-of select="concat('filename', $input/@name)"/>
                        <xsl:text>', '</xsl:text>
                        <xsl:value-of select="$input/@name"/>
                        <xsl:text>', this, 'relatedfile');</xsl:text>
                      </x:with-param>
                    </x:call-template>

                    <x:call-template name="button">
                      <x:with-param name="type" select="'button'"/>
                      <x:with-param name="name" select="'removefilerow{$input/@name}'"/>
                      <x:with-param name="image" select="'images/icon_remove.gif'"/>
                      <x:with-param name="disabled" select="$readonly"/>
                      <x:with-param name="onclick">
                        <xsl:text>javascript:__removeFile</xsl:text>
                        <xsl:text>('</xsl:text>
                        <xsl:value-of select="concat('filetable', $input/@name)"/>
                        <xsl:text>', this, '</xsl:text>
                        <xsl:value-of select="concat('filename', $input/@name)"/>
                        <xsl:text>', '</xsl:text>
                        <xsl:value-of select="$input/@name"/>
                        <xsl:text>' );</xsl:text>
                      </x:with-param>
                    </x:call-template>

                  </td>
                </tr>
              </x:when>
              <x:otherwise>
                <x:for-each>
                  <xsl:attribute name="select">
                    <xsl:value-of select="concat($input/xpath, '/file')"/>
                  </xsl:attribute>
                  <x:variable name="key" select="@key"/>
                  <x:if test="/contents/relatedcontents/content[@key = $key]">
                    <tr>
                      <td>

                        <input readonly="readonly" type="text" size="40">
                          <xsl:attribute name="name">
                            <xsl:value-of select="concat('filename', $input/@name)"/>
                          </xsl:attribute>
                          <xsl:attribute name="value">
                            <xsl:text>{//content[@key = $key]/contentdata/name}</xsl:text>
                          </xsl:attribute>
                          <x:if test="$readonly">
                            <x:attribute name="disabled">disabled</x:attribute>
                          </x:if>
                        </input>

                        <input type="hidden">
                          <xsl:attribute name="name">
                            <xsl:value-of select="$input/@name"/>
                          </xsl:attribute>
                          <x:attribute name="value">
                            <x:value-of select="@key"/>
                          </x:attribute>
                        </input>

                        <x:call-template name="button">
                          <x:with-param name="type" select="'button'"/>
                          <x:with-param name="name" select="'choosefile{$input/@name}'"/>
                          <x:with-param name="image" select="'images/icon_browse.gif'"/>
                          <x:with-param name="disabled" select="$readonly"/>
                          <x:with-param name="onclick">
                            <xsl:text>javascript:insert_file_onclick('</xsl:text>
                            <xsl:value-of select="concat('filename', $input/@name)"/>
                            <xsl:text>', '</xsl:text>
                            <xsl:value-of select="$input/@name"/>
                            <xsl:text>', this, 'relatedfiles');</xsl:text>
                          </x:with-param>
                        </x:call-template>

                        <x:call-template name="button">
                          <x:with-param name="type" select="'button'"/>
                          <x:with-param name="name" select="'removefilerow{$input/@name}'"/>
                          <x:with-param name="image" select="'images/icon_remove.gif'"/>
                          <x:with-param name="disabled" select="$readonly"/>
                          <x:with-param name="onclick">
                            <xsl:text>javascript:__removeFile</xsl:text>
                            <xsl:text>('</xsl:text>
                            <xsl:value-of select="concat('filetable', $input/@name)"/>
                            <xsl:text>', this, '</xsl:text>
                            <xsl:value-of select="concat('filename', $input/@name)"/>
                            <xsl:text>', '</xsl:text>
                            <xsl:value-of select="$input/@name"/>
                            <xsl:text>' );</xsl:text>
                          </x:with-param>
                        </x:call-template>
                      </td>
                    </tr>
                  </x:if>
                </x:for-each>
              </x:otherwise>
            </x:choose>
          </tbody>
        </table>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="displayimages">
    <xsl:param name="input"/>
    <tr name="imagerow">
      <x:call-template name="enhancedmultipleimageselector">
        <x:with-param name="disabled" select="$readonly"/>
        <x:with-param name="helpelement">
          <xsl:copy-of select="$input/help"/>
        </x:with-param>
        <x:with-param name="label">
          <xsl:attribute name="select">
            <xsl:text>'</xsl:text>
            <xsl:value-of select="$input/display"/>
            <xsl:text>:'</xsl:text>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="name">
          <xsl:attribute name="select">
            <xsl:text>'</xsl:text>
            <xsl:value-of select="$input/@name"/>
            <xsl:text>'</xsl:text>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="selected">
          <xsl:attribute name="select">
            <xsl:value-of select="concat($input/xpath, '/image')"/>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="colspan" select="'3'"/>
        <x:with-param name="imagetype" select="'body'"/>
        <x:with-param name="contenttest" select="'true'"/>
        <xsl:if test="$input/@required = 'true'">
          <x:with-param name="required" select="true()"/>
        </xsl:if>
      </x:call-template>
    </tr>
    <tr>
      <td>
        <br/>
      </td>
      <td>
        <table cellspacing="4">
          <tr>
            <td>
              <x:call-template name="button">
                <x:with-param name="type" select="'button'"/>
                <x:with-param name="caption" select="'%cmdNewImage%'"/>
                <x:with-param name="disabled" select="$readonly"/>
                <x:with-param name="onclick">
                  <xsl:text>javascript:addTableRow('</xsl:text>
                  <xsl:value-of select="$input/@name"/>
                  <xsl:text>table', 0, 1);relatedImagesDisplay('</xsl:text>
                  <xsl:value-of select="$input/@name"/>
                  <xsl:text>table');</xsl:text>
                </x:with-param>
              </x:call-template>
              <x:if>
                <xsl:attribute name="test">
                  <xsl:text>count(/contents/content/</xsl:text>
                  <xsl:value-of select="$input/xpath"/>
                  <xsl:text>) = 0</xsl:text>
                </xsl:attribute>
              </x:if>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="displayimage">
    <xsl:param name="input"/>
    <tr name="imagerow">
      <x:call-template name="enhancedimageselector">
        <x:with-param name="disabled" select="$readonly"/>
        <x:with-param name="helpelement">
          <xsl:copy-of select="$input/help"/>
        </x:with-param>
        <x:with-param name="label">
          <xsl:attribute name="select">
            <xsl:text>'</xsl:text>
            <xsl:value-of select="$input/display"/>
            <xsl:text>:'</xsl:text>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="name">
          <xsl:attribute name="select">
            <xsl:text>'</xsl:text>
            <xsl:value-of select="$input/@name"/>
            <xsl:text>'</xsl:text>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="selectedkey">
          <xsl:attribute name="select">
            <xsl:value-of select="concat($input/xpath, '/@key')"/>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="colspan" select="'3'"/>
        <x:with-param name="imagetype" select="'body'"/>
        <x:with-param name="contenttest" select="'true'"/>
        <xsl:if test="$input/@required = 'true'">
          <x:with-param name="required" select="true()"/>
        </xsl:if>
      </x:call-template>
    </tr>
  </xsl:template>


  <xsl:template name="displayradiobutton">
    <xsl:param name="input"/>

    <xsl:variable name="xpath">
      <xsl:value-of select="$input/xpath"/>
    </xsl:variable>

    <xsl:variable name="inputname" select="$input/@name"/>

    <tr>
      <x:call-template name="labelcolumn">
        <x:with-param name="label">
          <xsl:value-of select="$input/display"/>
          <xsl:text>:</xsl:text>
        </x:with-param>
        <x:with-param name="required">
          <xsl:value-of select="($input/@required = 'true')"/>
        </x:with-param>
        <x:with-param name="fieldname">
          <xsl:value-of select="$input/@name"/>
        </x:with-param>
          <xsl:if test="$input/help">
              <x:with-param name="helpelement">
                  <xsl:copy-of select="$input/help"/>
              </x:with-param>
          </xsl:if>

      </x:call-template>

      <td nowrap="nowrap" valign="top">
        <xsl:if test="$input/help">
          <x:call-template name="displayhelp">
            <x:with-param name="fieldname">
              <xsl:value-of select="$input/@name"/>
            </x:with-param>
            <x:with-param name="helpelement">
              <xsl:copy-of select="$input/help"/>
            </x:with-param>
          </x:call-template>
        </xsl:if>

        <x:variable name="random">
            <xsl:value-of select="admin:random()"/>
        </x:variable>

        <div class="radiobutton-group">

          <xsl:variable name="isRequiredAndHasNoneChecked" select="$input/@required = 'true' and count($input/options/option[@checked = 'true']) = 0"/>

            <xsl:for-each select="$input/options/option">
                <input type="radio" value="{@value}">
                  <x:attribute name="name">
                    <x:text>rb:</x:text>
                    <x:value-of select="substring-after($random, '.')"/>
                    <x:text>:</x:text>
                    <xsl:value-of select="$inputname"/>
                  </x:attribute>
                <x:if test="$readonly">
                  <x:attribute name="disabled">true</x:attribute>
                </x:if>

                  <xsl:if test="$isRequiredAndHasNoneChecked and position() = 1">
                    <x:attribute name="checked">checked</x:attribute>
                  </xsl:if>

                <x:choose>
                  <x:when>
                    <xsl:attribute name="test">
                      <xsl:text>$create = '1'</xsl:text>
                    </xsl:attribute>
                    <xsl:if test="@checked = 'true'">
                      <x:attribute name="checked">checked</x:attribute>
                    </xsl:if>
                  </x:when>
                  <x:otherwise>
                    <x:if>
                      <xsl:attribute name="test">
                        <xsl:value-of select="$xpath"/>
                        = '<xsl:value-of select="@value"/>'
                      </xsl:attribute>
                      <x:attribute name="checked">checked</x:attribute>
                    </x:if>
                  </x:otherwise>
                </x:choose>
              </input>
              &nbsp;
              <xsl:value-of select="."/>
              <br/>
            </xsl:for-each>
        </div>

      </td>
    </tr>
  </xsl:template>

  <xsl:template name="displaymultiplechoice">
    <xsl:param name="input"/>

    <xsl:variable name="xpath">
        <xsl:value-of select="$input/xpath"/>
    </xsl:variable>

    <xsl:variable name="inputname" select="$input/@name"/>

    <xsl:variable name="minimumalternatives">
      <xsl:choose>
        <xsl:when test="$input/minimumalternatives">
          <xsl:value-of select="$input/minimumalternatives"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>2</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <x:variable name="minimumalternatives">
      <xsl:value-of select="$minimumalternatives"/>
    </x:variable>

    <script type="text/javascript" language="JavaScript">
      /*
        Method: storeCheckboxStates

          Store states of the checkboxes in a hidden input field.
      */
      function storeCheckboxStates() {
        var checkboxes = document.getElementsByName('<xsl:value-of select="$inputname"/>_checkbox');
        var checkbox_values = document.getElementsByName('<xsl:value-of select="$inputname"/>_checkbox_values');
        for (var i = 0; i &lt; checkboxes.length; i++) {
          checkbox_values[i].value = checkboxes[i].checked;
        }
      }
      // ---------------------------------------------------------------------------------------------------------------

      /*
        Method: updateCheckboxStates

          Update the checkboxes with data from a hidden input field.
      */
      function updateCheckboxStates() {
        var checkboxes = document.getElementsByName('<xsl:value-of select="$inputname"/>_checkbox');
        var checkbox_values = document.getElementsByName('<xsl:value-of select="$inputname"/>_checkbox_values');
        for (var i = 0; i &lt; checkboxes.length; i++) {
          if (checkbox_values[i].value == 'true')
            checkboxes[i].checked=checkbox_values[i].value;
        }
      }
      // ---------------------------------------------------------------------------------------------------------------

      /*
        Method: storeCheckboxStates

          set correct disabled and disabled buttons.
      */
      function setDisabledEnabledButtons( inputName )
      {
        var buttons = document.getElementsByName('moverupper_' + inputName);
        buttons[0].setAttribute("disabled", "disabled");
        buttons[0].childNodes[0].src = 'images/icon_move_up-disabled.gif';

        for ( var i = 1; i &lt; buttons.length; ++i )
        {
          if (buttons[i].disabled) {
            <x:if test="not($readonly)">
            buttons[i].removeAttribute("disabled");
            </x:if>
            buttons[i].childNodes[0].src = 'images/icon_move_up.gif';
          }
        }

        buttons = document.getElementsByName('moverdowner_' + inputName);
        buttons[buttons.length-1].setAttribute("disabled", "disabled");
        buttons[buttons.length-1].childNodes[0].src = 'images/icon_move_down-disabled.gif';
        for (var i = 0; i &lt; buttons.length - 1; ++i) {
          if (buttons[i].disabled) {
            <x:if test="not($readonly)">
            buttons[i].removeAttribute("disabled");
            </x:if>
            buttons[i].childNodes[0].src = 'images/icon_move_down.gif';
          }
        }
      }
      // ---------------------------------------------------------------------------------------------------------------

      /*
        Method: clearOrRemove

          Clear or remove a row in the optionstable.
      */
      function clearOrRemove(obj) {
        if (itemcount(document.getElementsByName(obj.name)) &lt;= <xsl:value-of select="$minimumalternatives"/>) {
          var idx = getObjectIndex(obj);
          document.getElementsByName('<xsl:value-of select="$inputname"/>_alternative')[idx].value = '';
          document.getElementsByName('<xsl:value-of select="$inputname"/>_checkbox')[idx].checked = false;
          document.getElementsByName('<xsl:value-of select="$inputname"/>_checkbox_values')[idx].value = '';
        } else {
          removeTableRow(obj, '<xsl:value-of select="$inputname"/>_table', null, 1);
        }
      }
    </script>
    <tr>
      <x:call-template name="labelcolumn">
        <x:with-param name="label">
          <xsl:value-of select="$input/display"/>
          <xsl:text>:</xsl:text>
        </x:with-param>
        <x:with-param name="required">
          <xsl:value-of select="($input/@required = 'true')"/>
        </x:with-param>
        <x:with-param name="fieldname">
          <xsl:value-of select="$input/@name"/>
        </x:with-param>
          <xsl:if test="$input/help">
              <x:with-param name="helpelement">
                  <xsl:copy-of select="$input/help"/>
              </x:with-param>
          </xsl:if>

      </x:call-template>

      <td nowrap="nowrap" valign="top">
        <xsl:if test="$input/help">
          <x:call-template name="displayhelp">
            <x:with-param name="fieldname">
              <xsl:value-of select="$input/@name"/>
            </x:with-param>
            <x:with-param name="helpelement">
              <xsl:copy-of select="$input/help"/>
            </x:with-param>
          </x:call-template>
        </xsl:if>
        <input type="text" name="{$inputname}">
          <xsl:attribute name="size">
            <xsl:choose>
              <xsl:when test="questionsize">
                <xsl:value-of select="questionsize/@value"/>
              </xsl:when>
              <xsl:otherwise>55</xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
          <x:attribute name="value">
            <x:value-of>
              <xsl:attribute name="select">
                <xsl:value-of select="$input/xpath"/>
                <xsl:text>/text</xsl:text>
              </xsl:attribute>
            </x:value-of>
          </x:attribute>
          <x:if test="$readonly">
            <x:attribute name="disabled">disabled</x:attribute>
          </x:if>
        </input>
      </td>
    </tr>
    <tr>
      <td valign="top" class="form_labelcolumn">
        <xsl:value-of select="$input/displayalternatives"/>
        <xsl:text>:</xsl:text>
        <xsl:if test="$input/@required = 'true'">
          <span class="requiredfield">*</span>
        </xsl:if>
      </td>

      <td nowrap="nowrap" valign="top">
        <table border="0" cellspacing="2" cellpadding="0">
          <tbody id="{$inputname}_table">

            <xsl:if test="$input/column1text or $input/column2text or $input/column3text">
              <tr>

                <x:call-template name="tablecolumnheader">
                  <x:with-param name="caption">
                    <xsl:value-of select="$input/column1text"/>
                  </x:with-param>
                  <x:with-param name="sortable" select="'false'"/>
                </x:call-template>
                <x:call-template name="tablecolumnheader">
                  <x:with-param name="caption">
                    <xsl:value-of select="$input/column2text"/>
                  </x:with-param>
                  <x:with-param name="sortable" select="'false'"/>
                </x:call-template>
                <x:call-template name="tablecolumnheader">
                  <x:with-param name="caption">
                    <xsl:value-of select="$input/column3text"/>
                  </x:with-param>
                  <x:with-param name="sortable" select="'false'"/>
                </x:call-template>

              </tr>
            </xsl:if>

            <xsl:variable name="temp">
              <xsl:value-of select="$xpath"/>
              <xsl:text>/alternative</xsl:text>
            </xsl:variable>

            <x:variable name="alternatives_xpath">
              <xsl:attribute name="select">
                <xsl:value-of select="exslt-common:node-set($temp)"/>
              </xsl:attribute>
            </x:variable>

            <x:variable name="alternatives">
              <x:choose>
                <x:when test="count($alternatives_xpath)>$minimumalternatives">
                  <x:value-of select="count($alternatives_xpath)"/>
                </x:when>
                <x:otherwise>
                  <xsl:value-of select="$minimumalternatives"/>
                </x:otherwise>
              </x:choose>
            </x:variable>

            <x:call-template name="multiplechoice_alternative">
              <x:with-param name="name">
                <xsl:value-of select="$inputname"/>
              </x:with-param>
              <x:with-param name="size">
                <xsl:value-of select="$input/alternativessize"/>
              </x:with-param>
              <x:with-param name="alternatives" select="$alternatives"/>
              <x:with-param name="input" select="$alternatives_xpath"/>
              <x:with-param name="readonly" select="$readonly"/>
            </x:call-template>

          </tbody>
        </table>

        <x:call-template name="button">
          <x:with-param name="type" select="'button'"/>
          <x:with-param name="disabled" select="$readonly"/>
          <x:with-param name="caption">
            <xsl:choose>
              <xsl:when test="$input/newbuttontext">
                <xsl:value-of select="$input/newbuttontext"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>%cmdNew%</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </x:with-param>
          <x:with-param name="onclick">
            <xsl:text>javascript:addTableRow('</xsl:text>
            <xsl:value-of select="$input/@name"/>
            <xsl:text>_table', 1, 1);setDisabledEnabledButtons('</xsl:text>
            <xsl:value-of select="$input/@name"/>
            <xsl:text>');</xsl:text>
          </x:with-param>
        </x:call-template>

      </td>
    </tr>

    <script type="text/javascript" language="JavaScript">
      setDisabledEnabledButtons('<xsl:value-of select="$input/@name"/>');
    </script>

  </xsl:template>

  <xsl:template name="displaytext">
    <xsl:param name="input"/>
    <tr>
      <x:call-template name="textfield">
        <x:with-param name="disabled" select="$readonly"/>
        <x:with-param name="helpelement">
          <xsl:copy-of select="$input/help"/>
        </x:with-param>
        <x:with-param name="name">
          <xsl:attribute name="select">
            <xsl:text>'</xsl:text>
            <xsl:value-of select="$input/@name"/>
            <xsl:text>'</xsl:text>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="label">
          <xsl:attribute name="select">
            <xsl:text>'</xsl:text>
            <xsl:value-of select="$input/display"/>
            <xsl:text>:'</xsl:text>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="selectnode">
          <x:choose>
            <x:when>
              <xsl:attribute name="test">
                <xsl:value-of select="$input/xpath"/>
              </xsl:attribute>
              <x:value-of>
                <xsl:attribute name="select">
                  <xsl:value-of select="$input/xpath"/>
                </xsl:attribute>
              </x:value-of>
            </x:when>
            <x:otherwise>
              <xsl:value-of select="$input/default"/>
            </x:otherwise>
          </x:choose>
        </x:with-param>
        <x:with-param name="size">
          <xsl:attribute name="select">
            <xsl:choose>
              <xsl:when test="$input/size">
                <xsl:text>'</xsl:text>
                <xsl:value-of select="$input/size/@value"/>
                <xsl:text>'</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>'60'</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="maxlength">
          <xsl:attribute name="select">
            <xsl:choose>
              <xsl:when test="$input/maxlength">
                <xsl:text>'</xsl:text>
                <xsl:value-of select="$input/maxlength/@value"/>
                <xsl:text>'</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                  <xsl:choose>
                      <xsl:when test="/config/form/title/@name = $input/@name">
                          <xsl:text>'256'</xsl:text>
                      </xsl:when>
                      <xsl:otherwise>
                          <xsl:text>''</xsl:text>
                      </xsl:otherwise>
                  </xsl:choose>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="colspan" select="'3'"/>
        <xsl:if test="$input/@required = 'true'">
          <x:with-param name="required" select="'true'"/>
        </xsl:if>
      </x:call-template>
    </tr>
  </xsl:template>

  <xsl:template name="displaytextarea">
    <xsl:param name="input"/>
    <xsl:param name="xml" select="'false'"/>
    <tr>
      <x:call-template name="textarea">
        <x:with-param name="xml">
          <xsl:value-of select="$xml"/>
        </x:with-param>
        <x:with-param name="disabled" select="$readonly"/>
        <x:with-param name="helpelement">
          <xsl:copy-of select="$input/help"/>
        </x:with-param>
        <x:with-param name="name">
          <xsl:attribute name="select">
            <xsl:text>'</xsl:text>
            <xsl:value-of select="$input/@name"/>
            <xsl:text>'</xsl:text>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="label">
          <xsl:attribute name="select">
            <xsl:text>'</xsl:text>
            <xsl:value-of select="$input/display"/>
            <xsl:text>:'</xsl:text>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="selectnode">
          <xsl:attribute name="select">
            <xsl:value-of select="$input/xpath"/>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="default">
          <xsl:value-of select="$input/default"/>
        </x:with-param>

        <x:with-param name="rows">
          <xsl:attribute name="select">
            <xsl:choose>
              <xsl:when test="$input/rows">
                <xsl:text>'</xsl:text>
                <xsl:value-of select="$input/rows/@value"/>
                <xsl:text>'</xsl:text>
              </xsl:when>
              <xsl:when test="$xml = 'true'">
                <xsl:text>'15'</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>'10'</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="cols">
          <xsl:attribute name="select">
            <xsl:choose>
              <xsl:when test="$input/cols">
                <xsl:text>'</xsl:text>
                <xsl:value-of select="$input/cols/@value"/>
                <xsl:text>'</xsl:text>
              </xsl:when>
              <xsl:when test="$xml = 'true'">
                <xsl:text>'95'</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>'60'</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="colspan" select="'3'"/>
        <xsl:if test="$input/@required = 'true'">
          <x:with-param name="required" select="'true'"/>
        </xsl:if>
      </x:call-template>
    </tr>
  </xsl:template>

  <xsl:template name="displaydatefield">
    <xsl:param name="input"/>
    <tr>
      <x:call-template name="textfielddate">
        <x:with-param name="disabled" select="$readonly"/>
        <x:with-param name="helpelement">
          <xsl:copy-of select="$input/help"/>
        </x:with-param>
        <x:with-param name="indexcallback" select="'getGroupIndex'"/>
        <x:with-param name="name">
          <xsl:attribute name="select">
            <xsl:text>'</xsl:text>
            <xsl:value-of select="$input/@name"/>
            <xsl:text>'</xsl:text>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="label">
          <xsl:attribute name="select">
            <xsl:text>'</xsl:text>
            <xsl:value-of select="$input/display"/>
            <xsl:text>:'</xsl:text>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="selectnode">
          <x:choose>
            <x:when>
              <xsl:attribute name="test">
                <xsl:value-of select="$input/xpath"/>
              </xsl:attribute>
              <x:value-of>
                <xsl:attribute name="select">
                  <xsl:value-of select="$input/xpath"/>
                </xsl:attribute>
              </x:value-of>
            </x:when>
            <x:otherwise>
              <xsl:value-of select="$input/default"/>
            </x:otherwise>
          </x:choose>
        </x:with-param>
        <x:with-param name="size">
          <xsl:attribute name="select">
            <xsl:choose>
              <xsl:when test="$input/size">
                <xsl:text>'</xsl:text>
                <xsl:value-of select="$input/size/@value"/>
                <xsl:text>'</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>'60'</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="maxlength">
          <xsl:attribute name="select">
            <xsl:choose>
              <xsl:when test="$input/maxlength">
                <xsl:text>'</xsl:text>
                <xsl:value-of select="$input/maxlength/@value"/>
                <xsl:text>'</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:text>''</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
        </x:with-param>
        <x:with-param name="colspan" select="'3'"/>
        <xsl:if test="$input/@required = 'true'">
          <x:with-param name="required" select="true()"/>
        </xsl:if>
      </x:call-template>
    </tr>
  </xsl:template>

  <xsl:template name="displaysimplehtmlarea">
    <xsl:param name="input"/>
    <tr>
      <xsl:variable name="configxpath">
        <xsl:text>/contents/htmleditorconfig[@name='</xsl:text>
        <xsl:value-of select="$input/@name"/>
        <xsl:text>']</xsl:text>
      </xsl:variable>
    </tr>
  </xsl:template>

  <xsl:template name="displayreadonly">
    <xsl:param name="input"/>
    <tr>
      <x:call-template name="labelcolumn">
        <x:with-param name="label">
          <xsl:value-of select="$input/display"/>
          <xsl:text>:</xsl:text>
        </x:with-param>
        <x:with-param name="required">
              <xsl:value-of select="($input/@required = 'true')"/>
            </x:with-param>
        <x:with-param name="fieldname">
          <xsl:value-of select="$input/@name"/>
        </x:with-param>
          <xsl:if test="$input/help">
              <x:with-param name="helpelement">
                  <xsl:copy-of select="$input/help"/>
              </x:with-param>
          </xsl:if>

      </x:call-template>
      
      <td colspan="3">
        <xsl:if test="$input/help">
          <x:call-template name="displayhelp">
            <x:with-param name="fieldname">
              <xsl:value-of select="$input/@name"/>
            </x:with-param>
            <x:with-param name="helpelement">
              <xsl:copy-of select="$input/help"/>
            </x:with-param>
          </x:call-template>
        </xsl:if>

        <xsl:choose>
          <xsl:when test="$input/@type = 'xml'">
            <input type="hidden" name="{$input/@name}">
              <x:attribute name="value">
                <x:call-template name="serialize">
                  <x:with-param name="xpath">
                    <xsl:attribute name="select">
                      <xsl:value-of select="$input/xpath"/>
                    </xsl:attribute>
                  </x:with-param>
                </x:call-template>
              </x:attribute>
            </input>
            <textarea readonly="true">
              <x:attribute name="class">
                <x:text>no-border</x:text>
                <x:if test="$readonly">
                  <x:text> disabled-element</x:text>
                </x:if>
              </x:attribute>
              <x:attribute name="cols">
                <xsl:choose>
                  <xsl:when test="$input/cols">
                    <xsl:value-of select="$input/cols/@value"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>60</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
              </x:attribute>

              <x:attribute name="rows">
                <xsl:choose>
                  <xsl:when test="$input/rows">
                    <xsl:value-of select="$input/rows/@value"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>10</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
              </x:attribute>

              <x:call-template name="serialize">
                <x:with-param name="xpath">
                  <xsl:attribute name="select">
                    <xsl:value-of select="$input/xpath"/>
                  </xsl:attribute>
                </x:with-param>
              </x:call-template>
            </textarea>
          </xsl:when>
          <xsl:when test="$input/@type = 'textarea'">
            <textarea style="display: none" name="{$input/@name}">
              <x:value-of>
                <xsl:attribute name="select">
                  <xsl:value-of select="$input/xpath"/>
                </xsl:attribute>
              </x:value-of>
            </textarea>
            <textarea readonly="true">
              <x:attribute name="class">
                <x:text>no-border</x:text>
                <x:if test="$readonly">
                  <x:text> disabled-element</x:text>
                </x:if>
              </x:attribute>
              <x:attribute name="cols">
                <xsl:choose>
                  <xsl:when test="$input/cols">
                    <xsl:value-of select="$input/cols/@value"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>60</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
              </x:attribute>

              <x:attribute name="rows">
                <xsl:choose>
                  <xsl:when test="$input/rows">
                    <xsl:value-of select="$input/rows/@value"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>10</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
              </x:attribute>

              <x:value-of>
                <xsl:attribute name="select">
                  <xsl:value-of select="$input/xpath"/>
                </xsl:attribute>
              </x:value-of>
            </textarea>
          </xsl:when>
          <xsl:otherwise>

            <!-- Text, Date, URL -->
            <x:variable name="value">
              <x:choose>
                <!-- Has user value -->
                <x:when>
                  <xsl:attribute name="test">
                    <xsl:value-of select="$input/xpath"/>
                  </xsl:attribute>
                  <xsl:choose>
                    <xsl:when test="$input/@type = 'date'">
                      <x:call-template name="formatdate">
                        <x:with-param name="date">
                          <x:value-of>
                            <xsl:attribute name="select">
                              <xsl:value-of select="$input/xpath"/>
                            </xsl:attribute>
                          </x:value-of>
                        </x:with-param>
                      </x:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                      <x:value-of>
                        <xsl:attribute name="select">
                          <xsl:value-of select="$input/xpath"/>
                        </xsl:attribute>
                      </x:value-of>
                    </xsl:otherwise>
                  </xsl:choose>
                </x:when>
                <!-- Config default value -->
                <x:otherwise>
                  <xsl:choose>
                    <xsl:when test="$input/@type = 'date'">
                      <x:call-template name="formatdate">
                        <x:with-param name="date">
                          <xsl:value-of select="$input/default"/>
                        </x:with-param>
                      </x:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="$input/default"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </x:otherwise>
              </x:choose>
            </x:variable>

            <xsl:variable name="name">
              <xsl:choose>
                <xsl:when test="$input/@type = 'date'">
                  <xsl:value-of select="concat('date', $input/@name)"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$input/@name"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <input type="hidden" name="{$name}">
              <x:attribute name="value">
                <x:value-of select="$value"/>
              </x:attribute>
            </input>
            <span>
              <x:if test="$readonly">
                <x:attribute name="class">
                  <x:text>disabled-element</x:text>
                </x:attribute>
              </x:if>
              <x:value-of select="$value"/>
            </span>
          </xsl:otherwise>
        </xsl:choose>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="displayhtmlarea">
    <xsl:param name="input"/>
    <tr>
      <x:call-template name="labelcolumn">
        <x:with-param name="label">
          <xsl:value-of select="$input/display"/>
          <xsl:text>:</xsl:text>
        </x:with-param>
        <x:with-param name="required">
          <xsl:value-of select="($input/@required = 'true')"/>
        </x:with-param>
        <x:with-param name="fieldname">
          <xsl:value-of select="$input/@name"/>
        </x:with-param>
          <xsl:if test="$input/help">
              <x:with-param name="helpelement">
                  <xsl:copy-of select="$input/help"/>
              </x:with-param>
          </xsl:if>
        <x:with-param name="valign">
          <xsl:text>top</xsl:text>
        </x:with-param>
      </x:call-template>
      <td colspan="3">
        <xsl:variable name="configxpath">
          <xsl:text>/contents/htmleditorconfig[@name='</xsl:text>
          <xsl:value-of select="$input/@name"/>
          <xsl:text>']</xsl:text>
        </xsl:variable>

        <x:call-template name="xhtmleditor">
          <x:with-param name="id">
            <xsl:attribute name="select">
              <xsl:text>'</xsl:text>
              <xsl:value-of select="$input/@name"/>
              <xsl:text>'</xsl:text>
            </xsl:attribute>
          </x:with-param>
          <x:with-param name="name">
            <xsl:attribute name="select">
              <xsl:text>'</xsl:text>
              <xsl:value-of select="$input/@name"/>
              <xsl:text>'</xsl:text>
            </xsl:attribute>
          </x:with-param>
          <x:with-param name="content">
            <xsl:attribute name="select">
              <xsl:value-of select="$input/xpath"/>
            </xsl:attribute>
          </x:with-param>
          <xsl:if test="$input/@config">
            <x:with-param name="config">
              <xsl:attribute name="select">
                <xsl:text>'</xsl:text>
                <xsl:value-of select="$input/@config"/>
                <xsl:text>'</xsl:text>
              </xsl:attribute>
            </x:with-param>
          </xsl:if>
          <xsl:if test="count($input/buttons) !=0">
            <xsl:variable name="rowStr">
              <xsl:for-each select="$input/buttons">
                <xsl:value-of select="concat('﻿theme_advanced_container_row', position(), ': ', .)"/>
                <xsl:if test="position() != last()">;</xsl:if>
              </xsl:for-each>
            </xsl:variable>
            <x:with-param name="buttonRows">
              <xsl:attribute name="select">
                '<xsl:value-of select="$rowStr"/>'
              </xsl:attribute>
            </x:with-param>
          </xsl:if>
          <x:with-param name="customcss" select="$csskey"/>
          <xsl:if test="$input/@width">
            <x:with-param name="width">
              <xsl:attribute name="select">
                <xsl:value-of select="$input/@width"/>
              </xsl:attribute>
            </x:with-param>
          </xsl:if>
          <xsl:if test="$input/@height">
            <x:with-param name="height">
              <xsl:attribute name="select">
                <xsl:value-of select="$input/@height"/>
              </xsl:attribute>
            </x:with-param>
          </xsl:if>
          <x:with-param name="helpelement">
            <xsl:copy-of select="$input/help"/>
          </x:with-param>
          <x:with-param name="menukey" select="$menukey"/>
          <x:with-param name="disabled" select="$readonly"/>
          <xsl:if test="$input/@readonly = 'true'">
            <x:with-param name="readonly">
              <xsl:value-of select="$input/@readonly = 'true'"/>
            </x:with-param>
          </xsl:if>
          <x:with-param name="accessToHtmlSource" select="$accessToHtmlSource = 'true'"/>
          <xsl:if test="$input/@required = 'true'">
            <x:with-param name="required" select="true()"/>
          </xsl:if>
          <xsl:if test="$input/@inlinepopups = 'true'">
            <x:with-param name="inlinePopups" select="true()"/>
          </xsl:if>
          <xsl:if test="$input/block-format-elements != ''">
            <x:with-param name="block-format-elements">
              <xsl:attribute name="select">
                <xsl:text>'</xsl:text>
                <xsl:value-of select="$input/block-format-elements"/>
                <xsl:text>'</xsl:text>
              </xsl:attribute>
            </x:with-param>
          </xsl:if>
        </x:call-template>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="uploadfile">
    <xsl:param name="input"/>

    <x:variable name="binarykey">
      <x:value-of select="{concat($input/xpath, '/binarydata/@key')}"/>
    </x:variable>

    <x:variable name="binaryelem" select="/contents/content/binaries/binary[@key = $binarykey]"/>

    <tr>
      <x:call-template name="labelcolumn">
        <x:with-param name="label">
          <xsl:value-of select="$input/display"/>
          <xsl:text>:</xsl:text>
        </x:with-param>
        <x:with-param name="required">
          <xsl:value-of select="($input/@required = 'true')"/>
        </x:with-param>
        <x:with-param name="fieldname">
          <xsl:value-of select="$input/@name"/>
        </x:with-param>
          <xsl:if test="$input/help">
              <x:with-param name="helpelement">
                  <xsl:copy-of select="$input/help"/>
              </x:with-param>
          </xsl:if>

      </x:call-template>

      <td>
        <xsl:if test="$input/help">
          <x:call-template name="displayhelp">
            <x:with-param name="fieldname">
              <xsl:value-of select="$input/@name"/>
            </x:with-param>
            <x:with-param name="helpelement">
              <xsl:copy-of select="$input/help"/>
            </x:with-param>
          </x:call-template>
        </xsl:if>

        <input type="hidden" name="{$input/@name}">
          <x:attribute name="value">
            <x:choose>
              <x:when test="$binarykey != '%0'">
                <x:value-of select="$binarykey"/>
              </x:when>
              <x:otherwise>
                <x:text></x:text>
              </x:otherwise>
            </x:choose>
          </x:attribute>
        </input>

        <script language="javascript">
          /*
           Method: getFilename
          */
          function getFilename(iname, obj, fname) {
            var idx = getObjectIndex(obj);
            var newName = fname.match(/[^\/\\]+$/);
            document.getElementsByName('filename_'+iname)[idx].value = newName;
          }
          // -----------------------------------------------------------------------------------------------------------

          /*
           Method: removeFile
          */
          function removeFile(iname, obj) {
            var idx = getObjectIndex(obj);
            var el = document.getElementsByName('filename_'+iname)[idx];
            var parent = el.parentNode;

            if (!parent)
              return;

            document.getElementsByName('filename_'+iname)[idx].value = "";

            document.getElementsByName(iname)[idx].value = "";
            var link = document.getElementsByName(iname+'_link')[idx];
            var br = parent.getElementsByTagName('br')[0];
            if (link) {
              parent.removeChild(link);
              if (br)
                parent.removeChild(br);
            }

            //Because of security implications it is not posible to set the value of an input element with type=file
            //Posible solution: Remove the element and build it again.
            var tempHTML = parent.innerHTML;
            parent.innerHTML = '';
            parent.innerHTML = tempHTML;
          }
        </script>

        <input type="hidden" name="{concat('filename_', $input/@name)}">
          <x:if test="$binaryelem/@filename">
            <x:attribute name="value">
              <x:value-of select="$binaryelem/@filename"/>
            </x:attribute>
          </x:if>
        </input>

        <x:choose>
          <x:when test="$binaryelem/@filename != ''">
            <a name="{$input/@name}_link" target="_blank">
              <x:attribute name="href">
                <x:text>_attachment/</x:text>
                <x:value-of select="/contents/content/@key"/>
                <x:text>/binary/</x:text>
                <x:value-of select="$binarykey"/>
              </x:attribute>
              <x:value-of select="$binaryelem/@filename"/>
            </a>
            <br/>
          </x:when>
          <x:otherwise>
            <a name="{$input/@name}_link"/>
          </x:otherwise>
        </x:choose>

        <input type="file" size="40">
          <xsl:attribute name="onchange">
            <xsl:text>javascript:getFilename('</xsl:text>
            <xsl:value-of select="$input/@name"/>
            <xsl:text>', this, this.value);</xsl:text>
          </xsl:attribute>
          <xsl:attribute name="name">
            <xsl:value-of select="concat('f_', $input/@name)"/>
          </xsl:attribute>
          <x:if test="$readonly">
            <x:attribute name="disabled">disabled</x:attribute>
          </x:if>
        </input>
        <xsl:text>&nbsp;</xsl:text>
        <x:call-template name="button">
          <x:with-param name="image" select="'images/icon_remove.gif'"/>
          <x:with-param name="name" select="'{$input/@name}_removebutton'"/>
          <x:with-param name="disabled" select="$readonly"/>
          <x:with-param name="onclick">
            <xsl:text>javascript:removeFile('</xsl:text>
            <xsl:value-of select="$input/@name"/>
            <xsl:text>', this);</xsl:text>
          </x:with-param>
        </x:call-template>
        <br/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template name="appended_input">
    <xsl:param name="input"/>
    <xsl:element name="input">
      <xsl:attribute name="type">
        <xsl:value-of select="@type"/>
      </xsl:attribute>
      <xsl:attribute name="name">
        <xsl:value-of select="@name"/>
      </xsl:attribute>
      <xsl:if test="@required">
        <xsl:attribute name="required">
          <xsl:value-of select="@required"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@multiple">
        <xsl:attribute name="multiple">
          <xsl:value-of select="@multiple"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@index">
        <xsl:attribute name="index">
          <xsl:value-of select="@index"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@width">
        <xsl:attribute name="width">
          <xsl:value-of select="@width"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@height">
        <xsl:attribute name="height">
          <xsl:value-of select="@height"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@readonly">
        <xsl:attribute name="readonly">
          <xsl:value-of select="@readonly"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@config">
        <xsl:attribute name="config">
          <xsl:value-of select="@config"/>
        </xsl:attribute>
      </xsl:if>

      <xsl:copy-of select="display"/>
      <xsl:element name="xpath">
        <xsl:text>/contents/content/</xsl:text>
        <xsl:value-of select="xpath"/>
      </xsl:element>
      <xsl:for-each select="*[name() != 'display' and name() != 'xpath']">
        <xsl:copy-of select="."/>
      </xsl:for-each>
    </xsl:element>
  </xsl:template>

  <xsl:template name="replaceSubstring">
    <xsl:param name="inputString"/>
    <xsl:param name="inputSubstring">
      <xsl:text>
      </xsl:text>
    </xsl:param>
    <xsl:param name="outputSubstring" select="'&lt;br/&gt;'"/>
    <xsl:choose>
      <xsl:when test="contains($inputString, $inputSubstring)">
        <xsl:value-of disable-output-escaping="yes"
                      select="concat(substring-before($inputString, $inputSubstring), $outputSubstring)"/>
        <xsl:call-template name="replaceSubstring">
          <xsl:with-param name="inputString" select="substring-after($inputString, $inputSubstring)"/>
          <xsl:with-param name="inputSubstring" select="$inputSubstring"/>
          <xsl:with-param name="outputSubstring" select="$outputSubstring"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of disable-output-escaping="yes" select="$inputString"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>

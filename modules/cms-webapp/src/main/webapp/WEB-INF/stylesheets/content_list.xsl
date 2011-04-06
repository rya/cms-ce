<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>

<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

    <xsl:output method="html"/>

    <xsl:include href="common/generic_parameters.xsl"/>
    <xsl:include href="common/string.xsl"/>
    <xsl:include href="common/serialize.xsl"/>
    <xsl:include href="common/button.xsl"/>
    <xsl:include href="common/labelcolumn.xsl"/>
    <xsl:include href="common/textarea.xsl"/>
    <xsl:include href="common/contentbrowsemenu.xsl"/>
    <xsl:include href="common/contentsearchresultsmenu.xsl"/>
    <xsl:include href="common/paging.xsl"/>
    <xsl:include href="common/formatdate.xsl"/>
    <xsl:include href="common/publishstatus.xsl"/>
    <xsl:include href="common/tablecolumnheader.xsl"/>
    <xsl:include href="common/tablerowpainter.xsl"/>
    <xsl:include href="common/displaysystempath.xsl"/>
    <xsl:include href="common/newcontentoperations.xsl"/>
    <xsl:include href="common/displayhelp.xsl"/>
    <xsl:include href="common/displayerror.xsl"/>
    <xsl:include href="common/displaycontentpath.xsl"/>
    <xsl:include href="common/escapequotes.xsl"/>
    <xsl:include href="common/displayfeedback.xsl"/>
    <xsl:include href="common/waitsplash.xsl"/>
    <xsl:include href="common/display-icon.xsl"/>
    <xsl:include href="common/display-content-icon.xsl"/>
    <xsl:include href="menu/header.xsl"/>
    <xsl:include href="column_templates/default.xsl"/>
    <xsl:include href="common/textfielddate.xsl"/>
    <xsl:include href="common/textfielddatetime.xsl"/>
    <xsl:include href="common/searchfield.xsl"/>
    <xsl:include href="common/advanced_search_form.xsl"/>
    <xsl:include href="common/user-picker-with-autocomplete.xsl"/>

    <xsl:param name="sortby"/>
    <xsl:param name="sortby-direction"/>

    <xsl:param name="contenthandler"/>

    <xsl:param name="index"/>
    <xsl:param name="count"/>
    <xsl:param name="reload"/>
    <xsl:param name="op"/>
    <xsl:param name="subop"/>
    <xsl:param name="hasAdminBrowse"/>
    <xsl:param name="hasCategoryRead"/>
    <xsl:param name="hasCategoryCreate"/>
    <xsl:param name="hasCategoryPublish"/>
    <xsl:param name="hasCategoryAdministrate"/>
    <xsl:param name="searchtype"/>
    <xsl:param name="fieldname"/>
    <xsl:param name="fieldrow"/>
    <xsl:param name="contenttypestring"/>
    <!-- params needed by simple search -->
    <xsl:param name="searchtext"/>
    <xsl:param name="scope"/>
    <!-- params needed by advanced search -->
    <xsl:param name="asearchtext"/>
    <xsl:param name="ascope"/>
    <xsl:param name="subcategories"/>
    <xsl:param name="state"/>
    <xsl:param name="owner"/>
    <xsl:param name="owner.uid"/>
    <xsl:param name="owner.fullName"/>
    <xsl:param name="owner.qualifiedName"/>
    <xsl:param name="modifier"/>
    <xsl:param name="modifier.uid"/>
    <xsl:param name="modifier.fullName"/>
    <xsl:param name="modifier.qualifiedName"/>
    <xsl:param name="lastmodified"/>
    <xsl:param name="poperator"/>
    <xsl:param name="priority"/>
    <xsl:param name="searchonly" select="'false'"/>
    <xsl:param name="filter"/>
    <xsl:param name="created"/>
    <xsl:param name="created.op"/>
    <xsl:param name="modified"/>
    <xsl:param name="modified.op"/>
    <xsl:param name="assignment.assigneeUserKey"/>
    <xsl:param name="assignment.assigneeDisplayName"/>
    <xsl:param name="assignment.assigneeQualifiedName"/>
    <xsl:param name="assignment.assignerUserKey"/>
    <xsl:param name="assignment.assignerDisplayName"/>
    <xsl:param name="assignment.assignerQualifiedName"/>
    <xsl:param name="assignment.dueDate"/>
    <xsl:param name="assignment.dueDate.op"/>
    <xsl:param name="duedate"/>
    <xsl:param name="acontentkey"/>
    <xsl:param name="datecreated"/>
    <xsl:param name="allowed-contenttypes-for-advanced-search"/>
    <xsl:param name="selectedtabpage"/>
    <xsl:param name="minoccurrence"/>
    <xsl:param name="maxoccurrence"/>

  <xsl:variable name="popupmode" select="not($subop = 'browse')"/>

    <xsl:variable name="pageURLWithoutSearch">
      <xsl:text>adminpage?op=</xsl:text>
      <xsl:value-of select="$op"/>

      <xsl:text>&amp;index=</xsl:text><xsl:value-of select="$index"/>
      <xsl:text>&amp;count=</xsl:text><xsl:value-of select="$count"/>

      <xsl:text>&amp;page=</xsl:text>
      <xsl:value-of select="$page"/>
      <xsl:text>&amp;categorykey=</xsl:text>
      <xsl:value-of select="$cat"/>
      <xsl:text>&amp;selectedunitkey=</xsl:text>
      <xsl:value-of select="$selectedunitkey"/>
      <xsl:text>&amp;subop=</xsl:text>
      <xsl:value-of select="$subop"/>
      <xsl:if test="$fieldname">
        <xsl:text>&amp;fieldname=</xsl:text>
        <xsl:value-of select="$fieldname"/>
      </xsl:if>
      <xsl:if test="$fieldrow">
        <xsl:text>&amp;fieldrow=</xsl:text>
        <xsl:value-of select="$fieldrow"/>
      </xsl:if>
      <xsl:if test="$contenttypestring">
        <xsl:text>&amp;contenttypestring=</xsl:text>
        <xsl:value-of select="$contenttypestring"/>
      </xsl:if>
      <xsl:if test="$contenthandler">
        <xsl:text>&amp;contenthandler=</xsl:text>
        <xsl:value-of select="$contenthandler"/>
      </xsl:if>
      <xsl:if test="$minoccurrence">
        <xsl:text>&amp;minoccurrence=</xsl:text>
        <xsl:value-of select="$minoccurrence"/>
      </xsl:if>
      <xsl:if test="$maxoccurrence">
        <xsl:text>&amp;maxoccurrence=</xsl:text>
        <xsl:value-of select="$maxoccurrence"/>
      </xsl:if>
    </xsl:variable>

  <xsl:variable name="pageURL">
    <xsl:value-of select="$pageURLWithoutSearch"/>
      <xsl:choose>
        <xsl:when test="$searchtype = 'simple'">
          <xsl:text>&amp;searchtype=</xsl:text>
          <xsl:value-of select="$searchtype"/>
          <xsl:text>&amp;searchtext=</xsl:text>
          <xsl:value-of select="$searchtext"/>
          <xsl:text>&amp;scope=</xsl:text>
          <xsl:value-of select="$scope"/>
        </xsl:when>
        <xsl:when test="$searchtype = 'advanced'">
          <xsl:text>&amp;searchtype=</xsl:text>
          <xsl:value-of select="$searchtype"/>
          <xsl:text>&amp;asearchtext=</xsl:text>
          <xsl:value-of select="$asearchtext"/>
          <xsl:text>&amp;ascope=</xsl:text>
          <xsl:value-of select="$ascope"/>
          <xsl:text>&amp;subcategories=</xsl:text>
          <xsl:value-of select="$subcategories"/>
          <xsl:text>&amp;state=</xsl:text>
          <xsl:value-of select="$state"/>
          <xsl:text>&amp;owner=</xsl:text>
          <xsl:value-of select="$owner"/>
          <xsl:text>&amp;lastmodified=</xsl:text>
          <xsl:value-of select="$lastmodified"/>
          <xsl:text>&amp;poperator=</xsl:text>
          <xsl:value-of select="$poperator"/>
          <xsl:text>&amp;priority=</xsl:text>
          <xsl:value-of select="$priority"/>
          <xsl:text>&amp;filter=</xsl:text>
          <xsl:value-of select="$filter"/>
          <xsl:text>&amp;owner.uid=</xsl:text>
          <xsl:value-of select="$owner.uid"/>
          <xsl:text>&amp;owner.fullName=</xsl:text>
          <xsl:value-of select="$owner.fullName"/>
          <xsl:text>&amp;modifier=</xsl:text>
          <xsl:value-of select="$modifier"/>
          <xsl:text>&amp;modifier.uid=</xsl:text>
          <xsl:value-of select="$modifier.uid"/>
          <xsl:text>&amp;modifier.fullName=</xsl:text>
          <xsl:value-of select="$modifier.fullName"/>
          <xsl:text>&amp;datecreated=</xsl:text>
          <xsl:value-of select="$datecreated"/>
          <xsl:text>&amp;created=</xsl:text>
          <xsl:value-of select="$created"/>
          <xsl:text>&amp;modified=</xsl:text>
          <xsl:value-of select="$modified"/>
          <xsl:text>&amp;owner.qualifiedName=</xsl:text>
          <xsl:value-of select="$owner.qualifiedName"/>
          <xsl:text>&amp;modifier.qualifiedName=</xsl:text>
          <xsl:value-of select="$modifier.qualifiedName"/>
          <xsl:text>&amp;_assignee=</xsl:text>
          <xsl:value-of select="$assignment.assigneeUserKey"/>
          <xsl:text>&amp;_assigner=</xsl:text>
          <xsl:value-of select="$assignment.assignerUserKey"/>
          <xsl:text>&amp;duedate=</xsl:text>
          <xsl:value-of select="$duedate"/>
          <xsl:text>&amp;date_assignmentDueDate=</xsl:text>
          <xsl:value-of select="$duedate"/>
          <xsl:text>&amp;_assignmentDueDate.op=</xsl:text>
          <xsl:value-of select="$assignment.dueDate.op"/>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="pageURLWithSorting">
    	<xsl:value-of select="$pageURL"/>
    	<xsl:if test="$sortby">
			<xsl:text>&amp;sortby=</xsl:text><xsl:value-of select="$sortby"/>
			<xsl:text>&amp;sortby-direction=</xsl:text><xsl:value-of select="$sortby-direction"/>
		</xsl:if>
    </xsl:variable>

    <!-- Additional next/from parameters -->
    <xsl:variable name="extraparams">
      <xsl:choose>
        <xsl:when test="$searchtype = 'simple'">
          <!-- params needed by simple search -->
          <xsl:text>&amp;searchtype=</xsl:text>
          <xsl:value-of select="$searchtype"/>
          <xsl:text>&amp;searchtext=</xsl:text>
          <xsl:value-of select="$searchtext"/>
          <xsl:text>&amp;scope=</xsl:text>
          <xsl:value-of select="$scope"/>
        </xsl:when>
        <xsl:when test="$searchtype = 'advanced'">
          <!-- params needed by advanced search -->
          <xsl:text>&amp;searchtype=</xsl:text>
          <xsl:value-of select="$searchtype"/>
          <xsl:text>&amp;asearchtext=</xsl:text>
          <xsl:value-of select="$asearchtext"/>
          <xsl:text>&amp;ascope=</xsl:text>
          <xsl:value-of select="$ascope"/>
          <xsl:text>&amp;subcategories=</xsl:text>
          <xsl:value-of select="$subcategories"/>
          <xsl:text>&amp;state=</xsl:text>
          <xsl:value-of select="$state"/>
          <xsl:text>&amp;lastmodified=</xsl:text>
          <xsl:value-of select="$lastmodified"/>
          <xsl:text>&amp;poperator=</xsl:text>
          <xsl:value-of select="$poperator"/>
          <xsl:text>&amp;priority=</xsl:text>
          <xsl:value-of select="$priority"/>
          <xsl:text>&amp;filter=</xsl:text>
          <xsl:value-of select="$filter"/>
          <xsl:text>&amp;owner=</xsl:text>
          <xsl:value-of select="$owner"/>
          <xsl:text>&amp;owner.uid=</xsl:text>
          <xsl:value-of select="$owner.uid"/>
          <xsl:text>&amp;owner.fullName=</xsl:text>
          <xsl:value-of select="$owner.fullName"/>
          <xsl:text>&amp;owner.qualifiedName=</xsl:text>
          <xsl:value-of select="$owner.qualifiedName"/>
          <xsl:text>&amp;modifier=</xsl:text>
          <xsl:value-of select="$modifier"/>
          <xsl:text>&amp;modifier.uid=</xsl:text>
          <xsl:value-of select="$modifier.uid"/>
          <xsl:text>&amp;modifier.fullName=</xsl:text>
          <xsl:value-of select="$modifier.fullName"/>
          <xsl:text>&amp;modifier.qualifiedName=</xsl:text>
          <xsl:value-of select="$modifier.qualifiedName"/>
          <xsl:text>&amp;created=</xsl:text>
          <xsl:value-of select="$created"/>
          <xsl:text>&amp;datecreated=</xsl:text>
          <xsl:value-of select="$created"/>
          <xsl:text>&amp;created.op=</xsl:text>
          <xsl:value-of select="$created.op"/>
          <xsl:text>&amp;modified=</xsl:text>
          <xsl:value-of select="$modified"/>
          <xsl:text>&amp;datemodified=</xsl:text>
          <xsl:value-of select="$modified"/>
          <xsl:text>&amp;modified.op=</xsl:text>
          <xsl:value-of select="$modified.op"/>
          <xsl:text>&amp;cat=</xsl:text>
          <xsl:value-of select="$cat"/>
          <xsl:text>&amp;modified=</xsl:text>
          <xsl:value-of select="$modified"/>
          <xsl:text>&amp;_assignee=</xsl:text>
          <xsl:value-of select="$assignment.assigneeUserKey"/>
          <xsl:text>&amp;_assigner=</xsl:text>
          <xsl:value-of select="$assignment.assignerUserKey"/>
          <xsl:text>&amp;duedate=</xsl:text>
          <xsl:value-of select="$duedate"/>
          <xsl:text>&amp;date_assignmentDueDate=</xsl:text>
          <xsl:value-of select="$duedate"/>
          <xsl:text>&amp;_assignmentDueDate.op=</xsl:text>
          <xsl:value-of select="$assignment.dueDate.op"/>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="urlForNavigation">
      <xsl:text>adminpage?page=</xsl:text>
      <xsl:value-of select="$page"/>
      <xsl:text>&amp;op=</xsl:text>
      <xsl:value-of select="$op"/>
      <xsl:text>&amp;subop=</xsl:text>
      <xsl:value-of select="$subop"/>
      <xsl:text>&amp;fieldname=</xsl:text>
      <xsl:value-of select="$fieldname"/>
      <xsl:text>&amp;fieldrow=</xsl:text>
      <xsl:value-of select="$fieldrow"/>
      <xsl:text>&amp;contenttypestring=</xsl:text>
      <xsl:value-of select="$contenttypestring"/>
      <xsl:text>&amp;categorykey=</xsl:text>
      <xsl:value-of select="$cat"/>
      <xsl:text>&amp;sortby=</xsl:text>
      <xsl:value-of select="$sortby"/>
      <xsl:text>&amp;sortby-direction=</xsl:text>
      <xsl:value-of select="$sortby-direction"/>
      <xsl:text>&amp;contenthandler=</xsl:text>
      <xsl:value-of select="$contenthandler"/>
      <xsl:value-of select="$extraparams"/>
      <xsl:if test="$minoccurrence">
        <xsl:text>&amp;minoccurrence=</xsl:text>
        <xsl:value-of select="$minoccurrence"/>
      </xsl:if>
      <xsl:if test="$maxoccurrence">
        <xsl:text>&amp;maxoccurrence=</xsl:text>
        <xsl:value-of select="$maxoccurrence"/>
      </xsl:if>
    </xsl:variable>

  <xsl:variable name="isPopup" select="$subop = 'relatedcontent' or
                           $subop = 'relatedfiles' or
                           $subop = 'relatedfile' or
                           $subop = 'relatedimages' or
                           $subop = 'relatedimage' or
                           $subop = 'contentfield' or
                           $subop = 'insert' or
                           $subop = 'addcontenttosection'"/>

  <xsl:variable name="icon-column-width" select="40"/>

  <xsl:template match="/">
      <html>
        <head>
          <xsl:call-template name="waitsplash"/>
          <link href="css/admin.css" rel="stylesheet" type="text/css"/>
          <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>
          <link rel="stylesheet" type="text/css" href="css/calendar_picker.css"/>
          <link href="javascript/cms/ui/style.css" rel="stylesheet" type="text/css"/>

          <link type="text/css" rel="stylesheet" href="javascript/lib/jquery/ui/autocomplete/css/cms/jquery-ui-1.8.1.custom.css"/>
          <link type="text/css" rel="stylesheet" href="javascript/lib/jquery/ui/autocomplete/css/cms/jquery-ui-1.8.1.overrides.css"/>
          <link type="text/css" rel="stylesheet" href="css/user-picker-with-autocomplete.css"/>

          <script type="text/javascript" src="javascript/admin.js">//</script>
          <script type="text/javascript" src="javascript/content_form.js">//</script>
          <script type="text/javascript" src="javascript/content_advancedsearch.js">//</script>

          <xsl:if test="$isPopup">
            <script type="text/javascript" src="javascript/window.js">//</script>
            <script type="text/javascript">
              cms.window.attatchKeyEvent('close');
            </script>
          </xsl:if>

          <script type="text/javascript" src="javascript/accessrights.js">//</script>
          <script type="text/javascript" src="javascript/tabpane.js">//</script>

          <script type="text/javascript" src="javascript/cms/core.js">//</script>
          <script type="text/javascript" src="javascript/cms/utils/Event.js">//</script>
          <script type="text/javascript" src="javascript/cms/element/Css.js">//</script>
          <script type="text/javascript" src="javascript/cms/element/Dimensions.js">//</script>
          <script type="text/javascript" src="javascript/cms/ui/MenuButton.js">//</script>

          <xsl:if test="$searchtype = 'advanced'">
            <script type="text/javascript" src="javascript/calendar_picker.js">//</script>
            <script type="text/javascript" src="javascript/validate.js">//</script>

            <script type="text/javascript" src="javascript/lib/jquery/jquery-1.4.2.min.js">//</script>
            <script type="text/javascript" src="javascript/lib/jquery/ui/autocomplete/js/jquery-ui-1.8.1.custom.min.js">//</script>
            <script type="text/javascript" src="javascript/user-picker-with-autocomplete.js">//</script>
          </xsl:if>
          <script type="text/javascript" language="JavaScript">

            /*
              Function: getCheckedLength

                Returns how many checkboxes that are checked.
            */
            function getCheckedLength(checkboxes) {
              if (checkboxes == null)
                return;
              var count = 0;
              var size = checkboxes.length;
              for (var i = 0; i &lt; size; i++) {
                if (checkboxes[i].checked)
                  count++;
              }
              return count;
            }
            // ---------------------------------------------------------------------------------------------------
            /*
              Method: menuItem_addContentToContentField
            */
            function menuItem_addContentToContentField(subop, fieldName, fieldRow)
            {
              var checkboxes = document.getElementsByName('batch_operation');
              var checkboxesLn = checkboxes.length;

              var key, title;

              for (var i = 0; i &lt; checkboxesLn; i++) {
                if (checkboxes[i].checked) {
                  key = checkboxes[i].value;
                  title = document.getElementById('title_' + key).value;
                  break;
                }
              }

              if ( key )
              {
                window.top.opener.callback_contentfield('_selected_content', -1 , key, title );
                window.top.opener.contentKeys[0] = key;
                window.top.close();
              }
            }
            // ---------------------------------------------------------------------------------------------------

            function navigateToEditorInsertImageForm()
            {
              var selectedContentKey = contentlist_getCheckedCheckbox();
              var versionKey, hiddenButtonElement, isIE = document.all;

              versionKey = document.getElementById('content_versionkey_' + selectedContentKey).value;
              hiddenButtonElement = document.getElementById('operation_insert_' + selectedContentKey + versionKey);

              // Use the click method since IE does not report the referrer when using location href
              if( isIE )
              {
                hiddenButtonElement.click();
              }
              else
              {
                document.location.href = hiddenButtonElement.href;
              }
            }
            // ---------------------------------------------------------------------------------------------------

            function insertContentToEditorLinkForm()
            {
              var selectedContentKey = contentlist_getCheckedCheckbox();
              var editorLinkForm = window.parent.opener.document.forms['formAdmin'];
              var contentTitle = document.getElementById('title_' + selectedContentKey).value;

              window.parent.opener.setPickerFieldValues('input-content', 'content://' + selectedContentKey, contentTitle);
              window.parent.close();
            
            }
            // ---------------------------------------------------------------------------------------------------

            function insertFileToEditorLinkForm()
            {
              var selectedContentKey = contentlist_getCheckedCheckbox();
              var fileExtension = '';
              var editorLinkForm = window.parent.opener.document.forms['formAdmin'];
              var contentTitle = document.getElementById('title_' + selectedContentKey).value;

              if ( selectedContentKey !== null )
              {
                versionKey = document.getElementById('content_versionkey_' + selectedContentKey).value;
                contentTitle = document.getElementById('title_' + selectedContentKey).value;

                fileExtension = getFileExtension(contentTitle);

                if ( fileExtension &amp;&amp; fileExtension === 'png' || fileExtension === 'jpg' || fileExtension === 'jpeg' || fileExtension === 'gif' )
                {
                    alert('%alertImagesIsNotAllowedAsAttachment%');
                }
                else
                {
                  window.parent.opener.setPickerFieldValues('input-file', 'attachment://' + selectedContentKey, contentTitle);
                  window.parent.close();
                }
              }
            }
            // ---------------------------------------------------------------------------------------------------

            function contentlist_getCheckedCheckbox()
            {
              var checkboxes = document.getElementsByName('batch_operation');
              var checkboxesLn = checkboxes.length;
              var checkbox, contentKey;

              for ( var i = 0; i &lt; checkboxesLn; i++ )
              {
                var checkbox = checkboxes[i];
                var contentKey = checkbox.value;
                if ( checkbox.checked )
                {
                  return contentKey;
                }
              }

              return null;
            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: insertRelatedFile
            */
            function insertRelatedFile(fieldName, fieldRow, key, title, fileName, opener, counter) {
              if (!fileName[counter] &amp;&amp; fileName.length &gt; 0) {
                opener.addTableRow('filetable' + fieldName, 0, 1);
                // Use try catch since some of the built in content handlers does not have this method.
                try
                {
                  eval('window.top.opener.clearNewRow' + fieldName + '()');
                }
                catch(err)
                {
                  /**/
                }
              }
              opener.addRelatedFiles(fieldName, fieldRow, key, title);
            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: insertRelatedImages
            */
            function insertRelatedImages(fieldName, fieldRow, key, title, text, opener, counter) {
              if (!text[counter]) {
                opener.addTableRow(fieldName + 'table', 0, 1);
              }

              opener.addRelatedImages(fieldName, fieldRow, key, null)
              opener.relatedImagesDisplay(fieldName + 'table');
            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: insertRelatedImage
            */
            function insertRelatedImage(fieldName, fieldRow, key, bKey) {
              window.top.opener.addRelatedImage(fieldName, fieldRow, key, bKey);
              window.top.close();
            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: batchInsert
            */
            function batchInsert(subop, fieldName, fieldRow) {
              // var closeWindow = confirm('%confirmCloseWindow%');
              var checkboxes = document.getElementsByName('batch_operation');
              var checkboxesLn = checkboxes.length;
              var inUseContent = false;
              var opener = window.top.opener;
              var checkedLn = getCheckedLength(checkboxes);
              var counter = fieldRow;
              //var addedTitle = new Array();

              if (subop == 'relatedfiles') {
                var fileName = opener.document.getElementsByName('filename' + fieldName) || null;
                var fileNameLn = fileName.length;
              }

              if (subop == 'relatedimages') {
                var text = opener.document.getElementsByName(fieldName + 'text') || null;
                var textLn = text.length;
              }

              if (subop == 'contentfield') {
                 menuItem_addContentToContentField(subop, fieldName, fieldRow);
                 return;
              }

              if (subop == 'relatedimage') {
                var count = 0;
                for (var i = 0; i &lt; checkboxesLn; i++) {
                  if (checkboxes[i].checked)
                    count++;
                }
                if (count &gt; 1) {
                  alert('%alertOneContentOnly%');
                  return;
                } else {
                  var key;
                  for (var i = 0; i &lt; checkboxesLn; i++) {
                    if (checkboxes[i].checked) {
                      key = checkboxes[i].value;
                      break;
                    }
                  }

                  insertRelatedImage(fieldName, fieldRow, key, null);

                }
                return;
              }

              if (subop == 'relatedfile') {
                var fileName = opener.document.getElementsByName('filename' + fieldName) || null;

                var count = 0;
                for (var i = 0; i &lt; checkboxesLn; i++) {
                  if (checkboxes[i].checked)
                    count++;
                }

                if (count &gt; 1) {
                  alert('%alertOneContentOnly%');
                  return;
                } else {

                  var title, key;

                  for (var i = 0; i &lt; checkboxesLn; i++) {
                    if (checkboxes[i].checked) {
                      key = checkboxes[i].value;
                      title = document.getElementById('title_' + key).value;
                      break;
                    }
                  }

                  if (fileName &amp;&amp; key) {
                    // window.top.opener.addRelatedFiles(fieldName, fieldRow, key, title);
                    insertRelatedFile(fieldName, fieldRow, key, title, fileName, opener, 0);
                    window.top.close();
                    return;
                  }
                }
              }


              for (var i = 0; i &lt; checkboxesLn; i++) {
                if (checkboxes[i].checked) {
                  var key = checkboxes[i].value;
                  var title = document.getElementById('title_' + key).value;

                  // document.getElementById('title_' + key).className += ' is-added';
                  paintIsAdded(key);

                  if(subop == 'relatedfiles') {
                    insertRelatedFile(fieldName, fieldRow, key, title, fileName, opener, counter);
                    fieldRow++;
                    counter++;
                  } else if (subop == 'relatedimages') {
                    insertRelatedImages(fieldName, fieldRow, key, title, text, opener, counter);
                    fieldRow++;
                    counter++;
                  } else { // Related content
                    <xsl:variable name="_minoccurrence">
                      <xsl:choose>
                        <xsl:when test="not($minoccurrence)">-1</xsl:when>
                        <xsl:otherwise>
                          <xsl:value-of select="$minoccurrence"/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:variable>

                    <xsl:variable name="_maxoccurrence">
                      <xsl:choose>
                        <xsl:when test="not($maxoccurrence)">-1</xsl:when>
                        <xsl:otherwise>
                          <xsl:value-of select="$maxoccurrence"/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:variable>
            
                    opener.addRelatedContent(fieldName, fieldRow, key, title, <xsl:value-of select="$_minoccurrence"/>, <xsl:value-of select="$_maxoccurrence"/>);
                  }
                }
              }
              window.top.close();
            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: batchRemove
            */
              function batchRemove() {
                var form = document.forms['formAdmin'];
                var checkboxes = document.getElementsByName('batch_operation');

                form.op.value = 'batch_remove';

                var toBeRemoved = [];

                for (var i=0; i &lt; checkboxes.length; i++) {
                  if (checkboxes[i].checked) {
                    toBeRemoved.push(checkboxes[i].value);
                  }
                }

                AjaxService.isContentInUse(toBeRemoved, {
                  callback:function(bInUse) {
                    verifyBatchRemoval(bInUse, form);
                  }
                });
            }

            // ---------------------------------------------------------------------------------------------------

            /*
              Method: verifyBatchRemoval
            */
              function verifyBatchRemoval(bInUse, form) {

              var alertMsg;

              if (bInUse) {
                alertMsg = '%msgConfirmRemoveSelectedInUse%';
              } else {
                alertMsg = '%msgConfirmRemoveSelected%';
              }

              if ( confirm(alertMsg) )
              {
                form.submit();
              }

            }

            // ---------------------------------------------------------------------------------------------------

            /*
              Method: removeContent
            */

            function removeContent(contentKey, page, cat) {
              var toBeRemoved = [];
              toBeRemoved.push(contentKey);

              AjaxService.isContentInUse(toBeRemoved, {
                  callback:function(bInUse) {
                    doRemoveContent(bInUse, contentKey, page, cat);
                  }
              });

              return false;

            }

            // ---------------------------------------------------------------------------------------------------

            /*
              Method: doRemoveContent
            */

            function doRemoveContent(bInUse, contentKey, page, cat) {
              var alertMsg;

                if(bInUse) {
                   alertMsg = '%alertDeleteContentWithParents%';
                } else {
                   alertMsg = '%msgConfirmRemoveSelected%';
                }

                if (confirm(alertMsg)) {

                  var refferer = document.forms.formAdmin.referer.value;
                  var deleteOperationUrl = 'adminpage?page=' + page + '&amp;op=remove&amp;key=' + contentKey + '&amp;cat=' + cat;

                  if ( refferer &amp;&amp; refferer !== '' )
                  {
                    deleteOperationUrl += '&amp;referer=' + encodeURIComponent( refferer );
                  }

                  document.location = deleteOperationUrl;
                }
            }

            // ---------------------------------------------------------------------------------------------------

            /*
              Method: batchApprove
            */
            function batchApprove() {
              if( confirm('%msgConfirmApproveSelected%') )
              {
                document.forms['formAdmin'].op.value = 'batch_approve';
                document.forms['formAdmin'].submit();
              }
            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: batchArchive
            */
            function batchArchive() {

                var form = document.forms['formAdmin'];
                var checkboxes = document.getElementsByName('batch_operation');

                document.forms['formAdmin'].op.value = 'batch_archive';

                var toBeArchived = [];

                for (var i=0; i &lt; checkboxes.length; i++) {
                  if (checkboxes[i].checked) {
                    toBeArchived.push(checkboxes[i].value);
                  }
                }

                AjaxService.isContentInUse(toBeArchived, {
                  callback:function(bInUse) {
                    verifyBatchArchive(bInUse, form);
                  }
                });
            }


            // ---------------------------------------------------------------------------------------------------

            /*
              Method: verifyBatchArchive
            */
              function verifyBatchArchive(bInUse, form) {

              var alertMsg;

              if (bInUse) {
                alertMsg = '%msgConfirmArchiveSelectedInUse%';
              } else {
                alertMsg = '%msgConfirmArchiveSelected%';
              }

              if ( confirm(alertMsg) )
              {
                form.submit();
              }

            }

            // ---------------------------------------------------------------------------------------------------

            /*
              Method: batchMove
            */
            function batchMove() {
              document.forms['formAdmin'].op.value = 'batch_move';
              _OpenNewCategorySelector();

            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: batchCopy
            */
            function batchCopy() {
              document.forms['formAdmin'].op.value = 'batch_copy';
              _OpenNewCategorySelector();
            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: _getY
            */
            function _getY(el) {
              var curtop = 0;
              if (el.offsetParent) {
                curtop = el.offsetTop;
                while (el = el.offsetParent) {
                  curtop += el.offsetTop;
                }
              }
              return curtop;
            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: setBatchButtonRowsEnabled
            */
            function setBatchButtonsEnabled() {
              setBatchButtonRowEnabled(0);
              setBatchButtonRowEnabled(1);

              <xsl:variable name="only-one-content-is-allowed" select="$maxoccurrence = 1"/>

              <xsl:if test="$subop = 'relatedimage' or $subop = 'relatedfile' or $subop = 'contentfield' or $subop = 'insert' or $only-one-content-is-allowed">
                var countChecked = getCheckedLength(document.getElementsByName('batch_operation'));
                var batchInsertBtn = document.getElementsByName('batchinsertbtn');
                var batchInsertBtnLn = batchInsertBtn.length;

                for ( var i = 0; i &lt; batchInsertBtnLn; i++ )
                {
                  var shim = document.getElementById('batchinsertbtn_shim_' + (i+1));
                  if ( countChecked &gt; 1 || countChecked == 0 )
                  {
                    if ( shim )
                    {
                      shim.style.top = findPosY(batchInsertBtn[i]) + 'px';
                      shim.style.left = findPosX(batchInsertBtn[i]) + 'px';
                      shim.style.width = batchInsertBtn[i].offsetWidth;
                      shim.style.height = batchInsertBtn[i].offsetHeight;
                    }

                    batchInsertBtn[i].disabled = true;
                    batchInsertBtn[i].style.cursor = 'default';
                  }
                  else
                  {
                    if ( shim )
                    {
                      shim.style.top = '-1000px';
                      shim.style.left = '-1000px';
                    }

                    batchInsertBtn[i].disabled = false;
                    batchInsertBtn[i].style.cursor = 'pointer';
                  }
                }
              </xsl:if>

            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: setBatchSelectorEnabled
            */
            function setBatchSelectorEnabled( enable )
            {
              var batchSelectors = document.getElementsByName('batchSelector');
              var batchSelectorsLn = batchSelectors.length;
              for (var i = 0; i &lt; batchSelectorsLn; i++)
              {
                var batchSelector = batchSelectors[i];
                batchSelector.disabled = !enable;
              }

            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: setBatchButtonsEnabled
            */
            function setBatchButtonRowEnabled(r) {
              var batchRemoveBtn = document.getElementsByName('batchremovebtn')[r];
              var batchMoveBtn = document.getElementsByName('batchmovebtn')[r];
              var batchCopyBtn = document.getElementsByName('batchcopybtn')[r];
              var batchInsertBtn = document.getElementsByName('batchinsertbtn')[r];

              setBatchSelectorEnabled( anyChecked('batch_operation') )

              if (anyChecked('batch_operation')) {

                if (batchInsertBtn) {
                  batchInsertBtn.disabled = false;
                  batchInsertBtn.style.cursor = 'pointer';
                }

                if (batchRemoveBtn) {
                  batchRemoveBtn.disabled = false;
                  batchRemoveBtn.style.cursor = 'pointer';
                }

                if (batchMoveBtn) {
                  batchMoveBtn.disabled = false;
                  batchMoveBtn.style.cursor = 'pointer';
                }

                if (batchCopyBtn) {
                  var contentTypeKeys = document.getElementsByName('contenttypekey');
                  var checkedIndexes = getCheckedIndexes("batch_operation");
                  var differentCtys = false;
                  var contentTypeKey = contentTypeKeys[checkedIndexes[0]].value;

                  for (var i=1; i &lt; checkedIndexes.length; i++) {
                    if (contentTypeKeys[checkedIndexes[i]].value != contentTypeKey) {
                      differentCtys = true;
                    }
                  }

                  if (!differentCtys) {
                    batchCopyBtn.disabled = false;
                    batchCopyBtn.style.cursor = 'pointer';
                  } else {
                    batchCopyBtn.disabled = true;
                    batchCopyBtn.style.cursor = 'default';
                  }

                }
              } else {

                if (batchInsertBtn) {
                  batchInsertBtn.disabled = true;
                  batchInsertBtn.style.cursor = 'default';
                }

                if (batchRemoveBtn) {
                  batchRemoveBtn.disabled = true;
                  batchRemoveBtn.style.cursor = 'default';
                }

                if (batchMoveBtn) {
                  batchMoveBtn.disabled = true;
                  batchMoveBtn.style.cursor = 'default';
                }

                if (batchCopyBtn) {
                  batchCopyBtn.disabled = true;
                  batchCopyBtn.style.cursor = 'default';
                }

                // Make sure that the master button is off. (<xsl:value-of select="($subop != 'relatedimage') or ($subop != 'relatedfile')"/>, <xsl:value-of select="$subop"/>)
                <xsl:if test="$subop != 'relatedimage' and $subop !='relatedfile'">
                  var obj = document.getElementById('batch_operation_field');
                  var img = document.getElementById('batch_operation_image');

                  if ( obj )
                  {
                    obj.value = false;
                  }

                  if ( img )
                  {
                    img.src = "images/checkbox_unchecked.gif";
                  }
                </xsl:if>

              }

            }
            // ---------------------------------------------------------------------------------------------------
            /*
              Method: updateContentKeys

                contentKeys will keep the content keys selected in checkboxes when doing move to category,
                or the content key when move category is clicked in a row
            */
            var contentKeys = null;
            function updateContentKeys() {
              contentKeys = getCheckedValues('batch_operation');
            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: moveContent

                Helper for the content operations
            */
            function moveContent(contentKey, contentType) {
              document.forms['formAdmin'].op.value = 'batch_move';
              setValuesUnchecked('batch_operation');
              setValueChecked('batch_operation', contentKey);
              _OpenNewCategorySelector();
            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: callback_newCategorySelector
            */
            function callback_newCategorySelector(categoryKey) {
              var op = document.forms['formAdmin'].op.value;
              if (op == 'move_category') {
                window.location = "adminpage?op=move&amp;cat="+ <xsl:value-of select="$cat"/> +"&amp;newparent="+ categoryKey +"&amp;page=200&amp;oldpage="+ <xsl:value-of select="$page"/>;
                return;
              }
              updateContentKeys();
              if (contentKeys.length > 0) {
                document.forms['formAdmin'].newcategory.value = categoryKey;
                waitsplash();
                document.forms['formAdmin'].submit();
              }
            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: paintIsAdded
            */
            function paintIsAdded(key) {
              try {
                var trElements = document.getElementById('content_row_' + key);
                var tdElements = trElements.getElementsByTagName('td');
                var tdLn = tdElements.length;
                var tdElement;
                for (var i = 0; i &lt; tdLn; i++) {
                  tdElement = tdElements[i];
                  tdElement.className += ' is-added';
                  tdElement.title = '%msgContentAlreadyAdded%';
                }
              } catch (e) { /**/ }
            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: setChecked
            */
            function setChecked(id) {
              var checkbox = document.getElementById(id);

              if (!checkbox) return;

              if (checkbox.checked)
              {
                checkbox.checked = true;
              }
              else
              {
                checkbox.checked = false;
              }

              checkbox.click(); // Fx checks the button visually but the property is not set.
            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: toggleCheckbox
            */
            function toggleCheckbox(key) {
              setChecked('batch_operation' + key);
            }

            // ---------------------------------------------------------------------------------------------------

            /*
              Method: changeBatchSelection
            */
            function changeBatchSelection( selectElement )
            {
              var action = selectElement.value;
              var form = document.forms['formAdmin'];

              if ( action == 'delete' )
              {
                batchRemove();
              }
              else if ( action == 'move' )
              {
                batchMove();
              }
              else if ( action == 'copy' )
              {
                batchCopy();
              }
              else if ( action == 'approve' )
              {
                batchApprove();
              }
              else if ( action == 'archive' )
              {
                batchArchive();
              }
              else
              {
                //
              }

              selectElement.selectedIndex = 0;
            }
            // ---------------------------------------------------------------------------------------------------

            /*
              Method: setCount
            */
            function setCount( selectElement )
            {
              var selectedValue = selectElement.value;

              // Get the urlForNavigation string from a hidden input. The reason for this is to avoid quoted string in the url string.
              var urlForNavigation = document.getElementById('navigation-url-for-for-count-dropdown').value;

              var url = urlForNavigation + '<xsl:text>&amp;index=</xsl:text><xsl:value-of select="$index"/><xsl:text>&amp;count=</xsl:text>' + selectedValue;
              document.location = url;
            }
            // ---------------------------------------------------------------------------------------------------

            function contentAdded( contentKey )
            {
              var contentKeys = window.top.opener.contentKeys;
              var found = false;
              for(i in contentKeys)
              {
                if(contentKeys[i] == contentKey)
                {
                  found = true;
                  break;
                }
              }
              return found;
            }
            // ---------------------------------------------------------------------------------------------------

            function addContentToSectionPage( url )
            {
              var checkboxes = document.getElementsByName('batch_operation');
              var checkboxesLn = checkboxes.length;

              var checkboxesChecked = 0;
              for ( var i = 0; i &lt; checkboxesLn; i++ )
              {
                var checkbox = checkboxes[i];
                var contentKey = checkbox.value;
                if ( checkbox.checked &amp;&amp; !contentAdded( contentKey ) )
                {
                  url += '&amp;key=' + contentKey;
                  checkboxesChecked++;
                }
              }

              if ( checkboxesChecked &gt; 0 )
              {
                 window.top.opener.location = url;
                 window.top.close();
              }
              else
              {
                alert('%alertContentAlreadyInSection%');
              }
            }
            // ---------------------------------------------------------------------------------------------------

            function getFileExtension( filename )
            {
                return (/[.]/.exec(filename)) ? /[^.]+$/.exec(filename) : undefined;
            }
          
            <xsl:variable name="superCategoryKey">
              <xsl:choose>
                <xsl:when test="/data/category/@supercategorykey">
                  <xsl:value-of select="/data/category/@supercategorykey"/>;
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>-1</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <xsl:variable name="contentTypeKey">
              <xsl:choose>
                <xsl:when test="/data/category/@contenttypekey">
                  <xsl:value-of select="/data/category/@contenttypekey"/>;
                </xsl:when>
                <xsl:otherwise>
                  <xsl:text>-1</xsl:text>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            function _OpenNewCategorySelector() {
              var unitKey = <xsl:value-of select="$selectedunitkey"/>;
              var categoryKey = <xsl:value-of select="$cat"/>;
              var superCategoryKey = <xsl:value-of select="$superCategoryKey"/>;
              var contentTypeKey = <xsl:value-of select="$contentTypeKey"/>;
              var op = document.forms['formAdmin'].op.value;

              if (op == 'move_category') {
                OpenNewCategorySelector(unitKey, '', contentTypeKey, true, superCategoryKey, categoryKey);
              } else {
                var excludeCategoryKey = -1;
                if (op == 'batch_move')
                  excludeCategoryKey = categoryKey;

                var checkedIndexes = getCheckedIndexes("batch_operation");

                if (!checkedIndexes.length > 0)
                  return;

                var contentTypeKeys = document.getElementsByName('contenttypekey');
                var contentTypeString = contentTypeKeys[checkedIndexes[0]].value;
                OpenNewCategorySelector(unitKey, contentTypeString, null, false, excludeCategoryKey, -1);
              }
            }

            <xsl:if test="$subop = 'insert'">
              document.onkeypress = function(e) {
                var e = e || event;
                if (e.keyCode == 27 &amp;&amp; window.parent)
                  window.parent.close();
              }
            </xsl:if>
          </script>
        </head>

        <body class="jquery-ui">

          <xsl:if test="$reload = 'true'">
            <script type="text/javascript">window.top.frames['leftFrame'].refreshMenu();</script>
          </xsl:if>

          <input type="hidden" name="navigation-url-for-for-count-dropdown" id="navigation-url-for-for-count-dropdown" value="{$urlForNavigation}"/>
          
          <xsl:variable name="url">
            <xsl:text>adminpage?op=browse&amp;subop=</xsl:text>
            <xsl:value-of select="$subop"/>
            <xsl:if test="$fieldname">
              <xsl:text>&amp;fieldname=</xsl:text>
              <xsl:value-of select="$fieldname"/>
            </xsl:if>
            <xsl:if test="$fieldrow">
              <xsl:text>&amp;fieldrow=</xsl:text>
              <xsl:value-of select="$fieldrow"/>
            </xsl:if>
            <xsl:if test="$contenttypestring">
              <xsl:text>&amp;contenttypestring=</xsl:text>
              <xsl:value-of select="$contenttypestring"/>
            </xsl:if>
            <xsl:if test="$contenthandler">
              <xsl:text>&amp;contenthandler=</xsl:text>
              <xsl:value-of select="$contenthandler"/>
            </xsl:if>
            <xsl:if test="$minoccurrence">
              <xsl:text>&amp;minoccurrence=</xsl:text>
              <xsl:value-of select="$minoccurrence"/>
            </xsl:if>
            <xsl:if test="$maxoccurrence">
              <xsl:text>&amp;maxoccurrence=</xsl:text>
              <xsl:value-of select="$maxoccurrence"/>
            </xsl:if>
          </xsl:variable>

          <h1>
            <xsl:apply-templates select="/data/path/node()">
              <xsl:with-param name="url" select="$url"/>
              <xsl:with-param name="usedisable" select="false()"/>
            </xsl:apply-templates>
          </h1>

          <xsl:if test="not($hasAdminBrowse) and not($searchtype)">
            <h2>
              %msgCategoryYouHaveNoAccessToBrowse%
            </h2>
          </xsl:if>

          <!-- main button controls -->
          <xsl:choose>
            <xsl:when test="$searchtype = 'simple'">
              <xsl:call-template name="contentsearchresultsmenu">
                <xsl:with-param name="op" select="$op"/>
                <xsl:with-param name="subop" select="$subop"/>

                <xsl:with-param name="fieldname" select="$fieldname"/>
                <xsl:with-param name="fieldrow" select="$fieldrow"/>

                <xsl:with-param name="page" select="$page"/>
                <xsl:with-param name="cat" select="$cat"/>
                <xsl:with-param name="selectedunitkey" select="$selectedunitkey"/>
                <xsl:with-param name="searchtype" select="$searchtype"/>
                <xsl:with-param name="searchtext" select="$searchtext"/>
                <xsl:with-param name="scope" select="$scope"/>
                <xsl:with-param name="contenttypestring" select="$contenttypestring"/>
                <xsl:with-param name="contenthandler" select="$contenthandler"/>
                <xsl:with-param name="minoccurrence" select="$minoccurrence"/>
                <xsl:with-param name="maxoccurrence" select="$maxoccurrence"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:when test="$searchtype = 'advanced'">
              <!-- No menu when in advanced mode -->
            </xsl:when>
            <xsl:otherwise>

              <xsl:variable name="ctykey" select="/data/category/@contenttypekey"/>

              <xsl:call-template name="contentbrowsemenu">
                <xsl:with-param name="op" select="$op"/>
                <xsl:with-param name="subop" select="$subop"/>
                <xsl:with-param name="page" select="$page"/>
                <xsl:with-param name="cat" select="$cat"/>
                <xsl:with-param name="fieldname" select="$fieldname"/>
                <xsl:with-param name="fieldrow" select="$fieldrow"/>
                <xsl:with-param name="contenttypestring" select="$contenttypestring"/>
                <xsl:with-param name="selectedunitkey" select="$selectedunitkey"/>
                <xsl:with-param name="newbutton" select="/data/category/@contenttypekey != 50"/>
                <xsl:with-param name="searchonly" select="$searchonly = 'true'"/>
                <xsl:with-param name="contenttypeelem" select="/data/contenttypes/contenttype[@key = $ctykey]"/>
                <xsl:with-param name="contenthandler" select="$contenthandler"/>
                <xsl:with-param name="user-has-categorycreate" select="$hasCategoryCreate"/>
                <xsl:with-param name="user-has-categoryadministrate" select="$hasCategoryAdministrate"/>
                <xsl:with-param name="minoccurrence" select="$minoccurrence"/>
                <xsl:with-param name="maxoccurrence" select="$maxoccurrence"/>
              </xsl:call-template>
            </xsl:otherwise>
          </xsl:choose>

          <!-- Advanced search form -->
          <xsl:if test="$searchtype = 'advanced'">
            <xsl:call-template name="advanced_search_form">
              <xsl:with-param name="subop" select="$subop"/>
              <xsl:with-param name="page" select="$page"/>
              <xsl:with-param name="fieldname" select="$fieldname"/>
              <xsl:with-param name="fieldrow" select="$fieldrow"/>
              <xsl:with-param name="cat" select="$cat"/>
              <xsl:with-param name="asearchtext" select="$asearchtext"/>
              <xsl:with-param name="ascope" select="$ascope"/>
              <xsl:with-param name="includeSubcategories" select="$subcategories"/>
              <xsl:with-param name="contenttypestring" select="$contenttypestring"/>
              <xsl:with-param name="cancelUrl" select="$pageURLWithoutSearch"/>
              <xsl:with-param name="state" select="$state"/>
              <xsl:with-param name="owner" select="$owner"/>
              <xsl:with-param name="owner.uid" select="$owner.uid"/>
              <xsl:with-param name="owner.fullName" select="$owner.fullName"/>
              <xsl:with-param name="owner.qualifiedName" select="$owner.qualifiedName"/>
              <xsl:with-param name="modifier" select="$modifier"/>
              <xsl:with-param name="modifier.uid" select="$modifier.uid"/>
              <xsl:with-param name="modifier.fullName" select="$modifier.fullName"/>
              <xsl:with-param name="modifier.qualifiedName" select="$modifier.qualifiedName"/>
              <xsl:with-param name="assignment.assigneeUserKey" select="$assignment.assigneeUserKey"/>
              <xsl:with-param name="assignment.assigneeDisplayName" select="$assignment.assigneeDisplayName"/>
              <xsl:with-param name="assignment.assigneeQualifiedName" select="$assignment.assigneeQualifiedName"/>
              <xsl:with-param name="assignment.assignerUserKey" select="$assignment.assignerUserKey"/>
              <xsl:with-param name="assignment.assignerDisplayName" select="$assignment.assignerDisplayName"/>
              <xsl:with-param name="assignment.assignerQualifiedName" select="$assignment.assignerQualifiedName"/>
              <xsl:with-param name="duedate" select="$duedate"/>
              <xsl:with-param name="assignment.dueDate" select="$assignment.dueDate"/>
              <xsl:with-param name="assignment.dueDate.op" select="$assignment.dueDate.op"/>
              <xsl:with-param name="created" select="$created"/>
              <xsl:with-param name="created.op" select="$created.op"/>
              <xsl:with-param name="modified" select="$modified"/>
              <xsl:with-param name="modified.op" select="$modified.op"/>
              <xsl:with-param name="acontentkey" select="$acontentkey"/>
              <xsl:with-param name="filter" select="$filter"/>
              <xsl:with-param name="contenthandler" select="$contenthandler"/>
              <xsl:with-param name="selectedtabpage" select="$selectedtabpage"/>
              <xsl:with-param name="minoccurrence" select="$minoccurrence"/>
              <xsl:with-param name="maxoccurrence" select="$maxoccurrence"/>
            </xsl:call-template>
          </xsl:if>

          <!-- Feedback message -->
          <xsl:if test="/node()/feedback/@code">
            <p>
              <xsl:call-template name="displayfeedback"/>
            </p>
          </xsl:if>

          <xsl:choose>
            <xsl:when test="$searchonly = 'true' or (not($searchtype) and not(/data/category/@contenttypekey))">
              <form name="formAdmin" method="post" action="adminpage">
                <input type="hidden" name="page" value="{$page}"/>
                <input type="hidden" name="op" value=""/>
                <!-- newcategory is used by "move to category" -->
                <input type="hidden" name="newcategory"/>
                <input type="hidden" name="referer" value="{$pageURLWithSorting}"/>
              </form>
            </xsl:when>
            <xsl:otherwise>
              <form name="formAdmin" method="post" action="adminpage">
                <input type="hidden" name="page" value="{$page}"/>
                <input type="hidden" name="op" value="batch_remove"/>
                <!-- newcategory is used by "move to category" -->
                <!-- Good to know!  -->
                <input type="hidden" name="newcategory"/>
                <input type="hidden" name="referer" value="{$pageURLWithSorting}"/>

                <fieldset class="table-panel">

                  <xsl:call-template name="operationsBar">
                    <xsl:with-param name="pos" select="1"/>
                  </xsl:call-template>

                  <!-- Content table -->
                  <table width="100%" cellspacing="0" cellpadding="0" class="browsetable">
                    <xsl:variable name="fieldsxpath">
                      <xsl:choose>
                        <xsl:when test="$searchtype">
                          <xsl:text>/data/browse/column</xsl:text>
                        </xsl:when>
                        <xsl:when test="/data/contenttypes/contenttype/moduledata/browse">
                          <xsl:text>/data/contenttypes/contenttype/moduledata/browse/column</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:text>/data/browse/column</xsl:text>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:variable>

                    <tr>
                      <!-- Display checkbox header -->
                      <xsl:choose>
                        <xsl:when test="$subop = 'contentfield' or $subop = 'relatedfile' or $subop = 'relatedimage'">
                          <xsl:call-template name="tablecolumnheader">
                            <xsl:with-param name="align" select="'center'"/>
                            <xsl:with-param name="width" select="'20'"/>
                            <xsl:with-param name="sortable" select="'false'"/>
                          </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:call-template name="tablecolumnheader">
                            <xsl:with-param name="align" select="'center'"/>
                            <xsl:with-param name="width" select="'20'"/>
                            <xsl:with-param name="sortable" select="'false'"/>
                            <xsl:with-param name="checkboxname" select="'batch_operation'"/>
                            <xsl:with-param name="checkBoxOnClickFallback" select="'setBatchButtonsEnabled'"/>
                          </xsl:call-template>
                        </xsl:otherwise>
                      </xsl:choose>

                      <xsl:call-template name="tablecolumnheader">
                        <xsl:with-param name="width" select="$icon-column-width"/>
                        <xsl:with-param name="caption" select="'%fldType%'"/>
                        <xsl:with-param name="sortable" select="'false'"/>
                        <xsl:with-param name="align" select="'center'"/>
                      </xsl:call-template>

                      <xsl:for-each select="saxon:evaluate($fieldsxpath)">
                        <!-- Column title -->
                        <xsl:variable name="title">
                          <xsl:choose>
                            <xsl:when test="@title">
                              <xsl:value-of select="@title"/>
                            </xsl:when>
                            <xsl:otherwise>
                              <xsl:apply-templates select="*[1]" mode="title"/>
                            </xsl:otherwise>
                          </xsl:choose>
                        </xsl:variable>

                        <!-- Column width -->
                        <xsl:variable name="width">
                          <xsl:choose>
                            <xsl:when test="@width">
                              <xsl:value-of select="@width"/>
                            </xsl:when>
                            <xsl:otherwise>
                              <xsl:apply-templates select="*[1]" mode="width"/>
                            </xsl:otherwise>
                          </xsl:choose>
                        </xsl:variable>

                        <!-- Column align -->
                        <xsl:variable name="titlealign">
                          <xsl:choose>
                            <xsl:when test="@titlealign">
                              <xsl:value-of select="@titlealign"/>
                            </xsl:when>
                            <xsl:otherwise>
                              <xsl:apply-templates select="*[1]" mode="titlealign"/>
                            </xsl:otherwise>
                          </xsl:choose>
                        </xsl:variable>

                        <!-- Column ordering -->
                        <xsl:variable name="orderby">
                          <xsl:choose>
                            <xsl:when test="@orderby">
                              <xsl:value-of select="@orderby"/>
                            </xsl:when>
                            <xsl:otherwise>
                              <xsl:apply-templates select="*[1]" mode="orderby">
                                <xsl:with-param name="indexingxpath" select="'/data/indexparameters/'"/>
                                <xsl:with-param name="indexingelem" select="/data/indexparameters"/>
                              </xsl:apply-templates>
                            </xsl:otherwise>
                          </xsl:choose>
                        </xsl:variable>

                        <!-- Column ordering -->
                        <xsl:variable name="sortable">
                          <xsl:choose>
                            <xsl:when test="$orderby = ''">false</xsl:when>
                            <xsl:otherwise>true</xsl:otherwise>
                          </xsl:choose>
                        </xsl:variable>

                        <!-- Display column header -->
                        <xsl:call-template name="tablecolumnheader">
                          <xsl:with-param name="align" select="$titlealign"/>
                          <xsl:with-param name="width" select="$width"/>
                          <xsl:with-param name="caption" select="$title"/>
                          <xsl:with-param name="pageURL" select="$pageURL"/>
                          <xsl:with-param name="sortable" select="$sortable"/>
                          <xsl:with-param name="current-sortby" select="$sortby"/>
                          <xsl:with-param name="current-sortby-direction" select="$sortby-direction"/>
                          <xsl:with-param name="sortby" select="$orderby"/>
                          <xsl:with-param name="asc_sign" select="'ASC'"/>
                          <xsl:with-param name="desc_sign" select="'DESC'"/>
                        </xsl:call-template>

                      </xsl:for-each>

                      <!-- Status column header -->
                      <xsl:call-template name="tablecolumnheader">
                        <xsl:with-param name="width" select="'50'"/>
                        <xsl:with-param name="align" select="'center'"/>
                        <xsl:with-param name="caption" select="'%fldStatus% '"/>
                        <xsl:with-param name="sortable" select="'true'"/>
                        <xsl:with-param name="pageURL" select="$pageURL"/>
                        <xsl:with-param name="current-sortby" select="$sortby"/>
                        <xsl:with-param name="current-sortby-direction" select="$sortby-direction"/>
                        <xsl:with-param name="sortby" select="'@status'"/>
                        <xsl:with-param name="asc_sign" select="'ASC'"/>
                        <xsl:with-param name="desc_sign" select="'DESC'"/>
                      </xsl:call-template>

                      <!-- Content operations header -->
                      <xsl:call-template name="tablecolumnheader">
                        <xsl:with-param name="width" select="'146'"/>
                        <xsl:with-param name="caption" select="''"/>
                        <xsl:with-param name="sortable" select="'false'"/>
                      </xsl:call-template>

                    </tr>

                    <!-- For each content -->
                    <xsl:for-each select="/data/contents/content">
                      <xsl:variable name="contentxpath">
                        <xsl:text>/data/contents/content[</xsl:text>
                        <xsl:value-of select="position()"/>
                        <xsl:text>]</xsl:text>
                      </xsl:variable>

                      <xsl:variable name="currentContent" select="."/>
                      
                      <xsl:variable name="suffix">
                        <xsl:call-template name="getsuffix">
                          <xsl:with-param name="fname" select="$currentContent/title"/>
                        </xsl:call-template>
                      </xsl:variable>

                      <xsl:variable name="isFileHandlerAndIsFileOfTypeImage"
                                    select="$contenthandler = 'attachment' and ($suffix = 'png' or $suffix = 'jpg' or $suffix = 'jpeg' or $suffix = 'gif')"/>

                      <xsl:variable name="tooltip-text">
                        <xsl:choose>
                          <xsl:when test="$popupmode">
                            <xsl:choose>
                              <xsl:when test="$isFileHandlerAndIsFileOfTypeImage">%alertImagesIsNotAllowedAsAttachment%</xsl:when>
                              <xsl:otherwise>%msgClickToSelect%</xsl:otherwise>
                            </xsl:choose>


                          </xsl:when>
                          <xsl:when test="@state = 0">%msgClickToEdit%</xsl:when>
                          <xsl:otherwise>%msgClickToOpen%</xsl:otherwise>
                        </xsl:choose>
                      </xsl:variable>

                      <xsl:variable name="page">
                        <xsl:choose>
                          <xsl:when test="$contenthandler = 'any'">
                            <xsl:text>994</xsl:text>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:value-of select="number(@contenttypekey) + 999"/>
                          </xsl:otherwise>
                        </xsl:choose>
                      </xsl:variable>

                      <input type="hidden" name="contenttypekey" value="{@contenttypekey}"/>

                      <xsl:if test="$isPopup">
                        <script type="text/javascript">
                          try {
                          <xsl:choose>
                            <xsl:when test="$subop = 'relatedcontent'">
                              var isAdded = window.top.opener.getRelatedContent('<xsl:value-of select="$fieldname"/>', '<xsl:value-of select="$fieldrow"/>',<xsl:value-of select="@key"/>);
                            </xsl:when>
                            <xsl:when test="$subop = 'addcontenttosection' or $subop = 'contentfield'">
                              var isAdded = contentAdded(<xsl:value-of select="@key"/>);
                            </xsl:when>
                            <xsl:otherwise>
                              var isAdded = window.top.opener.getRelatedFile('<xsl:value-of select="$fieldname"/>', '<xsl:value-of select="$fieldrow"/>',<xsl:value-of select="@key"/>);
                            </xsl:otherwise>
                          </xsl:choose>
                          } catch(err) {}
                        </script>
                      </xsl:if>
                      <tr id="content_row_{@key}">
                        <xsl:call-template name="tablerowpainter"/>
                        <xsl:variable name="key" select="@key"/>
                        <xsl:variable name="categorykey" select="categoryname/@key"/>
                        <xsl:variable name="ctykey" select="@contenttypekey"/>
                        
                         <xsl:variable name="clickable">
                            <xsl:choose>
                              <xsl:when test="@clickable">
                                <xsl:value-of select="@clickable"/>
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:apply-templates select="*" mode="clickable"/>
                              </xsl:otherwise>
                            </xsl:choose>
                          </xsl:variable>

                          <xsl:variable name="css-class">
                            <xsl:text>browsetablecell</xsl:text>
                            <xsl:if test="$clickable = 'false'">
                              <xsl:text>arrow</xsl:text>
                            </xsl:if>
                            <xsl:if test="$isFileHandlerAndIsFileOfTypeImage">
                              <xsl:text> is-added</xsl:text>
                            </xsl:if>
                            <xsl:if test="position() = last()">
                              <xsl:text> row-last</xsl:text>
                            </xsl:if>
                          </xsl:variable>

                        <!-- Display checkbox column -->
                        <td style="padding: 0px;" align="center" onclick="javascript: setChecked('batch_operation{@key}');">
                          <xsl:attribute name="class">
                            <xsl:text>cell-first</xsl:text>
                            <xsl:if test="position() = last()">
                              <xsl:text> row-last</xsl:text>
                            </xsl:if>
                          </xsl:attribute>
                          <xsl:if test="$subop = 'relatedimage' or $subop = 'relatedfile'">
                            <xsl:attribute name="title">%alertOneContentOnly%</xsl:attribute>
                          </xsl:if>

                          <input type="checkbox" style="margin:0;padding:0;border:none;" name="batch_operation" id="batch_operation{@key}" value="{@key}" onclick="javascript: setBatchButtonsEnabled(); setChecked('batch_operation{@key}');"/>
                          <input type="hidden" name="ischild_{@key}" id="ischild_{@key}" value="{@child}"/>
                          <input type="hidden" name="title_{@key}" id="title_{@key}" value="{title}"/>
                          <input type="hidden" name="content_versionkey_{@key}" id="content_versionkey_{@key}" value="{@versionkey}"/>
                        </td>

                        <td class="{$css-class}" style="{concat('width:', $icon-column-width ,'px')}">
                          <xsl:call-template name="addJSEvent">
                            <xsl:with-param name="node" select="$currentContent"/>
                          </xsl:call-template>
                          
                          <xsl:call-template name="display-content-icon">
                            <xsl:with-param name="content-node" select="."/>
                            <xsl:with-param name="title" select="title"/>
                            <xsl:with-param name="contenthandler-class-name" select="/data/contenttypes/contenttype[@key = current()/@contenttypekey]/@handler"/>
                            <xsl:with-param name="content-type-name" select="@contenttype"/>
                         </xsl:call-template>
                        </td>

                        <xsl:for-each select="saxon:evaluate($fieldsxpath)">
                          <!-- Column align -->
                          <xsl:variable name="columnalign">
                            <xsl:choose>
                              <xsl:when test="@columnalign">
                                <xsl:value-of select="@columnalign"/>
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:apply-templates select="*" mode="columnalign"/>
                              </xsl:otherwise>
                            </xsl:choose>
                          </xsl:variable>

                          <xsl:variable name="add-click-event" select="not(boolean(binarylink))"/>

                          <!-- Display the cell with the data -->
                          <td align="{$columnalign}">
                            <xsl:attribute name="class">
                              <xsl:value-of select="$css-class"/>
                            </xsl:attribute>
                            <xsl:choose>
                              <xsl:when test="$clickable = 'false'">
                                <!-- -->
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:attribute name="title">
                                  <xsl:value-of select="$tooltip-text"/>
                                </xsl:attribute>
                                <xsl:if test="$subop = 'relatedcontent'
                                    or $subop = 'relatedfile'
                                    or $subop = 'relatedfiles'
                                    or $subop = 'relatedimage'
                                    or $subop = 'addcontenttosection'
                                    or $subop = 'relatedimages'">
                                  <xsl:attribute name="id">
                                    <xsl:text>title_</xsl:text>
                                    <xsl:value-of select="$currentContent/@key"/>
                                  </xsl:attribute>
                                </xsl:if>

                                <xsl:if test="$add-click-event">
                                  <xsl:if test="not($isFileHandlerAndIsFileOfTypeImage)">
                                    <xsl:call-template name="addJSEvent">
                                      <xsl:with-param name="node" select="$currentContent"/>
                                    </xsl:call-template>
                                  </xsl:if>
                                </xsl:if>

                              </xsl:otherwise>
                            </xsl:choose>       


                            <!-- Browser hack -->
                            <a>
                              <xsl:attribute name="style">display:none;</xsl:attribute>
                              <xsl:attribute name="id">
                                <xsl:text>operation_insert_</xsl:text>
                                <xsl:value-of select="$currentContent/@key"/>
                                <xsl:value-of select="$currentContent/@versionkey"/>
                              </xsl:attribute>
                              <xsl:attribute name="href">
                                <xsl:text>adminpage?</xsl:text>
                                <xsl:text>page=</xsl:text>
                                <xsl:value-of select="$page"/>
                                <xsl:text>&amp;key=</xsl:text>
                                <xsl:value-of select="$currentContent/@key"/>
                                <xsl:text>&amp;cat=</xsl:text>
                                <xsl:value-of select="$currentContent/categoryname/@key"/>
                                <xsl:text>&amp;op=</xsl:text>
                            <xsl:choose>
                                  <xsl:when test="$contenthandler = 'any'">
                                    <xsl:text>linkcontentineditor</xsl:text>
                                  </xsl:when>
                                  <xsl:otherwise>
                                    <xsl:text>insert</xsl:text>
                                  </xsl:otherwise>
                                </xsl:choose>
                                <xsl:text>&amp;subop=insert</xsl:text>
                              </xsl:attribute>
                              <xsl:text>&nbsp;</xsl:text>
                            </a>

                            <xsl:choose>
                              <!-- If this is a search result -->
                              <xsl:when test="$searchtype">
                                <span>
                                  <xsl:attribute name="title">
                                    <xsl:variable name="categoryxpath"
                                                  select="/data/categories//category[@key = $categorykey]"/>
                                    <xsl:call-template name="displaycontentpath">
                                      <xsl:with-param name="pathelem" select="$categoryxpath"/>
                                      <xsl:with-param name="levels" select="'100'"/>
                                    </xsl:call-template>
                                  </xsl:attribute>
                                  <xsl:choose>
                                    <xsl:when
                                        test="position() = 1 and (/data/contenttypes/contenttype[@key = $ctykey]/moduledata/browse/column[@maincolumn='true']/*)">
                                      <xsl:apply-templates
                                          select="/data/contenttypes/contenttype[@key = $ctykey]/moduledata/browse/column[@maincolumn='true']/*"
                                          mode="display">
                                        <xsl:with-param name="contentxpath" select="concat($contentxpath,'/')"/>
                                        <xsl:with-param name="contentelem" select="saxon:evaluate($contentxpath)"/>
                                        <xsl:with-param name="contenttypeelem"
                                                        select="/data/contenttypes/contenttype[@key = $ctykey]"/>
                                      </xsl:apply-templates>
                                    </xsl:when>
                                    <xsl:otherwise>
                                      <xsl:apply-templates select="*" mode="display">
                                        <xsl:with-param name="contentxpath" select="concat($contentxpath,'/')"/>
                                        <xsl:with-param name="contentelem" select="saxon:evaluate($contentxpath)"/>
                                        <xsl:with-param name="contenttypeelem"
                                                        select="/data/contenttypes/contenttype[@key = $ctykey]"/>
                                      </xsl:apply-templates>
                                    </xsl:otherwise>
                                  </xsl:choose>
                                </span>
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:apply-templates select="*" mode="display">
                                  <xsl:with-param name="contentxpath" select="concat($contentxpath,'/')"/>
                                  <xsl:with-param name="contentelem" select="saxon:evaluate($contentxpath)"/>
                                  <xsl:with-param name="contenttypeelem"
                                                  select="/data/contenttypes/contenttype[@key = $ctykey]"/>
                                </xsl:apply-templates>
                              </xsl:otherwise>
                            </xsl:choose>
                            <br/>
                          </td>

                        </xsl:for-each>

                        <td align="center" class="{$css-class}" title="{$tooltip-text}">
                          <xsl:attribute name="id">
                            <xsl:text>operation_default_</xsl:text>
                            <xsl:value-of select="$key"/>
                          </xsl:attribute>
                          <xsl:call-template name="addJSEvent">
                            <xsl:with-param name="node" select="$currentContent"/>
                          </xsl:call-template>

                          <!-- Status column -->
                          <xsl:call-template name="publishstatus">
                            <xsl:with-param name="key" select="@key"/>
                            <xsl:with-param name="state" select="@state"/>
                            <xsl:with-param name="publishfrom" select="@publishfrom"/>
                            <xsl:with-param name="publishto" select="@publishto"/>
                          </xsl:call-template>
                        </td>

                        <!-- Content operations column -->
                        <td align="center" class="{$css-class}">
                          <xsl:call-template name="contentoperations">
                            <xsl:with-param name="contentelem" select="."/>

                            <xsl:with-param name="includepreview" select="true()"/>
                            <xsl:with-param name="includepublish" select="$hasCategoryPublish = true() and not($popupmode)"/>
                            <xsl:with-param name="includecopy" select="$hasCategoryPublish = true()"/>
                            <xsl:with-param name="includemove" select="$hasCategoryPublish = true()"/>
                            <xsl:with-param name="includeremove" select="$hasCategoryPublish = true()"/>

                            <xsl:with-param name="contenttypekey" select="@contenttypekey"/>
                            <xsl:with-param name="cat" select="$cat"/>
                            <xsl:with-param name="key" select="@key"/>
                            <xsl:with-param name="versionkey">
                              <xsl:choose>
                                <xsl:when test="@has-draft = 'true'">
                                  <xsl:value-of select="versions/version[@status = 0]/@key"/>
                                </xsl:when>
                                <xsl:otherwise>
                                  <xsl:value-of select="@versionkey"/>
                                </xsl:otherwise>
                              </xsl:choose>
                            </xsl:with-param>
                            <xsl:with-param name="ischild" select="@child"/>
                            <xsl:with-param name="contenttypeelem" select="/data/contenttypes/contenttype[@key = $ctykey]"/>
                            <xsl:with-param name="includeparams">
                              <xsl:if test="$fieldname">
                                <xsl:text>&amp;subop=</xsl:text>
                                <xsl:value-of select="$subop"/>
                                <xsl:text>&amp;fieldname=</xsl:text>
                                <xsl:value-of select="$fieldname"/>
                                <xsl:text>&amp;fieldrow=</xsl:text>
                                <xsl:value-of select="$fieldrow"/>
                              </xsl:if>
                              <xsl:if test="$contenttypestring">
                                <xsl:text>&amp;contenttypestring=</xsl:text>
                                <xsl:value-of select="$contenttypestring"/>
                              </xsl:if>
                              <xsl:if test="$minoccurrence">
                                <xsl:text>&amp;minoccurrence=</xsl:text>
                                <xsl:value-of select="$minoccurrence"/>
                              </xsl:if>
                              <xsl:if test="$maxoccurrence">
                                <xsl:text>&amp;maxoccurrence=</xsl:text>
                                <xsl:value-of select="$maxoccurrence"/>
                              </xsl:if>
                            </xsl:with-param>
                            <xsl:with-param name="editable-locked-version" select="not(@state = 0)"/>
                          </xsl:call-template>
                        </td>
                      </tr>

                      <xsl:if test="$isPopup">
                        <script type="text/javascript">
                          if ( isAdded ) {
                            paintIsAdded( <xsl:value-of select="@key"/> );
                          }
                        </script>
                      </xsl:if>

                    </xsl:for-each>

                  </table>

                  <xsl:call-template name="operationsBar">
                    <xsl:with-param name="pos" select="2"/>
                  </xsl:call-template>

                </fieldset>

              </form>

            </xsl:otherwise>
          </xsl:choose>

          <script type="text/javascript">
            var searchField = document.getElementById('searchtext');
            if (searchField) {
              searchField.focus();
            }
          </script>

        </body>
      </html>
    </xsl:template>

  <xsl:template name="addJSEvent">
    <xsl:param name="node"/>
    <xsl:attribute name="onclick">
      <xsl:choose>
        <!--xsl:when test="$subop = 'addcontenttosection'">
          <xsl:text>var error = window.top.opener.popup_callback(</xsl:text>
          <xsl:value-of select="$node/@key"/>
          <xsl:text>, '</xsl:text>
          <xsl:call-template name="escapequotes">
            <xsl:with-param name="string" select="$node/title"/>
          </xsl:call-template>
          <xsl:text>');if (error) alert(error);</xsl:text>
        </xsl:when-->
        <xsl:when test="$subop = 'callback_single'">
          <xsl:text>var error = window.top.opener.popup_callback(</xsl:text>
          <xsl:value-of select="$node/@key"/>
          <xsl:text>, '</xsl:text>
          <xsl:call-template name="escapequotes">
            <xsl:with-param name="string" select="$node/title"/>
          </xsl:call-template>
          <xsl:text>');if (error) alert(error); else window.top.close();</xsl:text>
        </xsl:when>
        <!--xsl:when test="$subop = 'insert'">
          <xsl:choose>
            <xsl:when test="$contenthandler = 'file'">
              <xsl:text>navigateToInsertFileForm(</xsl:text>
              <xsl:value-of select="$node/@key"/>
              <xsl:value-of select="$node/@versionkey"/>
              <xsl:text>,'</xsl:text>
              <xsl:value-of select="$node/title"/>
              <xsl:text>'</xsl:text>
              <xsl:text>); return false;</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>javascript:if( document.all) {</xsl:text>
              <xsl:text>document.getElementById('operation_insert_</xsl:text>
              <xsl:value-of select="$node/@key"/>
              <xsl:value-of select="$node/@versionkey"/>
              <xsl:text>').click();</xsl:text>
              <xsl:text>} else { document.location.href = document.getElementById('operation_insert_</xsl:text>
              <xsl:value-of select="$node/@key"/>
              <xsl:value-of select="$node/@versionkey"/>
              <xsl:text>').href; }</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when-->
        <xsl:when test="$isPopup">
          <xsl:text>javascript: toggleCheckbox(</xsl:text>
          <xsl:value-of select="$node/@key"/>
          <xsl:text>);</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <!--
            Hack!!!
            Firefox < 2.0 is missing the click() method.
            https://bugzilla.mozilla.org/show_bug.cgi?id=148585
          -->
          <xsl:variable name="editbutton-id">
            <xsl:text>operation_edit_</xsl:text>
            <xsl:value-of select="$node/@key"/>
            <xsl:choose>
              <xsl:when test="$node/@has-draft = 'true'">
                <xsl:value-of select="$node/versions/version[@status = 0]/@key"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="$node/@versionkey"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>

          <xsl:text>javascript:if( document.all) {</xsl:text>
          <xsl:text>document.getElementById('</xsl:text>
          <xsl:value-of select="$editbutton-id"/>
          <xsl:text>').click();</xsl:text>
          <xsl:text>} else { document.location.href = document.getElementById('</xsl:text>
          <xsl:value-of select="$editbutton-id"/>
          <xsl:text>').href; }</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
  </xsl:template>


  <xsl:template name="operationsBar">
    <xsl:param name="pos" select="0"/>

    <xsl:variable name="css-margins">
      <xsl:choose>
        <xsl:when test="$pos = 1">operation-top</xsl:when>
        <xsl:when test="$pos = 2">operation-bottom</xsl:when>
      </xsl:choose>
    </xsl:variable>


    <table border="0" cellpadding="0" cellspacing="0" style="width:100%" class="{$css-margins}">
      <tr>
        <td>
          <div>
            <xsl:call-template name="batchControls">
              <xsl:with-param name="pos" select="$pos"/>
            </xsl:call-template>
          </div>
        </td>
        <td align="right">
          <xsl:call-template name="paging">
            <xsl:with-param name="url">
              <xsl:value-of select="$urlForNavigation"/>
            </xsl:with-param>
            <xsl:with-param name="index" select="$index"/>
            <xsl:with-param name="count" select="$count"/>
            <xsl:with-param name="totalcount" select="/data/contents/@totalcount"/>
            <xsl:with-param name="sortby" select="$sortby"/>
            <xsl:with-param name="sortby-direction" select="$sortby-direction"/>
          </xsl:call-template>

        </td>

        <td align="right">
          <xsl:call-template name="perPage">
            <xsl:with-param name="index" select="$index"/>
            <xsl:with-param name="count" select="$count"/>
            <xsl:with-param name="totalcount" select="/data/contents/@totalcount"/>
          </xsl:call-template>
          &nbsp;
          <xsl:call-template name="countSelect">
            <xsl:with-param name="count" select="$count"/>
          </xsl:call-template>
        </td>
      </tr>
    </table>

  </xsl:template>

  <xsl:template name="batchControls">
    <xsl:param name="pos" select="0"/>

    <!-- Batch add -->
    <xsl:if test="$isPopup">

      <xsl:variable name="countKeysInContentTypeString">
        <xsl:call-template name="inString">
          <xsl:with-param name="stringToSearchFor" select="/data/category/@contenttypekey"/>
          <xsl:with-param name="stringToSearchIn" select="$contenttypestring"/>
        </xsl:call-template>
      </xsl:variable>

      <!-- Hidden shim for tooltip (disabled elements does not have mouse/keyboard input -->
      <img src="./images/shim.gif" id="batchinsertbtn_shim_{$pos}" title="%alertOneContentOnly%" style="position:absolute; top:-100px; left:-100px;"/>

          <xsl:variable name="onlick">
            <xsl:choose>
              <xsl:when test="$contenthandler = 'any'">
                <xsl:text>insertContentToEditorLinkForm();</xsl:text>
              </xsl:when>
              <xsl:when test="$contenthandler = 'attachment'">
                <xsl:text>insertFileToEditorLinkForm();</xsl:text>
              </xsl:when>
              <xsl:when test="$contenthandler = 'image'">
                <xsl:text>navigateToEditorInsertImageForm();</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:choose>
                  <xsl:when test="$subop = 'addcontenttosection'">
                    <xsl:text>javascript:window.top.opener.popup_callback( window )</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:text>javascript: batchInsert('</xsl:text>
                    <xsl:value-of select="$subop"/>
                    <xsl:text>','</xsl:text>
                    <xsl:value-of select="$fieldname"/>
                    <xsl:text>',</xsl:text>
                    <xsl:value-of select="$fieldrow"/>
                    <xsl:text>);</xsl:text>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>

          <xsl:choose>
            <xsl:when test="$contenthandler = 'attachment' or $contenthandler = 'image' or $contenthandler = 'any'">
              <xsl:call-template name="button">
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="caption" select="'%cmdChoose%'"/>
                <xsl:with-param name="name" select="'batchinsertbtn'"/>
                <xsl:with-param name="disabled" select="'true'"/>
                <xsl:with-param name="onclick">
                  <xsl:value-of select="$onlick"/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="button">
                <xsl:with-param name="type" select="'button'"/>
                <xsl:with-param name="caption" select="'%cmdBatchAdd%'"/>
                <xsl:with-param name="name" select="'batchinsertbtn'"/>
                <xsl:with-param name="disabled" select="'true'"/>
                <xsl:with-param name="onclick">
                  <xsl:value-of select="$onlick"/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:otherwise>
          </xsl:choose>
      <xsl:text>&nbsp;</xsl:text>

    </xsl:if>

    <select disabled="true" name="batchSelector" onchange="changeBatchSelection( this )" class="content-operation">
      <option value="0">
        <xsl:choose>
          <xsl:when test="$isPopup">%cmdMoreActions% ...</xsl:when>
          <xsl:otherwise>%cmdChooseAction% ...</xsl:otherwise>
        </xsl:choose>
      </option>

      <xsl:if test="not($searchtype)">
        <option value="move">%cmdBatchMove%</option>
      </xsl:if>

      <xsl:if test="not($searchtype)">
        <option value="copy">%cmdBatchCopy%</option>
      </xsl:if>

      <xsl:if test="$hasCategoryPublish = true() or $hasCategoryAdministrate = true()">

        <xsl:if test="not($searchtype)">
          <option value="approve">%cmdBatchApprove%</option>
        </xsl:if>

        <xsl:if test="not($searchtype)">
          <option value="archive">%cmdBatchArchive%</option>
        </xsl:if>

      </xsl:if>

      <xsl:if test="$hasCategoryPublish = true() or $hasCategoryAdministrate = true()">
        <option value="delete">%cmdBatchDelete%</option>
      </xsl:if>

    </select>
  </xsl:template>
</xsl:stylesheet>

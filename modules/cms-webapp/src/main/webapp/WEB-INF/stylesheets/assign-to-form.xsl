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

  <xsl:output method="html"/>

  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="common/assignee-status.xsl"/>
  <xsl:include href="common/displayerror.xsl"/>
  <xsl:include href="common/formatdate.xsl"/>
  <xsl:include href="common/serialize.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>
  <xsl:include href="common/categoryheader.xsl"/>
  <xsl:include href="common/readonlyvalue.xsl"/>
  <xsl:include href="common/textfield.xsl"/>
  <xsl:include href="common/searchfield.xsl"/>
  <xsl:include href="common/checkbox_boolean.xsl"/>
  <xsl:include href="common/textarea.xsl"/>
  <xsl:include href="common/textfielddatetime.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/user-picker-with-autocomplete.xsl"/>

  <xsl:param name="key"/>
  <xsl:param name="versionkey"/>
  <xsl:param name="name"/>
  <xsl:param name="redirect"/>

  <xsl:template match="/">

    <html>
      <head>
        <link type="text/css" rel="stylesheet" href="css/admin.css"/>
        <link type="text/css" rel="styleSheet" href="javascript/tab.webfx.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/lib/jquery/ui/autocomplete/css/cms/jquery-ui-1.8.1.custom.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/lib/jquery/ui/autocomplete/css/cms/jquery-ui-1.8.1.overrides.css"/>
        <link type="text/css" rel="stylesheet" href="css/calendar_picker.css"/>
        <link type="text/css" rel="stylesheet" href="css/assignment.css"/>
        <link type="text/css" rel="stylesheet" href="css/user-picker-with-autocomplete.css"/>

        <script type="text/javascript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" src="javascript/admin.js">//</script>            
        <script type="text/javascript" src="javascript/validate.js">//</script>
        <script type="text/javascript" src="javascript/accessrights.js">//</script>
        <script type="text/javascript" src="javascript/lib/jquery/jquery-1.4.2.min.js">//</script>
        <script type="text/javascript" src="javascript/lib/jquery/ui/autocomplete/js/jquery-ui-1.8.1.custom.min.js">//</script>
        <script type="text/javascript" src="javascript/user-picker-with-autocomplete.js">//</script>
        <script type="text/javascript" src="javascript/assign-to-form.js">//</script>
        <script type="text/javascript" src="javascript/calendar_picker.js">//</script>
      </head>
      <body class="jquery-ui assignment">

        <xsl:call-template name="assignee-status">
          <xsl:with-param name="content" select="/model/content"/>
          <xsl:with-param name="selected-version-is-unsaved-draft" select="false()"/>
          <xsl:with-param name="editlockedversionmode" select="false()"/>
        </xsl:call-template>

        <h1>
          <xsl:call-template name="send-to-assignee-breadcrumb"/>
        </h1>

        <form action="adminpage" name="formAdmin" id="formAdmin" method="post">
          <input type="hidden" name="key" id="content-key" value="{$key}"/>
          <input type="hidden" name="cat" value="{$cat}"/>
          <input type="hidden" name="versionkey" value="{$versionkey}"/>
          <input type="hidden" name="page" value="994"/>
          <input type="hidden" name="op" value="save_and_assign"/>
          <input type="hidden" name="referer" value="{$referer}"/>

          <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td>
                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'button'"/>
                  <xsl:with-param name="caption" select="'%cmdAssign%'"/>
                  <xsl:with-param name="name" select="'send-button-1'"/>
                  <xsl:with-param name="disabled" select="'true'"/>
                  <xsl:with-param name="onclick">
                    <xsl:text>AssignToForm.validateAndSubmit();</xsl:text>
                  </xsl:with-param>
                </xsl:call-template>
                <xsl:text>&nbsp;</xsl:text>
                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'cancel'"/>
                  <xsl:with-param name="referer" select="$referer"/>
                </xsl:call-template>
              </td>
            </tr>
            <tr>
              <td class="form_title_form_seperator">
                <img src="images/1x1.gif"/>
              </td>
            </tr>
            <tr>
              <td>

                <xsl:call-template name="send-to-assignee-form"/>

              </td>
            </tr>
            <tr>
              <td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
            </tr>
            <tr>
              <td>
                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'button'"/>
                  <xsl:with-param name="caption" select="'%cmdAssign%'"/>
                  <xsl:with-param name="name" select="'send-button-2'"/>
                  <xsl:with-param name="disabled" select="'true'"/>
                  <xsl:with-param name="onclick">
                    <xsl:text>AssignToForm.validateAndSubmit();</xsl:text>
                  </xsl:with-param>
                </xsl:call-template>
                <xsl:text>&nbsp;</xsl:text>
                <xsl:call-template name="button">
                  <xsl:with-param name="type" select="'cancel'"/>
                  <xsl:with-param name="referer" select="$referer"/>
                </xsl:call-template>
              </td>
            </tr>
          </table>

          <script type="text/javascript">
            AssignToForm.enableDisableSendButtons();
          </script>
          
        </form>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="send-to-assignee-form">

    <xsl:variable name="version-change-comment" select="/model/content/versions/version[@status = 0]/@comment"/>

    <div class="tab-pane" id="tab-pane-1">
      <script type="text/javascript">
        var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
      </script>
      <div class="tab-page" id="tab-page-1">
        <span class="tab">%tabAssignTo%</span>
        <script type="text/javascript">
          tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
        </script>

        <xsl:call-template name="assignment-fieldset"/>

        <xsl:variable name="change-comment-help-element">
          <help>%hlpChangeComment%</help>
        </xsl:variable>
        
        <fieldset>
          <legend>%blockVersion%</legend>
          <table width="100%" cellspacing="0" cellpadding="2" border="0">
            <tr>
              <xsl:call-template name="textfield">
                <xsl:with-param name="name" select="'_comment'"/>
                <xsl:with-param name="id" select="'_comment'"/>
                <xsl:with-param name="label" select="'%fldChangeComment%:'"/>
                <xsl:with-param name="helpelement" select="$change-comment-help-element"/>
                <xsl:with-param name="selectnode" select="$version-change-comment"/>
                <xsl:with-param name="size" select="53"/>
                <xsl:with-param name="colspan" select="'1'"/>
              </xsl:call-template>
            </tr>
          </table>
        </fieldset>
      </div>
    </div>
    <script type="text/javascript">
      setupAllTabs();
    </script>
  </xsl:template>

  <xsl:template name="assignment-fieldset">

    <xsl:variable name="autocomplete-help-element">
      <help>%hlpAssignToAutocomplete%</help>
    </xsl:variable>

    <fieldset>
      <legend>%blockAssignment%</legend>
      <table width="100%" cellspacing="0" cellpadding="2" border="0">
        <tr>

          <xsl:call-template name="user-picker-with-autocomplete">
            <xsl:with-param name="name" select="'_assignee'"/>
            <xsl:with-param name="label" select="'%fldAssignee%:'"/>
            <xsl:with-param name="required" select="true()"/>
            <xsl:with-param name="size" select="53"/>
            <xsl:with-param name="help-element" select="$autocomplete-help-element"/>
            <xsl:with-param name="ajax-service-function-to-execute" select="'findUsersAndAccessType'"/>
            <xsl:with-param name="on-add-callback" select="'AssignToForm.addUser'"/>
            <xsl:with-param name="on-remove-callback" select="'AssignToForm.removeUser'"/>
          </xsl:call-template>
        </tr>
        <tr>
          <xsl:call-template name="textfielddatetime">
            <xsl:with-param name="name" select="'_assignment_duedate'"/>
            <xsl:with-param name="id" select="'_assignment_duedate'"/>
            <xsl:with-param name="label" select="'%fldDueDate%:'"/>
          </xsl:call-template>
        </tr>
        <tr>
          <xsl:call-template name="textarea">
            <xsl:with-param name="name" select="'_assignment_description'"/>
            <xsl:with-param name="id" select="'_assignment_description'"/>
            <xsl:with-param name="label" select="'%fldAssignmentDescr%:'"/>
            <xsl:with-param name="rows" select="'5'"/>
            <xsl:with-param name="cols" select="'50'"/>
            <xsl:with-param name="colspan" select="'1'"/>
          </xsl:call-template>
        </tr>
      </table>
    </fieldset>
  </xsl:template>

  <xsl:template name="send-to-assignee-breadcrumb">
    <a href="adminpage?mainmenu=true&amp;op=browse&amp;page=600">
      <xsl:text>%headContentRepositories%</xsl:text>
    </a>
    <xsl:call-template name="categoryheader">
      <xsl:with-param name="rootelem" select="/model" />
    </xsl:call-template>
    <xsl:text>&nbsp;</xsl:text>
    <span>
      <xsl:value-of select="concat('/ ', /model/content/title)"/>
    </span>
  </xsl:template>

</xsl:stylesheet>
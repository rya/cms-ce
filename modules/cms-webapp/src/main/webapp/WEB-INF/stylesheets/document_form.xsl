<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>

  <xsl:include href="common/standard_form_templates.xsl"/>
  <xsl:include href="common/generic_formheader.xsl"/>
  <xsl:include href="common/textfield.xsl"/>
  <xsl:include href="common/textarea.xsl"/>
  <xsl:include href="editor/xhtmleditor.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>

  <xsl:param name="create"/>
  <xsl:param name="unitkey"/>
  <xsl:param name="contenttypekey"/>
  <xsl:param name="modulename"/>
  <xsl:param name="referer"/>
  <xsl:param name="expertcontributor"/>
  <xsl:param name="developer"/>

  <xsl:param name="accessToHtmlSource">
    <xsl:choose>
      <xsl:when test="$expertcontributor = 'true' or $developer = 'true'">
        <xsl:value-of select="true()"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="false()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:param>

  <xsl:template match="/">

    <html>
      <head>
        <title>CMS</title>

        <script type="text/javascript" src="javascript/cms/core.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Event.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Cookie.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Css.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Dimensions.js">//</script>
        <script type="text/javascript" src="javascript/cms/ui/SplitButton.js">//</script>

        <link type="text/css" rel="stylesheet" href="css/admin.css"/>
        <link type="text/css" rel="stylesheet" href="css/menu.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>
        <link rel="stylesheet" type="text/css" href="css/calendar_picker.css"/>
        <link rel="stylesheet" type="text/css" href="javascript/cms/ui/style.css"/>

        <script type="text/javascript" language="JavaScript" src="javascript/admin.js"/>
        <script type="text/javascript" language="JavaScript" src="javascript/accessrights.js"/>
        <script type="text/javascript" language="JavaScript" src="javascript/validate.js"/>
        <script type="text/javascript" language="JavaScript" src="javascript/tabpane.js"/>
        <script type="text/javascript" language="JavaScript" src="javascript/accessrights.js"/>
        <script type="text/javascript" language="JavaScript" src="javascript/menu.js"/>
        <script type="text/javascript" language="JavaScript" src="javascript/calendar_picker.js"/>
        <script type="text/javascript" language="JavaScript" src="javascript/properties.js"/>

        <script type="text/javascript" src="tinymce/jscripts/cms/Util.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/cms/Editor.js">//</script>
        <script type="text/javascript" src="tinymce/jscripts/tiny_mce/tiny_mce.js">//</script>


        <script type="text/javascript" language="JavaScript">
          // variables used by menu.js
          var branchOpen = new Array();
          var cookiename = "contentform";

          // array with names og compulsory fields
          // array with friendlyname, fieldname and function for validated fields
          var validatedFields = new Array(1);
          validatedFields[0] = new Array("%fldTitle%", "contentdata_title", validateRequired);

          function validateAll(formName)
          {
            // Copy the content from all editor instances to their textareas.
            tinyMCE.triggerSave();

            var f = document.forms[formName];

            if ( !checkAll(formName, validatedFields) )
            return;

            disableFormButtons();
            f.submit();
          }
        </script>
      </head>

      <xsl:call-template name="contentform"/>
    </html>

  </xsl:template>

  <xsl:template name="contenttypeform">
    <xsl:param name="readonly"/>
    <fieldset>
      <legend>&nbsp;%blockDocument%&nbsp;</legend>
      <table width="100%" border="0" cellspacing="2" cellpadding="2">
        <tr>
          <xsl:call-template name="textfield">
            <xsl:with-param name="name" select="'contentdata_title'"/>
            <xsl:with-param name="label" select="'%fldTitle%:'"/>
            <xsl:with-param name="selectnode" select="/contents/content/contentdata/title"/>
            <xsl:with-param name="size" select="'60'"/>
            <xsl:with-param name="maxlength" select="'100'"/>
            <xsl:with-param name="colspan" select="'3'"/>
            <xsl:with-param name="required" select="'true'"/>
            <xsl:with-param name="disabled" select="$readonly"/>
            <xsl:with-param name="onkeyup">
              <xsl:text>javascript:document.getElementById('titlename').innerHTML = this.value;</xsl:text>
            </xsl:with-param>
          </xsl:call-template>
        </tr>
        <tr>
          <xsl:call-template name="textarea">
            <xsl:with-param name="name" select="'contentdata_teaser'"/>
            <xsl:with-param name="label" select="'%fldTeaser%:'"/>
            <xsl:with-param name="selectnode" select="/contents/content/contentdata/teaser/text"/>
            <xsl:with-param name="rows" select="'5'"/>
            <xsl:with-param name="cols" select="'60'"/>
            <xsl:with-param name="colspan" select="'3'"/>
            <xsl:with-param name="disabled" select="$readonly"/>
          </xsl:call-template>
        </tr>
        <td width="120" class="form_labelcolumn" valign="top" nowrap="nowrap" >%fldDocument%:</td>
        <td colspan="2">
          <xsl:call-template name="xhtmleditor">
            <xsl:with-param name="id" select="'contentdata_body'"/>
            <xsl:with-param name="name" select="'contentdata_body'"/>
            <xsl:with-param name="content" select="/contents/content/contentdata/body/text"/>
            <xsl:with-param name="config" select="'document'"/>
            <xsl:with-param name="disabled" select="$readonly"/>
            <xsl:with-param name="accessToHtmlSource" select="$accessToHtmlSource = 'true'"/>
          </xsl:call-template>
        </td>
      </table>
    </fieldset>
  </xsl:template>

</xsl:stylesheet>
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>

  <xsl:include href="common/standard_form_templates.xsl"/>

  <xsl:include href="common/generic_formheader.xsl"/>
  <xsl:include href="editor/xhtmleditor.xsl"/>
  <xsl:include href="common/textarea.xsl"/>
  <xsl:include href="common/textfield.xsl"/>
  <xsl:include href="common/relatedlinks.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>

  <xsl:param name="create"/>
  <xsl:param name="contenttypekey"/>
  <xsl:param name="expertcontributor"/>
  <xsl:param name="developer"/>

  <xsl:param name="selected-menuitem-key"/>
  <xsl:param name="selected-menuitem-path"/>


  <xsl:param name="pagetemplatekey"/>

  <xsl:template match="/">

    <html>
      <head>
        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/accessrights.js">//</script>
        <script type="text/javascript" src="javascript/validate.js">//</script>
        <script type="text/javascript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" src="javascript/calendar_picker.js">//</script>
        <script type="text/javascript" src="javascript/properties.js">//</script>
        <script type="text/javascript" src="javascript/newsletter_form.js">//</script>
        <script type="text/javascript" src="javascript/content_form.js">//</script>

        <script type="text/javascript" src="javascript/cms/core.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Event.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Cookie.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Css.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Dimensions.js">//</script>
        <script type="text/javascript" src="javascript/cms/ui/SplitButton.js">//</script>

        <script type="text/javascript" language="JavaScript">
          var validatedFields = new Array(3);
          validatedFields[0] = new Array("%fldSubject%", "contentdata_subject", validateRequired);
          validatedFields[1] = new Array("%fldNewsletterPage%", "contentdata_newsletter_@menuitemkey", validateRequired);
          validatedFields[2] = new Array("%fldGeneratedNewsletter%", "contentdata_newsletter_XML", validateRequired);

          function validateAll( formName )
          {
            var f = document.forms[formName];

            if ( !checkAll(formName, validatedFields) )
              return false;

            disableFormButtons();
            f.submit();
            return true;
          }

          function renderNewsletter() {
            if ( confirm("%alertNewsletterReloadWarning%") )
            {
              document.forms['formAdmin'].setAttribute("action", "adminpage?page=<xsl:value-of select="$page"/>&amp;op=form&amp;cat=<xsl:value-of select="$cat"/>");
              document.forms['formAdmin'].submit();
            }
          }
        </script>

        <xsl:call-template name="waitsplash"/>

        <link href="css/admin.css" rel="stylesheet" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="javascript/cms/ui/style.css"/>

        <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>
        <link rel="stylesheet" type="text/css" href="css/assignment.css"/>
        <link rel="stylesheet" type="text/css" href="css/calendar_picker.css"/>

      </head>

      <xsl:call-template name="contentform"/>

    </html>
  </xsl:template>

  <xsl:template name="contenttypeform">
    <xsl:param name="readonly"/>
    
    <fieldset>
      <legend>&nbsp;%blockNewsletter%&nbsp;</legend>
      <table border="0">
        <tr>
          <xsl:call-template name="textfield">
            <xsl:with-param name="name" select="'contentdata_subject'"/>
            <xsl:with-param name="label" select="'%fldSubject%:'"/>
            <xsl:with-param name="selectnode" select="/contents/content/contentdata/subject"/>
            <xsl:with-param name="size" select="'40'"/>
            <xsl:with-param name="maxlength" select="'255'"/>
            <xsl:with-param name="disabled" select="$readonly"/>
            <xsl:with-param name="required" select="'true'"/>
          </xsl:call-template>
        </tr>
        <tr>
          <xsl:call-template name="textarea">
            <xsl:with-param name="name" select="'contentdata_summary'"/>
            <xsl:with-param name="label" select="'%fldSummary%:'"/>
            <xsl:with-param name="selectnode" select="/contents/content/contentdata/summary"/>
            <xsl:with-param name="rows" select="'5'"/>
            <xsl:with-param name="cols" select="'80'"/>
            <xsl:with-param name="disabled" select="$readonly"/>
          </xsl:call-template>
        </tr>
        <tr>
          
          <xsl:variable name="selectfunction">
            <xsl:text>javascript:OpenMenuItemsAcrossSitesSelectorWindowPage( 400, 300 );</xsl:text>
          </xsl:variable>
          <xsl:variable name="removefunction">
            <xsl:text>javascript:removeNewsletterMenuItemParam(this)</xsl:text>
          </xsl:variable>
          
          <xsl:call-template name="searchfield">
            <xsl:with-param name="name" select="'contentdata_newsletter_@menuitemkey'"/>
            <xsl:with-param name="id" select="'contentdata_newsletter_@menuitemkey'"/>
            <xsl:with-param name="label" select="concat('%fldNewsletterPage%', ':')"/>
            <xsl:with-param name="selectedkey" select="$selected-menuitem-key"/>
            <xsl:with-param name="selectnode" select="$selected-menuitem-path"/>
            <xsl:with-param name="size" select="'80'"/>
            <xsl:with-param name="maxlength" select="'1000'"/>
            <xsl:with-param name="buttonfunction" select="$selectfunction"/>
            <xsl:with-param name="removebutton" select="false()"/>
            <xsl:with-param name="colspan" select="'1'"/>
            <xsl:with-param name="disabled" select="$readonly"/>
            <xsl:with-param name="required" select="true()"/>
          </xsl:call-template>
        </tr>
        <tr>
          <td><br/></td>
          <td>
            <xsl:call-template name="button">
            <xsl:with-param name="type" select="'button'"/>
            <xsl:with-param name="caption" select="'%generateNewsletter%'"/>
            <xsl:with-param name="name" select="'render_newsletter'"/>
            <xsl:with-param name="onclick">
              <xsl:text>javascript: renderNewsletter();</xsl:text>
            </xsl:with-param>
            <xsl:with-param name="disabled" select="$readonly"/>
          </xsl:call-template>
          </td>
        </tr>
        <tr>
          <td class="form_labelcolumn" valign="top" nowrap="nowrap" colspan="2"><br/>
            <iframe id="contentdata_newsletter_XML_iframe" border="0" width="600" height="500" class="html-document-iframe">
              <xsl:comment> // </xsl:comment>
            </iframe>

            <textarea id="contentdata_newsletter_XML" name="contentdata_newsletter_XML" style="display: none">
              <xsl:call-template name="serialize">
                <xsl:with-param name="xpath" select="/contents/content/contentdata/newsletter"/>
              </xsl:call-template>
            </textarea>
            <script type="text/javascript">
              writeTextAreaValueToIFrameDocument('contentdata_newsletter_XML', '');
            </script>
          </td>
        </tr>
      </table>
    </fieldset>
  </xsl:template>
</xsl:stylesheet>
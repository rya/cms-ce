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

  <xsl:include href="common/standard_form_templates.xsl"/>

  <xsl:include href="common/textarea.xsl"/>
  <xsl:include href="common/textfield.xsl"/>
  <xsl:include href="common/attachmentlink.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>

  <xsl:param name="create"/>
  <xsl:param name="contenttypekey"/>

  <xsl:template match="/">
    <html>
      <head>
                
        <link type="text/css" rel="stylesheet" href="css/admin.css"/>
        <link type="text/css" rel="stylesheet" href="css/menu.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>
        <link rel="stylesheet" type="text/css" href="css/assignment.css"/>
        <link rel="stylesheet" type="text/css" href="css/calendar_picker.css"/>
        <link rel="stylesheet" type="text/css" href="javascript/cms/ui/style.css"/>

        <script type="text/javascript" language="JavaScript" src="javascript/admin.js">//</script>
        <xsl:if test="$subop = 'relatedcontent' or
                      $subop = 'relatedfiles' or
                      $subop = 'relatedfile' or
                      $subop = 'relatedimages' or
                      $subop = 'relatedimage' or
                      $subop = 'addcontenttosection' or
                      $subop = 'insert'">
          <script type="text/javascript" src="javascript/window.js"/>
          <script type="text/javascript">
            cms.window.attatchKeyEvent('close');
          </script>
        </xsl:if>

        <script type="text/javascript" src="javascript/accessrights.js">//</script>
        <script type="text/javascript" src="javascript/validate.js">//</script>
        <script type="text/javascript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" src="javascript/menu.js">//</script>

        <script type="text/javascript" src="javascript/cms/core.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Event.js">//</script>
        <script type="text/javascript" src="javascript/cms/utils/Cookie.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Css.js">//</script>
        <script type="text/javascript" src="javascript/cms/element/Dimensions.js">//</script>
        <script type="text/javascript" src="javascript/cms/ui/SplitButton.js">//</script>

        <script type="text/javascript" src="javascript/calendar_picker.js">//</script>
        <script type="text/javascript" src="javascript/content_form.js">//</script>
        <script type="text/javascript" src="javascript/properties.js">//</script>

        <xsl:call-template name="waitsplash"/>

        <script type="text/javascript" language="JavaScript">
          // variables used by menu.js
          var branchOpen = new Array();
          var cookiename = "contentform";

          var validatedFields = new Array(2);
          validatedFields[0] = new Array("%fldName%", "name", validateRequired);
          <xsl:if test="$create=1">
              validatedFields[1] = new Array("%fldFile%", "newfile", validateRequired);
          </xsl:if>
                    
          function validateAll( formName )
          {
            var f = document.forms[formName];

            if ( !checkAll(formName, validatedFields) )
              return;

            tabPane1.element.style.display="none";
            waitsplash();
            disableFormButtons();
            f.submit();
          }
          // -------------------------------------------------------------------------------------------------------------------------------

          function getFilename(fname)
          {
            var file = fname.match(/[^\/\\]+$/).toString();
            document.formAdmin.name.value = file;
            document.getElementById('description').focus();
          }
        </script>
      </head>

        <body>
            <script type="text/javascript">waitsplash();</script>
            <xsl:call-template name="contentform">
                <xsl:with-param name="multipart" select="true()"/>
            </xsl:call-template>
            <script type="text/javascript">removeWaitsplash();</script>
        </body>

    </html>
  </xsl:template>

    <xsl:template name="contenttypeform">
        <xsl:param name="readonly"/>
		
        <fieldset>
            <legend>&nbsp;%blockGeneral%&nbsp;</legend>
            <table border="0" cellspacing="2" cellpadding="0">
                <tr>
                    <xsl:call-template name="filefield">
                        <xsl:with-param name="name" select="'newfile'"/>
                        <xsl:with-param name="label" select="'%fldFile%:'"/>
                        <xsl:with-param name="required" select="'true'"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="disabled" select="$readonly"/>
                        <xsl:with-param name="onchange" select="'getFilename(this.value)'"/>
                    </xsl:call-template>
                </tr>
                <tr>
                    <xsl:call-template name="textfield">
                        <xsl:with-param name="name" select="'name'"/>
                        <xsl:with-param name="label" select="'%fldName%:'"/>
                        <xsl:with-param name="required" select="'true'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/name"/>
                        <xsl:with-param name="size" select="'60'"/>
                        <xsl:with-param name="maxlength" select="'255'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                    <input type="hidden" name="oldbinarydatakey">
                        <xsl:attribute name="value"><xsl:value-of select="/contents/content/contentdata/binarydata/@key"/></xsl:attribute>
                    </input>
                    <input type="hidden" name="oldfilesize">
                        <xsl:attribute name="value"><xsl:value-of select="/contents/content/contentdata/filesize"/></xsl:attribute>
                    </input>
                </tr>
                <xsl:if test="$create = '0'">
                    <tr>
                        <xsl:call-template name="attachmentlink">
                            <xsl:with-param name="name" select="/contents/content/contentdata/name"/>
                            <xsl:with-param name="selectnode" select="/contents/content/@key"/>
                            <xsl:with-param name="binarykey" select="/contents/content/contentdata/binarydata/@key"/>
                            <xsl:with-param name="label" select="''"/>
                            <xsl:with-param name="size" select="'65'"/>
                            <xsl:with-param name="colspan" select="'3'"/>
                        </xsl:call-template>
                    </tr>
                </xsl:if>
                <tr>
                    <xsl:call-template name="textarea">
                        <xsl:with-param name="name" select="'description'"/>
                        <xsl:with-param name="id" select="'description'"/>
                        <xsl:with-param name="label" select="'%fldDescription%:'"/>
                        <xsl:with-param name="selectnode" select="/contents/content/contentdata/description"/>
                        <xsl:with-param name="rows" select="'5'"/>
                        <xsl:with-param name="cols" select="'60'"/>
                        <xsl:with-param name="colspan" select="'3'"/>
                        <xsl:with-param name="disabled" select="$readonly"/>
                    </xsl:call-template>
                </tr>

                <xsl:variable name="keywords">
                    <xsl:for-each select="/contents/content/contentdata/keywords/keyword">
                        <xsl:value-of select="."/>
                        <xsl:text> </xsl:text>
                    </xsl:for-each>
                </xsl:variable>

                <xsl:variable name="keywords-help-element">
                    <xsl:element name="help">
                        <xsl:text>%hlpFileKeyWords%</xsl:text>
                    </xsl:element>
                </xsl:variable>

                <xsl:call-template name="textfield">
                    <xsl:with-param name="name" select="'keywords'"/>
                    <xsl:with-param name="label" select="'%fldKeywords%:'"/>
                    <xsl:with-param name="selectnode" select="normalize-space($keywords)"/>
                    <xsl:with-param name="helpelement" select="$keywords-help-element"/>
                    <xsl:with-param name="size" select="'60'"/>
                    <xsl:with-param name="maxlength" select="'100'"/>
                    <xsl:with-param name="colspan" select="'3'"/>
                    <xsl:with-param name="disabled" select="$readonly"/>
                </xsl:call-template>
            </table>
        </fieldset>
    </xsl:template>

</xsl:stylesheet>
<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
    ]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>
	
  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="common/textfield.xsl"/>
  <xsl:include href="common/textarea.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/displayuserstorepath.xsl"/>
  <xsl:include href="common/passwordfield.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>
  <xsl:include href="common/displayerror.xsl"/>
  <xsl:include href="common/serialize.xsl"/>
	
  <xsl:param name="create"/>
  <xsl:param name="authorize"/>
  <xsl:param name="uid"/>
  <xsl:param name="redirect"/>
  <xsl:param name="callback"/>
    
  <xsl:param name="userstorekey" />
  <xsl:param name="userstorename" />

  <xsl:template match="/">

    <html>
      <head>
        <link type="text/css" rel="StyleSheet" href="javascript/tab.webfx.css" />
        <link rel="stylesheet" type="text/css" href="css/admin.css"/>
        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/tabpane.js">//</script>
        <script type="text/javascript" src="javascript/validate.js">//</script>

        <script type="text/javascript" language="JavaScript">
          var validatedFields = new Array(13);
          validatedFields[0] = new Array("%fldPassword%", "password", validateRequired);
          validatedFields[1] = new Array("%fldRepeatPassword%", "repeatpassword", validateRequired);

          function validateAll(formName)
          {
            var form = document.forms[formName];

            if ( !checkAll(formName, validatedFields) )
            {
              return;
            }

            form.submit();
          }
        </script>
      </head>
      <body>
        <xsl:copy-of select="/"/>
        <h1>
          <xsl:call-template name="displayuserstorepath">
            <xsl:with-param name="mode" select="'users'"/>
            <xsl:with-param name="userstorekey" select="$userstorekey"/>
            <xsl:with-param name="userstorename" select="$userstorename"/>
            <xsl:with-param name="disabled" select="not($callback = '')"/>
          </xsl:call-template>
        </h1>
        <h2>
          %headUsersChangePasswordFor%: <xsl:value-of select="$uid"/>
        </h2>

        <form name="formAdmin" method="post">
       	
          <xsl:attribute name="action">
            <xsl:if test="$create=1">
              <xsl:text>adminpage?page=</xsl:text>
              <xsl:value-of select="$page"/>
              <xsl:text>&amp;op=changepassword</xsl:text>
            </xsl:if>
            <xsl:if test="$create=0">
              <xsl:text>adminpage?page=</xsl:text>
              <xsl:value-of select="$page"/>
              <xsl:text>&amp;op=update</xsl:text>
            </xsl:if>
          </xsl:attribute>
            
          <input type="hidden" name="redirect" value="{$redirect}"/>
          <input type="hidden" name="subop" value="submit"/>
          <input type="hidden" name="uid">
            <xsl:attribute name="value">
              <xsl:value-of select="$uid"/>
            </xsl:attribute>
          </input>

            
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td class="form_title_form_seperator"><img src="images/1x1.gif"/></td>
            </tr>
            <tr>
              <td>

                <div class="tab-pane" id="tab-pane-1">
                  <script type="text/javascript" language="JavaScript">
                    var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
                  </script>
                                    
                  <div class="tab-page" id="tab-page-standard">
                    <span class="tab">%fldPassword%</span>

                    <script type="text/javascript" language="JavaScript">
                      tabPane1.addTabPage( document.getElementById( "tab-page-standard" ) );
                    </script>

                    <xsl:call-template name="userform"/>
                  </div>
                </div>
              </td>
            </tr>
            <tr>
              <td class="form_form_buttonrow_seperator"><img src="images/1x1.gif"/></td>
            </tr>
          </table>

          <script type="text/javascript" language="JavaScript">
            setupAllTabs();
          </script>

          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'button'"/>
            <xsl:with-param name="caption" select="'%cmdSave%'"/>
            <xsl:with-param name="onclick">
              <xsl:text>jjavascript: validateAll('formAdmin');</xsl:text>
            </xsl:with-param>
          </xsl:call-template>
          <xsl:text>&nbsp;</xsl:text>
          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'button'"/>
            <xsl:with-param name="caption" select="'%cmdCancel%'"/>
            <xsl:with-param name="onclick">
              <xsl:text>javaScriptjavascript: history.back();</xsl:text>
            </xsl:with-param>
          </xsl:call-template>
        </form>

        <script type="text/javascript">
          setFocus();
        </script>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="userform">
    <table border="0" cellspacing="0" cellpadding="2">
      <xsl:if test="$authorize">
        <tr>
          <xsl:call-template name="passwordfield">
            <xsl:with-param name="name" select="'oldpassword'"/>
            <xsl:with-param name="label" select="'%fldOldPassword%:'"/>
            <xsl:with-param name="selectnode" select="''"/>
            <xsl:with-param name="size" select="'30'"/>
            <xsl:with-param name="maxlength" select="'30'"/>
            <xsl:with-param name="colspan" select="'1'"/>
            <xsl:with-param name="tdwidth" select="'150'"/>
            <xsl:with-param name="disableAutoComplete" select="true()"/>
          </xsl:call-template>
        </tr>
      </xsl:if>
      <tr>
        <xsl:call-template name="passwordfield">
          <xsl:with-param name="name" select="'password'"/>
          <xsl:with-param name="label" select="'%fldNewPassword%:'"/>
          <xsl:with-param name="selectnode" select="''"/>
          <xsl:with-param name="size" select="'30'"/>
          <xsl:with-param name="maxlength" select="'30'"/>
          <xsl:with-param name="colspan" select="'1'"/>
          <xsl:with-param name="tdwidth" select="'150'"/>
          <xsl:with-param name="disableAutoComplete" select="true()"/>
        </xsl:call-template>
      </tr>
      <tr>
        <xsl:call-template name="passwordfield">
          <xsl:with-param name="name" select="'repeatpassword'"/>
          <xsl:with-param name="label" select="'%fldRepeatPassword%:'"/>
          <xsl:with-param name="selectnode" select="''"/>
          <xsl:with-param name="size" select="'30'"/>
          <xsl:with-param name="maxlength" select="'30'"/>
          <xsl:with-param name="colspan" select="'1'"/>
          <xsl:with-param name="tdwidth" select="'150'"/>
          <xsl:with-param name="disableAutoComplete" select="true()"/>
        </xsl:call-template>
      </tr>
    </table>
  </xsl:template>

</xsl:stylesheet>
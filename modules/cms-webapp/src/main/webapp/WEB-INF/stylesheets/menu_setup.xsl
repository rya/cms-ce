<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>

  <xsl:include href="common/accessrights.xsl"/>
  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:include href="common/button.xsl"/>
  <xsl:include href="common/searchfield.xsl"/>
  <xsl:include href="common/labelcolumn.xsl"/>
  <xsl:include href="common/displayhelp.xsl"/>

  <xsl:variable name="menuelem" select="/model/selected-menu/menu"/>
  <xsl:variable name="frontPageElem" select="/model/front-page/menuitem"/>
  <xsl:variable name="loginPageElem" select="/model/login-page/menuitem"/>
  <xsl:variable name="errorPageElem" select="/model/error-page/menuitem"/>
  <xsl:variable name="defaultPageTemplate" select="/model/pagetemplates/pagetemplate[@key = $menuelem/defaultpagetemplate/@pagetemplatekey]"/>

  <xsl:template match="/">
    <html>
      <head>
        <link href="css/admin.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="javascript/admin.js">//</script>
        <script type="text/javascript" src="javascript/accessrights.js">//</script>
        <script type="text/javascript" src="javascript/validate.js">//</script>
        <script type="text/javascript" src="javascript/tabpane.js">//</script>
        <link type="text/css" rel="stylesheet" href="javascript/tab.webfx.css"/>

        <script type="text/javascript" language="JavaScript">
          function OpenSelectorWindow( page, width, height, returnkey, returnview, filter )
          {
              var l = ( screen.width - width ) / 2;
              var t = ( screen.height - height ) / 2;
              <xsl:text>newWindow = window.open( "adminpage?page=" + page + "&amp;op=select&amp;selectedunitkey=</xsl:text><xsl:value-of select="$selectedunitkey"/><xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/><xsl:text>&amp;returnkey="+ returnkey +"&amp;returnview="+ returnview +"&amp;filter="+ filter, "Preview", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height + ",top=" + t + ",left=" + l );</xsl:text>
              newWindow.focus();
          }

          function OpenSelectorWindowThis( objThis, page, width, height, returnkey, returnview, filter )
          {
              var currentRow = getCurrentObjectIndex( objThis, objThis.name );
              var l = ( screen.width - width ) / 2;
              var t = ( screen.height - height ) / 2;
              <xsl:text>newWindow = window.open( "adminpage?returnrow=" + currentRow + "&amp;page=" + page + "&amp;op=select&amp;selectedunitkey=</xsl:text><xsl:value-of select="$selectedunitkey"/><xsl:text>&amp;menukey=</xsl:text><xsl:value-of select="$menukey"/><xsl:text>&amp;returnkey="+ returnkey +"&amp;returnview="+ returnview +"&amp;filter="+ filter, "Preview", "toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=1,copyhistory=0,width=" + width + ",height=" + height + ",top=" + t + ",left=" + l );</xsl:text>
              newWindow.focus();
          }
        </script>
      </head>

      <body>
        <h1>
          %headPageBuilderSettings%
        </h1>
        <form action="adminpage?page={$page}&amp;op=setup&amp;subop=save" method="POST" name="formAdmin" id="formAdmin">

          <div class="tab-pane" id="tab-pane-1">
            <script type="text/javascript" language="JavaScript">
              var tabPane1 = new WebFXTabPane( document.getElementById( "tab-pane-1" ), true );
            </script>

            <div class="tab-page" id="tab-page-1">
              <span class="tab">%blockGeneral%</span>

              <script type="text/javascript" language="JavaScript">
                tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
              </script>

              <fieldset>
                <legend>&nbsp;%blockGeneral%&nbsp;</legend>

                <table cellspacing="0" cellpadding="2" border="0">
                  <tr>

                    <xsl:variable name="fpfunction">
                      <xsl:text>javascript: OpenSelectorWindow(</xsl:text>
                      <xsl:value-of select="$page"/>
                      <xsl:text>, 300, 400, 'frontpage_key', 'viewfrontpage_key');</xsl:text>
                    </xsl:variable>

                    <xsl:call-template name="searchfield">
                      <xsl:with-param name="label" select="'%fldFrontPage%:'"/>
                      <xsl:with-param name="name" select="'frontpage_key'"/>
                      <xsl:with-param name="selectedkey"
                                      select="$frontPageElem/@key"/>
                      <xsl:with-param name="selectnode"
                                      select="$frontPageElem/name"/>
                      <xsl:with-param name="size" select="'25'"/>
                      <xsl:with-param name="maxlength" select="'25'"/>
                      <xsl:with-param name="buttonfunction" select="$fpfunction"/>
                      <xsl:with-param name="colspan" select="'1'"/>
                    </xsl:call-template>
                  </tr>
                  <tr>

                    <xsl:variable name="lpfunction">
                      <xsl:text>javascript: OpenSelectorWindow(</xsl:text>
                      <xsl:value-of select="$page"/>
                      <xsl:text>, 300, 400, 'loginpage_key', 'viewloginpage_key', 'anonymous');</xsl:text>
                    </xsl:variable>

                    <xsl:call-template name="searchfield">
                      <xsl:with-param name="label" select="'%fldLoginPage%:'"/>
                      <xsl:with-param name="name" select="'loginpage_key'"/>
                      <xsl:with-param name="selectedkey"
                                      select="$loginPageElem/@key"/>
                      <xsl:with-param name="selectnode"
                                      select="$loginPageElem/name"/>
                      <xsl:with-param name="size" select="'25'"/>
                      <xsl:with-param name="maxlength" select="'25'"/>
                      <xsl:with-param name="buttonfunction" select="$lpfunction"/>
                      <xsl:with-param name="colspan" select="'1'"/>
                    </xsl:call-template>
                  </tr>
                  <tr>
                    <xsl:variable name="epfunction">
                      <xsl:text>javascript: OpenSelectorWindow(</xsl:text>
                      <xsl:value-of select="$page"/>
                      <xsl:text>, 300, 400, 'errorpage_key', 'viewerrorpage_key', 'anonymous');</xsl:text>
                    </xsl:variable>
                    <xsl:call-template name="searchfield">
                      <xsl:with-param name="label" select="'%fldErrorPage%:'"/>
                      <xsl:with-param name="name" select="'errorpage_key'"/>
                      <xsl:with-param name="selectedkey"
                                      select="$errorPageElem/@key"/>
                      <xsl:with-param name="selectnode"
                                      select="$errorPageElem/name"/>
                      <xsl:with-param name="size" select="'25'"/>
                      <xsl:with-param name="maxlength" select="'25'"/>
                      <xsl:with-param name="buttonfunction" select="$epfunction"/>
                      <xsl:with-param name="colspan" select="'1'"/>
                    </xsl:call-template>
                  </tr>
                  <tr>
                    <td>
                      %fldDefaultPageTemplate%:
                    </td>
                    <td>
                      <xsl:call-template name="framework_dropdown">
                        <xsl:with-param name="patkey" select="$defaultPageTemplate/@key"/>
                      </xsl:call-template>
                    </td>
                  </tr>
                  <tr>
                    <td>
                      <input type="hidden" name="selectedunitkey" value="{$selectedunitkey}"/>
                      <input type="hidden" name="menukey" value="{$menukey}"/>
                    </td>
                  </tr>
                </table>
              </fieldset>
            </div>

            <script type="text/javascript" language="JavaScript">
              setupAllTabs();
            </script>
          </div>
          <br/>
          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'submit'"/>
            <xsl:with-param name="caption" select="'%cmdSave%'"/>
            <xsl:with-param name="name" select="'lagre'"/>
          </xsl:call-template>
          <xsl:text>&nbsp;</xsl:text>
          <xsl:call-template name="button">
            <xsl:with-param name="type" select="'button'"/>
            <xsl:with-param name="caption" select="'%cmdCancel%'"/>
            <xsl:with-param name="name" select="'avbryt'"/>
            <xsl:with-param name="onclick">
              <xsl:text>javascript: history.back();</xsl:text>
            </xsl:with-param>
          </xsl:call-template>
        </form>
      </body>
    </html>
  </xsl:template>

  <xsl:template name="framework_dropdown">
    <xsl:param name="patkey"/>
    <select name="patkey">
      <option value="">%sysDropDownChoose%</option>
      <xsl:for-each select="/model/pagetemplates/pagetemplate">
        <option value="{@key}">
          <xsl:if test="@key = $patkey">
            <xsl:attribute name="selected"><xsl:text>selected</xsl:text></xsl:attribute>
          </xsl:if>
          <xsl:value-of select="name"/>
        </option>
      </xsl:for-each>
    </select>
  </xsl:template>
</xsl:stylesheet>

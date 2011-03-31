<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:template name="contentform">
    <xsl:param name="subfunctions"/>
    <xsl:param name="multipart" select="false()"/>
    <xsl:param name="show-edit-content-button" select="true()"/>

    <body>

      <xsl:call-template name="assignee-status">
        <xsl:with-param name="content" select="/contents/content"/>
        <xsl:with-param name="selected-version-is-unsaved-draft" select="not(/contents/content/@state) or ( $editlockedversionmode and not(/contents/content/@has-draft = 'true') )"/>
        <xsl:with-param name="editlockedversionmode" select="$editlockedversionmode"/>
      </xsl:call-template>
      
      <xsl:call-template name="version-form">
        <xsl:with-param name="contentxpath" select="/contents/content"/>
        <xsl:with-param name="referer" select="$referer"/>
      </xsl:call-template>

      <form name="formAdmin" method="post">
				<xsl:if test="$multipart">
          <xsl:attribute name="enctype">multipart/form-data</xsl:attribute>
        </xsl:if>
        <xsl:attribute name="action">
          <xsl:if test="$create=1">
            <xsl:text>adminpage?page=</xsl:text>
            <xsl:value-of select="$page"/>
            <xsl:text>&amp;op=create</xsl:text>
            <xsl:text>&amp;cat=</xsl:text>
            <xsl:value-of select="$cat"/>
          </xsl:if>
          <xsl:if test="$create=0">
            <xsl:text>adminpage?page=</xsl:text>
            <xsl:value-of select="$page"/>
            <xsl:text>&amp;op=update</xsl:text>
            <xsl:text>&amp;cat=</xsl:text>
            <xsl:value-of select="$cat"/>
          </xsl:if>
        </xsl:attribute>

        <xsl:call-template name="generalhiddenfields"/>

        <xsl:call-template name="contentheader">
          <xsl:with-param name="links" select="not($subop = 'popup')"/>
        </xsl:call-template>

        <xsl:call-template name="displayfeedback">
          <xsl:with-param name="addbr" select="true()"/>
        </xsl:call-template>

        <xsl:call-template name="contentformbuttons">
          <xsl:with-param name="subfunctions" select="$subfunctions"/>
          <xsl:with-param name="topbuttons" select="true()"/>
          <xsl:with-param name="show-edit-content-button" select="$show-edit-content-button"/>
        </xsl:call-template>

        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td class="form_title_form_seperator"><img src="images/1x1.gif"/></td>
          </tr>
          <tr>
            <td>
              <div class="tab-pane" id="tab-container">
                <script type="text/javascript" language="JavaScript">
                  var tabPane1 = new WebFXTabPane( document.getElementById( "tab-container" ), true );
                </script>
                <div class="tab-page" id="tab-page-1">
                  <span class="tab">%blockContent%</span>
                  <script type="text/javascript" language="JavaScript">
                    tabPane1.addTabPage( document.getElementById( "tab-page-1" ) );
                  </script>

                  <xsl:call-template name="statusAndVersionstuff"/>

                  <xsl:call-template name="contenttypeform">
                    <xsl:with-param name="readonly" select="not($enableform)"/>
                  </xsl:call-template>
                </div>

                <!--xsl:call-template name="versionpage"/-->
                <xsl:call-template name="properties"/>
                <xsl:call-template name="publishing"/>
                <xsl:call-template name="content_accessrights"/>
              	<xsl:call-template name="content_usedby">
              		<xsl:with-param name="contentKey" select="/contents/content/@key"/>
              	</xsl:call-template>
                <xsl:call-template name="content_source"/>
              </div>
              <script type="text/javascript" language="JavaScript">
                setupAllTabs();
              </script>
            </td>
          </tr>
          <tr>
            <td>
              <br/>
            </td>
          </tr>
          <xsl:call-template name="contentformbuttons">
            <xsl:with-param name="subfunctions" select="$subfunctions"/>
            <xsl:with-param name="show-edit-content-button" select="$show-edit-content-button"/>
          </xsl:call-template>
        </table>
      </form>
    </body>
  </xsl:template>

</xsl:stylesheet>
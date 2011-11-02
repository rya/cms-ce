<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:include href="accessrights.xsl"/>

  <xsl:template name="content_accessrights">

    <!--xsl:if test="$create = 1 or $current_uid = /contents/content/owner/name or ( not(/contents/userright) or /contents/userright/@publish = 'true' )"-->
    <div class="tab-page" id="tab-page-security">
      <span class="tab">%blockSecurity%</span>

      <script type="text/javascript" language="JavaScript">
        tabPane1.addTabPage( document.getElementById( "tab-page-security" ) );
      </script>

      <fieldset>
        <legend>&nbsp;%blockSecurity%&nbsp;</legend>
        <xsl:choose>
          <xsl:when test="$create = 0">
            <xsl:call-template name="accessrights">
              <xsl:with-param name="right_create_available" select="false()"/>
              <xsl:with-param name="right_publish_available" select="false()"/>
              <xsl:with-param name="right_administrate_available" select="false()"/>
              <xsl:with-param name="dataxpath" select="/contents/content"/>
              <xsl:with-param name="readonly" select="not($categoryadmin)"/>
              <xsl:with-param name="allowauthenticated" select="true()"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="accessrights">
              <xsl:with-param name="right_create_available" select="false()"/>
              <xsl:with-param name="right_publish_available" select="false()"/>
              <xsl:with-param name="right_administrate_available" select="false()"/>
              <xsl:with-param name="dataxpath" select="/contents"/>
              <xsl:with-param name="readonly" select="not($categoryadmin)"/>
              <xsl:with-param name="allowauthenticated" select="true()"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </fieldset>
    </div>
    <!--/xsl:if-->

  </xsl:template>

</xsl:stylesheet>
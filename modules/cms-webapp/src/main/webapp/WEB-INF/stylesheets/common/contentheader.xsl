<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:template name="contentheader">
    <xsl:param name="links" select="true()"/>
    <xsl:param name="subop"/>
    <xsl:param name="fieldrow"/>
    <xsl:param name="fieldname"/>
    <h1>
      <xsl:call-template name="genericheader">
        <xsl:with-param name="endslash" select="false()"/>
        <xsl:with-param name="links" select="$links"/>
        <xsl:with-param name="subop" select="$subop"/>
        <xsl:with-param name="fieldrow" select="$fieldrow"/>
        <xsl:with-param name="fieldname" select="$fieldname"/>
        <xsl:with-param name="minoccurrence" select="$minoccurrence"/>
        <xsl:with-param name="maxoccurrence" select="$maxoccurrence"/>
      </xsl:call-template>
      <xsl:call-template name="categoryheader">
        <xsl:with-param name="nolinks" select="not($links)"/>
        <xsl:with-param name="subop" select="$subop"/>
        <xsl:with-param name="fieldrow" select="$fieldrow"/>
        <xsl:with-param name="fieldname" select="$fieldname"/>
        <xsl:with-param name="minoccurrence" select="$minoccurrence"/>
        <xsl:with-param name="maxoccurrence" select="$maxoccurrence"/>
      </xsl:call-template>
      <xsl:text>&nbsp;</xsl:text>
      <span id="titlename" title="%name%">
        <xsl:if test="$create != 1">
          <xsl:value-of select="concat('/ ', /contents/content/name)"/>
        </xsl:if>
      </span>
    </h1>
  </xsl:template>
</xsl:stylesheet>
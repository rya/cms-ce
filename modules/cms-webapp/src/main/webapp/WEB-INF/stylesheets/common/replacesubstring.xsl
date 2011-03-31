<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:output method="html"/>

  <!-- Standard substring replace template -->
  <xsl:template name="replacesubstring">
    <xsl:param name="stringsource"/>
    <xsl:param name="substringsource"/>
    <xsl:param name="substringdest"/>

    <xsl:choose>

      <xsl:when test="contains($stringsource,$substringsource)">
        <xsl:value-of select="concat(substring-before($stringsource,$substringsource),$substringdest)"/>
        <xsl:call-template name="replacesubstring">
          <xsl:with-param name="stringsource" select="substring-after($stringsource,$substringsource)"/>
          <xsl:with-param name="substringsource" select="$substringsource"/>
          <xsl:with-param name="substringdest" select="$substringdest"/>
        </xsl:call-template>
      </xsl:when>

      <xsl:otherwise>
        <xsl:value-of select="$stringsource"/>
      </xsl:otherwise>

    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
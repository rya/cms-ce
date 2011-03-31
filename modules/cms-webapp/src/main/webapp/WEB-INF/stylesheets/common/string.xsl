<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

	<xsl:output method="html"/>

  <!--
    Traverses a comma delimited string and looks for string needle.
    Returns an integer. >-1 = found.
   -->
  <xsl:template name="inString">
    <xsl:param name="stringToSearchFor"/>
    <xsl:param name="stringToSearchIn"/>
    <xsl:param name="delimiter" select="','"/>
    <xsl:param name="count" select="0"/>
    <xsl:variable name="currentString">
      <xsl:choose>
        <xsl:when test="contains($stringToSearchIn, $delimiter)">
          <xsl:value-of select="substring-before($stringToSearchIn, $delimiter)"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$stringToSearchIn"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="restStr" select="substring-after($stringToSearchIn, $delimiter)"/>

    <xsl:variable name="_count">
      <xsl:choose>
        <xsl:when test="$currentString = $stringToSearchFor">
          <xsl:value-of select="$count + 1"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$count"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="contains($restStr, $delimiter) or $restStr !=''">
        <xsl:call-template name="inString">
          <xsl:with-param name="stringToSearchFor" select="$stringToSearchFor"/>
          <xsl:with-param name="stringToSearchIn" select="$restStr"/>
          <xsl:with-param name="count" select="$_count"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$_count"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="string-replace-all">
    <xsl:param name="text"/>
    <xsl:param name="replace"/>
    <xsl:param name="by"/>
    <xsl:choose>
      <xsl:when test="contains($text,$replace)">
        <xsl:value-of select="substring-before($text,$replace)"/>
        <xsl:value-of select="$by"/>
        <xsl:call-template name="string-replace-all">
          <xsl:with-param name="text" select="substring-after($text,$replace)"/>
          <xsl:with-param name="replace" select="$replace"/>
          <xsl:with-param name="by" select="$by"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
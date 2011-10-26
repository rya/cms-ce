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
  <xsl:include href="common/generic_parameters.xsl"/>
  <xsl:param name="pagetemplatekey"/>
  <xsl:param name="contentkey"/>
  <xsl:param name="versionkey"/>
  <xsl:param name="contenttypekey"/>
  <xsl:param name="contenttitle"/>
  <xsl:param name="sessiondata"/>
  <xsl:param name="menuitemkey"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>
          <xsl:text>%headPreview%</xsl:text>
          <xsl:if test="$contenttitle != ''">
            <xsl:text>:</xsl:text>
            <xsl:value-of select="$contenttitle"/>
          </xsl:if>
        </title>
      </head>
      <frameset name="previewFrameset" rows="55, *">
        <frame name="topFrame" scrolling="no" bottommargin="0">
          <xsl:attribute name="src">
            <xsl:text>adminpage?page=</xsl:text>
            <xsl:value-of select="$page"/>
            <xsl:text>&amp;op=preview&amp;subop=list</xsl:text>
            <xsl:if test="$selectedunitkey != ''">
              <xsl:text>&amp;selectedunitkey=</xsl:text>
              <xsl:value-of select="$selectedunitkey"/>
            </xsl:if>
            <xsl:if test="$pagetemplatekey != ''">
              <xsl:text>&amp;pagetemplatekey=</xsl:text>
              <xsl:value-of select="$pagetemplatekey"/>
            </xsl:if>
            <xsl:if test="$contentkey != ''">
              <xsl:text>&amp;contentkey=</xsl:text>
              <xsl:value-of select="$contentkey"/>
            </xsl:if>
            <xsl:if test="$versionkey != ''">
              <xsl:text>&amp;versionkey=</xsl:text>
              <xsl:value-of select="$versionkey"/>
            </xsl:if>
            <xsl:if test="$contenttypekey != ''">
              <xsl:text>&amp;contenttypekey=</xsl:text>
              <xsl:value-of select="$contenttypekey"/>
            </xsl:if>
            <xsl:if test="$sessiondata = 'true'">
              <xsl:text>&amp;sessiondata=true</xsl:text>
            </xsl:if>
            <xsl:if test="$menuitemkey != ''">
              <xsl:text>&amp;menuitemkey=</xsl:text>
              <xsl:value-of select="$menuitemkey"/>
            </xsl:if>
            <xsl:if test="$menukey != ''">
              <xsl:text>&amp;menukey=</xsl:text>
              <xsl:value-of select="$menukey"/>
            </xsl:if>
          </xsl:attribute>
        </frame>
        <frame name="mainFrame"/>
      </frameset>
    </html>
  </xsl:template>
</xsl:stylesheet>
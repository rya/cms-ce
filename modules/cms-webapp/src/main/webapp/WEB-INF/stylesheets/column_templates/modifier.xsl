<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:template match="modifier" mode="display">
    <xsl:param name="contentelem"/>
    <xsl:value-of select="$contentelem/modifier/display-name"/>
  </xsl:template>

  <xsl:template match="modifier" mode="title">
    <xsl:text>%fldModifiedBy%</xsl:text>
  </xsl:template>

  <xsl:template match="modifier" mode="orderby">
    <xsl:text>modifier</xsl:text>
  </xsl:template>

  <xsl:template match="modifier" mode="width">
    <xsl:text>150</xsl:text>
  </xsl:template>

</xsl:stylesheet>

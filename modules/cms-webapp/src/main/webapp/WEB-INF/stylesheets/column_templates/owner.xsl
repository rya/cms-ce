<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:template match="owner" mode="display">
    <xsl:param name="contentelem"/>
    <xsl:value-of select="$contentelem/owner/display-name"/>
  </xsl:template>

  <xsl:template match="owner" mode="title">
    <xsl:text>%fldOwner%</xsl:text>
  </xsl:template>

  <xsl:template match="owner" mode="orderby">
    <xsl:text>owner/qualifiedname</xsl:text>
  </xsl:template>

  <xsl:template match="owner" mode="width">
    <xsl:text>150</xsl:text>
  </xsl:template>

</xsl:stylesheet>

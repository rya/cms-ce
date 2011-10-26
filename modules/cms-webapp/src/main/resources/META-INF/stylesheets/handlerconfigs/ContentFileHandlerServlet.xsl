<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [<!ENTITY nbsp "&#160;">]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:template match="attribute::handler[self::node() = 'com.enonic.vertical.adminweb.handlers.ContentFileHandlerServlet']" mode="icon">
    <xsl:text>filefolder</xsl:text>
  </xsl:template>

  <xsl:template match="attribute::handler[self::node() = 'com.enonic.vertical.adminweb.handlers.ContentFileHandlerServlet']" mode="custombuttons_center">
		
    <xsl:variable name="category" select="/data/category"/>
    <xsl:variable name="categoryadministrate" select="$category and (not($category/accessrights/userright) or ($category/accessrights/userright/@administrate = 'true'))"/>

    <xsl:if test="$categoryadministrate">
      <xsl:call-template name="button">
        <xsl:with-param name="type" select="'link'"/>
        <xsl:with-param name="caption" select="'%cmdImport%'"/>
        <xsl:with-param name="href">
          <xsl:text>adminpage?page=</xsl:text>
          <xsl:value-of select="$page"/>
          <xsl:text>&amp;op=wizard&amp;name=import</xsl:text>
          <xsl:text>&amp;cat=</xsl:text>
          <xsl:value-of select="$cat"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>

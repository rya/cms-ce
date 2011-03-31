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

  <xsl:template name="resourceheader">
      <xsl:if test="$mode = ''">
          <xsl:call-template name="genericheader">
              <xsl:with-param name="links" select="true()"/>
          </xsl:call-template>
      </xsl:if>
      
      <a>
          <xsl:attribute name="href">
              <xsl:text>adminpage?page=</xsl:text>
              <xsl:value-of select="$page"/>
              <xsl:text>&amp;op=browse</xsl:text>
              <xsl:if test="$menukey != ''">
                  <xsl:text>&amp;selectedmenukey=</xsl:text>
                  <xsl:value-of select="$menukey"/>
              </xsl:if>
              <xsl:if test="$mode = 'popup'">
                  <xsl:text>&amp;mode=popup&amp;inserttypekey=</xsl:text>
                  <xsl:value-of select="$inserttypekey"/>
                  <xsl:text>&amp;fieldname=</xsl:text>
                  <xsl:value-of select="$fieldname"/>
              </xsl:if>
          </xsl:attribute>
          <xsl:choose>
              <xsl:when test="$menukey != '' or $mode = 'popup'">
                  <xsl:text>%headResources%</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                  <xsl:text>%headSharedResources%</xsl:text>
              </xsl:otherwise>
          </xsl:choose>
      </a>

      <xsl:if test="$mode = 'popup' and $menuname != ''">
          <xsl:text>&nbsp;/&nbsp;</xsl:text>
          <a>
              <xsl:attribute name="href">
                  <xsl:text>adminpage?page=</xsl:text>
                  <xsl:value-of select="$page"/>
                  <xsl:text>&amp;op=browse</xsl:text>
                  <xsl:text>&amp;selectedmenukey=</xsl:text>
                  <xsl:value-of select="$menukey"/>
                  <xsl:text>&amp;mode=popup&amp;inserttypekey=</xsl:text>
                  <xsl:value-of select="$inserttypekey"/>
                  <xsl:text>&amp;fieldname=</xsl:text>
                  <xsl:value-of select="$fieldname"/>
              </xsl:attribute>
              <xsl:value-of select="$menuname"/>
          </a>
      </xsl:if>
      
      <xsl:if test="$typekey != ''">
          
          <xsl:text>&nbsp;/&nbsp;</xsl:text>
          <a>
              <xsl:attribute name="href">
                  <xsl:value-of select="$pageURL"/>
              </xsl:attribute>
              <xsl:call-template name="resourcetypename">
                  <xsl:with-param name="typekey" select="$typekey"/>
              </xsl:call-template>
          </a>
      </xsl:if>
  </xsl:template>

  <xsl:template name="resourcetypename">
      <xsl:param name="typekey"/>

      <xsl:choose>
          <xsl:when test="$typekey = 1"> 
              <xsl:text>%headFrameXSL%</xsl:text>
          </xsl:when>
          <xsl:when test="$typekey = 2"> 
              <xsl:text>%headPortletXSL%</xsl:text>
          </xsl:when>
          <xsl:when test="$typekey = 3"> 
              <xsl:text>%headFrameworkXSL%</xsl:text>
          </xsl:when>
          <xsl:when test="$typekey = 4"> 
              <xsl:text>%headSiteCSS%</xsl:text>
          </xsl:when>
          <xsl:when test="$typekey = 5"> 
              <xsl:text>%headReportXSL%</xsl:text>
          </xsl:when>
          <xsl:when test="$typekey = 6"> 
              <xsl:text>%headIncludeXSL%</xsl:text>
          </xsl:when>
          <xsl:when test="$typekey = 7"> 
              <xsl:text>%headArchiveCSS%</xsl:text>
          </xsl:when>
          <xsl:when test="$typekey = 8"> 
              <xsl:text>%headScripts%</xsl:text>
          </xsl:when>
          <xsl:when test="$typekey = 9">
              <xsl:text>%headController%</xsl:text>
          </xsl:when>
          <xsl:otherwise>
              <xsl:text>Unknown resource type: </xsl:text>
              <xsl:value-of select="$typekey"/>
          </xsl:otherwise>
      </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>

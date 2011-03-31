<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE xsl:stylesheet [
    <!ENTITY nbsp "&#160;">
    ]>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exslt-common="http://exslt.org/common"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:admin="java:com.enonic.cms.core.xslt.lib.AdminFunctions">

  <xsl:template match="image" mode="display">
    <xsl:param name="contentxpath"/>
    
    <xsl:variable name="contentKey" select="saxon:evaluate(concat($contentxpath, '@key'))"/>

    <xsl:variable name="width">
      <xsl:choose>
        <xsl:when test="@width and @width != ''">
          <xsl:value-of select="@width"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>80</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <div class="content-list-image-container">

      <xsl:variable name="label">
        <xsl:choose>
          <xsl:when test="saxon:evaluate(concat($contentxpath,'/binaries/binary[@label = &quot;small&quot;]'))">
            <xsl:text>small</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>source</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <img src="_image/{$contentKey}/{$label}/source?_filter=scalemax({$width})" alt=""/>

      <xsl:value-of select="saxon:evaluate(concat($contentxpath, @textxpath))"/>
    </div>
  </xsl:template>
</xsl:stylesheet>

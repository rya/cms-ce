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

  <xsl:template name="categoryheader">
    <xsl:param name="rootname" select="''"/>
    <xsl:param name="op" select="'browse'"/>
    <xsl:param name="contentselector" select="'false'"/>
    <xsl:param name="rootelem"/>
    <xsl:param name="nolinks" select="false()"/>
    <xsl:param name="create" select="false()"/>

    <xsl:param name="subop"/>
    <xsl:param name="fieldrow"/>
    <xsl:param name="fieldname"/>
    <xsl:param name="minoccurrence"/>
    <xsl:param name="maxoccurrence"/>

    <xsl:choose>
      <xsl:when test="$rootelem">
        <xsl:for-each select="$rootelem/categorynames/categoryname">
          <xsl:variable name="name">
            <xsl:choose>
              <xsl:when test="position() = 1 and $rootname != ''">
                <xsl:value-of select="$rootname"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:choose>
                  <xsl:when test="string-length(.) &gt; 0">
                    <xsl:value-of select="."/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="@name"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>

          <xsl:text> / </xsl:text>
          <xsl:call-template name="categoryheader1">
            <xsl:with-param name="op" select="$op"/>
            <xsl:with-param name="subop" select="$subop"/>
            <xsl:with-param name="fieldrow" select="$fieldrow"/>
            <xsl:with-param name="fieldname" select="$fieldname"/>
            <xsl:with-param name="contentselector" select="$contentselector"/>
            <xsl:with-param name="category" select="."/>
            <xsl:with-param name="name" select="$name"/>
            <xsl:with-param name="nolinks" select="$nolinks"/>
            <xsl:with-param name="create" select="$create"/>
            <xsl:with-param name="minoccurrence" select="$minoccurrence"/>
            <xsl:with-param name="maxoccurrence" select="$maxoccurrence"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:when>
      <xsl:when test="/contents">
        <xsl:for-each select="/contents/categorynames/categoryname">
          <xsl:text> / </xsl:text>
          <xsl:call-template name="categoryheader1">
            <xsl:with-param name="op" select="$op"/>
            <xsl:with-param name="subop" select="$subop"/>
            <xsl:with-param name="fieldrow" select="$fieldrow"/>
            <xsl:with-param name="fieldname" select="$fieldname"/>
            <xsl:with-param name="contentselector" select="$contentselector"/>
            <xsl:with-param name="category" select="."/>
            <xsl:with-param name="name">
              <xsl:choose>
                <xsl:when test="position() = 1 and $rootname != ''">
                  <xsl:value-of select="$rootname"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="."/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="nolinks" select="$nolinks"/>
            <xsl:with-param name="create" select="$create"/>
            <xsl:with-param name="minoccurrence" select="$minoccurrence"/>
            <xsl:with-param name="maxoccurrence" select="$maxoccurrence"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:when>
      <xsl:when test="/categories">
        <xsl:for-each select="/categories/categorynames/categoryname">
          <xsl:choose>
            <xsl:when test="$create = 'false' and position() != last()">
              <xsl:text> / </xsl:text>
            </xsl:when>
            <xsl:when test="$create = 'true'">
              <xsl:text> / </xsl:text>
            </xsl:when>
          </xsl:choose>
          <xsl:call-template name="categoryheader1">
            <xsl:with-param name="op" select="$op"/>
            <xsl:with-param name="subop" select="$subop"/>
            <xsl:with-param name="fieldrow" select="$fieldrow"/>
            <xsl:with-param name="fieldname" select="$fieldname"/>
            <xsl:with-param name="contentselector" select="$contentselector"/>
            <xsl:with-param name="category" select="."/>
            <xsl:with-param name="name">
              <xsl:choose>
                <xsl:when test="position() = 1 and $rootname != ''">
                  <xsl:value-of select="$rootname"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="."/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="nolinks" select="$nolinks"/>
            <xsl:with-param name="create" select="$create"/>
            <xsl:with-param name="minoccurrence" select="$minoccurrence"/>
            <xsl:with-param name="maxoccurrence" select="$maxoccurrence"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:for-each select="/data/categorynames/categoryname">
          <xsl:text> / </xsl:text>
          <xsl:call-template name="categoryheader1">
            <xsl:with-param name="op" select="$op"/>
            <xsl:with-param name="subop" select="$subop"/>
            <xsl:with-param name="fieldrow" select="$fieldrow"/>
            <xsl:with-param name="fieldname" select="$fieldname"/>
            <xsl:with-param name="contentselector" select="$contentselector"/>
            <xsl:with-param name="category" select="."/>
            <xsl:with-param name="name">
              <xsl:choose>
                <xsl:when test="position() = 1 and $rootname != ''">
                  <xsl:value-of select="$rootname"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="."/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="nolinks" select="$nolinks"/>
            <xsl:with-param name="create" select="$create"/>
            <xsl:with-param name="minoccurrence" select="$minoccurrence"/>
            <xsl:with-param name="maxoccurrence" select="$maxoccurrence"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:if test="$create = 'true'">
      <xsl:text>&nbsp;</xsl:text>
      <span id="titlename"> </span>
    </xsl:if>
  </xsl:template>

  <xsl:template name="categoryheader1">
    <xsl:param name="category"/>
    <xsl:param name="name" select="''"/>
    <xsl:param name="op"/>
    <xsl:param name="contentselector"/>
    <xsl:param name="nolinks" select="false()"/>
    <xsl:param name="create" select="false()"/>

    <xsl:param name="subop"/>
    <xsl:param name="fieldrow"/>
    <xsl:param name="fieldname"/>
    <xsl:param name="minoccurrence"/>
    <xsl:param name="maxoccurrence"/>

    <xsl:variable name="categorykey">
      <xsl:choose>
        <xsl:when test="$category/@key and string-length($category/@key) &gt; 0">
          <xsl:value-of select="$category/@key"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$category/@categorykey"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="position() = last() and $create = 'false'">
        <xsl:text>&nbsp;</xsl:text>
        <span id="titlename">
          <xsl:value-of select="concat('/ ', $name)"/>
        </span>
      </xsl:when>

      <xsl:when test="$nolinks">
        <xsl:value-of select="$name"/>
      </xsl:when>
      <xsl:otherwise>
        <a>
          <xsl:attribute name="href">
            <xsl:text>adminpage?page=</xsl:text>
            <xsl:choose>
              <xsl:when test="not($category/@contenttypekey)">
                <xsl:value-of select="991"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="string(number($category/@contenttypekey) + 999)"/>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:text>&amp;op=</xsl:text><xsl:value-of select="$op"/>
            <xsl:if test="$subop">
              <xsl:text>&amp;subop=</xsl:text><xsl:value-of select="$subop"/>
            </xsl:if>
            <xsl:if test="$fieldname">
              <xsl:text>&amp;fieldname=</xsl:text><xsl:value-of select="$fieldname"/>
            </xsl:if>
            <xsl:if test="$fieldrow">
              <xsl:text>&amp;fieldrow=</xsl:text><xsl:value-of select="$fieldrow"/>
            </xsl:if>
            <xsl:text>&amp;selectedunitkey=</xsl:text><xsl:value-of select="$selectedunitkey"/>
            <xsl:text>&amp;cat=</xsl:text><xsl:value-of select="$categorykey"/>
            <xsl:if test="$contentselector = 'true'">
              <xsl:text>&amp;contentselector=true</xsl:text>
            </xsl:if>
            <xsl:if test="$minoccurrence">
              <xsl:text>&amp;minoccurrence=</xsl:text>
              <xsl:value-of select="$minoccurrence"/>
            </xsl:if>
            <xsl:if test="$maxoccurrence">
              <xsl:text>&amp;maxoccurrence=</xsl:text>
              <xsl:value-of select="$maxoccurrence"/>
            </xsl:if>
          </xsl:attribute>
          <xsl:value-of select="$name"/>
        </a>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
